package dev.corgitaco.enhancedcelestials2core.neoforge;

import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarDimensionSettings;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEventProbabilities;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.EquipmentSet;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifierTypes;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.LunarEventSpawnRuleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "enhancedcelestials2core"
)
public class ECModBusEventsHandler {
   @SubscribeEvent
   public static void registerDatapack(NewRegistry event) {
      event.dataPackRegistry(EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_KEY, LunarEventModifierTypes.CODEC, LunarEventModifierTypes.CODEC);
      event.dataPackRegistry(EnhancedCelestialsRegistry.LUNAR_EVENT_SPAWN_RULE_KEY, LunarEventSpawnRuleTypes.CODEC, LunarEventSpawnRuleTypes.CODEC);
      event.dataPackRegistry(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, LunarEvent.DIRECT_CODEC, LunarEvent.DIRECT_CODEC);
      event.dataPackRegistry(EnhancedCelestialsRegistry.LUNAR_DIMENSION_SETTINGS_KEY, LunarDimensionSettings.CODEC, LunarDimensionSettings.CODEC);
      event.dataPackRegistry(EnhancedCelestialsRegistry.LUNAR_EVENT_PROBABILITIES_KEY, LunarEventProbabilities.CODEC, LunarEventProbabilities.CODEC);
      event.dataPackRegistry(EnhancedCelestialsRegistry.EQUIPMENT_SET_KEY, EquipmentSet.DIRECT_CODEC, EquipmentSet.DIRECT_CODEC);
   }
}
