package net.diebuddies.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DebugRenderer {
   private MainRenderer mainRenderer;
   private Matrix4d transformation = new Matrix4d();
   private Matrix4f localT = new Matrix4f();

   public DebugRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view) {
   }

   private void debugRenderTangents(PhysicsEntity entity) {
      for (int j = 0; j < entity.models.size(); j++) {
         Model model = entity.models.get(j);
         Mesh mesh = model.mesh;
         if (mesh.indices != null) {
            int size = mesh.indices.size();
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            float length = 0.175F;

            for (int i = 0; i < size / 3; i++) {
               int i0 = mesh.indices.getInt(i * 3);
               int i1 = mesh.indices.getInt(i * 3 + 1);
               int i2 = mesh.indices.getInt(i * 3 + 2);
               Vector3f p0 = mesh.positions.get(i0);
               Vector3f p1 = mesh.positions.get(i1);
               Vector3f p2 = mesh.positions.get(i2);
               Vector3f n0 = mesh.normals.get(i0);
               Vector3f n1 = mesh.normals.get(i1);
               Vector3f n2 = mesh.normals.get(i2);
               Vector3d p = new Vector3d(p0).add(p1).add(p2).div(3.0);
               Vector3f normal = new Vector3f(n0).add(n1).add(n2).div(3.0F);
               bufferbuilder.addVertex((float)p.x, (float)p.y, (float)p.z).setColor(1.0F, 0.0F, 0.0F, 1.0F);
               bufferbuilder.addVertex((float)p.x + normal.x * length, (float)p.y + normal.y * length, (float)p.z + normal.z * length)
                  .setColor(1.0F, 0.0F, 0.0F, 1.0F);
               if (StarterClient.iris && mesh.midcoords != null) {
                  Vector4f t0 = mesh.tangents.get(i0);
                  Vector4f t1 = mesh.tangents.get(i1);
                  Vector4f t2 = mesh.tangents.get(i2);
                  Vector4f tangent = new Vector4f(t0).add(t1).add(t2).div(3.0F);
                  bufferbuilder.addVertex((float)p.x, (float)p.y, (float)p.z).setColor(0.0F, 0.0F, 1.0F, 1.0F);
                  bufferbuilder.addVertex((float)p.x + tangent.x * length, (float)p.y + tangent.y * length, (float)p.z + tangent.z * length)
                     .setColor(0.0F, 0.0F, 1.0F, 1.0F);
               }
            }

            BufferUploader.drawWithShader(bufferbuilder.build());
         }
      }
   }

   private void debugRenderBox(float width, float height, float depth, float r, float g, float b, float a) {
      BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
      float[] positions = new float[]{
         -0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F
      };
      int[] indices = new int[]{
         0, 1, 2, 0, 2, 3, 6, 5, 4, 7, 6, 4, 10, 9, 8, 11, 10, 8, 12, 13, 14, 12, 14, 15, 18, 17, 16, 19, 18, 16, 20, 21, 22, 20, 22, 23
      };

      for (int i = 0; i < indices.length; i++) {
         int index = indices[i] * 3;
         bufferbuilder.addVertex(positions[index] * width, positions[index + 1] * height, positions[index + 2] * depth).setColor(r, g, b, a);
      }

      BufferUploader.drawWithShader(bufferbuilder.build());
   }
}
