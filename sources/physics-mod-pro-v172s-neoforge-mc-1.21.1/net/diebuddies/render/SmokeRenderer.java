package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Pack;
import net.diebuddies.opengl.SaveTexture;
import net.diebuddies.opengl.Shader;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.Usage;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.smoke.ParticleInfo;
import net.diebuddies.physics.smoke.SmokeDomain;
import net.diebuddies.physics.smoke.SmokeShader;
import net.diebuddies.physics.smoke.SmokeShadowTransformer;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL32C;

public class SmokeRenderer {
   private MainRenderer mainRenderer;
   private Matrix4f invProjectionMatrix = new Matrix4f();
   private Vector3f tmpColor = new Vector3f();
   private MutableBlockPos blockPos = new MutableBlockPos();
   public static SmokeShader smokeShader;
   public static SmokeShader smokeShadowShader;
   public static Texture smokeDepthCopy;
   public static VAO smokeVAO;
   private float[] smokepos = new float[400];
   private float[] smokeposnew = new float[400];
   private byte[] smokelight = new byte[400];
   private int count;
   private SmokeShadowTransformer shadowTransformer;
   private boolean needsSmokeUpdate = false;

   public SmokeRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.shadowTransformer = ConfigClient.smokeShadowTransformer;
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack rotationModelStack, Vec3 cameraPos) {
      PerformanceTracker.startNoFlush("smoke_rendering");
      if (physics.getSmokeDomain().getSmokeUpdateCallback() == null) {
         physics.getSmokeDomain().setSmokeUpdateCallback(domain -> this.needsSmokeUpdate = true);
      }

      if (this.needsSmokeUpdate) {
         this.updateSmokeInstances(physics.getSmokeDomain(), cameraPos);
         this.needsSmokeUpdate = false;
      }

      boolean isShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
      if (this.count > 0 && (!isShadowPass || isShadowPass && this.shadowTransformer != SmokeShadowTransformer.DISABLED)) {
         smokeDepthCopy = SaveTexture.copyFramebufferDepthTexture(smokeDepthCopy);
         smokeVAO.bind();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int textureID = Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.SMOKE_TEXTURE).getId();
         RenderSystem.setShaderTexture(0, textureID);
         RenderSystem.activeTexture(33984);
         RenderSystem.bindTexture(textureID);
         RenderSystem.activeTexture(33986);
         RenderSystem.bindTexture(RenderSystem.getShaderTexture(2));
         RenderSystem.activeTexture(33987);
         RenderSystem.bindTexture(smokeDepthCopy.getID());
         RenderSystem.activeTexture(33984);
         RenderSystem.disableCull();
         RenderSystem.applyModelViewMatrix();
         int previousProgram = GL32C.glGetInteger(35725);
         if (smokeShader == null) {
            smokeShader = new SmokeShader("");
            smokeShadowShader = new SmokeShader((String)this.shadowTransformer.getTransformer().get());
         }

         if (this.shadowTransformer != ConfigClient.smokeShadowTransformer) {
            this.shadowTransformer = ConfigClient.smokeShadowTransformer;
            smokeShadowShader.destroy();
            smokeShadowShader = new SmokeShader((String)this.shadowTransformer.getTransformer().get());
         }

         Shader shader = isShadowPass ? smokeShadowShader : smokeShader;
         shader.bind();
         shader.setUniform1(shader.getUniformLocation("Sampler0"), 0);
         shader.setUniform1(shader.getUniformLocation("Sampler1"), 1);
         shader.setUniform1(shader.getUniformLocation("Sampler2"), 2);
         shader.setUniform1(shader.getUniformLocation("DepthTexture"), 3);
         shader.setUniform1(shader.getUniformLocation("SmokeDensity"), ConfigClient.smokeDensity);
         this.tmpColor.set(ConfigClient.smokeColorRed, ConfigClient.smokeColorGreen, ConfigClient.smokeColorBlue);
         shader.setUniform3(shader.getUniformLocation("SmokeColor"), this.tmpColor);
         this.tmpColor.set(ConfigClient.smokeDenseColorRed, ConfigClient.smokeDenseColorGreen, ConfigClient.smokeDenseColorBlue);
         shader.setUniform3(shader.getUniformLocation("SmokeDenseColor"), this.tmpColor);
         shader.uploadMatrix(shader.getUniformLocation("ModelViewMat"), RenderSystem.getModelViewMatrix());
         shader.uploadMatrix(shader.getUniformLocation("ProjMat"), RenderSystem.getProjectionMatrix());
         this.invProjectionMatrix.set(RenderSystem.getProjectionMatrix());
         this.invProjectionMatrix.invert();
         shader.uploadMatrix(shader.getUniformLocation("InvProjMat"), this.invProjectionMatrix);
         shader.setUniform(shader.getUniformLocation("ColorModulator"), RenderSystem.getShaderColor());
         shader.setUniform1(shader.getUniformLocation("FogStart"), RenderSystem.getShaderFogStart());
         shader.setUniform1(shader.getUniformLocation("FogEnd"), RenderSystem.getShaderFogEnd());
         shader.setUniform(shader.getUniformLocation("FogColor"), RenderSystem.getShaderFogColor());
         Vector3d offset = physics.getOffset();
         shader.setUniform3(
            shader.getUniformLocation("SmokeCameraPos"), (float)(cameraPos.x - offset.x), (float)(cameraPos.y - offset.y), (float)(cameraPos.z - offset.z)
         );
         double renderPercent = physics.getRenderPercent();
         shader.setUniform1(shader.getUniformLocation("RenderPercent"), (float)renderPercent);
         smokeVAO.renderInstanced(this.count);
         GL32C.glUseProgram(previousProgram);
      }

      PerformanceTracker.end("smoke_rendering");
   }

   private void updateSmokeInstances(SmokeDomain smokeDomain, Vec3 cameraPos) {
      if (smokeVAO == null) {
         this.createSmokeVAO(PhysicsMod.smoke);
      }

      PhysicsWorld physics = smokeDomain.getWorld();
      Vector3d physicsOffset = physics.getOffset();
      List<IRigidBody> smokeParticles = smokeDomain.getAllParticles();
      this.checkSmokeArrays(smokeParticles.size() * 4);
      this.count = 0;

      for (int i = 0; i < smokeParticles.size(); i++) {
         IRigidBody body = smokeParticles.get(i);
         if (!body.isDestroyed()) {
            this.prepareSmokeInstances(
               physics, physics.getLevel(), cameraPos, body.getEntity(), body, physicsOffset.x, physicsOffset.y, physicsOffset.z, this.count
            );
            this.count++;
         }
      }

      smokeVAO.bind();
      smokeVAO.updateAttribute(Data.SMOKE_LIGHT, this.smokelight, this.count * 4);
      smokeVAO.updateAttribute(Data.SMOKE_POS, this.smokepos, this.count * 4);
      smokeVAO.updateAttribute(Data.SMOKE_POS_NEW, this.smokeposnew, this.count * 4);
   }

   private void checkSmokeArrays(int neededSize) {
      boolean changed = false;

      int size;
      for (size = this.smokelight.length; neededSize > size; changed = true) {
         size *= 2;
      }

      if (changed) {
         this.smokelight = new byte[size];
         this.smokepos = new float[size];
         this.smokeposnew = new float[size];
      }
   }

   private void prepareSmokeInstances(
      PhysicsWorld physics, Level level, Vec3 view, PhysicsEntity particle, IRigidBody body, double ox, double oy, double oz, int offset
   ) {
      Matrix4d oldTransformation = particle.getOldTransformation();
      Matrix4d currentTransformation = particle.getTransformation();
      this.blockPos.set(currentTransformation.m30() + ox, currentTransformation.m31() + oy, currentTransformation.m32() + oz);
      int brightness = particle.getLight(level, this.blockPos);
      int mulOffset = offset * 4;
      byte scale = (byte)(Math.remapClamp((double)particle.scale, 0.25, 5.0, 0.0, 1.0) * 255.0);
      this.smokelight[mulOffset] = (byte)(particle.getBGRA() & 0xFF);
      this.smokelight[mulOffset + 1] = (byte)(particle.getBGRA() >> 8 & 0xFF);
      this.smokelight[mulOffset + 2] = (byte)(brightness >> 4 & 15 | brightness >> 16 & 240);
      this.smokelight[mulOffset + 3] = scale;
      double animationScale = particle.getDespawnScale(level);
      this.smokepos[mulOffset] = (float)oldTransformation.m30();
      this.smokepos[mulOffset + 1] = (float)oldTransformation.m31();
      this.smokepos[mulOffset + 2] = (float)oldTransformation.m32();
      this.smokepos[mulOffset + 3] = java.lang.Math.min(1.0F, (float)animationScale);
      this.smokeposnew[mulOffset] = (float)currentTransformation.m30();
      this.smokeposnew[mulOffset + 1] = (float)currentTransformation.m31();
      this.smokeposnew[mulOffset + 2] = (float)currentTransformation.m32();
      this.smokeposnew[mulOffset + 3] = ((ParticleInfo)body.getUserData()).averagedDensity;
   }

   private void createSmokeVAO(Mesh mesh) {
      int size = mesh.indices.size();
      if (smokeVAO == null) {
         net.diebuddies.opengl.Mesh openglMesh = new net.diebuddies.opengl.Mesh();
         this.mainRenderer.checkArrays(size);

         for (int i = 0; i < size; i++) {
            int index = mesh.indices.getInt(i);
            Vector3f p = mesh.positions.get(index);
            Vector2f uv = mesh.uvs.get(index);
            Vector3f normal = mesh.normals.get(index);
            this.mainRenderer.mcol[i] = -1;
            int cp = i * 3;
            this.mainRenderer.mpos[cp] = p.x;
            this.mainRenderer.mpos[cp + 1] = p.y;
            this.mainRenderer.mpos[cp + 2] = p.z;
            int cu = i * 2;
            this.mainRenderer.muv[cu] = uv.x;
            this.mainRenderer.muv[cu + 1] = uv.y;
            this.mainRenderer.mnormals[i] = Pack.normal(normal.x, normal.y, normal.z);
         }

         openglMesh.set(this.mainRenderer.mpos, Data.POSITION);
         openglMesh.set(this.mainRenderer.mcol, Data.COLOR);
         openglMesh.set(this.mainRenderer.muv, Data.TEX_COORD);
         openglMesh.set(this.mainRenderer.mnormals, Data.NORMAL);
         openglMesh.set(this.smokelight, Data.SMOKE_LIGHT);
         openglMesh.set(this.smokepos, Data.SMOKE_POS);
         openglMesh.set(this.smokeposnew, Data.SMOKE_POS_NEW);
         openglMesh.setSize(Data.POSITION, size * 3);
         openglMesh.setSize(Data.COLOR, size);
         openglMesh.setSize(Data.TEX_COORD, size * 2);
         openglMesh.setSize(Data.NORMAL, size);
         openglMesh.setSize(Data.INDEX, size);
         smokeVAO = openglMesh.constructVAO(Usage.DYNAMIC);
      }
   }

   public static void destroy() {
      if (smokeShader != null) {
         smokeShader.destroy();
      }

      if (smokeShadowShader != null) {
         smokeShadowShader.destroy();
      }

      if (smokeVAO != null) {
         smokeVAO.destroy();
      }

      if (smokeDepthCopy != null) {
         smokeDepthCopy.destroy();
      }
   }
}
