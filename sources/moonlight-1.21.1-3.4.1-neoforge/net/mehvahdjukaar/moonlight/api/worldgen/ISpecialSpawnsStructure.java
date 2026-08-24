package net.mehvahdjukaar.moonlight.api.worldgen;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

public interface ISpecialSpawnsStructure {
   @Nullable
   WeightedRandomList<SpawnerData> ml$getSpecialSpawns(StructureManager var1, Structure var2, BlockPos var3, LongSet var4, MobCategory var5);
}
