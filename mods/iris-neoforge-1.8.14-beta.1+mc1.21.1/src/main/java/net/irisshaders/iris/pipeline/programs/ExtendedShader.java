/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.preprocessor.GlslPreprocessor
 *  com.mojang.blaze3d.shaders.Program
 *  com.mojang.blaze3d.shaders.Program$Type
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Shader
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.irisshaders.iris.pipeline.programs;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.program.IrisProgramTypes;
import net.irisshaders.iris.gl.program.ProgramImages;
import net.irisshaders.iris.gl.program.ProgramSamplers;
import net.irisshaders.iris.gl.program.ProgramUniforms;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.gl.uniform.DynamicLocationalUniformHolder;
import net.irisshaders.iris.mixinterface.ShaderInstanceInterface;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.samplers.IrisSamplers;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ExtendedShader
extends ShaderInstance
implements ShaderInstanceInterface {
    private static final Matrix4f IDENTITY = new Matrix4f().identity();
    private static final Uniform FAKE_UNIFORM = new Uniform("", 1, 2, null);
    private final boolean intensitySwizzle;
    private final List<BufferBlendOverride> bufferBlendOverrides;
    private final boolean hasOverrides;
    private final Uniform modelViewInverse;
    private final Uniform projectionInverse;
    private final Uniform normalMatrix;
    private final CustomUniforms customUniforms;
    private final IrisRenderingPipeline parent;
    private final ProgramUniforms uniforms;
    private final ProgramSamplers samplers;
    private final ProgramImages images;
    private final GlFramebuffer writingToBeforeTranslucent;
    private final GlFramebuffer writingToAfterTranslucent;
    private final BlendModeOverride blendModeOverride;
    private final float alphaTest;
    private final boolean usesTessellation;
    private final Matrix4f tempMatrix4f = new Matrix4f();
    private final Matrix3f tempMatrix3f = new Matrix3f();
    private final float[] tempFloats = new float[16];
    private final float[] tempFloats2 = new float[9];
    private Program geometry;
    private Program tessControl;
    private Program tessEval;

    public ExtendedShader(ResourceProvider resourceFactory, String name, VertexFormat vertexFormat, boolean usesTessellation, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent, BlendModeOverride blendModeOverride, AlphaTest alphaTest, Consumer<DynamicLocationalUniformHolder> uniformCreator, BiConsumer<SamplerHolder, ImageHolder> samplerCreator, boolean isIntensity, IrisRenderingPipeline parent, @Nullable List<BufferBlendOverride> bufferBlendOverrides, CustomUniforms customUniforms) throws IOException {
        super(resourceFactory, name, vertexFormat);
        this.setupDebugNames(name);
        ProgramUniforms.Builder uniformBuilder = ProgramUniforms.builder(name, this.getId());
        ProgramSamplers.Builder samplerBuilder = ProgramSamplers.builder(this.getId(), IrisSamplers.WORLD_RESERVED_TEXTURE_UNITS);
        ProgramImages.Builder imageBuilder = ProgramImages.builder(this.getId());
        uniformCreator.accept(uniformBuilder);
        samplerCreator.accept(samplerBuilder, imageBuilder);
        customUniforms.mapholderToPass(uniformBuilder, this);
        this.uniforms = uniformBuilder.buildUniforms();
        this.samplers = samplerBuilder.build();
        this.images = imageBuilder.build();
        this.usesTessellation = usesTessellation;
        this.writingToBeforeTranslucent = writingToBeforeTranslucent;
        this.writingToAfterTranslucent = writingToAfterTranslucent;
        this.blendModeOverride = blendModeOverride;
        this.bufferBlendOverrides = bufferBlendOverrides;
        this.hasOverrides = bufferBlendOverrides != null && !bufferBlendOverrides.isEmpty();
        this.alphaTest = alphaTest.reference();
        this.parent = parent;
        this.customUniforms = customUniforms;
        this.intensitySwizzle = isIntensity;
        this.modelViewInverse = this.getUniform("ModelViewMatInverse");
        this.projectionInverse = this.getUniform("ProjMatInverse");
        this.normalMatrix = this.getUniform("NormalMat");
    }

    private void setupDebugNames(String name) {
        GLDebug.nameObject(33505, this.getVertexProgram().getId(), name + "_vertex.vsh");
        GLDebug.nameObject(33505, this.getFragmentProgram().getId(), name + "_fragment.fsh");
        GLDebug.nameObject(33506, this.getId(), name);
    }

    public void clear() {
        ProgramUniforms.clearActiveUniforms();
        ProgramSamplers.clearActiveSamplers();
        if (this.blendModeOverride != null || this.hasOverrides) {
            BlendModeOverride.restore();
        }
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public void apply() {
        CapturedRenderingState.INSTANCE.setCurrentAlphaTest(this.alphaTest);
        ProgramManager.glUseProgram((int)this.getId());
        this.setupTextures();
        this.updateMatrices();
        this.updateUniforms();
        this.applyBlendModes();
        this.bindFramebuffer();
    }

    private void setupTextures() {
        if (this.intensitySwizzle) {
            IrisRenderSystem.texParameteriv(RenderSystem.getShaderTexture((int)0), TextureType.TEXTURE_2D.getGlType(), 36422, new int[]{6403, 6403, 6403, 6403});
        }
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 0, RenderSystem.getShaderTexture((int)0));
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 1, RenderSystem.getShaderTexture((int)1));
        IrisRenderSystem.bindTextureToUnit(TextureType.TEXTURE_2D.getGlType(), 2, RenderSystem.getShaderTexture((int)2));
        ImmediateState.usingTessellation = this.usesTessellation;
    }

    private void updateMatrices() {
        if (this.PROJECTION_MATRIX != null && this.projectionInverse != null) {
            this.projectionInverse.set(this.tempMatrix4f.set(this.PROJECTION_MATRIX.getFloatBuffer()).invert().get(this.tempFloats));
        } else if (this.projectionInverse != null) {
            this.projectionInverse.set(IDENTITY);
        }
        if (this.MODEL_VIEW_MATRIX != null) {
            if (this.modelViewInverse != null) {
                this.modelViewInverse.set(this.tempMatrix4f.set(this.MODEL_VIEW_MATRIX.getFloatBuffer()).invert().get(this.tempFloats));
            }
            if (this.normalMatrix != null) {
                this.normalMatrix.set(this.tempMatrix3f.set((Matrix4fc)this.tempMatrix4f.set(this.MODEL_VIEW_MATRIX.getFloatBuffer())).invert().transpose().get(this.tempFloats2));
            }
        }
    }

    private void updateUniforms() {
        this.uploadIfNotNull(this.projectionInverse);
        this.uploadIfNotNull(this.modelViewInverse);
        this.uploadIfNotNull(this.normalMatrix);
        ((ShaderInstance)this).uniforms.forEach(this::uploadIfNotNull);
        this.samplers.update();
        this.uniforms.update();
        this.customUniforms.push(this);
        this.images.update();
    }

    private void applyBlendModes() {
        if (this.blendModeOverride != null) {
            this.blendModeOverride.apply();
        }
        if (this.hasOverrides) {
            this.bufferBlendOverrides.forEach(BufferBlendOverride::apply);
        }
    }

    private void bindFramebuffer() {
        if (this.parent.isBeforeTranslucent) {
            this.writingToBeforeTranslucent.bind();
        } else {
            this.writingToAfterTranslucent.bind();
        }
    }

    @Nullable
    public Uniform getUniform(@NotNull String name) {
        Uniform uniform = super.getUniform("iris_" + name);
        if (uniform == null && (name.equalsIgnoreCase("OverlayUV") || name.equalsIgnoreCase("LightUV"))) {
            return FAKE_UNIFORM;
        }
        return uniform;
    }

    private void uploadIfNotNull(Uniform uniform) {
        if (uniform != null) {
            uniform.upload();
        }
    }

    public void attachToProgram() {
        super.attachToProgram();
        this.attachExtraShaders();
    }

    private void attachExtraShaders() {
        if (this.geometry != null) {
            this.geometry.attachToShader((Shader)this);
        }
        if (this.tessControl != null) {
            this.tessControl.attachToShader((Shader)this);
        }
        if (this.tessEval != null) {
            this.tessEval.attachToShader((Shader)this);
        }
    }

    @Override
    public void iris$createExtraShaders(ResourceProvider factory, String name) {
        this.createGeometryShader(factory, name);
        this.createTessControlShader(factory, name);
        this.createTessEvalShader(factory, name);
    }

    @Override
    public void setShouldSkip(MethodHandle s) {
    }

    private void createGeometryShader(ResourceProvider factory, String name) {
        this.createShader(factory, name, "_geometry.gsh", IrisProgramTypes.GEOMETRY, program -> {
            this.geometry = program;
        });
    }

    private void createTessControlShader(ResourceProvider factory, String name) {
        this.createShader(factory, name, "_tessControl.tcs", IrisProgramTypes.TESS_CONTROL, program -> {
            this.tessControl = program;
        });
    }

    private void createTessEvalShader(ResourceProvider factory, String name) {
        this.createShader(factory, name, "_tessEval.tes", IrisProgramTypes.TESS_EVAL, program -> {
            this.tessEval = program;
        });
    }

    private void createShader(ResourceProvider factory, String name, String suffix, Program.Type type, Consumer<Program> programSetter) {
        factory.getResource(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)(name + suffix))).ifPresent(resource -> {
            try {
                Program program = Program.compileShader((Program.Type)type, (String)name, (InputStream)resource.open(), (String)resource.sourcePackId(), (GlslPreprocessor)new GlslPreprocessor(this){

                    @Nullable
                    public String applyImport(boolean bl, String string) {
                        return null;
                    }
                });
                GLDebug.nameObject(33505, program.getId(), name + suffix);
                programSetter.accept(program);
            }
            catch (IOException e) {
                Iris.logger.error("Failed to create shader program", e);
            }
        });
    }

    public Program getGeometry() {
        return this.geometry;
    }

    public Program getTessControl() {
        return this.tessControl;
    }

    public Program getTessEval() {
        return this.tessEval;
    }

    public boolean hasActiveImages() {
        return this.images.getActiveImages() > 0;
    }
}

