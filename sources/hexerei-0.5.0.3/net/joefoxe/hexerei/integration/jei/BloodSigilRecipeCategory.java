package net.joefoxe.hexerei.integration.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class BloodSigilRecipeCategory implements IRecipeCategory<BloodSigilRecipeJEI> {
   public static final ResourceLocation UID = HexereiUtil.getResource("blood_sigil");
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/gui/blood_sigil_gui_jei.png");
   private IDrawable background;
   private final IDrawable icon;
   private final IDrawable cauldronFG;

   public BloodSigilRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 126, 59);
      this.icon = helper.createDrawableItemStack(new ItemStack((ItemLike)ModItems.BLOOD_SIGIL.get()));
      this.cauldronFG = helper.createDrawable(TEXTURE, 232, 48, 24, 16);
   }

   public int getWidth() {
      return this.background.getWidth();
   }

   public int getHeight() {
      return this.background.getHeight();
   }

   public void getTooltip(ITooltipBuilder tooltip, BloodSigilRecipeJEI recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if (this.isHovering(mouseX, mouseY, 33.0, 25.0, 24.0, 15.0)) {
         tooltip.add(Component.translatable("gui.jei.category.blood_sigil_tooltip1"));
      } else if (this.isHovering(mouseX, mouseY, 58.0, 18.0, 24.0, 21.0)) {
         tooltip.add(Component.translatable("gui.jei.category.blood_sigil_tooltip2"));
      }

      super.getTooltip(tooltip, recipe, recipeSlotsView, mouseX, mouseY);
   }

   public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
   }

   public RecipeType<BloodSigilRecipeJEI> getRecipeType() {
      return new RecipeType(UID, BloodSigilRecipeJEI.class);
   }

   public Component getTitle() {
      return Component.translatable("gui.jei.category.blood_sigil");
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, final BloodSigilRecipeJEI recipe, IFocusGroup focuses) {
      builder.moveRecipeTransferButton(160, 90);
      builder.addSlot(RecipeIngredientRole.INPUT, 14, 24).addItemStack(recipe.getInput());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 24)
         .setFluidRenderer(500L, false, 16, 16)
         .addFluidStack(recipe.getOutputFluid().getFluid(), 250L, recipe.getOutputFluid().getComponentsPatch())
         .setOverlay(
            new IDrawable() {
               public int getWidth() {
                  return 16;
               }

               public int getHeight() {
                  return 16;
               }

               public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
                  RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                  RenderSystem.enableBlend();
                  RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  guiGraphics.pose().pushPose();
                  guiGraphics.pose().translate(xOffset, yOffset, 0.0F);
                  double val = Math.sin(ClientEvents.getClientTicks() / 5.0F);
                  if (Minecraft.getInstance().player != null) {
                     BloodSigilRecipeCategory.renderEntityInInventoryFollowsAngle(
                        guiGraphics,
                        9.0,
                        Math.min(val, 0.25) * 10.0 + 10.0,
                        9.0,
                        16,
                        (float)Math.toRadians(-20.0),
                        (float)Math.toRadians(-30.0),
                        Minecraft.getInstance().player
                     );
                  }

                  guiGraphics.pose().popPose();
                  Lighting.setupFor3DItems();
                  RenderSystem.enableDepthTest();
                  BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                  guiGraphics.pose().pushPose();
                  guiGraphics.pose().translate(xOffset, yOffset, 0.0F);
                  guiGraphics.pose().mulPose(new Matrix4f().scale(1.0F, -1.0F, 1.0F));
                  guiGraphics.pose().translate(-3.0F, -15.0F, 0.0F);
                  guiGraphics.pose().scale(17.0F, 17.0F, 17.0F);
                  guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(0.0F));
                  guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(20.0F));
                  guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(30.0F));
                  BlockState blockState = (BlockState)((MixingCauldron)ModBlocks.MIXING_CAULDRON.get())
                     .defaultBlockState()
                     .setValue(MixingCauldron.GUI_RENDER, true);
                  RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                  RenderSystem.enableBlend();
                  RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  BloodSigilRecipeCategory.this.renderBlock(guiGraphics.pose(), buffer, 15728880, blockState, -12566464);
                  MixingCauldronRenderer.renderFluidGUI(guiGraphics.pose(), buffer, recipe.getOutputFluid(), 1.0F, 1.0F, OverlayTexture.NO_OVERLAY);
                  guiGraphics.pose().popPose();
                  BloodSigilRecipeCategory.this.cauldronFG.draw(guiGraphics, 92, 28);
               }
            },
            -34,
            0
         );
   }

   public void draw(BloodSigilRecipeJEI recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      Minecraft minecraft = Minecraft.getInstance();
      Component outputName = recipe.getOutputFluid().getHoverName();
      this.background.draw(guiGraphics);
      int width = minecraft.font.width(outputName);
      float lineHeight = 9.0F / 2.0F;
      if (width > 80) {
         float percent = width / 80.0F;
         guiGraphics.pose().pushPose();
         guiGraphics.pose().scale(1.0F / percent, 1.0F / percent, 1.0F / percent);
         minecraft.font
            .drawInBatch(
               outputName,
               7.0F * percent,
               (5.0F + lineHeight) * percent - 4.5F,
               -12566464,
               false,
               guiGraphics.pose().last().pose(),
               guiGraphics.bufferSource(),
               DisplayMode.NORMAL,
               0,
               15728880
            );
         guiGraphics.pose().popPose();
      } else {
         minecraft.font
            .drawInBatch(
               outputName,
               7.0F,
               5.0F + lineHeight - 4.5F,
               -12566464,
               false,
               guiGraphics.pose().last().pose(),
               guiGraphics.bufferSource(),
               DisplayMode.NORMAL,
               0,
               15728880
            );
      }
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

   public static void renderEntityInInventoryFollowsAngle(
      GuiGraphics pGuiGraphics, double pX, double pY, double pZ, int pScale, float angleXComponent, float angleYComponent, LivingEntity pEntity
   ) {
      Quaternionf quaternionf = new Quaternionf().rotateZ(3.1415927F);
      Quaternionf quaternionf1 = new Quaternionf().rotateX(angleYComponent * 20.0F * 0.017453292F);
      quaternionf.mul(quaternionf1);
      float f2 = pEntity.yBodyRot;
      float f3 = pEntity.getYRot();
      float f4 = pEntity.getXRot();
      float f5 = pEntity.yHeadRotO;
      float f6 = pEntity.yHeadRot;
      pEntity.yBodyRot = 180.0F + angleXComponent * 20.0F;
      pEntity.setYRot(180.0F + angleXComponent * 40.0F);
      pEntity.setXRot(-angleYComponent * 20.0F);
      pEntity.yHeadRot = pEntity.getYRot();
      pEntity.yHeadRotO = pEntity.getYRot();
      renderEntityInInventory(pGuiGraphics, pX, pY, pZ, pScale, quaternionf, quaternionf1, pEntity);
      pEntity.yBodyRot = f2;
      pEntity.setYRot(f3);
      pEntity.setXRot(f4);
      pEntity.yHeadRotO = f5;
      pEntity.yHeadRot = f6;
   }

   public static void renderEntityInInventory(
      GuiGraphics pGuiGraphics, double pX, double pY, double pZ, int pScale, Quaternionf pPose, @Nullable Quaternionf pCameraOrientation, LivingEntity pEntity
   ) {
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(pX, pY, pZ);
      pGuiGraphics.pose().mulPose(new Matrix4f().scaling(pScale, pScale, -pScale));
      pGuiGraphics.pose().mulPose(pPose);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      if (pCameraOrientation != null) {
         pCameraOrientation.conjugate();
         entityrenderdispatcher.overrideCameraOrientation(pCameraOrientation);
      }

      entityrenderdispatcher.setRenderShadow(false);
      RenderSystem.runAsFancy(
         () -> entityrenderdispatcher.render(pEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, pGuiGraphics.pose(), pGuiGraphics.bufferSource(), 15728880)
      );
      pGuiGraphics.flush();
      entityrenderdispatcher.setRenderShadow(true);
      pGuiGraphics.pose().popPose();
      Lighting.setupFor3DItems();
   }
}
