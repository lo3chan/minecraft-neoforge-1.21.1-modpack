/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.diebuddies.physics.Mesh;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RenderHelper {
    private static Matrix4f transformation = new Matrix4f();
    private static Matrix3f normalMatrix = new Matrix3f();
    private static Vector3f tmpPos = new Vector3f();
    private static Vector3f tmpNormal = new Vector3f();
    private static Random random = new Random();

    public static void renderMesh(Entity entity, float tickDelta, MultiBufferSource multiBufferSource, EntityRenderDispatcher entityRenderDispatcher, ResourceLocation texture, Mesh mesh, PoseStack poseStack, int light, int overlay, boolean shade) {
        VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entitySolid((ResourceLocation)texture));
        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;
        transformation.set((Matrix4fc)poseStack.last().pose());
        int id = entity.getId();
        float progress = (float)entity.tickCount + tickDelta;
        random.setSeed(id);
        transformation.rotateX(random.nextFloat() * (float)Math.PI);
        transformation.rotateY(random.nextFloat() * (float)Math.PI);
        transformation.rotateZ(random.nextFloat() * (float)Math.PI + progress * 0.5f);
        transformation.normal(normalMatrix);
        if (!shade) {
            normalMatrix.set((Matrix3fc)poseStack.last().normal());
        }
        for (int i = 0; i < mesh.indicesQuads.size(); ++i) {
            int index = mesh.indicesQuads.getInt(i);
            Vector3f position = mesh.positions.get(index);
            Vector2f uv = mesh.uvs.get(index);
            Vector3f normal = mesh.normals.get(index);
            position = transformation.transformPosition((Vector3fc)position, tmpPos);
            if (shade) {
                tmpNormal.set(normal.x, normal.y, normal.z);
            } else {
                tmpNormal.set(0.0, 1.0, 0.0);
            }
            normalMatrix.transform(tmpNormal);
            if (mesh.colors.size() > 0) {
                int color = mesh.colors.getInt(index);
                r = (float)(color & 0xFF) / 255.0f;
                g = (float)(color >> 8 & 0xFF) / 255.0f;
                b = (float)(color >> 16 & 0xFF) / 255.0f;
            }
            consumer.addVertex(position.x, position.y, position.z).setColor(r, g, b, 1.0f).setUv(uv.x, uv.y).setOverlay(overlay).setLight(light).setNormal(RenderHelper.tmpNormal.x, RenderHelper.tmpNormal.y, RenderHelper.tmpNormal.z);
        }
    }
}

