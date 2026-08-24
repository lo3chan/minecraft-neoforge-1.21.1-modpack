package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.container.HerbJarContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class HerbJarScreen extends AbstractContainerScreen<HerbJarContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/herb_jar_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");

   public HerbJarScreen(HerbJarContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.inventoryLabelY = 107;
      this.inventoryLabelX = 8;
      this.titleLabelY = -27;
      this.titleLabelX = 52;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
      this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      int i = this.leftPos;
      int j = this.topPos;
      guiGraphics.blit(this.GUI, i, j - 3 - 28, 0, 0, 214, 157);
      if (((HerbJarContainer)this.menu).getToggled() == 1) {
         guiGraphics.blit(this.GUI, i + 82, j + 105 - 28, 238, 26, 18, 18);
      }

      guiGraphics.blit(this.GUI, i + 78, j - 30 - 28, 230, 0, 26, 26);
      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      RenderSystem.disableDepthTest();
      guiGraphics.renderItem(((HerbJarContainer)this.menu).stack, this.leftPos + 83, this.topPos - 25 - 28);
      RenderSystem.enableDepthTest();
      guiGraphics.blit(this.INVENTORY, i + 3, j + 129 - 28, 0, 0, 176, 100);
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      if (x > this.leftPos + 82 && x <= this.leftPos + 82 + 18 && y >= this.topPos + 105 - 28 && y < this.topPos + 105 + 18 - 28) {
         ((HerbJarContainer)this.menu).setToggled(1 - ((HerbJarContainer)this.menu).getToggled());
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      return mouseClicked;
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
   }

   public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      List<Component> components = new ArrayList<>();
      if (this.isHovering(mouseX, mouseY, 82.0, 77.0, 18.0, 18.0)) {
         components.add(Component.translatable("tooltip.hexerei.gather_to_here_button"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.gather_to_here_button_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         } else {
            components.add(
               Component.translatable(
                     "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         }

         guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
      }
   }
}
