package com.iafenvoy.jupiter._loader.neoforge.network;

import com.iafenvoy.jupiter.network.ClientNetworkHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientNetworkHelperImpl implements ClientNetworkHelper {
   public static final Map<Type<CustomPacketPayload>, ClientNetworkHelper.Handler<CustomPacketPayload>> RECEIVERS = new HashMap<>();

   @Override
   public <T extends CustomPacketPayload> void registerReceiver(Type<T> id, ClientNetworkHelper.Handler<T> handler) {
      RECEIVERS.put(id, handler);
   }

   public static void handleData(CustomPacketPayload payload, IPayloadContext ctx) {
      RECEIVERS.entrySet()
         .stream()
         .filter(x -> x.getKey().id().equals(payload.type().id()))
         .map(e -> e.getValue().handle(Minecraft.getInstance(), payload))
         .filter(Objects::nonNull)
         .forEach(Runnable::run);
   }

   @Override
   public void sendToServer(CustomPacketPayload payload) {
      PacketDistributor.sendToServer(payload, new CustomPacketPayload[0]);
   }
}
