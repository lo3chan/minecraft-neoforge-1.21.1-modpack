package net.diebuddies.compat;

import com.mojang.blaze3d.shaders.Uniform;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import javax.annotation.Nullable;
import net.diebuddies.mixins.iris.MixinExtendedShaderAccessor;
import net.diebuddies.mixins.iris.MixinHandRendererAccessor;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.PhysicsExtendedPipeline;
import net.diebuddies.util.ShaderType;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Iris {
   public static String oceanError = "";
   public static String liquidsError = "";
   public static final ThreadLocal<Boolean> compilingLiquidShadowShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<Boolean> vertexShaderSupportsOcean = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<Boolean> fragmentShaderSupportsOcean = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<Boolean> geometryShaderSupportsOcean = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<ShaderType> preprocessOceanStage = ThreadLocal.withInitial(() -> null);
   public static final ThreadLocal<Boolean> injectIntoEntityOrShadowShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   private static Matrix4f tmp1 = new Matrix4f();
   private static Matrix3f tmp2 = new Matrix3f();

   public static short getMaterialID(BlockState block) {
      if (StarterClient.iris) {
         try {
            Object2IntMap<BlockState> idMap = WorldRenderingSettings.INSTANCE.getBlockStateIds();
            if (idMap != null) {
               return (short)idMap.getOrDefault(block, -1);
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      return -1;
   }

   public static void enableHandRendering() {
      if (StarterClient.iris) {
         ((MixinHandRendererAccessor)HandRenderer.INSTANCE).setRenderingSolid(true);
      }
   }

   public static void disableHandRendering() {
      if (StarterClient.iris) {
         ((MixinHandRendererAccessor)HandRenderer.INSTANCE).setRenderingSolid(false);
      }
   }

   public static boolean isExtending() {
      return StarterClient.iris ? IrisApi.getInstance().isShaderPackInUse() : false;
   }

   public static boolean isShadowPass() {
      return StarterClient.iris ? IrisApi.getInstance().isRenderingShadowPass() : false;
   }

   public static int getSpecularTextureID() {
      if (StarterClient.iris) {
         WorldRenderingPipeline pipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable();
         return pipeline.getCurrentSpecularTexture();
      } else {
         return 0;
      }
   }

   public static int getNormalTextureID() {
      if (StarterClient.iris) {
         WorldRenderingPipeline pipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable();
         return pipeline.getCurrentNormalTexture();
      } else {
         return 0;
      }
   }

   public static void setNormalMatrix(ShaderInstance shader, Matrix4f modelViewMatrix) {
      if (shader instanceof MixinExtendedShaderAccessor extended) {
         Uniform mvi = extended.getModelViewInverse();
         Uniform normal = extended.getNormalMatrix();
         if (mvi != null) {
            mvi.set(modelViewMatrix.invert(tmp1));
            mvi.upload();
         }

         if (normal != null) {
            if (mvi != null) {
               normal.set(tmp1.transpose3x3(tmp2));
            } else {
               normal.set(modelViewMatrix.normal(tmp2));
            }

            normal.upload();
         }
      }
   }

   public static void setNormalMatrix(ShaderInstance shader, Matrix4f modelViewMatrix, Matrix3f normalMatrix) {
      if (shader instanceof MixinExtendedShaderAccessor extended) {
         Uniform mvi = extended.getModelViewInverse();
         Uniform normal = extended.getNormalMatrix();
         if (mvi != null) {
            mvi.set(modelViewMatrix.invert(tmp1));
            mvi.upload();
         }

         if (normal != null) {
            normal.set(normalMatrix);
            normal.upload();
         }
      }
   }

   @Nullable
   public static ShaderInstance getOceanProgram() {
      return StarterClient.iris && net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getOceanShader()
         : null;
   }

   @Nullable
   public static ShaderInstance getOceanShadowProgram() {
      return StarterClient.iris && net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getOceanShadowShader()
         : null;
   }

   @Nullable
   public static ShaderInstance getLiquidProgram() {
      return StarterClient.iris && net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getLiquidShader()
         : null;
   }

   @Nullable
   public static ShaderInstance getLiquidShadowProgram() {
      return StarterClient.iris && net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getLiquidShadowShader()
         : null;
   }

   public static boolean renderOceanShadow() {
      return StarterClient.iris && net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$renderOceanShadow()
         : false;
   }

   public static boolean renderLiquidShadow() {
      return StarterClient.iris && net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$renderLiquidShadow()
         : false;
   }
}
