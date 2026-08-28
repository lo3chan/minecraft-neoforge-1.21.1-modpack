/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL32C;

public class ClothRenderer {
    private MainRenderer mainRenderer;
    private double lastRenderPercent;
    private PoseStack tmpStack = new PoseStack();

    public ClothRenderer(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
    }

    public void render(ClientLevel level, RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        int i;
        if (level == null) {
            return;
        }
        if (StarterClient.optifabric && PhysicsMod.optifineClothCompat.size() > 0) {
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.disableCull();
            for (i = 0; i < PhysicsMod.optifineClothCompat.size(); ++i) {
                PhysicsMod.optifineClothCompat.get(i).renderSlow((Level)level);
            }
            PhysicsMod.optifineClothCompat.clear();
            RenderSystem.enableBlend();
        }
        PerformanceTracker.startNoFlush("cloth_rendering");
        blockLayerIn.setupRenderState();
        this.mainRenderer.bindProperShader();
        RenderSystem.setProjectionMatrix((Matrix4f)projectionMatrix, (VertexSorting)RenderSystem.getVertexSorting());
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.activeTexture((int)33984);
        this.mainRenderer.resetColor();
        this.mainRenderer.setupShader(RenderSystem.getShader());
        VAO.storePreviouslyBoundState();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL32C.glVertexAttribI2ui((int)Data.OVERLAY.getAttribute(), (int)0, (int)10);
        GL32C.glVertexAttrib4f((int)Data.COLOR.getAttribute(), (float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (StarterClient.optifabric) {
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_OPTIFINE.getAttribute(), (float)0.0f, (float)0.0f);
        } else {
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_SHADER.getAttribute(), (float)0.0f, (float)0.0f);
        }
        for (i = 0; i < PhysicsMod.clothRenderFast.size(); ++i) {
            ClothRenderCommand renderCommand = PhysicsMod.clothRenderFast.get(i);
            this.renderFast(level, renderCommand);
        }
        PhysicsMod.clothRenderFast.clear();
        VAO.restorePreviouslyBoundState();
        RenderSystem.getShader().clear();
        RenderSystem.activeTexture((int)33984);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableCull();
        blockLayerIn.clearRenderState();
        RenderSystem.applyModelViewMatrix();
        BufferUploader.reset();
        PerformanceTracker.end("cloth_rendering");
    }

    private void renderFast(ClientLevel level, ClothRenderCommand renderCommand) {
        GL32C.glVertexAttribI2ui((int)Data.LIGHT.getAttribute(), (int)(renderCommand.brightness & 0xF0), (int)(renderCommand.brightness >> 16 & 0xF0));
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double renderPercent = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        if (Minecraft.getInstance().isPaused()) {
            renderPercent = this.lastRenderPercent;
        } else {
            this.lastRenderPercent = renderPercent;
        }
        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        ShaderInstance shader = RenderSystem.getShader();
        matrixStack.pushMatrix();
        int glID = renderCommand.textureID;
        RenderSystem.activeTexture((int)33984);
        RenderSystem.setShaderTexture((int)0, (int)glID);
        RenderSystem.bindTexture((int)glID);
        LivingEntity entity = renderCommand.entity;
        double px = Mth.lerp((double)renderPercent, (double)entity.xOld, (double)entity.getX());
        double py = Mth.lerp((double)renderPercent, (double)entity.yOld, (double)entity.getY());
        double pz = Mth.lerp((double)renderPercent, (double)entity.zOld, (double)entity.getZ());
        matrixStack.translation((float)(-view.x + px), (float)(-view.y + py), (float)(-view.z + pz));
        if (ConfigClient.areOceanPhysicsEnabled()) {
            OceanWorld oceanWorld = PhysicsMod.getInstance((Level)level).getPhysicsWorld().getOceanWorld();
            float yRot = Mth.lerp((float)((float)renderPercent), (float)entity.yRotO, (float)entity.getYRot());
            oceanWorld.computeEntityOffset((Matrix4f)matrixStack, null, (Level)level, (Entity)entity, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, yRot, (float)renderPercent);
        }
        renderCommand.modelPart.loadPose(renderCommand.modelPose);
        this.tmpStack.last().pose().set((Matrix4fc)matrixStack);
        ModelPartConstraint.entityTransformation(this.tmpStack, entity, (float)renderPercent);
        ModelPartConstraint.modelPartTransformation(renderCommand.modelPart, this.tmpStack);
        Matrix4f transformation = this.tmpStack.last().pose();
        this.mainRenderer.setupLighting(transformation, shader, level, true);
        PhysicsMod.viewMatrix.mul((Matrix4fc)transformation, transformation);
        this.mainRenderer.setupModelViewMatrix(shader, transformation, null, true);
        this.mainRenderer.setupPBRTextures();
        this.mainRenderer.setupShaderUniforms(shader);
        if (!renderCommand.onlyRenderPlayer) {
            if (ConfigClient.clothSmoothShading) {
                renderCommand.cloth.vao.render();
            } else {
                renderCommand.cloth.vaoFlatShaded.render();
            }
        }
        if (renderCommand.cloth.playerVAO != null && entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer)entity;
            glID = Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().texture()).getId();
            RenderSystem.setShaderTexture((int)0, (int)glID);
            RenderSystem.bindTexture((int)glID);
            this.mainRenderer.setupPBRTextures();
            renderCommand.cloth.playerVAO.render();
        }
        matrixStack.popMatrix();
    }
}

