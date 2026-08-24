package com.seibel.distanthorizons.common.wrappers.world;

import com.seibel.distanthorizons.api.enums.config.EDhApiLodShading;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiLevelType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderRegister;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.ClientBlockStateColorCache_neoforge;
import com.seibel.distanthorizons.common.wrappers.level.KeyedClientLevelManager_neoforge;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.level.IKeyedClientLevelManager;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.world.AbstractDhWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IDimensionTypeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientLevelWrapper_neoforge implements IClientLevelWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Map<ClientLevel, WeakReference<ClientLevelWrapper_neoforge>> LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL = Collections.synchronizedMap(
      new WeakHashMap<>()
   );
   private static final KeyedClientLevelManager_neoforge KEYED_CLIENT_LEVEL_MANAGER = (KeyedClientLevelManager_neoforge)SingletonInjector.INSTANCE
      .get(IKeyedClientLevelManager.class);
   private static final Minecraft MINECRAFT = Minecraft.getInstance();
   private static final ThreadLocal<DhBlockPosMutable> MUTABLE_BLOCK_POS_THREAD_LOCAL = ThreadLocal.withInitial(DhBlockPosMutable::new);
   private static final Timer CLIENT_CLEANUP_TIMER = TimerUtil.CreateTimer("ClientLevelTickCleanup");
   private static final TimerTask CLIENT_CLEANUP_TASK = TimerUtil.createTimerTask(ClientLevelWrapper_neoforge::tickCleanup);
   private static final long INACTIVE_TIME_BEFORE_UNLOADED_IN_MS = 30000L;
   private final ClientLevel level;
   private final ConcurrentHashMap<BlockState, ClientBlockStateColorCache_neoforge> blockColorCacheByBlockState = new ConcurrentHashMap<>();
   private final Function<BlockState, ClientBlockStateColorCache_neoforge> createCachedBlockColorCacheFunc = blockState -> new ClientBlockStateColorCache_neoforge(
      blockState, this
   );
   private boolean cloudColorFailLogged = false;
   private volatile BlockStateWrapper_neoforge dirtBlockWrapper;
   private volatile IDhLevel dhLevel;
   private volatile long lastAccessTime = System.currentTimeMillis();
   private IDimensionTypeWrapper dimensionTypeWrapper = null;
   private String dimensionName = null;
   private Boolean dimHasCeiling = null;
   private Boolean dimHasSkyLight = null;
   private Integer dimMaxHeight = null;
   private Integer dimMinHeight = null;

   protected ClientLevelWrapper_neoforge(ClientLevel level) {
      this.level = level;
   }

   @Override
   public synchronized void markAccessed() {
      this.lastAccessTime = System.currentTimeMillis();
   }

   public synchronized long getLastAccessTime() {
      return this.lastAccessTime;
   }

   public static void tickCleanup() {
      ClientLevel clientLevel = MINECRAFT.level;
      if (clientLevel != null) {
         long currentTime = System.currentTimeMillis();
         ArrayList<ClientLevelWrapper_neoforge> levelsToUnload = new ArrayList<>();
         synchronized (LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL) {
            for (WeakReference<ClientLevelWrapper_neoforge> ref : LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL.values()) {
               ClientLevelWrapper_neoforge levelWrapper = ref.get();
               if (levelWrapper != null && levelWrapper.level != clientLevel) {
                  long inactiveTimeMs = currentTime - levelWrapper.getLastAccessTime();
                  if (inactiveTimeMs > 30000L) {
                     levelsToUnload.add(levelWrapper);
                  }
               }
            }
         }

         for (ClientLevelWrapper_neoforge wrapper : levelsToUnload) {
            synchronized (wrapper) {
               long inactiveTimeMs = currentTime - wrapper.getLastAccessTime();
               if (wrapper.level != clientLevel && inactiveTimeMs > 30000L) {
                  LOGGER.debug("Unloading level [" + wrapper.getDhIdentifier() + "] due to inactivity");
                  wrapper.tryUnloadFromWorld();
               }
            }
         }
      }
   }

   @Override
   public void setDhLevel(IDhLevel dhLevel) {
      this.dhLevel = dhLevel;
   }

   @Override
   public IDhLevel getDhLevel() {
      return this.dhLevel;
   }

   @Nullable
   public static IClientLevelWrapper getWrapperIfDifferent(@Nullable IClientLevelWrapper levelWrapper, @NotNull ClientLevel level) {
      if (KEYED_CLIENT_LEVEL_MANAGER.isEnabled()) {
         IServerKeyedClientLevel keyedLevel = KEYED_CLIENT_LEVEL_MANAGER.getServerKeyedLevel(getWrapper(level, true));
         if (keyedLevel != levelWrapper) {
            return getWrapper(level);
         }
      }

      ClientLevelWrapper_neoforge clientLevelWrapper = (ClientLevelWrapper_neoforge)levelWrapper;
      return (IClientLevelWrapper)(clientLevelWrapper != null && clientLevelWrapper.level == level ? clientLevelWrapper : getWrapper(level));
   }

   @Nullable
   public static IClientLevelWrapper getWrapper(@NotNull ClientLevel level) {
      return getWrapper(level, false);
   }

   @Nullable
   public static IClientLevelWrapper getWrapper(@Nullable ClientLevel level, boolean bypassLevelKeyManager) {
      if (!bypassLevelKeyManager) {
         if (level == null) {
            return null;
         }

         IServerKeyedClientLevel overrideLevel = KEYED_CLIENT_LEVEL_MANAGER.getServerKeyedLevel(getWrapper(level, true));
         if (overrideLevel != null) {
            WeakReference<ClientLevelWrapper_neoforge> levelRef = LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL.get(level);
            if (levelRef != null && levelRef.get() != overrideLevel) {
               ClientLevelWrapper_neoforge levelWrapper = levelRef.get();
               if (levelWrapper != null) {
                  levelWrapper.tryUnloadFromWorld();
               }

               levelRef = null;
            }

            if (levelRef == null && overrideLevel instanceof ClientLevelWrapper_neoforge) {
               LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL.put(level, new WeakReference<>((ClientLevelWrapper_neoforge)overrideLevel));
            }

            return overrideLevel;
         }
      }

      WeakReference<ClientLevelWrapper_neoforge> levelRefx = LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL.get(level);
      if (levelRefx != null) {
         ClientLevelWrapper_neoforge levelWrapper = levelRefx.get();
         if (levelWrapper != null) {
            return levelWrapper;
         }
      }

      return LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL.compute(level, (newLevel, newLevelRef) -> {
         if (newLevelRef != null) {
            ClientLevelWrapper_neoforge oldLevelWrapper = newLevelRef.get();
            if (oldLevelWrapper != null) {
               return newLevelRef;
            }
         }

         return new WeakReference<>(new ClientLevelWrapper_neoforge(newLevel));
      }).get();
   }

   @Nullable
   @Override
   public IServerLevelWrapper tryGetServerSideWrapper() {
      try {
         if (MINECRAFT.getSingleplayerServer() == null) {
            return null;
         } else {
            Iterable<ServerLevel> serverLevels = MINECRAFT.getSingleplayerServer().getAllLevels();
            ServerLevelWrapper_neoforge foundLevelWrapper = null;

            for (ServerLevel serverLevel : serverLevels) {
               if (serverLevel.dimension() == this.level.dimension()) {
                  foundLevelWrapper = ServerLevelWrapper_neoforge.getWrapper(serverLevel);
                  break;
               }
            }

            return foundLevelWrapper;
         }
      } catch (Exception var5) {
         LOGGER.error("Failed to get server side wrapper for client level: " + this.level);
         return null;
      }
   }

   @Override
   public int getBlockColor(
      DhBlockPos blockWorldPos, IBiomeWrapper biome, FullDataSourceV2 fullDataSource, IBlockStateWrapper blockWrapper, boolean allowApiOverride
   ) {
      ClientBlockStateColorCache_neoforge blockColorCache = this.blockColorCacheByBlockState.get(((BlockStateWrapper_neoforge)blockWrapper).blockState);
      if (blockColorCache == null) {
         blockColorCache = this.blockColorCacheByBlockState
            .computeIfAbsent(((BlockStateWrapper_neoforge)blockWrapper).blockState, this.createCachedBlockColorCacheFunc);
      }

      return blockColorCache.getColor((BiomeWrapper_neoforge)biome, fullDataSource, blockWorldPos, allowApiOverride);
   }

   @Override
   public int getDirtBlockColor() {
      if (this.dirtBlockWrapper == null) {
         try {
            this.dirtBlockWrapper = (BlockStateWrapper_neoforge)BlockStateWrapper_neoforge.deserialize("minecraft:dirt", this);
         } catch (IOException var2) {
            LOGGER.warn("Unable to get dirt color with resource location [minecraft:dirt] with level [" + this + "].", var2);
            return -1;
         }
      }

      return this.getBlockColor(DhBlockPos.ZERO, BiomeWrapper_neoforge.EMPTY_WRAPPER, null, this.dirtBlockWrapper);
   }

   @Override
   public void clearBlockColorCache() {
      this.blockColorCacheByBlockState.clear();
   }

   @Override
   public IDimensionTypeWrapper getDimensionType() {
      if (this.dimensionTypeWrapper != null) {
         return this.dimensionTypeWrapper;
      } else {
         this.dimensionTypeWrapper = DimensionTypeWrapper_neoforge.getDimensionTypeWrapper(this.level.dimensionType());
         return this.dimensionTypeWrapper;
      }
   }

   @Override
   public String getDimensionName() {
      if (this.dimensionName != null) {
         return this.dimensionName;
      } else {
         this.dimensionName = this.level.dimension().location().toString();
         return this.dimensionName;
      }
   }

   @Override
   public long getHashedSeed() {
      return this.level.getBiomeManager().biomeZoomSeed;
   }

   @Override
   public String getDhIdentifier() {
      return this.getHashedSeedEncoded() + "@" + this.getDimensionName();
   }

   @Override
   public EDhApiLevelType getLevelType() {
      return EDhApiLevelType.CLIENT_LEVEL;
   }

   public ClientLevel getLevel() {
      return this.level;
   }

   @Override
   public boolean hasCeiling() {
      if (this.dimHasCeiling != null) {
         return this.dimHasCeiling;
      } else {
         this.dimHasCeiling = this.level.dimensionType().hasCeiling();
         return this.dimHasCeiling;
      }
   }

   @Override
   public boolean hasSkyLight() {
      if (this.dimHasSkyLight != null) {
         return this.dimHasSkyLight;
      } else {
         this.dimHasSkyLight = this.level.dimensionType().hasSkyLight();
         return this.dimHasSkyLight;
      }
   }

   @Override
   public int getMaxHeight() {
      if (this.dimMaxHeight != null) {
         return this.dimMaxHeight;
      } else {
         this.dimMaxHeight = this.level.getHeight();
         return this.dimMaxHeight;
      }
   }

   @Override
   public int getMinHeight() {
      if (this.dimMinHeight != null) {
         return this.dimMinHeight;
      } else {
         this.dimMinHeight = this.level.getMinBuildHeight();
         return this.dimMinHeight;
      }
   }

   public ClientLevel getWrappedMcObject() {
      return this.level;
   }

   private void tryUnloadFromWorld() {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world == null || !world.unloadLevel(this)) {
         this.onUnload();
      }
   }

   @Override
   public void onUnload() {
      LEVEL_WRAPPER_REF_BY_CLIENT_LEVEL.remove(this.level);
      this.dhLevel = null;
   }

   @Override
   public File getDhSaveFolder() {
      return this.dhLevel == null ? null : this.dhLevel.getSaveStructure().getSaveFolder(this);
   }

   @Override
   public DhApiResult<Color> getBlockColorPreApi(
      IDhApiBlockStateWrapper blockStateWrapper,
      IDhApiBiomeWrapper biomeWrapper,
      int blockWorldPosX,
      int blockWorldPosY,
      int blockWorldPosZ,
      IDhApiFullDataSource dataSource
   ) {
      if (blockStateWrapper instanceof IBlockStateWrapper coreBlockStateWrapper) {
         if (biomeWrapper instanceof IBiomeWrapper coreBiomeWrapper) {
            if (dataSource instanceof FullDataSourceV2 coreDataSource) {
               DhBlockPosMutable blockWorldPos = MUTABLE_BLOCK_POS_THREAD_LOCAL.get();
               blockWorldPos.setX(blockWorldPosX);
               blockWorldPos.setY(blockWorldPosY);
               blockWorldPos.setZ(blockWorldPosZ);
               int color = this.getBlockColor(blockWorldPos, coreBiomeWrapper, coreDataSource, coreBlockStateWrapper, false);
               return DhApiResult.createSuccess(ColorUtil.toColorObjARGB(color));
            } else {
               return DhApiResult.createFail("Unable to cast [" + dataSource.getClass() + "] to [" + FullDataSourceV2.class + "]");
            }
         } else {
            return DhApiResult.createFail("Unable to cast [" + biomeWrapper.getClass() + "] to [" + IBiomeWrapper.class + "]");
         }
      } else {
         return DhApiResult.createFail("Unable to cast [" + blockStateWrapper.getClass() + "] to [" + IBlockStateWrapper.class + "]");
      }
   }

   @Override
   public IDhApiCustomRenderRegister getRenderRegister() {
      return this.dhLevel == null ? null : this.dhLevel.getGenericRenderer();
   }

   @Override
   public Color getCloudColor(float tickDelta) {
      Vec3 colorVec3 = null;

      try {
         colorVec3 = this.level.getCloudColor(tickDelta);
         return new Color((float)colorVec3.x, (float)colorVec3.y, (float)colorVec3.z);
      } catch (Exception var5) {
         if (!this.cloudColorFailLogged) {
            this.cloudColorFailLogged = true;
            String colorString = "NULL";
            if (colorVec3 != null) {
               colorString = "r[" + (float)colorVec3.x + "] g[" + (float)colorVec3.y + "] b[" + (float)colorVec3.z + "]";
            }

            LOGGER.warn("Failed to get cloud color for [" + this.getDhIdentifier() + "]. vec3 [" + colorString + "], error: [" + var5.getMessage() + "].", var5);
         }

         return Color.WHITE;
      }
   }

   @Override
   public float getShade(EDhDirection lodDirection) {
      EDhApiLodShading lodShading = Config.Client.Advanced.Graphics.Quality.lodShading.get();
      switch (lodShading) {
         case AUTO:
         default:
            Direction mcDir = McObjectConverter_neoforge.convert(lodDirection);
            return this.level.getShade(mcDir, true);
         case ENABLED:
            switch (lodDirection) {
               case DOWN:
                  return 0.5F;
               case UP:
               default:
                  return 1.0F;
               case NORTH:
               case SOUTH:
                  return 0.8F;
               case WEST:
               case EAST:
                  return 0.6F;
            }
         case DISABLED:
            return 1.0F;
      }
   }

   @Override
   public String toString() {
      return this.level == null ? "Wrapped{null}" : "Wrapped{" + this.level.toString() + "@" + this.getDhIdentifier() + "}";
   }

   static {
      CLIENT_CLEANUP_TIMER.scheduleAtFixedRate(CLIENT_CLEANUP_TASK, 0L, 50L);
   }
}
