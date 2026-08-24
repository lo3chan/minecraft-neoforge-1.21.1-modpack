package net.blay09.mods.balm.common.config;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.ConfigReloadedEvent;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.client.DisconnectedFromServerEvent;
import net.blay09.mods.balm.api.module.BalmModule;
import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.network.ClientboundConfigPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class ConfigSync implements BalmModule {
   public static boolean hasSyncedProperties(BalmConfigSchema schema) {
      return schema.rootProperties().stream().anyMatch(ConfiguredProperty::synced) || schema.categories().stream().anyMatch(ConfigSync::hasSyncedProperties);
   }

   public static boolean hasSyncedProperties(ConfigCategory category) {
      return category.properties().stream().anyMatch(ConfiguredProperty::synced);
   }

   @Override
   public ResourceLocation getId() {
      return ResourceLocation.fromNamespaceAndPath("balm", "config_sync");
   }

   @Override
   public void registerNetworking(BalmNetworking networking) {
      networking.registerClientboundPacket(
         ClientboundConfigPacket.TYPE, ClientboundConfigPacket.class, ClientboundConfigPacket.STREAM_CODEC, ClientboundConfigPacket::handle
      );
   }

   @Override
   public void registerEvents(BalmEvents events) {
      events.onEvent(PlayerLoginEvent.class, event -> {
         for (BalmConfigSchema schema : Balm.getConfig().getSchemas()) {
            if (hasSyncedProperties(schema)) {
               LoadedConfig loaded = Balm.getConfig().getActiveConfig(schema);
               if (loaded != null) {
                  ClientboundConfigPacket packet = new ClientboundConfigPacket(schema, loaded);
                  Balm.getNetworking().sendTo(event.getPlayer(), packet);
               }
            }
         }
      });
      events.onEvent(ConfigReloadedEvent.class, event -> {
         MinecraftServer server = Balm.getHooks().getServer();
         if (server != null) {
            BalmConfigSchema schema = event.getSchema();
            if (schema != null && hasSyncedProperties(schema)) {
               LoadedConfig loaded = Balm.getConfig().getActiveConfig(schema);
               if (loaded != null) {
                  ClientboundConfigPacket packet = new ClientboundConfigPacket(schema, loaded);
                  Balm.getNetworking().sendToAll(server, packet);
               }
            }
         }
      });
      events.onEvent(DisconnectedFromServerEvent.class, event -> {
         if (Balm.getConfig() instanceof AbstractBalmConfig abstractBalmConfig) {
            abstractBalmConfig.resetToLocalConfig();
         }
      });
   }
}
