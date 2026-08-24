package at.petrak.hexcasting.api.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class SpendMediaTrigger extends SimpleCriterionTrigger<SpendMediaTrigger.Instance> {
   public static final String ID = "spend_media";
   private static final String TAG_MEDIA_SPENT = "media_spent";
   private static final String TAG_MEDIA_WASTED = "media_wasted";

   public Codec<SpendMediaTrigger.Instance> codec() {
      return SpendMediaTrigger.Instance.CODEC;
   }

   public void trigger(ServerPlayer player, int mediaSpent, int mediaWasted) {
      super.trigger(player, inst -> inst.test(mediaSpent, mediaWasted));
   }

   public record Instance(Optional<ContextAwarePredicate> player, Ints mediaSpent, Ints mediaWasted) implements SimpleInstance {
      public static final Codec<SpendMediaTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SpendMediaTrigger.Instance::player),
               Ints.CODEC.optionalFieldOf("media_spent", Ints.ANY).forGetter(SpendMediaTrigger.Instance::mediaSpent),
               Ints.CODEC.optionalFieldOf("media_wasted", Ints.ANY).forGetter(SpendMediaTrigger.Instance::mediaWasted)
            )
            .apply(instance, SpendMediaTrigger.Instance::new)
      );

      private boolean test(int mediaSpentIn, int mediaWastedIn) {
         return this.mediaSpent.matches(mediaSpentIn) && this.mediaWasted.matches(mediaWastedIn);
      }
   }
}
