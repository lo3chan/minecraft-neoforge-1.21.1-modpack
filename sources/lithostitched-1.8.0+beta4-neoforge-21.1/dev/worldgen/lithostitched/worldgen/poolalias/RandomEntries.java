package dev.worldgen.lithostitched.worldgen.poolalias;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;

public record RandomEntries(List<ResourceKey<StructureTemplatePool>> aliases, List<HolderSet<StructureTemplatePool>> pools) implements PoolAliasBinding {
   public static final MapCodec<RandomEntries> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ResourceKey.codec(Registries.TEMPLATE_POOL).listOf().fieldOf("aliases").forGetter(RandomEntries::aliases),
               HolderSetCodec.create(Registries.TEMPLATE_POOL, StructureTemplatePool.CODEC, false).listOf().fieldOf("pools").forGetter(RandomEntries::pools)
            )
            .apply(instance, RandomEntries::new)
      )
      .validate(RandomEntries::validate);

   private static DataResult<RandomEntries> validate(RandomEntries entry) {
      return DataResult.success(entry);
   }

   public void forEachResolved(RandomSource random, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> consumer) {
      int index = random.nextInt(((HolderSet)this.pools.getFirst()).size());

      for (int i = 0; i < this.pools.size(); i++) {
         consumer.accept(this.aliases.get(i), (ResourceKey<StructureTemplatePool>)this.pools.get(i).get(index).unwrapKey().get());
      }
   }

   public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
      return Stream.of();
   }

   public MapCodec<? extends PoolAliasBinding> codec() {
      return CODEC;
   }
}
