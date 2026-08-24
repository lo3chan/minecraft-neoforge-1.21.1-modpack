package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CofferRenderer implements BlockEntityRenderer<CofferTile> {
   public static ModelResourceLocation ENTANGLED_COFFER_CHEST = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("hexerei", "block/entangled_coffer_chest")
   );
   public static ModelResourceLocation ENTANGLED_COFFER_CONTAINER = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("hexerei", "block/entangled_coffer_container")
   );
   public static ModelResourceLocation ENTANGLED_COFFER_HINGE = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("hexerei", "block/entangled_coffer_hinge")
   );
   public static ModelResourceLocation ENTANGLED_COFFER_LID = ModelResourceLocation.standalone(
      ResourceLocation.fromNamespaceAndPath("hexerei", "block/entangled_coffer_lid")
   );

   public static float easeOutBack(float x) {
      float c1 = 1.70158F;
      float c3 = c1 + 1.0F;
      return (float)(1.0 + c3 * Math.pow(x - 1.0F, 3.0) + c1 * Math.pow(x - 1.0F, 2.0));
   }

   public void render(
      CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CofferTile) {
         matrixStackIn.pushPose();
         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(1.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(1.0, 0.0, 0.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
         }

         if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
            BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_CHEST);
            if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               int i = tileEntityIn.getDyeColor();
               float f = (i >> 16 & 0xFF) / 255.0F;
               float f1 = (i >> 8 & 0xFF) / 255.0F;
               float f2 = (i & 0xFF) / 255.0F;

               for (RenderType rt : baseModel.getRenderTypes(
                  ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
               )) {
                  dispatcher.getModelRenderer()
                     .renderModel(
                        matrixStackIn.last(),
                        bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                        ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                        baseModel,
                        f,
                        f1,
                        f2,
                        combinedLightIn,
                        combinedOverlayIn,
                        ModelData.EMPTY,
                        rt
                     );
               }
            }
         } else {
            this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_CHEST.get()).defaultBlockState(), tileEntityIn.getDyeColor());
         }

         matrixStackIn.popPose();
         matrixStackIn.pushPose();
         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(0.5, 0.25, 0.25);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.translate(0.5, 0.25, 0.75);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(0.75, 0.25, 0.5);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
         } else if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(0.25, 0.25, 0.5);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
         }

         float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
         float percent = lerpDegreesOpened / 112.0F;
         percent = easeOutBack(percent);
         float sideRotation = percent * 135.0F;
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(percent * 112.0F));
         if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
            BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_LID);
            if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
               BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
               int i = tileEntityIn.getDyeColor();
               float f = (i >> 16 & 0xFF) / 255.0F;
               float f1 = (i >> 8 & 0xFF) / 255.0F;
               float f2 = (i & 0xFF) / 255.0F;

               for (RenderType rt : baseModel.getRenderTypes(
                  ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
               )) {
                  dispatcher.getModelRenderer()
                     .renderModel(
                        matrixStackIn.last(),
                        bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                        ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                        baseModel,
                        f,
                        f1,
                        f2,
                        combinedLightIn,
                        combinedOverlayIn,
                        ModelData.EMPTY,
                        rt
                     );
               }
            }
         } else {
            this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_LID.get()).defaultBlockState(), tileEntityIn.getDyeColor());
         }

         matrixStackIn.popPose();
         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH
            || tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.73311875, 0.154825, 0.3421875);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.73311875, 0.154825, 0.6578125);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.26688125, 0.154825, 0.3421875);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.26688125, 0.154825, 0.6578125);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(
               0.0625 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.109375 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.3125
            );
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_CONTAINER);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_CONTAINER.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(
               0.6875 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.109375 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.3125
            );
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_CONTAINER);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_CONTAINER.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
         }

         if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST
            || tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(0.73311875, 0.154825, 0.3421875);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(0.73311875, 0.154825, 0.6578125);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-sideRotation));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(0.26688125, 0.154825, 0.3421875);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(0.26688125, 0.154825, 0.6578125);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(sideRotation));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_HINGE);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_HINGE.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(
               0.0625 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.109375 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.3125
            );
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_CONTAINER);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_CONTAINER.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 0.0, 1.0);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(
               0.6875 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.109375 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
               0.3125
            );
            if (tileEntityIn.getBlockState().is(ModBlocks.ENTANGLED_COFFER)) {
               BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(ENTANGLED_COFFER_CONTAINER);
               if (baseModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                  BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                  int i = -1;
                  float f = (i >> 16 & 0xFF) / 255.0F;
                  float f1 = (i >> 8 & 0xFF) / 255.0F;
                  float f2 = (i & 0xFF) / 255.0F;

                  for (RenderType rt : baseModel.getRenderTypes(
                     ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(), RandomSource.create(42L), ModelData.EMPTY
                  )) {
                     dispatcher.getModelRenderer()
                        .renderModel(
                           matrixStackIn.last(),
                           bufferIn.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                           ((Coffer)ModBlocks.ENTANGLED_COFFER.get()).defaultBlockState(),
                           baseModel,
                           f,
                           f1,
                           f2,
                           combinedLightIn,
                           combinedOverlayIn,
                           ModelData.EMPTY,
                           rt
                        );
                  }
               }
            } else {
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.COFFER_CONTAINER.get()).defaultBlockState());
            }

            matrixStackIn.popPose();
         }

         if (lerpDegreesOpened > 2.0F) {
            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               this.renderItemsNorth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               this.renderItemsWest(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               this.renderItemsSouth(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }

            if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               this.renderItemsEast(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
            }
         }
      }
   }

   private void renderItemsNorth(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
      float percent = lerpDegreesOpened / 112.0F;
      percent = easeOutBack(percent);
      float sideRotation = percent * 135.0F;
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.25, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.34375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.4375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.5625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.65625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.75, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.25, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.34375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.4375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.5625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.65625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.75, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.25, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.34375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.4375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.5625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.65625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.75, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.25, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.34375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.4375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.5625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.65625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.75, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.25, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.34375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.4375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.5625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.65625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0, 0.0, -0.96875);
      matrixStackIn.translate(0.75, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
   }

   private void renderItemsSouth(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
      float percent = lerpDegreesOpened / 112.0F;
      percent = easeOutBack(percent);
      float sideRotation = percent * 135.0F;
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.25, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.34375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.4375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.5625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.65625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.75, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.25, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.34375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.4375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.5625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.65625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.75, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.25, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.34375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.4375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.5625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.65625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.75, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.25, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.34375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.4375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.5625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.65625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.75, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.25, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.34375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.4375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.5625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.65625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0, 0.0, -0.03125);
      matrixStackIn.translate(0.75, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
   }

   private void renderItemsWest(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
      float percent = lerpDegreesOpened / 112.0F;
      percent = easeOutBack(percent);
      float sideRotation = percent * 135.0F;
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.03125, 0.0, 1.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
   }

   private void renderItemsEast(CofferTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      float lerpDegreesOpened = Mth.lerp(partialTicks, tileEntityIn.degreesOpenedPrev, tileEntityIn.degreesOpened);
      float percent = lerpDegreesOpened / 112.0F;
      percent = easeOutBack(percent);
      float sideRotation = percent * 135.0F;
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(35), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(34), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(33), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(32), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(31), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.1875 - Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(30), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(29), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(28), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.40625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(27), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(26), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(25), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(24), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(23), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(22), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.46875);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(21), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(20), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(19), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(18), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(17), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(16), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.53125);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(15), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(14), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(13), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(12), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(11), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(10), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.59375);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(9), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.25, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(8), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.34375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(7), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.4375, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(6), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.4375
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(5), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(4), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(1.0, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(
         0.8125 + Math.sin((sideRotation - 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.25 - Math.cos((sideRotation + 90.0F) / 180.0F * 3.141592653589793) * 3.0 / 16.0,
         0.5625
      );
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(3), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.5625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(2), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.65625, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(1), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.96875, 0.0, 0.0);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(270.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
      matrixStackIn.translate(-1.0F, 0.0F, -1.0F);
      matrixStackIn.translate(0.75, 0.15, 0.65625);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(22.5F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(15.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(2.5F));
      matrixStackIn.scale(0.2F, 0.2F, 0.2F);
      this.renderItem(tileEntityIn.getItemStackInSlot(0), tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
      matrixStackIn.popPose();
   }

   private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
      Minecraft.getInstance()
         .getBlockRenderer()
         .renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
   }

   private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
      this.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color
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
                     p_110914_.last(),
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
               IClientItemExtensions.of(stack.getItem())
                  .getCustomRenderer()
                  .renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
