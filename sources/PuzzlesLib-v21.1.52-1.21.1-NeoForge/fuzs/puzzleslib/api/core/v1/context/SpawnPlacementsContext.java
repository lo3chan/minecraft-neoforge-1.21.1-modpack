package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.level.levelgen.Heightmap.Types;

@FunctionalInterface
public interface SpawnPlacementsContext {
   <T extends Mob> void registerSpawnPlacement(EntityType<T> var1, SpawnPlacementType var2, Types var3, SpawnPredicate<T> var4);
}
