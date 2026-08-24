package dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.util.ArrayList;
import java.util.Collection;

public record ValidMoonPhaseRule(IntArraySet validMoonPhases) implements LunarEventSpawnRule {
   public static final MapCodec<ValidMoonPhaseRule> CODEC = Codec.list(Codec.intRange(0, 8))
      .fieldOf("valid_moon_phases")
      .xmap(ValidMoonPhaseRule::new, rule -> new ArrayList(rule.validMoonPhases));

   public ValidMoonPhaseRule(Collection<Integer> validMoonPhases) {
      this(new IntArraySet(validMoonPhases));
   }

   @Override
   public boolean passes(SpawnRuleContext context) {
      return this.validMoonPhases.contains(context.moonPhase());
   }

   @Override
   public LunarEventSpawnRuleType<?> type() {
      return LunarEventSpawnRuleTypes.VALID_MOON_PHASE;
   }
}
