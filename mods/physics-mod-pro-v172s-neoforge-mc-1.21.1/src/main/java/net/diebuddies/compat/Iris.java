/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Uniform
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  javax.annotation.Nullable
 *  net.irisshaders.iris.Iris
 *  net.irisshaders.iris.api.v0.IrisApi
 *  net.irisshaders.iris.pathways.HandRenderer
 *  net.irisshaders.iris.pipeline.WorldRenderingPipeline
 *  net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
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
                Object2IntMap idMap = WorldRenderingSettings.INSTANCE.getBlockStateIds();
                if (idMap != null) {
                    return (short)idMap.getOrDefault((Object)block, -1);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
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
        if (StarterClient.iris) {
            return IrisApi.getInstance().isShaderPackInUse();
        }
        return false;
    }

    public static boolean isShadowPass() {
        if (StarterClient.iris) {
            return IrisApi.getInstance().isRenderingShadowPass();
        }
        return false;
    }

    public static int getSpecularTextureID() {
        if (StarterClient.iris) {
            WorldRenderingPipeline pipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable();
            return pipeline.getCurrentSpecularTexture();
        }
        return 0;
    }

    public static int getNormalTextureID() {
        if (StarterClient.iris) {
            WorldRenderingPipeline pipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable();
            return pipeline.getCurrentNormalTexture();
        }
        return 0;
    }

    public static void setNormalMatrix(ShaderInstance shader, Matrix4f modelViewMatrix) {
        if (shader instanceof MixinExtendedShaderAccessor) {
            MixinExtendedShaderAccessor extended = (MixinExtendedShaderAccessor)shader;
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
        if (shader instanceof MixinExtendedShaderAccessor) {
            MixinExtendedShaderAccessor extended = (MixinExtendedShaderAccessor)shader;
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
        WorldRenderingPipeline worldRenderingPipeline;
        if (StarterClient.iris && (worldRenderingPipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable()) instanceof PhysicsExtendedPipeline) {
            PhysicsExtendedPipeline extended = (PhysicsExtendedPipeline)worldRenderingPipeline;
            return extended.physicsmod$getOceanShader();
        }
        return null;
    }

    @Nullable
    public static ShaderInstance getOceanShadowProgram() {
        WorldRenderingPipeline worldRenderingPipeline;
        if (StarterClient.iris && (worldRenderingPipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable()) instanceof PhysicsExtendedPipeline) {
            PhysicsExtendedPipeline extended = (PhysicsExtendedPipeline)worldRenderingPipeline;
            return extended.physicsmod$getOceanShadowShader();
        }
        return null;
    }

    @Nullable
    public static ShaderInstance getLiquidProgram() {
        WorldRenderingPipeline worldRenderingPipeline;
        if (StarterClient.iris && (worldRenderingPipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable()) instanceof PhysicsExtendedPipeline) {
            PhysicsExtendedPipeline extended = (PhysicsExtendedPipeline)worldRenderingPipeline;
            return extended.physicsmod$getLiquidShader();
        }
        return null;
    }

    @Nullable
    public static ShaderInstance getLiquidShadowProgram() {
        WorldRenderingPipeline worldRenderingPipeline;
        if (StarterClient.iris && (worldRenderingPipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable()) instanceof PhysicsExtendedPipeline) {
            PhysicsExtendedPipeline extended = (PhysicsExtendedPipeline)worldRenderingPipeline;
            return extended.physicsmod$getLiquidShadowShader();
        }
        return null;
    }

    public static boolean renderOceanShadow() {
        WorldRenderingPipeline worldRenderingPipeline;
        if (StarterClient.iris && (worldRenderingPipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable()) instanceof PhysicsExtendedPipeline) {
            PhysicsExtendedPipeline extended = (PhysicsExtendedPipeline)worldRenderingPipeline;
            return extended.physicsmod$renderOceanShadow();
        }
        return false;
    }

    public static boolean renderLiquidShadow() {
        WorldRenderingPipeline worldRenderingPipeline;
        if (StarterClient.iris && (worldRenderingPipeline = net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable()) instanceof PhysicsExtendedPipeline) {
            PhysicsExtendedPipeline extended = (PhysicsExtendedPipeline)worldRenderingPipeline;
            return extended.physicsmod$renderLiquidShadow();
        }
        return false;
    }
}

