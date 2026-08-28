/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.FrustumIntersection
 *  org.joml.Math
 *  org.joml.Matrix3f
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.compat.Sodium;
import net.diebuddies.math.MatrixUtil;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.render.ClothRenderer;
import net.diebuddies.render.DebugRenderer;
import net.diebuddies.render.LiquidDeferredRenderer;
import net.diebuddies.render.LiquidRenderer;
import net.diebuddies.render.OceanRenderer;
import net.diebuddies.render.PhysicsUpdater;
import net.diebuddies.render.SmokeRenderer;
import net.diebuddies.render.SnowRenderer;
import net.diebuddies.render.TransparencyRenderer;
import net.diebuddies.render.shader.ParallaxShader;
import net.diebuddies.render.shader.ParallaxSlideShader;
import net.diebuddies.render.shader.SolidPhysicsShader;
import net.diebuddies.util.DoublyLinkedList;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.FrustumIntersection;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryUtil;

public class MainRenderer {
    private static final int DEFAULT_SIZE = 200;
    private static final Matrix4f IDENTITY_4_BY_4 = new Matrix4f();
    private static final Matrix3f IDENTITY_3_BY_3 = new Matrix3f();
    public static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    public static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    public static final Vector3f NETHER_DIFFUSE_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    public static final Vector3f NETHER_DIFFUSE_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.7f).normalize();
    public Vector3f lightDirection0 = new Vector3f();
    public Vector3f lightDirection1 = new Vector3f();
    public int size = 200;
    public float[] mpos = new float[this.size * 3];
    public int[] mcol = new int[this.size];
    public float[] muv = new float[this.size * 2];
    public int[] mnormals = new int[this.size];
    public float[] mmidtexcoord = new float[this.size * 2];
    public int[] mtangent = new int[this.size];
    public FrustumIntersection frustumInt = new FrustumIntersection();
    private Matrix4f viewProjectionMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f lastTextureMatrix;
    private Matrix4f transformation = new Matrix4f();
    private Matrix3f normalMatrix = new Matrix3f();
    private Matrix3f cameraNormalMatrix = new Matrix3f();
    private BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
    private Matrix4f currentPose = new Matrix4f();
    private float uniformRed;
    private float uniformGreen;
    private float uniformBlue;
    private float uniformAlpha;
    private boolean resetAttributes;
    private int lastBrightness;
    public int normalLocation = -1;
    public int specularLocation = -1;
    private volatile int failedVerificationCount;
    public long tickCountdown = 120000000000L;
    private long lastVerifTime;
    private PhysicsUpdater physicsUpdater = new PhysicsUpdater();
    private DebugRenderer debugRenderer = new DebugRenderer(this);
    private SmokeRenderer smokeRenderer = new SmokeRenderer(this);
    private SnowRenderer snowRenderer = new SnowRenderer(this);
    public OceanRenderer oceanRenderer = new OceanRenderer(this);
    public LiquidDeferredRenderer liquidDeferredRenderer;
    private LiquidRenderer liquidRenderer = new LiquidRenderer(this);
    private ClothRenderer clothRenderer;
    private TransparencyRenderer transparencyRenderer;
    private static ParallaxShader parallaxShader;
    private static ParallaxSlideShader parallaxSlideShader;
    public static SolidPhysicsShader solidPhysicsShader;
    public static FloatBuffer matrixBuffer;
    public static FloatBuffer identityMatrixBuffer;
    public static FloatBuffer vector4Buffer;
    public static FloatBuffer vector3Buffer;

    public MainRenderer() {
        this.liquidDeferredRenderer = new LiquidDeferredRenderer(this);
        this.transparencyRenderer = new TransparencyRenderer(this);
        this.clothRenderer = new ClothRenderer(this);
        if (matrixBuffer == null) {
            matrixBuffer = MemoryUtil.memAllocFloat((int)16);
            identityMatrixBuffer = MemoryUtil.memAllocFloat((int)16);
            vector4Buffer = MemoryUtil.memAllocFloat((int)4);
            vector3Buffer = MemoryUtil.memAllocFloat((int)3);
            IDENTITY_4_BY_4.get(identityMatrixBuffer);
        }
    }

    public void renderAll(ClientLevel level, RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        RenderSystem.assertOnRenderThread();
        this.verifyAndUtility();
        this.projectionMatrix.set((Matrix4fc)projectionMatrix);
        this.viewMatrix.set((Matrix4fc)viewMatrix);
        this.projectionMatrix.mul((Matrix4fc)this.viewMatrix, this.viewProjectionMatrix);
        this.frustumInt.set((Matrix4fc)this.viewProjectionMatrix, true);
        PhysicsMod.projectionMatrix.set((Matrix4fc)projectionMatrix);
        PhysicsMod.viewMatrix.set((Matrix4fc)viewMatrix);
        if (level != null) {
            Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            PhysicsMod mod = PhysicsMod.getInstance((Level)level);
            PhysicsWorld physics = mod.getPhysicsWorld();
            physics.updateLastSeen();
            this.physicsUpdater.updatePhysics(mod, level, view, physics);
            if (physics.getBodies().size() > 0 || physics.getRagdolls().size() > 0 || physics.getVerletSimulations().size() > 0 || physics.getSnowWorld().getChunks().size() > 0 || physics.getSmokeDomain().getAllParticles().size() > 0 || PhysicsMod.clothRenderFast.size() > 0) {
                PerformanceTracker.startNoFlush("blocks_mobs_particles_rendering");
                blockLayerIn.setupRenderState();
                this.bindProperShader();
                RenderSystem.setProjectionMatrix((Matrix4f)projectionMatrix, (VertexSorting)RenderSystem.getVertexSorting());
                Matrix4fStack matrixStackIn = RenderSystem.getModelViewStack();
                RenderSystem.enableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                matrixStackIn.pushMatrix();
                matrixStackIn.set((Matrix4fc)viewMatrix);
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                RenderSystem.activeTexture((int)33984);
                this.resetColor();
                this.setupShader(RenderSystem.getShader());
                VAO.storePreviouslyBoundState();
                matrixStackIn.normal(this.cameraNormalMatrix);
                this.lastTextureMatrix = null;
                this.resetAttributes = true;
                Vector3d physicsOffset = physics.getOffset();
                double offsetX = physicsOffset.x - view.x;
                double offsetY = physicsOffset.y - view.y;
                double offsetZ = physicsOffset.z - view.z;
                boolean isShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
                physics.bindForRendering();
                this.createPhysicsModels(physics);
                physics.bindForRendering();
                DoublyLinkedList<IRigidBody> bodies = physics.getBodies();
                Iterator<IRigidBody> it = bodies.iterator();
                int size = bodies.size();
                for (int i = 0; i < size; ++i) {
                    IRigidBody body = it.next();
                    PhysicsEntity entity = body.getEntity();
                    if (entity.models == null) continue;
                    this.setTransformation(physics, body, entity, isShadowPass);
                    this.render(physics, level, matrixStackIn, view, offsetX, offsetY, offsetZ, body, entity, false);
                }
                StateTracker.unbindVertexArray();
                PerformanceTracker.end("blocks_mobs_particles_rendering");
                this.snowRenderer.render(physics, level, matrixStackIn, view);
                this.smokeRenderer.render(physics, level, matrixStackIn, view);
                if (StarterClient.optifabric) {
                    BufferUploader.reset();
                }
                this.resetAttributes = true;
                this.transparencyRenderer.render(physics, level, matrixStackIn, view);
                if (RenderSystem.getShader().TEXTURE_MATRIX != null) {
                    RenderSystem.getShader().TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
                }
                VAO.restorePreviouslyBoundState();
                RenderSystem.getShader().clear();
                this.debugRenderer.render(physics, level, matrixStackIn, view);
                matrixStackIn.popMatrix();
                RenderSystem.activeTexture((int)33984);
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                RenderSystem.enableCull();
                blockLayerIn.clearRenderState();
                RenderSystem.applyModelViewMatrix();
                BufferUploader.reset();
            }
        }
    }

    public void resetColor() {
        this.uniformRed = -1.0f;
        this.uniformGreen = -1.0f;
        this.uniformBlue = -1.0f;
        this.uniformAlpha = -1.0f;
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public void bindProperShader() {
        boolean useCustomShader = true;
        if (StarterClient.iris && Iris.isExtending()) {
            useCustomShader = false;
        }
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            useCustomShader = false;
        }
        if (useCustomShader) {
            if (solidPhysicsShader == null) {
                try {
                    solidPhysicsShader = new SolidPhysicsShader();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            RenderSystem.setShader(() -> solidPhysicsShader);
        } else {
            RenderSystem.setShader(GameRenderer::getRendertypeArmorCutoutNoCullShader);
            if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
                Optifine.useEntityShader();
            }
        }
    }

    private void createPhysicsModels(PhysicsWorld physics) {
        Set<PhysicsEntity> bodies = physics.getQueueForModelCreation();
        Iterator<PhysicsEntity> it = bodies.iterator();
        int size = bodies.size();
        block0: for (int i = 0; i < size; ++i) {
            PhysicsEntity entity = it.next();
            List<Model> models = entity.models;
            if (entity.models == null) continue;
            int modelsSize = models.size();
            for (int j = 0; j < modelsSize; ++j) {
                Model model = models.get(j);
                boolean shade = entity.shade;
                Mesh mesh = model.mesh;
                if (mesh == null || mesh.indices.size() < 3) continue block0;
                if (model.memorySegment != null) continue;
                entity.getBoundingSphereRadius();
                model.createModelMemorySegment(physics, shade);
            }
        }
        bodies.clear();
    }

    public void setTransformation(PhysicsWorld physics, IRigidBody body, PhysicsEntity entity, boolean isShadowPass) {
        if (isShadowPass) {
            return;
        }
        if (body.hasTransformationChanged()) {
            MatrixUtil.slerpNoScale(entity, physics.getRenderPercent(), entity.getRenderTransformation());
        } else {
            entity.getRenderTransformation().set((Matrix4dc)entity.getTransformation());
        }
    }

    public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view, double offsetX, double offsetY, double offsetZ, IRigidBody body, PhysicsEntity particle, boolean renderTransparency) {
        List<Model> models = particle.models;
        Model modelZero = models.get(0);
        if (modelZero.memorySegment == null) {
            return;
        }
        Matrix4f renderTransformation = particle.getRenderTransformation();
        this.transformation.set3x3(particle.getRenderTransformation());
        float posx = (float)((double)renderTransformation.m30() + offsetX);
        float posy = (float)((double)renderTransformation.m31() + offsetY);
        float posz = (float)((double)renderTransformation.m32() + offsetZ);
        this.transformation.setTranslation(posx, posy, posz);
        if (!renderTransparency && !this.frustumInt.testSphere(posx, posy, posz, particle.getBoundingSphereRadius())) {
            return;
        }
        double animationScale = particle.getDespawnScale((Level)level);
        boolean alphaRender = false;
        if (particle.getAnimationType() == AnimationType.Vanish) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)Math.min((float)1.0f, (float)((float)animationScale)));
            if (animationScale < 1.0) {
                alphaRender = true;
            }
            animationScale = 1.0;
        } else if (particle.getAnimationType() == AnimationType.Shrink_and_Vanish) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)Math.min((float)1.0f, (float)((float)animationScale)));
            if (animationScale < 1.0) {
                alphaRender = true;
            }
        } else {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        if (alphaRender && !renderTransparency) {
            this.transparencyRenderer.addTransparentObject(body, Vector3f.lengthSquared((float)posx, (float)posy, (float)posz));
            return;
        }
        if (particle.backfaceCulling) {
            RenderSystem.enableCull();
        } else {
            RenderSystem.disableCull();
        }
        double scaleX = (animationScale *= (double)particle.scale) * (double)particle.scalePhysics.x;
        double scaleY = animationScale * (double)particle.scalePhysics.y;
        double scaleZ = animationScale * (double)particle.scalePhysics.z;
        this.transformation.scale((float)scaleX, (float)scaleY, (float)scaleZ);
        ShaderInstance shader = RenderSystem.getShader();
        this.blockPos.set((double)posx + view.x, (double)posy + view.y, (double)posz + view.z);
        matrixStackIn.mulAffine((Matrix4fc)this.transformation, this.currentPose);
        this.setupModelViewMatrix(shader, this.currentPose, this.cameraNormalMatrix, particle.shade);
        this.setupLighting(this.transformation, shader, level, particle.shade);
        int size = models.size();
        for (int j = 0; j < size; ++j) {
            Model model = models.get(j);
            int glID = model.textureID;
            RenderSystem.setShaderTexture((int)0, (int)glID);
            RenderSystem.activeTexture((int)33984);
            RenderSystem.bindTexture((int)glID);
            this.setupPBRTextures();
            if (model.animationSprite != null && StarterClient.sodium) {
                Sodium.markSpriteActive(model.animationSprite);
            }
            this.renderFast(physics, level, particle, model, this.blockPos);
        }
    }

    public void setupModelViewMatrix(ShaderInstance shader, Matrix4f transformation, @Nullable Matrix3f cameraNormalMatrix, boolean shade) {
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setModelViewMatrix(transformation);
        } else {
            int location = shader.MODEL_VIEW_MATRIX.getLocation();
            if (location != -1) {
                GL32C.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)transformation.get(matrixBuffer));
            }
        }
        if (StarterClient.iris) {
            if (shade) {
                Iris.setNormalMatrix(shader, transformation);
            } else {
                Iris.setNormalMatrix(shader, transformation, cameraNormalMatrix);
            }
        }
    }

    public void setupLighting(Matrix4f transformation, ShaderInstance shader, ClientLevel level, boolean shade) {
        if (shader.LIGHT0_DIRECTION != null) {
            if (shade) {
                Matrix3f lightMatrix = transformation.normal(this.normalMatrix).invert();
                if (shader.LIGHT0_DIRECTION != null || shader.LIGHT1_DIRECTION != null) {
                    if (level.effects().constantAmbientLight()) {
                        lightMatrix.transform((Vector3fc)NETHER_DIFFUSE_LIGHT_0, this.lightDirection0);
                        lightMatrix.transform((Vector3fc)NETHER_DIFFUSE_LIGHT_1, this.lightDirection1);
                    } else {
                        lightMatrix.transform((Vector3fc)DIFFUSE_LIGHT_0, this.lightDirection0);
                        lightMatrix.transform((Vector3fc)DIFFUSE_LIGHT_1, this.lightDirection1);
                    }
                    RenderSystem.shaderLightDirections[0] = this.lightDirection0;
                    RenderSystem.shaderLightDirections[1] = this.lightDirection1;
                }
            } else if (shader.LIGHT0_DIRECTION != null || shader.LIGHT1_DIRECTION != null) {
                if (level.effects().constantAmbientLight()) {
                    RenderSystem.shaderLightDirections[0] = NETHER_DIFFUSE_LIGHT_0;
                    RenderSystem.shaderLightDirections[1] = NETHER_DIFFUSE_LIGHT_1;
                } else {
                    RenderSystem.shaderLightDirections[0] = DIFFUSE_LIGHT_0;
                    RenderSystem.shaderLightDirections[1] = DIFFUSE_LIGHT_1;
                }
            }
        }
    }

    public void setupPBRTextures() {
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            int before = RenderSystem.getShaderTexture((int)0);
            Optifine.bindPBRTexture(before);
            RenderSystem.activeTexture((int)33984);
            RenderSystem.bindTexture((int)before);
        } else if (StarterClient.iris) {
            if (this.normalLocation != -1) {
                RenderSystem.activeTexture((int)(33984 + this.normalLocation));
                RenderSystem.bindTexture((int)Iris.getNormalTextureID());
            }
            if (this.specularLocation != -1) {
                RenderSystem.activeTexture((int)(33984 + this.specularLocation));
                RenderSystem.bindTexture((int)Iris.getSpecularTextureID());
            }
            RenderSystem.activeTexture((int)33984);
        }
    }

    private void renderFast(PhysicsWorld physics, ClientLevel level, PhysicsEntity particle, Model model, BlockPos.MutableBlockPos blockPos) {
        if (model.memorySegment == null) {
            return;
        }
        int brightness = particle.getLight((Level)level, blockPos);
        if (this.resetAttributes) {
            GL32C.glVertexAttribI2ui((int)Data.LIGHT.getAttribute(), (int)(brightness & 0xF0), (int)(brightness >> 16 & 0xF0));
            GL32C.glVertexAttribI2ui((int)Data.OVERLAY.getAttribute(), (int)0, (int)10);
            this.resetAttributes = false;
        } else if (this.lastBrightness != brightness) {
            GL32C.glVertexAttribI2ui((int)Data.LIGHT.getAttribute(), (int)(brightness & 0xF0), (int)(brightness >> 16 & 0xF0));
        }
        this.lastBrightness = brightness;
        ShaderInstance shader = RenderSystem.getShader();
        float[] shaderColor = RenderSystem.getShaderColor();
        shaderColor[0] = shaderColor[0] * particle.getRed();
        shaderColor[1] = shaderColor[1] * particle.getGreen();
        shaderColor[2] = shaderColor[2] * particle.getBlue();
        this.setupShaderUniforms(shader, model.textureMatrix);
        ArenaBuffer.MemorySegment segment = model.memorySegment;
        GL32C.glDrawArrays((int)4, (int)(segment.offset / physics.format.getStride()), (int)(segment.size / physics.format.getStride()));
    }

    public void setupShaderUniforms(ShaderInstance shader) {
        this.setupShaderUniforms(shader, null);
    }

    public void setupShaderUniforms(ShaderInstance shader, @Nullable Matrix4f textureMatrix) {
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setColorModulator(RenderSystem.getShaderColor());
            Matrix4f upload = IDENTITY_4_BY_4;
            if (textureMatrix != null) {
                upload = textureMatrix;
            }
            if (upload != this.lastTextureMatrix) {
                if (upload == IDENTITY_4_BY_4) {
                    Optifine.setTextureMatrix(IDENTITY_4_BY_4);
                } else {
                    Optifine.setTextureMatrix(upload);
                }
                this.lastTextureMatrix = upload;
            }
        } else {
            float[] shaderColor;
            int location;
            if (shader.LIGHT0_DIRECTION != null && (location = shader.LIGHT0_DIRECTION.getLocation()) != -1) {
                GL32C.glUniform3fv((int)location, (FloatBuffer)RenderSystem.shaderLightDirections[0].get(vector3Buffer));
            }
            if (shader.LIGHT1_DIRECTION != null && (location = shader.LIGHT1_DIRECTION.getLocation()) != -1) {
                GL32C.glUniform3fv((int)location, (FloatBuffer)RenderSystem.shaderLightDirections[1].get(vector3Buffer));
            }
            if (shader.COLOR_MODULATOR != null && (location = shader.COLOR_MODULATOR.getLocation()) != -1 && ((shaderColor = RenderSystem.getShaderColor())[0] != this.uniformRed || shaderColor[1] != this.uniformGreen || shaderColor[2] != this.uniformBlue || shaderColor[3] != this.uniformAlpha)) {
                this.uniformRed = shaderColor[0];
                this.uniformGreen = shaderColor[1];
                this.uniformBlue = shaderColor[2];
                this.uniformAlpha = shaderColor[3];
                FloatBuffer colorBuffer = vector4Buffer;
                long pointer = MemoryUtil.memAddress((FloatBuffer)colorBuffer);
                MemoryUtil.memPutFloat((long)pointer, (float)this.uniformRed);
                MemoryUtil.memPutFloat((long)(pointer + 4L), (float)this.uniformGreen);
                MemoryUtil.memPutFloat((long)(pointer + 8L), (float)this.uniformBlue);
                MemoryUtil.memPutFloat((long)(pointer + 12L), (float)this.uniformAlpha);
                GL32C.glUniform4fv((int)location, (FloatBuffer)colorBuffer);
            }
            if (shader.TEXTURE_MATRIX != null && (location = shader.TEXTURE_MATRIX.getLocation()) != -1) {
                Matrix4f upload = IDENTITY_4_BY_4;
                if (textureMatrix != null) {
                    upload = textureMatrix;
                }
                if (upload != this.lastTextureMatrix) {
                    if (upload == IDENTITY_4_BY_4) {
                        GL32C.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)identityMatrixBuffer);
                    } else {
                        GL32C.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)upload.get(matrixBuffer));
                    }
                    this.lastTextureMatrix = upload;
                }
            }
        }
    }

    public void setupShader(ShaderInstance shader) {
        Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
        for (int n = 0; n < 8; ++n) {
            int o = RenderSystem.getShaderTexture((int)n);
            shader.setSampler("Sampler" + n, (Object)o);
        }
        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
        }
        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
        }
        if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }
        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if (shader.TEXTURE_MATRIX != null) {
            shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }
        if (shader.GAME_TIME != null) {
            shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }
        if (shader.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            shader.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
        }
        RenderSystem.setupShaderLights((ShaderInstance)shader);
        if (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal()) {
            shader.apply();
        }
        if (StarterClient.iris) {
            this.normalLocation = -1;
            this.specularLocation = -1;
            int program = GL32C.glGetInteger((int)35725);
            int normalUniformLocation = GL32C.glGetUniformLocation((int)program, (CharSequence)"normals");
            int specularUniformLocation = GL32C.glGetUniformLocation((int)program, (CharSequence)"specular");
            if (normalUniformLocation != -1) {
                this.normalLocation = GL32C.glGetUniformi((int)program, (int)normalUniformLocation);
            }
            if (specularUniformLocation != -1) {
                this.specularLocation = GL32C.glGetUniformi((int)program, (int)specularUniformLocation);
            }
        }
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setModelViewMatrix(RenderSystem.getModelViewMatrix());
            Optifine.setProjectionMatrix(RenderSystem.getProjectionMatrix());
            Optifine.setTextureMatrix(RenderSystem.getTextureMatrix());
            Optifine.setColorModulator(RenderSystem.getShaderColor());
        }
    }

    public void checkArrays(int neededSize) {
        boolean changed = false;
        while (neededSize > this.size) {
            this.size *= 2;
            changed = true;
        }
        if (changed) {
            this.mpos = new float[this.size * 3];
            this.mcol = new int[this.size];
            this.muv = new float[this.size * 2];
            this.mnormals = new int[this.size];
            this.mmidtexcoord = new float[this.size * 2];
            this.mtangent = new int[this.size];
        }
    }

    public void renderCloth(ClientLevel level, RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        this.clothRenderer.render(level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
    }

    public void renderLiquid(ClientLevel level, RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        this.liquidRenderer.render(level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
    }

    public void verifyAndUtility() {
        if (StarterClient.updateMessage != null && !StarterClient.updateMessage.isBlank() && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage((Component)Component.Serializer.fromJson((String)StarterClient.updateMessage, (HolderLookup.Provider)Minecraft.getInstance().player.registryAccess()), false);
            StarterClient.updateMessage = "";
        }
        if (StarterClient.customMessage != null && !StarterClient.customMessage.isBlank() && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage((Component)Component.Serializer.fromJson((String)StarterClient.customMessage, (HolderLookup.Provider)Minecraft.getInstance().player.registryAccess()), false);
            StarterClient.customMessage = "";
        }
    }

    public static ParallaxShader getParallaxShader() {
        if (parallaxShader == null) {
            try {
                parallaxShader = new ParallaxShader();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parallaxShader;
    }

    public static ParallaxSlideShader getParallaxSlideShader() {
        if (parallaxSlideShader == null) {
            try {
                parallaxSlideShader = new ParallaxSlideShader();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parallaxSlideShader;
    }

    public static void destroy() {
        if (solidPhysicsShader != null) {
            solidPhysicsShader.close();
        }
        if (parallaxShader != null) {
            parallaxShader.close();
        }
        if (parallaxSlideShader != null) {
            parallaxSlideShader.close();
        }
        SmokeRenderer.destroy();
        TransparencyRenderer.destroy();
        OceanRenderer.destroy();
        LiquidDeferredRenderer.destroy();
        if (matrixBuffer != null) {
            MemoryUtil.memFree((Buffer)matrixBuffer);
        }
        if (identityMatrixBuffer != null) {
            MemoryUtil.memFree((Buffer)identityMatrixBuffer);
        }
        if (vector4Buffer != null) {
            MemoryUtil.memFree((Buffer)vector4Buffer);
        }
        if (vector3Buffer != null) {
            MemoryUtil.memFree((Buffer)vector3Buffer);
        }
    }
}

