package net.diebuddies.render;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.FBO;
import net.diebuddies.opengl.Mesh;
import net.diebuddies.opengl.Shader;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.Usage;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.physics.ocean.RippleParticle;
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
   private static float[] puddlepos = new float[400];
   private static float[] puddleposnew = new float[400];
   private static int[] viewport = new int[4];
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
      if (ConfigClient.oceanRipples) {
         VAO rippleVAO = layer.getRippleVAO();
         int rippleCount = layer.getRippleCount();
         if (rippleCount != 0 && rippleVAO != null) {
            RenderSystem.activeTexture(33984);
            int boundBefore = GL32C.glGetInteger(32873);
            int drawFboBoundBefore = GL32C.glGetInteger(36006);
            int readFboBoundBefore = GL32C.glGetInteger(36010);
            int previousProgram = GL32C.glGetInteger(35725);
            int oldBlendEquationRGB = GL32C.glGetInteger(32777);
            GL32C.glGetIntegerv(2978, viewport);
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
            RenderSystem.viewport(0, 0, resolution, resolution);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.clear(16384, false);
            int textureID = Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.PUDDLE_TEXTURE).getId();
            RenderSystem.setShaderTexture(0, textureID);
            RenderSystem.activeTexture(33984);
            RenderSystem.bindTexture(textureID);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.blendEquation(32776);
            RenderSystem.blendFuncSeparate(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
            Vector3d rippleOffset = layer.getRippleOffset();
            this.offsetCamera.set(cameraPos.x - rippleOffset.x, cameraPos.y - rippleOffset.y, cameraPos.z - rippleOffset.z);
            Shader shader = rippleShader;
            shader.bind();
            shader.setUniform1(shader.getUniformLocation("Sampler0"), 0);
            shader.uploadMatrix(shader.getUniformLocation("ModelViewMat"), this.viewMatrix);
            shader.uploadMatrix(shader.getUniformLocation("ProjMat"), this.projectionMatrix);
            shader.setUniform3(shader.getUniformLocation("RippleCameraPos"), (float)this.offsetCamera.x, (float)this.offsetCamera.y, (float)this.offsetCamera.z);
            double renderPercent = physics.getRenderPercent();
            shader.setUniform1(shader.getUniformLocation("RenderPercent"), (float)renderPercent);
            rippleVAO.renderInstanced(rippleCount);
            RenderSystem.viewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            GL32C.glBindTexture(3553, boundBefore);
            GL32C.glBindFramebuffer(36009, drawFboBoundBefore);
            GL32C.glBindFramebuffer(36008, readFboBoundBefore);
            GL32C.glUseProgram(previousProgram);
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendEquation(oldBlendEquationRGB);
         }
      }
   }

   private VAO createRippleVAO() {
      int size = 6;
      Mesh openglMesh = new Mesh();
      this.mainRenderer.checkArrays(6);
      this.mainRenderer.mpos[0] = -0.5F;
      this.mainRenderer.mpos[1] = 0.0F;
      this.mainRenderer.mpos[2] = -0.5F;
      this.mainRenderer.muv[0] = 0.0F;
      this.mainRenderer.muv[1] = 0.0F;
      this.mainRenderer.mpos[3] = 0.5F;
      this.mainRenderer.mpos[4] = 0.0F;
      this.mainRenderer.mpos[5] = -0.5F;
      this.mainRenderer.muv[2] = 1.0F;
      this.mainRenderer.muv[3] = 0.0F;
      this.mainRenderer.mpos[6] = 0.5F;
      this.mainRenderer.mpos[7] = 0.0F;
      this.mainRenderer.mpos[8] = 0.5F;
      this.mainRenderer.muv[4] = 1.0F;
      this.mainRenderer.muv[5] = 1.0F;
      this.mainRenderer.mpos[9] = -0.5F;
      this.mainRenderer.mpos[10] = 0.0F;
      this.mainRenderer.mpos[11] = -0.5F;
      this.mainRenderer.muv[6] = 0.0F;
      this.mainRenderer.muv[7] = 0.0F;
      this.mainRenderer.mpos[12] = 0.5F;
      this.mainRenderer.mpos[13] = 0.0F;
      this.mainRenderer.mpos[14] = 0.5F;
      this.mainRenderer.muv[8] = 1.0F;
      this.mainRenderer.muv[9] = 1.0F;
      this.mainRenderer.mpos[15] = -0.5F;
      this.mainRenderer.mpos[16] = 0.0F;
      this.mainRenderer.mpos[17] = 0.5F;
      this.mainRenderer.muv[10] = 0.0F;
      this.mainRenderer.muv[11] = 1.0F;
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
      boolean changed = false;

      int size;
      for (size = puddlepos.length; neededSize > size; changed = true) {
         size *= 2;
      }

      if (changed) {
         puddlepos = new float[size];
         puddleposnew = new float[size];
      }
   }

   public void updateRippleInstances(OceanWorld oceanWorld, ProxyOceanLayer layer, Vec3 cameraPos) {
      List<RippleParticle> rippleParticles = layer.getRippleParticles();
      this.checkRippleArrays(rippleParticles.size() * 4);
      int count = 0;
      Vector3d rippleOffset = layer.getRippleOffset();
      this.offsetCamera.set(cameraPos.x - rippleOffset.x, cameraPos.y - rippleOffset.y, cameraPos.z - rippleOffset.z);

      for (RippleParticle particle : rippleParticles) {
         if (this.prepareRippleInstances(particle, this.offsetCamera, count)) {
            count++;
         }
      }

      layer.setRippleCount(count);
      if (count != 0) {
         VAO rippleVAO = layer.getRippleVAO();
         if (rippleVAO == null) {
            layer.setRippleVAO(rippleVAO = this.createRippleVAO());
         }

         rippleVAO.bind();
         rippleVAO.updateAttribute(Data.PUDDLE_POS, puddlepos, count * 4);
         rippleVAO.updateAttribute(Data.PUDDLE_POS_NEW, puddleposnew, count * 4);
      }
   }

   private boolean prepareRippleInstances(RippleParticle particle, Vector3d cameraPos, int offset) {
      int mulOffset = offset * 4;
      double minDistance = Math.min(this.rippleRange - Math.abs(cameraPos.x - particle.x), this.rippleRange - Math.abs(cameraPos.z - particle.z))
         - (particle.scale * 0.5 + 0.5);
      minDistance = Math.max(0.0, Math.min(minDistance, 2.0)) * 0.5;
      float totalAlpha = particle.alpha * (float)minDistance;
      if (totalAlpha < 1.0E-4F) {
         return false;
      } else {
         puddlepos[mulOffset] = (float)particle.x;
         puddlepos[mulOffset + 1] = (float)particle.y;
         puddlepos[mulOffset + 2] = (float)particle.z;
         puddlepos[mulOffset + 3] = totalAlpha;
         puddleposnew[mulOffset] = (float)particle.xo;
         puddleposnew[mulOffset + 1] = particle.state;
         puddleposnew[mulOffset + 2] = (float)particle.zo;
         puddleposnew[mulOffset + 3] = particle.scale;
         return true;
      }
   }

   public Texture getRippleTexture(@Nullable ProxyOceanLayer layer) {
      return ConfigClient.oceanRipples && rippleFBO != null && layer != null && layer.getRippleCount() > 0 ? rippleFBO.getTexture() : PhysicsMod.blackTexture;
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
}
