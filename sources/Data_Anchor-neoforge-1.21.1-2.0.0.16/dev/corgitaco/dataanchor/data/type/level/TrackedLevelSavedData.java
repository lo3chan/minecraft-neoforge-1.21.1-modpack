package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class TrackedLevelSavedData extends SavedData implements TrackedDataContainer<Level, LevelTrackedData> {
   public static final String DATA_NAME = "dataanchor_saved_tracked_data";
   private final Map<TrackedDataKey<LevelTrackedData>, LevelTrackedData> trackedDataMap = new Reference2ReferenceOpenHashMap();
   private final List<TickableTrackedData> tickableData = new ArrayList<>();
   private final ServerLevel serverLevel;

   public void setDirty(boolean dirty) {
      super.setDirty(dirty);
      if (!dirty && this.serverLevel instanceof InternalDirtyMarker dirtyMarker) {
         dirtyMarker.dataAnchor$clearDirty();
      }
   }

   private TrackedLevelSavedData(ServerLevel serverLevel, CompoundTag tag) {
      this.serverLevel = serverLevel;
      this.dataAnchor$createTrackedData();

      for (Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : this.trackedDataMap.entrySet()) {
         String idString = entry.getKey().getId().toString();
         if (tag.contains(idString, 10)) {
            entry.getValue().load(tag.getCompound(idString));
         }
      }
   }

   private TrackedLevelSavedData(ServerLevel serverLevel) {
      this(serverLevel, new CompoundTag());
   }

   public static TrackedLevelSavedData get(ServerLevel world) {
      DimensionDataStorage data = world.getDataStorage();
      return (TrackedLevelSavedData)data.computeIfAbsent(
         new Factory(
            () -> new TrackedLevelSavedData(world), (compoundTag, provider) -> new TrackedLevelSavedData(world, compoundTag), DataFixTypes.SAVED_DATA_MAP_DATA
         ),
         "dataanchor_saved_tracked_data"
      );
   }

   public CompoundTag save(CompoundTag compoundTag, Provider registries) {
      for (Entry<TrackedDataKey<LevelTrackedData>, LevelTrackedData> entry : this.trackedDataMap.entrySet()) {
         CompoundTag save = entry.getValue().save();
         if (save != null) {
            compoundTag.put(entry.getKey().getId().toString(), save);
         }
      }

      return compoundTag;
   }

   @Override
   public <E extends LevelTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
      LevelTrackedData levelTrackedData = this.trackedDataMap.get(key);
      return levelTrackedData == null ? Optional.empty() : Optional.of((E)levelTrackedData);
   }

   @Override
   public void dataAnchor$createTrackedData() {
      TrackedDataRegistries.LEVEL.factories().forEach((key, factory) -> {
         LevelTrackedData trackedData = factory.create((TrackedDataKey<LevelTrackedData>)key, this.serverLevel);
         if (trackedData instanceof ServerTrackedData) {
            if (trackedData instanceof TickableTrackedData tickableData) {
               this.tickableData.add(tickableData);
            }

            this.trackedDataMap.put((TrackedDataKey<LevelTrackedData>)key, trackedData);
         }
      });
   }

   @Override
   public Collection<TrackedDataKey<LevelTrackedData>> dataAnchor$getTrackedDataKeys() {
      return this.trackedDataMap.keySet();
   }
}
