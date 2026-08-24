package net.nycto_team.overpacked.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.nycto_team.overpacked.registry.ModEntities;
import net.nycto_team.overpacked.registry.ModKeyBinds;

@EventBusSubscriber(
   modid = "overpacked",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientEvents {
   @SubscribeEvent
   public static void registerLayer(RegisterLayerDefinitions event) {
      ModEntities.RegisterLayers(event);
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      ModEntities.ClientSetup();
   }

   @SubscribeEvent
   public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
      event.register(ModKeyBinds.take_off);
   }
}
