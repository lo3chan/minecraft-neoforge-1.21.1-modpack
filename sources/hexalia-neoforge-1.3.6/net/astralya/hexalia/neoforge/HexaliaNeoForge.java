package net.astralya.hexalia.neoforge;

import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.neoforge.event.NeoForgeArmorEvents;
import net.astralya.hexalia.neoforge.event.NeoForgeSagePendantEvents;
import net.astralya.hexalia.util.ModVanillaBehaviors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

@Mod("hexalia")
public final class HexaliaNeoForge {
   public HexaliaNeoForge(IEventBus modEventBus, ModContainer modContainer) {
      HexaliaNeoForgeConfig.init(modContainer, modEventBus);
      Hexalia.init();
      NeoForgeArmorEvents.register();
      NeoForgeSagePendantEvents.register();
      modEventBus.addListener(HexaliaNeoForge::commonSetup);
      modEventBus.addListener(HexaliaNeoForge::registerAttributes);
      modEventBus.addListener(HexaliaNeoForge::registerSpawnPlacements);
      if (FMLEnvironment.dist == Dist.CLIENT) {
         HexaliaNeoForgeClient.init(modEventBus);
      }
   }

   private static void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(ModVanillaBehaviors::register);
   }

   private static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)ModEntities.SILK_MOTH.get(), SilkMothEntity.setAttributes());
      event.put((EntityType)ModEntities.CACOFEY.get(), CacofeyEntity.setAttributes());
   }

   private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)ModEntities.SILK_MOTH.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Animal::checkAnimalSpawnRules,
         Operation.REPLACE
      );
      event.register(
         (EntityType)ModEntities.CACOFEY.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Animal::checkAnimalSpawnRules,
         Operation.REPLACE
      );
   }
}
