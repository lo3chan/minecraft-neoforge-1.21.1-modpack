package io.wispforest.owo.offline;

import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface DataSavedEvents {
   Event<DataSavedEvents.PlayerData> PLAYER_DATA = EventFactory.createArrayBacked(DataSavedEvents.PlayerData.class, callbacks -> (playerUuid, newTag) -> {
      for (DataSavedEvents.PlayerData callback : callbacks) {
         callback.onSaved(playerUuid, newTag);
      }
   });
   Event<DataSavedEvents.Advancements> ADVANCEMENTS = EventFactory.createArrayBacked(DataSavedEvents.Advancements.class, callbacks -> (playerUuid, newMap) -> {
      for (DataSavedEvents.Advancements callback : callbacks) {
         callback.onSaved(playerUuid, newMap);
      }
   });

   public interface Advancements {
      void onSaved(UUID var1, Map<ResourceLocation, AdvancementProgress> var2);
   }

   public interface PlayerData {
      void onSaved(UUID var1, CompoundTag var2);
   }
}
