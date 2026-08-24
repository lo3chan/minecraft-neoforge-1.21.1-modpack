package dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule;

import com.mojang.serialization.MapCodec;

public interface LunarEventSpawnRuleType<T extends LunarEventSpawnRule> {
   MapCodec<T> codec();
}
