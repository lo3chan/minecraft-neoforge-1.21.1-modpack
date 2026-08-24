package com.seibel.distanthorizons.core.dataObjects.transformers;

import com.seibel.distanthorizons.api.enums.config.EDhApiBlocksToAvoid;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dataObjects.render.ColumnRenderSource;
import com.seibel.distanthorizons.core.dataObjects.render.columnViews.ColumnRenderView;
import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockTextureRegistry;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.RenderDataPointReducingList;
import com.seibel.distanthorizons.core.util.RenderDataPointUtil;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.HashSet;
import org.jetbrains.annotations.Nullable;

public class FullDataToRenderDataTransformer {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private static final LongOpenHashSet BROKEN_POS_SET = new LongOpenHashSet();
   private static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("Data Transformer");
   private static HashSet<IBlockStateWrapper> snowLayerBlockStates = null;

   @Nullable
   public static ColumnRenderSource transformFullDataToRenderSource(@Nullable FullDataSourceV2 fullDataSource, @Nullable IClientLevelWrapper levelWrapper) {
      if (fullDataSource == null) {
         return null;
      } else if (levelWrapper == null) {
         return null;
      } else {
         try {
            return transformCompleteFullDataToColumnData(levelWrapper, fullDataSource);
         } catch (InterruptedException var3) {
            return null;
         }
      }
   }

   private static ColumnRenderSource transformCompleteFullDataToColumnData(IClientLevelWrapper levelWrapper, FullDataSourceV2 fullDataSource) throws InterruptedException {
      long pos = fullDataSource.getPos();
      byte dataDetail = fullDataSource.getDataDetailLevel();
      int maxVertSliceCount = Config.Client.Advanced.Graphics.Quality.verticalQuality
         .get()
         .calculateMaxNumberOfVerticalSlicesAtDetailLevel(fullDataSource.getDataDetailLevel());
      ColumnRenderSource columnSource = ColumnRenderSource.createEmpty(pos, maxVertSliceCount, levelWrapper.getMinHeight());
      if (fullDataSource.isEmpty) {
         return columnSource;
      } else {
         columnSource.markNotEmpty();
         int baseX = DhSectionPos.getMinCornerBlockX(pos);
         int baseZ = DhSectionPos.getMinCornerBlockZ(pos);
         ColumnRenderView columnArrayView = ColumnRenderView.getPooled();

         try {
            PhantomArrayListCheckout phantomCheckout = ARRAY_LIST_POOL.checkoutLongArrays(1);

            try {
               ColumnRenderView tempExpandingColumnView = ColumnRenderView.getPooled();

               try {
                  RenderDataPointReducingList reducingList = new RenderDataPointReducingList();

                  try {
                     DhBlockPosMutable mutableBlockPos = new DhBlockPosMutable();

                     for (int x = 0; x < 64; x++) {
                        for (int z = 0; z < 64; z++) {
                           columnSource.populateColumnView(columnArrayView, x, z);
                           LongArrayList dataColumn = fullDataSource.getColumnAtRelPos(x, z);
                           updateOrReplaceRenderDataViewColumnWithFullDataColumn(
                              levelWrapper,
                              fullDataSource,
                              baseX + BitShiftUtil.pow(x, dataDetail),
                              baseZ + BitShiftUtil.pow(z, dataDetail),
                              columnArrayView,
                              dataColumn,
                              columnSource,
                              x,
                              z,
                              phantomCheckout,
                              tempExpandingColumnView,
                              reducingList,
                              mutableBlockPos
                           );
                        }
                     }
                  } catch (Throwable var21) {
                     try {
                        reducingList.close();
                     } catch (Throwable var20) {
                        var21.addSuppressed(var20);
                     }

                     throw var21;
                  }

                  reducingList.close();
               } catch (Throwable var22) {
                  if (tempExpandingColumnView != null) {
                     try {
                        tempExpandingColumnView.close();
                     } catch (Throwable var19) {
                        var22.addSuppressed(var19);
                     }
                  }

                  throw var22;
               }

               if (tempExpandingColumnView != null) {
                  tempExpandingColumnView.close();
               }
            } catch (Throwable var23) {
               if (phantomCheckout != null) {
                  try {
                     phantomCheckout.close();
                  } catch (Throwable var18) {
                     var23.addSuppressed(var18);
                  }
               }

               throw var23;
            }

            if (phantomCheckout != null) {
               phantomCheckout.close();
            }
         } catch (Throwable var24) {
            if (columnArrayView != null) {
               try {
                  columnArrayView.close();
               } catch (Throwable var17) {
                  var24.addSuppressed(var17);
               }
            }

            throw var24;
         }

         if (columnArrayView != null) {
            columnArrayView.close();
         }

         return columnSource;
      }
   }

   private static void updateOrReplaceRenderDataViewColumnWithFullDataColumn(
      IClientLevelWrapper levelWrapper,
      FullDataSourceV2 fullDataSource,
      int blockX,
      int blockZ,
      ColumnRenderView columnArrayView,
      LongArrayList fullDataColumn,
      @Nullable ColumnRenderSource columnSource,
      int sourceRelX,
      int sourceRelZ,
      PhantomArrayListCheckout phantomCheckout,
      ColumnRenderView tempExpandingColumnView,
      RenderDataPointReducingList reducingList,
      DhBlockPosMutable mutableBlockPos
   ) {
      if (fullDataColumn != null && fullDataColumn.size() != 0) {
         int fullDataLength = fullDataColumn.size();
         if (fullDataLength <= columnArrayView.maxVerticalSliceCount) {
            setRenderColumnView(levelWrapper, fullDataSource, blockX, blockZ, columnArrayView, fullDataColumn, mutableBlockPos);
         } else {
            LongArrayList dataArrayList = phantomCheckout.getLongArray(0, fullDataLength);
            tempExpandingColumnView.populate(dataArrayList, fullDataLength, 0, fullDataLength);
            setRenderColumnView(levelWrapper, fullDataSource, blockX, blockZ, tempExpandingColumnView, fullDataColumn, mutableBlockPos);
            columnArrayView.changeVerticalSizeFrom(tempExpandingColumnView, reducingList);
         }

         if (columnSource != null && columnSource.hasTextureSetIds()) {
            applyTextureSetIds(fullDataSource, columnArrayView, fullDataColumn, columnSource, sourceRelX, sourceRelZ);
         }
      }
   }

   private static void applyTextureSetIds(
      FullDataSourceV2 fullDataSource,
      ColumnRenderView renderColumnData,
      LongArrayList fullDataColumn,
      ColumnRenderSource columnSource,
      int sourceRelX,
      int sourceRelZ
   ) {
      for (int renderIndex = 0; renderIndex < renderColumnData.size; renderIndex++) {
         long renderData = renderColumnData.get(renderIndex);
         if (!RenderDataPointUtil.doesDataPointExist(renderData) || RenderDataPointUtil.hasZeroHeight(renderData)) {
            break;
         }

         int topBlockY = RenderDataPointUtil.getYMax(renderData) - 1;
         short textureId = 0;
         int fullIndex = 0;

         while (true) {
            if (fullIndex < fullDataColumn.size()) {
               long fullData = fullDataColumn.getLong(fullIndex);
               int bottomY = FullDataPointUtil.getBottomY(fullData);
               if (bottomY > topBlockY) {
                  fullIndex++;
                  continue;
               }

               if (topBlockY < bottomY + FullDataPointUtil.getHeight(fullData)) {
                  try {
                     IBlockStateWrapper block = fullDataSource.mapping.getBlockStateWrapper(FullDataPointUtil.getId(fullData));
                     textureId = BlockTextureRegistry.INSTANCE.getOrRegisterBlockStateSetId(block);
                  } catch (IndexOutOfBoundsException var16) {
                  }
               }
            }

            columnSource.setTextureSetId(sourceRelX, sourceRelZ, renderIndex, textureId);
            break;
         }
      }
   }

   private static void setRenderColumnView(
      IClientLevelWrapper levelWrapper,
      FullDataSourceV2 fullDataSource,
      int blockX,
      int blockZ,
      ColumnRenderView renderColumnData,
      LongArrayList fullColumnData,
      DhBlockPosMutable mutableBlockPos
   ) {
      boolean ignoreNonCollidingBlocks = Config.Client.Advanced.Graphics.Culling.blocksToIgnore.get() == EDhApiBlocksToAvoid.NON_COLLIDING;
      boolean colorBelowWithAvoidedBlocks = Config.Client.Advanced.Graphics.Culling.tintWithAvoidedBlocks.get();
      ObjectOpenHashSet<IBlockStateWrapper> blockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredBlocks(levelWrapper);
      ObjectOpenHashSet<IBlockStateWrapper> caveBlockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredCaveBlocks(levelWrapper);
      ObjectOpenHashSet<IBlockStateWrapper> waterSubsurfaceReplacementBlocks = WRAPPER_FACTORY.getWaterSubsurfaceReplacementBlocks(levelWrapper);
      ObjectOpenHashSet<IBlockStateWrapper> waterSurfaceReplacementBlocks = WRAPPER_FACTORY.getWaterSurfaceReplacementBlocks(levelWrapper);
      IBlockStateWrapper water = WRAPPER_FACTORY.getWaterBlockStateWrapper(levelWrapper);
      if (snowLayerBlockStates == null) {
         snowLayerBlockStates = new HashSet<>();
         snowLayerBlockStates.add(WRAPPER_FACTORY.deserializeBlockStateWrapperOrGetDefault("minecraft:snow_STATE_{layers:1}", levelWrapper));
         snowLayerBlockStates.add(WRAPPER_FACTORY.deserializeBlockStateWrapperOrGetDefault("minecraft:snow_STATE_{layers:2}", levelWrapper));
         snowLayerBlockStates.add(WRAPPER_FACTORY.deserializeBlockStateWrapperOrGetDefault("minecraft:snow_STATE_{layers:3}", levelWrapper));
      }

      int caveCullingMaxY = Config.Client.Advanced.Graphics.Culling.caveCullingHeight.get() - levelWrapper.getMinHeight();
      boolean caveCullingEnabled = Config.Client.Advanced.Graphics.Culling.enableCaveCulling.get()
         && !levelWrapper.hasCeiling()
         && !levelWrapper.getDimensionType().isTheEnd();
      boolean isColumnVoid = true;
      int colorToApplyToNextBlock = -1;
      int lastColor = 0;
      int lastBottom = -10000;
      IBlockStateWrapper lastBlock = null;
      int skylightToApplyToNextBlock = -1;
      int blocklightToApplyToNextBlock = -1;
      int renderDataIndex = 0;
      FullDataPointIdMap fullDataMapping = fullDataSource.mapping;
      mutableBlockPos.setX(blockX);
      mutableBlockPos.setZ(blockZ);

      for (int fullDataIndex = 0; fullDataIndex < fullColumnData.size(); fullDataIndex++) {
         long fullData = fullColumnData.getLong(fullDataIndex);
         int bottomY = FullDataPointUtil.getBottomY(fullData);
         int blockHeight = FullDataPointUtil.getHeight(fullData);
         int topY = bottomY + blockHeight;
         int id = FullDataPointUtil.getId(fullData);
         int blockLight = FullDataPointUtil.getBlockLight(fullData);
         int skyLight = FullDataPointUtil.getSkyLight(fullData);
         mutableBlockPos.setY(bottomY + levelWrapper.getMinHeight());

         IBiomeWrapper biome;
         IBlockStateWrapper block;
         try {
            biome = fullDataMapping.getBiomeWrapper(id);
            block = fullDataMapping.getBlockStateWrapper(id);
         } catch (IndexOutOfBoundsException var44) {
            if (!BROKEN_POS_SET.contains(fullDataMapping.getPos())) {
               BROKEN_POS_SET.add(fullDataMapping.getPos());
               String levelId = levelWrapper.getDhIdentifier();
               LOGGER.warn(
                  "Unable to get data point with id ["
                     + id
                     + "] (Max possible ID: ["
                     + fullDataMapping.getMaxValidId()
                     + "]) for pos ["
                     + fullDataMapping.getPos()
                     + "] in level ["
                     + levelId
                     + "]. Error: ["
                     + var44.getMessage()
                     + "]. Further errors for this position won't be logged."
               );
            }
            continue;
         }

         if (waterSubsurfaceReplacementBlocks.contains(block) && (lastBlock == null || lastBlock.isAir())) {
            block = water;
         }

         boolean ignoreBlock = blockStatesToIgnore.contains(block);
         boolean caveBlock = caveBlockStatesToIgnore.contains(block);
         if (!caveBlock && block.isSolid() && !block.isLiquid() && block.getOpacity() >= 16) {
            if (ignoreBlock) {
               skylightToApplyToNextBlock = skyLight;
               continue;
            }
         } else {
            if (caveCullingEnabled
               && skyLight == 0
               && topY < caveCullingMaxY
               && renderDataIndex != 0
               && fullDataIndex != 0
               && fullDataIndex + 1 < fullColumnData.size()) {
               long nextFullData = fullColumnData.getLong(fullDataIndex + 1);
               int nextSkyLight = FullDataPointUtil.getSkyLight(nextFullData);
               if (nextSkyLight == 0 && ColorUtil.getAlpha(lastColor) == 255) {
                  long columnData = renderColumnData.get(renderDataIndex - 1);
                  columnData = RenderDataPointUtil.setYMin(columnData, bottomY);
                  renderColumnData.set(renderDataIndex - 1, columnData);
               }
               continue;
            }

            if (ignoreBlock) {
               continue;
            }
         }

         boolean ignoreNonSolidBlock = ignoreNonCollidingBlocks && !block.isSolid() && !block.isLiquid() && block.getOpacity() != 16;
         boolean isSnowLayer = snowLayerBlockStates.contains(block);
         boolean isWaterSurfaceReplacement = waterSurfaceReplacementBlocks.contains(block);
         if (isSnowLayer || isWaterSurfaceReplacement) {
            if (isWaterSurfaceReplacement) {
               block = WRAPPER_FACTORY.getWaterBlockStateWrapper(levelWrapper);
            }

            if (--blockHeight == 0) {
               ignoreNonSolidBlock = true;
               if (isSnowLayer) {
                  int snowColor = levelWrapper.getBlockColor(mutableBlockPos, biome, fullDataSource, block);
                  colorToApplyToNextBlock = ColorUtil.setAlpha(snowColor, 255);
               } else {
                  colorToApplyToNextBlock = levelWrapper.getBlockColor(mutableBlockPos, biome, fullDataSource, block);
               }
            }
         }

         if (ignoreNonSolidBlock) {
            int ignoredColor = levelWrapper.getBlockColor(mutableBlockPos, biome, fullDataSource, block);
            int ignoredAlpha = ColorUtil.getAlpha(ignoredColor);
            if (colorBelowWithAvoidedBlocks && ignoredAlpha != 0) {
               colorToApplyToNextBlock = ColorUtil.setAlpha(ignoredColor, 255);
            }

            if (ignoredAlpha != 0) {
               skylightToApplyToNextBlock = skyLight;
               blocklightToApplyToNextBlock = blockLight;
            }
         } else {
            int color;
            if (colorToApplyToNextBlock == -1) {
               color = levelWrapper.getBlockColor(mutableBlockPos, biome, fullDataSource, block);
               if (skylightToApplyToNextBlock != -1) {
                  skyLight = skylightToApplyToNextBlock;
                  skylightToApplyToNextBlock = -1;
               }

               if (blocklightToApplyToNextBlock != -1) {
                  blockLight = blocklightToApplyToNextBlock;
                  blocklightToApplyToNextBlock = -1;
               }
            } else {
               color = colorToApplyToNextBlock;
               colorToApplyToNextBlock = -1;
               if (skylightToApplyToNextBlock != -1) {
                  skyLight = skylightToApplyToNextBlock;
               }

               if (blocklightToApplyToNextBlock != -1) {
                  blockLight = blocklightToApplyToNextBlock;
               }
            }

            if (color == lastColor && bottomY + blockHeight == lastBottom && renderDataIndex > 0) {
               long columnData = renderColumnData.get(renderDataIndex - 1);
               columnData = RenderDataPointUtil.setYMin(columnData, bottomY);
               renderColumnData.set(renderDataIndex - 1, columnData);
            } else {
               isColumnVoid = false;
               long columnData = RenderDataPointUtil.createDataPoint(bottomY + blockHeight, bottomY, color, skyLight, blockLight, block.getMaterialId());
               renderColumnData.set(renderDataIndex, columnData);
               renderDataIndex++;
            }

            lastBottom = bottomY;
            lastColor = color;
            lastBlock = block;
         }
      }

      if (isColumnVoid) {
         renderColumnData.set(0, 0L);
      }
   }
}
