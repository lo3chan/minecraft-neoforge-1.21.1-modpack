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
 *  org.joml.Vector3fc
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
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

public class BlockEntityVertexConsumer
implements VertexConsumer {
    private Model model = new Model();
    private Mesh mesh;
    private int vertCount = 0;
    private Vector3f tmpPos = new Vector3f();
    private Vector3f tmpNormal = new Vector3f();
    private Vector3f tmp1 = new Vector3f();
    private Vector3f tmp2 = new Vector3f();

    public BlockEntityVertexConsumer() {
        this.mesh = this.model.mesh = new Mesh();
    }

    public void validateModel() {
        int indicesFaceCount;
        int faceCount = this.mesh.positions.size() / 4;
        if (faceCount != (indicesFaceCount = this.mesh.indices.size() / 6)) {
            int i;
            this.mesh.indices.clear();
            int index = 0;
            for (i = 0; i < faceCount; ++i) {
                this.mesh.indices.add(index);
                this.mesh.indices.add(index + 1);
                this.mesh.indices.add(index + 2);
                this.mesh.indices.add(index);
                this.mesh.indices.add(index + 2);
                this.mesh.indices.add(index + 3);
                index += 4;
            }
            if (this.mesh.positions.size() != this.mesh.normals.size()) {
                for (i = this.mesh.normals.size(); i < this.mesh.positions.size(); ++i) {
                    Vector3f tmp1;
                    int face = i / 4;
                    Vector3f pos0 = this.mesh.positions.get(face * 4 + 1);
                    Vector3f pos1 = this.mesh.positions.get(face * 4 + 2);
                    Vector3f pos2 = this.mesh.positions.get(face * 4 + 3);
                    Vector3f tmp0 = pos1.sub((Vector3fc)pos0, this.tmp1);
                    Vector3f normal = tmp0.cross((Vector3fc)(tmp1 = pos2.sub((Vector3fc)pos0, this.tmp2)));
                    float length = normal.lengthSquared();
                    if ((double)length != 0.0) {
                        normal.mul(1.0f / length);
                    } else {
                        normal.set(0.0, 1.0, 0.0);
                    }
                    this.mesh.normals.add(normal);
                }
            }
        }
    }

    public VertexConsumer addVertex(float x, float y, float z) {
        this.mesh.positions.add(new Vector3f(x, y, z));
        this.model.textureID = TextureHelper.getLoadedTextures();
        return this;
    }

    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        this.mesh.addColor(red, green, blue, alpha);
        return this;
    }

    public VertexConsumer setUv(float u, float v) {
        this.mesh.uvs.add(new Vector2f(u, v));
        return this;
    }

    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    public VertexConsumer setUv2(int u, int v) {
        return this;
    }

    public VertexConsumer setNormal(float x, float y, float z) {
        this.mesh.normals.add(new Vector3f(x, y, z));
        return this;
    }

    public VertexConsumer addVertex(Matrix4f matrix, float x, float y, float z) {
        this.tmpPos.set(x, y, z);
        matrix.transformPosition(this.tmpPos);
        this.mesh.positions.add(new Vector3f(this.tmpPos.x(), this.tmpPos.y(), this.tmpPos.z()));
        this.model.textureID = TextureHelper.getLoadedTextures();
        return this;
    }

    public VertexConsumer setNormal(PoseStack.Pose pose, float x, float y, float z) {
        this.tmpNormal.set(x, y, z);
        pose.transformNormal(x, y, z, this.tmpNormal);
        this.mesh.normals.add(new Vector3f(this.tmpNormal.x(), this.tmpNormal.y(), this.tmpNormal.z()));
        return this;
    }

    public void putBulkData(PoseStack.Pose matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean useQuadColorData) {
        int[] js = quad.getVertices();
        Vec3i faceNormal = quad.getDirection().getNormal();
        this.tmpNormal.set((float)faceNormal.getX(), (float)faceNormal.getY(), (float)faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.pose();
        matrixEntry.normal().transform(this.tmpNormal);
        int integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
        int j = js.length / integerSize;
        this.model.textureID = TextureHelper.getLoadedTextures();
        PhysicsMod.getCurrentInstance().itemStackEntity.shade = quad.isShade();
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = stack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
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
                    float l = (float)(byteBuffer.get(12) & 0xFF) * 0.003921569f;
                    v = (float)(byteBuffer.get(13) & 0xFF) * 0.003921569f;
                    w = (float)(byteBuffer.get(14) & 0xFF) * 0.003921569f;
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
                this.tmpPos.set(f, g, h);
                matrix4f.transformPosition(this.tmpPos);
                this.mesh.positions.add(new Vector3f(this.tmpPos.x(), this.tmpPos.y(), this.tmpPos.z()));
                this.mesh.addColor(r, s, t);
                this.mesh.normals.add(new Vector3f(this.tmpNormal.x(), this.tmpNormal.y(), this.tmpNormal.z()));
                this.mesh.uvs.add(new Vector2f(v, w));
            }
            int index = this.mesh.positions.size() - 4;
            this.mesh.indices.add(index);
            this.mesh.indices.add(index + 1);
            this.mesh.indices.add(index + 2);
            this.mesh.indices.add(index);
            this.mesh.indices.add(index + 2);
            this.mesh.indices.add(index + 3);
        }
    }

    public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        ++this.vertCount;
        this.model.textureID = TextureHelper.getLoadedTextures();
        this.mesh.positions.add(new Vector3f(x, y, z));
        this.mesh.addColor((float)FastColor.ARGB32.red((int)color) / 255.0f, (float)FastColor.ARGB32.green((int)color) / 255.0f, (float)FastColor.ARGB32.blue((int)color) / 255.0f, (float)FastColor.ARGB32.alpha((int)color) / 255.0f);
        this.mesh.normals.add(new Vector3f(normalX, normalY, normalZ));
        this.mesh.uvs.add(new Vector2f(u, v));
        if (this.vertCount == 4) {
            this.vertCount = 0;
            int index = this.mesh.positions.size() - 4;
            this.mesh.indices.add(index);
            this.mesh.indices.add(index + 1);
            this.mesh.indices.add(index + 2);
            this.mesh.indices.add(index);
            this.mesh.indices.add(index + 2);
            this.mesh.indices.add(index + 3);
        }
    }

    public VertexConsumer setColor(float red, float green, float blue, float alpha) {
        this.mesh.addColor(red, green, blue, alpha);
        return this;
    }

    public VertexConsumer setLight(int uv) {
        return this;
    }

    public VertexConsumer setOverlay(int uv) {
        return this;
    }

    public Model getModel() {
        return this.model;
    }
}

