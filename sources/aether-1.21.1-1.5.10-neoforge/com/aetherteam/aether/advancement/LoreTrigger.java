package com.aetherteam.aether.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class LoreTrigger extends SimpleCriterionTrigger<LoreTrigger.Instance> {
   public Codec<LoreTrigger.Instance> codec() {
      return LoreTrigger.Instance.CODEC;
   }

   public void trigger(ServerPlayer player, ItemStack stack) {
      this.trigger(player, instance -> instance.test(stack));
   }

   public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {
      public static final Codec<LoreTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(LoreTrigger.Instance::player),
               ItemPredicate.CODEC.optionalFieldOf("item").forGetter(LoreTrigger.Instance::item)
            )
            .apply(instance, LoreTrigger.Instance::new)
      );

      public static Criterion<LoreTrigger.Instance> forItem(ItemPredicate item) {
         return ((LoreTrigger)AetherAdvancementTriggers.LORE_ENTRY.get()).createCriterion(new LoreTrigger.Instance(Optional.empty(), Optional.of(item)));
      }

      public static Criterion<LoreTrigger.Instance> forItem(ItemLike item) {
         return forItem(Builder.item().of(new ItemLike[]{item}).build());
      }

      public static Criterion<LoreTrigger.Instance> forAny() {
         return ((LoreTrigger)AetherAdvancementTriggers.LORE_ENTRY.get()).createCriterion(new LoreTrigger.Instance(Optional.empty(), Optional.empty()));
      }

      public boolean test(ItemStack stack) {
         return this.item.isEmpty() || this.item.get().test(stack);
      }
   }
}
