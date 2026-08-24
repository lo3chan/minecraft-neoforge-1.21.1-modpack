package dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record MinNightsBetweenRule(int minNights) implements LunarEventSpawnRule {
   public static final MapCodec<MinNightsBetweenRule> CODEC = Codec.INT.fieldOf("min_nights").xmap(MinNightsBetweenRule::new, MinNightsBetweenRule::minNights);

   @Override
   public boolean passes(SpawnRuleContext context) {
      return context.lastDayOfThisEvent() == -1L || context.day() - context.lastDayOfThisEvent() > this.minNights;
   }

   @Override
   public LunarEventSpawnRuleType<?> type() {
      return LunarEventSpawnRuleTypes.MIN_NIGHTS_BETWEEN;
   }
}
