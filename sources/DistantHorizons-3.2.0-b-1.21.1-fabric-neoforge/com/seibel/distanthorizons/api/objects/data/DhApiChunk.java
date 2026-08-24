package com.seibel.distanthorizons.api.objects.data;

import java.util.ArrayList;
import java.util.List;

public class DhApiChunk {
   public final int chunkPosX;
   public final int chunkPosZ;
   public final int bottomYBlockPos;
   public final int topYBlockPos;
   private final List<List<DhApiTerrainDataPoint>> dataPoints;

   public static DhApiChunk create(int chunkPosX, int chunkPosZ, int bottomYBlockPos, int topYBlockPos) {
      return new DhApiChunk(chunkPosX, chunkPosZ, bottomYBlockPos, topYBlockPos);
   }

   private DhApiChunk(int chunkPosX, int chunkPosZ, int bottomYBlockPos, int topYBlockPos) {
      this.chunkPosX = chunkPosX;
      this.chunkPosZ = chunkPosZ;
      this.bottomYBlockPos = bottomYBlockPos;
      this.topYBlockPos = topYBlockPos;
      this.dataPoints = new ArrayList<>(256);

      for (int i = 0; i < 256; i++) {
         this.dataPoints.add(i, null);
      }
   }

   public List<DhApiTerrainDataPoint> getDataPoints(int relX, int relZ) throws IndexOutOfBoundsException {
      throwIfRelativePosOutOfBounds(relX, relZ);
      return this.dataPoints.get(relZ << 4 | relX);
   }

   public void setDataPoints(int relX, int relZ, List<DhApiTerrainDataPoint> dataPoints) throws IndexOutOfBoundsException, IllegalArgumentException {
      int internalArrayIndex = relZ << 4 | relX;
      throwIfRelativePosOutOfBounds(relX, relZ);
      if (dataPoints == null) {
         throw new IllegalArgumentException(
            "Null columns aren't allowed. If you want to remove all data from a column please clear the list or pass in an empty list."
         );
      } else {
         List<DhApiTerrainDataPoint> column = this.dataPoints.get(internalArrayIndex);
         if (column == null) {
            column = new ArrayList<>();
            this.dataPoints.set(internalArrayIndex, column);
         }

         column.addAll(dataPoints);
      }
   }

   private static void throwIfRelativePosOutOfBounds(int relX, int relZ) {
      if (relX < 0 || relX > 15 || relZ < 0 || relZ > 15) {
         throw new IndexOutOfBoundsException(
            "Relative block positions must be between 0 and 15 (inclusive) the block pos: (" + relX + "," + relZ + ") is outside of those boundaries."
         );
      }
   }
}
