package net.cibernet.alchemancy.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class ActivateForgeTrigger extends SimpleCriterionTrigger<ActivateForgeTrigger.TriggerInstance> {
   public Codec<ActivateForgeTrigger.TriggerInstance> codec() {
      return ActivateForgeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer player, BlockPos pos) {
      super.trigger(player, triggerInsance -> triggerInsance.matches(pos));
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<BlockPos> pos) implements SimpleInstance {
      public static final Codec<ActivateForgeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ActivateForgeTrigger.TriggerInstance::player),
               BlockPos.CODEC.optionalFieldOf("forge_position").forGetter(ActivateForgeTrigger.TriggerInstance::pos)
            )
            .apply(instance, ActivateForgeTrigger.TriggerInstance::new)
      );

      public boolean matches(BlockPos pos) {
         return this.pos.isEmpty() || this.pos.get().equals(pos);
      }
   }
}
