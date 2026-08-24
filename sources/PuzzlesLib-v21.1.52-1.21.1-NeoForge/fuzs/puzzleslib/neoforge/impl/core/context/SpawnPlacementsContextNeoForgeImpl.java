package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import java.util.Objects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

public record SpawnPlacementsContextNeoForgeImpl(RegisterSpawnPlacementsEvent evt) implements SpawnPlacementsContext {
   @Override
   public <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacementType location, Types heightmap, SpawnPredicate<T> spawnPredicate) {
      Objects.requireNonNull(entityType, "entity type is null");
      Objects.requireNonNull(location, "location is null");
      Objects.requireNonNull(heightmap, "heightmap is null");
      Objects.requireNonNull(spawnPredicate, "spawnPredicate is null");
      this.evt.register(entityType, location, heightmap, spawnPredicate, Operation.REPLACE);
   }
}
