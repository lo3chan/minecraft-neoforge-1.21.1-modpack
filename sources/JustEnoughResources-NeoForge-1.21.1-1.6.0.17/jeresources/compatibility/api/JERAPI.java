package jeresources.compatibility.api;

import jeresources.api.IDungeonRegistry;
import jeresources.api.IJERAPI;
import jeresources.api.IMobRegistry;
import jeresources.api.IPlantRegistry;
import jeresources.api.IWorldGenRegistry;
import jeresources.compatibility.CompatBase;
import jeresources.platform.Services;
import net.minecraft.world.level.Level;

public class JERAPI implements IJERAPI {
   private IWorldGenRegistry worldGenRegistry = new WorldGenRegistryImpl();
   private IMobRegistry mobRegistry = new MobRegistryImpl();
   private IPlantRegistry plantRegistry = new PlantRegistryImpl();
   private IDungeonRegistry dungeonRegistry = new DungeonRegistryImpl();
   private static IJERAPI instance;

   public static IJERAPI getInstance() {
      if (instance == null) {
         instance = new JERAPI();
      }

      return instance;
   }

   private JERAPI() {
   }

   public static void init() {
      Services.PLATFORM.injectApi(getInstance());
   }

   @Override
   public IMobRegistry getMobRegistry() {
      return this.mobRegistry;
   }

   @Override
   public IWorldGenRegistry getWorldGenRegistry() {
      return this.worldGenRegistry;
   }

   @Override
   public IPlantRegistry getPlantRegistry() {
      return this.plantRegistry;
   }

   @Override
   public IDungeonRegistry getDungeonRegistry() {
      return this.dungeonRegistry;
   }

   @Override
   public Level getLevel() {
      return CompatBase.getLevel();
   }

   public static void commit(boolean initWorldGen) {
      DungeonRegistryImpl.commit();
      MobRegistryImpl.commit();
      PlantRegistryImpl.commit();
      if (initWorldGen) {
         WorldGenRegistryImpl.commit();
      }
   }
}
