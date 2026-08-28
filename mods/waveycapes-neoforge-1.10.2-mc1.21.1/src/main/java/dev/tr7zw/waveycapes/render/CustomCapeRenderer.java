/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.tr7zw.transition.mc.MathUtil
 *  dev.tr7zw.transition.mc.VertexConsumerUtil
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.transition.mc.VertexConsumerUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import dev.tr7zw.waveycapes.versionless.util.Vector4;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class CustomCapeRenderer {
    private static final int PART_COUNT = 16;
    private final ModelPart[] customCape = NMSUtil.buildCape(64, 64, x -> 0, y -> y);
    private static final float CAPE_WIDTH = 0.625f;
    private static final float CAPE_HEIGHT = 1.0f;
    private static final float CAPE_DEPTH = 0.0625f;

    public void render(PlayerWrapper capeRenderInfo, CapeRenderer renderer, VertexConsumer vertexConsumer, PoseStack poseStack, int packedLight, float delta) {
        if (ModBase.config.capeStyle == CapeStyle.SMOOTH && renderer.vanillaUvValues()) {
            this.renderSmoothCape(poseStack, vertexConsumer, capeRenderInfo, delta, packedLight);
        } else {
            ModelPart[] parts = this.customCape;
            for (int part = 0; part < 16; ++part) {
                ModelPart model = parts[part];
                this.modifyPoseStack(poseStack, capeRenderInfo, delta, part);
                renderer.render(capeRenderInfo, part, model, poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }

    private void renderSmoothCape(PoseStack poseStack, VertexConsumer bufferBuilder, PlayerWrapper capeRenderInfo, float delta, int light) {
        int part;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float alpha = SupportManager.getAlphaSupplier().get().floatValue();
        Matrix4f[] positionMatrices = new Matrix4f[16];
        Vector3[] frontNormalVecs = new Vector3[16];
        Vector3[] backNormalVecs = new Vector3[16];
        for (part = 0; part < 16; ++part) {
            this.modifyPoseStack(poseStack, capeRenderInfo, delta, part);
            positionMatrices[part] = new Matrix4f((Matrix4fc)poseStack.last().pose());
            frontNormalVecs[part] = CustomCapeRenderer.getNormalVec(positionMatrices[Math.max(part - 1, 0)], positionMatrices[Math.max(part - 1, 0)], positionMatrices[part], new Vector3(0.3125f, (float)part * 0.0625f, -0.0625f), new Vector3(-0.3125f, (float)part * 0.0625f, -0.0625f), new Vector3(0.3125f, (float)(part + 1) * 0.0625f, -0.0625f), light == 0xF000F0);
            backNormalVecs[part] = CustomCapeRenderer.getNormalVec(positionMatrices[Math.max(part - 1, 0)], positionMatrices[Math.max(part - 1, 0)], positionMatrices[part], new Vector3(0.3125f, (float)(part + 1) * 0.0625f, 0.0f), new Vector3(-0.3125f, (float)(part + 1) * 0.0625f, 0.0f), new Vector3(0.3125f, (float)part * 0.0625f, 0.0f), light == 0xF000F0);
            poseStack.popPose();
        }
        for (part = 0; part < 16; ++part) {
            Vector3 normalVec;
            float maxV;
            float minV;
            float maxU;
            float minU;
            if (part == 0) {
                minU = 0.015625f;
                maxU = 0.171875f;
                minV = 0.0f;
                maxV = 0.03125f;
                normalVec = CustomCapeRenderer.getNormalVec(positionMatrices[0], positionMatrices[0], positionMatrices[0], new Vector3(0.3125f, 0.0f, 0.0f), new Vector3(-0.3125f, 0.0f, 0.0f), new Vector3(0.3125f, 0.0f, 0.0625f), light == 0xF000F0);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[0], (float)0.3125f, (float)0.0f, (float)0.0f, (float)maxU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[0], (float)-0.3125f, (float)0.0f, (float)0.0f, (float)minU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[0], (float)-0.3125f, (float)0.0f, (float)-0.0625f, (float)minU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[0], (float)0.3125f, (float)0.0f, (float)-0.0625f, (float)maxU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            }
            if (part == 15) {
                minU = 0.171875f;
                maxU = 0.328125f;
                minV = 0.0f;
                maxV = 0.03125f;
                normalVec = CustomCapeRenderer.getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[part], new Vector3(0.3125f, 1.0f, -0.0625f), new Vector3(-0.3125f, 1.0f, -0.0625f), new Vector3(0.3125f, 1.0f, 0.0f), light == 0xF000F0);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)0.3125f, (float)1.0f, (float)-0.0625f, (float)maxU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)-0.3125f, (float)1.0f, (float)-0.0625f, (float)minU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)-0.3125f, (float)1.0f, (float)0.0f, (float)minU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
                VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)0.3125f, (float)1.0f, (float)0.0f, (float)maxU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            }
            minU = 0.0f;
            maxU = 0.015625f;
            minV = 0.03125f * (float)(part + 1);
            maxV = minV + 0.03125f;
            normalVec = CustomCapeRenderer.getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[Math.max(part - 1, 0)], new Vector3(-0.3125f, (float)(part + 1) * 0.0625f, 0.0f), new Vector3(-0.3125f, (float)(part + 1) * 0.0625f, -0.0625f), new Vector3(-0.3125f, (float)part * 0.0625f, 0.0f), light == 0xF000F0);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)-0.3125f, (float)((float)(part + 1) * 0.0625f), (float)0.0f, (float)minU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)-0.3125f, (float)((float)(part + 1) * 0.0625f), (float)-0.0625f, (float)maxU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)-0.3125f, (float)((float)part * 0.0625f), (float)-0.0625f, (float)maxU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)-0.3125f, (float)((float)part * 0.0625f), (float)0.0f, (float)minU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            minU = 0.171875f;
            maxU = 0.1875f;
            normalVec = CustomCapeRenderer.getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[Math.max(part - 1, 0)], new Vector3(0.3125f, (float)(part + 1) * 0.0625f, -0.0625f), new Vector3(0.3125f, (float)(part + 1) * 0.0625f, 0.0f), new Vector3(0.3125f, (float)part * 0.0625f, -0.0625f), light == 0xF000F0);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)0.3125f, (float)((float)(part + 1) * 0.0625f), (float)-0.0625f, (float)minU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)0.3125f, (float)((float)(part + 1) * 0.0625f), (float)0.0f, (float)maxU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)0.3125f, (float)((float)part * 0.0625f), (float)0.0f, (float)maxU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)0.3125f, (float)((float)part * 0.0625f), (float)-0.0625f, (float)minU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVec.x, (float)normalVec.y, (float)normalVec.z, (float)alpha);
            minU = 0.015625f;
            maxU = 0.171875f;
            Vector3 normalVecTop = frontNormalVecs[part].clone().add(frontNormalVecs[Math.max(part - 1, 0)]).div(2.0f);
            Vector3 normalVecBottom = frontNormalVecs[part].clone().add(frontNormalVecs[Math.min(part + 1, 15)]).div(2.0f);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)0.3125f, (float)((float)part * 0.0625f), (float)-0.0625f, (float)maxU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecTop.x, (float)normalVecTop.y, (float)normalVecTop.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)-0.3125f, (float)((float)part * 0.0625f), (float)-0.0625f, (float)minU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecTop.x, (float)normalVecTop.y, (float)normalVecTop.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)-0.3125f, (float)((float)(part + 1) * 0.0625f), (float)-0.0625f, (float)minU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecBottom.x, (float)normalVecBottom.y, (float)normalVecBottom.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)0.3125f, (float)((float)(part + 1) * 0.0625f), (float)-0.0625f, (float)maxU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecBottom.x, (float)normalVecBottom.y, (float)normalVecBottom.z, (float)alpha);
            minU = 0.1875f;
            maxU = 0.34375f;
            normalVecTop = backNormalVecs[part].clone().add(backNormalVecs[Math.max(part - 1, 0)]).div(2.0f);
            normalVecBottom = backNormalVecs[part].clone().add(backNormalVecs[Math.min(part + 1, 15)]).div(2.0f);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)0.3125f, (float)((float)part * 0.0625f), (float)0.0f, (float)minU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecTop.x, (float)normalVecTop.y, (float)normalVecTop.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[Math.max(part - 1, 0)], (float)-0.3125f, (float)((float)part * 0.0625f), (float)0.0f, (float)maxU, (float)minV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecTop.x, (float)normalVecTop.y, (float)normalVecTop.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)-0.3125f, (float)((float)(part + 1) * 0.0625f), (float)0.0f, (float)maxU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecBottom.x, (float)normalVecBottom.y, (float)normalVecBottom.z, (float)alpha);
            VertexConsumerUtil.addVertex((VertexConsumer)bufferBuilder, (Matrix4f)positionMatrices[part], (float)0.3125f, (float)((float)(part + 1) * 0.0625f), (float)0.0f, (float)minU, (float)maxV, (int)OverlayTexture.NO_OVERLAY, (int)light, (float)normalVecBottom.x, (float)normalVecBottom.y, (float)normalVecBottom.z, (float)alpha);
        }
    }

    private void modifyPoseStack(PoseStack poseStack, PlayerWrapper capeRenderInfo, float h, int part) {
        if (WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            this.modifyPoseStackSimulation(poseStack, capeRenderInfo, h, part);
            return;
        }
        this.modifyPoseStackVanilla(poseStack, (AbstractClientPlayer)capeRenderInfo.getEntity(), h, part);
    }

    private void modifyPoseStackSimulation(PoseStack poseStack, PlayerWrapper capeRenderInfo, float delta, int part) {
        Player entity = capeRenderInfo.getEntity();
        BasicSimulation simulation = ((CapeHolder)entity).getSimulation();
        if (simulation == null) {
            poseStack.pushPose();
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.125);
        float x = simulation.getPoints().get(part).getLerpX(delta) - simulation.getPoints().get(0).getLerpX(delta);
        if (x > 0.0f) {
            x = 0.0f;
        }
        float y = simulation.getPoints().get(0).getLerpY(delta) - (float)part - simulation.getPoints().get(part).getLerpY(delta);
        float z = simulation.getPoints().get(0).getLerpZ(delta) - simulation.getPoints().get(part).getLerpZ(delta);
        float sidewaysRotationOffset = 0.0f;
        float partRotation = this.getRotation(delta, part, simulation);
        float height = 0.0f;
        float naturalWindSwing = this.getNatrualWindSwing(part, entity.isUnderWater());
        poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0f + height + naturalWindSwing));
        poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0f));
        poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0f - sidewaysRotationOffset / 2.0f));
        poseStack.translate(-z / 16.0f, y / 16.0f, x / 16.0f);
        poseStack.translate(0.0, 0.03, -0.03);
        poseStack.translate(0.0f, (float)part * 1.0f / 16.0f, (float)(part * 0 / 16));
        poseStack.mulPose(MathUtil.XP.rotationDegrees(-partRotation));
        poseStack.translate(0.0f, (float)(-part) * 1.0f / 16.0f, (float)(-part * 0 / 16));
        poseStack.translate(0.0, -0.03, 0.03);
    }

    private float getRotation(float delta, int part, BasicSimulation simulation) {
        if (part == 15) {
            return this.getRotation(delta, part - 1, simulation);
        }
        return (float)this.getAngle(simulation.getPoints().get(part).getLerpedPos(delta), simulation.getPoints().get(part + 1).getLerpedPos(delta));
    }

    private double getAngle(Vector3 a, Vector3 b) {
        Vector3 angle = b.subtract(a);
        return Math.toDegrees(Math.atan2(angle.x, angle.y)) + 180.0;
    }

    private void modifyPoseStackVanilla(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h, int part) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.125);
        double d = Mth.lerp((double)h, (double)abstractClientPlayer.xCloakO, (double)abstractClientPlayer.xCloak) - Mth.lerp((double)h, (double)abstractClientPlayer.xo, (double)abstractClientPlayer.getX());
        double e = Mth.lerp((double)h, (double)abstractClientPlayer.yCloakO, (double)abstractClientPlayer.yCloak) - Mth.lerp((double)h, (double)abstractClientPlayer.yo, (double)abstractClientPlayer.getY());
        double m = Mth.lerp((double)h, (double)abstractClientPlayer.zCloakO, (double)abstractClientPlayer.zCloak) - Mth.lerp((double)h, (double)abstractClientPlayer.zo, (double)abstractClientPlayer.getZ());
        float n = abstractClientPlayer.yBodyRotO + abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO;
        double o = Mth.sin((float)(n * ((float)Math.PI / 180)));
        double p = -Mth.cos((float)(n * ((float)Math.PI / 180)));
        float height = (float)e * 10.0f;
        height = Mth.clamp((float)height, (float)-6.0f, (float)32.0f);
        float swing = (float)(d * o + m * p) * CustomCapeRenderer.easeOutSine(0.0625f * (float)part) * 100.0f;
        swing = Mth.clamp((float)swing, (float)0.0f, (float)(150.0f * CustomCapeRenderer.easeOutSine(0.0625f * (float)part)));
        float sidewaysRotationOffset = (float)(d * p - m * o) * 100.0f;
        sidewaysRotationOffset = Mth.clamp((float)sidewaysRotationOffset, (float)-20.0f, (float)20.0f);
        float t = Mth.lerp((float)h, (float)abstractClientPlayer.oBob, (float)abstractClientPlayer.bob);
        float naturalWindSwing = this.getNatrualWindSwing(part, abstractClientPlayer.isUnderWater());
        poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0f + swing / 2.0f + (height += Mth.sin((float)(Mth.lerp((float)h, (float)abstractClientPlayer.walkDistO, (float)abstractClientPlayer.walkDist) * 6.0f)) * 32.0f * t) + naturalWindSwing));
        poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0f));
        poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0f - sidewaysRotationOffset / 2.0f));
    }

    private static float easeOutSine(float x) {
        return Mth.sin((float)((float)((double)x * Math.PI / 2.0)));
    }

    private float getNatrualWindSwing(int part, boolean underwater) {
        long highlightedPart = System.currentTimeMillis() / (long)(underwater ? 9 : 3) % 360L;
        float relativePart = (float)(part + 1) / 16.0f;
        if (WaveyCapesBase.config.windMode == WindMode.WAVES) {
            return (float)(Math.sin(Math.toRadians(relativePart * 360.0f - (float)highlightedPart)) * 3.0);
        }
        return 0.0f;
    }

    private static Vector3 getNormalVec(Matrix4f matrix1, Matrix4f matrix2, Matrix4f matrix3, Vector3 vector1, Vector3 vector2, Vector3 vector3, boolean inverse) {
        Vector3 vector1Transformed = CustomCapeRenderer.transform(matrix1, new Vector4(vector1.x, vector1.y, vector1.z, 1.0f)).toVec3();
        Vector3 vector2Transformed = CustomCapeRenderer.transform(matrix2, new Vector4(vector2.x, vector2.y, vector2.z, 1.0f)).toVec3();
        Vector3 vector3Transformed = CustomCapeRenderer.transform(matrix3, new Vector4(vector3.x, vector3.y, vector3.z, 1.0f)).toVec3();
        vector2Transformed.subtract(vector1Transformed);
        vector3Transformed.subtract(vector1Transformed);
        vector2Transformed.cross(vector3Transformed);
        vector2Transformed.normalize();
        return inverse ? vector2Transformed.mul(-1.0f) : vector2Transformed;
    }

    private static Vector4 transform(Matrix4f matrix, Vector4 vector) {
        Vector4f vector4f = matrix.transform(new Vector4f(vector.x, vector.y, vector.z, vector.w));
        return new Vector4(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }
}

