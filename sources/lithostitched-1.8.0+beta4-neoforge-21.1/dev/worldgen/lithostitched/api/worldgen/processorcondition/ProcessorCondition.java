package dev.worldgen.lithostitched.api.worldgen.processorcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.processor.condition.AllOf;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public interface ProcessorCondition {
   Codec<ProcessorCondition> BASE_CODEC = Codec.lazyInitialized(() -> {
      Optional<? extends Registry<?>> registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistries.PROCESSOR_CONDITION_TYPE.location());
      if (registry.isEmpty()) {
         throw new NullPointerException("Processor condition registry does not exist yet!");
      } else {
         return registry.get().byNameCodec();
      }
   }).dispatch(ProcessorCondition::codec, Function.identity());
   Codec<ProcessorCondition> CODEC = Codec.withAlternative(BASE_CODEC, BASE_CODEC.listOf(), AllOf::new);

   boolean test(WorldGenLevel var1, ProcessorCondition.Data var2, StructurePlaceSettings var3, RandomSource var4);

   MapCodec<? extends ProcessorCondition> codec();

   public record Data(BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute) {
   }
}
