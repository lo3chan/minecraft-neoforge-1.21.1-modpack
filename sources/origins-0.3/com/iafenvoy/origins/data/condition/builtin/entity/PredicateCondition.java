package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.EntityCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public record PredicateCondition(ResourceLocation predicate) implements EntityCondition {
   public static final MapCodec<PredicateCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ResourceLocation.CODEC.fieldOf("predicate").forGetter(PredicateCondition::predicate)).apply(instance, PredicateCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity entity) {
      if (entity.level() instanceof ServerLevel level) {
         Reference<LootItemCondition> lootCondition = (Reference<LootItemCondition>)level.getServer()
            .reloadableRegistries()
            .lookup()
            .get(Registries.PREDICATE, ResourceKey.create(Registries.PREDICATE, this.predicate))
            .orElse(null);
         if (lootCondition != null) {
            return ((LootItemCondition)lootCondition.value())
               .test(
                  new Builder(
                        new net.minecraft.world.level.storage.loot.LootParams.Builder(level)
                           .withParameter(LootContextParams.ORIGIN, entity.position())
                           .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                           .create(LootContextParamSets.COMMAND)
                     )
                     .create(Optional.empty())
               );
         }
      }

      return false;
   }
}
