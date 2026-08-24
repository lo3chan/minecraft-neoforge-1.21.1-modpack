package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.container.CrowContainer;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CrowCanAttackToServer;
import net.joefoxe.hexerei.util.message.CrowInteractionRangeToServer;
import net.joefoxe.hexerei.util.message.CrowWhitelistSyncToServer;
import net.joefoxe.hexerei.util.message.PlayerWhitelistingForCrowSyncToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class CrowScreen extends AbstractContainerScreen<CrowContainer> {
   private static final int FRONT_OVERLAY_BLIT_LAYER = 3;
   private static final int FRONT_BLIT_LAYER = 2;
   private static final int BACK_OVERLAY_BLIT_LAYER = 1;
   private static final int BACK_BLIT_LAYER = 0;
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/crow_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   public final CrowEntity crowEntity;
   public float whitelistOffset;
   public float leftPanelOffset;
   private int whitelistPage;
   private int rangeSlider;
   private boolean rangeSliderClicked;
   private double rangeSliderClickedPos;

   public CrowScreen(CrowContainer crowContainer, Inventory inv, Component titleIn) {
      super(crowContainer, inv, titleIn);
      this.crowEntity = crowContainer.crowEntity;
      this.titleLabelY = -27;
      this.titleLabelX = 4;
      this.inventoryLabelY = 106;
      this.inventoryLabelX = 9;
      this.whitelistOffset = 0.0F;
      this.leftPanelOffset = 0.0F;
      this.whitelistPage = 0;
      this.rangeSlider = this.crowEntity.interactionRange;
      this.rangeSliderClickedPos = 0.0;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      int i = this.leftPos;
      int j = this.topPos - 28;
      if (isMouseOver(mouseX, mouseY, i + 184, j + 18, 10 + (int)this.whitelistOffset, 100)) {
         this.whitelistOffset = HexereiUtil.moveTo(this.whitelistOffset, 28.0F, 2.0F * ((32.0F - this.whitelistOffset) / 31.0F));
      } else {
         this.whitelistOffset = HexereiUtil.moveTo(this.whitelistOffset, 0.0F, Math.abs(2.0F * ((-1.0F - this.whitelistOffset) / 31.0F)));
      }

      if (isMouseOver(mouseX, mouseY, i - 5 - (int)this.leftPanelOffset, j + 18, 10 + (int)this.leftPanelOffset, 100)) {
         this.leftPanelOffset = HexereiUtil.moveTo(this.leftPanelOffset, 28.0F, 2.0F * ((32.0F - this.leftPanelOffset) / 31.0F));
      } else {
         this.leftPanelOffset = HexereiUtil.moveTo(this.leftPanelOffset, 0.0F, Math.abs(2.0F * ((-1.0F - this.leftPanelOffset) / 31.0F)));
      }

      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
      this.renderButtonTooltip(guiGraphics, mouseX, mouseY);
   }

   public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
      return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY;
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos - 28 + y && mouseY < this.topPos - 28 + y + height;
   }

   protected void containerTick() {
      super.containerTick();
   }

   public Component getTitle() {
      return super.getTitle();
   }

   public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      List<Component> components = new ArrayList<>();
      if (this.whitelistOffset > 21.0F) {
         if (this.isHovering(mouseX, mouseY, 162 + (int)this.whitelistOffset, 27.0, 18.0, 18.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_whitelist_button"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(Component.translatable("tooltip.hexerei.crow_whitelist_button_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               components.add(Component.translatable("tooltip.hexerei.crow_whitelist_button_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

         if (this.isHovering(mouseX, mouseY, 175 + (int)this.whitelistOffset, 48.0, 7.0, 7.0)) {
            if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 1) {
               components.add(Component.translatable("tooltip.hexerei.crow_whitelist_remove"));
               guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }
         } else if (this.isHovering(mouseX, mouseY, 175 + (int)this.whitelistOffset, 66.0, 7.0, 7.0)) {
            if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 2) {
               components.add(Component.translatable("tooltip.hexerei.crow_whitelist_remove"));
               guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }
         } else if (this.isHovering(mouseX, mouseY, 175 + (int)this.whitelistOffset, 84.0, 7.0, 7.0)) {
            if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 3) {
               components.add(Component.translatable("tooltip.hexerei.crow_whitelist_remove"));
               guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }
         } else if (this.isHovering(mouseX, mouseY, 175 + (int)this.whitelistOffset - 12, 52.0, 16.0, 16.0)) {
            if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 1) {
               components.add(this.crowEntity.harvestWhitelist.get(this.whitelistPage * 3).getName());
               components.add(this.crowEntity.harvestWhitelist.get(this.whitelistPage * 3).getName());
               guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }
         } else if (this.isHovering(mouseX, mouseY, 175 + (int)this.whitelistOffset - 12, 70.0, 16.0, 16.0)) {
            if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 2) {
               components.add(this.crowEntity.harvestWhitelist.get(this.whitelistPage * 3 + 1).getName());
               guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
            }
         } else if (this.isHovering(mouseX, mouseY, 175 + (int)this.whitelistOffset - 12, 88.0, 16.0, 16.0)
            && this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 3) {
            components.add(this.crowEntity.harvestWhitelist.get(this.whitelistPage * 3 + 2).getName());
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         }

         if (this.isHovering(mouseX, mouseY, 177 + (int)this.whitelistOffset, 107.0, 7.0, 10.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_whitelist_next"));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         }

         if (this.isHovering(mouseX, mouseY, 159 + (int)this.whitelistOffset, 107.0, 7.0, 10.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_whitelist_back"));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         }
      }

      if (this.leftPanelOffset > 21.0F) {
         if (this.isHovering(mouseX, mouseY, 5 - (int)this.leftPanelOffset, 107.0, 7.0, 10.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_range_decrease"));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         } else if (this.isHovering(mouseX, mouseY, 23 - (int)this.leftPanelOffset, 107.0, 7.0, 10.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_range_increase"));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         } else if (this.isHovering(mouseX, mouseY, 6 - (int)this.leftPanelOffset, 94.0, 22.0, 15.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_range_interaction"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(Component.translatable("tooltip.hexerei.crow_range_interaction_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               components.add(Component.translatable("tooltip.hexerei.crow_range_interaction_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               components.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
         } else if (this.isHovering(mouseX, mouseY, 4 - (int)this.leftPanelOffset, 30.0, 18.0, 18.0)) {
            components.add(Component.translatable("tooltip.hexerei.crow_attack_toggle"));
            if (Screen.hasShiftDown()) {
               components.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(
                  Component.translatable("tooltip.hexerei.crow_attack_toggled", new Object[]{this.crowEntity.canAttack ? "On" : "Off"})
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               components.add(Component.translatable("tooltip.hexerei.crow_attack_toggle_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               components.add(Component.translatable("tooltip.hexerei.crow_attack_toggle_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

      if (this.isHovering(mouseX, mouseY, 23.0, 92.0, 18.0, 18.0)) {
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

      if (this.isHovering(mouseX, mouseY, 43.0, 92.0, 18.0, 18.0)) {
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

      if (this.isHovering(mouseX, mouseY, 63.0, 92.0, 18.0, 18.0)) {
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

      if (this.isHovering(mouseX, mouseY, 83.0, 92.0, 18.0, 18.0)) {
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

      if (this.isHovering(mouseX, mouseY, 107.0, 92.0, 18.0, 18.0)) {
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

      if (this.isHovering(mouseX, mouseY, 127.0, 92.0, 18.0, 18.0)) {
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

      if (this.isHovering(mouseX, mouseY, 147.0, 92.0, 18.0, 18.0)) {
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
   }

   private void drawFont(GuiGraphics guiGraphics, MutableComponent component, float x, float y, int z, int color, boolean shadow) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(x, y, z);
      guiGraphics.drawString(this.minecraft.font, component, 0, 0, color, shadow);
      guiGraphics.pose().popPose();
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      int i = this.leftPos;
      int j = this.topPos - 28;
      this.inventoryLabelY = 106;
      this.inventoryLabelX = 9;
      guiGraphics.blit(this.GUI, i + 184 - 28 + (int)this.whitelistOffset, j + 19, 0, 0.0F, 156.0F, 37, 100, 256, 256);
      guiGraphics.blit(this.GUI, i - 5 - (int)this.leftPanelOffset, j + 19, 0, 74.0F, 156.0F, 37, 100, 256, 256);
      if (CrowWhitelistEvent.whiteListingCrow != null && CrowWhitelistEvent.whiteListingCrow == this.crowEntity) {
         guiGraphics.blit(this.GUI, i + 184 - 28 + 6 + (int)this.whitelistOffset, j + 19 + 8, 1, 238.0F, 178.0F, 18, 18, 256, 256);
      }

      guiGraphics.blit(this.GUI, i + 184 - 28 + (int)this.whitelistOffset, j + 19 + 100 - 12, 1, 37.0F, 244.0F, 37, 12, 256, 256);
      guiGraphics.blit(this.GUI, i + 2 - (int)this.leftPanelOffset, j + 19 + 100 - 12, 1, 37.0F, 244.0F, 37, 12, 256, 256);
      if (!this.crowEntity.canAttack) {
         guiGraphics.blit(this.GUI, i + 8 - (int)this.leftPanelOffset, j + 30, 1, 238.0F, 196.0F, 18, 18, 256, 256);
      }

      if (this.rangeSliderClicked) {
         guiGraphics.blit(
            this.GUI,
            i - 5 + 14 - (int)this.leftPanelOffset,
            j + 19 + 64 - Mth.clamp(this.crowEntity.interactionRange + (int)(this.rangeSliderClickedPos - y), 0, 24),
            1,
            40.0F,
            232.0F,
            16,
            5,
            256,
            256
         );
      } else {
         guiGraphics.blit(this.GUI, i - 5 + 14 - (int)this.leftPanelOffset, j + 19 + 64 - this.crowEntity.interactionRange, 1, 40.0F, 238.0F, 16, 5, 256, 256);
      }

      MutableComponent component = Component.literal(String.valueOf(this.crowEntity.interactionRange));
      if (this.rangeSliderClicked) {
         component = Component.literal(String.valueOf(Mth.clamp(this.crowEntity.interactionRange + (int)(this.rangeSliderClickedPos - y), 0, 24)));
      }

      if (this.minecraft != null) {
         this.drawFont(
            guiGraphics,
            component,
            i - 5 + 22.5F - (int)this.leftPanelOffset - this.font.width(component.getVisualOrderText()) / 2,
            j + 102 - 9.0F / 2.0F,
            1,
            -13619152,
            false
         );
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 1) {
         guiGraphics.blit(this.GUI, i + 184 - 28 + 19 + (int)this.whitelistOffset, j + 19 + 29, 1, 231.0F, 117.0F, 7, 7, 256, 256);
      }

      if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 2) {
         guiGraphics.blit(this.GUI, i + 184 - 28 + 19 + (int)this.whitelistOffset, j + 19 + 29 + 18, 1, 231.0F, 117.0F, 7, 7, 256, 256);
      }

      if (this.crowEntity.harvestWhitelist.size() >= this.whitelistPage * 3 + 3) {
         guiGraphics.blit(this.GUI, i + 184 - 28 + 19 + (int)this.whitelistOffset, j + 19 + 29 + 18 + 18, 1, 231.0F, 117.0F, 7, 7, 256, 256);
      }

      if (((CrowContainer)this.menu).crowEntity.harvestWhitelist.size() > 3 + 3 * this.whitelistPage) {
         guiGraphics.blit(this.GUI, i + 184 + 21 - 28 + (int)this.whitelistOffset, j + 19 + 88, 1, 217.0F, 107.0F, 7, 10, 256, 256);
      }

      if (this.whitelistPage > 0) {
         guiGraphics.blit(this.GUI, i + 184 + 3 - 28 + (int)this.whitelistOffset, j + 19 + 88, 1, 210.0F, 107.0F, 7, 10, 256, 256);
      }

      if (((CrowContainer)this.menu).crowEntity.interactionRange < 24) {
         guiGraphics.blit(this.GUI, i + 5 - (int)this.leftPanelOffset + 18, j + 19 + 100 - 12, 1, 217.0F, 107.0F, 7, 10, 256, 256);
      }

      if (((CrowContainer)this.menu).crowEntity.interactionRange > 0) {
         guiGraphics.blit(this.GUI, i + 5 - (int)this.leftPanelOffset, j + 19 + 100 - 12, 1, 210.0F, 107.0F, 7, 10, 256, 256);
      }

      guiGraphics.blit(this.GUI, i, j, 2, 0.0F, 0.0F, 188, 153, 256, 256);
      if (((CrowContainer)this.menu).getCommand() == 0) {
         guiGraphics.blit(this.GUI, i + 23, j + 92, 2, 238.0F, 52.0F, 18, 18, 256, 256);
      } else if (((CrowContainer)this.menu).getCommand() == 1) {
         guiGraphics.blit(this.GUI, i + 43, j + 92, 2, 238.0F, 70.0F, 18, 18, 256, 256);
      } else if (((CrowContainer)this.menu).getCommand() == 2) {
         guiGraphics.blit(this.GUI, i + 63, j + 92, 2, 238.0F, 88.0F, 18, 18, 256, 256);
      } else if (((CrowContainer)this.menu).getCommand() == 3) {
         guiGraphics.blit(this.GUI, i + 83, j + 92, 2, 238.0F, 106.0F, 18, 18, 256, 256);
      }

      if (((CrowContainer)this.menu).getCommand() == 3) {
         if (((CrowContainer)this.menu).getHelpCommand() == 0) {
            guiGraphics.blit(this.GUI, i + 107, j + 92, 2, 238.0F, 124.0F, 18, 18, 256, 256);
         }

         if (((CrowContainer)this.menu).getHelpCommand() == 1) {
            guiGraphics.blit(this.GUI, i + 127, j + 92, 2, 238.0F, 142.0F, 18, 18, 256, 256);
         }

         if (((CrowContainer)this.menu).getHelpCommand() == 2) {
            guiGraphics.blit(this.GUI, i + 147, j + 92, 2, 238.0F, 160.0F, 18, 18, 256, 256);
         }
      } else {
         if (((CrowContainer)this.menu).getHelpCommand() == 0) {
            guiGraphics.blit(this.GUI, i + 107, j + 92, 2, 220.0F, 124.0F, 18, 18, 256, 256);
         } else {
            guiGraphics.blit(this.GUI, i + 107, j + 92, 2, 202.0F, 124.0F, 18, 18, 256, 256);
         }

         if (((CrowContainer)this.menu).getHelpCommand() == 1) {
            guiGraphics.blit(this.GUI, i + 127, j + 92, 2, 220.0F, 142.0F, 18, 18, 256, 256);
         } else {
            guiGraphics.blit(this.GUI, i + 127, j + 92, 2, 202.0F, 142.0F, 18, 18, 256, 256);
         }

         if (((CrowContainer)this.menu).getHelpCommand() == 2) {
            guiGraphics.blit(this.GUI, i + 147, j + 92, 2, 220.0F, 160.0F, 18, 18, 256, 256);
         } else {
            guiGraphics.blit(this.GUI, i + 147, j + 92, 2, 202.0F, 160.0F, 18, 18, 256, 256);
         }
      }

      if (!this.crowEntity.itemHandler.getStackInSlot(0).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 86, j + 50, 2, 235.0F, 31.0F, 16, 16, 256, 256);
      }

      if (!this.crowEntity.itemHandler.getStackInSlot(1).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 37, j + 50, 2, 235.0F, 31.0F, 16, 16, 256, 256);
      }

      if (!this.crowEntity.itemHandler.getStackInSlot(2).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 134, j + 50, 2, 235.0F, 31.0F, 16, 16, 256, 256);
      }

      guiGraphics.blit(this.GUI, i + 81, j - 30, 2, 230.0F, 0.0F, 26, 26, 256, 256);
      guiGraphics.blit(this.INVENTORY, i + 6, j + 129, 2, 0.0F, 0.0F, 176, 100, 256, 256);
      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.setShaderTexture(0, this.GUI);
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      InventoryScreen.renderEntityInInventoryFollowsMouse(
         guiGraphics, this.leftPos + 94 - 20, j - 17 - 20, this.leftPos + 94 + 20, j - 17 + 20, 25, 0.0625F, x, y, this.crowEntity
      );
      RenderSystem.disableDepthTest();
      MutableComponent hat = Component.translatable("entity.hexerei.crow_slot_0");
      MutableComponent misc1 = Component.translatable("entity.hexerei.crow_slot_1");
      MutableComponent misc2 = Component.translatable("entity.hexerei.crow_slot_2");
      MutableComponent command;
      if (((CrowContainer)this.menu).getCommand() == 0) {
         command = Component.translatable("entity.hexerei.crow_command_gui_0");
      } else if (((CrowContainer)this.menu).getCommand() == 1) {
         command = Component.translatable("entity.hexerei.crow_command_gui_1");
      } else if (((CrowContainer)this.menu).getCommand() == 2) {
         command = Component.translatable("entity.hexerei.crow_command_gui_2");
      } else {
         command = Component.translatable("entity.hexerei.crow_command_gui_3");
      }

      MutableComponent helpCommand;
      if (((CrowContainer)this.menu).getHelpCommand() == 0) {
         helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_0");
      } else if (((CrowContainer)this.menu).getHelpCommand() == 1) {
         helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_1");
      } else {
         helpCommand = Component.translatable("entity.hexerei.crow_help_command_gui_2");
      }

      this.drawFont(guiGraphics, hat, (float)(this.leftPos + 45) - this.font.width(hat.getVisualOrderText()) / 2, j + 32, 3, -10461088, false);
      this.drawFont(guiGraphics, misc1, (float)(this.leftPos + 94) - this.font.width(misc1.getVisualOrderText()) / 2, j + 32, 3, -10461088, false);
      this.drawFont(guiGraphics, misc2, (float)(this.leftPos + 142) - this.font.width(misc2.getVisualOrderText()) / 2, j + 32, 3, -10461088, false);
      this.drawFont(guiGraphics, command, (float)(this.leftPos + 56) - this.font.width(command.getVisualOrderText()) / 2, j + 77, 3, -10461088, false);
      this.drawFont(guiGraphics, helpCommand, (float)(this.leftPos + 131) - this.font.width(helpCommand.getVisualOrderText()) / 2, j + 77, 3, -10461088, false);
      if (!this.crowEntity.harvestWhitelist.isEmpty() && this.whitelistOffset > 21.0F) {
         ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(this.leftPos + 186.0F - 28.0F + (int)this.whitelistOffset, j + 73, 100.0F);
         guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
         guiGraphics.pose().scale(11.0F, 11.0F, 11.0F);
         guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
         Vec3 rotationOffset = new Vec3(0.5, 0.0, 0.5);
         float zRot = 0.0F;
         float xRot = 20.0F;
         float yRot = 130.0F * (this.whitelistOffset - 21.0F) / 8.0F;
         guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
         guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
         guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
         guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
         guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
         BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
         Lighting.setupFor3DItems();
         guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-90.0F));
         int max3 = 0;

         for (int itor = this.whitelistPage * 3; itor < this.crowEntity.harvestWhitelist.size(); itor++) {
            if (++max3 > 3) {
               break;
            }

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, -(max3 - 1) * 1.75F, 0.0F);
            BlockState state = this.crowEntity.harvestWhitelist.get(itor).defaultBlockState();
            if (state.hasProperty(BlockStateProperties.AGE_1)) {
               state = (BlockState)state.setValue(BlockStateProperties.AGE_1, 1);
            } else if (state.hasProperty(BlockStateProperties.AGE_2)) {
               state = (BlockState)state.setValue(BlockStateProperties.AGE_2, 2);
            } else if (state.hasProperty(BlockStateProperties.AGE_3)) {
               state = (BlockState)state.setValue(BlockStateProperties.AGE_3, 3);
            } else if (state.hasProperty(BlockStateProperties.AGE_4)) {
               state = (BlockState)state.setValue(BlockStateProperties.AGE_4, 4);
            } else if (state.hasProperty(BlockStateProperties.AGE_5)) {
               state = (BlockState)state.setValue(BlockStateProperties.AGE_5, 5);
            } else if (state.hasProperty(BlockStateProperties.AGE_7)) {
               state = (BlockState)state.setValue(BlockStateProperties.AGE_7, 7);
            }

            guiGraphics.pose().scale((this.whitelistOffset - 21.0F) / 8.0F, (this.whitelistOffset - 21.0F) / 8.0F, (this.whitelistOffset - 21.0F) / 8.0F);
            this.renderBlock(guiGraphics.pose(), buffer, 15728880, state, -1);
            if (state.hasProperty(PickableDoublePlant.HALF)) {
               guiGraphics.pose().pushPose();
               guiGraphics.pose().translate(0.0F, 1.0F, 0.0F);
               state = (BlockState)state.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
               this.renderBlock(guiGraphics.pose(), buffer, 15728880, state, -1);
               guiGraphics.pose().popPose();
            }

            guiGraphics.pose().popPose();
         }

         buffer.endBatch();
         guiGraphics.pose().popPose();
      }
   }

   private float moveTo(float input, float moveTo, float speed) {
      float distance = moveTo - input;
      if (Math.abs(distance) <= speed) {
         return moveTo;
      } else {
         if (distance > 0.0F) {
            input += speed;
         } else {
            input -= speed;
         }

         return input;
      }
   }

   public boolean mouseReleased(double x, double y, int button) {
      boolean mouseReleased = super.mouseReleased(x, y, button);
      if (this.rangeSliderClicked) {
         this.rangeSliderClicked = false;
         this.crowEntity.interactionRange = Mth.clamp(this.crowEntity.interactionRange + (int)(this.rangeSliderClickedPos - y), 0, 24);
         HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(this.crowEntity, this.crowEntity.interactionRange));
      }

      return mouseReleased;
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      int i = this.leftPos;
      int j = this.topPos - 28;
      if (this.whitelistOffset > 21.0F
         && x > i + 190.0F - 28.0F + (int)this.whitelistOffset
         && x < i + 190.0F - 28.0F + (int)this.whitelistOffset + 18.0F
         && y > j + 27
         && y < j + 27 + 18) {
         if (CrowWhitelistEvent.whiteListingCrow != null && ((CrowContainer)this.menu).crowEntity == CrowWhitelistEvent.whiteListingCrow) {
            CrowWhitelistEvent.whiteListingCrow = null;
         } else {
            CrowWhitelistEvent.whiteListingCrow = ((CrowContainer)this.menu).crowEntity;
            if (this.crowEntity.getOwner() instanceof Player) {
               ((Player)this.crowEntity.getOwner())
                  .displayClientMessage(Component.translatable("Right Click a harvestable block to add to the whitelist"), true);
            }
         }

         HexereiPacketHandler.sendToServer(new PlayerWhitelistingForCrowSyncToServer(CrowWhitelistEvent.whiteListingCrow != null));
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (this.leftPanelOffset > 21.0F
         && x > i + 9 - (int)this.leftPanelOffset
         && x < i + 9 - (int)this.leftPanelOffset + 16
         && y > j + 30 + 52 - this.crowEntity.interactionRange
         && y < j + 30 + 52 - this.crowEntity.interactionRange + 7) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.rangeSliderClicked = true;
         this.rangeSliderClickedPos = y;
      } else if (this.leftPanelOffset > 21.0F
         && x > i + 9 + 6 - (int)this.leftPanelOffset
         && x < i + 9 + 10 - (int)this.leftPanelOffset
         && y > j + 30 + 55 - 24
         && y < j + 30 + 56) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         int newPos = j + 30 + 55 - (int)y;
         HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(this.crowEntity, newPos));
         this.rangeSliderClicked = true;
         this.rangeSliderClickedPos = y;
      } else if (this.leftPanelOffset > 21.0F
         && x > i + 8 - (int)this.leftPanelOffset
         && x < i + 8 - (int)this.leftPanelOffset + 18
         && y > j + 30
         && y < j + 30 + 18) {
         HexereiPacketHandler.sendToServer(new CrowCanAttackToServer(this.crowEntity, !this.crowEntity.canAttack));
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (this.crowEntity.getInteractionRange() > 0
         && this.leftPanelOffset > 21.0F
         && x > i + 5 - (int)this.leftPanelOffset
         && x < i + 5 - (int)this.leftPanelOffset + 7
         && y > j + 107
         && y < j + 107 + 10) {
         HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(this.crowEntity, this.crowEntity.getInteractionRange() - 1));
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (this.crowEntity.getInteractionRange() < 24
         && this.leftPanelOffset > 21.0F
         && x > i + 5 + 18 - (int)this.leftPanelOffset
         && x < i + 5 + 18 - (int)this.leftPanelOffset + 7
         && y > j + 107
         && y < j + 107 + 10) {
         HexereiPacketHandler.sendToServer(new CrowInteractionRangeToServer(this.crowEntity, this.crowEntity.getInteractionRange() + 1));
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      } else if (this.whitelistOffset > 21.0F
         && x > i + 184 - 28 + 19 + (int)this.whitelistOffset
         && x < i + 184 - 28 + 19 + (int)this.whitelistOffset + 7
         && y > j + 19 + 29
         && y < j + 19 + 29 + 7) {
         if (((CrowContainer)this.menu).crowEntity.harvestWhitelist.size() > this.whitelistPage * 3) {
            ((CrowContainer)this.menu).crowEntity.harvestWhitelist.remove(this.whitelistPage * 3);
            if (((CrowContainer)this.menu).crowEntity.harvestWhitelist.size() - this.whitelistPage * 3 == 0 && this.whitelistPage > 0) {
               this.whitelistPage--;
            }

            HexereiPacketHandler.sendToServer(
               new CrowWhitelistSyncToServer(((CrowContainer)this.menu).crowEntity, ((CrowContainer)this.menu).crowEntity.harvestWhitelist)
            );
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      } else if (this.whitelistOffset > 21.0F
         && x > i + 184 - 28 + 19 + (int)this.whitelistOffset
         && x < i + 184 - 28 + 19 + (int)this.whitelistOffset + 7
         && y > j + 19 + 29 + 18
         && y < j + 19 + 29 + 18 + 7) {
         if (((CrowContainer)this.menu).crowEntity.harvestWhitelist.size() > 1 + this.whitelistPage * 3) {
            ((CrowContainer)this.menu).crowEntity.harvestWhitelist.remove(1 + this.whitelistPage * 3);
            HexereiPacketHandler.sendToServer(
               new CrowWhitelistSyncToServer(((CrowContainer)this.menu).crowEntity, ((CrowContainer)this.menu).crowEntity.harvestWhitelist)
            );
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      } else if (this.whitelistOffset > 21.0F
         && x > i + 184 - 28 + 19 + (int)this.whitelistOffset
         && x < i + 184 - 28 + 19 + (int)this.whitelistOffset + 7
         && y > j + 19 + 29 + 18 + 18
         && y < j + 19 + 29 + 18 + 18 + 7) {
         if (((CrowContainer)this.menu).crowEntity.harvestWhitelist.size() > 2 + this.whitelistPage * 3) {
            ((CrowContainer)this.menu).crowEntity.harvestWhitelist.remove(2 + this.whitelistPage * 3);
            HexereiPacketHandler.sendToServer(
               new CrowWhitelistSyncToServer(((CrowContainer)this.menu).crowEntity, ((CrowContainer)this.menu).crowEntity.harvestWhitelist)
            );
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      } else if (this.whitelistOffset > 21.0F
         && x > i + 184 + 3 - 28 + (int)this.whitelistOffset
         && x < i + 184 + 3 - 28 + (int)this.whitelistOffset + 7
         && y > j + 19 + 88
         && y < j + 19 + 88 + 10) {
         if (this.whitelistPage > 0) {
            this.whitelistPage--;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      } else if (this.whitelistOffset > 21.0F
         && x > i + 184 + 21 - 28 + (int)this.whitelistOffset
         && x < i + 184 + 20 - 28 + (int)this.whitelistOffset + 7
         && y > j + 19 + 88
         && y < j + 19 + 88 + 10) {
         if (((CrowContainer)this.menu).crowEntity.harvestWhitelist.size() > 3 + 3 * this.whitelistPage) {
            this.whitelistPage++;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      } else if (x > i + 23.0F && x < i + 23.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setCommand(0);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(Component.translatable("entity.hexerei.crow_command_0", new Object[]{this.crowEntity.getName()}), true);
         }
      } else if (x > i + 43.0F && x < i + 43.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setCommand(1);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(Component.translatable("entity.hexerei.crow_command_1", new Object[]{this.crowEntity.getName()}), true);
         }
      } else if (x > i + 63.0F && x < i + 63.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setCommand(2);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(Component.translatable("entity.hexerei.crow_command_2", new Object[]{this.crowEntity.getName()}), true);
         }
      } else if (x > i + 83.0F && x < i + 83.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setCommand(3);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(
                  Component.translatable(
                     "entity.hexerei.crow_command_3_" + ((CrowContainer)this.menu).getHelpCommand(), new Object[]{this.crowEntity.getName()}
                  ),
                  true
               );
         }
      } else if (x > i + 107.0F && x < i + 107.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setHelpCommand(0);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_0", new Object[]{this.crowEntity.getName()}), true);
         }
      } else if (x > i + 127.0F && x < i + 127.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setHelpCommand(1);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_1", new Object[]{this.crowEntity.getName()}), true);
         }
      } else if (x > i + 147.0F && x < i + 147.0F + 18.0F && y > j + 92 && y < j + 92 + 18) {
         ((CrowContainer)this.menu).setHelpCommand(2);
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.crowEntity.getOwner() instanceof Player) {
            ((Player)this.crowEntity.getOwner())
               .displayClientMessage(Component.translatable("entity.hexerei.crow_command_3_2", new Object[]{this.crowEntity.getName()}), true);
         }
      }

      return mouseClicked;
   }

   @OnlyIn(Dist.CLIENT)
   private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   @OnlyIn(Dist.CLIENT)
   public void renderSingleBlock(
      BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
   ) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch (rendershape) {
            case MODEL:
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
               float f = (color >> 16 & 0xFF) / 255.0F;
               float f1 = (color >> 8 & 0xFF) / 255.0F;
               float f2 = (color & 0xFF) / 255.0F;
               dispatcher.getModelRenderer()
                  .renderModel(
                     poseStack.last(),
                     p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)),
                     p_110913_,
                     bakedmodel,
                     f,
                     f1,
                     f2,
                     p_110916_,
                     p_110917_,
                     modelData,
                     null
                  );
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               poseStack.translate(0.2, -0.1, -0.1);
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
