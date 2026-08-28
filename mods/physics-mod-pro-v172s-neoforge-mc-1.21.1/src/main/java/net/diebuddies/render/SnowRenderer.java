/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3i
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ClientChunkCacheAccessor;
import net.diebuddies.mixins.vines.StorageInvoker;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.snow.ChunkEntity;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowBatch;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;

public class SnowRenderer {
    public static final int SNOW_ENTITY_ID = 829925;
    private MainRenderer mainRenderer;
    private MultiDrawElementsBaseVertexCommand drawElementsCommand;
    private MultiDrawArraysCommand drawAraysCommands;
    private Matrix4f transformation = new Matrix4f();
    private Matrix4f currentPose = new Matrix4f();
    private Matrix3f tmp = new Matrix3f();

    public SnowRenderer(MainRenderer mainRenderer) {
        this.mainRenderer = mainRenderer;
        this.drawElementsCommand = new MultiDrawElementsBaseVertexCommand();
        this.drawAraysCommands = new MultiDrawArraysCommand();
    }

    public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view) {
        ClientChunkCache clientChunkCache;
        if (!ConfigClient.areSnowPhysicsEnabled()) {
            return;
        }
        PerformanceTracker.startNoFlush("snow_rendering");
        if (level.effects().constantAmbientLight()) {
            RenderSystem.shaderLightDirections[0] = MainRenderer.NETHER_DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.NETHER_DIFFUSE_LIGHT_1;
        } else {
            RenderSystem.shaderLightDirections[0] = MainRenderer.DIFFUSE_LIGHT_0;
            RenderSystem.shaderLightDirections[1] = MainRenderer.DIFFUSE_LIGHT_1;
        }
        RenderSystem.setShaderTexture((int)0, (int)PhysicsMod.whiteTexture.getID());
        RenderSystem.activeTexture((int)33984);
        RenderSystem.bindTexture((int)PhysicsMod.whiteTexture.getID());
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableCull();
        this.mainRenderer.setupPBRTextures();
        RenderSystem.activeTexture((int)33984);
        ShaderInstance shader = RenderSystem.getShader();
        RenderSystem.setupShaderLights((ShaderInstance)shader);
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setColorModulator(RenderSystem.getShaderColor());
        } else if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
            shader.COLOR_MODULATOR.upload();
        }
        if (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal()) {
            if (shader.LIGHT0_DIRECTION != null) {
                shader.LIGHT0_DIRECTION.upload();
            }
            if (shader.LIGHT1_DIRECTION != null) {
                shader.LIGHT1_DIRECTION.upload();
            }
        }
        GL32C.glVertexAttrib4f((int)Data.COLOR.getAttribute(), (float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL32C.glVertexAttrib2f((int)Data.TEX_COORD_SHADER.getAttribute(), (float)0.0f, (float)0.0f);
        if (StarterClient.optifabric) {
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_OPTIFINE.getAttribute(), (float)0.0f, (float)0.0f);
        } else if (StarterClient.iris) {
            GL32C.glVertexAttrib2f((int)Data.MID_TEX_COORD_SHADER.getAttribute(), (float)0.0f, (float)0.0f);
        }
        GL32C.glVertexAttribI2ui((int)Data.OVERLAY.getAttribute(), (int)0, (int)10);
        int entityIdLocation = GL32C.glGetUniformLocation((int)RenderSystem.getShader().getId(), (CharSequence)"entityId");
        int lastEntityId = 0;
        if (entityIdLocation != -1) {
            lastEntityId = GL32C.glGetUniformi((int)RenderSystem.getShader().getId(), (int)entityIdLocation);
            GL32C.glUniform1i((int)entityIdLocation, (int)829925);
        } else if (StarterClient.iris && Data.ENTITY_ID_SHADER.getAttribute() != -1) {
            GL32C.glVertexAttribI3ui((int)Data.ENTITY_ID_SHADER.getAttribute(), (int)829925, (int)0, (int)0);
        }
        SnowWorld snowWorld = physics.getSnowWorld();
        Matrix4fStack cameraRotation = matrixStackIn;
        Matrix3f normal = cameraRotation.normal(this.tmp);
        snowWorld.bindForRendering();
        if (level.getChunkSource() != null && (clientChunkCache = level.getChunkSource()) instanceof ClientChunkCacheAccessor) {
            ClientChunkCacheAccessor cacheAccessor = (ClientChunkCacheAccessor)clientChunkCache;
            StorageInvoker storageInvoker = (StorageInvoker)cacheAccessor.getStorage();
            for (Map.Entry<Vector3i, SnowBatch.SnowChunkBucket> entry : snowWorld.getSnowBatch().getBuckets().entrySet()) {
                Vector3i position = entry.getKey();
                SnowBatch.SnowChunkBucket bucket = entry.getValue();
                AABB3D modelBoundingBox = bucket.getAABB();
                Vector3d start = modelBoundingBox.start;
                Vector3d end = modelBoundingBox.end;
                if (!this.mainRenderer.frustumInt.testAab((float)(start.x - view.x), (float)(start.y - view.y), (float)(start.z - view.z), (float)(end.x - view.x), (float)(end.y - view.y), (float)(end.z - view.z))) continue;
                for (ChunkEntity snowChunk : bucket.getEntities()) {
                    if (!storageInvoker.invokeInRange(snowChunk.position.x, snowChunk.position.z)) continue;
                    this.renderSnow(snowWorld, (Level)level, (Matrix4f)cameraRotation, normal, view, snowChunk);
                }
                if (this.drawElementsCommand.size() <= 0 && this.drawAraysCommands.size() <= 0) continue;
                this.setupMatrices(shader, position, view, (Matrix4f)cameraRotation, normal);
                this.executeDrawCommands();
            }
        }
        if (entityIdLocation != -1) {
            GL32C.glUniform1i((int)entityIdLocation, (int)lastEntityId);
        } else if (Data.ENTITY_ID_SHADER.getAttribute() != -1) {
            GL32C.glVertexAttribI3ui((int)Data.ENTITY_ID_SHADER.getAttribute(), (int)0, (int)0, (int)0);
        }
        StateTracker.unbindVertexArray();
        PerformanceTracker.end("snow_rendering");
    }

    private void executeDrawCommands() {
        MemoryStack stack;
        int size = this.drawElementsCommand.size();
        if (size > 0) {
            stack = MemoryStack.stackPush();
            try {
                IntBuffer counts = stack.mallocInt(size);
                PointerBuffer pointers = stack.mallocPointer(size);
                IntBuffer baseVertices = stack.mallocInt(size);
                for (int i = 0; i < size; ++i) {
                    counts.put(i, this.drawElementsCommand.getCount(i));
                    pointers.put(i, this.drawElementsCommand.getPointer(i));
                    baseVertices.put(i, this.drawElementsCommand.getBaseVertex(i));
                }
                GL32C.glMultiDrawElementsBaseVertex((int)4, (IntBuffer)counts, (int)5125, (PointerBuffer)pointers, (IntBuffer)baseVertices);
            }
            finally {
                if (stack != null) {
                    stack.close();
                }
            }
            this.drawElementsCommand.clear();
        }
        if ((size = this.drawAraysCommands.size()) > 0) {
            stack = MemoryStack.stackPush();
            try {
                IntBuffer firsts = stack.mallocInt(size);
                IntBuffer counts = stack.mallocInt(size);
                for (int i = 0; i < size; ++i) {
                    firsts.put(i, this.drawAraysCommands.getFirst(i));
                    counts.put(i, this.drawAraysCommands.getCount(i));
                }
                GL32C.glMultiDrawArrays((int)4, (IntBuffer)firsts, (IntBuffer)counts);
            }
            finally {
                if (stack != null) {
                    stack.close();
                }
            }
            this.drawAraysCommands.clear();
        }
    }

    private void setupMatrices(ShaderInstance shader, Vector3i position, Vec3 view, Matrix4f cameraRotation, Matrix3f normal) {
        float scale = 1.0f / (float)IChunk.CHUNK_MULTIPLE;
        this.transformation.setTranslation((float)(((double)(position.x * IChunk.CHUNK_SIZE) + 0.5) * (double)scale - view.x), (float)(((double)(position.y * IChunk.CHUNK_SIZE) + 0.5) * (double)scale - view.y), (float)(((double)(position.z * IChunk.CHUNK_SIZE) + 0.5) * (double)scale - view.z));
        this.transformation.m00(scale);
        this.transformation.m11(scale);
        this.transformation.m22(scale);
        cameraRotation.mul((Matrix4fc)this.transformation, this.currentPose);
        if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            Optifine.setModelViewMatrix(this.currentPose);
        } else {
            int location = shader.MODEL_VIEW_MATRIX.getLocation();
            if (location != -1) {
                GL32C.glUniformMatrix4fv((int)location, (boolean)false, (FloatBuffer)this.currentPose.get(MainRenderer.matrixBuffer));
            }
            if (StarterClient.iris) {
                Iris.setNormalMatrix(shader, this.currentPose, normal);
            }
        }
    }

    private void renderSnow(SnowWorld snowWorld, Level level, Matrix4f cameraRotationMatrix, Matrix3f normalMatrix, Vec3 view, ChunkEntity snow) {
        AABB3D modelBoundingBox = snow.aabb;
        Vector3d start = modelBoundingBox.start;
        Vector3d end = modelBoundingBox.end;
        if (!this.mainRenderer.frustumInt.testAab((float)(start.x - view.x), (float)(start.y - view.y), (float)(start.z - view.z), (float)(end.x - view.x), (float)(end.y - view.y), (float)(end.z - view.z))) {
            return;
        }
        if (snowWorld.getSnowIndexData() == null) {
            ArenaBuffer.MemorySegment segment = snow.vertexSegment;
            this.drawAraysCommands.add(segment.offset / snowWorld.format.getStride(), segment.size / snowWorld.format.getStride());
        } else {
            ArenaBuffer.MemorySegment vertexSegment = snow.vertexSegment;
            ArenaBuffer.MemorySegment indexSegment = snow.indexSegment;
            int baseVertex = vertexSegment.offset / snowWorld.format.getStride();
            this.drawElementsCommand.add(indexSegment.size / 4, indexSegment.offset, baseVertex);
        }
    }

    private static class MultiDrawElementsBaseVertexCommand {
        public IntList count = new IntArrayList();
        public LongList pointer = new LongArrayList();
        public IntList baseVertex = new IntArrayList();

        public void add(int count, long pointer, int baseVertex) {
            this.count.add(count);
            this.pointer.add(pointer);
            this.baseVertex.add(baseVertex);
        }

        public int getCount(int index) {
            return this.count.getInt(index);
        }

        public long getPointer(int index) {
            return this.pointer.getLong(index);
        }

        public int getBaseVertex(int index) {
            return this.baseVertex.getInt(index);
        }

        public void clear() {
            this.count.clear();
            this.pointer.clear();
            this.baseVertex.clear();
        }

        public int size() {
            return this.count.size();
        }
    }

    private static class MultiDrawArraysCommand {
        public IntList first = new IntArrayList();
        public IntList count = new IntArrayList();

        public void add(int first, int count) {
            this.first.add(first);
            this.count.add(count);
        }

        public int getFirst(int index) {
            return this.first.getInt(index);
        }

        public int getCount(int index) {
            return this.count.getInt(index);
        }

        public void clear() {
            this.first.clear();
            this.count.clear();
        }

        public int size() {
            return this.count.size();
        }
    }
}

