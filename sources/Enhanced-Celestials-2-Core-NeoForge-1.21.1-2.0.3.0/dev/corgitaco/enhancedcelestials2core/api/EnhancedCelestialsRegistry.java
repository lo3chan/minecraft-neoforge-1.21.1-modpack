package dev.corgitaco.enhancedcelestials2core.api;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarDimensionSettings;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEventProbabilities;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.EquipmentSet;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifierType;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.LunarEventSpawnRule;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.LunarEventSpawnRuleType;
import dev.corgitaco.enhancedcelestials2core.platform.services.RegistrationService;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class EnhancedCelestialsRegistry {
   public static final ResourceKey<Registry<LunarEvent>> LUNAR_EVENT_KEY = ResourceKey.createRegistryKey(EnhancedCelestials.createLocation("lunar/event"));
   public static final ResourceKey<Registry<LunarEventProbabilities>> LUNAR_EVENT_PROBABILITIES_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/event_probability")
   );
   public static final ResourceKey<Registry<LunarDimensionSettings>> LUNAR_DIMENSION_SETTINGS_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/dimension_settings")
   );
   public static final ResourceKey<Registry<LunarEventModifier>> LUNAR_EVENT_MODIFIER_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/event_modifier")
   );
   public static final ResourceKey<Registry<LunarEventSpawnRule>> LUNAR_EVENT_SPAWN_RULE_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/event_spawn_rule")
   );
   public static final ResourceKey<Registry<EquipmentSet>> EQUIPMENT_SET_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/equipment_set")
   );
   public static final ResourceKey<Registry<LunarEventModifierType<?>>> LUNAR_EVENT_MODIFIER_TYPE_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/event_modifier_type")
   );
   public static final Registry<LunarEventModifierType<?>> LUNAR_EVENT_MODIFIER_TYPE = RegistrationService.INSTANCE
      .createSimpleBuiltin(LUNAR_EVENT_MODIFIER_TYPE_KEY);
   public static final ResourceKey<Registry<LunarEventSpawnRuleType<?>>> LUNAR_EVENT_SPAWN_RULE_TYPE_KEY = ResourceKey.createRegistryKey(
      EnhancedCelestials.createLocation("lunar/event_spawn_rule_type")
   );
   public static final Registry<LunarEventSpawnRuleType<?>> LUNAR_EVENT_SPAWN_RULE_TYPE = RegistrationService.INSTANCE
      .createSimpleBuiltin(LUNAR_EVENT_SPAWN_RULE_TYPE_KEY);

   public static void init() {
   }
}
