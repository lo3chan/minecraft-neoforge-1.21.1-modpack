/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.BiomeColors
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Style
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.system.MemoryStack
 */
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
import net.diebuddies.render.MainRenderer;
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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.BlockAndTintGetter;
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
    private static float[] liquidpos;
    private static float[] liquidposnew;
    private static int[] viewport;
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
        boolean skipShadowPass;
        if (!ConfigClient.liquidPhysics) {
            return;
        }
        TextureAtlasSprite waterTexture = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        this.waterID = Minecraft.getInstance().getTextureManager().getTexture(waterTexture.atlasLocation()).getId();
        this.waterBounds.set(waterTexture.getU0(), waterTexture.getU1(), waterTexture.getV0(), waterTexture.getV1());
        this.waterMidCoord.set(this.waterBounds.x + this.waterBounds.y, this.waterBounds.z + this.waterBounds.w).mul(0.5f);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !Iris.liquidsError.isEmpty()) {
            player.displayClientMessage((Component)Component.literal((String)Iris.liquidsError).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
            Iris.liquidsError = "";
        }
        RenderSystem.activeTexture((int)33984);
        int boundBefore = GL32C.glGetInteger((int)32873);
        int previousProgram = GL32C.glGetInteger((int)35725);
        int oldBlendEquationRGB = GL32C.glGetInteger((int)32777);
        GL32C.glGetIntegerv((int)2978, (int[])viewport);
        if (liquidVAO == null) {
            liquidVAO = this.createLiquidVAO(PhysicsMod.liquid);
            emptyVAO = new VAO();
            liquidShader = new LiquidShader();
            emptyTextureShader = new EmptyTextureShader();
        }
        boolean isShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
        boolean bl = skipShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() && !Iris.renderLiquidShadow();
        if (!isShadowPass) {
            this.updateLiquidInstances(physics, cameraPos);
        }
        if (this.liquidCount == 0 || skipShadowPass) {
            return;
        }
        if (!isShadowPass) {
            this.renderLiquidDataIntoTexture(physics, level, cameraPos);
        } else {
            this.renderInstancedLiquidSpheres(physics, level, matrixStackIn, cameraPos);
        }
        GL32C.glUseProgram((int)previousProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendEquation((int)oldBlendEquationRGB);
        RenderSystem.activeTexture((int)33984);
        GL32C.glBindTexture((int)3553, (int)boundBefore);
        StateTracker.unbindVertexArray();
    }

    private void renderInstancedLiquidSpheres(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 cameraPos) {
        int location;
        this.bindLiquidsShader();
        ShaderInstance shaderInstance = RenderSystem.getShader();
        this.initRenderingStates(level, shaderInstance, true);
        this.setupLiquidsRendering(physics, level, shaderInstance, this.waterID);
        RenderSystem.enableCull();
        if (!(StarterClient.optifabric && Optifine.isUsingShadersNoInternal() || (location = shaderInstance.MODEL_VIEW_MATRIX.getLocation()) == -1)) {
            GL32C.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)matrixStackIn.get(MainRenderer.matrixBuffer));
        }
        if (StarterClient.iris) {
            Iris.setNormalMatrix(shaderInstance, (Matrix4f)matrixStackIn);
        }
        Vector3d offset = physics.getOffset();
        this.offsetCamera.set(cameraPos.x - offset.x, cameraPos.y - offset.y, cameraPos.z - offset.z);
        int shaderId = GL32C.glGetInteger((int)35725);
        int cameraLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_liquidCameraPos");
        int renderPercentLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_renderPercent");
        if (cameraLocation != -1) {
            GL32C.glUniform3f((int)cameraLocation, (float)((float)this.offsetCamera.x), (float)((float)this.offsetCamera.y), (float)((float)this.offsetCamera.z));
        }
        if (renderPercentLocation != -1) {
            GL32C.glUniform1f((int)renderPercentLocation, (float)((float)physics.getRenderPercent()));
        }
        liquidVAO.renderInstanced(this.liquidCount);
    }

    private void renderLiquidDataIntoTexture(PhysicsWorld physics, ClientLevel level, Vec3 cameraPos) {
        int drawFboBoundBefore = GL32C.glGetInteger((int)36006);
        int readFboBoundBefore = GL32C.glGetInteger((int)36010);
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
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableBlend();
        Vector3d offset = physics.getOffset();
        this.offsetCamera.set(cameraPos.x - offset.x, cameraPos.y - offset.y, cameraPos.z - offset.z);
        LiquidShader shader = liquidShader;
        shader.bind();
        shader.uploadMatrix(shader.getUniformLocation("ModelViewMat"), RenderSystem.getModelViewMatrix());
        shader.uploadMatrix(shader.getUniformLocation("ProjMat"), RenderSystem.getProjectionMatrix());
        shader.setUniform3(shader.getUniformLocation("LiquidCameraPos"), (float)this.offsetCamera.x, (float)this.offsetCamera.y, (float)this.offsetCamera.z);
        double renderPercent = physics.getRenderPercent();
        shader.setUniform1(shader.getUniformLocation("RenderPercent"), (float)renderPercent);
        liquidVAO.renderInstanced(this.liquidCount);
        for (int i = 0; i < ConfigClient.cudaLiquidsBlurPasses; ++i) {
            blurEffect.render(emptyVAO);
        }
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        GL32C.glBindFramebuffer((int)36009, (int)drawFboBoundBefore);
        GL32C.glBindFramebuffer((int)36008, (int)readFboBoundBefore);
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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            RenderSystem.setShader(() -> liquidCompositeShader);
            this.mainRenderer.setupShader(liquidCompositeShader);
        }
    }

    private void initRenderingStates(ClientLevel level, ShaderInstance shader, boolean shadowPass) {
        if (StarterClient.optifabric) {
            GL32C.glVertexAttrib3f((int)Data.NORMAL.getAttribute(), (float)0.0f, (float)1.0f, (float)0.0f);
        } else {
            GL32C.glVertexAttrib3f((int)Data.NORMAL_SHADER.getAttribute(), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        int color = BiomeColors.getAverageWaterColor((BlockAndTintGetter)level, (BlockPos)Minecraft.getInstance().player.blockPosition());
        GL32C.glVertexAttrib4f((int)Data.COLOR_SHADER.getAttribute(), (float)Pack.getRed(color), (float)Pack.getGreen(color), (float)Pack.getBlue(color), (float)1.0f);
        GL32C.glVertexAttribI2i((int)Data.LIGHT_SHADER.getAttribute(), (int)240, (int)240);
        GL32C.glVertexAttrib2f((int)Data.TEX_COORD_SHADER.getAttribute(), (float)this.waterMidCoord.x, (float)this.waterMidCoord.y);
        if (StarterClient.iris) {
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), (float)this.waterMidCoord.x, (float)this.waterMidCoord.y);
            GL32C.glVertexAttrib4f((int)Data.TANGENT_TERRAIN_SHADER.getAttribute(), (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f);
            this.mcEntityLocation = GL20.glGetAttribLocation((int)RenderSystem.getShader().getId(), (CharSequence)"mc_Entity");
            if (this.mcEntityLocation != -1) {
                GL32C.glVertexAttrib4f((int)this.mcEntityLocation, (float)Iris.getMaterialID(Blocks.WATER.defaultBlockState()), (float)1.0f, (float)-1.0f, (float)-1.0f);
            }
        } else if (StarterClient.optifabric) {
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_OPTIFINE.getAttribute(), (float)this.waterMidCoord.x, (float)this.waterMidCoord.y);
            GL32C.glVertexAttrib4f((int)Data.TANGENT_OPTIFINE.getAttribute(), (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f);
            this.mcEntityLocation = Optifine.isUsingShadersNoInternal() ? 11 : GL20.glGetAttribLocation((int)RenderSystem.getShader().getId(), (CharSequence)"mc_Entity");
            if (this.mcEntityLocation != -1) {
                GL32C.glVertexAttrib4f((int)this.mcEntityLocation, (float)Optifine.getMaterialID(Blocks.WATER.defaultBlockState()), (float)Optifine.getRenderType(Blocks.WATER.defaultBlockState()), (float)-1.0f, (float)-1.0f);
            }
        }
        if (!shadowPass) {
            RenderSystem.activeTexture((int)(33984 + this.depthActiveTexture));
            GL32C.glBindTexture((int)3553, (int)depth.getID());
        }
        RenderSystem.activeTexture((int)33984);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void setupLiquidsRendering(PhysicsWorld physics, ClientLevel level, ShaderInstance shader, int waterTexture) {
        FloatBuffer matrixBuffer;
        MemoryStack stack;
        if (level.effects().constantAmbientLight()) {
            RenderSystem.shaderLightDirections[0] = MainRenderer.NETHER_DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.NETHER_DIFFUSE_LIGHT_1;
        } else {
            RenderSystem.shaderLightDirections[0] = MainRenderer.DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.DIFFUSE_LIGHT_1;
        }
        RenderSystem.setupShaderLights((ShaderInstance)shader);
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
        int shaderId = GL32C.glGetInteger((int)35725);
        int depthLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_depth");
        int invProjLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_invProjectionMatrix");
        int invViewLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_invViewMatrix");
        if (depthLocation != -1) {
            GL32C.glUniform1i((int)depthLocation, (int)this.depthActiveTexture);
        }
        if (invProjLocation != -1) {
            stack = MemoryStack.stackPush();
            try {
                matrixBuffer = stack.mallocFloat(16);
                RenderSystem.getProjectionMatrix().invert(this.tmpMatrix).get(matrixBuffer);
                GL32C.glUniformMatrix4fv((int)invProjLocation, (boolean)false, (FloatBuffer)matrixBuffer);
            }
            finally {
                if (stack != null) {
                    stack.close();
                }
            }
        }
        if (invViewLocation != -1) {
            stack = MemoryStack.stackPush();
            try {
                matrixBuffer = stack.mallocFloat(16);
                RenderSystem.getModelViewMatrix().invert(this.tmpMatrix).get(matrixBuffer);
                GL32C.glUniformMatrix4fv((int)invViewLocation, (boolean)false, (FloatBuffer)matrixBuffer);
            }
            finally {
                if (stack != null) {
                    stack.close();
                }
            }
        }
        RenderSystem.setShaderTexture((int)0, (int)waterTexture);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)waterTexture);
    }

    private VAO createLiquidVAO(Mesh mesh) {
        int size = mesh.indices.size();
        net.diebuddies.opengl.Mesh openglMesh = new net.diebuddies.opengl.Mesh();
        this.mainRenderer.checkArrays(size);
        for (int i = 0; i < size; ++i) {
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
        int size;
        boolean changed = false;
        for (size = liquidpos.length; neededSize > size; size *= 2) {
            changed = true;
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
            LiquidCuda liquid;
            int i;
            for (i = 0; i < physics.getLiquids().size(); ++i) {
                liquid = (LiquidCuda)physics.getLiquids().get(i);
                size += liquid.cudaPositions.length;
            }
            this.checkLiquidArrays(size * 4);
            for (i = 0; i < physics.getLiquids().size(); ++i) {
                liquid = (LiquidCuda)physics.getLiquids().get(i);
                float[] positions = liquid.cudaPositions;
                float[] oldPositions = liquid.cudaOldPositions;
                for (int j = 0; j < liquid.cudaParticleSize; ++j) {
                    this.prepareLiquidCudaInstances(physics, positions, oldPositions, j, this.offsetCamera, count++);
                }
            }
            this.liquidCount = count;
        } else {
            Liquid liquid;
            int i;
            for (i = 0; i < physics.getLiquids().size(); ++i) {
                liquid = physics.getLiquids().get(i);
                size += liquid.particles.size();
            }
            this.checkLiquidArrays(size * 4);
            for (i = 0; i < physics.getLiquids().size(); ++i) {
                liquid = physics.getLiquids().get(i);
                for (IRigidBody body : liquid.particles) {
                    this.prepareLiquidInstances(physics, body, this.offsetCamera, count++);
                }
            }
            this.liquidCount = count;
        }
        if (count == 0) {
            return;
        }
        liquidVAO.bind();
        liquidVAO.updateAttribute(Data.LIQUID_POS, liquidpos, count * 4);
        liquidVAO.updateAttribute(Data.LIQUID_POS_NEW, liquidposnew, count * 4);
    }

    private boolean prepareLiquidInstances(PhysicsWorld physics, IRigidBody body, Vector3d cameraPos, int offset) {
        int mulOffset = offset * 4;
        PhysicsEntity particle = body.getEntity();
        Matrix4d transformation = particle.getTransformation();
        Matrix4d transformationOld = particle.getOldTransformation();
        float scale = particle.getDespawnScale(physics.getWorld());
        LiquidDeferredRenderer.liquidpos[mulOffset] = (float)transformationOld.m30();
        LiquidDeferredRenderer.liquidpos[mulOffset + 1] = (float)transformationOld.m31();
        LiquidDeferredRenderer.liquidpos[mulOffset + 2] = (float)transformationOld.m32();
        LiquidDeferredRenderer.liquidpos[mulOffset + 3] = 1.0f;
        LiquidDeferredRenderer.liquidposnew[mulOffset] = (float)transformation.m30();
        LiquidDeferredRenderer.liquidposnew[mulOffset + 1] = (float)transformation.m31();
        LiquidDeferredRenderer.liquidposnew[mulOffset + 2] = (float)transformation.m32();
        LiquidDeferredRenderer.liquidposnew[mulOffset + 3] = scale * 0.25f;
        return true;
    }

    private boolean prepareLiquidCudaInstances(PhysicsWorld physics, float[] positions, float[] oldPositions, int index, Vector3d cameraPos, int offset) {
        int mulOffset = offset * 4;
        int mulIndex = index * 3;
        LiquidDeferredRenderer.liquidpos[mulOffset] = oldPositions[mulIndex];
        LiquidDeferredRenderer.liquidpos[mulOffset + 1] = oldPositions[mulIndex + 1];
        LiquidDeferredRenderer.liquidpos[mulOffset + 2] = oldPositions[mulIndex + 2];
        LiquidDeferredRenderer.liquidpos[mulOffset + 3] = 1.0f;
        LiquidDeferredRenderer.liquidposnew[mulOffset] = positions[mulIndex];
        LiquidDeferredRenderer.liquidposnew[mulOffset + 1] = positions[mulIndex + 1];
        LiquidDeferredRenderer.liquidposnew[mulOffset + 2] = positions[mulIndex + 2];
        LiquidDeferredRenderer.liquidposnew[mulOffset + 3] = physics.fluidParticleSize;
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

    static {
        liquidpos = new float[400];
        liquidposnew = new float[400];
        viewport = new int[4];
    }
}

