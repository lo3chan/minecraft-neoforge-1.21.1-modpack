package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Impact;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.network.payload.ChooseOriginC2SPayload;
import com.iafenvoy.origins.registry.OriginsItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ChooseOriginScreen extends OriginDisplayScreen {
   private static final ResourceLocation ORIGINS_CHOICES = ResourceLocation.fromNamespaceAndPath("origins", "textures/gui/origin_choices.png");
   private static final int CHOICES_WIDTH = 219;
   private static final int CHOICES_HEIGHT = 182;
   private static final int ORIGIN_ICON_SIZE = 26;
   private static final int COUNT_PER_PAGE = 35;
   private final List<Holder<Layer>> layers;
   private final List<Holder<Origin>> origins;
   private final int currentLayerIndex;
   private Holder<Origin> randomOrigin;
   private int currentOriginIndex = 0;
   private int maxSelection = 0;
   private int calculatedTop;
   private int calculatedLeft;
   private int currentPage = 0;
   private int pages;

   public ChooseOriginScreen(List<Holder<Layer>> layers, int currentLayerIndex, boolean showDirtBackground) {
      super(Component.empty(), showDirtBackground);
      this.layers = layers;
      this.currentLayerIndex = currentLayerIndex;
      this.origins = new ArrayList<>(layers.size());
      this.initRandomOrigin();
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         Layer currentLayer = (Layer)this.getCurrentLayer().value();
         currentLayer.collectOrigins(player).forEach(holder -> {
            if (!((Origin)holder.value()).unchoosable()) {
               ItemStack iconStack = ((Origin)holder.value()).icon().orElse(ItemStack.EMPTY);
               if (iconStack.is(Items.PLAYER_HEAD) && !iconStack.has(DataComponents.PROFILE)) {
                  iconStack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
               }

               this.origins.add((Holder<Origin>)holder);
            }
         });
         this.origins
            .sort(
               Comparator.<Holder<Origin>>comparingInt(o -> ((Origin)o.value()).impact().getImpactValue()).thenComparingInt(x -> ((Origin)x.value()).order())
            );
         this.maxSelection = currentLayer.getOriginOptionCount(player);
         if (this.maxSelection == 0) {
            this.openNextLayerScreen();
         }

         Holder<Origin> newOrigin = this.getCurrentOrigin();
         this.showOrigin(newOrigin, this.getCurrentLayer(), Objects.equals(newOrigin.getKey(), this.randomOrigin.getKey()));
      }
   }

   private void openNextLayerScreen() {
      Minecraft.getInstance().setScreen(new WaitForNextLayerScreen(this.layers, this.currentLayerIndex, this.showDirtBackground));
   }

   private void initRandomOrigin() {
      this.randomOrigin = Holder.direct(
         Origin.special(ResourceLocation.fromNamespaceAndPath("origins", "random"), OriginsItems.ORB_OF_ORIGIN.toStack(), Impact.NONE, -1)
      );
      MutableComponent randomOriginText = Component.empty();
      Player player = Minecraft.getInstance().player;

      assert player != null;

      ((Layer)this.layers.get(this.currentLayerIndex).value()).collectRandomizableOrigins(player).sorted((ia, ib) -> {
         Origin a = (Origin)ia.value();
         Origin b = (Origin)ib.value();
         int impactDelta = Integer.compare(a.impact().getImpactValue(), b.impact().getImpactValue());
         return impactDelta != 0 ? impactDelta : Integer.compare(a.order(), b.order());
      }).forEach(origin -> {
         randomOriginText.append(Origin.getName((Holder<Origin>)origin));
         randomOriginText.append(Component.literal("\n"));
      });
      this.setRandomOriginText(randomOriginText);
   }

   @NotNull
   public Component getTitle() {
      return ((Layer)this.getCurrentLayer().value())
         .getChooseOriginTitle(Component.translatable("origins.gui.choose_origin.title", new Object[]{Layer.getName(this.getCurrentLayer())}));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   protected void init() {
      super.init();
      this.calculatedTop = (this.height - 182) / 2;
      this.calculatedLeft = (this.width - 405) / 2;
      this.guiTop = (this.height - 182) / 2;
      this.guiLeft = this.calculatedLeft + 219 + 10;
      this.pages = (int)Math.ceil(1.0 * this.maxSelection / 35.0);
      int x = 0;
      int y = 0;

      for (int i = 0; i < Math.min(this.maxSelection, 35); i++) {
         if (x > 6) {
            x = 0;
            y++;
         }

         int actualX = 12 + x * 28 + this.calculatedLeft;
         int actualY = 10 + y * 30 + this.calculatedTop;
         int finalI = i;
         this.addWidget(Button.builder(Component.empty(), b -> {
            int index = finalI + this.currentPage * 35;
            if (index <= this.maxSelection - 1) {
               this.currentOriginIndex = index;
               Holder<Origin> newOrigin = this.getCurrentOrigin();
               this.showOrigin(newOrigin, this.layers.get(this.currentLayerIndex), newOrigin == this.randomOrigin);
            }
         }).pos(actualX, actualY).size(26, 26).build());
         x++;
      }

      if (this.maxSelection > 35) {
         this.addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            this.currentPage--;
            if (this.currentPage < 0) {
               this.currentPage = this.pages - 1;
            }
         }).pos(this.calculatedLeft, this.guiTop + 182 + 5).size(20, 20).build());
         this.addRenderableWidget(
            Button.builder(Component.literal(">"), b -> this.currentPage = (this.currentPage + 1) % this.pages)
               .pos(this.calculatedLeft + 219 - 20, this.guiTop + 182 + 5)
               .size(20, 20)
               .build()
         );
      }

      if (this.maxSelection > 0) {
         this.addRenderableWidget(
            Button.builder(
                  Component.translatable("origins.gui.select"),
                  button -> {
                     PacketDistributor.sendToServer(
                        new ChooseOriginC2SPayload(
                           this.getCurrentLayer(), this.currentOriginIndex == this.origins.size() ? Optional.empty() : Optional.of(super.getCurrentOrigin())
                        ),
                        new CustomPacketPayload[0]
                     );
                     this.openNextLayerScreen();
                  }
               )
               .bounds(this.guiLeft + 88 - 50, this.guiTop + 182 + 5, 100, 20)
               .build()
         );
      }
   }

   @Override
   public Holder<Layer> getCurrentLayer() {
      return this.layers.get(this.currentLayerIndex);
   }

   @Override
   public Holder<Origin> getCurrentOrigin() {
      return this.currentOriginIndex == this.origins.size() ? this.randomOrigin : this.origins.get(this.currentOriginIndex);
   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      if (this.maxSelection == 0) {
         this.openNextLayerScreen();
      } else {
         super.render(graphics, mouseX, mouseY, delta);
      }
   }

   @Override
   protected void renderOriginWindow(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.renderOriginChoicesBox(graphics, mouseX, mouseY);
      super.renderOriginWindow(graphics, mouseX, mouseY, delta);
   }

   public void renderOriginChoicesBox(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.blit(ORIGINS_CHOICES, this.calculatedLeft, this.calculatedTop, 0, 0, 219, 182);
      int x = 0;
      int y = 0;

      for (int i = this.currentPage * 35; i < Math.min((this.currentPage + 1) * 35, this.maxSelection); i++) {
         if (x > 6) {
            x = 0;
            y++;
         }

         int actualX = 12 + x * 28 + this.calculatedLeft;
         int actualY = 10 + y * 30 + this.calculatedTop;
         if (i >= this.origins.size()) {
            boolean selected = this.getCurrentOrigin().equals(this.randomOrigin);
            this.renderRandomOrigin(graphics, mouseX, mouseY, actualX, actualY, selected);
         } else {
            Holder<Origin> origin = this.origins.get(i);
            boolean selected = Objects.equals(origin.getKey(), this.getCurrentOrigin().getKey());
            this.renderOriginWidget(graphics, mouseX, mouseY, actualX, actualY, selected, origin);
            graphics.renderItem(((Origin)origin.value()).icon().orElse(ItemStack.EMPTY), actualX + 5, actualY + 5);
         }

         x++;
      }

      graphics.drawCenteredString(
         this.font, Component.literal(this.currentPage + 1 + "/" + this.pages).getVisualOrderText(), this.calculatedLeft + 109, this.guiTop + 182 + 9, 16777215
      );
   }

   private boolean renderOriginBase(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, boolean selected, Holder<Origin> origin) {
      boolean mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + 26 && mouseY < y + 26;
      boolean guiSelected = this.getFocused() instanceof Button buttonWidget && buttonWidget.getX() == x && buttonWidget.getY() == y || mouseHovering;
      graphics.blit(ORIGINS_CHOICES, x, y, 230, (selected ? 26 : 0) + (guiSelected ? 52 : 0), 26, 26);
      if (mouseHovering) {
         Component text = Layer.getName(this.getCurrentLayer()).copy().append(": ").append(Origin.getName(origin));
         graphics.renderTooltip(this.font, text, mouseX, mouseY);
      }

      return guiSelected;
   }

   public void renderOriginWidget(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, boolean selected, Holder<Origin> origin) {
      boolean guiSelected = this.renderOriginBase(graphics, mouseX, mouseY, x, y, selected, origin);
      switch (((Origin)origin.value()).impact()) {
         case NONE:
            graphics.blit(ORIGINS_CHOICES, x, y, 224, guiSelected ? 112 : 104, 8, 8);
            break;
         case LOW:
            graphics.blit(ORIGINS_CHOICES, x, y, 232, guiSelected ? 112 : 104, 8, 8);
            break;
         case MEDIUM:
            graphics.blit(ORIGINS_CHOICES, x, y, 240, guiSelected ? 112 : 104, 8, 8);
            break;
         case HIGH:
            graphics.blit(ORIGINS_CHOICES, x, y, 248, guiSelected ? 112 : 104, 8, 8);
      }
   }

   public void renderRandomOrigin(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, boolean selected) {
      boolean guiSelected = this.renderOriginBase(graphics, mouseX, mouseY, x, y, selected, this.randomOrigin);
      graphics.blit(ORIGINS_CHOICES, x + 6, y + 5, 243, 120, 13, 16);
      int impact = (int)(Minecraft.getInstance().clientTickCount / 15L) % 4;
      graphics.blit(ORIGINS_CHOICES, x, y, 224 + impact * 8, guiSelected ? 112 : 104, 8, 8);
   }
}
