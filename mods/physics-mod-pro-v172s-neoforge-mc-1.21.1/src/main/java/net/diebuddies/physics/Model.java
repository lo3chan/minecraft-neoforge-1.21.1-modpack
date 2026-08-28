/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Model {
    public TextureAtlasSprite texture;
    public Matrix4f textureMatrix;
    public TextureAtlasSprite animationSprite;
    public int textureID;
    public Mesh mesh;
    public Mesh physicsMesh;
    public ArenaBuffer.MemorySegment memorySegment;
    public boolean onlyVisual;

    public void createModelMemorySegment(PhysicsWorld physics, boolean shade) {
        boolean usePBRData;
        int size = this.mesh.indices.size();
        int positionSize = size * 3 * 4;
        int colorSize = size * 4;
        int uvSize = size * 2 * 4;
        int normalSize = size * 4;
        int vertexSize = positionSize + colorSize + uvSize + normalSize;
        boolean bl = usePBRData = StarterClient.iris || StarterClient.optifabric;
        if (usePBRData) {
            int midUvSize = size * 2 * 4;
            int tangentSize = size * 4;
            vertexSize += midUvSize + tangentSize;
        }
        ByteBuffer data = null;
        boolean stackAlloc = false;
        if (vertexSize <= StarterClient.memoryStack.getSize()) {
            MemoryStack stack = StarterClient.memoryStack.push();
            data = stack.malloc(vertexSize);
            stackAlloc = true;
        } else {
            data = MemoryUtil.memAlloc((int)vertexSize);
        }
        long pointer = MemoryUtil.memAddress((ByteBuffer)data);
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;
        if (this.texture != null && this.textureMatrix == null) {
            minU = this.texture.getU0();
            maxU = this.texture.getU1();
            minV = this.texture.getV0();
            maxV = this.texture.getV1();
        }
        for (int i = 0; i < size; ++i) {
            int index = this.mesh.indices.getInt(i);
            Vector3f p = this.mesh.positions.get(index);
            MemoryUtil.memPutFloat((long)pointer, (float)p.x);
            MemoryUtil.memPutFloat((long)(pointer + 4L), (float)p.y);
            MemoryUtil.memPutFloat((long)(pointer + 8L), (float)p.z);
            if (this.mesh.colors.size() > 0) {
                MemoryUtil.memPutInt((long)(pointer + 12L), (int)this.mesh.colors.getInt(index));
            } else {
                MemoryUtil.memPutInt((long)(pointer + 12L), (int)-1);
            }
            if (this.mesh.uvs.size() == 0) {
                MemoryUtil.memPutFloat((long)(pointer + 16L), (float)Math.remapClamp(0.5f, 0.0f, 1.0f, minU, maxU));
                MemoryUtil.memPutFloat((long)(pointer + 20L), (float)Math.remapClamp(0.5f, 0.0f, 1.0f, minV, maxV));
            } else {
                Vector2f uv = this.mesh.uvs.get(index);
                MemoryUtil.memPutFloat((long)(pointer + 16L), (float)Math.remapClamp(uv.x, 0.0f, 1.0f, minU, maxU));
                MemoryUtil.memPutFloat((long)(pointer + 20L), (float)Math.remapClamp(uv.y, 0.0f, 1.0f, minV, maxV));
            }
            if (shade && this.mesh.normals.size() > 0) {
                Vector3f normal = this.mesh.normals.get(index);
                MemoryUtil.memPutInt((long)(pointer + 24L), (int)Pack.normal(normal.x, normal.y, normal.z));
            } else {
                MemoryUtil.memPutInt((long)(pointer + 24L), (int)Pack.Y_POS_NORMAL);
            }
            if (usePBRData && this.mesh.midcoords != null) {
                if (shade && this.mesh.tangents.size() > 0) {
                    Vector4f tangent = this.mesh.tangents.get(index);
                    MemoryUtil.memPutInt((long)(pointer + 28L), (int)Pack.normal(tangent.x, tangent.y, tangent.z, tangent.w));
                } else {
                    MemoryUtil.memPutInt((long)(pointer + 28L), (int)Pack.X_POS_TANGENT);
                }
                if (this.mesh.midcoords.size() == 0) {
                    MemoryUtil.memPutFloat((long)(pointer + 32L), (float)Math.remapClamp(0.5f, 0.0f, 1.0f, minU, maxU));
                    MemoryUtil.memPutFloat((long)(pointer + 36L), (float)Math.remapClamp(0.5f, 0.0f, 1.0f, minV, maxV));
                } else {
                    Vector2f miduv = this.mesh.midcoords.get(i / 3);
                    MemoryUtil.memPutFloat((long)(pointer + 32L), (float)Math.remapClamp(miduv.x, 0.0f, 1.0f, minU, maxU));
                    MemoryUtil.memPutFloat((long)(pointer + 36L), (float)Math.remapClamp(miduv.y, 0.0f, 1.0f, minV, maxV));
                }
                pointer += 12L;
            }
            pointer += 28L;
        }
        this.memorySegment = physics.getModelVertexData().uploadData(data);
        if (stackAlloc) {
            StarterClient.memoryStack.pop();
        } else {
            MemoryUtil.memFree((Buffer)data);
        }
        this.mesh.clearMemory();
        if (this.physicsMesh != null) {
            this.physicsMesh.clearMemory();
        }
    }
}

