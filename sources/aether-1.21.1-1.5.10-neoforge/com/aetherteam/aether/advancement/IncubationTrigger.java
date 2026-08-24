package com.aetherteam.aether.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class IncubationTrigger extends SimpleCriterionTrigger<IncubationTrigger.Instance> {
   public Codec<IncubationTrigger.Instance> codec() {
      return IncubationTrigger.Instance.CODEC;
   }

   public void trigger(ServerPlayer player, ItemStack stack) {
      this.trigger(player, instance -> instance.test(stack));
   }

   public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {
      public static final Codec<IncubationTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(IncubationTrigger.Instance::player),
               ItemPredicate.CODEC.optionalFieldOf("item").forGetter(IncubationTrigger.Instance::item)
            )
            .apply(instance, IncubationTrigger.Instance::new)
      );

      public static Criterion<IncubationTrigger.Instance> forItem(ItemPredicate item) {
         return ((IncubationTrigger)AetherAdvancementTriggers.INCUBATION_TRIGGER.get())
            .createCriterion(new IncubationTrigger.Instance(Optional.empty(), Optional.of(item)));
      }

      public boolean test(ItemStack stack) {
         return this.item.isEmpty() || this.item.get().test(stack);
      }
   }
}
