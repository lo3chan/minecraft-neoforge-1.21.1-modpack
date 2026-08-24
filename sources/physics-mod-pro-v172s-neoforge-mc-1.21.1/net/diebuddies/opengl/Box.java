package net.diebuddies.opengl;

import java.util.Arrays;
import org.joml.Vector3d;

public class Box {
   public static final int[] INDICES = new int[]{
      0, 1, 2, 0, 2, 3, 6, 5, 4, 7, 6, 4, 10, 9, 8, 11, 10, 8, 12, 13, 14, 12, 14, 15, 18, 17, 16, 19, 18, 16, 20, 21, 22, 20, 22, 23
   };
   public static final float[] POSITIONS = new float[]{
      -1.0F,
      -1.0F,
      1.0F,
      1.0F,
      -1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      -1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      1.0F,
      1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      1.0F,
      1.0F,
      1.0F,
      -1.0F,
      1.0F,
      1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      1.0F,
      -1.0F,
      -1.0F,
      1.0F
   };
   public static final float[] UVS = new float[]{
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      1.0F
   };
   public static final byte[] COLORS = new byte[]{
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1
   };
   public static final float[] NORMALS = new float[]{
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F,
      0.0F,
      -1.0F,
      0.0F
   };

   public static Mesh createBox(float halfWidth, float halfHeight, float halfDepth) {
      Mesh mesh = new Mesh();
      int[] ind = new int[INDICES.length];
      System.arraycopy(INDICES, 0, ind, 0, ind.length);
      float[] pos = new float[POSITIONS.length];

      for (int i = 0; i < POSITIONS.length / 3; i++) {
         pos[i * 3] = POSITIONS[i * 3] * halfWidth;
         pos[i * 3 + 1] = POSITIONS[i * 3 + 1] * halfHeight;
         pos[i * 3 + 2] = POSITIONS[i * 3 + 2] * halfDepth;
      }

      mesh.set(pos, Data.POSITION);
      mesh.set(Arrays.copyOf(COLORS, COLORS.length), Data.COLOR);
      mesh.set(Arrays.copyOf(NORMALS, NORMALS.length), Data.NORMAL);
      mesh.set(ind, Data.INDEX);
      return mesh;
   }

   public static Mesh createBox(float halfWidth, float halfHeight, float halfDepth, Vector3d... boxPositions) {
      Mesh mesh = new Mesh();
      float[] p = new float[POSITIONS.length * boxPositions.length];
      byte[] c = new byte[COLORS.length * boxPositions.length];
      float[] n = new float[NORMALS.length * boxPositions.length];
      int[] i = new int[INDICES.length * boxPositions.length];

      for (int j = 0; j < boxPositions.length; j++) {
         for (int k = 0; k < POSITIONS.length / 3; k++) {
            p[j * POSITIONS.length + k * 3] = POSITIONS[k * 3] * halfWidth + (float)boxPositions[j].x;
            p[j * POSITIONS.length + k * 3 + 1] = POSITIONS[k * 3 + 1] * halfHeight + (float)boxPositions[j].y;
            p[j * POSITIONS.length + k * 3 + 2] = POSITIONS[k * 3 + 2] * halfDepth + (float)boxPositions[j].z;
         }

         for (int k = 0; k < INDICES.length; k++) {
            i[j * INDICES.length + k] = INDICES[k] + j * INDICES.length;
         }

         for (int k = 0; k < COLORS.length; k++) {
            c[j * COLORS.length + k] = COLORS[k];
         }

         for (int k = 0; k < NORMALS.length; k++) {
            n[j * NORMALS.length + k] = NORMALS[k];
         }
      }

      mesh.set(p, Data.POSITION);
      mesh.set(c, Data.COLOR);
      mesh.set(n, Data.NORMAL);
      mesh.set(i, Data.INDEX);
      return mesh;
   }
}
