package dev.latvian.mods.kubejs.integration.gamestages;

import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(
   modid = "gamestages"
)
public class GameStagesIntegration {
   @SubscribeEvent
   public static void override(StageCreationEvent event) {
   }
}
