/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4fStack
 *  org.joml.Vector3d
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.LinkedList;
import javax.annotation.Nullable;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.FBO;
import net.diebuddies.opengl.Mesh;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.Usage;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.physics.ocean.RippleParticle;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.render.shader.OceanRippleShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL32C;

public class OceanRippleRenderer {
    private MainRenderer mainRenderer;
    private static FBO rippleFBO;
    private static OceanRippleShader rippleShader;
    private static float[] puddlepos;
    private static float[] puddleposnew;
    private static int[] viewport;
    private double rippleRange = 32.0;
    private Matrix4d projectionMatrix;
    private Matrix4d viewMatrix;
    private Vector3d offsetCamera = new Vector3d(0.0, 0.0, 0.0);

    public OceanRippleRenderer(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
        this.projectionMatrix = new Matrix4d().ortho(-this.rippleRange, this.rippleRange, this.rippleRange, -this.rippleRange, -100.0, 100.0);
        this.viewMatrix = new Matrix4d().rotateX(Math.toRadians(90.0));
    }

    public void renderSmallWaves(PhysicsWorld physics, ProxyOceanLayer layer, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 cameraPos) {
        if (!ConfigClient.oceanRipples) {
            return;
        }
        VAO rippleVAO = layer.getRippleVAO();
        int rippleCount = layer.getRippleCount();
        if (rippleCount == 0 || rippleVAO == null) {
            return;
        }
        RenderSystem.activeTexture((int)33984);
        int boundBefore = GL32C.glGetInteger((int)32873);
        int drawFboBoundBefore = GL32C.glGetInteger((int)36006);
        int readFboBoundBefore = GL32C.glGetInteger((int)36010);
        int previousProgram = GL32C.glGetInteger((int)35725);
        int oldBlendEquationRGB = GL32C.glGetInteger((int)32777);
        GL32C.glGetIntegerv((int)2978, (int[])viewport);
        int resolution = ConfigClient.oceanPuddleResolutionQuality;
        if (rippleFBO == null) {
            rippleFBO = new FBO(resolution, resolution, false);
            rippleShader = new OceanRippleShader();
        } else if (resolution != rippleFBO.getTexture().getWidth()) {
            rippleFBO.destroy();
            rippleFBO = new FBO(resolution, resolution, false);
        }
        rippleShader.bind();
        rippleFBO.bind();
        rippleVAO.bind();
        RenderSystem.viewport((int)0, (int)0, (int)resolution, (int)resolution);
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        int textureID = Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.PUDDLE_TEXTURE).getId();
        RenderSystem.setShaderTexture((int)0, (int)textureID);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)textureID);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.blendEquation((int)32776);
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Vector3d rippleOffset = layer.getRippleOffset();
        this.offsetCamera.set(cameraPos.x - rippleOffset.x, cameraPos.y - rippleOffset.y, cameraPos.z - rippleOffset.z);
        OceanRippleShader shader = rippleShader;
        shader.bind();
        shader.setUniform1(shader.getUniformLocation("Sampler0"), 0);
        shader.uploadMatrix(shader.getUniformLocation("ModelViewMat"), this.viewMatrix);
        shader.uploadMatrix(shader.getUniformLocation("ProjMat"), this.projectionMatrix);
        shader.setUniform3(shader.getUniformLocation("RippleCameraPos"), (float)this.offsetCamera.x, (float)this.offsetCamera.y, (float)this.offsetCamera.z);
        double renderPercent = physics.getRenderPercent();
        shader.setUniform1(shader.getUniformLocation("RenderPercent"), (float)renderPercent);
        rippleVAO.renderInstanced(rippleCount);
        RenderSystem.viewport((int)viewport[0], (int)viewport[1], (int)viewport[2], (int)viewport[3]);
        GL32C.glBindTexture((int)3553, (int)boundBefore);
        GL32C.glBindFramebuffer((int)36009, (int)drawFboBoundBefore);
        GL32C.glBindFramebuffer((int)36008, (int)readFboBoundBefore);
        GL32C.glUseProgram((int)previousProgram);
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendEquation((int)oldBlendEquationRGB);
    }

    private VAO createRippleVAO() {
        int size = 6;
        Mesh openglMesh = new Mesh();
        this.mainRenderer.checkArrays(6);
        this.mainRenderer.mpos[0] = -0.5f;
        this.mainRenderer.mpos[1] = 0.0f;
        this.mainRenderer.mpos[2] = -0.5f;
        this.mainRenderer.muv[0] = 0.0f;
        this.mainRenderer.muv[1] = 0.0f;
        this.mainRenderer.mpos[3] = 0.5f;
        this.mainRenderer.mpos[4] = 0.0f;
        this.mainRenderer.mpos[5] = -0.5f;
        this.mainRenderer.muv[2] = 1.0f;
        this.mainRenderer.muv[3] = 0.0f;
        this.mainRenderer.mpos[6] = 0.5f;
        this.mainRenderer.mpos[7] = 0.0f;
        this.mainRenderer.mpos[8] = 0.5f;
        this.mainRenderer.muv[4] = 1.0f;
        this.mainRenderer.muv[5] = 1.0f;
        this.mainRenderer.mpos[9] = -0.5f;
        this.mainRenderer.mpos[10] = 0.0f;
        this.mainRenderer.mpos[11] = -0.5f;
        this.mainRenderer.muv[6] = 0.0f;
        this.mainRenderer.muv[7] = 0.0f;
        this.mainRenderer.mpos[12] = 0.5f;
        this.mainRenderer.mpos[13] = 0.0f;
        this.mainRenderer.mpos[14] = 0.5f;
        this.mainRenderer.muv[8] = 1.0f;
        this.mainRenderer.muv[9] = 1.0f;
        this.mainRenderer.mpos[15] = -0.5f;
        this.mainRenderer.mpos[16] = 0.0f;
        this.mainRenderer.mpos[17] = 0.5f;
        this.mainRenderer.muv[10] = 0.0f;
        this.mainRenderer.muv[11] = 1.0f;
        openglMesh.set(this.mainRenderer.mpos, Data.POSITION);
        openglMesh.set(this.mainRenderer.muv, Data.TEX_COORD);
        openglMesh.set(puddlepos, Data.PUDDLE_POS);
        openglMesh.set(puddleposnew, Data.PUDDLE_POS_NEW);
        openglMesh.setSize(Data.POSITION, size * 3);
        openglMesh.setSize(Data.TEX_COORD, size * 2);
        openglMesh.setSize(Data.INDEX, size);
        return openglMesh.constructVAO(Usage.DYNAMIC);
    }

    private void checkRippleArrays(int neededSize) {
        int size;
        boolean changed = false;
        for (size = puddlepos.length; neededSize > size; size *= 2) {
            changed = true;
        }
        if (changed) {
            puddlepos = new float[size];
            puddleposnew = new float[size];
        }
    }

    public void updateRippleInstances(OceanWorld oceanWorld, ProxyOceanLayer layer, Vec3 cameraPos) {
        LinkedList<RippleParticle> rippleParticles = layer.getRippleParticles();
        this.checkRippleArrays(rippleParticles.size() * 4);
        int count = 0;
        Vector3d rippleOffset = layer.getRippleOffset();
        this.offsetCamera.set(cameraPos.x - rippleOffset.x, cameraPos.y - rippleOffset.y, cameraPos.z - rippleOffset.z);
        for (RippleParticle particle : rippleParticles) {
            if (!this.prepareRippleInstances(particle, this.offsetCamera, count)) continue;
            ++count;
        }
        layer.setRippleCount(count);
        if (count == 0) {
            return;
        }
        VAO rippleVAO = layer.getRippleVAO();
        if (rippleVAO == null) {
            rippleVAO = this.createRippleVAO();
            layer.setRippleVAO(rippleVAO);
        }
        rippleVAO.bind();
        rippleVAO.updateAttribute(Data.PUDDLE_POS, puddlepos, count * 4);
        rippleVAO.updateAttribute(Data.PUDDLE_POS_NEW, puddleposnew, count * 4);
    }

    private boolean prepareRippleInstances(RippleParticle particle, Vector3d cameraPos, int offset) {
        int mulOffset = offset * 4;
        double minDistance = Math.min(this.rippleRange - Math.abs(cameraPos.x - particle.x), this.rippleRange - Math.abs(cameraPos.z - particle.z)) - ((double)particle.scale * 0.5 + 0.5);
        float totalAlpha = particle.alpha * (float)(minDistance = Math.max(0.0, Math.min(minDistance, 2.0)) * 0.5);
        if (totalAlpha < 1.0E-4f) {
            return false;
        }
        OceanRippleRenderer.puddlepos[mulOffset] = (float)particle.x;
        OceanRippleRenderer.puddlepos[mulOffset + 1] = (float)particle.y;
        OceanRippleRenderer.puddlepos[mulOffset + 2] = (float)particle.z;
        OceanRippleRenderer.puddlepos[mulOffset + 3] = totalAlpha;
        OceanRippleRenderer.puddleposnew[mulOffset] = (float)particle.xo;
        OceanRippleRenderer.puddleposnew[mulOffset + 1] = particle.state;
        OceanRippleRenderer.puddleposnew[mulOffset + 2] = (float)particle.zo;
        OceanRippleRenderer.puddleposnew[mulOffset + 3] = particle.scale;
        return true;
    }

    public Texture getRippleTexture(@Nullable ProxyOceanLayer layer) {
        if (ConfigClient.oceanRipples && rippleFBO != null && layer != null && layer.getRippleCount() > 0) {
            return rippleFBO.getTexture();
        }
        return PhysicsMod.blackTexture;
    }

    public double getRippleRange() {
        return this.rippleRange;
    }

    public static void destroy() {
        if (rippleFBO != null) {
            rippleFBO.destroy(false);
        }
        if (rippleShader != null) {
            rippleShader.destroy();
        }
    }

    static {
        puddlepos = new float[400];
        puddleposnew = new float[400];
        viewport = new int[4];
    }
}

