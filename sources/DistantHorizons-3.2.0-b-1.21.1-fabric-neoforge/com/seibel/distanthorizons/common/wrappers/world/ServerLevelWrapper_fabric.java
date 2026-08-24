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
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import org.jetbrains.annotations.Nullable;

public class ServerLevelWrapper_fabric implements IServerLevelWrapper {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Map<class_3218, WeakReference<ServerLevelWrapper_fabric>> LEVEL_WRAPPER_REF_BY_SERVER_LEVEL = Collections.synchronizedMap(
      new WeakHashMap<>()
   );
   private final class_3218 level;
   private IDhLevel dhLevel;
   private final String keyedLevelDimensionName;

   public static ServerLevelWrapper_fabric getWrapper(class_3218 level) {
      return LEVEL_WRAPPER_REF_BY_SERVER_LEVEL.compute(level, (newLevel, levelRef) -> {
         if (levelRef != null) {
            ServerLevelWrapper_fabric oldLevelWrapper = levelRef.get();
            if (oldLevelWrapper != null) {
               return levelRef;
            }
         }

         return new WeakReference<>(new ServerLevelWrapper_fabric(newLevel));
      }).get();
   }

   public ServerLevelWrapper_fabric(class_3218 level) {
      this.level = level;
      this.keyedLevelDimensionName = this.createKeyedLevelDimensionName();
   }

   @Override
   public File getMcSaveFolder() {
      return this.level.method_14178().method_17981().field_17664;
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
         return this.level.method_8503().method_3847(class_1937.field_25179).method_14178().method_17981().field_17664.getParentFile().getName();
      } catch (Exception var2) {
         LOGGER.warn("Unable to get world folder name. LODs may not load or save correctly. Error: [" + var2.getMessage() + "].", var2);
         return "unknown_world";
      }
   }

   public DimensionTypeWrapper_fabric getDimensionType() {
      return DimensionTypeWrapper_fabric.getDimensionTypeWrapper(this.level.method_8597());
   }

   @Override
   public String getDimensionName() {
      return this.level.method_27983().method_29177().toString();
   }

   @Override
   public long getHashedSeed() {
      return this.level.method_22385().field_20641;
   }

   @Override
   public String getDhIdentifier() {
      return this.getDimensionName();
   }

   @Override
   public EDhApiLevelType getLevelType() {
      return EDhApiLevelType.SERVER_LEVEL;
   }

   public class_3218 getLevel() {
      return this.level;
   }

   @Override
   public boolean hasCeiling() {
      return this.level.method_8597().comp_643();
   }

   @Override
   public boolean hasSkyLight() {
      return this.level.method_8597().comp_642();
   }

   @Override
   public int getMaxHeight() {
      return this.level.method_31605();
   }

   @Override
   public int getMinHeight() {
      return this.level.method_31607();
   }

   public class_3218 getWrappedMcObject() {
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
            + ServerLevelWrapper_fabric.class.getSimpleName()
            + "]'s cannot get block colors, please use a ["
            + ClientLevelWrapper_fabric.class.getSimpleName()
            + "] instead."
      );
   }

   @Override
   public String toString() {
      return "Wrapped{" + this.level.toString() + "@" + this.getDhIdentifier() + "}";
   }
}
