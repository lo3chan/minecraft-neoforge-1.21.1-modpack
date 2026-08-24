package com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiTransparency;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.render.columnViews.ColumnRenderView;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.RenderDataPointUtil;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColumnBox {
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final byte SKYLIGHT_COVERED = -1;

   public static void addBoxQuadsToBuilder(
      LodQuadBuilder builder,
      PhantomArrayListCheckout phantomArrayCheckout,
      IDhClientLevel clientLevel,
      short blockWidth,
      short yHeight,
      short minX,
      short minY,
      short minZ,
      int color,
      byte irisBlockMaterialId,
      byte skyLight,
      byte blockLight,
      long topData,
      long bottomData,
      ColumnRenderView[] adjData,
      boolean[] isAdjDataSameDetailLevel,
      @Nullable short[] faceTileIdsByDirectionOrdinal
   ) {
      IClientLevelWrapper clientLevelWrapper = clientLevel.getClientLevelWrapper();
      if (clientLevelWrapper == null) {
         LodUtil.assertNotReach("addBoxQuadsToBuilder getClientLevelWrapper should always succeed");
      }

      short maxX = (short)(minX + blockWidth);
      short maxY = (short)(minY + yHeight);
      short maxZ = (short)(minZ + blockWidth);
      byte skyLightBottom = RenderDataPointUtil.doesDataPointExist(bottomData) ? RenderDataPointUtil.getLightSky(bottomData) : 0;
      boolean transparencyEnabled = Config.Client.Advanced.Graphics.Quality.transparency.get() == EDhApiTransparency.COMPLETE;
      boolean isTransparent = ColorUtil.getAlpha(color) < 255 && transparencyEnabled;
      boolean overVoid = !RenderDataPointUtil.doesDataPointExist(bottomData);
      boolean isTopTransparent = RenderDataPointUtil.getAlpha(topData) < 255 && transparencyEnabled;
      boolean isBottomTransparent = RenderDataPointUtil.getAlpha(bottomData) < 255 && transparencyEnabled;
      int caveCullingMaxY = -2147483648;
      if (Config.Client.Advanced.Graphics.Culling.enableCaveCulling.get()) {
         caveCullingMaxY = Config.Client.Advanced.Graphics.Culling.caveCullingHeight.get() - clientLevel.getLevelWrapper().getMinHeight();
      }

      if (!RenderDataPointUtil.doesDataPointExist(bottomData)) {
         color = ColorUtil.setAlpha(color, 255);
      }

      boolean skipTop = RenderDataPointUtil.doesDataPointExist(topData) && RenderDataPointUtil.getYMin(topData) == maxY && !isTopTransparent;
      if (!skipTop) {
         builder.addQuadUp(
            minX,
            maxY,
            minZ,
            blockWidth,
            ColorUtil.applyShade(color, clientLevelWrapper.getShade(EDhDirection.UP)),
            tryGetTextureId(faceTileIdsByDirectionOrdinal, EDhDirection.UP),
            irisBlockMaterialId,
            skyLight,
            blockLight
         );
      }

      skipTop = RenderDataPointUtil.doesDataPointExist(bottomData) && RenderDataPointUtil.getYMax(bottomData) == minY && !isBottomTransparent;
      if (!skipTop) {
         builder.addQuadDown(
            minX,
            minY,
            minZ,
            blockWidth,
            ColorUtil.applyShade(color, clientLevelWrapper.getShade(EDhDirection.DOWN)),
            tryGetTextureId(faceTileIdsByDirectionOrdinal, EDhDirection.DOWN),
            irisBlockMaterialId,
            skyLightBottom,
            blockLight
         );
      }

      ColumnRenderView adjCol = adjData[EDhDirection.NORTH.compassIndex];
      boolean adjSameDetailLevel = isAdjDataSameDetailLevel[EDhDirection.NORTH.compassIndex];
      if (adjCol == null) {
         if (!isTransparent || overVoid) {
            builder.addQuadAdj(
               EDhDirection.NORTH,
               minX,
               minY,
               minZ,
               blockWidth,
               yHeight,
               color,
               tryGetTextureId(faceTileIdsByDirectionOrdinal, EDhDirection.NORTH),
               irisBlockMaterialId,
               (byte)15,
               blockLight
            );
         }
      } else {
         makeAdjVerticalQuad(
            builder,
            phantomArrayCheckout,
            clientLevelWrapper,
            adjCol,
            faceTileIdsByDirectionOrdinal,
            adjSameDetailLevel,
            caveCullingMaxY,
            EDhDirection.NORTH,
            minX,
            minY,
            minZ,
            blockWidth,
            yHeight,
            color,
            irisBlockMaterialId,
            blockLight
         );
      }

      ColumnRenderView adjColx = adjData[EDhDirection.SOUTH.compassIndex];
      adjSameDetailLevel = isAdjDataSameDetailLevel[EDhDirection.SOUTH.compassIndex];
      if (adjColx == null) {
         if (!isTransparent || overVoid) {
            builder.addQuadAdj(
               EDhDirection.SOUTH,
               minX,
               minY,
               maxZ,
               blockWidth,
               yHeight,
               color,
               tryGetTextureId(faceTileIdsByDirectionOrdinal, EDhDirection.SOUTH),
               irisBlockMaterialId,
               (byte)15,
               blockLight
            );
         }
      } else {
         makeAdjVerticalQuad(
            builder,
            phantomArrayCheckout,
            clientLevelWrapper,
            adjColx,
            faceTileIdsByDirectionOrdinal,
            adjSameDetailLevel,
            caveCullingMaxY,
            EDhDirection.SOUTH,
            minX,
            minY,
            maxZ,
            blockWidth,
            yHeight,
            color,
            irisBlockMaterialId,
            blockLight
         );
      }

      ColumnRenderView adjColxx = adjData[EDhDirection.WEST.compassIndex];
      adjSameDetailLevel = isAdjDataSameDetailLevel[EDhDirection.WEST.compassIndex];
      if (adjColxx == null) {
         if (!isTransparent || overVoid) {
            builder.addQuadAdj(
               EDhDirection.WEST,
               minX,
               minY,
               minZ,
               blockWidth,
               yHeight,
               color,
               tryGetTextureId(faceTileIdsByDirectionOrdinal, EDhDirection.WEST),
               irisBlockMaterialId,
               (byte)15,
               blockLight
            );
         }
      } else {
         makeAdjVerticalQuad(
            builder,
            phantomArrayCheckout,
            clientLevelWrapper,
            adjColxx,
            faceTileIdsByDirectionOrdinal,
            adjSameDetailLevel,
            caveCullingMaxY,
            EDhDirection.WEST,
            minX,
            minY,
            minZ,
            blockWidth,
            yHeight,
            color,
            irisBlockMaterialId,
            blockLight
         );
      }

      ColumnRenderView adjColxxx = adjData[EDhDirection.EAST.compassIndex];
      adjSameDetailLevel = isAdjDataSameDetailLevel[EDhDirection.EAST.compassIndex];
      if (adjColxxx == null) {
         if (!isTransparent || overVoid) {
            builder.addQuadAdj(
               EDhDirection.EAST,
               maxX,
               minY,
               minZ,
               blockWidth,
               yHeight,
               color,
               tryGetTextureId(faceTileIdsByDirectionOrdinal, EDhDirection.EAST),
               irisBlockMaterialId,
               (byte)15,
               blockLight
            );
         }
      } else {
         makeAdjVerticalQuad(
            builder,
            phantomArrayCheckout,
            clientLevelWrapper,
            adjColxxx,
            faceTileIdsByDirectionOrdinal,
            adjSameDetailLevel,
            caveCullingMaxY,
            EDhDirection.EAST,
            maxX,
            minY,
            minZ,
            blockWidth,
            yHeight,
            color,
            irisBlockMaterialId,
            blockLight
         );
      }
   }

   private static void makeAdjVerticalQuad(
      LodQuadBuilder builder,
      PhantomArrayListCheckout phantomArrayCheckout,
      IClientLevelWrapper clientLevelWrapper,
      @NotNull ColumnRenderView adjColumnView,
      @Nullable short[] faceTileIdsByDirectionOrdinal,
      boolean adjacentIsSameDetailLevel,
      int caveCullingMaxY,
      EDhDirection direction,
      short x,
      short yMin,
      short z,
      short horizontalBlockWidth,
      short ySize,
      int color,
      byte irisBlockMaterialId,
      byte blockLight
   ) {
      LongArrayList segments = phantomArrayCheckout.getLongArray(0, 0);
      LongArrayList newSegments = phantomArrayCheckout.getLongArray(1, 0);
      color = ColorUtil.applyShade(color, clientLevelWrapper.getShade(direction));
      if (adjColumnView.size != 0 && !RenderDataPointUtil.hasZeroHeight(adjColumnView.get(0))) {
         boolean transparencyEnabled = Config.Client.Advanced.Graphics.Quality.transparency.get() == EDhApiTransparency.COMPLETE;
         boolean inputTransparent = ColorUtil.getAlpha(color) < 255 && transparencyEnabled;
         short yMax = (short)(yMin + ySize);
         int adjCount = adjColumnView.size;
         segments.add(ColumnBox.YSegmentUtil.encode(yMin, yMax, (byte)15));

         for (int adjIndex = 0; adjIndex < adjCount; adjIndex++) {
            long adjPoint = adjColumnView.get(adjIndex);
            short adjMinY = RenderDataPointUtil.getYMin(adjPoint);
            short adjMaxY = RenderDataPointUtil.getYMax(adjPoint);
            if (RenderDataPointUtil.doesDataPointExist(adjPoint) && !RenderDataPointUtil.hasZeroHeight(adjPoint) && yMax > adjMinY) {
               long adjAbovePoint = adjIndex != 0 ? adjColumnView.get(adjIndex - 1) : 0L;
               long adjBelowPoint = adjIndex + 1 < adjCount ? adjColumnView.get(adjIndex + 1) : 0L;
               boolean adjOverVoid = !RenderDataPointUtil.doesDataPointExist(adjBelowPoint);
               boolean adjTransparent = !adjOverVoid && RenderDataPointUtil.getAlpha(adjPoint) < 255 && transparencyEnabled;
               byte adjSkyLight = RenderDataPointUtil.getLightSky(adjPoint);
               byte lightToApply;
               if (adjTransparent) {
                  lightToApply = RenderDataPointUtil.getLightSky(adjBelowPoint);
               } else {
                  boolean onBorder = direction == EDhDirection.WEST && x == 0
                     || direction == EDhDirection.NORTH && z == 0
                     || direction == EDhDirection.EAST && x == horizontalBlockWidth * 64
                     || direction == EDhDirection.SOUTH && z == horizontalBlockWidth * 64;
                  boolean isLit = RenderDataPointUtil.getLightSky(adjPoint) != 0 || RenderDataPointUtil.getLightBlock(adjPoint) != 0;
                  boolean useAdjLighting = onBorder && isLit && RenderDataPointUtil.getYMax(adjPoint) >= caveCullingMaxY;
                  lightToApply = useAdjLighting ? adjSkyLight : -1;
               }

               applyLightToRangeAndPopulateNewSgements(segments, newSegments, adjMinY, adjMaxY, lightToApply);
               LongArrayList temp = segments;
               segments = newSegments;
               newSegments = temp;
               short adjAboveMinY = RenderDataPointUtil.getYMin(adjAbovePoint);
               if (adjMaxY < adjAboveMinY) {
                  applyLightToRangeAndPopulateNewSgements(segments, temp, adjMaxY, adjAboveMinY, adjSkyLight);
                  LongArrayList tempx = segments;
                  segments = temp;
                  newSegments = tempx;
               }
            }
         }

         for (int i = 0; i < segments.size(); i++) {
            long segment = segments.getLong(i);
            tryAddVerticalFaceWithSkyLightToBuilder(
               builder,
               direction,
               x,
               z,
               horizontalBlockWidth,
               color,
               tryGetTextureId(faceTileIdsByDirectionOrdinal, direction),
               irisBlockMaterialId,
               blockLight,
               ColumnBox.YSegmentUtil.getSkyLight(segment),
               inputTransparent,
               ColumnBox.YSegmentUtil.getEndY(segment),
               ColumnBox.YSegmentUtil.getStartY(segment)
            );
         }
      } else {
         builder.addQuadAdj(
            direction,
            x,
            yMin,
            z,
            horizontalBlockWidth,
            ySize,
            color,
            tryGetTextureId(faceTileIdsByDirectionOrdinal, direction),
            irisBlockMaterialId,
            (byte)15,
            blockLight
         );
      }
   }

   private static void applyLightToRangeAndPopulateNewSgements(
      LongArrayList segments, LongArrayList newSegments, short rangeStart, short rangeEnd, byte newLight
   ) {
      newSegments.clear();

      for (int i = 0; i < segments.size(); i++) {
         long seg = segments.getLong(i);
         short endY = ColumnBox.YSegmentUtil.getEndY(seg);
         short startY = ColumnBox.YSegmentUtil.getStartY(seg);
         byte skyLight = ColumnBox.YSegmentUtil.getSkyLight(seg);
         if (endY > rangeStart && startY < rangeEnd) {
            if (startY < rangeStart) {
               newSegments.add(ColumnBox.YSegmentUtil.encode(startY, rangeStart, skyLight));
            }

            short overlapStart = (short)Math.max((int)startY, (int)rangeStart);
            short overlapEnd = (short)Math.min((int)endY, (int)rangeEnd);
            byte minLight = (byte)Math.min((int)newLight, (int)skyLight);
            newSegments.add(ColumnBox.YSegmentUtil.encode(overlapStart, overlapEnd, minLight));
            if (endY > rangeEnd) {
               newSegments.add(ColumnBox.YSegmentUtil.encode(rangeEnd, endY, skyLight));
            }
         } else {
            newSegments.add(seg);
         }
      }
   }

   private static void tryAddVerticalFaceWithSkyLightToBuilder(
      LodQuadBuilder builder,
      EDhDirection direction,
      short x,
      short z,
      short horizontalWidth,
      int color,
      short faceTextureId,
      byte irisBlockMaterialId,
      byte blockLight,
      byte lastSkyLight,
      boolean inputTransparent,
      int quadTopY,
      int quadBottomY
   ) {
      if (lastSkyLight >= 0) {
         if (!inputTransparent || lastSkyLight == 15) {
            short height = (short)(quadTopY - quadBottomY);
            if (height > 0) {
               builder.addQuadAdj(
                  direction, x, (short)quadBottomY, z, horizontalWidth, height, color, faceTextureId, irisBlockMaterialId, lastSkyLight, blockLight
               );
            }
         }
      }
   }

   private static short tryGetTextureId(@Nullable short[] faceTileIdsByDirectionOrdinal, EDhDirection direction) {
      return faceTileIdsByDirectionOrdinal == null ? 0 : faceTileIdsByDirectionOrdinal[direction.faceIndex];
   }

   private static class YSegmentUtil {
      private static final int HEIGHT_WIDTH = 16;
      private static final int SKY_LIGHT_WIDTH = 8;
      private static final int START_Y_MASK = (int)Math.pow(2.0, 16.0) - 1;
      private static final int END_Y_MASK = (int)Math.pow(2.0, 16.0) - 1;
      private static final int SKY_LIGHT_MASK = (int)Math.pow(2.0, 8.0) - 1;
      private static final int START_Y_OFFSET = 0;
      private static final int END_Y_OFFSET = 16;
      private static final int SKY_LIGHT_OFFSET = 32;

      public static long encode(short startY, short endY, byte skyLight) {
         long data = 0L;
         data |= (long)(startY & START_Y_MASK) << 0;
         data |= (long)(endY & END_Y_MASK) << 16;
         return data | (long)(skyLight & SKY_LIGHT_MASK) << 32;
      }

      public static short getStartY(long data) {
         return (short)(data >> 0 & START_Y_MASK);
      }

      public static short getEndY(long data) {
         return (short)(data >> 16 & END_Y_MASK);
      }

      public static byte getSkyLight(long data) {
         return (byte)(data >> 32 & SKY_LIGHT_MASK);
      }
   }
}
