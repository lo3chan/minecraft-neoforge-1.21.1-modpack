package com.github.alexthe666.citadel.server.world;

import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class CitadelServerData extends SavedData {
   private static Map<MinecraftServer, CitadelServerData> dataMap = new HashMap<>();
   private static final String IDENTIFIER = "citadel_world_data";
   private MinecraftServer server;
   private ServerTickRateTracker tickRateTracker = null;

   public CitadelServerData(MinecraftServer server) {
      this.server = server;
   }

   public static Factory<CitadelServerData> factory(MinecraftServer level) {
      return new Factory(() -> new CitadelServerData(level), (tag, provider) -> load(level, tag), null);
   }

   public static CitadelServerData get(MinecraftServer server) {
      CitadelServerData fromMap = dataMap.get(server);
      if (fromMap == null) {
         DimensionDataStorage storage = server.getLevel(Level.OVERWORLD).getDataStorage();
         CitadelServerData data = (CitadelServerData)storage.computeIfAbsent(factory(server), "citadel_world_data");
         data.setDirty();
         dataMap.put(server, data);
         return data;
      } else {
         return fromMap;
      }
   }

   public static CitadelServerData load(MinecraftServer server, CompoundTag tag) {
      CitadelServerData data = new CitadelServerData(server);
      if (tag.contains("TickRateTracker")) {
         data.tickRateTracker = new ServerTickRateTracker(server, tag.getCompound("TickRateTracker"));
      } else {
         data.tickRateTracker = new ServerTickRateTracker(server);
      }

      return data;
   }

   public ServerTickRateTracker getOrCreateTickRateTracker() {
      if (this.tickRateTracker == null) {
         this.tickRateTracker = new ServerTickRateTracker(this.server);
      }

      return this.tickRateTracker;
   }

   public CompoundTag save(CompoundTag tag, Provider registries) {
      if (this.tickRateTracker != null) {
         tag.put("TickRateTracker", this.tickRateTracker.toTag());
      }

      return tag;
   }
}
