package net.mehvahdjukaar.moonlight.core.criteria_triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GrindItemTrigger extends SimpleCriterionTrigger<GrindItemTrigger.Instance> {
   public Codec<GrindItemTrigger.Instance> codec() {
      return GrindItemTrigger.Instance.CODEC;
   }

   public void trigger(ServerPlayer playerEntity, ItemStack stack) {
      this.trigger(playerEntity, instance -> instance.matches(stack));
   }

   public record Instance(Optional<ContextAwarePredicate> player, ItemPredicate item) implements SimpleInstance {
      public static final Codec<GrindItemTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(GrindItemTrigger.Instance::player),
               ItemPredicate.CODEC.fieldOf("item").forGetter(GrindItemTrigger.Instance::item)
            )
            .apply(instance, GrindItemTrigger.Instance::new)
      );

      public boolean matches(ItemStack stack) {
         return this.item.test(stack);
      }
   }
}
