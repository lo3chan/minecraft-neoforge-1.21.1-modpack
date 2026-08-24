package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.SinglePoolElementAccessor;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public record SetPoolElementProcessorsModifier(
   Optional<LoadPredicate> predicate,
   int priority,
   HolderSet<StructureTemplatePool> templatePools,
   Optional<List<ResourceLocation>> locations,
   Holder<StructureProcessorList> processorList,
   boolean append
) implements WorldgenModifier {
   public static final MapCodec<SetPoolElementProcessorsModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(SetPoolElementProcessorsModifier::priority),
            LithostitchedCodecs.registrySet(Registries.TEMPLATE_POOL, "template_pools").forGetter(SetPoolElementProcessorsModifier::templatePools),
            LithostitchedCodecs.compactList(ResourceLocation.CODEC).optionalFieldOf("locations").forGetter(SetPoolElementProcessorsModifier::locations),
            StructureProcessorType.LIST_CODEC.fieldOf("processor_list").forGetter(SetPoolElementProcessorsModifier::processorList),
            Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetPoolElementProcessorsModifier::append)
         )
         .apply(instance, SetPoolElementProcessorsModifier::new)
   );

   @Override
   public void apply(RegistryAccess registries) {
      for (Holder<StructureTemplatePool> templatePool : this.templatePools) {
         StructureTemplatePoolAccessor pool = (StructureTemplatePoolAccessor)templatePool.value();

         for (StructurePoolElement element : pool.getRawTemplates().stream().map(Pair::getFirst).toList()) {
            this.applyModifier(element);
         }
      }
   }

   private void applyModifier(StructurePoolElement element) {
      if (element instanceof SinglePoolElement) {
         SinglePoolElementAccessor accessor = (SinglePoolElementAccessor)element;
         Optional<ResourceLocation> template = accessor.getTemplate().left();
         if (this.locations.isEmpty() || template.isPresent() && this.locations.get().contains(template.get())) {
            this.addProcessor(accessor);
         }
      } else if (element instanceof DelegatingPoolElement delegating) {
         this.applyModifier(delegating.delegate());
      }
   }

   private void addProcessor(SinglePoolElementAccessor element) {
      List<StructureProcessor> processors = new ArrayList<>();
      if (this.append) {
         processors.addAll(((StructureProcessorList)element.getProcessors().value()).list());
      }

      processors.addAll(((StructureProcessorList)this.processorList.value()).list());
      element.setProcessors(Holder.direct(new StructureProcessorList(processors)));
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
