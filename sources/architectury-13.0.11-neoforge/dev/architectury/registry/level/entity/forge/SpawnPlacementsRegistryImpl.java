package dev.architectury.registry.level.entity.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

public class SpawnPlacementsRegistryImpl {
   private static List<SpawnPlacementsRegistryImpl.Entry<?>> entries = new ArrayList<>();

   public static <T extends Mob> void register(
      Supplier<? extends EntityType<T>> type, SpawnPlacementType spawnPlacement, Types heightmapType, SpawnPredicate<T> spawnPredicate
   ) {
      if (entries != null) {
         entries.add(new SpawnPlacementsRegistryImpl.Entry<>(type, spawnPlacement, heightmapType, spawnPredicate));
      } else {
         throw new IllegalStateException("SpawnPlacementsRegistry.register must not be called after the registry has been collected!");
      }
   }

   static {
      EventBusesHooks.whenAvailable("architectury", bus -> bus.addListener(event -> {
         for (SpawnPlacementsRegistryImpl.Entry<?> entry : entries) {
            event.register(entry.type().get(), entry.spawnPlacement(), entry.heightmapType(), entry.spawnPredicate(), Operation.OR);
         }

         entries = null;
      }));
   }

   private record Entry<T extends Mob>(
      Supplier<? extends EntityType<T>> type, SpawnPlacementType spawnPlacement, Types heightmapType, SpawnPredicate<T> spawnPredicate
   ) {
   }
}
