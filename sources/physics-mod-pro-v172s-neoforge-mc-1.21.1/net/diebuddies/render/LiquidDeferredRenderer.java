package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.nio.FloatBuffer;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.FBO;
import net.diebuddies.opengl.Pack;
import net.diebuddies.opengl.SaveTexture;
import net.diebuddies.opengl.Shader;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.Usage;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.liquid.LiquidCuda;
import net.diebuddies.render.shader.EmptyTextureShader;
import net.diebuddies.render.shader.GaussianDepthBlurEffect;
import net.diebuddies.render.shader.LiquidCompositeShader;
import net.diebuddies.render.shader.LiquidShader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;

public class LiquidDeferredRenderer {
   private MainRenderer mainRenderer;
   private static FBO depthFBO;
   private static LiquidShader liquidShader;
   private static LiquidCompositeShader liquidCompositeShader;
   private static EmptyTextureShader emptyTextureShader;
   private static VAO liquidVAO;
   private static VAO emptyVAO;
   private static Texture liquidDepthCopy;
   private static Texture depth;
   private static GaussianDepthBlurEffect blurEffect;
   private static float[] liquidpos = new float[400];
   private static float[] liquidposnew = new float[400];
   private static int[] viewport = new int[4];
   private int liquidCount;
   private int mcEntityLocation;
   private int depthActiveTexture = 25;
   private Vector4f waterBounds = new Vector4f();
   private Vector2f waterMidCoord = new Vector2f();
   private int waterID;
   private Vector3d offsetCamera = new Vector3d(0.0, 0.0, 0.0);
   private Matrix4f tmpMatrix = new Matrix4f();

   public LiquidDeferredRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 cameraPos) {
      if (ConfigClient.liquidPhysics) {
         TextureAtlasSprite waterTexture = Minecraft.getInstance()
            .getModelManager()
            .getBlockModelShaper()
            .getBlockModel(Blocks.WATER.defaultBlockState())
            .getParticleIcon();
         this.waterID = Minecraft.getInstance().getTextureManager().getTexture(waterTexture.atlasLocation()).getId();
         this.waterBounds.set(waterTexture.getU0(), waterTexture.getU1(), waterTexture.getV0(), waterTexture.getV1());
         this.waterMidCoord.set(this.waterBounds.x + this.waterBounds.y, this.waterBounds.z + this.waterBounds.w).mul(0.5F);
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && !Iris.liquidsError.isEmpty()) {
            player.displayClientMessage(Component.literal(Iris.liquidsError).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
            Iris.liquidsError = "";
         }

         RenderSystem.activeTexture(33984);
         int boundBefore = GL32C.glGetInteger(32873);
         int previousProgram = GL32C.glGetInteger(35725);
         int oldBlendEquationRGB = GL32C.glGetInteger(32777);
         GL32C.glGetIntegerv(2978, viewport);
         if (liquidVAO == null) {
            liquidVAO = this.createLiquidVAO(PhysicsMod.liquid);
            emptyVAO = new VAO();
            liquidShader = new LiquidShader();
            emptyTextureShader = new EmptyTextureShader();
         }

         boolean isShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
         boolean skipShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() && !Iris.renderLiquidShadow();
         if (!isShadowPass) {
            this.updateLiquidInstances(physics, cameraPos);
         }

         if (this.liquidCount != 0 && !skipShadowPass) {
            if (!isShadowPass) {
               this.renderLiquidDataIntoTexture(physics, level, cameraPos);
            } else {
               this.renderInstancedLiquidSpheres(physics, level, matrixStackIn, cameraPos);
            }

            GL32C.glUseProgram(previousProgram);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendEquation(oldBlendEquationRGB);
            RenderSystem.activeTexture(33984);
            GL32C.glBindTexture(3553, boundBefore);
            StateTracker.unbindVertexArray();
         }
      }
   }

   private void renderInstancedLiquidSpheres(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 cameraPos) {
      this.bindLiquidsShader();
      ShaderInstance shaderInstance = RenderSystem.getShader();
      this.initRenderingStates(level, shaderInstance, true);
      this.setupLiquidsRendering(physics, level, shaderInstance, this.waterID);
      RenderSystem.enableCull();
      if (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal()) {
         int location = shaderInstance.MODEL_VIEW_MATRIX.getLocation();
         if (location != -1) {
            GL32C.glUniformMatrix4fv(location, false, matrixStackIn.get(MainRenderer.matrixBuffer));
         }
      }

      if (StarterClient.iris) {
         Iris.setNormalMatrix(shaderInstance, matrixStackIn);
      }

      Vector3d offset = physics.getOffset();
      this.offsetCamera.set(cameraPos.x - offset.x, cameraPos.y - offset.y, cameraPos.z - offset.z);
      int shaderId = GL32C.glGetInteger(35725);
      int cameraLocation = GL32C.glGetUniformLocation(shaderId, "physics_liquidCameraPos");
      int renderPercentLocation = GL32C.glGetUniformLocation(shaderId, "physics_renderPercent");
      if (cameraLocation != -1) {
         GL32C.glUniform3f(cameraLocation, (float)this.offsetCamera.x, (float)this.offsetCamera.y, (float)this.offsetCamera.z);
      }

      if (renderPercentLocation != -1) {
         GL32C.glUniform1f(renderPercentLocation, (float)physics.getRenderPercent());
      }

      liquidVAO.renderInstanced(this.liquidCount);
   }

   private void renderLiquidDataIntoTexture(PhysicsWorld physics, ClientLevel level, Vec3 cameraPos) {
      int drawFboBoundBefore = GL32C.glGetInteger(36006);
      int readFboBoundBefore = GL32C.glGetInteger(36010);
      boolean create = depthFBO == null || liquidDepthCopy.getWidth() != viewport[2] || liquidDepthCopy.getHeight() != viewport[3];
      liquidDepthCopy = SaveTexture.copyFramebufferDepthTexture(liquidDepthCopy);
      if (create) {
         if (depthFBO != null) {
            depthFBO.destroy(false);
            depth.destroy();
            blurEffect.destroy();
         }

         blurEffect = new GaussianDepthBlurEffect(8.0, 20);
         depthFBO = new FBO();
         depth = Texture.createTexture(viewport[2], viewport[3], 33326, 6403, 5126);
         depthFBO.attachColorBuffer(depth);
         depthFBO.attachDepthBuffer(liquidDepthCopy);
         depthFBO.checkError();
         blurEffect.setImage(depthFBO);
      }

      liquidShader.bind();
      depthFBO.bind();
      liquidVAO.bind();
      RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      RenderSystem.clear(16384, false);
      RenderSystem.applyModelViewMatrix();
      RenderSystem.disableBlend();
      Vector3d offset = physics.getOffset();
      this.offsetCamera.set(cameraPos.x - offset.x, cameraPos.y - offset.y, cameraPos.z - offset.z);
      Shader shader = liquidShader;
      shader.bind();
      shader.uploadMatrix(shader.getUniformLocation("ModelViewMat"), RenderSystem.getModelViewMatrix());
      shader.uploadMatrix(shader.getUniformLocation("ProjMat"), RenderSystem.getProjectionMatrix());
      shader.setUniform3(shader.getUniformLocation("LiquidCameraPos"), (float)this.offsetCamera.x, (float)this.offsetCamera.y, (float)this.offsetCamera.z);
      double renderPercent = physics.getRenderPercent();
      shader.setUniform1(shader.getUniformLocation("RenderPercent"), (float)renderPercent);
      liquidVAO.renderInstanced(this.liquidCount);

      for (int i = 0; i < ConfigClient.cudaLiquidsBlurPasses; i++) {
         blurEffect.render(emptyVAO);
      }

      RenderSystem.enableBlend();
      RenderSystem.disableDepthTest();
      GL32C.glBindFramebuffer(36009, drawFboBoundBefore);
      GL32C.glBindFramebuffer(36008, readFboBoundBefore);
      this.bindLiquidsShader();
      ShaderInstance shaderInstance = RenderSystem.getShader();
      this.initRenderingStates(level, shaderInstance, false);
      this.setupLiquidsRendering(physics, level, shaderInstance, this.waterID);
      emptyVAO.renderEmptyTriangle();
   }

   private void bindLiquidsShader() {
      if (StarterClient.iris && Iris.isExtending()) {
         if (Iris.isShadowPass()) {
            if (Iris.getLiquidShadowProgram() != null) {
               RenderSystem.setShader(() -> Iris.getLiquidShadowProgram());
               this.mainRenderer.setupShader(Iris.getLiquidShadowProgram());
            }
         } else if (Iris.getLiquidProgram() != null) {
            RenderSystem.setShader(() -> Iris.getLiquidProgram());
            this.mainRenderer.setupShader(Iris.getLiquidProgram());
         }
      } else if (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal()) {
         if (liquidCompositeShader == null) {
            try {
               liquidCompositeShader = new LiquidCompositeShader();
            } catch (IOException var2) {
               var2.printStackTrace();
            }
         }

         RenderSystem.setShader(() -> liquidCompositeShader);
         this.mainRenderer.setupShader(liquidCompositeShader);
      }
   }

   private void initRenderingStates(ClientLevel level, ShaderInstance shader, boolean shadowPass) {
      if (StarterClient.optifabric) {
         GL32C.glVertexAttrib3f(Data.NORMAL.getAttribute(), 0.0F, 1.0F, 0.0F);
      } else {
         GL32C.glVertexAttrib3f(Data.NORMAL_SHADER.getAttribute(), 0.0F, 1.0F, 0.0F);
      }

      int color = BiomeColors.getAverageWaterColor(level, Minecraft.getInstance().player.blockPosition());
      GL32C.glVertexAttrib4f(Data.COLOR_SHADER.getAttribute(), Pack.getRed(color), Pack.getGreen(color), Pack.getBlue(color), 1.0F);
      GL32C.glVertexAttribI2i(Data.LIGHT_SHADER.getAttribute(), 240, 240);
      GL32C.glVertexAttrib2f(Data.TEX_COORD_SHADER.getAttribute(), this.waterMidCoord.x, this.waterMidCoord.y);
      if (StarterClient.iris) {
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), this.waterMidCoord.x, this.waterMidCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_TERRAIN_SHADER.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         this.mcEntityLocation = GL20.glGetAttribLocation(RenderSystem.getShader().getId(), "mc_Entity");
         if (this.mcEntityLocation != -1) {
            GL32C.glVertexAttrib4f(this.mcEntityLocation, Iris.getMaterialID(Blocks.WATER.defaultBlockState()), 1.0F, -1.0F, -1.0F);
         }
      } else if (StarterClient.optifabric) {
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), this.waterMidCoord.x, this.waterMidCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_OPTIFINE.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         if (Optifine.isUsingShadersNoInternal()) {
            this.mcEntityLocation = 11;
         } else {
            this.mcEntityLocation = GL20.glGetAttribLocation(RenderSystem.getShader().getId(), "mc_Entity");
         }

         if (this.mcEntityLocation != -1) {
            GL32C.glVertexAttrib4f(
               this.mcEntityLocation,
               Optifine.getMaterialID(Blocks.WATER.defaultBlockState()),
               Optifine.getRenderType(Blocks.WATER.defaultBlockState()),
               -1.0F,
               -1.0F
            );
         }
      }

      if (!shadowPass) {
         RenderSystem.activeTexture(33984 + this.depthActiveTexture);
         GL32C.glBindTexture(3553, depth.getID());
      }

      RenderSystem.activeTexture(33984);
      RenderSystem.disableCull();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void setupLiquidsRendering(PhysicsWorld physics, ClientLevel level, ShaderInstance shader, int waterTexture) {
      if (level.effects().constantAmbientLight()) {
         RenderSystem.shaderLightDirections[0] = MainRenderer.NETHER_DIFFUSE_LIGHT_0;
         RenderSystem.shaderLightDirections[1] = MainRenderer.NETHER_DIFFUSE_LIGHT_1;
      } else {
         RenderSystem.shaderLightDirections[0] = MainRenderer.DIFFUSE_LIGHT_0;
         RenderSystem.shaderLightDirections[1] = MainRenderer.DIFFUSE_LIGHT_1;
      }

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
         Optifine.setColorModulator(RenderSystem.getShaderColor());
      } else if (shader.COLOR_MODULATOR != null) {
         shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
         shader.COLOR_MODULATOR.upload();
      }

      int shaderId = GL32C.glGetInteger(35725);
      int depthLocation = GL32C.glGetUniformLocation(shaderId, "physics_depth");
      int invProjLocation = GL32C.glGetUniformLocation(shaderId, "physics_invProjectionMatrix");
      int invViewLocation = GL32C.glGetUniformLocation(shaderId, "physics_invViewMatrix");
      if (depthLocation != -1) {
         GL32C.glUniform1i(depthLocation, this.depthActiveTexture);
      }

      if (invProjLocation != -1) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            RenderSystem.getProjectionMatrix().invert(this.tmpMatrix).get(matrixBuffer);
            GL32C.glUniformMatrix4fv(invProjLocation, false, matrixBuffer);
         } catch (Throwable var15) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var13) {
                  var15.addSuppressed(var13);
               }
            }

            throw var15;
         }

         if (stack != null) {
            stack.close();
         }
      }

      if (invViewLocation != -1) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            RenderSystem.getModelViewMatrix().invert(this.tmpMatrix).get(matrixBuffer);
            GL32C.glUniformMatrix4fv(invViewLocation, false, matrixBuffer);
         } catch (Throwable var14) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var12) {
                  var14.addSuppressed(var12);
               }
            }

            throw var14;
         }

         if (stack != null) {
            stack.close();
         }
      }

      RenderSystem.setShaderTexture(0, waterTexture);
      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(waterTexture);
   }

   private VAO createLiquidVAO(Mesh mesh) {
      int size = mesh.indices.size();
      net.diebuddies.opengl.Mesh openglMesh = new net.diebuddies.opengl.Mesh();
      this.mainRenderer.checkArrays(size);

      for (int i = 0; i < size; i++) {
         int index = mesh.indices.getInt(i);
         Vector3f p = mesh.positions.get(index);
         Vector3f normal = mesh.normals.get(index);
         int cp = i * 3;
         this.mainRenderer.mpos[cp] = p.x;
         this.mainRenderer.mpos[cp + 1] = p.y;
         this.mainRenderer.mpos[cp + 2] = p.z;
         this.mainRenderer.mnormals[i] = Pack.normal(normal.x, normal.y, normal.z);
      }

      openglMesh.set(this.mainRenderer.mpos, Data.POSITION);
      openglMesh.set(this.mainRenderer.mnormals, Data.NORMAL);
      openglMesh.set(liquidpos, Data.LIQUID_POS);
      openglMesh.set(liquidposnew, Data.LIQUID_POS_NEW);
      openglMesh.setSize(Data.POSITION, size * 3);
      openglMesh.setSize(Data.NORMAL, size);
      openglMesh.setSize(Data.INDEX, size);
      return openglMesh.constructVAO(Usage.DYNAMIC);
   }

   private void checkLiquidArrays(int neededSize) {
      boolean changed = false;

      int size;
      for (size = liquidpos.length; neededSize > size; changed = true) {
         size *= 2;
      }

      if (changed) {
         liquidpos = new float[size];
         liquidposnew = new float[size];
      }
   }

   public void updateLiquidInstances(PhysicsWorld physics, Vec3 cameraPos) {
      int size = 0;
      int count = 0;
      Vector3d offset = physics.getOffset();
      this.offsetCamera.set(cameraPos.x + offset.x, cameraPos.y + offset.y, cameraPos.z + offset.z);
      if (ConfigClient.cudaLiquids()) {
         for (int i = 0; i < physics.getLiquids().size(); i++) {
            LiquidCuda liquid = (LiquidCuda)physics.getLiquids().get(i);
            size += liquid.cudaPositions.length;
         }

         this.checkLiquidArrays(size * 4);

         for (int i = 0; i < physics.getLiquids().size(); i++) {
            LiquidCuda liquid = (LiquidCuda)physics.getLiquids().get(i);
            float[] positions = liquid.cudaPositions;
            float[] oldPositions = liquid.cudaOldPositions;

            for (int j = 0; j < liquid.cudaParticleSize; j++) {
               this.prepareLiquidCudaInstances(physics, positions, oldPositions, j, this.offsetCamera, count++);
            }
         }

         this.liquidCount = count;
      } else {
         for (int i = 0; i < physics.getLiquids().size(); i++) {
            Liquid liquid = physics.getLiquids().get(i);
            size += liquid.particles.size();
         }

         this.checkLiquidArrays(size * 4);

         for (int i = 0; i < physics.getLiquids().size(); i++) {
            Liquid liquid = physics.getLiquids().get(i);

            for (IRigidBody body : liquid.particles) {
               this.prepareLiquidInstances(physics, body, this.offsetCamera, count++);
            }
         }

         this.liquidCount = count;
      }

      if (count != 0) {
         liquidVAO.bind();
         liquidVAO.updateAttribute(Data.LIQUID_POS, liquidpos, count * 4);
         liquidVAO.updateAttribute(Data.LIQUID_POS_NEW, liquidposnew, count * 4);
      }
   }

   private boolean prepareLiquidInstances(PhysicsWorld physics, IRigidBody body, Vector3d cameraPos, int offset) {
      int mulOffset = offset * 4;
      PhysicsEntity particle = body.getEntity();
      Matrix4d transformation = particle.getTransformation();
      Matrix4d transformationOld = particle.getOldTransformation();
      float scale = particle.getDespawnScale(physics.getWorld());
      liquidpos[mulOffset] = (float)transformationOld.m30();
      liquidpos[mulOffset + 1] = (float)transformationOld.m31();
      liquidpos[mulOffset + 2] = (float)transformationOld.m32();
      liquidpos[mulOffset + 3] = 1.0F;
      liquidposnew[mulOffset] = (float)transformation.m30();
      liquidposnew[mulOffset + 1] = (float)transformation.m31();
      liquidposnew[mulOffset + 2] = (float)transformation.m32();
      liquidposnew[mulOffset + 3] = scale * 0.25F;
      return true;
   }

   private boolean prepareLiquidCudaInstances(PhysicsWorld physics, float[] positions, float[] oldPositions, int index, Vector3d cameraPos, int offset) {
      int mulOffset = offset * 4;
      int mulIndex = index * 3;
      liquidpos[mulOffset] = oldPositions[mulIndex];
      liquidpos[mulOffset + 1] = oldPositions[mulIndex + 1];
      liquidpos[mulOffset + 2] = oldPositions[mulIndex + 2];
      liquidpos[mulOffset + 3] = 1.0F;
      liquidposnew[mulOffset] = positions[mulIndex];
      liquidposnew[mulOffset + 1] = positions[mulIndex + 1];
      liquidposnew[mulOffset + 2] = positions[mulIndex + 2];
      liquidposnew[mulOffset + 3] = physics.fluidParticleSize;
      return true;
   }

   public static void destroy() {
      if (depthFBO != null) {
         depthFBO.destroy(true);
      }

      if (liquidCompositeShader != null) {
         liquidCompositeShader.close();
      }

      if (liquidShader != null) {
         liquidShader.destroy();
      }

      if (liquidVAO != null) {
         liquidVAO.destroy();
      }

      if (liquidDepthCopy != null) {
         liquidDepthCopy.destroy();
      }

      if (emptyVAO != null) {
         emptyVAO.destroy();
      }

      if (emptyTextureShader != null) {
         emptyTextureShader.destroy();
      }

      if (blurEffect != null) {
         blurEffect.destroy();
      }
   }
}
