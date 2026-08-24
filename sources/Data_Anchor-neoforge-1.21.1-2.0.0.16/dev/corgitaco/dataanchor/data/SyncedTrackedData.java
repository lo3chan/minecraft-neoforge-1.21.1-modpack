package dev.corgitaco.dataanchor.data;

import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface SyncedTrackedData extends ServerTrackedData, ClientTrackedData {
   void sync();

   void syncToPlayer(ServerPlayer var1);

   Packet syncPacket();

   default void readFromNetwork(CompoundTag tag) {
      if (this instanceof TrackedData trackedData) {
         trackedData.load(tag);
      }
   }

   default CompoundTag writeToNetwork() {
      return this instanceof TrackedData trackedData ? trackedData.save() : new CompoundTag();
   }
}
