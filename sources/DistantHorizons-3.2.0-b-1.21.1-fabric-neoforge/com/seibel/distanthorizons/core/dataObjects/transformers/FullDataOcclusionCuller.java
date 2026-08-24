package com.seibel.distanthorizons.core.dataObjects.transformers;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.jetbrains.annotations.NotNull;

public class FullDataOcclusionCuller {
   public static void cullHiddenDatapointsInColumn(FullDataSourceV2 dataSource, int relX, int relZ) {
      LongArrayList centerColumn = dataSource.getColumnAtRelPos(relX, relZ);
      LongArrayList posXColumn = dataSource.tryGetColumnAtRelPos(relX + 1, relZ);
      LongArrayList negXColumn = dataSource.tryGetColumnAtRelPos(relX - 1, relZ);
      LongArrayList posZColumn = dataSource.tryGetColumnAtRelPos(relX, relZ + 1);
      LongArrayList negZColumn = dataSource.tryGetColumnAtRelPos(relX, relZ - 1);
      if (posXColumn != null
         && posXColumn.size() != 0
         && negXColumn != null
         && negXColumn.size() != 0
         && posZColumn != null
         && posZColumn.size() != 0
         && negZColumn != null
         && negZColumn.size() != 0) {
         int centerIndex = centerColumn.size() - 1;
         int posXIndex = posXColumn.size() - 1;
         int negXIndex = negXColumn.size() - 1;
         int posZIndex = posZColumn.size() - 1;

         for (int negZIndex = negZColumn.size() - 1; centerIndex >= 0; centerIndex--) {
            long currentPoint = centerColumn.getLong(centerIndex);
            if (!isTranslucent(dataSource, currentPoint)
               && centerIndex != 0
               && !isTranslucent(dataSource, centerColumn.getLong(centerIndex - 1))
               && (centerIndex + 1 >= centerColumn.size() || !isTranslucent(dataSource, centerColumn.getLong(centerIndex + 1)))
               && centerIndex + 1 != centerColumn.size()) {
               posXIndex = checkOcclusion(dataSource, currentPoint, posXColumn, posXIndex);
               if (posXIndex < 0) {
                  posXIndex = ~posXIndex;
               } else {
                  negXIndex = checkOcclusion(dataSource, currentPoint, negXColumn, negXIndex);
                  if (negXIndex < 0) {
                     negXIndex = ~negXIndex;
                  } else {
                     posZIndex = checkOcclusion(dataSource, currentPoint, posZColumn, posZIndex);
                     if (posZIndex < 0) {
                        posZIndex = ~posZIndex;
                     } else {
                        negZIndex = checkOcclusion(dataSource, currentPoint, negZColumn, negZIndex);
                        if (negZIndex < 0) {
                           negZIndex = ~negZIndex;
                        } else {
                           centerColumn.removeLong(centerIndex);
                           long above = centerColumn.getLong(centerIndex - 1);
                           above = FullDataPointUtil.setBottomY(above, FullDataPointUtil.getBottomY(currentPoint));
                           above = FullDataPointUtil.setHeight(above, FullDataPointUtil.getHeight(currentPoint) + FullDataPointUtil.getHeight(above));
                           centerColumn.set(centerIndex - 1, above);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int checkOcclusion(@NotNull FullDataSourceV2 source, long centerPoint, @NotNull LongArrayList adjacentColumn, int adjacentIndex) {
      if (adjacentColumn.isEmpty()) {
         return ~adjacentIndex;
      } else if (adjacentColumn.size() == 1 && adjacentColumn.getLong(0) == 0L) {
         return ~adjacentIndex;
      } else {
         int bottomOfCenter = FullDataPointUtil.getBottomY(centerPoint);
         int topOfCenter = bottomOfCenter + FullDataPointUtil.getHeight(centerPoint);

         while (adjacentIndex >= 0) {
            long adjacentPoint = adjacentColumn.getLong(adjacentIndex);
            int topOfAdjacent = FullDataPointUtil.getBottomY(adjacentPoint) + FullDataPointUtil.getHeight(adjacentPoint);
            if (topOfAdjacent > bottomOfCenter) {
               if (isTranslucent(source, adjacentPoint)) {
                  return ~adjacentIndex;
               }

               if (topOfAdjacent >= topOfCenter) {
                  return adjacentIndex;
               }
            }

            adjacentIndex--;
         }

         return ~adjacentIndex;
      }
   }

   private static boolean isTranslucent(FullDataSourceV2 source, long point) {
      int id = FullDataPointUtil.getId(point);
      int opacity = source.mapping.getBlockStateWrapper(id).getOpacity();
      return opacity < 16;
   }
}
