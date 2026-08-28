/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.TheEndPortalRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.entity.TheEndPortalBlockEntity
 *  org.joml.Matrix3f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.joml.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TheEndPortalRenderer.class})
public class MixinTheEndPortalRenderer {
    @Unique
    private static final float RED = 0.075f;
    @Unique
    private static final float GREEN = 0.15f;
    @Unique
    private static final float BLUE = 0.2f;

    @Shadow
    protected float getOffsetUp() {
        return 0.75f;
    }

    @Shadow
    protected float getOffsetDown() {
        return 0.375f;
    }

    @Inject(method={"render(Lnet/minecraft/world/level/block/entity/TheEndPortalBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void iris$onRender(TheEndPortalBlockEntity entity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay, CallbackInfo ci) {
        if (Iris.getCurrentPack().isEmpty()) {
            return;
        }
        ci.cancel();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid((ResourceLocation)TheEndPortalRenderer.END_PORTAL_LOCATION));
        PoseStack.Pose pose = poseStack.last();
        Matrix3f normal = poseStack.last().normal();
        float progress = SystemTimeUniforms.TIMER.getFrameTimeCounter() * 0.01f % 1.0f;
        float topHeight = this.getOffsetUp();
        float bottomHeight = this.getOffsetDown();
        this.quad(entity, vertexConsumer, pose, normal, Direction.UP, progress, overlay, light, 0.0f, topHeight, 1.0f, 1.0f, topHeight, 1.0f, 1.0f, topHeight, 0.0f, 0.0f, topHeight, 0.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.DOWN, progress, overlay, light, 0.0f, bottomHeight, 1.0f, 0.0f, bottomHeight, 0.0f, 1.0f, bottomHeight, 0.0f, 1.0f, bottomHeight, 1.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.NORTH, progress, overlay, light, 0.0f, topHeight, 0.0f, 1.0f, topHeight, 0.0f, 1.0f, bottomHeight, 0.0f, 0.0f, bottomHeight, 0.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.WEST, progress, overlay, light, 0.0f, topHeight, 1.0f, 0.0f, topHeight, 0.0f, 0.0f, bottomHeight, 0.0f, 0.0f, bottomHeight, 1.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.SOUTH, progress, overlay, light, 0.0f, topHeight, 1.0f, 0.0f, bottomHeight, 1.0f, 1.0f, bottomHeight, 1.0f, 1.0f, topHeight, 1.0f);
        this.quad(entity, vertexConsumer, pose, normal, Direction.EAST, progress, overlay, light, 1.0f, topHeight, 1.0f, 1.0f, bottomHeight, 1.0f, 1.0f, bottomHeight, 0.0f, 1.0f, topHeight, 0.0f);
    }

    @Unique
    private void quad(TheEndPortalBlockEntity entity, VertexConsumer vertexConsumer, PoseStack.Pose pose, Matrix3f normal, Direction direction, float progress, int overlay, int light, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        if (!entity.shouldRenderFace(direction)) {
            return;
        }
        float nx = direction.getStepX();
        float ny = direction.getStepY();
        float nz = direction.getStepZ();
        vertexConsumer.addVertex(pose, x1, y1, z1).setColor(0.075f, 0.15f, 0.2f, 1.0f).setUv(0.0f + progress, 0.0f + progress).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vertexConsumer.addVertex(pose, x2, y2, z2).setColor(0.075f, 0.15f, 0.2f, 1.0f).setUv(0.0f + progress, 0.2f + progress).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vertexConsumer.addVertex(pose, x3, y3, z3).setColor(0.075f, 0.15f, 0.2f, 1.0f).setUv(0.2f + progress, 0.2f + progress).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        vertexConsumer.addVertex(pose, x4, y4, z4).setColor(0.075f, 0.15f, 0.2f, 1.0f).setUv(0.2f + progress, 0.0f + progress).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
    }
}

