package dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule;

public interface LunarEventSpawnRule {
   boolean passes(SpawnRuleContext var1);

   LunarEventSpawnRuleType<?> type();
}
