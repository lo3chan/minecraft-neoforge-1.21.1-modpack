package net.mehvahdjukaar.moonlight.api.worldgen;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.concurrent.atomic.AtomicReference;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.worldgen.SpawnBoxStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISpawnBoxStructure extends ISpecialSpawnsStructure {
   @Nullable
   @Override
   default WeightedRandomList<SpawnerData> ml$getSpecialSpawns(
      StructureManager structureManager, Structure structure, BlockPos pos, LongSet chunkPosReferences, MobCategory category
   ) {
      SpawnBoxSettings settings = this.ml$getSpawnBoxSettings();
      if (settings == SpawnBoxSettings.EMPTY) {
         return null;
      } else if (!settings.hasCategory(category)) {
         return null;
      } else {
         AtomicReference<String> boxName = new AtomicReference<>();
         structureManager.fillStartsForStructure(structure, chunkPosReferences, structureStart -> {
            if (boxName.get() == null) {
               String foundName = SpawnBoxStructurePiece.getNamedBoxesAt(structureStart, pos);
               if (foundName != null) {
                  boxName.set(foundName);
               }
            }
         });
         String boxID = boxName.get();
         if (boxID != null) {
            WeightedRandomList<SpawnerData> found = settings.get(boxID, category);
            if (found == null) {
               Moonlight.LOGGER
                  .warn("Spawn Box in structure '{}' is missing spawns settings for name '{}' and category '{}'", structure.type(), boxID, category.getName());
            }

            return found;
         } else {
            return null;
         }
      }
   }

   @NotNull
   SpawnBoxSettings ml$getSpawnBoxSettings();

   default void ml$setSpawnBoxSettings(@NotNull SpawnBoxSettings settings) {
   }
}
