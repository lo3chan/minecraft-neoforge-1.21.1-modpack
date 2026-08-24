package net.joefoxe.hexerei.data.owl;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   modid = "hexerei"
)
public class OwlLoadedChunksSavedData extends SavedData {
   protected static final String DATA_NAME = "hexerei_owl_loaded_chunks";
   Map<ResourceKey<Level>, Map<ChunkPos, List<UUID>>> chunkData = new HashMap<>();
   Map<ResourceKey<Level>, Set<ChunkPos>> selfLoadedChunks = new HashMap<>();

   public OwlLoadedChunksSavedData addOwlLoading(ServerLevel level, OwlEntity owl, Set<ChunkPos> newChunks) {
      Map<ResourceKey<Level>, Set<ChunkPos>> lastChunksMap = owl.messagingController.getLastCheckedChunks();
      if (!this.chunkData.containsKey(level.dimension())) {
         this.chunkData.put(level.dimension(), new HashMap<>());
      }

      for (ChunkPos chunkPos : newChunks) {
         List<UUID> list;
         if (this.chunkData.get(level.dimension()).containsKey(chunkPos)) {
            list = this.chunkData.get(level.dimension()).get(chunkPos);
         } else {
            list = new ArrayList<>();
         }

         if (!list.contains(owl.getUUID())) {
            list.add(owl.getUUID());
            this.chunkData.get(level.dimension()).put(chunkPos, list);
         }
      }

      lastChunksMap.forEach((key, lastChunks) -> {
         for (ChunkPos chunkPosx : lastChunks) {
            if (!newChunks.contains(chunkPosx) && key.equals(level.dimension()) && this.chunkData.get(key).containsKey(chunkPosx)) {
               List<UUID> listx = this.chunkData.get(key).get(chunkPosx);
               listx.remove(owl.getUUID());
               if (listx.isEmpty()) {
                  this.chunkData.get(key).remove(chunkPosx);
                  if (!this.selfLoadedChunks.containsKey(key)) {
                     this.selfLoadedChunks.put((ResourceKey<Level>)key, new HashSet<>());
                  }

                  if (this.selfLoadedChunks.get(key).contains(chunkPosx)) {
                     level.setChunkForced(chunkPosx.x, chunkPosx.z, false);
                     this.selfLoadedChunks.get(key).remove(chunkPosx);
                  }
               }
            }
         }
      });
      this.setDirty();
      return this;
   }

   public void clearOwl(ServerLevel serverLevel, OwlEntity owl) {
      Map<ResourceKey<?>, Set<ChunkPos>> _to_remove = new HashMap<>();
      this.chunkData.forEach((dimension, data) -> data.forEach((chunkPos, uuids) -> {
         List<UUID> list = (List<UUID>)data.get(chunkPos);
         list.remove(owl.getUUID());
         if (list.isEmpty()) {
            if (!_to_remove.containsKey(dimension)) {
               _to_remove.put((ResourceKey<?>)dimension, new HashSet<>());
            }

            _to_remove.get(dimension).add(chunkPos);
            if (!this.selfLoadedChunks.containsKey(dimension)) {
               this.selfLoadedChunks.put((ResourceKey<Level>)dimension, new HashSet<>());
            }

            if (this.selfLoadedChunks.get(dimension).contains(chunkPos)) {
               ServerLevel level = serverLevel.getServer().getLevel(dimension);
               if (level != null) {
                  level.setChunkForced(chunkPos.x, chunkPos.z, false);
               }

               this.selfLoadedChunks.get(dimension).remove(chunkPos);
            }
         }
      }));
      _to_remove.forEach((dim, posSet) -> {
         for (ChunkPos pos : posSet) {
            if (this.chunkData.containsKey(dim)) {
               this.chunkData.get(dim).remove(pos);
            }
         }
      });
      this.setDirty();
   }

   public void tick(ServerLevel serverLevel) {
      this.chunkData.forEach((dimension, data) -> data.forEach((chunkPos, uuids) -> {
         ServerLevel level = serverLevel.getServer().getLevel(dimension);
         if (level != null) {
            LongSet forcedChunks = level.getForcedChunks();
            if (!this.selfLoadedChunks.containsKey(dimension)) {
               this.selfLoadedChunks.put((ResourceKey<Level>)dimension, new HashSet<>());
            }

            if (!this.selfLoadedChunks.get(dimension).contains(chunkPos) && !forcedChunks.contains(chunkPos.toLong())) {
               level.setChunkForced(chunkPos.x, chunkPos.z, true);
               this.selfLoadedChunks.get(dimension).add(chunkPos);
               this.setDirty();
            }
         }
      }));
   }

   @SubscribeEvent
   public static void serverTickEvent(Pre event) {
      get(event.getServer().overworld()).tick(event.getServer().overworld());
   }

   private static OwlLoadedChunksSavedData create(CompoundTag tag, Provider registries) {
      OwlLoadedChunksSavedData data = new OwlLoadedChunksSavedData();
      data.load(tag, registries);
      return data;
   }

   public void load(CompoundTag pCompoundTag, Provider registries) {
      this.selfLoadedChunks.clear();
      if (pCompoundTag.contains("selfLoadedChunks")) {
         CompoundTag selfLoadedChunksTag = pCompoundTag.getCompound("selfLoadedChunks");

         for (String key : selfLoadedChunksTag.getAllKeys()) {
            ListTag chunkListTag = selfLoadedChunksTag.getList(key, 10);
            ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(key));
            Set<ChunkPos> chunkPosSet = new HashSet<>();

            for (int i = 0; i < chunkListTag.size(); i++) {
               CompoundTag chunkTag = chunkListTag.getCompound(i);
               int x = chunkTag.getInt("x");
               int z = chunkTag.getInt("z");
               chunkPosSet.add(new ChunkPos(x, z));
            }

            this.selfLoadedChunks.put(levelKey, chunkPosSet);
         }
      }

      CompoundTag dataTag = pCompoundTag.getCompound("chunkData");
      dataTag.getAllKeys().forEach(resourceKeyString -> {
         ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(resourceKeyString));
         CompoundTag resourceTag = dataTag.getCompound(resourceKeyString);
         Map<ChunkPos, List<UUID>> chunkMap = new HashMap<>();
         resourceTag.getAllKeys().forEach(chunkPosString -> {
            CompoundTag chunkTagx = resourceTag.getCompound(chunkPosString);
            int xx = chunkTagx.getInt("x");
            int zx = chunkTagx.getInt("z");
            ChunkPos chunkPos = new ChunkPos(xx, zx);
            ListTag uuidTagList = chunkTagx.getList("UUIDs", 8);
            List<UUID> uuidList = new ArrayList<>();
            uuidTagList.forEach(uuidTag -> uuidList.add(UUID.fromString(uuidTag.getAsString())));
            chunkMap.put(chunkPos, uuidList);
         });
         this.chunkData.put(resourceKey, chunkMap);
      });
   }

   public CompoundTag save(CompoundTag pCompoundTag, Provider registries) {
      CompoundTag compoundTag = new CompoundTag();
      this.selfLoadedChunks.forEach((resourceKey, chunkSet) -> {
         ListTag chunkListTag = new ListTag();
         chunkSet.forEach(chunkPos -> {
            CompoundTag chunkNBT = new CompoundTag();
            chunkNBT.putInt("x", chunkPos.x);
            chunkNBT.putInt("z", chunkPos.z);
            chunkListTag.add(chunkNBT);
         });
         compoundTag.put(resourceKey.location().toString(), chunkListTag);
      });
      pCompoundTag.put("selfLoadedChunks", compoundTag);
      CompoundTag dataTag = new CompoundTag();
      this.chunkData.forEach((resourceKey, chunkMap) -> {
         CompoundTag resourceTag = new CompoundTag();
         chunkMap.forEach((chunkPos, uuidList) -> {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putInt("x", chunkPos.x);
            chunkTag.putInt("z", chunkPos.z);
            ListTag uuidTagList = new ListTag();
            uuidList.forEach(uuid -> uuidTagList.add(StringTag.valueOf(uuid.toString())));
            chunkTag.put("UUIDs", uuidTagList);
            resourceTag.put(chunkPos.x + "," + chunkPos.z, chunkTag);
         });
         dataTag.put(resourceKey.location().toString(), resourceTag);
      });
      pCompoundTag.put("chunkData", dataTag);
      return pCompoundTag;
   }

   public static Factory<OwlLoadedChunksSavedData> factory() {
      return new Factory(OwlLoadedChunksSavedData::new, OwlLoadedChunksSavedData::create, null);
   }

   public static OwlLoadedChunksSavedData get(ServerLevel world) {
      return (OwlLoadedChunksSavedData)world.getServer().overworld().getDataStorage().computeIfAbsent(factory(), "hexerei_owl_loaded_chunks");
   }

   public static OwlLoadedChunksSavedData get() {
      return (OwlLoadedChunksSavedData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(factory(), "hexerei_owl_loaded_chunks");
   }
}
