package at.petrak.hexcasting.api.advancements;

import at.petrak.hexcasting.api.mod.HexConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class OvercastTrigger extends SimpleCriterionTrigger<OvercastTrigger.Instance> {
   public static final String ID = "overcast";
   private static final String TAG_MEDIA_GENERATED = "media_generated";
   private static final String TAG_HEALTH_USED = "health_used";
   private static final String TAG_HEALTH_LEFT = "mojang_i_am_begging_and_crying_please_add_an_entity_health_criterion";

   public Codec<OvercastTrigger.Instance> codec() {
      return OvercastTrigger.Instance.CODEC;
   }

   public void trigger(ServerPlayer player, int mediaGenerated) {
      super.trigger(player, inst -> {
         double mediaToHealth = HexConfig.common().mediaToHealthRate();
         double healthUsed = mediaGenerated / mediaToHealth;
         return inst.test(mediaGenerated, healthUsed / player.getMaxHealth(), player.getHealth());
      });
   }

   public record Instance(Optional<ContextAwarePredicate> player, Ints mediaGenerated, Doubles healthUsed, Doubles healthLeft) implements SimpleInstance {
      public static final Codec<OvercastTrigger.Instance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(OvercastTrigger.Instance::player),
               Ints.CODEC.optionalFieldOf("media_generated", Ints.ANY).forGetter(OvercastTrigger.Instance::mediaGenerated),
               Doubles.CODEC.optionalFieldOf("health_used", Doubles.ANY).forGetter(OvercastTrigger.Instance::healthUsed),
               Doubles.CODEC
                  .optionalFieldOf("mojang_i_am_begging_and_crying_please_add_an_entity_health_criterion", Doubles.ANY)
                  .forGetter(OvercastTrigger.Instance::healthLeft)
            )
            .apply(instance, OvercastTrigger.Instance::new)
      );

      private boolean test(int mediaGeneratedIn, double healthUsedIn, float healthLeftIn) {
         return this.mediaGenerated.matches(mediaGeneratedIn) && this.healthUsed.matches(healthUsedIn) && this.healthLeft.matches(healthLeftIn);
      }
   }
}
