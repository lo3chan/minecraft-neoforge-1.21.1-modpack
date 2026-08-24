package com.seibel.distanthorizons.common.wrappers.world;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiLevelType;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderRegister;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.world.EWorldEnvironment;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.awt.Color;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ServerLevelWrapper_neoforge implements IServerLevelWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Map<ServerLevel, WeakReference<ServerLevelWrapper_neoforge>> LEVEL_WRAPPER_REF_BY_SERVER_LEVEL = Collections.synchronizedMap(
      new WeakHashMap<>()
   );
   private final ServerLevel level;
   private IDhLevel dhLevel;
   private final String keyedLevelDimensionName;

   public static ServerLevelWrapper_neoforge getWrapper(ServerLevel level) {
      return LEVEL_WRAPPER_REF_BY_SERVER_LEVEL.compute(level, (newLevel, levelRef) -> {
         if (levelRef != null) {
            ServerLevelWrapper_neoforge oldLevelWrapper = levelRef.get();
            if (oldLevelWrapper != null) {
               return levelRef;
            }
         }

         return new WeakReference<>(new ServerLevelWrapper_neoforge(newLevel));
      }).get();
   }

   public ServerLevelWrapper_neoforge(ServerLevel level) {
      this.level = level;
      this.keyedLevelDimensionName = this.createKeyedLevelDimensionName();
   }

   @Override
   public File getMcSaveFolder() {
      return this.level.getChunkSource().getDataStorage().dataFolder;
   }

   @Override
   public String getKeyedLevelDimensionName() {
      return this.keyedLevelDimensionName;
   }

   private String createKeyedLevelDimensionName() {
      String dimensionName = this.getDhIdentifier();
      if (Config.Server.sendLevelKeys.get()) {
         String levelKeyPrefix = Config.Server.levelKeyPrefix.get();
         if (SharedApi.getEnvironment() == EWorldEnvironment.CLIENT_SERVER) {
            String cleanWorldFolderName = this.getWorldFolderName().replaceAll("[^a-zA-Z0-9-_ ]", "").replaceAll(" ", "_");
            levelKeyPrefix = levelKeyPrefix + (!levelKeyPrefix.isEmpty() ? "_" : "") + cleanWorldFolderName + "_" + this.getHashedSeedEncoded();
         }

         if (levelKeyPrefix.isEmpty()) {
            levelKeyPrefix = this.getHashedSeedEncoded();
         }

         String mainPart = "@" + dimensionName;
         return levelKeyPrefix.substring(0, Math.min(150 - mainPart.length(), levelKeyPrefix.length())) + mainPart;
      } else {
         return dimensionName;
      }
   }

   private String getWorldFolderName() {
      try {
         return this.level.getServer().getLevel(Level.OVERWORLD).getChunkSource().getDataStorage().dataFolder.getParentFile().getName();
      } catch (Exception var2) {
         LOGGER.warn("Unable to get world folder name. LODs may not load or save correctly. Error: [" + var2.getMessage() + "].", var2);
         return "unknown_world";
      }
   }

   public DimensionTypeWrapper_neoforge getDimensionType() {
      return DimensionTypeWrapper_neoforge.getDimensionTypeWrapper(this.level.dimensionType());
   }

   @Override
   public String getDimensionName() {
      return this.level.dimension().location().toString();
   }

   @Override
   public long getHashedSeed() {
      return this.level.getBiomeManager().biomeZoomSeed;
   }

   @Override
   public String getDhIdentifier() {
      return this.getDimensionName();
   }

   @Override
   public EDhApiLevelType getLevelType() {
      return EDhApiLevelType.SERVER_LEVEL;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   @Override
   public boolean hasCeiling() {
      return this.level.dimensionType().hasCeiling();
   }

   @Override
   public boolean hasSkyLight() {
      return this.level.dimensionType().hasSkyLight();
   }

   @Override
   public int getMaxHeight() {
      return this.level.getHeight();
   }

   @Override
   public int getMinHeight() {
      return this.level.getMinBuildHeight();
   }

   public ServerLevel getWrappedMcObject() {
      return this.level;
   }

   @Override
   public void onUnload() {
      LEVEL_WRAPPER_REF_BY_SERVER_LEVEL.remove(this.level);
   }

   @Override
   public void setDhLevel(IDhLevel dhLevel) {
      this.dhLevel = dhLevel;
   }

   @Nullable
   @Override
   public IDhLevel getDhLevel() {
      return this.dhLevel;
   }

   @Override
   public IDhApiCustomRenderRegister getRenderRegister() {
      return this.dhLevel == null ? null : this.dhLevel.getGenericRenderer();
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
      return DhApiResult.createFail(
         "["
            + ServerLevelWrapper_neoforge.class.getSimpleName()
            + "]'s cannot get block colors, please use a ["
            + ClientLevelWrapper_neoforge.class.getSimpleName()
            + "] instead."
      );
   }

   @Override
   public String toString() {
      return "Wrapped{" + this.level.toString() + "@" + this.getDhIdentifier() + "}";
   }
}
