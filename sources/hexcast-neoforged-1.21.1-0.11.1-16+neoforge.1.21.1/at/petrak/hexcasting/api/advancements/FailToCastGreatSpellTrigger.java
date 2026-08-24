package at.petrak.hexcasting.api.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class FailToCastGreatSpellTrigger extends SimpleCriterionTrigger<FailToCastGreatSpellTrigger.Instance> {
   public static final String ID = "fail_to_cast_great_spell";

   public Codec<FailToCastGreatSpellTrigger.Instance> codec() {
      return FailToCastGreatSpellTrigger.Instance.CODEC;
   }

   public void trigger(ServerPlayer player) {
      super.trigger(player, e -> true);
   }

   public record Instance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
      public static final Codec<FailToCastGreatSpellTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(FailToCastGreatSpellTrigger.Instance::player))
            .apply(instance, FailToCastGreatSpellTrigger.Instance::new)
      );
   }
}
