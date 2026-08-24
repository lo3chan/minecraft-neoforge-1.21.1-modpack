package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.api.interfaces.world.IDhApiDimensionTypeWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiWorldProxy;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.util.ArrayList;

public class DhApiWorldProxy implements IDhApiWorldProxy {
   public static DhApiWorldProxy INSTANCE = new DhApiWorldProxy();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftSharedWrapper MC_SHARED = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
   private static final String NO_WORLD_EXCEPTION_STRING = "No world loaded";
   private boolean isReadOnly = false;

   private DhApiWorldProxy() {
   }

   @Override
   public boolean worldLoaded() {
      return SharedApi.getAbstractDhWorld() != null;
   }

   @Override
   public void setReadOnly(boolean readOnly) throws IllegalStateException {
      this.setReadOnly(readOnly, true);
   }

   public void setReadOnly(boolean readOnly, boolean throwIfWorldUnloaded) throws IllegalStateException {
      if (throwIfWorldUnloaded && SharedApi.getAbstractDhWorld() == null) {
         throw new IllegalStateException("No world loaded");
      } else {
         boolean valueChanged = this.isReadOnly != readOnly;
         this.isReadOnly = readOnly;
         if (valueChanged) {
            if (this.isReadOnly) {
               LOGGER.info("DH world set to read-only. LODs will not update while this API flag is active.");
            } else {
               LOGGER.info("DH world is no longer in read-only mode. LODs will update like normal.");
            }
         }
      }
   }

   @Override
   public boolean getReadOnly() throws IllegalStateException {
      if (SharedApi.getAbstractDhWorld() == null) {
         throw new IllegalStateException("No world loaded");
      } else {
         return this.isReadOnly;
      }
   }

   public boolean tryGetReadOnly() {
      return SharedApi.getAbstractDhWorld() == null ? false : this.isReadOnly;
   }

   @Override
   public IDhApiLevelWrapper getSinglePlayerLevel() throws IllegalStateException {
      if (SharedApi.getAbstractDhWorld() == null) {
         throw new IllegalStateException("No world loaded");
      } else {
         return MC_SHARED.isDedicatedServer() ? null : MC_CLIENT.getWrappedClientLevel();
      }
   }

   @Override
   public Iterable<IDhApiLevelWrapper> getAllLoadedLevelWrappers() throws IllegalStateException {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world == null) {
         throw new IllegalStateException("No world loaded");
      } else {
         ArrayList<IDhApiLevelWrapper> returnList = new ArrayList<>();

         for (IDhLevel dhLevel : world.getAllLoadedLevels()) {
            returnList.add(dhLevel.getLevelWrapper());
         }

         return returnList;
      }
   }

   @Override
   public Iterable<IDhApiLevelWrapper> getAllLoadedLevelsForDimensionType(IDhApiDimensionTypeWrapper dimensionTypeWrapper) throws IllegalStateException {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world == null) {
         throw new IllegalStateException("No world loaded");
      } else {
         ArrayList<IDhApiLevelWrapper> returnList = new ArrayList<>();

         for (IDhLevel dhLevel : world.getAllLoadedLevels()) {
            ILevelWrapper levelWrapper = dhLevel.getLevelWrapper();
            if (levelWrapper.getDimensionType().equals(dimensionTypeWrapper)) {
               returnList.add(levelWrapper);
            }
         }

         return returnList;
      }
   }

   @Override
   public Iterable<IDhApiLevelWrapper> getAllLoadedLevelsWithDimensionNameLike(String dimensionName) throws IllegalStateException {
      AbstractDhWorld world = SharedApi.getAbstractDhWorld();
      if (world == null) {
         throw new IllegalStateException("No world loaded");
      } else {
         String soughtDimName = dimensionName.toLowerCase();
         ArrayList<IDhApiLevelWrapper> returnList = new ArrayList<>();

         for (IDhLevel dhLevel : world.getAllLoadedLevels()) {
            ILevelWrapper levelWrapper = dhLevel.getLevelWrapper();
            String levelDimName = levelWrapper.getDhIdentifier().toLowerCase();
            if (levelDimName.contains(soughtDimName)) {
               returnList.add(levelWrapper);
            }
         }

         return returnList;
      }
   }
}
