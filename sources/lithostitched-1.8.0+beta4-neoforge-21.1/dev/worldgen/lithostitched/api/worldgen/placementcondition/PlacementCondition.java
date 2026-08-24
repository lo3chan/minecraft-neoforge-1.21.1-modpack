package dev.worldgen.lithostitched.api.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.placementcondition.AllOfPlacementCondition;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;

public interface PlacementCondition {
   Codec<PlacementCondition> BASE_CODEC = Codec.lazyInitialized(() -> {
      Optional<? extends Registry<?>> registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistries.PLACEMENT_CONDITION_TYPE.location());
      if (registry.isEmpty()) {
         throw new NullPointerException("Placement condition registry does not exist yet!");
      } else {
         return registry.get().byNameCodec();
      }
   }).dispatch(PlacementCondition::codec, Function.identity());
   Codec<PlacementCondition> CODEC = Codec.withAlternative(BASE_CODEC, BASE_CODEC.listOf(), AllOfPlacementCondition::new);

   boolean test(PlacementCondition.Context var1, BlockPos var2);

   default boolean test(GenerationContext context, BlockPos pos) {
      return this.test(PlacementCondition.Context.create(context), pos);
   }

   default boolean test(PlacementContext context, BlockPos pos) {
      return this.test(PlacementCondition.Context.create(context), pos);
   }

   MapCodec<? extends PlacementCondition> codec();

   public record Context(
      RegistryAccess registries, ChunkGenerator generator, LevelHeightAccessor heightAccessor, RandomState randomState, BiomeSource biomeSource, long seed
   ) {
      private static PlacementCondition.Context create(GenerationContext context) {
         return new PlacementCondition.Context(
            context.registryAccess(), context.chunkGenerator(), context.heightAccessor(), context.randomState(), context.biomeSource(), context.seed()
         );
      }

      private static PlacementCondition.Context create(PlacementContext context) {
         WorldGenLevel level = context.getLevel();
         return new PlacementCondition.Context(
            level.registryAccess(),
            context.generator(),
            level,
            level.getLevel().getChunkSource().randomState(),
            context.generator().getBiomeSource(),
            level.getSeed()
         );
      }
   }
}
