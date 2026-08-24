package dev.architectury.registry.level.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import dev.architectury.registry.level.entity.forge.SpawnPlacementsRegistryImpl;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public final class SpawnPlacementsRegistry {
   @ExpectPlatform
   @Transformed
   public static <T extends Mob> void register(
      Supplier<? extends EntityType<T>> type, SpawnPlacementType spawnPlacement, Types heightmapType, SpawnPredicate<T> spawnPredicate
   ) {
      SpawnPlacementsRegistryImpl.register(type, spawnPlacement, heightmapType, spawnPredicate);
   }
}
