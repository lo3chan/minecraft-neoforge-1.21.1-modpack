/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.tr7zw.entityculling.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.NMSCullingHelper;
import dev.tr7zw.entityculling.access.EntityRendererInter;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class WorldRendererMixin {
    private EntityRenderDispatcher entityCulling$entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    private List<Runnable> lateRenders = new ArrayList<Runnable>();
    private double aabbExpansion = 0.5;

    @Inject(at={@At(value="HEAD")}, method={"renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"}, cancellable=true)
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo info) {
        EntityRenderer entityRenderer;
        EntityRenderer entityRenderer2;
        if (EntityCullingModBase.instance.config.skipEntityCulling) {
            return;
        }
        Cullable cullable = (Cullable)entity;
        if (!cullable.isForcedVisible() && cullable.isCulled() && !NMSCullingHelper.ignoresCulling(entity) && (entityRenderer2 = this.entityCulling$entityRenderDispatcher.getRenderer(entity)) instanceof EntityRenderer && (entityRenderer = entityRenderer2) instanceof EntityRendererInter) {
            EntityRendererInter entityRendererInter = (EntityRendererInter)entityRenderer;
            if (EntityCullingModBase.instance.config.renderNametagsThroughWalls && matrices != null && vertexConsumers != null && entityRendererInter.shadowShouldShowName(entity)) {
                double x = Mth.lerp((double)tickDelta, (double)entity.xOld, (double)entity.getX()) - cameraX;
                double y = Mth.lerp((double)tickDelta, (double)entity.yOld, (double)entity.getY()) - cameraY;
                double z = Mth.lerp((double)tickDelta, (double)entity.zOld, (double)entity.getZ()) - cameraZ;
                Vec3 vec3d = NMSCullingHelper.getRenderOffset(entityRenderer, entity, tickDelta);
                double d = x + vec3d.x;
                double e = y + vec3d.y;
                double f = z + vec3d.z;
                matrices.pushPose();
                matrices.translate(d, e, f);
                entityRendererInter.shadowRenderNameTag(entity, entity.getDisplayName(), matrices, vertexConsumers, this.entityCulling$entityRenderDispatcher.getPackedLightCoords(entity, tickDelta), tickDelta);
                matrices.popPose();
            }
            ++EntityCullingModBase.instance.skippedEntities;
            info.cancel();
            return;
        }
        ++EntityCullingModBase.instance.renderedEntities;
        cullable.setOutOfCamera(false);
    }
}

