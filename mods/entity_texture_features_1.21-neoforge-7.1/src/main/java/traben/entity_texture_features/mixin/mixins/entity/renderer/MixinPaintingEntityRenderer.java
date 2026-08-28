/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.PaintingRenderer
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.decoration.Painting
 *  net.minecraft.world.entity.decoration.PaintingVariant
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.BlockAndTintGetter;
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

@Mixin(value={PaintingRenderer.class})
public abstract class MixinPaintingEntityRenderer
extends EntityRenderer<Painting> {
    @Unique
    private static final ResourceLocation etf$BACK_SPRITE_ID = ETFUtils2.res("textures/painting/back.png");

    @Unique
    private void uVertex(PoseStack.Pose matrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light) {
        this.vertex(matrix, vertexConsumer, x, y, u, v, z, normalX, normalY, normalZ, light);
    }

    @Shadow
    protected abstract void vertex(PoseStack.Pose var1, VertexConsumer var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, int var10, int var11);

    protected MixinPaintingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method={"render(Lnet/minecraft/world/entity/decoration/Painting;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void etf$getSprites(Painting paintingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
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
                matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f - f));
                PaintingVariant paintingVariant = (PaintingVariant)paintingEntity.getVariant().value();
                int width = paintingVariant.width();
                int height = paintingVariant.height();
                this.etf$renderETFPainting(matrixStack.last(), vertexConsumerProvider, paintingEntity, width, height, etf$Sprite, etf$BackSprite);
                matrixStack.popPose();
                super.render((Entity)paintingEntity, f, g, matrixStack, vertexConsumerProvider, i);
                ci.cancel();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Unique
    private void etf$renderETFPainting(PoseStack.Pose entry, MultiBufferSource vertexConsumerProvider, Painting entity, int width, int height, ETFSprite ETFPaintingSprite, ETFSprite ETFBackSprite) {
        ETFRenderContext.preventRenderLayerTextureModify();
        VertexConsumer vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entitySolid((ResourceLocation)ETFPaintingSprite.getSpriteVariant().atlasLocation()));
        this.etf$renderETFPaintingFront(entry, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getSpriteVariant(), false);
        VertexConsumer vertexConsumerBack = vertexConsumerProvider.getBuffer(RenderType.entitySolid((ResourceLocation)ETFBackSprite.getSpriteVariant().atlasLocation()));
        this.etf$renderETFPaintingBack(entry, vertexConsumerBack, entity, width, height, ETFBackSprite.getSpriteVariant(), false);
        if (ETFPaintingSprite.isEmissive()) {
            vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent((ResourceLocation)ETFPaintingSprite.getEmissive().atlasLocation()));
            this.etf$renderETFPaintingFront(entry, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getEmissive(), true);
        }
        if (ETFBackSprite.isEmissive()) {
            vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent((ResourceLocation)ETFBackSprite.getEmissive().atlasLocation()));
            this.etf$renderETFPaintingBack(entry, vertexConsumerFront, entity, width, height, ETFBackSprite.getEmissive(), true);
        }
        ETFRenderContext.allowRenderLayerTextureModify();
    }

    @Unique
    private void etf$renderETFPaintingFront(PoseStack.Pose entry, VertexConsumer vertexConsumerFront, Painting entity, int width, int height, TextureAtlasSprite paintingSprite, boolean emissive) {
        float f = (float)(-width) / 2.0f;
        float g = (float)(-height) / 2.0f;
        int u = width;
        int v = height;
        double d = 1.0 / (double)u;
        double e = 1.0 / (double)v;
        for (int w = 0; w < u; ++w) {
            for (int x = 0; x < v; ++x) {
                int light;
                float y = f + (float)(w + 1);
                float z = f + (float)w;
                float aa = g + (float)(x + 1);
                float ab = g + (float)x;
                if (emissive) {
                    light = 0xF000F2;
                } else {
                    float divider = 1.0f;
                    int ac = entity.getBlockX();
                    int ad = Mth.floor((double)(entity.getY() + (double)((aa + ab) / 2.0f / divider)));
                    int ae = entity.getBlockZ();
                    Direction direction = entity.getDirection();
                    if (direction == Direction.NORTH) {
                        ac = Mth.floor((double)(entity.getX() + (double)((y + z) / 2.0f / divider)));
                    }
                    if (direction == Direction.WEST) {
                        ae = Mth.floor((double)(entity.getZ() - (double)((y + z) / 2.0f / divider)));
                    }
                    if (direction == Direction.SOUTH) {
                        ac = Mth.floor((double)(entity.getX() - (double)((y + z) / 2.0f / divider)));
                    }
                    if (direction == Direction.EAST) {
                        ae = Mth.floor((double)(entity.getZ() + (double)((y + z) / 2.0f / divider)));
                    }
                    light = LevelRenderer.getLightColor((BlockAndTintGetter)entity.level(), (BlockPos)new BlockPos(ac, ad, ae));
                }
                float zConst = 0.03125f;
                float ag = paintingSprite.getU((float)(d * (double)(u - w)));
                float ah = paintingSprite.getU((float)(d * (double)(u - (w + 1))));
                float ai = paintingSprite.getV((float)(e * (double)(v - x)));
                float aj = paintingSprite.getV((float)(e * (double)(v - (x + 1))));
                this.uVertex(entry, vertexConsumerFront, y, ab, ah, ai, -zConst, 0, 0, -1, light);
                this.uVertex(entry, vertexConsumerFront, z, ab, ag, ai, -zConst, 0, 0, -1, light);
                this.uVertex(entry, vertexConsumerFront, z, aa, ag, aj, -zConst, 0, 0, -1, light);
                this.uVertex(entry, vertexConsumerFront, y, aa, ah, aj, -zConst, 0, 0, -1, light);
            }
        }
    }

    @Unique
    private void etf$renderETFPaintingBack(PoseStack.Pose entry, VertexConsumer vertexConsumerBack, Painting entity, int width, int height, TextureAtlasSprite backSprite, boolean emissive) {
        float f = (float)(-width) / 2.0f;
        float g = (float)(-height) / 2.0f;
        float i = backSprite.getU0();
        float j = backSprite.getU1();
        float k = backSprite.getV0();
        float l = backSprite.getV1();
        float m = backSprite.getU0();
        float n = backSprite.getU1();
        float o = backSprite.getV0();
        float p = backSprite.getV(0.0625f);
        float q = backSprite.getU0();
        float r = backSprite.getU(0.0625f);
        float s = backSprite.getV0();
        float t = backSprite.getV1();
        int u = width;
        int v = height;
        for (int w = 0; w < u; ++w) {
            for (int x = 0; x < v; ++x) {
                int light;
                float y = f + (float)(w + 1);
                float z = f + (float)w;
                float aa = g + (float)(x + 1);
                float ab = g + (float)x;
                if (emissive) {
                    light = 0xF000F2;
                } else {
                    float divider = 1.0f;
                    int ac = entity.getBlockX();
                    int ad = Mth.floor((double)(entity.getY() + (double)((aa + ab) / 2.0f / divider)));
                    int ae = entity.getBlockZ();
                    Direction direction = entity.getDirection();
                    if (direction == Direction.NORTH) {
                        ac = Mth.floor((double)(entity.getX() + (double)((y + z) / 2.0f / divider)));
                    }
                    if (direction == Direction.WEST) {
                        ae = Mth.floor((double)(entity.getZ() - (double)((y + z) / 2.0f / divider)));
                    }
                    if (direction == Direction.SOUTH) {
                        ac = Mth.floor((double)(entity.getX() - (double)((y + z) / 2.0f / divider)));
                    }
                    if (direction == Direction.EAST) {
                        ae = Mth.floor((double)(entity.getZ() + (double)((y + z) / 2.0f / divider)));
                    }
                    light = LevelRenderer.getLightColor((BlockAndTintGetter)entity.level(), (BlockPos)new BlockPos(ac, ad, ae));
                }
                float zConst = 0.03125f;
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

