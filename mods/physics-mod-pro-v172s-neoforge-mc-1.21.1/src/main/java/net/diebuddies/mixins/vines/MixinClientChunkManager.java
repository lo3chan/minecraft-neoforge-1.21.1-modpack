/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.multiplayer.ClientChunkCache$Storage
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityTagOutput
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.Palette
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3i
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.minecraft.ClientChunkCacheAccessor;
import net.diebuddies.mixins.vines.StorageInvoker;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
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

@Mixin(value={ClientChunkCache.class})
public class MixinClientChunkManager
implements DynamicLoader,
ClientChunkCacheAccessor {
    @Shadow
    @Final
    protected volatile ClientChunkCache.Storage storage;
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
        if (this.mod == null) {
            return;
        }
        LongIterator it = this.loadedChunksSodiumFix.iterator();
        ObjectOpenHashSet affectedChunks = new ObjectOpenHashSet();
        while (it.hasNext()) {
            boolean shouldBeLoaded;
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            boolean isLoaded = this.loadedVines.containsKey(chunkIndex);
            if (isLoaded == (shouldBeLoaded = VineHelper.isChunkInRange(chunkX, chunkZ))) continue;
            if (isLoaded) {
                this.unloadDynamicBlockChunk(chunkX, chunkZ, (ObjectSet<Vector3i>)affectedChunks, true);
            } else {
                this.loadDynamicBlockChunk(((ClientChunkCache)this).getChunk(chunkX, chunkZ, false), chunkX, chunkZ, (ObjectSet<Vector3i>)affectedChunks);
            }
            for (Vector3i affectedChunk : affectedChunks) {
                if (StarterClient.sodium) {
                    Sodium.scheduleChunkRebuild(Minecraft.getInstance().levelRenderer, affectedChunk.x, affectedChunk.y, affectedChunk.z, true);
                    continue;
                }
                Minecraft.getInstance().levelRenderer.setSectionDirty(affectedChunk.x, affectedChunk.y, affectedChunk.z, true);
            }
            affectedChunks.clear();
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"updateViewRadius"})
    public void updateLoadDistance(int loadDistance, CallbackInfo info) {
        int properLoadDistance = Math.max(loadDistance, 2) + 3;
        LongIterator itLoaded = this.loadedChunksSodiumFix.iterator();
        while (itLoaded.hasNext()) {
            int chunkZ;
            long chunkIndex = itLoaded.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            if (this.isInRadius(properLoadDistance, chunkX, chunkZ = ChunkHelper.getChunkZ(chunkIndex))) continue;
            itLoaded.remove();
        }
        if (this.mod != null) {
            ObjectIterator it = this.loadedVines.long2ObjectEntrySet().iterator();
            while (it.hasNext()) {
                int chunkZ;
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
                long chunkIndex = entry.getLongKey();
                List ragdolls = (List)entry.getValue();
                int chunkX = ChunkHelper.getChunkX(chunkIndex);
                if (this.isInRadius(properLoadDistance, chunkX, chunkZ = ChunkHelper.getChunkZ(chunkIndex))) continue;
                this.unloadRagdolls(ragdolls, false);
                it.remove();
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

    @Inject(at={@At(value="HEAD")}, method={"drop"})
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
        List ragdolls = (List)this.loadedVines.remove(chunkIndex);
        if (affectedChunks != null && ragdolls != null) {
            for (DynamicRagdoll ragdoll : ragdolls) {
                for (BlockPos pos : ragdoll.getBlockPositions()) {
                    affectedChunks.add((Object)new Vector3i(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getY()), SectionPos.blockToSectionCoord((int)pos.getZ())));
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
                    continue;
                }
                this.mod.physicsWorld.removeRagdoll(ragdoll);
            }
        }
    }

    @Override
    public void unloadAllRagdolls() {
        for (List ragdolls : this.loadedVines.values()) {
            this.unloadRagdolls(ragdolls, false);
        }
        this.loadedVines.clear();
    }

    @Override
    public void loadAllRagdolls() {
        if (this.mod == null) {
            return;
        }
        LongIterator it = this.loadedChunksSodiumFix.iterator();
        while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)this).getChunk(chunkX, chunkZ, null, false);
            if (!VineHelper.isChunkInRange(chunkX, chunkZ)) continue;
            this.loadDynamicBlockChunk(chunk, chunkX, chunkZ);
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
        if (this.mod == null) {
            return;
        }
        LongIterator it = this.loadedChunksSodiumFix.iterator();
        while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)this).getChunk(chunkX, chunkZ, null, false);
            this.loadSnowChunk(chunk, chunkX, chunkZ);
        }
    }

    @Override
    public void unloadAllOcean() {
        if (this.mod != null) {
            this.mod.getPhysicsWorld().getOceanWorld().removeAll();
            this.mod.getPhysicsWorld().getOceanWorld().destroy();
            this.mod.getPhysicsWorld().setOceanWorld(new OceanWorld(this.mod.getPhysicsWorld(), (Level)this.level));
        }
    }

    @Override
    public void loadAllOcean() {
        if (this.mod == null) {
            return;
        }
        LongIterator it = this.loadedChunksSodiumFix.iterator();
        while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)this).getChunk(chunkX, chunkZ, null, false);
            this.loadOceanChunk(chunk, chunkX, chunkZ);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"replaceWithPacketData"})
    public void replaceWithPacketDataHead(int x, int z, FriendlyByteBuf buf, CompoundTag nbt, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> info) {
        StorageInvoker storageInvoker = (StorageInvoker)this.storage;
        if (!storageInvoker.invokeInRange(x, z)) {
            return;
        }
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

    @Inject(at={@At(value="RETURN")}, method={"replaceWithPacketData"})
    public void replaceWithPacketData(int x, int z, FriendlyByteBuf buf, CompoundTag nbt, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> info) {
        LevelChunk chunk = (LevelChunk)info.getReturnValue();
        if (chunk == null) {
            return;
        }
        long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
        this.loadedChunksSodiumFix.add(chunkIndex);
        if (this.mod != null) {
            this.loadCombinedPhysicsChunk(chunk, x, z);
        }
    }

    @Unique
    protected void loadCombinedPhysicsChunk(LevelChunk chunk, int x, int z) {
        boolean dynamicBlocks;
        boolean snow = ConfigClient.areSnowPhysicsEnabled();
        boolean ocean = ConfigClient.areOceanPhysicsEnabled();
        boolean bl = dynamicBlocks = ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.isChunkInRange(x, z);
        if (chunk != null && (snow || ocean || dynamicBlocks)) {
            long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
            LevelChunkSection[] sections = chunk.getSections();
            SnowWorld snowWorld = this.mod.getPhysicsWorld().getSnowWorld();
            OceanWorld oceanWorld = this.mod.getPhysicsWorld().getOceanWorld();
            ObjectArrayList loadSnowChunks = new ObjectArrayList();
            ObjectArrayList loadOceanChunks = new ObjectArrayList();
            Long2ObjectLinkedOpenHashMap vines = new Long2ObjectLinkedOpenHashMap();
            ObjectArrayList consumers = new ObjectArrayList();
            for (int i = 0; i < sections.length; ++i) {
                LevelChunkSection section = sections[i];
                Palette palette = section.getStates().data.palette();
                Object2ObjectOpenHashMap snowBlocks = new Object2ObjectOpenHashMap();
                int y = i + chunk.getLevel().getMinBuildHeight() / 16;
                if (!section.hasOnlyAir()) {
                    if (snow && palette.maybeHas(SnowSearcher::isPhysicsSnow)) {
                        consumers.add(new SnowSearcher(snowWorld, (Map<Vector3i, BlockState>)snowBlocks, x * 16, i * 16 + chunk.getLevel().getMinBuildHeight(), z * 16, (Palette<BlockState>)palette));
                    }
                    if (ocean) {
                        loadOceanChunks.add(new OceanChunkCreator((PalettedContainer<BlockState>)section.getStates().copy(), x, y, z));
                    }
                    if (dynamicBlocks && palette.maybeHas(VineSearcher::isPhysicsDynamicBlock)) {
                        int sectionY = chunk.getSectionYFromSectionIndex(i);
                        int bottomBlockY = SectionPos.sectionToBlockCoord((int)sectionY);
                        consumers.add(new VineSearcher((Long2ObjectMap<BlockState>)vines, (Palette<BlockState>)palette, bottomBlockY));
                    }
                    if (!consumers.isEmpty()) {
                        ((FastBlockSearcher)section.getStates().data.storage()).getAllFast(new MultiSearcherConsumer((Palette<BlockState>)palette, (List<FastBlockSearcherConsumer>)consumers));
                    }
                    consumers.clear();
                } else if (ocean) {
                    loadOceanChunks.add(new OceanChunkCreator(0, x, y, z));
                }
                if (!snow) continue;
                SnowChunkCreator creator = new SnowChunkCreator(snowWorld, (Map<Vector3i, BlockState>)snowBlocks, x, y, z);
                loadSnowChunks.add(creator);
            }
            if (!loadSnowChunks.isEmpty()) {
                snowWorld.addChunkColumn((List<ChunkCreator>)loadSnowChunks, x, z);
            }
            if (!loadOceanChunks.isEmpty()) {
                oceanWorld.addChunkColumn((List<OceanChunkCreator>)loadOceanChunks, x, z);
            }
            if (dynamicBlocks) {
                List<DynamicRagdoll> ragdolls = this.searchConnections(x, z, (Long2ObjectMap<BlockState>)vines);
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
        }
        ChunkPos chunkPos = levelChunk.getPos();
        return chunkPos.x == x && chunkPos.z == z;
    }

    @Unique
    protected void loadSnowChunk(LevelChunk chunk, int x, int z) {
        if (ConfigClient.areSnowPhysicsEnabled() && chunk != null) {
            LevelChunkSection[] sections = chunk.getSections();
            SnowWorld snowWorld = this.mod.getPhysicsWorld().getSnowWorld();
            ObjectArrayList loadChunks = new ObjectArrayList();
            for (int i = 0; i < sections.length; ++i) {
                LevelChunkSection section = sections[i];
                Object2ObjectOpenHashMap snow = new Object2ObjectOpenHashMap();
                Palette palette = section.getStates().data.palette();
                if (!section.hasOnlyAir() && palette.maybeHas(SnowSearcher::isPhysicsSnow)) {
                    SnowSearcher consumer = new SnowSearcher(snowWorld, (Map<Vector3i, BlockState>)snow, x * 16, i * 16 + chunk.getLevel().getMinBuildHeight(), z * 16, (Palette<BlockState>)palette);
                    ((FastBlockSearcher)section.getStates().data.storage()).getAllFast(consumer);
                }
                int y = i + chunk.getLevel().getMinBuildHeight() / 16;
                SnowChunkCreator creator = new SnowChunkCreator(snowWorld, (Map<Vector3i, BlockState>)snow, x, y, z);
                loadChunks.add(creator);
            }
            if (!loadChunks.isEmpty()) {
                snowWorld.addChunkColumn((List<ChunkCreator>)loadChunks, x, z);
            }
        }
    }

    @Unique
    protected void loadOceanChunk(LevelChunk chunk, int x, int z) {
        if (ConfigClient.areOceanPhysicsEnabled() && chunk != null) {
            LevelChunkSection[] sections = chunk.getSections();
            OceanWorld oceanWorld = this.mod.getPhysicsWorld().getOceanWorld();
            ObjectArrayList loadChunks = new ObjectArrayList();
            for (int i = 0; i < sections.length; ++i) {
                LevelChunkSection section = sections[i];
                int y = i + chunk.getLevel().getMinBuildHeight() / 16;
                if (!section.hasOnlyAir()) {
                    loadChunks.add(new OceanChunkCreator((PalettedContainer<BlockState>)section.getStates().copy(), x, y, z));
                    continue;
                }
                loadChunks.add(new OceanChunkCreator(0, x, y, z));
            }
            if (!loadChunks.isEmpty()) {
                oceanWorld.addChunkColumn((List<OceanChunkCreator>)loadChunks, x, z);
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
            Long2ObjectLinkedOpenHashMap vines = new Long2ObjectLinkedOpenHashMap();
            for (int i = 0; i < sections.length; ++i) {
                LevelChunkSection section = sections[i];
                boolean bl = false;
                Palette palette = section.getStates().data.palette();
                if (!section.hasOnlyAir() && palette.maybeHas(VineSearcher::isPhysicsDynamicBlock)) {
                    int sectionY = chunk.getSectionYFromSectionIndex(i);
                    int bottomBlockY = SectionPos.sectionToBlockCoord((int)sectionY);
                    VineSearcher consumer = new VineSearcher((Long2ObjectMap<BlockState>)vines, (Palette<BlockState>)palette, bottomBlockY);
                    ((FastBlockSearcher)section.getStates().data.storage()).getAllFast(consumer);
                    bl |= consumer.affected;
                }
                if (!bl || affectedChunks == null) continue;
                affectedChunks.add((Object)new Vector3i(x, i + chunk.getLevel().getMinBuildHeight() / 16, z));
            }
            List<DynamicRagdoll> ragdolls = this.searchConnections(x, z, (Long2ObjectMap<BlockState>)vines);
            this.unloadDynamicBlockChunk(x, z);
            for (Ragdoll ragdoll : ragdolls) {
                this.mod.physicsWorld.addRagdoll(ragdoll);
            }
            this.loadedVines.put(chunkIndex, ragdolls);
        }
    }

    @Override
    public void addVineRagdoll(DynamicRagdoll ragdoll, BlockPos pos) {
        long chunkIndex = ChunkHelper.calcChunkIndex(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getZ()));
        List ragdolls = (List)this.loadedVines.get(chunkIndex);
        if (ragdolls == null) {
            ragdolls = new ObjectArrayList();
            this.loadedVines.put(chunkIndex, (Object)ragdolls);
        }
        ragdolls.add(ragdoll);
    }

    @Override
    public void removeVineRagdoll(DynamicRagdoll ragdoll) {
        BlockPos pos;
        long chunkIndex;
        List ragdolls;
        if (ragdoll.getBlockPositions().size() > 0 && (ragdolls = (List)this.loadedVines.get(chunkIndex = ChunkHelper.calcChunkIndex(SectionPos.blockToSectionCoord((int)(pos = ragdoll.getBlockPositions().get(0)).getX()), SectionPos.blockToSectionCoord((int)pos.getZ())))) != null) {
            ragdolls.remove(ragdoll);
        }
    }

    @Unique
    protected List<DynamicRagdoll> searchConnections(int chunkX, int chunkZ, Long2ObjectMap<BlockState> vines) {
        ObjectArrayList ragdolls = new ObjectArrayList();
        while (vines.size() > 0) {
            DynamicRagdoll ragdoll;
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)vines.long2ObjectEntrySet().iterator().next();
            long index = entry.getLongKey();
            BlockState current = (BlockState)entry.getValue();
            int x = (int)(index >> 60) & 0xF;
            int y = (int)(index & 0xFFFFFFFFFFFFFFL);
            int z = (int)(index >> 56) & 0xF;
            DynamicSetting setting = VineHelper.getSetting(current);
            if (setting == null || (ragdoll = setting.createRagdoll(this.mod, current, new BlockPos(x + chunkX * 16, y, z + chunkZ * 16), vines)) == null) continue;
            ragdolls.add(ragdoll);
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
    public ClientChunkCache.Storage getStorage() {
        return this.storage;
    }
}

