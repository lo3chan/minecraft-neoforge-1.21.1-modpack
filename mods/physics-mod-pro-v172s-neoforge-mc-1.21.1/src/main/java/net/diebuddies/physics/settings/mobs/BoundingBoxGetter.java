/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  org.joml.Matrix4f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package net.diebuddies.physics.settings.mobs;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public class BoundingBoxGetter
implements VertexConsumer {
    public Vector3d min = new Vector3d(Double.MAX_VALUE);
    public Vector3d max = new Vector3d(-1.7976931348623157E308);
    private Vector3d tmp = new Vector3d();

    public VertexConsumer addVertex(float x, float y, float z) {
        this.min.min((Vector3dc)this.tmp.set((double)x, (double)y, (double)z));
        this.max.max((Vector3dc)this.tmp.set((double)x, (double)y, (double)z));
        return this;
    }

    public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
        return this;
    }

    public VertexConsumer setUv(float var1, float var2) {
        return this;
    }

    public VertexConsumer setUv1(int var1, int var2) {
        return this;
    }

    public VertexConsumer setUv2(int var1, int var2) {
        return this;
    }

    public VertexConsumer setNormal(float var1, float var2, float var3) {
        return this;
    }

    public void putBulkData(PoseStack.Pose matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean useQuadColorData) {
        int[] vertices = quad.getVertices();
        Matrix4f transformation = matrixEntry.pose();
        int integerSize = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
        int dataPerVertex = vertices.length / integerSize;
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = stack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int k = 0; k < dataPerVertex; ++k) {
                intBuffer.clear();
                intBuffer.put(vertices, k * integerSize, integerSize);
                float f = byteBuffer.getFloat(0);
                float g = byteBuffer.getFloat(4);
                float h = byteBuffer.getFloat(8);
                Vector4f vector4f = new Vector4f(f, g, h, 1.0f);
                transformation.transform(vector4f);
                this.min.min((Vector3dc)this.tmp.set((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z()));
                this.max.max((Vector3dc)this.tmp.set((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z()));
            }
        }
    }
}

