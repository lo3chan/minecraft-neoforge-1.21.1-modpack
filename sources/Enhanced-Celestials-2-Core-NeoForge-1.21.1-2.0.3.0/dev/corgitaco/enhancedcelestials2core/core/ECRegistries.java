package dev.corgitaco.enhancedcelestials2core.core;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifierTypes;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.LunarEventSpawnRuleTypes;
import dev.corgitaco.enhancedcelestials2core.core.lunarevent.DefaultLunarDimensionSettings;
import dev.corgitaco.enhancedcelestials2core.core.lunarevent.DefaultLunarEvents;

public record ECRegistries() {
   public static void loadClasses() {
      EnhancedCelestialsRegistry.init();
      LunarEventModifierTypes.loadClass();
      LunarEventSpawnRuleTypes.loadClass();
      DefaultLunarEvents.loadClass();
      DefaultLunarDimensionSettings.loadClass();
   }
}
