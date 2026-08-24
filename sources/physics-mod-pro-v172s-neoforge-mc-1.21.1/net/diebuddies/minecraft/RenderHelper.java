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
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class RenderHelper {
   private static Matrix4f transformation = new Matrix4f();
   private static Matrix3f normalMatrix = new Matrix3f();
   private static Vector3f tmpPos = new Vector3f();
   private static Vector3f tmpNormal = new Vector3f();
   private static Random random = new Random();

   public static void renderMesh(
      Entity entity,
      float tickDelta,
      MultiBufferSource multiBufferSource,
      EntityRenderDispatcher entityRenderDispatcher,
      ResourceLocation texture,
      Mesh mesh,
      PoseStack poseStack,
      int light,
      int overlay,
      boolean shade
   ) {
      VertexConsumer consumer = multiBufferSource.getBuffer(RenderType.entitySolid(texture));
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      transformation.set(poseStack.last().pose());
      int id = entity.getId();
      float progress = entity.tickCount + tickDelta;
      random.setSeed(id);
      transformation.rotateX(random.nextFloat() * 3.1415927F);
      transformation.rotateY(random.nextFloat() * 3.1415927F);
      transformation.rotateZ(random.nextFloat() * 3.1415927F + progress * 0.5F);
      transformation.normal(normalMatrix);
      if (!shade) {
         normalMatrix.set(poseStack.last().normal());
      }

      for (int i = 0; i < mesh.indicesQuads.size(); i++) {
         int index = mesh.indicesQuads.getInt(i);
         Vector3f position = mesh.positions.get(index);
         Vector2f uv = mesh.uvs.get(index);
         Vector3f normal = mesh.normals.get(index);
         position = transformation.transformPosition(position, tmpPos);
         if (shade) {
            tmpNormal.set(normal.x, normal.y, normal.z);
         } else {
            tmpNormal.set(0.0, 1.0, 0.0);
         }

         normalMatrix.transform(tmpNormal);
         if (mesh.colors.size() > 0) {
            int color = mesh.colors.getInt(index);
            r = (color & 0xFF) / 255.0F;
            g = (color >> 8 & 0xFF) / 255.0F;
            b = (color >> 16 & 0xFF) / 255.0F;
         }

         consumer.addVertex(position.x, position.y, position.z)
            .setColor(r, g, b, 1.0F)
            .setUv(uv.x, uv.y)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(tmpNormal.x, tmpNormal.y, tmpNormal.z);
      }
   }
}
