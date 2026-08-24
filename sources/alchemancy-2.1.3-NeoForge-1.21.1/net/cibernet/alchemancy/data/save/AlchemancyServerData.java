package net.cibernet.alchemancy.data.save;

import net.cibernet.alchemancy.network.S2CSetGlobalTimerPayload;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class AlchemancyServerData extends SavedData {
   private static long globalTimer = -1L;

   public static long getGlobalTimer() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         compute(server);
      }

      return globalTimer;
   }

   public static void setGlobalTimer(long t) {
      globalTimer = t;
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         compute(server).setDirty();
      }
   }

   public static void tickGlobalTimer() {
      globalTimer++;
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         compute(server).setDirty();
         if (globalTimer % 20L == 0L) {
            PacketDistributor.sendToAllPlayers(new S2CSetGlobalTimerPayload(globalTimer), new CustomPacketPayload[0]);
         }
      }
   }

   public static AlchemancyServerData compute(MinecraftServer server) {
      return (AlchemancyServerData)server.overworld()
         .getDataStorage()
         .computeIfAbsent(new Factory(AlchemancyServerData::new, AlchemancyServerData::load), "alchemancy_global");
   }

   public CompoundTag save(CompoundTag tag, Provider registries) {
      tag.putLong("global_timer", globalTimer);
      return tag;
   }

   private static AlchemancyServerData load(CompoundTag tag, Provider registries) {
      AlchemancyServerData data = new AlchemancyServerData();
      if (tag.contains("global_timer", 4)) {
         globalTimer = tag.getLong("global_timer");
      }

      return data;
   }
}
