package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.data.candle.PotionCandleEffect;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.util.DynamicTextureHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CandleRenderer implements BlockEntityRenderer<CandleTile> {
   CandleModel herbLayer;
   CandleModel glowLayer;
   CandleModel swirlLayer;
   CandleModel candleModel;
   CandleModel baseModel;

   public AABB getRenderBoundingBox(CandleTile blockEntity) {
      return super.getRenderBoundingBox(blockEntity).inflate(25.0);
   }

   public void render(
      CandleTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof CandleTile) {
         if (this.herbLayer == null) {
            this.herbLayer = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_HERB_LAYER));
         }

         if (this.glowLayer == null) {
            this.glowLayer = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_GLOW_LAYER));
         }

         if (this.swirlLayer == null) {
            this.swirlLayer = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_SWIRL_LAYER));
         }

         if (this.candleModel == null) {
            this.candleModel = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_LAYER));
         }

         if (this.baseModel == null) {
            this.baseModel = new CandleModel(Minecraft.getInstance().getEntityModels().bakeLayer(CandleModel.CANDLE_BASE_LAYER));
         }

         for (CandleData candleData : tileEntityIn.candles) {
            candleData.baseHeight = 0;
            if (candleData.hasCandle && candleData.hasBase()) {
               matrixStackIn.pushPose();
               this.translate(matrixStackIn, candleData, partialTicks, (Direction)tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
               if (candleData.base.layerFromBlockLocation) {
                  Optional<Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleData.base.layer);
                  if (holder.isPresent()) {
                     BlockState blockState = ((Block)holder.get().value()).defaultBlockState();
                     ResourceLocation loc = HexereiUtil.getResource("candle_base/" + candleData.base.layer.getPath());
                     if (DynamicTextureHandler.textures.containsKey(loc)) {
                        DynamicTextureHandler.DynamicBaseSprite baseSprite = DynamicTextureHandler.textures.get(loc);
                        VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(loc));
                        renderCube(
                           matrixStackIn,
                           vertexConsumer2,
                           baseSprite.width,
                           baseSprite.height,
                           baseSprite.width,
                           16.0F,
                           16.0F,
                           combinedLightIn,
                           combinedOverlayIn
                        );
                        candleData.baseHeight = baseSprite.height;
                     } else {
                        DynamicTextureHandler.addNewSprite(loc, blockState);
                     }
                  } else {
                     candleData.baseHeight = 2;
                     VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(MissingTextureAtlasSprite.getLocation()));
                     renderCube(matrixStackIn, vertexConsumer2, 3.0F, candleData.baseHeight, 3.0F, 16.0F, 16.0F, combinedLightIn, combinedOverlayIn);
                  }
               } else {
                  candleData.baseHeight = 1;
                  VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleData.base.layer));
                  renderCube(matrixStackIn, vertexConsumer2, 3.0F, candleData.baseHeight, 3.0F, 16.0F, 16.0F, combinedLightIn, combinedOverlayIn);
               }

               matrixStackIn.popPose();
            }
         }

         for (CandleData candleDatax : tileEntityIn.candles) {
            if (candleDatax.hasCandle) {
               matrixStackIn.pushPose();
               this.translate(matrixStackIn, candleDatax, partialTicks, (Direction)tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
               matrixStackIn.translate(0.0F, 1.4375F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               float[] col = HexereiUtil.rgbIntToFloatArray(candleDatax.dyeColor);
               matrixStackIn.translate(0.0F, -candleDatax.baseHeight / 16.0F, 0.0F);
               VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(HexereiUtil.getResource("textures/block/candle.png")));
               if (candleDatax.height != 0 && candleDatax.height <= 7) {
                  this.candleModel.wax[candleDatax.height - 1]
                     .render(
                        matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 1.0F)
                     );
               }

               matrixStackIn.pushPose();
               matrixStackIn.translate(0.0F, (7 - candleDatax.height) / 16.0F, 0.0F);
               this.candleModel
                  .wick
                  .render(matrixStackIn, vertexConsumer, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
               matrixStackIn.popPose();
               matrixStackIn.popPose();
            }
         }

         for (CandleData candleDataxx : tileEntityIn.candles) {
            if (candleDataxx.hasCandle && candleDataxx.hasHerb()) {
               matrixStackIn.pushPose();
               this.translate(matrixStackIn, candleDataxx, partialTicks, (Direction)tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
               matrixStackIn.translate(0.0F, 1.4375F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               float[] col = HexereiUtil.rgbIntToFloatArray(candleDataxx.dyeColor);
               matrixStackIn.translate(0.0F, -candleDataxx.baseHeight / 16.0F, 0.0F);
               if (candleDataxx.height != 0 && candleDataxx.height <= 7) {
                  if (candleDataxx.herb.layerFromBlockLocation) {
                     Optional<Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleDataxx.herb.layer);
                     if (holder.isPresent()) {
                        BlockState blockState = ((Block)holder.get().value()).defaultBlockState();
                        TextureAtlasSprite sprite = getFirstSprite(blockState);
                        if (sprite != null) {
                           VertexConsumer vertexConsumer2 = bufferIn.getBuffer(
                              RenderType.entityTranslucent(
                                 ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png")
                              )
                           );
                           this.herbLayer.wax[candleDataxx.height - 1]
                              .render(
                                 matrixStackIn,
                                 vertexConsumer2,
                                 combinedLightIn,
                                 OverlayTexture.NO_OVERLAY,
                                 HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F)
                              );
                        }
                     }
                  } else {
                     VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleDataxx.herb.layer));
                     this.herbLayer.wax[candleDataxx.height - 1]
                        .render(
                           matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F)
                        );
                  }
               }

               matrixStackIn.popPose();
            }
         }

         for (CandleData candleDataxxx : tileEntityIn.candles) {
            if (candleDataxxx.hasCandle && candleDataxxx.hasGlow()) {
               matrixStackIn.pushPose();
               this.translate(matrixStackIn, candleDataxxx, partialTicks, (Direction)tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
               matrixStackIn.translate(0.0F, 1.4375F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               float[] col = HexereiUtil.rgbIntToFloatArray(candleDataxxx.dyeColor);
               matrixStackIn.translate(0.0F, -candleDataxxx.baseHeight / 16.0F, 0.0F);
               if (candleDataxxx.glow.layerFromBlockLocation) {
                  Optional<Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleDataxxx.glow.layer);
                  if (holder.isPresent()) {
                     BlockState blockState = ((Block)holder.get().value()).defaultBlockState();
                     TextureAtlasSprite sprite = getFirstSprite(blockState);
                     if (sprite != null) {
                        VertexConsumer vertexConsumer2 = bufferIn.getBuffer(
                           RenderType.entityTranslucent(
                              ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png")
                           )
                        );
                        if (candleDataxxx.effect instanceof PotionCandleEffect potionCandleEffect && potionCandleEffect.effect != null) {
                           int color = potionCandleEffect.effect.getColor();
                           float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                           if (candleDataxxx.height != 0 && candleDataxxx.height <= 7) {
                              this.glowLayer.wax[candleDataxxx.height - 1]
                                 .render(
                                    matrixStackIn,
                                    vertexConsumer2,
                                    combinedLightIn,
                                    OverlayTexture.NO_OVERLAY,
                                    HexereiUtil.getColorValueAlpha(col2[0], col2[1], col2[2], 0.75F)
                                 );
                           }
                        } else if (candleDataxxx.height != 0 && candleDataxxx.height <= 7) {
                           this.glowLayer.wax[candleDataxxx.height - 1]
                              .render(
                                 matrixStackIn,
                                 vertexConsumer2,
                                 combinedLightIn,
                                 OverlayTexture.NO_OVERLAY,
                                 HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F)
                              );
                        }
                     }
                  }
               } else {
                  VertexConsumer vertexConsumer2 = bufferIn.getBuffer(RenderType.entityTranslucent(candleDataxxx.glow.layer));
                  if (candleDataxxx.effect instanceof PotionCandleEffect potionCandleEffectx && potionCandleEffectx.effect != null) {
                     int color = potionCandleEffectx.effect.getColor();
                     float[] col2 = HexereiUtil.rgbIntToFloatArray(color);
                     if (candleDataxxx.height != 0 && candleDataxxx.height <= 7) {
                        this.glowLayer.wax[candleDataxxx.height - 1]
                           .render(
                              matrixStackIn,
                              vertexConsumer2,
                              combinedLightIn,
                              OverlayTexture.NO_OVERLAY,
                              HexereiUtil.getColorValueAlpha(col2[0], col2[1], col2[2], 0.75F)
                           );
                     }
                  } else if (candleDataxxx.height != 0 && candleDataxxx.height <= 7) {
                     this.glowLayer.wax[candleDataxxx.height - 1]
                        .render(
                           matrixStackIn, vertexConsumer2, combinedLightIn, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 0.75F)
                        );
                  }
               }

               matrixStackIn.popPose();
            }
         }

         for (CandleData candleDataxxxx : tileEntityIn.candles) {
            if (candleDataxxxx.hasCandle && candleDataxxxx.hasSwirl()) {
               matrixStackIn.pushPose();
               this.translate(matrixStackIn, candleDataxxxx, partialTicks, (Direction)tileEntityIn.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
               matrixStackIn.translate(0.0F, 1.4375F, 0.0F);
               matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
               matrixStackIn.translate(0.0F, -candleDataxxxx.baseHeight / 16.0F, 0.0F);
               float[] col = HexereiUtil.rgbIntToFloatArray(candleDataxxxx.dyeColor);
               if (candleDataxxxx.swirl.layerFromBlockLocation) {
                  Optional<Reference<Block>> holder = BuiltInRegistries.BLOCK.getHolder(candleDataxxxx.swirl.layer);
                  if (holder.isPresent()) {
                     BlockState blockState = ((Block)holder.get().value()).defaultBlockState();
                     TextureAtlasSprite sprite = getFirstSprite(blockState);
                     if (sprite != null) {
                        float offset = ClientEvents.getClientTicksWithoutPartial() + (float)Minecraft.getInstance().getFrameTimeNs();
                        VertexConsumer vertexConsumer2 = bufferIn.getBuffer(
                           RenderType.energySwirl(
                              ResourceLocation.parse(sprite.contents().name().getNamespace() + ":textures/" + sprite.contents().name().getPath() + ".png"),
                              offset * 0.01F % 1.0F,
                              offset * 0.01F % 1.0F
                           )
                        );
                        if (candleDataxxxx.height != 0 && candleDataxxxx.height <= 7) {
                           this.swirlLayer.wax[candleDataxxxx.height - 1]
                              .render(
                                 matrixStackIn,
                                 vertexConsumer2,
                                 combinedLightIn,
                                 OverlayTexture.NO_OVERLAY,
                                 HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 0.75F)
                              );
                        }
                     }
                  }
               } else {
                  float offset = ClientEvents.getClientTicksWithoutPartial() + (float)Minecraft.getInstance().getFrameTimeNs();
                  VertexConsumer vertexConsumer2 = bufferIn.getBuffer(
                     RenderType.energySwirl(candleDataxxxx.swirl.layer, offset * 0.01F % 1.0F, offset * 0.01F % 1.0F)
                  );
                  if (candleDataxxxx.height != 0 && candleDataxxxx.height <= 7) {
                     this.swirlLayer.wax[candleDataxxxx.height - 1]
                        .render(
                           matrixStackIn,
                           vertexConsumer2,
                           combinedLightIn,
                           OverlayTexture.NO_OVERLAY,
                           HexereiUtil.getColorValueAlpha(col[0], col[1], col[2], 0.75F)
                        );
                  }
               }

               matrixStackIn.popPose();
            }
         }
      }
   }

   private void translate(PoseStack poseStack, CandleData candleData, float partialTicks, Direction facing) {
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.translate(
         Mth.lerp(partialTicks, candleData.xO, candleData.x),
         Mth.lerp(partialTicks, candleData.yO, candleData.y),
         Mth.lerp(partialTicks, candleData.zO, candleData.z)
      );
      if (facing == Direction.EAST) {
         poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
      } else if (facing == Direction.SOUTH) {
         poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
      } else if (facing == Direction.WEST) {
         poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      }
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

   public static void renderCube(
      PoseStack poseStack,
      VertexConsumer vertexConsumer,
      float xSize,
      float ySize,
      float zSize,
      float texWidth,
      float texHeight,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0F, -6.25E-4F, 0.0F);
      poseStack.scale(0.065F, 0.065F, 0.065F);
      float xOffset = xSize / 2.0F;
      float zOffset = zSize / 2.0F;
      float uT0 = 0.0F;
      float uT1 = uT0 + xSize / texWidth;
      float vT0 = 0.0F;
      float vT1 = vT0 + xSize / texWidth;
      addVertex(vertexConsumer, poseStack, -xOffset, ySize, -zOffset, uT0, vT0, 0.0F, 1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, ySize, -zOffset, uT0, vT1, 0.0F, 1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, ySize, zOffset, uT1, vT1, 0.0F, 1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, ySize, zOffset, uT1, vT0, 0.0F, 1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      float uB0 = xSize / texWidth;
      float uB1 = uB0 + xSize / texWidth;
      float vB0 = 0.0F;
      float vB1 = vB0 + xSize / texWidth;
      addVertex(vertexConsumer, poseStack, -xOffset, 0.0F, zOffset, uB0, vB0, 0.0F, -1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, 0.0F, -zOffset, uB1, vB0, 0.0F, -1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, 0.0F, -zOffset, uB1, vB1, 0.0F, -1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, 0.0F, zOffset, uB0, vB1, 0.0F, -1.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      float uN0 = 0.0F;
      float uN1 = uN0 + ySize / texWidth;
      float vN0 = xSize / texWidth;
      float vN1 = xSize / texWidth + xSize / texWidth;
      addVertex(vertexConsumer, poseStack, -xOffset, 0.0F, -zOffset, uN1, vN1, 0.0F, 0.0F, -1.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, 0.0F, -zOffset, uN1, vN0, 0.0F, 0.0F, -1.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, ySize, -zOffset, uN0, vN0, 0.0F, 0.0F, -1.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, ySize, -zOffset, uN0, vN1, 0.0F, 0.0F, -1.0F, combinedLightIn, combinedOverlayIn);
      float uS0 = ySize / texWidth;
      float uS1 = uS0 + ySize / texWidth;
      float vS0 = xSize / texWidth;
      float vS1 = vS0 + xSize / texWidth;
      addVertex(vertexConsumer, poseStack, -xOffset, 0.0F, zOffset, uS1, vS0, 0.0F, 0.0F, 1.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, 0.0F, zOffset, uS1, vS1, 0.0F, 0.0F, 1.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, ySize, zOffset, uS0, vS1, 0.0F, 0.0F, 1.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, ySize, zOffset, uS0, vS0, 0.0F, 0.0F, 1.0F, combinedLightIn, combinedOverlayIn);
      float uW0 = 0.0F;
      float uW1 = uW0 + ySize / texWidth;
      float vW0 = xSize / texWidth + xSize / texWidth;
      float vW1 = vW0 + xSize / texWidth;
      addVertex(vertexConsumer, poseStack, -xOffset, 0.0F, -zOffset, uW1, vW0, -1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, 0.0F, zOffset, uW1, vW1, -1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, ySize, zOffset, uW0, vW1, -1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, -xOffset, ySize, -zOffset, uW0, vW0, -1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      float uE0 = ySize / texWidth;
      float uE1 = uE0 + ySize / texWidth;
      float vE0 = xSize / texWidth + xSize / texWidth;
      float vE1 = vE0 + xSize / texWidth;
      addVertex(vertexConsumer, poseStack, xOffset, 0.0F, -zOffset, uE1, vE1, 1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, 0.0F, zOffset, uE1, vE0, 1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, ySize, zOffset, uE0, vE0, 1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      addVertex(vertexConsumer, poseStack, xOffset, ySize, -zOffset, uE0, vE1, 1.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn);
      poseStack.popPose();
   }

   private static void addVertex(
      VertexConsumer vertexConsumer,
      PoseStack poseStack,
      float x,
      float y,
      float z,
      float u,
      float v,
      float nx,
      float ny,
      float nz,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      vertexConsumer.addVertex(poseStack.last().pose(), x, y, z)
         .setColor(1.0F, 1.0F, 1.0F, 1.0F)
         .setUv(u, v)
         .setOverlay(combinedOverlayIn)
         .setLight(combinedLightIn)
         .setNormal(poseStack.last(), nx, ny, nz);
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

   public static TextureAtlasSprite getFirstSprite(BlockState blockState) {
      Minecraft minecraft = Minecraft.getInstance();
      BakedModel model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(blockState);

      for (Direction direction : Direction.values()) {
         List<BakedQuad> quads = model.getQuads(blockState, direction, RandomSource.create());
         if (!quads.isEmpty()) {
            return quads.get(0).getSprite();
         }
      }

      List<BakedQuad> unculledQuads = model.getQuads(blockState, null, RandomSource.create());
      return !unculledQuads.isEmpty() ? unculledQuads.get(0).getSprite() : null;
   }

   public static NativeImage modifyTexture(NativeImage originalImage) {
      int width = originalImage.getWidth();
      int height = originalImage.getHeight();
      NativeImage newImage = new NativeImage(width, height, true);

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            int color = originalImage.getPixelRGBA(x, y);
            int invertedColor = ~color | color & 0xFF000000;
            newImage.setPixelRGBA(x, y, invertedColor);
         }
      }

      return newImage;
   }
}
