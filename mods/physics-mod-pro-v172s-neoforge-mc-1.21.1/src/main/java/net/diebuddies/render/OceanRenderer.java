/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Style
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3f
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector3d
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.FloatBuffer;
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
import net.diebuddies.render.MainRenderer;
import net.diebuddies.render.OceanRippleRenderer;
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
import org.joml.Matrix4fc;
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
        if (!ConfigClient.areOceanPhysicsEnabled()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !Iris.oceanError.isEmpty()) {
            player.displayClientMessage((Component)Component.literal((String)Iris.oceanError).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
            Iris.oceanError = "";
        }
        PerformanceTracker.startNoFlush("ocean_rendering");
        OceanWorld oceanWorld = physics.getOceanWorld();
        TextureAtlasSprite waterTexture = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        int waterID = Minecraft.getInstance().getTextureManager().getTexture(waterTexture.atlasLocation()).getId();
        this.bindOceanShader();
        ShaderInstance shader = RenderSystem.getShader();
        this.initRenderingStates(shader, oceanWorld);
        Matrix4fStack cameraRotation = matrixStackIn;
        Matrix3f normal = cameraRotation.normal(this.tmp);
        this.setupOceanRendering(physics, null, level, shader, waterID);
        boolean isShadowPass = StarterClient.iris && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
        for (ProxyOceanLayer layer : oceanWorld.getOceanLayers().values()) {
            OceanSurface surface = layer.getOceanSurface();
            if (surface == null) continue;
            for (OceanMesh oceanMesh : surface.getMeshes().values()) {
                if (!this.isVisible(oceanWorld, oceanMesh, view)) continue;
                this.drawList.add(oceanMesh);
            }
            if (this.drawList.isEmpty()) continue;
            if (ConfigClient.oceanRipples && !isShadowPass) {
                boolean updatedRipples = false;
                updatedRipples = layer.needsRippleUpdate();
                if (updatedRipples) {
                    this.rippleRenderer.updateRippleInstances(physics.getOceanWorld(), layer, view);
                    layer.setNeedsRippleUpdate(false);
                }
                if (layer.getRippleCount() > 0) {
                    this.rippleRenderer.renderSmallWaves(physics, layer, level, matrixStackIn, view);
                    this.bindOceanShader();
                    this.setupOceanRendering(physics, layer, level, shader, waterID);
                } else {
                    this.bindRippleTexture(layer);
                    if (updatedRipples) {
                        physics.getOceanWorld().bindForRendering();
                    }
                }
            }
            for (OceanMesh oceanMesh : this.drawList) {
                this.renderOcean(oceanWorld, level, (Matrix4f)cameraRotation, normal, view, oceanMesh);
            }
            this.drawList.clear();
        }
        if (StarterClient.sodium) {
            Sodium.markSpriteActive(waterTexture);
        }
        RenderSystem.enableCull();
        RenderSystem.activeTexture((int)(33984 + this.activeTexture));
        RenderSystem.setShaderTexture((int)this.activeTexture, (int)this.oldTexture);
        RenderSystem.bindTexture((int)this.oldTexture);
        RenderSystem.activeTexture((int)(33984 + this.rippleActiveTexture));
        RenderSystem.setShaderTexture((int)this.rippleActiveTexture, (int)this.oldRippleTexture);
        RenderSystem.bindTexture((int)this.oldRippleTexture);
        RenderSystem.activeTexture((int)33984);
        StateTracker.unbindVertexArray();
        PerformanceTracker.end("ocean_rendering");
    }

    private void initRenderingStates(ShaderInstance shader, OceanWorld oceanWorld) {
        int shaderId = GL32C.glGetInteger((int)35725);
        this.physicsGameTimeLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_gameTime");
        this.globalGameTimeLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_globalTime");
        this.physicsIterationsNormalLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_iterationsNormal");
        this.physicsOceanHeightLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_oceanHeight");
        this.oceanWaveHorizontalScaleLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_oceanWaveHorizontalScale");
        this.rippleLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_ripples");
        this.foamLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_foam");
        this.lightmapLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_lightmap");
        this.rippleRangeLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_rippleRange");
        this.foamAmountLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_foamAmount");
        this.foamOpacityLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_foamOpacity");
        this.wavinessLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_waviness");
        this.waveOffsetLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_waveOffset");
        this.textureOffsetLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_textureOffset");
        this.modelOffsetLocation = GL32C.glGetUniformLocation((int)shaderId, (CharSequence)"physics_modelOffset");
        if (StarterClient.optifabric) {
            GL32C.glVertexAttrib3f((int)Data.NORMAL.getAttribute(), (float)0.0f, (float)1.0f, (float)0.0f);
        } else {
            GL32C.glVertexAttrib3f((int)Data.NORMAL_SHADER.getAttribute(), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (StarterClient.iris) {
            Vector2f midCoord = oceanWorld.getWaterMidCoord();
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), (float)midCoord.x, (float)midCoord.y);
            GL32C.glVertexAttrib4f((int)Data.TANGENT_TERRAIN_SHADER.getAttribute(), (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f);
            this.mcEntityLocation = GL20.glGetAttribLocation((int)RenderSystem.getShader().getId(), (CharSequence)"mc_Entity");
            if (this.mcEntityLocation != -1) {
                GL32C.glVertexAttrib4f((int)this.mcEntityLocation, (float)Iris.getMaterialID(Blocks.WATER.defaultBlockState()), (float)1.0f, (float)-1.0f, (float)-1.0f);
            }
            this.activeTexture = 15;
        } else if (StarterClient.optifabric) {
            Vector2f midCoord = oceanWorld.getWaterMidCoord();
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_OPTIFINE.getAttribute(), (float)midCoord.x, (float)midCoord.y);
            GL32C.glVertexAttrib4f((int)Data.TANGENT_OPTIFINE.getAttribute(), (float)0.0f, (float)0.0f, (float)1.0f, (float)1.0f);
            this.mcEntityLocation = Optifine.isUsingShadersNoInternal() ? 11 : GL20.glGetAttribLocation((int)RenderSystem.getShader().getId(), (CharSequence)"mc_Entity");
            if (this.mcEntityLocation != -1) {
                GL32C.glVertexAttrib4f((int)this.mcEntityLocation, (float)Optifine.getMaterialID(Blocks.WATER.defaultBlockState()), (float)Optifine.getRenderType(Blocks.WATER.defaultBlockState()), (float)-1.0f, (float)-1.0f);
            }
            this.activeTexture = 15;
        } else {
            this.activeTexture = 11;
        }
        this.rippleActiveTexture = this.activeTexture - 1;
        RenderSystem.activeTexture((int)(33984 + this.activeTexture));
        this.oldTexture = GL32C.glGetInteger((int)32873);
        RenderSystem.activeTexture((int)(33984 + this.rippleActiveTexture));
        this.oldRippleTexture = GL32C.glGetInteger((int)32873);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void setupOceanRendering(PhysicsWorld physics, @Nullable ProxyOceanLayer layer, ClientLevel level, ShaderInstance shader, int waterTexture) {
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
        if (this.physicsGameTimeLocation != -1) {
            GL32C.glUniform1f((int)this.physicsGameTimeLocation, (float)physics.getOceanWorld().getOceanTime());
        }
        if (this.globalGameTimeLocation != -1) {
            GL32C.glUniform1f((int)this.globalGameTimeLocation, (float)physics.getOceanWorld().getGlobalTime());
        }
        if (this.physicsIterationsNormalLocation != -1) {
            GL32C.glUniform1i((int)this.physicsIterationsNormalLocation, (int)(13 + (int)(ConfigClient.oceanDetail * 35.0f)));
        }
        if (this.physicsOceanHeightLocation != -1) {
            GL32C.glUniform1f((int)this.physicsOceanHeightLocation, (float)physics.getOceanWorld().getOceanHeight());
        }
        if (this.oceanWaveHorizontalScaleLocation != -1) {
            GL32C.glUniform1f((int)this.oceanWaveHorizontalScaleLocation, (float)ConfigClient.oceanHorizontalWaveScale);
        }
        if (this.rippleLocation != -1) {
            GL32C.glUniform1i((int)this.rippleLocation, (int)this.rippleActiveTexture);
        }
        if (this.foamLocation != -1) {
            GL32C.glUniform1i((int)this.foamLocation, (int)this.foamActiveTexture);
        }
        if (this.lightmapLocation != -1) {
            GL32C.glUniform1i((int)this.lightmapLocation, (int)this.lightmapActiveTexture);
        }
        if (this.rippleRangeLocation != -1) {
            GL32C.glUniform1f((int)this.rippleRangeLocation, (float)((float)this.rippleRenderer.getRippleRange()));
        }
        if (this.foamAmountLocation != -1) {
            GL32C.glUniform1f((int)this.foamAmountLocation, (float)ConfigClient.oceanFoamAmount);
        }
        if (this.foamOpacityLocation != -1) {
            GL32C.glUniform1f((int)this.foamOpacityLocation, (float)ConfigClient.oceanFoamOpacity);
        }
        if (this.wavinessLocation != -1) {
            GL32C.glUniform1i((int)this.wavinessLocation, (int)this.activeTexture);
        }
        RenderSystem.setShaderTexture((int)0, (int)waterTexture);
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)waterTexture);
        if (this.foamLocation != -1) {
            int foamTextureID = PhysicsMod.foamTexture.getID();
            RenderSystem.activeTexture((int)(33984 + this.foamActiveTexture));
            GL32C.glBindTexture((int)32879, (int)foamTextureID);
        }
        if (this.lightmapLocation != -1) {
            Minecraft mc = Minecraft.getInstance();
            int lightmapTextureID = mc.getTextureManager().getTexture(((MixinLightTextureAccessor)mc.gameRenderer.lightTexture()).getLightTextureLocation()).getId();
            RenderSystem.activeTexture((int)(33984 + this.lightmapActiveTexture));
            GL32C.glBindTexture((int)3553, (int)lightmapTextureID);
        }
        this.bindRippleTexture(layer);
        physics.getOceanWorld().bindForRendering();
    }

    private void bindRippleTexture(ProxyOceanLayer layer) {
        int rippleTextureID = this.rippleRenderer.getRippleTexture(layer).getID();
        RenderSystem.activeTexture((int)(33984 + this.rippleActiveTexture));
        RenderSystem.setShaderTexture((int)this.rippleActiveTexture, (int)rippleTextureID);
        RenderSystem.bindTexture((int)rippleTextureID);
        RenderSystem.activeTexture((int)(33984 + this.activeTexture));
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
                }
                catch (IOException e) {
                    e.printStackTrace();
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
        float halfHeight = Math.max(0.5f, oceanWorld.getOceanHeight() * 0.5f * oceanMesh.maxInfluence);
        return this.mainRenderer.frustumInt.testAab((float)(start.x - view.x), (float)(start.y - view.y - (double)halfHeight), (float)(start.z - view.z), (float)(end.x - view.x), (float)(end.y - view.y + (double)halfHeight), (float)(end.z - view.z));
    }

    public void renderOcean(OceanWorld oceanWorld, ClientLevel level, Matrix4f cameraRotationMatrix, Matrix3f normalMatrix, Vec3 view, OceanMesh oceanMesh) {
        int glID = oceanMesh.texture.getID();
        RenderSystem.setShaderTexture((int)this.activeTexture, (int)glID);
        RenderSystem.bindTexture((int)glID);
        Matrix4d oceanTransformation = oceanMesh.transformation;
        this.transformation.m00((float)oceanTransformation.m00());
        this.transformation.m11((float)oceanTransformation.m11());
        this.transformation.m22((float)oceanTransformation.m22());
        this.transformation.m30((float)(oceanTransformation.m30() - view.x));
        this.transformation.m31((float)(oceanTransformation.m31() - view.y));
        this.transformation.m32((float)(oceanTransformation.m32() - view.z));
        cameraRotationMatrix.mul((Matrix4fc)this.transformation, this.currentPose);
        ShaderInstance shader = RenderSystem.getShader();
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setModelViewMatrix(this.currentPose);
        } else {
            int location = shader.MODEL_VIEW_MATRIX.getLocation();
            if (location != -1) {
                GL32C.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)this.currentPose.get(MainRenderer.matrixBuffer));
            }
        }
        if (this.modelOffsetLocation != -1) {
            GL32C.glUniform3f((int)this.modelOffsetLocation, (float)((float)(oceanTransformation.m30() - view.x)), (float)((float)(oceanTransformation.m31() - view.y)), (float)((float)(oceanTransformation.m32() - view.z)));
        }
        if (this.waveOffsetLocation != -1) {
            GL32C.glUniform2f((int)this.waveOffsetLocation, (float)oceanMesh.offsetX, (float)oceanMesh.offsetZ);
        }
        if (this.textureOffsetLocation != -1) {
            GL32C.glUniform2i((int)this.textureOffsetLocation, (int)oceanMesh.textureOffsetX, (int)oceanMesh.textureOffsetZ);
        }
        if (StarterClient.iris) {
            Iris.setNormalMatrix(shader, this.currentPose, normalMatrix);
        }
        ArenaBuffer.MemorySegment vertexSegment = oceanMesh.vertexSegment;
        ArenaBuffer.MemorySegment indexSegment = oceanMesh.indexSegment;
        int baseVertex = vertexSegment.offset / oceanWorld.format.getStride();
        GL32C.glDrawElementsBaseVertex((int)4, (int)(indexSegment.size / 2), (int)5123, (long)indexSegment.offset, (int)baseVertex);
    }

    public void setupAttribute(int location, float v0, float v1, float v2, float v3) {
        if (location != -1) {
            GL20.glVertexAttrib4f((int)location, (float)v0, (float)v1, (float)v2, (float)v3);
        }
    }

    public static void destroy() {
        if (oceanShader != null) {
            oceanShader.close();
        }
        OceanRippleRenderer.destroy();
    }
}

