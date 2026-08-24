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
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
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
      if (level != null) {
         PhysicsMod mod = PhysicsMod.getInstance(level);
         PhysicsWorld physics = mod.getPhysicsWorld();
         if (physics.getLiquids().size() > 0 || physics.getOceanWorld().getOceanMeshes().size() > 0) {
            blockLayerIn.setupRenderState();
            Matrix4fStack matrixStackIn = RenderSystem.getModelViewStack();
            Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            matrixStackIn.pushMatrix();
            matrixStackIn.set(PhysicsMod.viewMatrix);
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.mainRenderer.setupShader(RenderSystem.getShader());
            VAO.storePreviouslyBoundState();
            if (StarterClient.optifabric && Optifine.areShadersEnabled()) {
               this.mcEntityLocation = 11;
            } else if (StarterClient.iris) {
               this.mcEntityLocation = GL20.glGetAttribLocation(RenderSystem.getShader().getId(), "mc_Entity");
            }

            PerformanceTracker.startNoFlush("liquid_rendering");
            if (ConfigClient.cudaLiquids()) {
               this.mainRenderer.liquidDeferredRenderer.render(physics, level, matrixStackIn, view);
            } else {
               for (int i = 0; i < physics.getLiquids().size(); i++) {
                  Liquid liquid = physics.getLiquids().get(i);
                  if (liquid.vao != null) {
                     this.renderLiquid(physics, level, matrixStackIn, view, liquid);
                  }
               }
            }

            PerformanceTracker.end("liquid_rendering");
            this.mainRenderer.oceanRenderer.render(physics, level, matrixStackIn, view);
            VAO.restorePreviouslyBoundState();
            BufferUploader.reset();
            RenderSystem.getShader().clear();
            matrixStackIn.popMatrix();
            RenderSystem.activeTexture(33984);
            RenderSystem.enableCull();
            blockLayerIn.clearRenderState();
            RenderSystem.applyModelViewMatrix();
            if (level.effects().constantAmbientLight()) {
               Lighting.setupNetherLevel();
            } else {
               Lighting.setupLevel();
            }

            this.setupAttribute(this.mcEntityLocation, 0.0F, 0.0F, 0.0F, 1.0F);
            if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
               Optifine.useWaterShader();
            }
         }
      }
   }

   private void renderLiquid(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view, Liquid liquid) {
      this.setupAttribute(this.mcEntityLocation, liquid.materialID, liquid.renderType, -1.0F, -1.0F);
      RenderSystem.enableCull();
      this.transformation.set(liquid.transformation);
      this.transformation.m30(this.transformation.m30() - view.x);
      this.transformation.m31(this.transformation.m31() - view.y);
      this.transformation.m32(this.transformation.m32() - view.z);
      float scale = 1.0F / liquid.gridSize;
      this.transformation.scale(scale);
      matrixStackIn.pushMatrix();
      this.localT.set(this.transformation);
      matrixStackIn.mul(this.localT);
      RenderSystem.applyModelViewMatrix();
      if (level.effects().constantAmbientLight()) {
         RenderSystem.shaderLightDirections[0] = MainRenderer.NETHER_DIFFUSE_LIGHT_0;
         RenderSystem.shaderLightDirections[1] = MainRenderer.NETHER_DIFFUSE_LIGHT_1;
      } else {
         RenderSystem.shaderLightDirections[0] = MainRenderer.DIFFUSE_LIGHT_0;
         RenderSystem.shaderLightDirections[1] = MainRenderer.DIFFUSE_LIGHT_1;
      }

      RenderSystem.setShaderTexture(0, liquid.textureID);
      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(liquid.textureID);
      this.mainRenderer.setupPBRTextures();
      ShaderInstance shader = RenderSystem.getShader();
      RenderSystem.setupShaderLights(shader);
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
         GL20.glVertexAttrib4f(location, v0, v1, v2, v3);
      }
   }
}
