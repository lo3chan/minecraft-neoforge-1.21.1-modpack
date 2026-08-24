package net.nycto_team.overpacked.core;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nycto_team.overpacked.core.packet.PlaceBackpackPacket;
import net.nycto_team.overpacked.registry.ModKeyBinds;
import net.nycto_team.overpacked.util.Utils;

@EventBusSubscriber(
   modid = "overpacked",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class KeyBindHandler {
   @SubscribeEvent
   public static void onKeyInput(Key event) {
      if (ModKeyBinds.take_off.isDown() && !Utils.get_curio_backpack(Minecraft.getInstance().player).isEmpty()) {
         PacketDistributor.sendToServer(new PlaceBackpackPacket(), new CustomPacketPayload[0]);
      }
   }
}
