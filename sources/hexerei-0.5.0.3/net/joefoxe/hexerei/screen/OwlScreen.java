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
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks.FavoriteBlockQuirk;
import net.joefoxe.hexerei.container.OwlContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class OwlScreen extends AbstractContainerScreen<OwlContainer> {
   private static final int FRONT_OVERLAY_BLIT_LAYER = 3;
   private static final int FRONT_BLIT_LAYER = 2;
   private static final int BACK_OVERLAY_BLIT_LAYER = 1;
   private static final int BACK_BLIT_LAYER = 0;
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/owl_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   public final OwlEntity owlEntity;
   public boolean quirkSideBarHidden;

   public OwlScreen(OwlContainer owlContainer, Inventory inv, Component titleIn) {
      super(owlContainer, inv, titleIn);
      this.owlEntity = owlContainer.owlEntity;
      this.titleLabelY = -27;
      this.titleLabelX = 4;
      this.inventoryLabelY = 94;
      this.inventoryLabelX = 9;
      this.quirkSideBarHidden = true;
   }

   protected void init() {
      super.init();
   }

   protected void containerTick() {
      super.containerTick();
   }

   public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
      this.renderBackground(guiGraphics, x, y, partialTicks);
      super.render(guiGraphics, x, y, partialTicks);
      int i = this.leftPos;
      int j = this.topPos;
      List<FavoriteBlockQuirk> list = FavoriteBlockQuirk.fromController(this.owlEntity.quirkController);
      if (this.quirkSideBarHidden) {
         if (!list.isEmpty()) {
            if (this.hovering(x, y, 9.0, 12.0, 160.0, 44.0)) {
               guiGraphics.blit(this.GUI, i + 161, j + 45, 2, 215.0F, 12.0F, 7, 10, 256, 256);
            } else {
               guiGraphics.blit(this.GUI, i + 161, j + 45, 2, 215.0F, 1.0F, 7, 10, 256, 256);
            }
         }
      } else {
         guiGraphics.blit(this.GUI, i + 170, j + 37, 2, 230.0F, 26.0F, 26, 26, 256, 256);
         if (this.hovering(x, y, 8.0, 8.0, 161.0, 46.0)) {
            guiGraphics.blit(this.GUI, i + 162, j + 47, 2, 223.0F, 12.0F, 6, 6, 256, 256);
         } else {
            guiGraphics.blit(this.GUI, i + 162, j + 47, 2, 223.0F, 1.0F, 6, 6, 256, 256);
         }

         ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(i + 169.5, j + 63, 200.0);
         guiGraphics.pose().translate(8.0F, -8.0F, 0.0F);
         guiGraphics.pose().scale(11.0F, 11.0F, 11.0F);
         guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
         Vec3 rotationOffset = new Vec3(0.5, 0.0, 0.5);
         float zRot = 0.0F;
         float xRot = 20.0F;
         float yRot = 215.0F;
         guiGraphics.pose().translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
         guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(zRot));
         guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(xRot));
         guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(yRot));
         guiGraphics.pose().translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
         BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
         Lighting.setupFor3DItems();
         guiGraphics.pose().last().normal().rotate(Axis.YP.rotationDegrees(-45.0F));
         if (!list.isEmpty() && this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
            BlockState state = ((FavoriteBlockQuirk)list.getFirst()).getFavoriteBlock().defaultBlockState();
            int col = this.minecraft.getBlockColors().getColor(state, this.minecraft.level, this.minecraft.player.blockPosition());
            this.renderBlock(guiGraphics.pose(), buffer, 15728880, state, col);
            if (state.hasProperty(PickableDoublePlant.HALF)) {
               guiGraphics.pose().pushPose();
               guiGraphics.pose().translate(0.0F, 1.0F, 0.0F);
               state = (BlockState)state.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
               this.renderBlock(guiGraphics.pose(), buffer, 15728880, state, col);
               guiGraphics.pose().popPose();
            }
         }

         buffer.endBatch();
         guiGraphics.pose().popPose();
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(
         guiGraphics, this.leftPos + 94 - 20, j - 17 - 20, this.leftPos + 94 + 20, j - 17 + 20, 20, 0.0625F, x, y, this.owlEntity
      );
      this.renderTooltip(guiGraphics, x, y);
      this.renderButtonTooltip(guiGraphics, x, y);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   public boolean hovering(double mouseX, double mouseY, double width, double height, double x, double y) {
      return mouseX >= this.leftPos + x && mouseX < this.leftPos + x + width && mouseY >= this.topPos + y && mouseY < this.topPos + y + height;
   }

   public void renderButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      List<Component> components = new ArrayList<>();
      List<FavoriteBlockQuirk> list = FavoriteBlockQuirk.fromController(this.owlEntity.quirkController);
      if (list.size() > 0) {
         if (!this.quirkSideBarHidden) {
            if (this.hovering(mouseX, mouseY, 18.0, 18.0, 174.0, 41.0)) {
               components.add(Component.translatable("tooltip.hexerei.owl_favorite_block").withStyle(ChatFormatting.DARK_AQUA));
               components.add(Component.translatable("tooltip.hexerei.owl_favorite_block2").withStyle(ChatFormatting.GRAY));
               components.add(Component.literal(""));
               if (this.minecraft != null) {
                  components.addAll(
                     list.get(0)
                        .getFavoriteBlock()
                        .asItem()
                        .getDefaultInstance()
                        .getTooltipLines(
                           TooltipContext.EMPTY, this.minecraft.player, this.minecraft.options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL
                        )
                  );
               }
            } else if (this.hovering(mouseX, mouseY, 8.0, 8.0, 161.0, 46.0)) {
               components.add(Component.translatable("tooltip.hexerei.owl_close_quirks_tab"));
            }
         } else if (this.hovering(mouseX, mouseY, 9.0, 12.0, 160.0, 44.0)) {
            components.add(Component.translatable("tooltip.hexerei.owl_open_quirks_tab"));
         }
      }

      if (!components.isEmpty()) {
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
      int i = this.leftPos;
      int j = this.topPos;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.GUI);
      guiGraphics.blit(this.GUI, i, j, 0, 0.0F, 0.0F, 188, 114, 256, 256);
      if (!this.owlEntity.itemHandler.getStackInSlot(0).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 86 + 24, j + 50, 0, 235.0F, 31.0F, 16, 16, 256, 256);
      }

      if (!this.owlEntity.itemHandler.getStackInSlot(1).isEmpty()) {
         guiGraphics.blit(this.GUI, i + 37 + 24, j + 50, 0, 235.0F, 31.0F, 16, 16, 256, 256);
      }

      guiGraphics.blit(this.GUI, i + 81, j - 30, 0, 230.0F, 0.0F, 26, 26, 256, 256);
      guiGraphics.blit(this.INVENTORY, i + 6, j + 90, 0, 0.0F, 0.0F, 176, 100, 256, 256);
      RenderSystem.setShaderTexture(0, this.GUI);
      MutableComponent hat = Component.translatable("entity.hexerei.crow_slot_0");
      MutableComponent hand = Component.translatable("entity.hexerei.crow_slot_1");
      this.drawFont(guiGraphics, hat, (float)(this.leftPos + 45 + 24) - this.font.width(hat.getVisualOrderText()) / 2, j + 32, 3, -10461088, false);
      this.drawFont(guiGraphics, hand, (float)(this.leftPos + 94 + 24) - this.font.width(hand.getVisualOrderText()) / 2, j + 32, 3, -10461088, false);
   }

   public boolean mouseReleased(double x, double y, int button) {
      return super.mouseReleased(x, y, button);
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      List<FavoriteBlockQuirk> list = FavoriteBlockQuirk.fromController(this.owlEntity.quirkController);
      if (list.size() > 0) {
         if (this.quirkSideBarHidden && this.hovering(x, y, 9.0, 12.0, 160.0, 44.0)) {
            this.quirkSideBarHidden = !this.quirkSideBarHidden;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         } else if (!this.quirkSideBarHidden && this.hovering(x, y, 8.0, 8.0, 161.0, 46.0)) {
            this.quirkSideBarHidden = !this.quirkSideBarHidden;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }
      }

      return mouseClicked;
   }

   public void mouseMoved(double pMouseX, double pMouseY) {
      super.mouseMoved(pMouseX, pMouseY);
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
