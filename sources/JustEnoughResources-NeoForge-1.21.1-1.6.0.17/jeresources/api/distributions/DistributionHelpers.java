package jeresources.api.distributions;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class DistributionHelpers {
   public static final float PI = 3.1415927F;

   public static float[] getTriangularDistribution(int midY, int range, float maxChance) {
      return getTriangularDistribution(midY - range, range, range, maxChance);
   }

   public static float[] getTriangularDistribution(int minY, int rand1, int rand2, float maxChance) {
      float[] triangle = new float[rand1 + rand2 + 1];
      float modChance = maxChance / Math.min(rand1, rand2);

      for (int i = 0; i < rand1; i++) {
         for (int j = 0; j < rand2; j++) {
            triangle[i + j] = triangle[i + j] + modChance;
         }
      }

      float[] result = new float[320];

      for (int i = 0; i < triangle.length; i++) {
         int mapToPos = i + minY + 64;
         if (mapToPos >= 0) {
            if (mapToPos == result.length) {
               break;
            }

            result[mapToPos] = triangle[i];
         }
      }

      return result;
   }

   public static float[] getSquareDistribution(int minY, int maxY, float chance) {
      float[] result = new float[320];

      for (int i = minY + 64; i <= maxY + 64; i++) {
         result[i] = chance;
      }

      return result;
   }

   public static float[] getRoundedSquareDistribution(int min0, int minY, int maxY, int max0, float chance) {
      float[] result = new float[320];
      addDistribution(result, getRampDistribution(min0, minY, chance), min0 + 64);
      addDistribution(result, getSquareDistribution(minY, maxY, chance));
      addDistribution(result, getRampDistribution(max0, maxY, chance), maxY + 64);
      return result;
   }

   public static float[] getUnderwaterDistribution(float chance) {
      float[] result = getTriangularDistribution(47, 8, chance / 7.0F);
      addDistribution(result, getRampDistribution(57, 62, chance), 57);
      result[62] = chance;
      addDistribution(result, getTriangularDistribution(55, 4, chance / 3.0F));
      return result;
   }

   public static float[] getRampDistribution(int minY, int maxY, float minChance, float maxChance) {
      if (minY == maxY) {
         return new float[0];
      } else if (minY > maxY) {
         return reverse(getRampDistribution(maxY, minY, minChance, maxChance));
      } else {
         int range = maxY - minY;
         float chanceDiff = maxChance - minChance;
         float[] result = new float[range + 1];

         for (int i = 0; i < range; i++) {
            result[i] = minChance + chanceDiff * i / range;
         }

         return result;
      }
   }

   public static float[] getRampDistribution(int minY, int maxY, float maxChance) {
      return getRampDistribution(minY + 64, maxY + 64, 0.0F, maxChance);
   }

   public static float[] getOverworldSurfaceDistribution(int oreDiameter) {
      float[] result = new float[320];
      float[] triangularDist = getOverworldSurface();
      float chance = oreDiameter / 320.0F;

      for (int i = 0; i < result.length - oreDiameter && i != triangularDist.length; i++) {
         if (triangularDist[i] != 0.0F) {
            for (int j = 0; j < oreDiameter; j++) {
               result[i + j] = result[i + j] + triangularDist[i] * chance;
            }
         }
      }

      return result;
   }

   public static float[] getOverworldSurface() {
      return getTriangularDistribution(69, 5, 0.09090909F);
   }

   public static float[] addDistribution(float[] base, float[] add) {
      return addDistribution(base, add, 0);
   }

   public static DistributionBase addDistribution(DistributionBase base, DistributionBase add) {
      return new DistributionCustom(addDistribution(base.getDistribution(), add.getDistribution()));
   }

   public static float[] addDistribution(float[] base, float[] add, int offset) {
      int addCount = 0;

      for (int i = offset; i < Math.min(base.length, add.length + offset); i++) {
         base[i] += add[addCount++];
      }

      return base;
   }

   public static float[] reverse(float[] array) {
      float[] result = new float[array.length];

      for (int i = 0; i < array.length; i++) {
         result[array.length - 1 - i] = array[i];
      }

      return result;
   }

   @Deprecated
   public static int calculateMeanLevel(float[] distribution, int mid, int oldMid, float difference) {
      return calculateMeanLevel(distribution, mid);
   }

   public static int calculateMeanLevel(float[] distribution, int mid) {
      float adjacent = 0.0F;
      float maxAdjacent = 0.0F;
      int consecutive = 0;
      mid = 0;

      for (int i = 0; i < 4 && i < distribution.length; i++) {
         adjacent += distribution[i];
      }

      for (int i = 0; i < distribution.length - 4; i++) {
         adjacent -= distribution[i] - distribution[i + 4];
         if (adjacent > maxAdjacent) {
            mid = i + 2;
            maxAdjacent = adjacent + 1.0E-5F;
            consecutive = 0;
         } else if (adjacent > maxAdjacent - 2.0E-5F) {
            consecutive++;
         } else {
            mid += consecutive / 2;
            consecutive = 0;
         }
      }

      return mid;
   }

   public static float[] divideArray(float[] array, float num) {
      float[] result = new float[array.length];

      for (int i = 0; i < array.length; i++) {
         result[i] = array[i] / num;
      }

      return result;
   }

   public static float[] multiplyArray(float[] array, float num) {
      float[] result = new float[array.length];

      for (int i = 0; i < array.length; i++) {
         result[i] = array[i] * num;
      }

      return result;
   }

   public static float[] maxJoinArray(float[] array1, float[] array2) {
      float[] result = new float[array1.length];
      if (array1.length != array2.length) {
         return result;
      } else {
         for (int i = 0; i < array1.length; i++) {
            result[i] = Math.max(array1[i], array2[i]);
         }

         return result;
      }
   }

   public static float sum(float[] distribution) {
      float result = 0.0F;

      for (float val : distribution) {
         result += val;
      }

      return result;
   }

   public static float calculateChance(int veinCount, int veinSize, int minY, int maxY) {
      return (float)veinCount * veinSize / ((maxY - minY + 1) * 320);
   }

   public static float[] getDistributionFromPoints(DistributionHelpers.OrePoint... points) {
      Set<DistributionHelpers.OrePoint> set = new TreeSet<>();
      Collections.addAll(set, points);
      points = set.toArray(new DistributionHelpers.OrePoint[set.size()]);
      float[] array = new float[320];
      addDistribution(array, getRampDistribution(0, points[0].level, points[0].chance));

      for (int i = 1; i < points.length; i++) {
         DistributionHelpers.OrePoint min;
         DistributionHelpers.OrePoint max;
         if (points[i - 1].chance <= points[i].chance) {
            min = points[i - 1];
            max = points[i];
         } else {
            max = points[i - 1];
            min = points[i];
         }

         float[] ramp = getRampDistribution(min.level, max.level, min.chance, max.chance);
         addDistribution(array, ramp, points[i - 1].level);
         array[points[i - 1].level] = points[i - 1].chance;
         array[points[i].level] = points[i].chance;
      }

      return array;
   }

   public static class OrePoint implements Comparable<DistributionHelpers.OrePoint> {
      private final int level;
      private final float chance;

      public OrePoint(int level, float chance) {
         this.level = level;
         this.chance = chance;
      }

      public int compareTo(DistributionHelpers.OrePoint o) {
         return this.level - o.level;
      }
   }
}
