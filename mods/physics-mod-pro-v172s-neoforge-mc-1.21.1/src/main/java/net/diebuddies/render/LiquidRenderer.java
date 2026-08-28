/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL20
 */
package net.diebuddies.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL20;

public class LiquidRenderer {
    private MainRenderer mainRenderer;
    private int mcEntityLocation = -1;
    private Matrix4d transformation = new Matrix4d();
    private Matrix4f localT = new Matrix4f();

    public LiquidRenderer(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
    }

    public void render(ClientLevel level, RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        PhysicsMod mod;
        PhysicsWorld physics;
        if (level != null && ((physics = (mod = PhysicsMod.getInstance((Level)level)).getPhysicsWorld()).getLiquids().size() > 0 || physics.getOceanWorld().getOceanMeshes().size() > 0)) {
            blockLayerIn.setupRenderState();
            Matrix4fStack matrixStackIn = RenderSystem.getModelViewStack();
            Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            matrixStackIn.pushMatrix();
            matrixStackIn.set((Matrix4fc)PhysicsMod.viewMatrix);
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.mainRenderer.setupShader(RenderSystem.getShader());
            VAO.storePreviouslyBoundState();
            if (StarterClient.optifabric && Optifine.areShadersEnabled()) {
                this.mcEntityLocation = 11;
            } else if (StarterClient.iris) {
                this.mcEntityLocation = GL20.glGetAttribLocation((int)RenderSystem.getShader().getId(), (CharSequence)"mc_Entity");
            }
            PerformanceTracker.startNoFlush("liquid_rendering");
            if (ConfigClient.cudaLiquids()) {
                this.mainRenderer.liquidDeferredRenderer.render(physics, level, matrixStackIn, view);
            } else {
                for (int i = 0; i < physics.getLiquids().size(); ++i) {
                    Liquid liquid = physics.getLiquids().get(i);
                    if (liquid.vao == null) continue;
                    this.renderLiquid(physics, level, matrixStackIn, view, liquid);
                }
            }
            PerformanceTracker.end("liquid_rendering");
            this.mainRenderer.oceanRenderer.render(physics, level, matrixStackIn, view);
            VAO.restorePreviouslyBoundState();
            BufferUploader.reset();
            RenderSystem.getShader().clear();
            matrixStackIn.popMatrix();
            RenderSystem.activeTexture((int)33984);
            RenderSystem.enableCull();
            blockLayerIn.clearRenderState();
            RenderSystem.applyModelViewMatrix();
            if (level.effects().constantAmbientLight()) {
                Lighting.setupNetherLevel();
            } else {
                Lighting.setupLevel();
            }
            this.setupAttribute(this.mcEntityLocation, 0.0f, 0.0f, 0.0f, 1.0f);
            if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
                Optifine.useWaterShader();
            }
        }
    }

    private void renderLiquid(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view, Liquid liquid) {
        this.setupAttribute(this.mcEntityLocation, liquid.materialID, liquid.renderType, -1.0f, -1.0f);
        RenderSystem.enableCull();
        this.transformation.set((Matrix4dc)liquid.transformation);
        this.transformation.m30(this.transformation.m30() - view.x);
        this.transformation.m31(this.transformation.m31() - view.y);
        this.transformation.m32(this.transformation.m32() - view.z);
        float scale = 1.0f / (float)liquid.gridSize;
        this.transformation.scale((double)scale);
        matrixStackIn.pushMatrix();
        this.localT.set((Matrix4dc)this.transformation);
        matrixStackIn.mul((Matrix4fc)this.localT);
        RenderSystem.applyModelViewMatrix();
        if (level.effects().constantAmbientLight()) {
            RenderSystem.shaderLightDirections[0] = MainRenderer.NETHER_DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.NETHER_DIFFUSE_LIGHT_1;
        } else {
            RenderSystem.shaderLightDirections[0] = MainRenderer.DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.DIFFUSE_LIGHT_1;
        }
        RenderSystem.setShaderTexture((int)0, (int)liquid.textureID);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)liquid.textureID);
        this.mainRenderer.setupPBRTextures();
        ShaderInstance shader = RenderSystem.getShader();
        RenderSystem.setupShaderLights((ShaderInstance)shader);
        if (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal()) {
            if (shader.LIGHT0_DIRECTION != null) {
                shader.LIGHT0_DIRECTION.upload();
            }
            if (shader.LIGHT1_DIRECTION != null) {
                shader.LIGHT1_DIRECTION.upload();
            }
        }
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setModelViewMatrix(RenderSystem.getModelViewMatrix());
        } else {
            shader.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
            shader.MODEL_VIEW_MATRIX.upload();
            if (StarterClient.iris) {
                Iris.setNormalMatrix(shader, RenderSystem.getModelViewMatrix());
            }
        }
        liquid.vao.render();
        matrixStackIn.popMatrix();
    }

    public void setupAttribute(int location, float v0, float v1, float v2, float v3) {
        if (location != -1) {
            GL20.glVertexAttrib4f((int)location, (float)v0, (float)v1, (float)v2, (float)v3);
        }
    }
}

