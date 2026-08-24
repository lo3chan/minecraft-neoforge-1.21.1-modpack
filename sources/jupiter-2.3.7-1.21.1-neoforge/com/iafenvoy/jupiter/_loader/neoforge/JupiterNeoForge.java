package com.iafenvoy.jupiter._loader.neoforge;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter._loader.neoforge.network.ClientNetworkHelperImpl;
import com.iafenvoy.jupiter._loader.neoforge.network.ServerNetworkHelperImpl;
import java.util.Map.Entry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("jupiter")
@EventBusSubscriber
public class JupiterNeoForge {
   public JupiterNeoForge() {
      Jupiter.init(!FMLEnvironment.production);
   }

   @SubscribeEvent
   public static void process(FMLCommonSetupEvent event) {
      Jupiter.process();
   }

   @SubscribeEvent
   public static void registerNetwork(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");

      for (Entry<Type<CustomPacketPayload>, StreamCodec<FriendlyByteBuf, CustomPacketPayload>> entry : ServerNetworkHelperImpl.TYPES.entrySet()) {
         registrar.playBidirectional(
            entry.getKey(), entry.getValue(), new DirectionalPayloadHandler(ClientNetworkHelperImpl::handleData, ServerNetworkHelperImpl::handleData)
         );
      }
   }

   @SubscribeEvent
   public static void registerServerListener(AddReloadListenerEvent event) {
      event.addListener(new ServerConfigManager());
   }
}
