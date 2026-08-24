package dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.platform.services.RegistrationService;
import net.minecraft.core.Registry;

public final class LunarEventSpawnRuleTypes {
   public static final Codec<LunarEventSpawnRule> CODEC = EnhancedCelestialsRegistry.LUNAR_EVENT_SPAWN_RULE_TYPE
      .byNameCodec()
      .dispatch("type", LunarEventSpawnRule::type, LunarEventSpawnRuleType::codec);
   public static final LunarEventSpawnRuleType<MinNightsBetweenRule> MIN_NIGHTS_BETWEEN = register("min_nights_between", MinNightsBetweenRule.CODEC);
   public static final LunarEventSpawnRuleType<ValidMoonPhaseRule> VALID_MOON_PHASE = register("valid_moon_phase", ValidMoonPhaseRule.CODEC);

   private LunarEventSpawnRuleTypes() {
   }

   public static <T extends LunarEventSpawnRule> LunarEventSpawnRuleType<T> register(String id, MapCodec<T> codec) {
      MapCodec<T> settingsCodec = codec.fieldOf("settings");
      LunarEventSpawnRuleType<T> type = () -> settingsCodec;
      RegistrationService.INSTANCE
         .register((Registry<LunarEventSpawnRuleType<T>>)EnhancedCelestialsRegistry.LUNAR_EVENT_SPAWN_RULE_TYPE, "enhancedcelestials2core", id, () -> type);
      return type;
   }

   public static void loadClass() {
   }
}
