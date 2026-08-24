package dev.architectury.event.forge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

public class EventHandlerImpl {
   @OnlyIn(Dist.CLIENT)
   public static void registerClient() {
      NeoForge.EVENT_BUS.register(EventHandlerImplClient.class);
      EventBusesHooks.whenAvailable("architectury", bus -> bus.register(EventHandlerImplClient.ModBasedEventHandler.class));
   }

   public static void registerCommon() {
      NeoForge.EVENT_BUS.register(EventHandlerImplCommon.class);
      EventBusesHooks.whenAvailable("architectury", bus -> bus.register(EventHandlerImplCommon.ModBasedEventHandler.class));
   }

   @OnlyIn(Dist.DEDICATED_SERVER)
   public static void registerServer() {
   }
}
