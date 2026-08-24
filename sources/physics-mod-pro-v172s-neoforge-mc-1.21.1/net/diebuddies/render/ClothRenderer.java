package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL32C;

public class ClothRenderer {
   private MainRenderer mainRenderer;
   private double lastRenderPercent;
   private PoseStack tmpStack = new PoseStack();

   public ClothRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void render(ClientLevel level, RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
      if (level != null) {
         if (StarterClient.optifabric && PhysicsMod.optifineClothCompat.size() > 0) {
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.disableCull();

            for (int i = 0; i < PhysicsMod.optifineClothCompat.size(); i++) {
               PhysicsMod.optifineClothCompat.get(i).renderSlow(level);
            }

            PhysicsMod.optifineClothCompat.clear();
            RenderSystem.enableBlend();
         }

         PerformanceTracker.startNoFlush("cloth_rendering");
         blockLayerIn.setupRenderState();
         this.mainRenderer.bindProperShader();
         RenderSystem.setProjectionMatrix(projectionMatrix, RenderSystem.getVertexSorting());
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
         RenderSystem.activeTexture(33984);
         this.mainRenderer.resetColor();
         this.mainRenderer.setupShader(RenderSystem.getShader());
         VAO.storePreviouslyBoundState();
         RenderSystem.disableCull();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL32C.glVertexAttribI2ui(Data.OVERLAY.getAttribute(), 0, 10);
         GL32C.glVertexAttrib4f(Data.COLOR.getAttribute(), 1.0F, 1.0F, 1.0F, 1.0F);
         if (StarterClient.optifabric) {
            GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), 0.0F, 0.0F);
         } else {
            GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_SHADER.getAttribute(), 0.0F, 0.0F);
         }

         for (int i = 0; i < PhysicsMod.clothRenderFast.size(); i++) {
            ClothRenderCommand renderCommand = PhysicsMod.clothRenderFast.get(i);
            this.renderFast(level, renderCommand);
         }

         PhysicsMod.clothRenderFast.clear();
         VAO.restorePreviouslyBoundState();
         RenderSystem.getShader().clear();
         RenderSystem.activeTexture(33984);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableCull();
         blockLayerIn.clearRenderState();
         RenderSystem.applyModelViewMatrix();
         BufferUploader.reset();
         PerformanceTracker.end("cloth_rendering");
      }
   }

   private void renderFast(ClientLevel level, ClothRenderCommand renderCommand) {
      GL32C.glVertexAttribI2ui(Data.LIGHT.getAttribute(), renderCommand.brightness & 240, renderCommand.brightness >> 16 & 240);
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
      RenderSystem.activeTexture(33984);
      RenderSystem.setShaderTexture(0, glID);
      RenderSystem.bindTexture(glID);
      LivingEntity entity = renderCommand.entity;
      double px = Mth.lerp(renderPercent, entity.xOld, entity.getX());
      double py = Mth.lerp(renderPercent, entity.yOld, entity.getY());
      double pz = Mth.lerp(renderPercent, entity.zOld, entity.getZ());
      matrixStack.translation((float)(-view.x + px), (float)(-view.y + py), (float)(-view.z + pz));
      if (ConfigClient.areOceanPhysicsEnabled()) {
         OceanWorld oceanWorld = PhysicsMod.getInstance(level).getPhysicsWorld().getOceanWorld();
         float yRot = Mth.lerp((float)renderPercent, entity.yRotO, entity.getYRot());
         oceanWorld.computeEntityOffset(matrixStack, null, level, entity, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, yRot, (float)renderPercent);
      }

      renderCommand.modelPart.loadPose(renderCommand.modelPose);
      this.tmpStack.last().pose().set(matrixStack);
      ModelPartConstraint.entityTransformation(this.tmpStack, entity, (float)renderPercent);
      ModelPartConstraint.modelPartTransformation(renderCommand.modelPart, this.tmpStack);
      Matrix4f transformation = this.tmpStack.last().pose();
      this.mainRenderer.setupLighting(transformation, shader, level, true);
      PhysicsMod.viewMatrix.mul(transformation, transformation);
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

      if (renderCommand.cloth.playerVAO != null && entity instanceof AbstractClientPlayer player) {
         glID = Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().texture()).getId();
         RenderSystem.setShaderTexture(0, glID);
         RenderSystem.bindTexture(glID);
         this.mainRenderer.setupPBRTextures();
         renderCommand.cloth.playerVAO.render();
      }

      matrixStack.popMatrix();
   }
}
