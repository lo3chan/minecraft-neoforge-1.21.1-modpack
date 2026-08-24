package com.iafenvoy.origins;

import java.util.function.LongSupplier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class Proxies {
   public static LongSupplier TICK_COUNT = () -> {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return server == null ? 0L : server.getTickCount();
   };
}
