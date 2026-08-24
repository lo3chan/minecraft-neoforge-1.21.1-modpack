package com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiDebugRendering;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.render.ColumnRenderSource;
import com.seibel.distanthorizons.core.dataObjects.render.columnViews.ColumnRenderView;
import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockTextureRegistry;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.RenderDataPointUtil;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import org.jetbrains.annotations.Nullable;

public class ColumnRenderBufferBuilder {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("Column Buffer Builder");

   public static void makeLodRenderData(
      LodQuadBuilder quadBuilder, ColumnRenderSource renderSource, IDhClientLevel clientLevel, ColumnRenderSource[] adjRegions, boolean[] isSameDetailLevel
   ) {
      boolean columnBuilderDebugEnabled = Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugEnable.get();
      if (columnBuilderDebugEnabled) {
         if (DhSectionPos.getDetailLevel(renderSource.pos) != Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugDetailLevel.get()
            || DhSectionPos.getX(renderSource.pos) != Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugXPos.get()
            || DhSectionPos.getZ(renderSource.pos) != Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugZPos.get()) {
            return;
         }

         boolean phantomArrayCheckout = false;
      }

      PhantomArrayListCheckout phantomArrayCheckout = ARRAY_LIST_POOL.checkoutLongArrays(2);

      try {
         ColumnRenderView columnRenderData = ColumnRenderView.getPooled();

         try {
            ColumnRenderView northAdjView = ColumnRenderView.getPooled();

            try {
               ColumnRenderView southAdjView = ColumnRenderView.getPooled();

               try {
                  ColumnRenderView eastAdjView = ColumnRenderView.getPooled();

                  try {
                     ColumnRenderView westAdjView = ColumnRenderView.getPooled();

                     try {
                        ColumnRenderView[] adjColumnViews = new ColumnRenderView[EDhDirection.CARDINAL_COMPASS.length];
                        adjColumnViews[EDhDirection.NORTH.compassIndex] = northAdjView;
                        adjColumnViews[EDhDirection.SOUTH.compassIndex] = southAdjView;
                        adjColumnViews[EDhDirection.EAST.compassIndex] = eastAdjView;
                        adjColumnViews[EDhDirection.WEST.compassIndex] = westAdjView;
                        byte thisDetailLevel = renderSource.getDataDetailLevel();

                        for (int relX = 0; relX < 64; relX++) {
                           for (int relZ = 0; relZ < 64; relZ++) {
                              renderSource.populateColumnView(columnRenderData, relX, relZ);
                              if (columnRenderData.size != 0
                                 && RenderDataPointUtil.doesDataPointExist(columnRenderData.get(0))
                                 && !RenderDataPointUtil.hasZeroHeight(columnRenderData.get(0))) {
                                 if (columnBuilderDebugEnabled) {
                                    int wantedX = Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugXRow.get();
                                    if (wantedX >= 0 && relX != wantedX) {
                                       continue;
                                    }

                                    int wantedZ = Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugZRow.get();
                                    if (wantedZ >= 0 && relZ != wantedZ) {
                                       continue;
                                    }
                                 }

                                 adjColumnViews[EDhDirection.NORTH.compassIndex].clear();
                                 adjColumnViews[EDhDirection.SOUTH.compassIndex].clear();
                                 adjColumnViews[EDhDirection.EAST.compassIndex].clear();
                                 adjColumnViews[EDhDirection.WEST.compassIndex].clear();

                                 for (EDhDirection direction : EDhDirection.CARDINAL_COMPASS) {
                                    try {
                                       int xAdj = relX + direction.normal.x;
                                       int zAdj = relZ + direction.normal.z;
                                       boolean isCrossRenderSourceBoundary = xAdj < 0 || xAdj >= 64 || zAdj < 0 || zAdj >= 64;
                                       ColumnRenderSource adjRenderSource;
                                       byte adjDetailLevel;
                                       if (!isCrossRenderSourceBoundary) {
                                          adjRenderSource = renderSource;
                                          adjDetailLevel = thisDetailLevel;
                                       } else {
                                          adjRenderSource = adjRegions[direction.compassIndex];
                                          if (adjRenderSource == null) {
                                             continue;
                                          }

                                          adjDetailLevel = adjRenderSource.getDataDetailLevel();
                                          if (adjDetailLevel == thisDetailLevel) {
                                             if (xAdj < 0) {
                                                xAdj += 64;
                                             }

                                             if (xAdj >= 64) {
                                                xAdj -= 64;
                                             }

                                             if (zAdj < 0) {
                                                zAdj += 64;
                                             }

                                             if (zAdj >= 64) {
                                                zAdj -= 64;
                                             }
                                          }
                                       }

                                       boolean expectedDetailLevels = adjDetailLevel == thisDetailLevel || adjDetailLevel > thisDetailLevel;
                                       if (!expectedDetailLevels) {
                                          LodUtil.assertNotReach(
                                             "Mismatch between adjacent detail level ["
                                                + adjDetailLevel
                                                + "] and this render source's detail level ["
                                                + thisDetailLevel
                                                + "]. Detail levels should be adj >= this."
                                          );
                                       }

                                       adjRenderSource.populateColumnView(adjColumnViews[direction.compassIndex], xAdj, zAdj);
                                    } catch (RuntimeException var32) {
                                       LOGGER.warn(
                                          "Failed to get adj data for relative pos: ["
                                             + thisDetailLevel
                                             + ":"
                                             + relX
                                             + ","
                                             + relZ
                                             + "] at ["
                                             + direction
                                             + "], Error: ["
                                             + var32.getMessage()
                                             + "].",
                                          var32
                                       );
                                    }
                                 }

                                 for (int i = 0; i < columnRenderData.size; i++) {
                                    if (Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugEnable.get()) {
                                       int wantedColumnIndex = Config.Client.Advanced.Debugging.ColumnBuilderDebugging.columnBuilderDebugColumnIndex.get();
                                       if (wantedColumnIndex >= 0 && i != wantedColumnIndex) {
                                          continue;
                                       }
                                    }

                                    long data = columnRenderData.get(i);
                                    if (RenderDataPointUtil.hasZeroHeight(data) || !RenderDataPointUtil.doesDataPointExist(data)) {
                                       break;
                                    }

                                    long topDataPoint = i - 1 >= 0 ? columnRenderData.get(i - 1) : 0L;
                                    long bottomDataPoint = i + 1 < columnRenderData.size ? columnRenderData.get(i + 1) : 0L;
                                    short[] faceTileIdsByDirectionOrdinal = null;
                                    if (renderSource.hasTextureSetIds()) {
                                       short textureSetId = renderSource.getTextureSetId(relX, relZ, i);
                                       faceTileIdsByDirectionOrdinal = BlockTextureRegistry.INSTANCE.getFaceTileIds(textureSetId);
                                    }

                                    addRenderDataPointToBuilder(
                                       clientLevel,
                                       phantomArrayCheckout,
                                       data,
                                       topDataPoint,
                                       bottomDataPoint,
                                       adjColumnViews,
                                       isSameDetailLevel,
                                       faceTileIdsByDirectionOrdinal,
                                       thisDetailLevel,
                                       relX,
                                       relZ,
                                       quadBuilder
                                    );
                                 }
                              }
                           }
                        }
                     } catch (Throwable var33) {
                        if (westAdjView != null) {
                           try {
                              westAdjView.close();
                           } catch (Throwable var31) {
                              var33.addSuppressed(var31);
                           }
                        }

                        throw var33;
                     }

                     if (westAdjView != null) {
                        westAdjView.close();
                     }
                  } catch (Throwable var34) {
                     if (eastAdjView != null) {
                        try {
                           eastAdjView.close();
                        } catch (Throwable var30) {
                           var34.addSuppressed(var30);
                        }
                     }

                     throw var34;
                  }

                  if (eastAdjView != null) {
                     eastAdjView.close();
                  }
               } catch (Throwable var35) {
                  if (southAdjView != null) {
                     try {
                        southAdjView.close();
                     } catch (Throwable var29) {
                        var35.addSuppressed(var29);
                     }
                  }

                  throw var35;
               }

               if (southAdjView != null) {
                  southAdjView.close();
               }
            } catch (Throwable var36) {
               if (northAdjView != null) {
                  try {
                     northAdjView.close();
                  } catch (Throwable var28) {
                     var36.addSuppressed(var28);
                  }
               }

               throw var36;
            }

            if (northAdjView != null) {
               northAdjView.close();
            }
         } catch (Throwable var37) {
            if (columnRenderData != null) {
               try {
                  columnRenderData.close();
               } catch (Throwable var27) {
                  var37.addSuppressed(var27);
               }
            }

            throw var37;
         }

         if (columnRenderData != null) {
            columnRenderData.close();
         }
      } catch (Throwable var38) {
         if (phantomArrayCheckout != null) {
            try {
               phantomArrayCheckout.close();
            } catch (Throwable var26) {
               var38.addSuppressed(var26);
            }
         }

         throw var38;
      }

      if (phantomArrayCheckout != null) {
         phantomArrayCheckout.close();
      }

      quadBuilder.mergeQuads();
   }

   private static void addRenderDataPointToBuilder(
      IDhClientLevel clientLevel,
      PhantomArrayListCheckout phantomArrayCheckout,
      long renderData,
      long topRenderData,
      long bottomRenderData,
      ColumnRenderView[] adjColumnViews,
      boolean[] isSameDetailLevel,
      @Nullable short[] faceTileIdsByDirectionOrdinal,
      byte detailLevel,
      int renderSourceOffsetPosX,
      int renderSourceOffsetPosZ,
      LodQuadBuilder quadBuilder
   ) {
      long sectionPos = DhSectionPos.encode(detailLevel, renderSourceOffsetPosX, renderSourceOffsetPosZ);
      short blockWidth = (short)DhSectionPos.getDetailLevelWidthInBlocks(detailLevel);
      short blockMinX = (short)DhSectionPos.getMinCornerBlockX(sectionPos);
      short blockMinY = RenderDataPointUtil.getYMin(renderData);
      short blockMinZ = (short)DhSectionPos.getMinCornerBlockZ(sectionPos);
      short blockMaxY = (short)(RenderDataPointUtil.getYMax(renderData) - blockMinY);
      if (blockMaxY != 0) {
         if (blockMaxY < 0) {
            throw new IllegalArgumentException("Negative y size for the renderDataPoint! Data: [" + RenderDataPointUtil.toString(renderData) + "].");
         } else {
            byte blockMaterialId = RenderDataPointUtil.getBlockMaterialId(renderData);
            boolean fullBright = false;
            EDhApiDebugRendering debugging = Config.Client.Advanced.Debugging.debugRenderingColors.get();
            int color;
            switch (debugging) {
               case OFF:
                  float saturationMultiplier = Config.Client.Advanced.Graphics.Quality.saturationMultiplier.get();
                  float brightnessMultiplier = Config.Client.Advanced.Graphics.Quality.brightnessMultiplier.get();
                  if (saturationMultiplier == 1.0 && brightnessMultiplier == 1.0) {
                     color = RenderDataPointUtil.getColor(renderData);
                  } else {
                     float[] ahsv = ColorUtil.argbToAhsv(RenderDataPointUtil.getColor(renderData));
                     color = ColorUtil.ahsvToArgb(ahsv[0], ahsv[1], ahsv[2] * saturationMultiplier, ahsv[3] * brightnessMultiplier);
                  }
                  break;
               case SHOW_DETAIL:
                  color = LodUtil.DEBUG_DETAIL_LEVEL_COLORS[detailLevel];
                  fullBright = true;
                  break;
               case SHOW_BLOCK_MATERIAL:
                  switch (EDhApiBlockMaterial.getFromIndex(blockMaterialId)) {
                     case UNKNOWN:
                     case AIR:
                        color = ColorUtil.HOT_PINK;
                        break;
                     case LEAVES:
                        color = ColorUtil.DARK_GREEN;
                        break;
                     case STONE:
                        color = ColorUtil.GRAY;
                        break;
                     case WOOD:
                        color = ColorUtil.BROWN;
                        break;
                     case METAL:
                        color = ColorUtil.DARK_GRAY;
                        break;
                     case DIRT:
                        color = ColorUtil.LIGHT_BROWN;
                        break;
                     case LAVA:
                        color = ColorUtil.ORANGE;
                        break;
                     case DEEPSLATE:
                        color = ColorUtil.BLACK;
                        break;
                     case SNOW:
                        color = ColorUtil.WHITE;
                        break;
                     case SAND:
                        color = ColorUtil.TAN;
                        break;
                     case TERRACOTTA:
                        color = ColorUtil.DARK_ORANGE;
                        break;
                     case NETHER_STONE:
                        color = ColorUtil.DARK_RED;
                        break;
                     case WATER:
                        color = ColorUtil.BLUE;
                        break;
                     case GRASS:
                        color = ColorUtil.GREEN;
                        break;
                     case ILLUMINATED:
                        color = ColorUtil.YELLOW;
                        break;
                     default:
                        color = ColorUtil.CYAN;
                  }

                  fullBright = true;
                  break;
               case SHOW_OVERLAPPING_QUADS:
                  color = ColorUtil.WHITE;
                  fullBright = true;
                  break;
               default:
                  throw new IllegalArgumentException("Unknown debug mode: " + debugging);
            }

            ColumnBox.addBoxQuadsToBuilder(
               quadBuilder,
               phantomArrayCheckout,
               clientLevel,
               blockWidth,
               blockMaxY,
               blockMinX,
               blockMinY,
               blockMinZ,
               color,
               blockMaterialId,
               RenderDataPointUtil.getLightSky(renderData),
               fullBright ? 15 : RenderDataPointUtil.getLightBlock(renderData),
               topRenderData,
               bottomRenderData,
               adjColumnViews,
               isSameDetailLevel,
               faceTileIdsByDirectionOrdinal
            );
         }
      }
   }
}
