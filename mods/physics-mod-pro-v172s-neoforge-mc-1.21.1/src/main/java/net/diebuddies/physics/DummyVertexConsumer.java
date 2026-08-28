/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.FastColor$ARGB32
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class DummyVertexConsumer
implements VertexConsumer {
    public boolean trackVertices = false;

    public VertexConsumer addVertex(float x, float y, float z) {
        return this;
    }

    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this;
    }

    public VertexConsumer setUv(float u, float v) {
        return this;
    }

    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    public VertexConsumer setUv2(int u, int v) {
        return this;
    }

    public VertexConsumer setNormal(float x, float y, float z) {
        return this;
    }

    public void putBulkData(PoseStack.Pose matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean useQuadColorData) {
        if (!this.trackVertices) {
            return;
        }
        int[] js = quad.getVertices();
        Vec3i vec3i = quad.getDirection().getNormal();
        Vector3f vec3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
        Matrix4f matrix4f = matrixEntry.pose();
        matrixEntry.normal().transform(vec3f);
        int integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
        int j = js.length / integerSize;
        PhysicsMod.getCurrentInstance().itemStackEntity.feature = PhysicsMod.getCurrentInstance().blockifyFeature;
        PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).textureID = TextureHelper.getLoadedTextures();
        PhysicsMod.getCurrentInstance().itemStackEntity.shade = quad.isShade();
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = stack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            Mesh mesh = PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh;
            for (int k = 0; k < j; ++k) {
                float t;
                float s;
                float r;
                float w;
                float v;
                intBuffer.clear();
                intBuffer.put(js, k * integerSize, integerSize);
                float f = byteBuffer.getFloat(0);
                float g = byteBuffer.getFloat(4);
                float h = byteBuffer.getFloat(8);
                if (useQuadColorData) {
                    float l = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    v = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    w = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    r = l * red;
                    s = v * green;
                    t = w * blue;
                } else {
                    r = red;
                    s = green;
                    t = blue;
                }
                v = byteBuffer.getFloat(16);
                w = byteBuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f, g, h, 1.0f);
                matrix4f.transform(vector4f);
                mesh.positions.add(new Vector3f(vector4f.x(), vector4f.y(), vector4f.z()));
                mesh.addColor(r, s, t);
                mesh.normals.add(new Vector3f(vec3f.x(), vec3f.y(), vec3f.z()));
                mesh.uvs.add(new Vector2f(v, w));
            }
            int index = mesh.positions.size() - 4;
            mesh.indices.add(index);
            mesh.indices.add(index + 1);
            mesh.indices.add(index + 2);
            mesh.indices.add(index);
            mesh.indices.add(index + 2);
            mesh.indices.add(index + 3);
        }
    }

    public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        if (!this.trackVertices) {
            return;
        }
        PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).textureID = TextureHelper.getLoadedTextures();
        Mesh mesh = PhysicsMod.getCurrentInstance().itemStackEntity.models.get((int)0).mesh;
        mesh.positions.add(new Vector3f(x, y, z));
        mesh.addColor((float)FastColor.ARGB32.red((int)color) / 255.0f, (float)FastColor.ARGB32.green((int)color) / 255.0f, (float)FastColor.ARGB32.blue((int)color) / 255.0f, (float)FastColor.ARGB32.alpha((int)color) / 255.0f);
        mesh.normals.add(new Vector3f(normalX, normalY, normalZ));
        mesh.uvs.add(new Vector2f(u, v));
        int index = mesh.positions.size() - 1;
        mesh.indices.add(index);
    }

    public VertexConsumer addVertex(Matrix4f matrix, float x, float y, float z) {
        return this;
    }

    public VertexConsumer setNormal(PoseStack.Pose matrix, float x, float y, float z) {
        return this;
    }

    public VertexConsumer setColor(float red, float green, float blue, float alpha) {
        return this;
    }

    public VertexConsumer setLight(int uv) {
        return this;
    }

    public VertexConsumer setOverlay(int uv) {
        return this;
    }
}

