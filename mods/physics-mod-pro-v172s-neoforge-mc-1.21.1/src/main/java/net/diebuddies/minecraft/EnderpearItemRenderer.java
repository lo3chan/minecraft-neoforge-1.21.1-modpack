/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.ItemSupplier
 *  net.minecraft.world.item.ItemDisplayContext
 */
package net.diebuddies.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.RenderHelper;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

public class EnderpearItemRenderer<T extends Entity>
extends EntityRenderer<T> {
    private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25f;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public EnderpearItemRenderer(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = f;
        this.fullBright = bl;
    }

    public EnderpearItemRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, false);
    }

    public int getBlockLightLevel(T entity, BlockPos blockPos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, blockPos);
    }

    public void render(T entity, float animationProgress, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        if (((Entity)entity).tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
            if (ConfigClient.enderpearlModel != 2) {
                RenderHelper.renderMesh(entity, tickDelta, multiBufferSource, this.entityRenderDispatcher, PhysicsMod.ENDERPEARL_TEXTURE, PhysicsMod.enderpearlMesh.get(ConfigClient.enderpearlModel), poseStack, light, OverlayTexture.NO_OVERLAY, ConfigClient.enderpearlShade);
            } else {
                poseStack.pushPose();
                poseStack.scale(this.scale, this.scale, this.scale);
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                this.itemRenderer.renderStatic(((ItemSupplier)entity).getItem(), ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, entity.level(), entity.getId());
                poseStack.popPose();
            }
            super.render(entity, animationProgress, tickDelta, poseStack, multiBufferSource, light);
        }
    }

    public ResourceLocation getTextureLocation(Entity entity) {
        if (ConfigClient.enderpearlModel != 2) {
            return PhysicsMod.ENDERPEARL_TEXTURE;
        }
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

