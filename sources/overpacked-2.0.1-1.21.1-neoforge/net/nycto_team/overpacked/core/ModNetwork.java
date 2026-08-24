package net.nycto_team.overpacked.core;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.nycto_team.overpacked.core.packet.PlaceBackpackPacket;

@EventBusSubscriber(
   modid = "overpacked",
   bus = Bus.MOD
)
public class ModNetwork {
   @SubscribeEvent
   public static void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      registrar.playToServer(PlaceBackpackPacket.type, PlaceBackpackPacket.codec, PlaceBackpackPacket::Handle);
   }
}
