package net.diebuddies.mixins.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.minecraft.ClientChunkCacheAccessor;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.thread.OceanChunkCreator;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.thread.ChunkCreator;
import net.diebuddies.physics.snow.thread.SnowChunkCreator;
import net.diebuddies.physics.vines.DynamicLoader;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.FastBlockSearcher;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.diebuddies.physics.vines.MultiSearcherConsumer;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSearcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientChunkCache.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityTagOutput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientChunkCache.class})
public class MixinClientChunkManager implements DynamicLoader, ClientChunkCacheAccessor {
   @Shadow
   @Final
   protected volatile Storage storage;
   @Shadow
   @Final
   protected ClientLevel level;
   @Unique
   protected Long2ObjectMap<List<DynamicRagdoll>> loadedVines = new Long2ObjectOpenHashMap();
   @Unique
   protected PhysicsMod mod;
   @Unique
   protected LongSet loadedChunksSodiumFix = new LongOpenHashSet();

   @Override
   public void chunkPosChanged() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();
         ObjectSet<Vector3i> affectedChunks = new ObjectOpenHashSet();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            boolean isLoaded = this.loadedVines.containsKey(chunkIndex);
            boolean shouldBeLoaded = VineHelper.isChunkInRange(chunkX, chunkZ);
            if (isLoaded != shouldBeLoaded) {
               if (isLoaded) {
                  this.unloadDynamicBlockChunk(chunkX, chunkZ, affectedChunks, true);
               } else {
                  this.loadDynamicBlockChunk(((ClientChunkCache)this).getChunk(chunkX, chunkZ, false), chunkX, chunkZ, affectedChunks);
               }

               ObjectIterator var9 = affectedChunks.iterator();

               while (var9.hasNext()) {
                  Vector3i affectedChunk = (Vector3i)var9.next();
                  if (StarterClient.sodium) {
                     Sodium.scheduleChunkRebuild(Minecraft.getInstance().levelRenderer, affectedChunk.x, affectedChunk.y, affectedChunk.z, true);
                  } else {
                     Minecraft.getInstance().levelRenderer.setSectionDirty(affectedChunk.x, affectedChunk.y, affectedChunk.z, true);
                  }
               }

               affectedChunks.clear();
            }
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"updateViewRadius"}
   )
   public void updateLoadDistance(int loadDistance, CallbackInfo info) {
      int properLoadDistance = Math.max(loadDistance, 2) + 3;
      LongIterator itLoaded = this.loadedChunksSodiumFix.iterator();

      while (itLoaded.hasNext()) {
         long chunkIndex = itLoaded.nextLong();
         int chunkX = ChunkHelper.getChunkX(chunkIndex);
         int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
         if (!this.isInRadius(properLoadDistance, chunkX, chunkZ)) {
            itLoaded.remove();
         }
      }

      if (this.mod != null) {
         Iterator<Entry<List<DynamicRagdoll>>> it = this.loadedVines.long2ObjectEntrySet().iterator();

         while (it.hasNext()) {
            Entry<List<DynamicRagdoll>> entry = it.next();
            long chunkIndex = entry.getLongKey();
            List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)entry.getValue();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            if (!this.isInRadius(properLoadDistance, chunkX, chunkZ)) {
               this.unloadRagdolls(ragdolls, false);
               it.remove();
            }
         }

         if (ConfigClient.areSnowPhysicsEnabled()) {
            VAO.storePreviouslyBoundState();
            this.unloadAllSnow();
            this.loadAllSnow();
            VAO.restorePreviouslyBoundState();
         }

         if (ConfigClient.areOceanPhysicsEnabled()) {
            VAO.storePreviouslyBoundState();
            this.unloadAllOcean();
            this.loadAllOcean();
            VAO.restorePreviouslyBoundState();
         }
      }
   }

   @Unique
   public boolean isInRadius(int radius, int chunkX, int chunkZ) {
      return Math.abs(chunkX - this.storage.viewCenterX) <= radius && Math.abs(chunkZ - this.storage.viewCenterZ) <= radius;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"drop"}
   )
   public void drop(ChunkPos chunkPos, CallbackInfo info) {
      int chunkX = chunkPos.x;
      int chunkZ = chunkPos.z;
      long chunkIndex = ChunkHelper.calcChunkIndex(chunkX, chunkZ);
      this.loadedChunksSodiumFix.remove(chunkIndex);
      if (this.mod != null) {
         this.unloadDynamicBlockChunk(chunkX, chunkZ);
         if (ConfigClient.areSnowPhysicsEnabled()) {
            this.mod.getPhysicsWorld().getSnowWorld().removeChunkColumn(chunkX, chunkZ);
         }

         if (ConfigClient.areOceanPhysicsEnabled()) {
            this.mod.getPhysicsWorld().getOceanWorld().removeChunkColumn(chunkX, chunkZ);
         }
      }
   }

   @Unique
   protected void unloadDynamicBlockChunk(int chunkX, int chunkZ) {
      this.unloadDynamicBlockChunk(chunkX, chunkZ, null, false);
   }

   @Unique
   protected void unloadDynamicBlockChunk(int chunkX, int chunkZ, ObjectSet<Vector3i> affectedChunks, boolean removeOneFrameLater) {
      long chunkIndex = ChunkHelper.calcChunkIndex(chunkX, chunkZ);
      List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)this.loadedVines.remove(chunkIndex);
      if (affectedChunks != null && ragdolls != null) {
         for (DynamicRagdoll ragdoll : ragdolls) {
            for (BlockPos pos : ragdoll.getBlockPositions()) {
               affectedChunks.add(
                  new Vector3i(
                     SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ())
                  )
               );
            }
         }
      }

      this.unloadRagdolls(ragdolls, removeOneFrameLater);
   }

   @Unique
   protected void unloadRagdolls(List<DynamicRagdoll> ragdolls, boolean removeOneFrameLater) {
      if (ragdolls != null) {
         for (DynamicRagdoll ragdoll : ragdolls) {
            if (removeOneFrameLater) {
               this.mod.sodiumRemoveRagdolls.add(ragdoll);
            } else {
               this.mod.physicsWorld.removeRagdoll(ragdoll);
            }
         }
      }
   }

   @Override
   public void unloadAllRagdolls() {
      ObjectIterator var1 = this.loadedVines.values().iterator();

      while (var1.hasNext()) {
         List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)var1.next();
         this.unloadRagdolls(ragdolls, false);
      }

      this.loadedVines.clear();
   }

   @Override
   public void loadAllRagdolls() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)this).getChunk(chunkX, chunkZ, null, false);
            if (VineHelper.isChunkInRange(chunkX, chunkZ)) {
               this.loadDynamicBlockChunk(chunk, chunkX, chunkZ);
            }
         }
      }
   }

   @Override
   public void unloadAllSnow() {
      if (this.mod != null) {
         this.mod.getPhysicsWorld().getSnowWorld().removeAll();
         this.mod.getPhysicsWorld().getSnowWorld().destroy();
         IChunk.updateChunkSize();
         this.mod.getPhysicsWorld().setSnowWorld(new SnowWorld(this.mod.getPhysicsWorld().getLevel()));
      }
   }

   @Override
   public void loadAllSnow() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)this).getChunk(chunkX, chunkZ, null, false);
            this.loadSnowChunk(chunk, chunkX, chunkZ);
         }
      }
   }

   @Override
   public void unloadAllOcean() {
      if (this.mod != null) {
         this.mod.getPhysicsWorld().getOceanWorld().removeAll();
         this.mod.getPhysicsWorld().getOceanWorld().destroy();
         this.mod.getPhysicsWorld().setOceanWorld(new OceanWorld(this.mod.getPhysicsWorld(), this.level));
      }
   }

   @Override
   public void loadAllOcean() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)this).getChunk(chunkX, chunkZ, null, false);
            this.loadOceanChunk(chunk, chunkX, chunkZ);
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"replaceWithPacketData"}
   )
   public void replaceWithPacketDataHead(
      int x, int z, FriendlyByteBuf buf, CompoundTag nbt, Consumer<BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> info
   ) {
      StorageInvoker storageInvoker = (StorageInvoker)this.storage;
      if (storageInvoker.invokeInRange(x, z)) {
         int storageIndex = storageInvoker.invokeGetIndex(x, z);
         LevelChunk levelChunk = storageInvoker.invokeGetChunk(storageIndex);
         if (levelChunk != null) {
            ChunkPos chunkPos = levelChunk.getPos();
            int chunkX = chunkPos.x;
            int chunkZ = chunkPos.z;
            if (chunkX != x || chunkZ != z) {
               long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
               this.loadedChunksSodiumFix.remove(chunkIndex);
               if (this.mod != null) {
                  this.unloadDynamicBlockChunk(chunkX, chunkZ);
                  if (ConfigClient.areSnowPhysicsEnabled()) {
                     this.mod.getPhysicsWorld().getSnowWorld().removeChunkColumn(chunkX, chunkZ);
                  }

                  if (ConfigClient.areOceanPhysicsEnabled()) {
                     this.mod.getPhysicsWorld().getOceanWorld().removeChunkColumn(chunkX, chunkZ);
                  }
               }
            }
         }
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"replaceWithPacketData"}
   )
   public void replaceWithPacketData(
      int x, int z, FriendlyByteBuf buf, CompoundTag nbt, Consumer<BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> info
   ) {
      LevelChunk chunk = (LevelChunk)info.getReturnValue();
      if (chunk != null) {
         long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
         this.loadedChunksSodiumFix.add(chunkIndex);
         if (this.mod != null) {
            this.loadCombinedPhysicsChunk(chunk, x, z);
         }
      }
   }

   @Unique
   protected void loadCombinedPhysicsChunk(LevelChunk chunk, int x, int z) {
      boolean snow = ConfigClient.areSnowPhysicsEnabled();
      boolean ocean = ConfigClient.areOceanPhysicsEnabled();
      boolean dynamicBlocks = ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.isChunkInRange(x, z);
      if (chunk != null && (snow || ocean || dynamicBlocks)) {
         long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
         LevelChunkSection[] sections = chunk.getSections();
         SnowWorld snowWorld = this.mod.getPhysicsWorld().getSnowWorld();
         OceanWorld oceanWorld = this.mod.getPhysicsWorld().getOceanWorld();
         List<ChunkCreator> loadSnowChunks = new ObjectArrayList();
         List<OceanChunkCreator> loadOceanChunks = new ObjectArrayList();
         Long2ObjectMap<BlockState> vines = new Long2ObjectLinkedOpenHashMap();
         List<FastBlockSearcherConsumer> consumers = new ObjectArrayList();

         for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            Palette<BlockState> palette = section.getStates().data.palette();
            Map<Vector3i, BlockState> snowBlocks = new Object2ObjectOpenHashMap();
            int y = i + chunk.getLevel().getMinBuildHeight() / 16;
            if (!section.hasOnlyAir()) {
               if (snow && palette.maybeHas(SnowSearcher::isPhysicsSnow)) {
                  consumers.add(new SnowSearcher(snowWorld, snowBlocks, x * 16, i * 16 + chunk.getLevel().getMinBuildHeight(), z * 16, palette));
               }

               if (ocean) {
                  loadOceanChunks.add(new OceanChunkCreator(section.getStates().copy(), x, y, z));
               }

               if (dynamicBlocks && palette.maybeHas(VineSearcher::isPhysicsDynamicBlock)) {
                  int sectionY = chunk.getSectionYFromSectionIndex(i);
                  int bottomBlockY = SectionPos.sectionToBlockCoord(sectionY);
                  consumers.add(new VineSearcher(vines, palette, bottomBlockY));
               }

               if (!consumers.isEmpty()) {
                  ((FastBlockSearcher)section.getStates().data.storage()).getAllFast(new MultiSearcherConsumer(palette, consumers));
               }

               consumers.clear();
            } else if (ocean) {
               loadOceanChunks.add(new OceanChunkCreator((byte)0, x, y, z));
            }

            if (snow) {
               SnowChunkCreator creator = new SnowChunkCreator(snowWorld, snowBlocks, x, y, z);
               loadSnowChunks.add(creator);
            }
         }

         if (!loadSnowChunks.isEmpty()) {
            snowWorld.addChunkColumn(loadSnowChunks, x, z);
         }

         if (!loadOceanChunks.isEmpty()) {
            oceanWorld.addChunkColumn(loadOceanChunks, x, z);
         }

         if (dynamicBlocks) {
            List<DynamicRagdoll> ragdolls = this.searchConnections(x, z, vines);
            this.unloadDynamicBlockChunk(x, z);

            for (Ragdoll ragdoll : ragdolls) {
               this.mod.physicsWorld.addRagdoll(ragdoll);
            }

            this.loadedVines.put(chunkIndex, ragdolls);
         }
      }
   }

   @Unique
   protected boolean isValidStorageChunk(@Nullable LevelChunk levelChunk, int x, int z) {
      if (levelChunk == null) {
         return false;
      } else {
         ChunkPos chunkPos = levelChunk.getPos();
         return chunkPos.x == x && chunkPos.z == z;
      }
   }

   @Unique
   protected void loadSnowChunk(LevelChunk chunk, int x, int z) {
      if (ConfigClient.areSnowPhysicsEnabled() && chunk != null) {
         LevelChunkSection[] sections = chunk.getSections();
         SnowWorld snowWorld = this.mod.getPhysicsWorld().getSnowWorld();
         List<ChunkCreator> loadChunks = new ObjectArrayList();

         for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            Map<Vector3i, BlockState> snow = new Object2ObjectOpenHashMap();
            Palette<BlockState> palette = section.getStates().data.palette();
            if (!section.hasOnlyAir() && palette.maybeHas(SnowSearcher::isPhysicsSnow)) {
               SnowSearcher consumer = new SnowSearcher(snowWorld, snow, x * 16, i * 16 + chunk.getLevel().getMinBuildHeight(), z * 16, palette);
               ((FastBlockSearcher)section.getStates().data.storage()).getAllFast(consumer);
            }

            int y = i + chunk.getLevel().getMinBuildHeight() / 16;
            SnowChunkCreator creator = new SnowChunkCreator(snowWorld, snow, x, y, z);
            loadChunks.add(creator);
         }

         if (!loadChunks.isEmpty()) {
            snowWorld.addChunkColumn(loadChunks, x, z);
         }
      }
   }

   @Unique
   protected void loadOceanChunk(LevelChunk chunk, int x, int z) {
      if (ConfigClient.areOceanPhysicsEnabled() && chunk != null) {
         LevelChunkSection[] sections = chunk.getSections();
         OceanWorld oceanWorld = this.mod.getPhysicsWorld().getOceanWorld();
         List<OceanChunkCreator> loadChunks = new ObjectArrayList();

         for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            int y = i + chunk.getLevel().getMinBuildHeight() / 16;
            if (!section.hasOnlyAir()) {
               loadChunks.add(new OceanChunkCreator(section.getStates().copy(), x, y, z));
            } else {
               loadChunks.add(new OceanChunkCreator((byte)0, x, y, z));
            }
         }

         if (!loadChunks.isEmpty()) {
            oceanWorld.addChunkColumn(loadChunks, x, z);
         }
      }
   }

   @Unique
   protected void loadDynamicBlockChunk(LevelChunk chunk, int x, int z) {
      this.loadDynamicBlockChunk(chunk, x, z, null);
   }

   @Unique
   protected void loadDynamicBlockChunk(LevelChunk chunk, int x, int z, ObjectSet<Vector3i> affectedChunks) {
      long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
      if (ConfigClient.areDynamicBlockPhysicsEnabled() && chunk != null) {
         LevelChunkSection[] sections = chunk.getSections();
         Long2ObjectMap<BlockState> vines = new Long2ObjectLinkedOpenHashMap();

         for (int i = 0; i < sections.length; i++) {
            LevelChunkSection section = sections[i];
            boolean affected = false;
            Palette<BlockState> palette = section.getStates().data.palette();
            if (!section.hasOnlyAir() && palette.maybeHas(VineSearcher::isPhysicsDynamicBlock)) {
               int sectionY = chunk.getSectionYFromSectionIndex(i);
               int bottomBlockY = SectionPos.sectionToBlockCoord(sectionY);
               VineSearcher consumer = new VineSearcher(vines, palette, bottomBlockY);
               ((FastBlockSearcher)section.getStates().data.storage()).getAllFast(consumer);
               affected |= consumer.affected;
            }

            if (affected && affectedChunks != null) {
               affectedChunks.add(new Vector3i(x, i + chunk.getLevel().getMinBuildHeight() / 16, z));
            }
         }

         List<DynamicRagdoll> ragdolls = this.searchConnections(x, z, vines);
         this.unloadDynamicBlockChunk(x, z);

         for (Ragdoll ragdoll : ragdolls) {
            this.mod.physicsWorld.addRagdoll(ragdoll);
         }

         this.loadedVines.put(chunkIndex, ragdolls);
      }
   }

   @Override
   public void addVineRagdoll(DynamicRagdoll ragdoll, BlockPos pos) {
      long chunkIndex = ChunkHelper.calcChunkIndex(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
      List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)this.loadedVines.get(chunkIndex);
      if (ragdolls == null) {
         ragdolls = new ObjectArrayList();
         this.loadedVines.put(chunkIndex, ragdolls);
      }

      ragdolls.add(ragdoll);
   }

   @Override
   public void removeVineRagdoll(DynamicRagdoll ragdoll) {
      if (ragdoll.getBlockPositions().size() > 0) {
         BlockPos pos = ragdoll.getBlockPositions().get(0);
         long chunkIndex = ChunkHelper.calcChunkIndex(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
         List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)this.loadedVines.get(chunkIndex);
         if (ragdolls != null) {
            ragdolls.remove(ragdoll);
         }
      }
   }

   @Unique
   protected List<DynamicRagdoll> searchConnections(int chunkX, int chunkZ, Long2ObjectMap<BlockState> vines) {
      List<DynamicRagdoll> ragdolls = new ObjectArrayList();

      while (vines.size() > 0) {
         Entry<BlockState> entry = (Entry<BlockState>)vines.long2ObjectEntrySet().iterator().next();
         long index = entry.getLongKey();
         BlockState current = (BlockState)entry.getValue();
         int x = (int)(index >> 60) & 15;
         int y = (int)(index & 72057594037927935L);
         int z = (int)(index >> 56) & 15;
         DynamicSetting setting = VineHelper.getSetting(current);
         if (setting != null) {
            DynamicRagdoll ragdoll = setting.createRagdoll(this.mod, current, new BlockPos(x + chunkX * 16, y, z + chunkZ * 16), vines);
            if (ragdoll != null) {
               ragdolls.add(ragdoll);
            }
         }
      }

      return ragdolls;
   }

   @Override
   public void setPhysicsMod(PhysicsMod physicsMod) {
      if (this.mod != null) {
         if (this.mod != physicsMod) {
            VAO.storePreviouslyBoundState();
            this.unloadAllRagdolls();
            this.unloadAllSnow();
            this.unloadAllOcean();
            this.mod = physicsMod;
            if (physicsMod != null) {
               this.loadAllRagdolls();
               this.loadAllSnow();
               this.loadAllOcean();
            }

            VAO.restorePreviouslyBoundState();
         }
      } else {
         this.mod = physicsMod;
         if (physicsMod != null) {
            VAO.storePreviouslyBoundState();
            this.loadAllRagdolls();
            this.loadAllSnow();
            this.loadAllOcean();
            VAO.restorePreviouslyBoundState();
         }
      }
   }

   @Override
   public Storage getStorage() {
      return this.storage;
   }
}
