package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.container.CrowFluteContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrowFluteScreen extends AbstractContainerScreen<CrowFluteContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/crow_flute_gui.png");
   public ItemStack crowFluteStack;
   public Player player;
   boolean xClicked;
   int xClickedTimer;

   public CrowFluteScreen(CrowFluteContainer crowFluteContainer, Inventory inv, Component titleIn) {
      super(crowFluteContainer, inv, titleIn);
      this.crowFluteStack = crowFluteContainer.stack.copy();
      this.titleLabelY = 22;
      this.titleLabelX = 5;
      this.inventoryLabelY = -800;
      this.inventoryLabelX = 13;
      this.player = inv.player;
      this.xClicked = false;
      this.xClickedTimer = 10;
   }

   protected void containerTick() {
      if (this.xClicked) {
         this.xClickedTimer--;
         if (this.xClickedTimer-- < 0) {
            this.xClicked = false;
            this.xClickedTimer = 10;
         }
      }

      super.containerTick();
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

   public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      FluteData data = (FluteData)this.crowFluteStack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      int commandmode = data.commandMode();
      int command = data.commandSelected();
      int helpcommand = data.commandSelected();
      List<Component> components = new ArrayList<>();
      if (this.isHovering(mouseX, mouseY, 23.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_command_gui_0"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_follow_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 43.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_command_gui_1"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_sit_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_sit_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 63.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_command_gui_2"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_wander_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_wander_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 83.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_command_gui_3"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_help_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 107.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_help_command_gui_0"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_gather_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 127.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_help_command_gui_1"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_harvest_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_harvest_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 147.0, 64.0, 18.0, 18.0)) {
         components.add(Component.translatable("entity.hexerei.crow_help_command_gui_2"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(
               Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(
               Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(
               Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(
               Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(
               Component.translatable("tooltip.hexerei.crow_flute_pickpocket_button_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
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

      if (this.isHovering(mouseX, mouseY, 22.0, 87.0, 62.0, 15.0)) {
         components.add(Component.translatable("entity.hexerei.crow_flute_select"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_select_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 104.0, 87.0, 62.0, 15.0)) {
         components.add(Component.translatable("entity.hexerei.crow_flute_perch"));
         if (Screen.hasShiftDown()) {
            components.add(
               Component.translatable(
                     "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                  )
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            components.add(Component.translatable("tooltip.hexerei.crow_flute_perch_button_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 89.0, 90.0, 10.0, 10.0)) {
         if (commandmode == 1) {
            components.add(Component.translatable("tooltip.hexerei.crow_flute_clear_selected_button"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(
                  Component.translatable("tooltip.hexerei.crow_flute_clear_selected_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            } else {
               components.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         } else if (commandmode == 2) {
            components.add(Component.translatable("tooltip.hexerei.crow_flute_clear_perch_button"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(
                  Component.translatable("tooltip.hexerei.crow_flute_clear_perch_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            } else {
               components.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }

         guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
      }
   }

   public Component getTitle() {
      MutableComponent mutablecomponent = Component.translatable("").append(this.crowFluteStack.getHoverName());
      if (this.crowFluteStack.has(DataComponents.CUSTOM_NAME)) {
         mutablecomponent.withStyle(ChatFormatting.ITALIC);
      }

      return mutablecomponent;
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      PoseStack matrixStack = guiGraphics.pose();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      int i = this.leftPos;
      int j = this.topPos;
      this.inventoryLabelY = -800;
      this.inventoryLabelX = 13;
      FluteData data = (FluteData)this.crowFluteStack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      int commandmode = data.commandMode();
      int command = data.commandSelected();
      int helpcommand = data.helpCommandSelected();
      guiGraphics.blit(this.GUI, i, j, 0, 0, 188, 130);
      if (command == 0) {
         guiGraphics.blit(this.GUI, i + 23, j + 64, 238, 52, 18, 18);
      } else if (command == 1) {
         guiGraphics.blit(this.GUI, i + 43, j + 64, 238, 70, 18, 18);
      } else if (command == 2) {
         guiGraphics.blit(this.GUI, i + 63, j + 64, 238, 88, 18, 18);
      } else if (command == 3) {
         guiGraphics.blit(this.GUI, i + 83, j + 64, 238, 106, 18, 18);
      }

      if (command == 3) {
         if (helpcommand == 0) {
            guiGraphics.blit(this.GUI, i + 107, j + 64, 238, 124, 18, 18);
         }

         if (helpcommand == 1) {
            guiGraphics.blit(this.GUI, i + 127, j + 64, 238, 142, 18, 18);
         }

         if (helpcommand == 2) {
            guiGraphics.blit(this.GUI, i + 147, j + 64, 238, 160, 18, 18);
         }
      } else {
         if (helpcommand == 0) {
            guiGraphics.blit(this.GUI, i + 107, j + 64, 220, 124, 18, 18);
         } else {
            guiGraphics.blit(this.GUI, i + 107, j + 64, 202, 124, 18, 18);
         }

         if (helpcommand == 1) {
            guiGraphics.blit(this.GUI, i + 127, j + 64, 220, 142, 18, 18);
         } else {
            guiGraphics.blit(this.GUI, i + 127, j + 64, 202, 142, 18, 18);
         }

         if (helpcommand == 2) {
            guiGraphics.blit(this.GUI, i + 147, j + 64, 220, 160, 18, 18);
         } else {
            guiGraphics.blit(this.GUI, i + 147, j + 64, 202, 160, 18, 18);
         }
      }

      if (commandmode == 1) {
         guiGraphics.blit(this.GUI, i + 22, j + 87, 0, 131, 62, 15);
         if (this.xClicked) {
            guiGraphics.blit(this.GUI, i + 89, j + 90, 74, 131, 10, 10);
         } else {
            guiGraphics.blit(this.GUI, i + 89, j + 90, 63, 131, 10, 10);
         }
      }

      if (commandmode == 2) {
         guiGraphics.blit(this.GUI, i + 104, j + 87, 0, 147, 62, 15);
         if (this.xClicked) {
            guiGraphics.blit(this.GUI, i + 89, j + 90, 74, 131, 10, 10);
         } else {
            guiGraphics.blit(this.GUI, i + 89, j + 90, 63, 131, 10, 10);
         }
      }

      guiGraphics.blit(this.GUI, i + 81, j - 14, 230, 0, 26, 26);
      CompoundTag tag = ((CustomData)this.crowFluteStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tag.contains("crowList")) {
         for (int k = 0; k < ((CrowFluteContainer)this.menu).crowList.size(); k++) {
            if (((CrowFluteContainer)this.menu).crowList.get(k) != null) {
               int offset = (((CrowFluteContainer)this.menu).crowList.size() % 2 == 1 ? 0 : -10) + (k % 2 == 1 ? 1 : -1) * ((k + 1) / 2) * 20;
               guiGraphics.blit(this.GUI, i + 86 + offset, j + 125, 85, 131, 15, 3);
               if (k != 0) {
                  guiGraphics.blit(this.GUI, i + 83 + offset + (k % 2 == 0 ? 20 : 0), j + 118, 101, 131, 2, 9);
               }
            }
         }
      }

      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      RenderSystem.disableDepthTest();
      guiGraphics.renderItem(this.crowFluteStack, this.leftPos + 86, this.topPos - 25 + 16);
      if (tag.contains("crowList")) {
         for (int kx = 0; kx < ((CrowFluteContainer)this.menu).crowList.size(); kx++) {
            if (((CrowFluteContainer)this.menu).crowList.get(kx) != null) {
               int offset = (((CrowFluteContainer)this.menu).crowList.size() % 2 == 1 ? 0 : -10) + (kx % 2 == 1 ? 1 : -1) * ((kx + 1) / 2) * 20;
               float f9 = ((LivingEntity)((CrowFluteContainer)this.menu).crowList.get(kx)).getScale();
               Vector3f vector3f = new Vector3f(0.0F, ((CrowFluteContainer)this.menu).crowList.get(kx).getBbHeight() / 2.0F + 0.0625F * f9, 0.0F);
               InventoryScreen.renderEntityInInventory(
                  guiGraphics,
                  this.leftPos + 94 + offset,
                  this.topPos + 126,
                  20.0F,
                  vector3f,
                  new Quaternionf().rotationXYZ(0.43633232F, 0.0F, 3.1415927F),
                  (Quaternionf)null,
                  (LivingEntity)((CrowFluteContainer)this.menu).crowList.get(kx)
               );
            }
         }
      }

      MutableComponent command2;
      if (command == 0) {
         command2 = Component.translatable("entity.hexerei.crow_command_gui_0");
      } else if (command == 1) {
         command2 = Component.translatable("entity.hexerei.crow_command_gui_1");
      } else if (command == 2) {
         command2 = Component.translatable("entity.hexerei.crow_command_gui_2");
      } else {
         command2 = Component.translatable("entity.hexerei.crow_command_gui_3");
      }

      MutableComponent helpCommand;
      if (helpcommand == 0) {
         helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_0");
      } else if (helpcommand == 1) {
         helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_1");
      } else {
         helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_2");
      }

      if (commandmode == 1) {
         MutableComponent crowSelect = Component.translatable("entity.hexerei.crow_flute_select");
         this.drawFont(
            guiGraphics, crowSelect, (float)(this.leftPos + 62) - this.font.width(crowSelect.getVisualOrderText()) / 2, this.topPos + 91, -3355444, true
         );
      } else {
         MutableComponent crowSelect = Component.translatable("entity.hexerei.crow_flute_select");
         this.drawFont(
            guiGraphics, crowSelect, (float)(this.leftPos + 62) - this.font.width(crowSelect.getVisualOrderText()) / 2, this.topPos + 91, -5592406, true
         );
      }

      this.drawFont(
         guiGraphics, command2, (float)(this.leftPos + 56) - this.font.width(command2.getVisualOrderText()) / 2, this.topPos + 63 - 14, -10461088, false
      );
      this.drawFont(
         guiGraphics, helpCommand, (float)(this.leftPos + 131) - this.font.width(helpCommand.getVisualOrderText()) / 2, this.topPos + 63 - 14, -10461088, false
      );
      this.drawFont(
         guiGraphics,
         Component.translatable("entity.hexerei.crow_flute_perch"),
         (float)(this.leftPos + 128) - this.font.width(Component.translatable("entity.hexerei.crow_flute_perch").getVisualOrderText()) / 2,
         this.topPos + 91,
         -5592406,
         true
      );
   }

   private void drawFont(GuiGraphics guiGraphics, MutableComponent component, float x, float y, int color, boolean shadow) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(x, y, 1.0F);
      guiGraphics.drawString(this.minecraft.font, component, 0, 0, color, shadow);
      guiGraphics.pose().popPose();
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      FluteData data = (FluteData)this.crowFluteStack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      int commandmode = data.commandMode();
      int command = data.commandSelected();
      int helpcommand = data.helpCommandSelected();
      if (x > this.leftPos + 23.0F && x < this.leftPos + 23.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommand(0, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 43.0F && x < this.leftPos + 43.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommand(1, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 63.0F && x < this.leftPos + 63.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommand(2, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 83.0F && x < this.leftPos + 83.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommand(3, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 107.0F && x < this.leftPos + 107.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setHelpCommand(0, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 127.0F && x < this.leftPos + 127.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setHelpCommand(1, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 147.0F && x < this.leftPos + 147.0F + 18.0F && y > this.topPos + 64 && y < this.topPos + 64 + 18) {
         this.crowFluteStack = ((CrowFluteContainer)this.menu).setHelpCommand(2, this.crowFluteStack);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (x > this.leftPos + 22 && x < this.leftPos + 22 + 62 && y > this.topPos + 87 && y < this.topPos + 87 + 15) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (commandmode != 0 && commandmode != 2) {
            this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommandMode(0, this.crowFluteStack);
         } else {
            this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommandMode(1, this.crowFluteStack);
         }

         if (((FluteData)this.crowFluteStack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 1) {
            Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message"), true);
         }
      } else if (x > this.leftPos + 104 && x < this.leftPos + 104 + 62 && y > this.topPos + 87 && y < this.topPos + 87 + 15) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (commandmode != 0 && commandmode != 1) {
            this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommandMode(0, this.crowFluteStack);
         } else {
            this.crowFluteStack = ((CrowFluteContainer)this.menu).setCommandMode(2, this.crowFluteStack);
         }

         if (((FluteData)this.crowFluteStack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
            Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message"), true);
         }
      } else if (x > this.leftPos + 89 && x < this.leftPos + 89 + 10 && y > this.topPos + 90 && y < this.topPos + 90 + 10) {
         if (commandmode == 1) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.crowFluteStack = ((CrowFluteContainer)this.menu).clearCrowList(this.crowFluteStack);
            Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_clear_select_message"), true);
            this.xClicked = true;
            this.xClickedTimer = 10;
         }

         if (commandmode == 2) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ((CrowFluteContainer)this.menu).clearCrowPerch();
            Hexerei.proxy.getPlayer().displayClientMessage(Component.translatable("entity.hexerei.crow_flute_clear_perch_message"), true);
            this.xClicked = true;
            this.xClickedTimer = 10;
         }
      }

      return mouseClicked;
   }
}
