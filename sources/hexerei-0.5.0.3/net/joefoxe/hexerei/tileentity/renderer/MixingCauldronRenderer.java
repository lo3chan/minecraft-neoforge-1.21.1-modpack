package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.awt.Color;
import java.util.Objects;
import java.util.Optional;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.Tags.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

public class MixingCauldronRenderer implements BlockEntityRenderer<MixingCauldronTile> {
   public static final float CORNERS = (float)MixingCauldron.SHAPE.min(Axis.X) + 0.1875F;
   public static final float MIN_Y = 0.25F;
   public static final float MAX_Y = 0.9375F;

   public AABB getRenderBoundingBox(MixingCauldronTile blockEntity) {
      return super.getRenderBoundingBox(blockEntity).inflate(5.0);
   }

   public void render(
      MixingCauldronTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos()).hasBlockEntity()
         && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof MixingCauldronTile) {
         boolean heated = false;
         BlockState heatSource = tileEntityIn.getLevel().getBlockState(tileEntityIn.getPos().below());
         if (heatSource.is(HexereiTags.Blocks.HEAT_SOURCES)
            && (!heatSource.hasProperty(BlockStateProperties.LIT) || (Boolean)heatSource.getValue(BlockStateProperties.LIT))) {
            heated = true;
         }

         float tickSpeed = ClientEvents.getClientTicks() / 2.0F;
         if (heated) {
            tickSpeed = (float)(tickSpeed * 3.0);
         }

         float craftPercent = 0.0F;
         if (Objects.requireNonNull(tileEntityIn.getLevel()).getBlockState(tileEntityIn.getPos()).hasBlockEntity()
            && tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof MixingCauldronTile) {
            craftPercent = tileEntityIn.craftDelay / 100.0F;
            float craftPercentOld = tileEntityIn.craftDelayOld / 100.0F;
            craftPercent = Mth.lerp(partialTicks, craftPercentOld, craftPercent);
            craftPercentOld = 0.0F;
            boolean flag = false;
            FluidStack fluidStack = tileEntityIn.getFluidInTank(0);
            if (tileEntityIn.renderedFluid != null) {
               fluidStack = tileEntityIn.renderedFluid;
               flag = true;
            }

            if (!fluidStack.isEmpty()) {
               craftPercentOld = Math.min(1.0F, (flag ? tileEntityIn.fluidRenderLevel : fluidStack.getAmount()) / tileEntityIn.getTankCapacity(0));
            }

            float dist = Math.abs(tileEntityIn.fluidRenderLevel - tileEntityIn.getFluidStack().getAmount()) / 1000.0F;
            tileEntityIn.fluidRenderLevel = HexereiUtil.moveTo(
               tileEntityIn.fluidRenderLevel, tileEntityIn.getFluidStack().getAmount(), (25.0F + 50.0F * dist) * partialTicks
            );
            this.renderBlock(
               matrixStackIn,
               bufferIn,
               combinedLightIn,
               combinedOverlayIn,
               ((Block)ModBlocks.MIXING_CAULDRON_DYE.get()).defaultBlockState(),
               null,
               tileEntityIn.getDyeColor()
            );
            float height = 0.25F + 0.6875F * craftPercentOld;

            for (int i = 0; i < 8; i++) {
               ItemStack item = tileEntityIn.getItemStackInSlot(i);
               if (!item.isEmpty()) {
                  matrixStackIn.pushPose();
                  matrixStackIn.translate(0.5, height + 0.00390625F, 0.5);
                  double itemRotationOffset = 0.8 * i + craftPercent * (20.0F * craftPercent);
                  if (craftPercentOld > 0.0F) {
                     matrixStackIn.translate(
                        0.0 + Math.sin(itemRotationOffset) / (3.5F + craftPercent * craftPercent * 10.0F),
                        Math.sin(3.141592653589793 * tickSpeed / 30.0 + i * 20) / 10.0 * 0.2,
                        0.0 + Math.cos(itemRotationOffset) / (3.5F + craftPercent * craftPercent * 10.0F)
                     );
                     matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float)(45 * i - 1.0F + 2.0 * Math.sin((tickSpeed + i * 20) / 40.0F))));
                     matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees((float)(-97.5 + 5.0 * Math.cos((tickSpeed + i * 22) / 40.0F))));
                     matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float)(-182.5 + 5.0 * Math.cos((tickSpeed + i * 24) / 40.0F))));
                     matrixStackIn.scale(1.0F - craftPercent * 0.5F, 1.0F - craftPercent * 0.5F, 1.0F - craftPercent * 0.5F);
                  } else {
                     matrixStackIn.translate(0.0 + Math.sin(itemRotationOffset) / 3.5, 0.0, 0.0 + Math.cos(itemRotationOffset) / 3.5);
                     matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45 * i));
                     matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(85.0F));
                     matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-2.5F));
                  }

                  matrixStackIn.scale(0.4F, 0.4F, 0.4F);
                  this.renderItem(item, tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
                  matrixStackIn.popPose();
               }
            }

            ItemStack item2 = new ItemStack(tileEntityIn.getItemInSlot(8));
            if (!item2.isEmpty()) {
               matrixStackIn.pushPose();
               matrixStackIn.translate(0.5, height + 0.00390625F, 0.5);
               if (craftPercentOld > 0.0F) {
                  matrixStackIn.translate(0.0, Math.sin(3.141592653589793 * tickSpeed / 60.0 + 20.0) / 10.0 * 0.2, 0.0);
                  matrixStackIn.mulPose(
                     com.mojang.math.Axis.YP
                        .rotationDegrees((float)(44.0 + 2.0 * Math.sin((tickSpeed + 20.0F) / 40.0F)) - craftPercent * craftPercent * 720.0F)
                  );
                  matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees((float)(82.5 + 5.0 * Math.cos((tickSpeed + 22.0F) / 40.0F))));
                  matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float)(-2.5 + 5.0 * Math.cos((tickSpeed + 24.0F) / 40.0F))));
               } else {
                  matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45.0F - craftPercent * craftPercent * 720.0F));
                  matrixStackIn.mulPose(com.mojang.math.Axis.XP.rotationDegrees(85.0F));
                  matrixStackIn.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-2.5F));
               }

               matrixStackIn.scale(0.4F, 0.4F, 0.4F);
               this.renderItem(item2, tileEntityIn.getLevel(), matrixStackIn, bufferIn, combinedLightIn);
               matrixStackIn.popPose();
            }

            if (tileEntityIn.getItemInSlot(9) == ModItems.BLOOD_SIGIL.get() && tileEntityIn.getItemInSlot(9).asItem() == ModItems.BLOOD_SIGIL.get()) {
               matrixStackIn.pushPose();
               this.renderBlock(matrixStackIn, bufferIn, combinedLightIn, ((Block)ModBlocks.BLOOD_SIGIL.get()).defaultBlockState());
               matrixStackIn.popPose();
            }

            if (bufferIn instanceof BufferSource bufferSource) {
               bufferSource.endBatch();
            }

            if (!fluidStack.isEmpty()) {
               matrixStackIn.pushPose();
               Color color2 = new Color(
                  BiomeColors.getAverageWaterColor(
                     tileEntityIn.getLevel(), new BlockPos(tileEntityIn.getPos().getX(), tileEntityIn.getPos().getY(), tileEntityIn.getPos().getZ())
                  )
               );
               int waterColor = HexereiUtil.getColorValue(color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F);
               if (fluidStack.getFluid().is(Fluids.GASEOUS)) {
                  renderFluid(matrixStackIn, bufferIn, fluidStack, craftPercentOld, 1.0F, combinedLightIn, tileEntityIn, waterColor);
               } else {
                  renderFluid(matrixStackIn, bufferIn, fluidStack, 1.0F, craftPercentOld, combinedLightIn, tileEntityIn, waterColor);
               }

               matrixStackIn.popPose();
            }
         }
      }
   }

   public static Optional<TextureAtlasSprite> getStillFluidSprite(FluidStack fluidStack) {
      Fluid fluid = fluidStack.getFluid();
      IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
      ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);
      return Optional.ofNullable(fluidStill)
         .map(f -> (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(f))
         .filter(s -> s.atlasLocation() != MissingTextureAtlasSprite.getLocation());
   }

   public static void renderFluidBox(
      FluidStack fluidStack,
      float xMin,
      float yMin,
      float zMin,
      float xMax,
      float yMax,
      float zMax,
      MultiBufferSource buffer,
      PoseStack matrixStack,
      int light,
      boolean renderBottom,
      int waterColor
   ) {
      Fluid fluid = fluidStack.getFluid();
      IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
      FluidType fluidAttributes = fluid.getFluidType();
      getStillFluidSprite(fluidStack).ifPresent(fluidTexture -> {
         VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentCull(fluidTexture.atlasLocation()));
         int color = clientFluid.getTintColor(fluidStack);
         int a = color >> 24 & 0xFF;
         int r = color >> 16 & 0xFF;
         int g = color >> 8 & 0xFF;
         int b = color >> 0 & 0xFF;
         if (FluidStack.isSameFluidSameComponents(fluidStack, new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1))) {
            color = a << 24 | waterColor;
         }

         int blockLightIn = light >> 4 & 15;
         int luminosity = Math.max(blockLightIn, fluidAttributes.getLightLevel(fluidStack));
         int lightF = light & 15728640 | luminosity << 4;
         matrixStack.pushPose();

         for (Direction side : Direction.values()) {
            if (side != Direction.DOWN || renderBottom) {
               boolean positive = side.getAxisDirection() == AxisDirection.POSITIVE;
               if (side.getAxis().isHorizontal()) {
                  if (side.getAxis() == Axis.X) {
                     renderStillTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, matrixStack, lightF, color, fluidTexture);
                  } else {
                     renderStillTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, matrixStack, lightF, color, fluidTexture);
                  }
               } else {
                  renderStillTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, matrixStack, lightF, color, fluidTexture);
               }
            }
         }

         matrixStack.popPose();
      });
   }

   public static void renderStillTiledFace(
      Direction dir,
      float left,
      float down,
      float right,
      float up,
      float depth,
      VertexConsumer builder,
      PoseStack ms,
      int light,
      int color,
      TextureAtlasSprite texture
   ) {
      renderTiledFace(dir, left, down, right, up, depth, builder, ms, light, color, texture, 0.0625F);
   }

   public static void renderFlowingTiledFace(
      Direction dir,
      float left,
      float down,
      float right,
      float up,
      float depth,
      VertexConsumer builder,
      PoseStack ms,
      int light,
      int color,
      TextureAtlasSprite texture
   ) {
      renderTiledFace(dir, left, down, right, up, depth, builder, ms, light, color, texture, 0.03125F);
   }

   public static void renderTiledFace(
      Direction dir,
      float left,
      float down,
      float right,
      float up,
      float depth,
      VertexConsumer builder,
      PoseStack ms,
      int light,
      int color,
      TextureAtlasSprite texture,
      float textureScale
   ) {
      boolean positive = dir.getAxisDirection() == AxisDirection.POSITIVE;
      boolean horizontal = dir.getAxis().isHorizontal();
      boolean x = dir.getAxis() == Axis.X;
      float shrink = texture.uvShrinkRatio() * 0.25F * textureScale;
      float centerU = texture.getU0() + (texture.getU1() - texture.getU0()) * 0.5F * textureScale;
      float centerV = texture.getV0() + (texture.getV1() - texture.getV0()) * 0.5F * textureScale;
      float x2 = 0.0F;
      float y2 = 0.0F;
      float x1 = left;

      while (x1 < right) {
         float f = Mth.floor(x1);
         x2 = Math.min(f + 1.0F, right);
         float u1;
         float u2;
         if (dir != Direction.NORTH && dir != Direction.EAST) {
            u1 = texture.getU((x1 - f) * 16.0F * textureScale);
            u2 = texture.getU((x2 - f) * 16.0F * textureScale);
         } else {
            f = Mth.ceil(x2);
            u1 = texture.getU((f - x2) * 16.0F * textureScale);
            u2 = texture.getU((f - x1) * 16.0F * textureScale);
         }

         u1 = Mth.lerp(shrink, u1, centerU);
         u2 = Mth.lerp(shrink, u2, centerU);
         float y1 = down;

         while (y1 < up) {
            f = Mth.floor(y1);
            y2 = Math.min(f + 1.0F, up);
            float v1;
            float v2;
            if (dir == Direction.UP) {
               v1 = texture.getV((y1 - f) * 16.0F * textureScale);
               v2 = texture.getV((y2 - f) * 16.0F * textureScale);
            } else {
               f = Mth.ceil(y2);
               v1 = texture.getV((f - y2) * 16.0F * textureScale);
               v2 = texture.getV((f - y1) * 16.0F * textureScale);
            }

            v1 = Mth.lerp(shrink, v1, centerV);
            v2 = Mth.lerp(shrink, v2, centerV);
            if (horizontal) {
               if (x) {
                  putVertex(builder, ms, depth, y2, positive ? x2 : x1, color, u1, v1, dir, light);
                  putVertex(builder, ms, depth, y1, positive ? x2 : x1, color, u1, v2, dir, light);
                  putVertex(builder, ms, depth, y1, positive ? x1 : x2, color, u2, v2, dir, light);
                  putVertex(builder, ms, depth, y2, positive ? x1 : x2, color, u2, v1, dir, light);
               } else {
                  putVertex(builder, ms, positive ? x1 : x2, y2, depth, color, u1, v1, dir, light);
                  putVertex(builder, ms, positive ? x1 : x2, y1, depth, color, u1, v2, dir, light);
                  putVertex(builder, ms, positive ? x2 : x1, y1, depth, color, u2, v2, dir, light);
                  putVertex(builder, ms, positive ? x2 : x1, y2, depth, color, u2, v1, dir, light);
               }
            } else {
               putVertex(builder, ms, x1, depth, positive ? y1 : y2, color, u1, v1, dir, light);
               putVertex(builder, ms, x1, depth, positive ? y2 : y1, color, u1, v2, dir, light);
               putVertex(builder, ms, x2, depth, positive ? y2 : y1, color, u2, v2, dir, light);
               putVertex(builder, ms, x2, depth, positive ? y1 : y2, color, u2, v1, dir, light);
            }

            y1 = y2;
         }

         x1 = x2;
      }
   }

   private static void putVertex(VertexConsumer builder, PoseStack ms, float x, float y, float z, int color, float u, float v, Direction face, int light) {
      Vec3i normal = face.getNormal();
      Pose peek = ms.last();
      int a = color >> 24 & 0xFF;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      builder.addVertex(peek.pose(), x, y, z)
         .setColor(r, g, b, a)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(light)
         .setNormal(peek, normal.getX(), normal.getY(), normal.getZ());
   }

   private static void renderFluid(
      PoseStack matrixStack,
      MultiBufferSource renderTypeBuffer,
      FluidStack fluidStack,
      float alpha,
      float heightPercentage,
      int combinedLight,
      MixingCauldronTile tileEntityIn,
      int waterColor
   ) {
      float from = 0.125F;
      float to = 0.875F;
      renderFluidBox(fluidStack, from, 0.25F, from, to, 0.25F + 0.6875F * heightPercentage, to, renderTypeBuffer, matrixStack, 15728880, false, waterColor);
   }

   public static void renderFluidGUI(
      PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, float heightPercentage, int combinedLight
   ) {
      ResourceLocation loc = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack);

      try {
         TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(loc);
         VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.entityTranslucentCull(sprite.atlasLocation()));
         int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
         alpha *= (color >> 24 & 0xFF) / 255.0F;
         float red = (color >> 16 & 0xFF) / 255.0F;
         float green = (color >> 8 & 0xFF) / 255.0F;
         float blue = (color & 0xFF) / 255.0F;
         renderQuads(matrixStack.last(), vertexBuilder, sprite, red, green, blue, alpha, heightPercentage, combinedLight);
      } catch (Exception var13) {
         System.out.println(fluidStack.getFluid());
         System.out.println(loc);
      }
   }

   public static void renderFluidBlockGUI(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, int combinedLight) {
      ResourceLocation loc = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack);
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(loc);
      VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.entityTranslucentCull(sprite.atlasLocation()));
      int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
      alpha *= (color >> 24 & 0xFF) / 255.0F;
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      renderQuadsBlock(matrixStack.last(), vertexBuilder, sprite, red, green, blue, alpha, combinedLight);
   }

   private static void renderQuads(
      Pose pose, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, float heightPercentage, int light
   ) {
      Matrix4f matrix = pose.pose();
      float height = 0.25F + 0.6875F * heightPercentage;
      float minU = sprite.getU(CORNERS);
      float maxU = sprite.getU(1.0F - CORNERS);
      float minV = sprite.getV(CORNERS);
      float maxV = sprite.getV(1.0F - CORNERS);
      vertexBuilder.addVertex(matrix, CORNERS, height, CORNERS)
         .setColor(r, g, b, alpha)
         .setUv(minU, minV)
         .setLight(15728880)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS, height, 1.0F - CORNERS)
         .setColor(r, g, b, alpha)
         .setUv(minU, maxV)
         .setLight(15728880)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS, height, 1.0F - CORNERS)
         .setColor(r, g, b, alpha)
         .setUv(maxU, maxV)
         .setLight(15728880)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS, height, CORNERS)
         .setColor(r, g, b, alpha)
         .setUv(maxU, minV)
         .setLight(15728880)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   private static void renderQuadsBlock(Pose pose, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, int light) {
      float height = 0.75F;
      float minU = sprite.getU(CORNERS);
      float maxU = sprite.getU(1.0F - CORNERS);
      float minV = sprite.getV(CORNERS);
      float maxV = sprite.getV(1.0F - CORNERS);
      Matrix4f matrix = pose.pose();
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, minV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, maxV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, maxV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, height, CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, minV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, minV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, -1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, maxV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, -1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, 0.0F, CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, maxV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, -1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, 0.0F, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, minV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, -1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, minV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 0.0F, -1.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, maxV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 0.0F, -1.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, 0.0F, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, maxV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 0.0F, -1.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, 0.0F, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, minV)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(pose, 0.0F, 0.0F, -1.0F);
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

   private void renderBlock(
      PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color
   ) {
      this.renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);
   }

   public void renderSingleBlock(
      BlockState p_110913_,
      PoseStack p_110914_,
      MultiBufferSource p_110915_,
      int p_110916_,
      int p_110917_,
      ModelData modelData,
      RenderType renderType,
      int color
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

               for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42L), modelData)) {
                  dispatcher.getModelRenderer()
                     .renderModel(
                        p_110914_.last(),
                        p_110915_.getBuffer(renderType != null ? renderType : RenderTypeHelper.getEntityRenderType(rt, false)),
                        p_110913_,
                        bakedmodel,
                        f,
                        f1,
                        f2,
                        p_110916_,
                        p_110917_,
                        modelData,
                        rt
                     );
               }
               break;
            case ENTITYBLOCK_ANIMATED:
               ItemStack stack = new ItemStack(p_110913_.getBlock());
               IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }
      }
   }
}
