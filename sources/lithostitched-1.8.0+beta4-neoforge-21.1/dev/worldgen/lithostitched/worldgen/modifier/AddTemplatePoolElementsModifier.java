package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record AddTemplatePoolElementsModifier(
   Optional<LoadPredicate> predicate, int priority, HolderSet<StructureTemplatePool> templatePools, List<Pair<StructurePoolElement, Integer>> elements
) implements WorldgenModifier {
   public static final MapCodec<AddTemplatePoolElementsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(AddTemplatePoolElementsModifier::priority),
            LithostitchedCodecs.registrySet(Registries.TEMPLATE_POOL, "template_pools").forGetter(AddTemplatePoolElementsModifier::templatePools),
            Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight"))
               .codec()
               .listOf()
               .fieldOf("elements")
               .forGetter(AddTemplatePoolElementsModifier::elements)
         )
         .apply(instance, AddTemplatePoolElementsModifier::new)
   );

   @Override
   public void apply(RegistryAccess registries) {
      this.templatePools.stream().<StructureTemplatePool>map(Holder::value).forEach(this::applyModifier);
   }

   private void applyModifier(StructureTemplatePool templatePool) {
      StructureTemplatePoolAccessor poolAccessor = (StructureTemplatePoolAccessor)templatePool;
      List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(poolAccessor.getRawTemplates());
      rawTemplates.addAll(this.elements());
      poolAccessor.setRawTemplates(rawTemplates);
      ObjectArrayList<StructurePoolElement> vanillaTemplates = new ObjectArrayList(poolAccessor.getVanillaTemplates());

      for (Pair<StructurePoolElement, Integer> pair : this.elements()) {
         for (int i = 0; i < pair.getSecond(); i++) {
            vanillaTemplates.add((StructurePoolElement)pair.getFirst());
         }
      }

      poolAccessor.setVanillaTemplates(vanillaTemplates);
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
