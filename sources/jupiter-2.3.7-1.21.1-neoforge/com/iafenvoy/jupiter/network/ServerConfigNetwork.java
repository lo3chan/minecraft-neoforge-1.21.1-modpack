package com.iafenvoy.jupiter.network;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.network.payload.ConfigErrorPayload;
import com.iafenvoy.jupiter.network.payload.ConfigRequestPayload;
import com.iafenvoy.jupiter.network.payload.ConfigSyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerConfigNetwork {
   public static void init() {
      ServerNetworkHelper.INSTANCE.registerReceiver(ConfigRequestPayload.TYPE, (server, player, payload) -> onConfigRequest(server, player, payload.id()));
      ServerNetworkHelper.INSTANCE
         .registerReceiver(ConfigSyncPayload.TYPE, (server, player, payload) -> onConfigSync(server, player, payload.id(), payload.compound()));
   }

   private static Runnable onConfigRequest(MinecraftServer server, ServerPlayer player, ResourceLocation id) {
      Jupiter.LOGGER.info("Player {} request to get config {}", player.getName().getString(), id);
      boolean b = ServerConfigManager.checkPermission(id, server, player, false);
      CompoundTag compound;
      if (b) {
         AbstractConfigContainer data = ServerConfigManager.getConfig(id);
         if (data == null) {
            compound = new CompoundTag();
            b = false;
         } else {
            compound = data.serializeNbt();
         }
      } else {
         compound = new CompoundTag();
      }

      boolean finalB = b;
      return () -> ServerNetworkHelper.INSTANCE.sendToPlayer(player, new ConfigSyncPayload(id, finalB, compound));
   }

   private static Runnable onConfigSync(MinecraftServer server, ServerPlayer player, ResourceLocation id, CompoundTag data) {
      Jupiter.LOGGER.info("Player {} request to change config {}", player.getName().getString(), id);
      return () -> {
         if (ServerConfigManager.checkPermission(id, server, player, true)) {
            AbstractConfigContainer container = ServerConfigManager.getConfig(id);
            if (container != null) {
               Jupiter.LOGGER.info(data.toString());
               container.deserializeNbt(data);
               container.onConfigsChanged();
               Jupiter.LOGGER.info("Player {} changed config {}", player.getName().getString(), id);
            }
         } else {
            ServerNetworkHelper.INSTANCE.sendToPlayer(player, new ConfigErrorPayload());
         }
      };
   }
}
