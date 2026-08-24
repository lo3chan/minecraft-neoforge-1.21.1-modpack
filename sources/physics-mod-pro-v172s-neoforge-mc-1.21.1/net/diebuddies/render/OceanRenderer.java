package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.mixins.ocean.MixinLightTextureAccessor;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanMesh;
import net.diebuddies.physics.ocean.OceanSurface;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.render.shader.OceanShader;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32C;

public class OceanRenderer {
   private MainRenderer mainRenderer;
   private OceanRippleRenderer rippleRenderer;
   private static OceanShader oceanShader;
   private int mcEntityLocation = -1;
   private Matrix4f transformation = new Matrix4f();
   private Matrix4f currentPose = new Matrix4f();
   private Matrix3f tmp = new Matrix3f();
   private int wavinessLocation = -1;
   private int waveOffsetLocation = -1;
   private int textureOffsetLocation = -1;
   private int modelOffsetLocation = -1;
   private int physicsGameTimeLocation = -1;
   private int globalGameTimeLocation = -1;
   private int physicsIterationsNormalLocation = -1;
   private int physicsOceanHeightLocation = -1;
   private int oceanWaveHorizontalScaleLocation = -1;
   private int rippleLocation = -1;
   private int foamLocation = -1;
   private int rippleRangeLocation = -1;
   private int foamAmountLocation = -1;
   private int foamOpacityLocation = -1;
   private int lightmapLocation = -1;
   private int rippleActiveTexture = 0;
   private int foamActiveTexture = 26;
   private int lightmapActiveTexture = 27;
   private int activeTexture = 0;
   private int oldTexture;
   private int oldRippleTexture;
   private List<OceanMesh> drawList;

   public OceanRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.drawList = new ObjectArrayList();
      this.rippleRenderer = new OceanRippleRenderer(mainRenderer);
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view) {
      if (ConfigClient.areOceanPhysicsEnabled()) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && !Iris.oceanError.isEmpty()) {
            player.displayClientMessage(Component.literal(Iris.oceanError).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
            Iris.oceanError = "";
         }

         PerformanceTracker.startNoFlush("ocean_rendering");
         OceanWorld oceanWorld = physics.getOceanWorld();
         TextureAtlasSprite waterTexture = Minecraft.getInstance()
            .getModelManager()
            .getBlockModelShaper()
            .getBlockModel(Blocks.WATER.defaultBlockState())
            .getParticleIcon();
         int waterID = Minecraft.getInstance().getTextureManager().getTexture(waterTexture.atlasLocation()).getId();
         this.bindOceanShader();
         ShaderInstance shader = RenderSystem.getShader();
         this.initRenderingStates(shader, oceanWorld);
         Matrix4f cameraRotation = matrixStackIn;
         Matrix3f normal = matrixStackIn.normal(this.tmp);
         this.setupOceanRendering(physics, null, level, shader, waterID);
         boolean isShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
         ObjectIterator var13 = oceanWorld.getOceanLayers().values().iterator();

         while (var13.hasNext()) {
            ProxyOceanLayer layer = (ProxyOceanLayer)var13.next();
            OceanSurface surface = layer.getOceanSurface();
            if (surface != null) {
               ObjectIterator updatedRipples = surface.getMeshes().values().iterator();

               while (updatedRipples.hasNext()) {
                  OceanMesh oceanMesh = (OceanMesh)updatedRipples.next();
                  if (this.isVisible(oceanWorld, oceanMesh, view)) {
                     this.drawList.add(oceanMesh);
                  }
               }

               if (!this.drawList.isEmpty()) {
                  if (ConfigClient.oceanRipples && !isShadowPass) {
                     boolean updatedRipplesx = false;
                     boolean var19;
                     if (var19 = layer.needsRippleUpdate()) {
                        this.rippleRenderer.updateRippleInstances(physics.getOceanWorld(), layer, view);
                        layer.setNeedsRippleUpdate(false);
                     }

                     if (layer.getRippleCount() > 0) {
                        this.rippleRenderer.renderSmallWaves(physics, layer, level, matrixStackIn, view);
                        this.bindOceanShader();
                        this.setupOceanRendering(physics, layer, level, shader, waterID);
                     } else {
                        this.bindRippleTexture(layer);
                        if (var19) {
                           physics.getOceanWorld().bindForRendering();
                        }
                     }
                  }

                  for (OceanMesh oceanMesh : this.drawList) {
                     this.renderOcean(oceanWorld, level, cameraRotation, normal, view, oceanMesh);
                  }

                  this.drawList.clear();
               }
            }
         }

         if (StarterClient.sodium) {
            Sodium.markSpriteActive(waterTexture);
         }

         RenderSystem.enableCull();
         RenderSystem.activeTexture(33984 + this.activeTexture);
         RenderSystem.setShaderTexture(this.activeTexture, this.oldTexture);
         RenderSystem.bindTexture(this.oldTexture);
         RenderSystem.activeTexture(33984 + this.rippleActiveTexture);
         RenderSystem.setShaderTexture(this.rippleActiveTexture, this.oldRippleTexture);
         RenderSystem.bindTexture(this.oldRippleTexture);
         RenderSystem.activeTexture(33984);
         StateTracker.unbindVertexArray();
         PerformanceTracker.end("ocean_rendering");
      }
   }

   private void initRenderingStates(ShaderInstance shader, OceanWorld oceanWorld) {
      int shaderId = GL32C.glGetInteger(35725);
      this.physicsGameTimeLocation = GL32C.glGetUniformLocation(shaderId, "physics_gameTime");
      this.globalGameTimeLocation = GL32C.glGetUniformLocation(shaderId, "physics_globalTime");
      this.physicsIterationsNormalLocation = GL32C.glGetUniformLocation(shaderId, "physics_iterationsNormal");
      this.physicsOceanHeightLocation = GL32C.glGetUniformLocation(shaderId, "physics_oceanHeight");
      this.oceanWaveHorizontalScaleLocation = GL32C.glGetUniformLocation(shaderId, "physics_oceanWaveHorizontalScale");
      this.rippleLocation = GL32C.glGetUniformLocation(shaderId, "physics_ripples");
      this.foamLocation = GL32C.glGetUniformLocation(shaderId, "physics_foam");
      this.lightmapLocation = GL32C.glGetUniformLocation(shaderId, "physics_lightmap");
      this.rippleRangeLocation = GL32C.glGetUniformLocation(shaderId, "physics_rippleRange");
      this.foamAmountLocation = GL32C.glGetUniformLocation(shaderId, "physics_foamAmount");
      this.foamOpacityLocation = GL32C.glGetUniformLocation(shaderId, "physics_foamOpacity");
      this.wavinessLocation = GL32C.glGetUniformLocation(shaderId, "physics_waviness");
      this.waveOffsetLocation = GL32C.glGetUniformLocation(shaderId, "physics_waveOffset");
      this.textureOffsetLocation = GL32C.glGetUniformLocation(shaderId, "physics_textureOffset");
      this.modelOffsetLocation = GL32C.glGetUniformLocation(shaderId, "physics_modelOffset");
      if (StarterClient.optifabric) {
         GL32C.glVertexAttrib3f(Data.NORMAL.getAttribute(), 0.0F, 1.0F, 0.0F);
      } else {
         GL32C.glVertexAttrib3f(Data.NORMAL_SHADER.getAttribute(), 0.0F, 1.0F, 0.0F);
      }

      if (StarterClient.iris) {
         Vector2f midCoord = oceanWorld.getWaterMidCoord();
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), midCoord.x, midCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_TERRAIN_SHADER.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         this.mcEntityLocation = GL20.glGetAttribLocation(RenderSystem.getShader().getId(), "mc_Entity");
         if (this.mcEntityLocation != -1) {
            GL32C.glVertexAttrib4f(this.mcEntityLocation, Iris.getMaterialID(Blocks.WATER.defaultBlockState()), 1.0F, -1.0F, -1.0F);
         }

         this.activeTexture = 15;
      } else if (StarterClient.optifabric) {
         Vector2f midCoord = oceanWorld.getWaterMidCoord();
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), midCoord.x, midCoord.y);
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

         this.activeTexture = 15;
      } else {
         this.activeTexture = 11;
      }

      this.rippleActiveTexture = this.activeTexture - 1;
      RenderSystem.activeTexture(33984 + this.activeTexture);
      this.oldTexture = GL32C.glGetInteger(32873);
      RenderSystem.activeTexture(33984 + this.rippleActiveTexture);
      this.oldRippleTexture = GL32C.glGetInteger(32873);
      RenderSystem.activeTexture(33984);
      RenderSystem.disableCull();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void setupOceanRendering(PhysicsWorld physics, @Nullable ProxyOceanLayer layer, ClientLevel level, ShaderInstance shader, int waterTexture) {
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

      if (this.physicsGameTimeLocation != -1) {
         GL32C.glUniform1f(this.physicsGameTimeLocation, physics.getOceanWorld().getOceanTime());
      }

      if (this.globalGameTimeLocation != -1) {
         GL32C.glUniform1f(this.globalGameTimeLocation, physics.getOceanWorld().getGlobalTime());
      }

      if (this.physicsIterationsNormalLocation != -1) {
         GL32C.glUniform1i(this.physicsIterationsNormalLocation, 13 + (int)(ConfigClient.oceanDetail * 35.0F));
      }

      if (this.physicsOceanHeightLocation != -1) {
         GL32C.glUniform1f(this.physicsOceanHeightLocation, physics.getOceanWorld().getOceanHeight());
      }

      if (this.oceanWaveHorizontalScaleLocation != -1) {
         GL32C.glUniform1f(this.oceanWaveHorizontalScaleLocation, ConfigClient.oceanHorizontalWaveScale);
      }

      if (this.rippleLocation != -1) {
         GL32C.glUniform1i(this.rippleLocation, this.rippleActiveTexture);
      }

      if (this.foamLocation != -1) {
         GL32C.glUniform1i(this.foamLocation, this.foamActiveTexture);
      }

      if (this.lightmapLocation != -1) {
         GL32C.glUniform1i(this.lightmapLocation, this.lightmapActiveTexture);
      }

      if (this.rippleRangeLocation != -1) {
         GL32C.glUniform1f(this.rippleRangeLocation, (float)this.rippleRenderer.getRippleRange());
      }

      if (this.foamAmountLocation != -1) {
         GL32C.glUniform1f(this.foamAmountLocation, ConfigClient.oceanFoamAmount);
      }

      if (this.foamOpacityLocation != -1) {
         GL32C.glUniform1f(this.foamOpacityLocation, ConfigClient.oceanFoamOpacity);
      }

      if (this.wavinessLocation != -1) {
         GL32C.glUniform1i(this.wavinessLocation, this.activeTexture);
      }

      RenderSystem.setShaderTexture(0, waterTexture);
      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(waterTexture);
      if (this.foamLocation != -1) {
         int foamTextureID = PhysicsMod.foamTexture.getID();
         RenderSystem.activeTexture(33984 + this.foamActiveTexture);
         GL32C.glBindTexture(32879, foamTextureID);
      }

      if (this.lightmapLocation != -1) {
         Minecraft mc = Minecraft.getInstance();
         int lightmapTextureID = mc.getTextureManager()
            .getTexture(((MixinLightTextureAccessor)mc.gameRenderer.lightTexture()).getLightTextureLocation())
            .getId();
         RenderSystem.activeTexture(33984 + this.lightmapActiveTexture);
         GL32C.glBindTexture(3553, lightmapTextureID);
      }

      this.bindRippleTexture(layer);
      physics.getOceanWorld().bindForRendering();
   }

   private void bindRippleTexture(ProxyOceanLayer layer) {
      int rippleTextureID = this.rippleRenderer.getRippleTexture(layer).getID();
      RenderSystem.activeTexture(33984 + this.rippleActiveTexture);
      RenderSystem.setShaderTexture(this.rippleActiveTexture, rippleTextureID);
      RenderSystem.bindTexture(rippleTextureID);
      RenderSystem.activeTexture(33984 + this.activeTexture);
   }

   private void bindOceanShader() {
      if (StarterClient.iris && Iris.isExtending()) {
         if (Iris.isShadowPass()) {
            if (!Iris.renderOceanShadow()) {
               return;
            }

            if (Iris.getOceanShadowProgram() != null) {
               RenderSystem.setShader(() -> Iris.getOceanShadowProgram());
               this.mainRenderer.setupShader(Iris.getOceanShadowProgram());
            }
         } else if (Iris.getOceanProgram() != null) {
            RenderSystem.setShader(() -> Iris.getOceanProgram());
            this.mainRenderer.setupShader(Iris.getOceanProgram());
         }
      } else if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         if (Optifine.isShadowPass()) {
            Optifine.useOceanShadowShader();
         } else {
            Optifine.useOceanShader();
         }

         this.mainRenderer.setupShader(RenderSystem.getShader());
      } else {
         if (oceanShader == null) {
            try {
               oceanShader = new OceanShader();
            } catch (IOException var2) {
               var2.printStackTrace();
            }
         }

         RenderSystem.setShader(() -> oceanShader);
         this.mainRenderer.setupShader(oceanShader);
      }
   }

   private boolean isVisible(OceanWorld oceanWorld, OceanMesh oceanMesh, Vec3 view) {
      AABB3D modelBoundingBox = oceanMesh.aabb;
      Vector3d start = modelBoundingBox.start;
      Vector3d end = modelBoundingBox.end;
      float halfHeight = Math.max(0.5F, oceanWorld.getOceanHeight() * 0.5F * oceanMesh.maxInfluence);
      return this.mainRenderer
         .frustumInt
         .testAab(
            (float)(start.x - view.x),
            (float)(start.y - view.y - halfHeight),
            (float)(start.z - view.z),
            (float)(end.x - view.x),
            (float)(end.y - view.y + halfHeight),
            (float)(end.z - view.z)
         );
   }

   public void renderOcean(OceanWorld oceanWorld, ClientLevel level, Matrix4f cameraRotationMatrix, Matrix3f normalMatrix, Vec3 view, OceanMesh oceanMesh) {
      int glID = oceanMesh.texture.getID();
      RenderSystem.setShaderTexture(this.activeTexture, glID);
      RenderSystem.bindTexture(glID);
      Matrix4d oceanTransformation = oceanMesh.transformation;
      this.transformation.m00((float)oceanTransformation.m00());
      this.transformation.m11((float)oceanTransformation.m11());
      this.transformation.m22((float)oceanTransformation.m22());
      this.transformation.m30((float)(oceanTransformation.m30() - view.x));
      this.transformation.m31((float)(oceanTransformation.m31() - view.y));
      this.transformation.m32((float)(oceanTransformation.m32() - view.z));
      cameraRotationMatrix.mul(this.transformation, this.currentPose);
      ShaderInstance shader = RenderSystem.getShader();
      if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         Optifine.setModelViewMatrix(this.currentPose);
      } else {
         int location = shader.MODEL_VIEW_MATRIX.getLocation();
         if (location != -1) {
            GL32C.glUniformMatrix4fv(location, false, this.currentPose.get(MainRenderer.matrixBuffer));
         }
      }

      if (this.modelOffsetLocation != -1) {
         GL32C.glUniform3f(
            this.modelOffsetLocation,
            (float)(oceanTransformation.m30() - view.x),
            (float)(oceanTransformation.m31() - view.y),
            (float)(oceanTransformation.m32() - view.z)
         );
      }

      if (this.waveOffsetLocation != -1) {
         GL32C.glUniform2f(this.waveOffsetLocation, oceanMesh.offsetX, oceanMesh.offsetZ);
      }

      if (this.textureOffsetLocation != -1) {
         GL32C.glUniform2i(this.textureOffsetLocation, oceanMesh.textureOffsetX, oceanMesh.textureOffsetZ);
      }

      if (StarterClient.iris) {
         Iris.setNormalMatrix(shader, this.currentPose, normalMatrix);
      }

      ArenaBuffer.MemorySegment vertexSegment = oceanMesh.vertexSegment;
      ArenaBuffer.MemorySegment indexSegment = oceanMesh.indexSegment;
      int baseVertex = vertexSegment.offset / oceanWorld.format.getStride();
      GL32C.glDrawElementsBaseVertex(4, indexSegment.size / 2, 5123, indexSegment.offset, baseVertex);
   }

   public void setupAttribute(int location, float v0, float v1, float v2, float v3) {
      if (location != -1) {
         GL20.glVertexAttrib4f(location, v0, v1, v2, v3);
      }
   }

   public static void destroy() {
      if (oceanShader != null) {
         oceanShader.close();
      }

      OceanRippleRenderer.destroy();
   }
}
