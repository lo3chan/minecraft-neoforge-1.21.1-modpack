package net.diebuddies.compat;

import java.lang.reflect.Method;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.util.ShaderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.optifine.Config;
import net.optifine.shaders.BlockAliases;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Optifine {
   public static Program compilingProgram;
   public static Program oceanProgram;
   public static Program oceanShadowProgram;
   public static ShaderType compileStage;
   private static Method getTextureById;
   private static boolean init = false;

   public static boolean areShadersEnabled() {
      if (!initMethods()) {
         return false;
      } else {
         try {
            return Config.isShaders();
         } catch (Exception var1) {
            var1.printStackTrace();
            return false;
         }
      }
   }

   public static boolean isShadowPass() {
      if (!initMethods()) {
         return false;
      } else {
         try {
            return Shaders.isShadowPass;
         } catch (Exception var1) {
            var1.printStackTrace();
            return false;
         }
      }
   }

   public static boolean isUsingShadersNoInternal() {
      if (!initMethods()) {
         return false;
      } else {
         try {
            return Shaders.uniform_modelViewMatrix.isDefined();
         } catch (Exception var1) {
            var1.printStackTrace();
            return false;
         }
      }
   }

   public static void setModelViewMatrix(Matrix4f modelViewMatrix) {
      if (initMethods()) {
         try {
            Shaders.setModelViewMatrix(modelViewMatrix);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setProjectionMatrix(Matrix4f projectionMatrix) {
      if (initMethods()) {
         try {
            Shaders.setProjectionMatrix(projectionMatrix);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setColorModulator(float[] colors) {
      if (initMethods()) {
         try {
            Shaders.setColorModulator(colors);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setTextureMatrix(Matrix4f textureMatrix) {
      if (initMethods()) {
         try {
            Shaders.setTextureMatrix(textureMatrix);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setNormalMatrix(Matrix3f normal) {
      if (initMethods()) {
         try {
            if (Shaders.uniform_normalMatrix.isDefined()) {
               Shaders.uniform_normalMatrix.setValue(normal);
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static boolean bindPBRTexture(int glID) {
      if (!initMethods()) {
         return false;
      } else {
         boolean isDefaultTexture = false;

         try {
            AbstractTexture texture = (AbstractTexture)getTextureById.invoke(Minecraft.getInstance().getTextureManager(), glID);
            if (texture == null) {
               texture = Shaders.defaultTexture;
               isDefaultTexture = true;
            }

            ShadersTex.bindTexture(texture);
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         return isDefaultTexture;
      }
   }

   public static int getMaterialID(BlockState state) {
      if (!initMethods()) {
         return -1;
      } else {
         try {
            return BlockAliases.getAliasBlockId(state);
         } catch (Exception var2) {
            var2.printStackTrace();
            return -1;
         }
      }
   }

   public static int getRenderType(BlockState state) {
      if (!initMethods()) {
         return -1;
      } else {
         try {
            return BlockAliases.getRenderType(state);
         } catch (Exception var2) {
            var2.printStackTrace();
            return -1;
         }
      }
   }

   public static void useEntityShader() {
      if (initMethods()) {
         Shaders.useProgram(Shaders.ProgramEntities);
      }
   }

   public static void useOceanShader() {
      if (initMethods()) {
         Shaders.useProgram(oceanProgram);
      }
   }

   public static void useOceanShadowShader() {
      if (initMethods()) {
         boolean before = Shaders.isShadowPass;
         Shaders.isShadowPass = false;
         Shaders.useProgram(oceanShadowProgram);
         Shaders.isShadowPass = before;
      }
   }

   public static void useWaterShader() {
      if (initMethods()) {
         Shaders.useProgram(Shaders.ProgramWater);
      }
   }

   private static boolean initMethods() {
      if (!StarterClient.optifabric) {
         return false;
      } else if (init) {
         return true;
      } else {
         init = true;

         try {
            Class<?> textureManager = TextureManager.class;
            getTextureById = textureManager.getMethod("getTextureById", int.class);
         } catch (SecurityException | NoSuchMethodException var1) {
            var1.printStackTrace();
         }

         return true;
      }
   }
}
