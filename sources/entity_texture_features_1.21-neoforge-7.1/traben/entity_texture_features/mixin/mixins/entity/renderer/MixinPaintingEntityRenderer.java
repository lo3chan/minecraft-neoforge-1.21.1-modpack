package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFSprite;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin({PaintingRenderer.class})
public abstract class MixinPaintingEntityRenderer extends EntityRenderer<Painting> {
   @Unique
   private static final ResourceLocation etf$BACK_SPRITE_ID = ETFUtils2.res("textures/painting/back.png");

   @Unique
   private void uVertex(
      Pose matrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light
   ) {
      this.vertex(matrix, vertexConsumer, x, y, u, v, z, normalX, normalY, normalZ, light);
   }

   @Shadow
   protected abstract void vertex(
      Pose var1, VertexConsumer var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, int var10, int var11
   );

   protected MixinPaintingEntityRenderer(Context ctx) {
      super(ctx);
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/decoration/Painting;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void etf$getSprites(
      Painting paintingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci
   ) {
      ETFEntityRenderState etfEntity = ETFEntityRenderState.forEntity((ETFEntity)paintingEntity);

      try {
         TextureAtlasSprite paintingSprite = Minecraft.getInstance().getPaintingTextures().get((PaintingVariant)paintingEntity.getVariant().value());
         TextureAtlasSprite backSprite = Minecraft.getInstance().getPaintingTextures().getBackSprite();
         ResourceLocation paintingId = paintingSprite.contents().name();
         String paintingFileName = paintingId.getPath();
         ResourceLocation paintingTexture = ETFUtils2.res(paintingId.getNamespace(), "textures/painting/" + paintingFileName + ".png");
         boolean aztec = "aztec".equals(paintingFileName);
         if (aztec) {
            ETFRenderContext.allowOnlyPropertiesRandom();
         }

         ETFTexture frontTexture = ETFManager.getInstance().getETFTextureVariant(paintingTexture, etfEntity);
         ETFSprite etf$Sprite = frontTexture.getPaintingSprite(paintingSprite, paintingTexture);
         if (aztec) {
            ETFRenderContext.allowAllRandom();
         }

         ETFTexture backTexture = ETFManager.getInstance().getETFTextureVariant(etf$BACK_SPRITE_ID, etfEntity);
         ETFSprite etf$BackSprite = backTexture.getPaintingSprite(backSprite, etf$BACK_SPRITE_ID);
         if (etf$Sprite.isETFAltered || etf$Sprite.isEmissive() || etf$BackSprite.isETFAltered || etf$BackSprite.isEmissive()) {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - f));
            PaintingVariant paintingVariant = (PaintingVariant)paintingEntity.getVariant().value();
            int width = paintingVariant.width();
            int height = paintingVariant.height();
            this.etf$renderETFPainting(matrixStack.last(), vertexConsumerProvider, paintingEntity, width, height, etf$Sprite, etf$BackSprite);
            matrixStack.popPose();
            super.render(paintingEntity, f, g, matrixStack, vertexConsumerProvider, i);
            ci.cancel();
         }
      } catch (Exception var22) {
      }
   }

   @Unique
   private void etf$renderETFPainting(
      Pose entry, MultiBufferSource vertexConsumerProvider, Painting entity, int width, int height, ETFSprite ETFPaintingSprite, ETFSprite ETFBackSprite
   ) {
      ETFRenderContext.preventRenderLayerTextureModify();
      VertexConsumer vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entitySolid(ETFPaintingSprite.getSpriteVariant().atlasLocation()));
      this.etf$renderETFPaintingFront(entry, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getSpriteVariant(), false);
      VertexConsumer vertexConsumerBack = vertexConsumerProvider.getBuffer(RenderType.entitySolid(ETFBackSprite.getSpriteVariant().atlasLocation()));
      this.etf$renderETFPaintingBack(entry, vertexConsumerBack, entity, width, height, ETFBackSprite.getSpriteVariant(), false);
      if (ETFPaintingSprite.isEmissive()) {
         vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(ETFPaintingSprite.getEmissive().atlasLocation()));
         this.etf$renderETFPaintingFront(entry, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getEmissive(), true);
      }

      if (ETFBackSprite.isEmissive()) {
         vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(ETFBackSprite.getEmissive().atlasLocation()));
         this.etf$renderETFPaintingBack(entry, vertexConsumerFront, entity, width, height, ETFBackSprite.getEmissive(), true);
      }

      ETFRenderContext.allowRenderLayerTextureModify();
   }

   @Unique
   private void etf$renderETFPaintingFront(
      Pose entry, VertexConsumer vertexConsumerFront, Painting entity, int width, int height, TextureAtlasSprite paintingSprite, boolean emissive
   ) {
      float f = -width / 2.0F;
      float g = -height / 2.0F;
      int u = width;
      int v = height;
      double d = 1.0 / width;
      double e = 1.0 / height;

      for (int w = 0; w < u; w++) {
         for (int x = 0; x < v; x++) {
            float y = f + (w + 1);
            float z = f + w;
            float aa = g + (x + 1);
            float ab = g + x;
            int light;
            if (emissive) {
               light = 15728882;
            } else {
               float divider = 1.0F;
               int ac = entity.getBlockX();
               int ad = Mth.floor(entity.getY() + (aa + ab) / 2.0F / divider);
               int ae = entity.getBlockZ();
               Direction direction = entity.getDirection();
               if (direction == Direction.NORTH) {
                  ac = Mth.floor(entity.getX() + (y + z) / 2.0F / divider);
               }

               if (direction == Direction.WEST) {
                  ae = Mth.floor(entity.getZ() - (y + z) / 2.0F / divider);
               }

               if (direction == Direction.SOUTH) {
                  ac = Mth.floor(entity.getX() - (y + z) / 2.0F / divider);
               }

               if (direction == Direction.EAST) {
                  ae = Mth.floor(entity.getZ() + (y + z) / 2.0F / divider);
               }

               light = LevelRenderer.getLightColor(entity.level(), new BlockPos(ac, ad, ae));
            }

            float zConst = 0.03125F;
            float ag = paintingSprite.getU((float)(d * (u - w)));
            float ah = paintingSprite.getU((float)(d * (u - (w + 1))));
            float ai = paintingSprite.getV((float)(e * (v - x)));
            float aj = paintingSprite.getV((float)(e * (v - (x + 1))));
            this.uVertex(entry, vertexConsumerFront, y, ab, ah, ai, -zConst, 0, 0, -1, light);
            this.uVertex(entry, vertexConsumerFront, z, ab, ag, ai, -zConst, 0, 0, -1, light);
            this.uVertex(entry, vertexConsumerFront, z, aa, ag, aj, -zConst, 0, 0, -1, light);
            this.uVertex(entry, vertexConsumerFront, y, aa, ah, aj, -zConst, 0, 0, -1, light);
         }
      }
   }

   @Unique
   private void etf$renderETFPaintingBack(
      Pose entry, VertexConsumer vertexConsumerBack, Painting entity, int width, int height, TextureAtlasSprite backSprite, boolean emissive
   ) {
      float f = -width / 2.0F;
      float g = -height / 2.0F;
      float i = backSprite.getU0();
      float j = backSprite.getU1();
      float k = backSprite.getV0();
      float l = backSprite.getV1();
      float m = backSprite.getU0();
      float n = backSprite.getU1();
      float o = backSprite.getV0();
      float p = backSprite.getV(0.0625F);
      float q = backSprite.getU0();
      float r = backSprite.getU(0.0625F);
      float s = backSprite.getV0();
      float t = backSprite.getV1();
      int u = width;
      int v = height;

      for (int w = 0; w < u; w++) {
         for (int x = 0; x < v; x++) {
            float y = f + (w + 1);
            float z = f + w;
            float aa = g + (x + 1);
            float ab = g + x;
            int light;
            if (emissive) {
               light = 15728882;
            } else {
               float divider = 1.0F;
               int ac = entity.getBlockX();
               int ad = Mth.floor(entity.getY() + (aa + ab) / 2.0F / divider);
               int ae = entity.getBlockZ();
               Direction direction = entity.getDirection();
               if (direction == Direction.NORTH) {
                  ac = Mth.floor(entity.getX() + (y + z) / 2.0F / divider);
               }

               if (direction == Direction.WEST) {
                  ae = Mth.floor(entity.getZ() - (y + z) / 2.0F / divider);
               }

               if (direction == Direction.SOUTH) {
                  ac = Mth.floor(entity.getX() - (y + z) / 2.0F / divider);
               }

               if (direction == Direction.EAST) {
                  ae = Mth.floor(entity.getZ() + (y + z) / 2.0F / divider);
               }

               light = LevelRenderer.getLightColor(entity.level(), new BlockPos(ac, ad, ae));
            }

            float zConst = 0.03125F;
            this.uVertex(entry, vertexConsumerBack, y, aa, j, k, zConst, 0, 0, 1, light);
            this.uVertex(entry, vertexConsumerBack, z, aa, i, k, zConst, 0, 0, 1, light);
            this.uVertex(entry, vertexConsumerBack, z, ab, i, l, zConst, 0, 0, 1, light);
            this.uVertex(entry, vertexConsumerBack, y, ab, j, l, zConst, 0, 0, 1, light);
            this.uVertex(entry, vertexConsumerBack, y, aa, m, o, -zConst, 0, 1, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, aa, n, o, -zConst, 0, 1, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, aa, n, p, zConst, 0, 1, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, aa, m, p, zConst, 0, 1, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, ab, m, o, zConst, 0, -1, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, ab, n, o, zConst, 0, -1, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, ab, n, p, -zConst, 0, -1, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, ab, m, p, -zConst, 0, -1, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, aa, r, s, zConst, -1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, ab, r, t, zConst, -1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, ab, q, t, -zConst, -1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, y, aa, q, s, -zConst, -1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, aa, r, s, -zConst, 1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, ab, r, t, -zConst, 1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, ab, q, t, zConst, 1, 0, 0, light);
            this.uVertex(entry, vertexConsumerBack, z, aa, q, s, zConst, 1, 0, 0, light);
         }
      }
   }
}
