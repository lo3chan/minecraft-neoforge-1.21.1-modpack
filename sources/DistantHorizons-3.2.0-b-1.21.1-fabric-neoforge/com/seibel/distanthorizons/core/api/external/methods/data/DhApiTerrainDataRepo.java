package com.seibel.distanthorizons.core.api.external.methods.data;

import com.seibel.distanthorizons.api.interfaces.data.IDhApiTerrainDataCache;
import com.seibel.distanthorizons.api.interfaces.data.IDhApiTerrainDataRepo;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.data.DhApiRaycastResult;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3i;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.util.DhApiTerrainDataPointUtil;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.RayCastUtil;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.util.math.DhVec3i;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.jetbrains.annotations.Nullable;

public class DhApiTerrainDataRepo implements IDhApiTerrainDataRepo {
   public static DhApiTerrainDataRepo INSTANCE = new DhApiTerrainDataRepo();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final AbstractDebugWireframeRenderer DEBUG_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   private static volatile boolean debugThreadRunning = false;
   private static DhApiTerrainDataCache debugDataCache = new DhApiTerrainDataCache();
   private static DhApiVec3i currentDebugVec3i = new DhVec3i();

   private DhApiTerrainDataRepo() {
   }

   @Override
   public DhApiResult<DhApiTerrainDataPoint> getSingleDataPointAtBlockPos(
      IDhApiLevelWrapper levelWrapper, int blockPosX, int blockPosY, int blockPosZ, IDhApiTerrainDataCache dataCache
   ) {
      return getTerrainDataAtBlockYPos(levelWrapper, DhSectionPos.encode((byte)0, blockPosX, blockPosZ), blockPosY, dataCache);
   }

   @Override
   public DhApiResult<DhApiTerrainDataPoint[]> getColumnDataAtBlockPos(
      IDhApiLevelWrapper levelWrapper, int blockPosX, int blockPosZ, IDhApiTerrainDataCache dataCache
   ) {
      return getTerrainDataColumnArray(levelWrapper, DhSectionPos.encode((byte)0, blockPosX, blockPosZ), null, dataCache);
   }

   @Override
   public DhApiResult<DhApiTerrainDataPoint[][][]> getAllTerrainDataAtChunkPos(
      IDhApiLevelWrapper levelWrapper, int chunkPosX, int chunkPosZ, IDhApiTerrainDataCache dataCache
   ) {
      return getTerrainDataOverAreaForPositionDetailLevel(levelWrapper, DhSectionPos.encode((byte)4, chunkPosX, chunkPosZ), dataCache);
   }

   @Override
   public DhApiResult<DhApiTerrainDataPoint[][][]> getAllTerrainDataAtRegionPos(
      IDhApiLevelWrapper levelWrapper, int regionPosX, int regionPosZ, IDhApiTerrainDataCache dataCache
   ) {
      return getTerrainDataOverAreaForPositionDetailLevel(levelWrapper, DhSectionPos.encode((byte)9, regionPosX, regionPosZ), dataCache);
   }

   @Override
   public DhApiResult<DhApiTerrainDataPoint[][][]> getAllTerrainDataAtDetailLevelAndPos(
      IDhApiLevelWrapper levelWrapper, byte detailLevel, int posX, int posZ, IDhApiTerrainDataCache dataCache
   ) {
      return getTerrainDataOverAreaForPositionDetailLevel(levelWrapper, DhSectionPos.encode(detailLevel, posX, posZ), dataCache);
   }

   private static DhApiResult<DhApiTerrainDataPoint> getTerrainDataAtBlockYPos(
      IDhApiLevelWrapper levelWrapper, long requestedColumnPos, Integer blockYPos, IDhApiTerrainDataCache dataCache
   ) {
      DhApiResult<DhApiTerrainDataPoint[]> result = getTerrainDataColumnArray(levelWrapper, requestedColumnPos, blockYPos, dataCache);
      return result.success && ((DhApiTerrainDataPoint[])result.payload).length > 0
         ? DhApiResult.createSuccess(result.message, result.payload[0])
         : DhApiResult.createFail(result.message);
   }

   private static DhApiResult<DhApiTerrainDataPoint[][][]> getTerrainDataOverAreaForPositionDetailLevel(
      IDhApiLevelWrapper levelWrapper, long requestedAreaPos, IDhApiTerrainDataCache dataCache
   ) {
      byte requestedDetailLevel = DhSectionPos.getDetailLevel(requestedAreaPos);
      long startingBlockPos = DhSectionPos.encode(
         (byte)0,
         DhSectionPos.getX(requestedAreaPos) * BitShiftUtil.powerOfTwo(requestedDetailLevel - 0),
         DhSectionPos.getZ(requestedAreaPos) * BitShiftUtil.powerOfTwo(requestedDetailLevel - 0)
      );
      int widthOfAreaInBlocks = BitShiftUtil.powerOfTwo((int)requestedDetailLevel);
      DhApiTerrainDataPoint[][][] returnArray = new DhApiTerrainDataPoint[widthOfAreaInBlocks][widthOfAreaInBlocks][];
      int dataColumnsReturned = 0;

      for (int x = 0; x < widthOfAreaInBlocks; x++) {
         for (int z = 0; z < widthOfAreaInBlocks; z++) {
            long blockColumnPos = DhSectionPos.encode((byte)0, DhSectionPos.getX(startingBlockPos) + x, DhSectionPos.getZ(startingBlockPos) + z);
            DhApiResult<DhApiTerrainDataPoint[]> result = getTerrainDataColumnArray(levelWrapper, blockColumnPos, null, dataCache);
            if (!result.success) {
               return DhApiResult.createFail(result.message, returnArray);
            }

            returnArray[x][z] = result.payload;
            dataColumnsReturned++;
         }
      }

      return dataColumnsReturned != 0
         ? DhApiResult.createSuccess("[" + dataColumnsReturned + "] columns returned.", returnArray)
         : DhApiResult.createSuccess("No data found.", returnArray);
   }

   private static DhApiResult<DhApiTerrainDataPoint[]> getTerrainDataColumnArray(
      IDhApiLevelWrapper levelWrapper, long requestedColumnPos, Integer nullableBlockYPos, IDhApiTerrainDataCache apiDataCache
   ) {
      AbstractDhWorld currentWorld = SharedApi.getAbstractDhWorld();
      if (currentWorld == null) {
         return DhApiResult.createFail("Unable to get terrain data before the world has loaded.");
      } else if (!(levelWrapper instanceof ILevelWrapper)) {
         return DhApiResult.createFail(
            "Unsupported ["
               + IDhApiLevelWrapper.class.getSimpleName()
               + "] implementation, only the core class ["
               + IDhLevel.class.getSimpleName()
               + "] is a valid parameter."
         );
      } else {
         ILevelWrapper coreLevelWrapper = (ILevelWrapper)levelWrapper;
         if (apiDataCache == null) {
            return DhApiResult.createFail(
               "Missing [" + IDhApiTerrainDataCache.class.getSimpleName() + "], if a cache isn't provided your repo operations will be significantly slower."
            );
         } else if (!(apiDataCache instanceof DhApiTerrainDataCache)) {
            return DhApiResult.createFail(
               "Unsupported ["
                  + IDhApiTerrainDataCache.class.getSimpleName()
                  + "] implementation, only the core class ["
                  + DhApiTerrainDataCache.class.getSimpleName()
                  + "] is a valid parameter."
            );
         } else {
            DhApiTerrainDataCache dataCache = (DhApiTerrainDataCache)apiDataCache;
            IDhLevel level = currentWorld.getLevel(coreLevelWrapper);
            if (level == null) {
               return DhApiResult.createFail("Unable to get terrain data before the world has loaded.");
            } else {
               byte requestedDetailLevel = DhSectionPos.getDetailLevel(requestedColumnPos);
               byte sectionDetailLevel = (byte)(requestedDetailLevel + 6);
               long sectionPos = DhSectionPos.convertToDetailLevel(requestedColumnPos, sectionDetailLevel);
               long relativePos = DhSectionPos.getDhSectionRelativePositionForDetailLevel(requestedColumnPos, DhSectionPos.getDetailLevel(requestedColumnPos));
               FullDataSourceV2 dataSource = null;

               try {
                  if (dataCache != null) {
                     dataSource = dataCache.get(sectionPos);
                  }

                  if (dataSource == null) {
                     dataSource = level.getFullDataProvider().getAsync(sectionPos).get();
                     if (dataSource == null) {
                        return DhApiResult.createFail(
                           "Unable to find/generate any data at the " + DhSectionPos.class.getSimpleName() + " [" + DhSectionPos.toString(sectionPos) + "]."
                        );
                     }

                     if (dataCache != null) {
                        dataCache.add(sectionPos, dataSource);
                     }
                  }

                  FullDataPointIdMap mapping = dataSource.mapping;
                  LongArrayList dataColumn = dataSource.getColumnAtRelPos(DhSectionPos.getX(relativePos), DhSectionPos.getZ(relativePos));
                  if (dataColumn == null) {
                     return DhApiResult.createSuccess(new DhApiTerrainDataPoint[0]);
                  } else {
                     int dataColumnIndexCount = dataColumn.size();
                     DhApiTerrainDataPoint[] returnArray = new DhApiTerrainDataPoint[dataColumnIndexCount];
                     boolean getSpecificYCoordinate = nullableBlockYPos != null;
                     int levelMinimumHeight = levelWrapper.getMinHeight();

                     for (int i = 0; i < dataColumnIndexCount; i++) {
                        long dataPoint = dataColumn.getLong(i);
                        if (!getSpecificYCoordinate) {
                           returnArray[i] = DhApiTerrainDataPointUtil.createApiDatapoint(levelWrapper.getMinHeight(), mapping, requestedDetailLevel, dataPoint);
                        } else if (dataPoint != 0L) {
                           int requestedY = nullableBlockYPos;
                           int bottomY = FullDataPointUtil.getBottomY(dataPoint) + levelMinimumHeight;
                           int height = FullDataPointUtil.getHeight(dataPoint);
                           int topY = bottomY + height;
                           if (bottomY <= requestedY && requestedY < topY) {
                              DhApiTerrainDataPoint apiTerrainData = DhApiTerrainDataPointUtil.createApiDatapoint(
                                 levelWrapper.getMinHeight(), mapping, requestedDetailLevel, dataPoint
                              );
                              return DhApiResult.createSuccess(new DhApiTerrainDataPoint[]{apiTerrainData});
                           }
                        }
                     }

                     return DhApiResult.createSuccess(returnArray);
                  }
               } catch (ExecutionException | InterruptedException var35) {
                  LOGGER.error("getTerrainDataColumnArray operation canceled. Error: [" + var35.getMessage() + "]", var35);
                  return DhApiResult.createFail("Operation cancled before it could complete: [" + var35.getMessage() + "].");
               } catch (Exception var36) {
                  LOGGER.error("Unexpected exception in getTerrainDataColumnArray. Error: [" + var36.getMessage() + "]", var36);
                  return DhApiResult.createFail("Unexpected exception: [" + var36.getMessage() + "].");
               } finally {
                  if (dataCache == null && dataSource != null) {
                     dataSource.close();
                  }
               }
            }
         }
      }
   }

   @Override
   public DhApiResult<DhApiRaycastResult> raycast(
      IDhApiLevelWrapper levelWrapper,
      double rayOriginX,
      double rayOriginY,
      double rayOriginZ,
      float rayDirectionX,
      float rayDirectionY,
      float rayDirectionZ,
      int maxRayBlockLength,
      @Nullable IDhApiTerrainDataCache dataCache
   ) {
      return this.raycastLodData(
         levelWrapper, new DhVec3d(rayOriginX, rayOriginY, rayOriginZ), new DhVec3f(rayDirectionX, rayDirectionY, rayDirectionZ), maxRayBlockLength, dataCache
      );
   }

   private DhApiResult<DhApiRaycastResult> raycastLodData(
      IDhApiLevelWrapper levelWrapper, DhVec3d rayOrigin, DhVec3f rayDirection, int maxRayBlockLength, @Nullable IDhApiTerrainDataCache dataCache
   ) {
      rayDirection.normalize();
      int minLevelBlockHeight = levelWrapper.getMinHeight();
      int maxLevelBlockHeight = levelWrapper.getMaxHeight();
      int currentLength = 0;
      DhVec3d exactPos = new DhVec3d(rayOrigin.x, rayOrigin.y, rayOrigin.z);
      DhVec3i blockPos = new DhVec3i((int)Math.round(rayOrigin.x), (int)Math.round(rayOrigin.y), (int)Math.round(rayOrigin.z));

      for (DhApiRaycastResult closetFoundDataPoint = null;
         blockPos.y >= minLevelBlockHeight && blockPos.y < maxLevelBlockHeight && currentLength <= maxRayBlockLength;
         currentLength = (int)(Math.abs(rayOrigin.x - exactPos.x) + Math.abs(rayOrigin.y - exactPos.y) + Math.abs(rayOrigin.z - exactPos.z))
      ) {
         for (DhVec3i columnPos : getIntersectingColumnsAtPosition(blockPos, rayDirection)) {
            DhApiResult<DhApiTerrainDataPoint[]> result = this.getColumnDataAtBlockPos(levelWrapper, columnPos.x, columnPos.z, dataCache);
            if (!result.success) {
               return DhApiResult.createFail(result.message);
            }

            for (DhApiTerrainDataPoint dataPoint : result.payload) {
               if (dataPoint.blockStateWrapper != null && !dataPoint.blockStateWrapper.isAir()) {
                  DhVec3i dataPointPos = new DhVec3i(columnPos.x, dataPoint.bottomYBlockPos, columnPos.z);
                  if (exactPos.y >= dataPoint.bottomYBlockPos && exactPos.y <= dataPoint.topYBlockPos) {
                     if (closetFoundDataPoint == null) {
                        closetFoundDataPoint = new DhApiRaycastResult(dataPoint, dataPointPos);
                     } else {
                        double previousDistanceSquared = Math.pow(rayOrigin.x - closetFoundDataPoint.pos.x, 2.0)
                           + Math.pow(rayOrigin.y - closetFoundDataPoint.pos.y, 2.0)
                           + Math.pow(rayOrigin.z - closetFoundDataPoint.pos.z, 2.0);
                        double newDistanceSquared = Math.pow(rayOrigin.x - dataPointPos.x, 2.0)
                           + Math.pow(rayOrigin.y - dataPointPos.y, 2.0)
                           + Math.pow(rayOrigin.z - dataPointPos.z, 2.0);
                        if (previousDistanceSquared > newDistanceSquared) {
                           closetFoundDataPoint = new DhApiRaycastResult(dataPoint, dataPointPos);
                        }
                     }
                  }
               }
            }
         }

         if (closetFoundDataPoint != null) {
            return DhApiResult.createSuccess(closetFoundDataPoint);
         }

         exactPos.x = exactPos.x + rayDirection.x;
         exactPos.y = exactPos.y + rayDirection.y;
         exactPos.z = exactPos.z + rayDirection.z;
         blockPos.x = (int)Math.round(exactPos.x);
         blockPos.y = (int)Math.round(exactPos.y);
         blockPos.z = (int)Math.round(exactPos.z);
      }

      return DhApiResult.createSuccess(null);
   }

   private static ArrayList<DhVec3i> getIntersectingColumnsAtPosition(DhVec3i rayEndingPos, DhVec3f rayDirection) {
      ArrayList<DhVec3i> returnList = new ArrayList<>(9);

      for (int x = -1; x <= 1; x++) {
         for (int z = -1; z <= 1; z++) {
            DhVec3i pos = new DhVec3i(rayEndingPos.x + x, rayEndingPos.y, rayEndingPos.z + z);
            if (RayCastUtil.rayIntersectsSquare(rayEndingPos.x, rayEndingPos.z, rayDirection.x, rayDirection.z, pos.x, pos.z, 1.0)) {
               returnList.add(pos);
            }
         }
      }

      return returnList;
   }

   @Override
   public DhApiResult<Void> overwriteChunkDataAsync(IDhApiLevelWrapper levelWrapper, Object[] chunkObjectArray) throws ClassCastException {
      if (!(levelWrapper instanceof ILevelWrapper)) {
         return DhApiResult.createFail("Level wrapper needs to be an instance of [" + IDhApiLevelWrapper.class.getSimpleName() + "].");
      } else {
         AbstractDhWorld dhWorld = SharedApi.getAbstractDhWorld();
         if (dhWorld == null) {
            return DhApiResult.createFail("No world loaded. This method can only be called while in a loaded world.");
         } else {
            IDhLevel dhLevel = dhWorld.getLevel((ILevelWrapper)levelWrapper);
            if (dhLevel == null) {
               return DhApiResult.createFail(
                  "No level exists for the given level wrapper. This either means the level hasn't been loaded yet, or was unloaded."
               );
            } else {
               IChunkWrapper chunk = SingletonInjector.INSTANCE.get(IWrapperFactory.class).createChunkWrapper(chunkObjectArray);
               SharedApi.INSTANCE.applyChunkUpdate(chunk, dhLevel.getLevelWrapper(), false);
               return DhApiResult.createSuccess();
            }
         }
      }
   }

   @Override
   public IDhApiTerrainDataCache createSoftCache() {
      return new DhApiTerrainDataCache();
   }

   public static void asyncDebugMethod(IDhApiLevelWrapper levelWrapper, int blockPosX, int blockPosY, int blockPosZ) {
      if (!debugThreadRunning) {
         debugThreadRunning = true;
         Thread thread = new Thread(
            () -> {
               try {
                  DhApiResult<DhApiTerrainDataPoint> single = getTerrainDataAtBlockYPos(
                     levelWrapper, DhSectionPos.encode((byte)0, blockPosX, blockPosZ), blockPosY, debugDataCache
                  );
                  DhApiResult<DhApiTerrainDataPoint[]> column = getTerrainDataColumnArray(
                     levelWrapper, DhSectionPos.encode((byte)0, blockPosX, blockPosZ), null, debugDataCache
                  );
                  long chunkPos = DhSectionPos.encodeContaining((byte)4, new DhChunkPos(blockPosX, blockPosZ));
                  DhApiResult<DhApiTerrainDataPoint[][][]> area = getTerrainDataOverAreaForPositionDetailLevel(levelWrapper, chunkPos, debugDataCache);
                  IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
                  DhApiResult<DhApiRaycastResult> rayCast = INSTANCE.raycastLodData(
                     levelWrapper, MC_RENDER.getCameraExactPosition(), MC_RENDER.getLookAtVector(), 1000, debugDataCache
                  );
                  if (rayCast.payload != null && !rayCast.payload.pos.equals(currentDebugVec3i)) {
                     currentDebugVec3i = rayCast.payload.pos;
                     String blockString = "[NULL BLOCK]";
                     if (rayCast.payload.dataPoint.blockStateWrapper != null) {
                        if (!rayCast.payload.dataPoint.blockStateWrapper.isAir() && rayCast.payload.dataPoint.blockStateWrapper.getWrappedMcObject() != null) {
                           blockString = rayCast.payload.dataPoint.blockStateWrapper.getWrappedMcObject().toString();
                        } else {
                           blockString = "[AIR]";
                        }
                     }

                     LOGGER.info("raycast: " + currentDebugVec3i + "\t block: " + blockString);
                  } else if (rayCast.payload == null && currentDebugVec3i != null) {
                     currentDebugVec3i = null;
                     LOGGER.info("raycast: [INFINITY]");
                  }

                  if (rayCast.success && rayCast.payload != null) {
                     DEBUG_RENDERER.makeParticle(
                        new AbstractDebugWireframeRenderer.BoxParticle(
                           new AbstractDebugWireframeRenderer.Box(
                              DhSectionPos.encode((byte)0, rayCast.payload.pos.x, rayCast.payload.pos.z),
                              rayCast.payload.dataPoint.bottomYBlockPos,
                              rayCast.payload.dataPoint.topYBlockPos,
                              -0.1F,
                              Color.RED
                           ),
                           1.0,
                           0.0F
                        )
                     );
                  }

                  boolean var17 = false;
               } catch (Exception var15) {
                  LOGGER.error("Test method Error: [" + var15.getMessage() + "]", var15);
               } finally {
                  debugThreadRunning = false;
               }
            }
         );
         thread.start();
      }
   }
}
