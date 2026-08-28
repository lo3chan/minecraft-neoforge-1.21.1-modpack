/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.ArenaBuffer;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.opengl.VertexFormat;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.ChunkEntity;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.Index;
import net.diebuddies.physics.snow.SnowBatch;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.snow.WorldContouring;
import net.diebuddies.physics.snow.math.SDF;
import net.diebuddies.physics.snow.math.SDFBoxRound;
import net.diebuddies.physics.snow.thread.ChunkCreator;
import net.diebuddies.physics.snow.thread.SnowChunkCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL32C;

public class SnowWorld {
    private static final Vector3d Y_AXIS = new Vector3d(0.0, 1.0, 0.0);
    public static final int SNOW_TRACK_UPDATES_PER_SECOND = 10;
    public Level level;
    public Long2ObjectMap<ChunkEntity> chunks;
    public ConcurrentLinkedQueue<Runnable> queue;
    public WorldContouring contouring;
    public Long2ObjectMap<ShortSet> fetchLightUpdates;
    private Long2ObjectMap<LongList> loadedColumns;
    public double trackTime;
    public Matrix4f viewProjection;
    public int trackUpdateCount;
    public Set<BlockUpdate> blockUpdates;
    private ArenaBuffer snowVertexData;
    private ArenaBuffer snowIndexData;
    public VertexFormat format;
    public int snowVAO = -1;
    public Matrix4d tmp = new Matrix4d();
    public SnowBatch snowBatch = new SnowBatch();
    private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public SnowWorld(Level level) {
        this.level = level;
        this.blockUpdates = new ObjectOpenHashSet();
        this.viewProjection = new Matrix4f();
        this.chunks = new Long2ObjectOpenHashMap();
        this.queue = new ConcurrentLinkedQueue();
        this.contouring = new WorldContouring(this, level.getMinSection(), level.getMaxSection());
        this.fetchLightUpdates = new Long2ObjectOpenHashMap();
        this.loadedColumns = new Long2ObjectOpenHashMap();
        this.contouring.start();
    }

    public ArenaBuffer getSnowVertexData() {
        if (this.snowVertexData == null) {
            this.createGLObjects();
        }
        return this.snowVertexData;
    }

    public ArenaBuffer getSnowIndexData() {
        return this.snowIndexData;
    }

    public int getGPUMemoryUsage() {
        if (this.snowVertexData == null) {
            return 0;
        }
        return this.snowVertexData.getTotalSize();
    }

    private void createGLObjects() {
        this.snowVAO = GL32C.glGenVertexArrays();
        this.format = StarterClient.iris ? new VertexFormat(Data.POSITION, Data.NORMAL, Data.LIGHT, Data.TANGENT_SHADER) : (StarterClient.optifabric ? new VertexFormat(Data.POSITION, Data.NORMAL, Data.LIGHT, Data.TANGENT_OPTIFINE) : new VertexFormat(Data.POSITION, Data.NORMAL, Data.LIGHT));
        this.snowVertexData = new ArenaBuffer(0x100000 * this.format.getStride());
        if (ConfigClient.snowSmoothShading) {
            this.snowIndexData = new ArenaBuffer(0x400000, 34963);
        }
        StateTracker.bindVertexArray(this.snowVAO);
    }

    public void bindForRendering() {
        if (this.snowVAO == -1) {
            return;
        }
        StateTracker.bindVertexArray(this.snowVAO);
        this.snowVertexData.bind();
        this.format.bindAttributeFormat();
        if (this.snowIndexData != null) {
            this.snowIndexData.bind();
        }
    }

    public void update(double diff) {
        if (this.snowVAO != -1) {
            StateTracker.bindVertexArray(this.snowVAO);
        }
        Runnable event = null;
        while ((event = this.queue.poll()) != null) {
            event.run();
        }
        if (!ConfigClient.areSnowPhysicsEnabled()) {
            this.loadedColumns.clear();
            this.fetchLightUpdates.clear();
            return;
        }
        this.snowBatch.update();
        ObjectArrayList events = new ObjectArrayList();
        this.applyBlockUpdates((List<Runnable>)events);
        this.applyLightUpdates((List<Runnable>)events);
        if (ConfigClient.snowTracks && diff > 0.0) {
            this.applyEntitySnowTracks(diff, (List<Runnable>)events);
        }
        if (!events.isEmpty()) {
            this.contouring.queueEvent(() -> SnowWorld.lambda$update$0((List)events));
        }
        this.contouring.uploadInformation();
    }

    private void applyBlockUpdates(List<Runnable> events) {
        if (!this.blockUpdates.isEmpty()) {
            ObjectArrayList updates = new ObjectArrayList(this.blockUpdates);
            for (BlockUpdate update : updates) {
                BlockPos pos = update.pos;
                int cx = SectionPos.blockToSectionCoord((int)pos.getX());
                int cy = SectionPos.blockToSectionCoord((int)pos.getY());
                int cz = SectionPos.blockToSectionCoord((int)pos.getZ());
                SnowSearcher.queueLightUpdates(this, this.getLightUpdates(SectionPos.asLong((int)cx, (int)cy, (int)cz)), pos.getX(), pos.getY(), pos.getZ());
            }
            events.add(() -> this.lambda$applyBlockUpdates$1((List)updates));
            this.blockUpdates.clear();
        }
    }

    private void applyLightUpdates(List<Runnable> events) {
        if (!this.fetchLightUpdates.isEmpty()) {
            ObjectIterator it = this.fetchLightUpdates.long2ObjectEntrySet().iterator();
            LevelLightEngine levelLightEngine = this.level.getLightEngine();
            while (it.hasNext()) {
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
                long chunkIndex = entry.getLongKey();
                int x = SectionPos.x((long)chunkIndex);
                int y = SectionPos.y((long)chunkIndex);
                int z = SectionPos.z((long)chunkIndex);
                ShortSet positions = (ShortSet)entry.getValue();
                if (positions.isEmpty() || !this.areSurroundingsLoaded(this.level, x, y, z)) {
                    it.remove();
                    continue;
                }
                SectionPos sectionPos = SectionPos.of((int)x, (int)y, (int)z);
                if (!levelLightEngine.lightOnInSection(sectionPos)) continue;
                ShortIterator blockIt = positions.iterator();
                ObjectArrayList updates = new ObjectArrayList();
                while (blockIt.hasNext()) {
                    short localPos = blockIt.nextShort();
                    byte lx = (byte)(localPos >> 8 & 0xF);
                    byte ly = (byte)(localPos >> 4 & 0xF);
                    byte lz = (byte)(localPos & 0xF);
                    int bx = x * 16 + lx;
                    int by = y * 16 + ly;
                    int bz = z * 16 + lz;
                    if (by < this.level.getMinBuildHeight() || by >= this.level.getMaxBuildHeight()) {
                        blockIt.remove();
                        continue;
                    }
                    this.pos.set(bx, by, bz);
                    int sky = Math.min(this.level.getBrightness(LightLayer.SKY, (BlockPos)this.pos), 15);
                    int block = Math.min(this.level.getBrightness(LightLayer.BLOCK, (BlockPos)this.pos), 15);
                    LightUpdate update = new LightUpdate(this);
                    update.posX = lx;
                    update.posY = ly;
                    update.posZ = lz;
                    update.lightData = (byte)(sky << 4 | block);
                    updates.add(update);
                    blockIt.remove();
                }
                if (positions.isEmpty()) {
                    it.remove();
                }
                if (updates.isEmpty()) continue;
                events.add(() -> this.lambda$applyLightUpdates$2(x, y, z, (List)updates));
            }
        }
    }

    private boolean areSurroundingsLoaded(Level level, int x, int y, int z) {
        for (int xo = -1; xo <= 1; ++xo) {
            for (int zo = -1; zo <= 1; ++zo) {
                if (!level.getChunkSource().hasChunk(x + xo, z + zo)) continue;
                return true;
            }
        }
        return false;
    }

    private void applyEntitySnowTracks(double diff, List<Runnable> events) {
        this.trackTime += diff;
        if (!(this.trackTime >= 0.1)) {
            return;
        }
        this.trackTime = 0.0;
        ClientLevel clientLevel = (ClientLevel)this.level;
        ObjectArrayList sdfs = new ObjectArrayList();
        double maxTrackDistanceSquared = ConfigClient.snowTrackDistance * ConfigClient.snowTrackDistance * 3.0;
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (Entity entity : clientLevel.entitiesForRendering()) {
            LivingEntity living;
            if (!(entity instanceof LivingEntity) || (living = (LivingEntity)entity).distanceToSqr(cameraPos) > maxTrackDistanceSquared) continue;
            AABB boundingBox = living.getBoundingBox();
            try {
                if (boundingBox == null || boundingBox.hasNaN() || living.isSpectator()) {
                    continue;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            double halfWidth = (boundingBox.maxX - boundingBox.minX) * 0.5 * (double)IChunk.CHUNK_MULTIPLE;
            double halfHeight = (boundingBox.maxY - boundingBox.minY) * 0.5 * (double)IChunk.CHUNK_MULTIPLE;
            double halfDepth = (boundingBox.maxZ - boundingBox.minZ) * 0.5 * (double)IChunk.CHUNK_MULTIPLE;
            double centerX = ((boundingBox.maxX + boundingBox.minX) * 0.5 - 0.5 / (double)IChunk.CHUNK_MULTIPLE) * (double)IChunk.CHUNK_MULTIPLE;
            double centerY = ((boundingBox.maxY + boundingBox.minY) * 0.5 - 0.5 / (double)IChunk.CHUNK_MULTIPLE) * (double)IChunk.CHUNK_MULTIPLE;
            double centerZ = ((boundingBox.maxZ + boundingBox.minZ) * 0.5 - 0.5 / (double)IChunk.CHUNK_MULTIPLE) * (double)IChunk.CHUNK_MULTIPLE;
            SDFBoxRound sdf = new SDFBoxRound(halfWidth, halfHeight, halfDepth, 0.1 * (double)IChunk.CHUNK_MULTIPLE);
            sdf.setTransformation(this.tmp.identity().translate(centerX, centerY, centerZ).rotate((double)living.yBodyRot, (Vector3dc)Y_AXIS));
            if (living instanceof Player) {
                sdf.setPriority(true);
            }
            sdfs.add(sdf);
        }
        events.add(() -> this.lambda$applyEntitySnowTracks$3((List)sdfs));
    }

    public Vec3 getCameraTranslation() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    public Matrix4f getCameraViewProjectionMatrix() {
        return this.viewProjection;
    }

    public void addChunkEntity(ChunkEntity entity, int chunkX, int chunkY, int chunkZ) {
        this.chunks.put(Index.chunk(chunkX, chunkY, chunkZ), (Object)entity);
        this.snowBatch.add(entity);
    }

    public void removeChunkEntity(int chunkX, int chunkY, int chunkZ) {
        ChunkEntity entity = (ChunkEntity)this.chunks.remove(Index.chunk(chunkX, chunkY, chunkZ));
        if (entity != null) {
            this.snowBatch.remove(entity);
            entity.vertexSegment.free();
            if (entity.indexSegment != null) {
                entity.indexSegment.free();
            }
        }
    }

    public void queueEvent(Runnable runnable) {
        this.queue.add(runnable);
    }

    public void addChunkColumn(List<ChunkCreator> asyncChunkCreation, int chunkX, int chunkZ) {
        long index = Index.chunk(chunkX, 0, chunkZ);
        LongList columns = (LongList)this.loadedColumns.get(index);
        if (columns == null) {
            columns = new LongArrayList();
            this.loadedColumns.put(index, (Object)columns);
        }
        for (int i = 0; i < asyncChunkCreation.size(); ++i) {
            columns.add(Index.chunk(chunkX, asyncChunkCreation.get(i).getY(), chunkZ));
        }
        this.contouring.queueEvent(() -> {
            for (int i = 0; i < asyncChunkCreation.size(); ++i) {
                this.contouring.addChunk(((ChunkCreator)asyncChunkCreation.get(i)).create());
            }
        });
    }

    public void removeChunkColumn(int chunkX, int chunkZ) {
        long index = Index.chunk(chunkX, 0, chunkZ);
        LongList columns = (LongList)this.loadedColumns.remove(index);
        if (columns != null) {
            this.contouring.queueEvent(() -> {
                for (int i = 0; i < columns.size(); ++i) {
                    long currentIndex = columns.getLong(i);
                    this.contouring.removeChunk(currentIndex);
                }
            });
        }
    }

    public void removeAll() {
        LongArrayList toRemove = new LongArrayList();
        for (LongList column : this.loadedColumns.values()) {
            for (int i = 0; i < column.size(); ++i) {
                toRemove.add(column.getLong(i));
            }
        }
        if (toRemove.size() > 0) {
            this.contouring.queueEvent(() -> this.lambda$removeAll$6((LongList)toRemove));
        }
    }

    public Collection<ChunkEntity> getChunks() {
        return this.chunks.values();
    }

    public void destroy() {
        this.contouring.shutdown();
        this.contouring.join();
        Runnable event = null;
        while ((event = this.queue.poll()) != null) {
            event.run();
        }
        if (this.snowVertexData != null) {
            this.snowVertexData.destroy();
        }
        if (this.snowIndexData != null) {
            this.snowIndexData.destroy();
        }
        if (this.snowVAO != -1) {
            GL32C.glDeleteVertexArrays((int)this.snowVAO);
        }
        this.chunks.clear();
    }

    public ShortSet getLightUpdates(long chunkIndex) {
        ShortSet lightUpdates = (ShortSet)this.fetchLightUpdates.get(chunkIndex);
        if (lightUpdates == null) {
            lightUpdates = new ShortOpenHashSet();
            this.fetchLightUpdates.put(chunkIndex, (Object)lightUpdates);
        }
        return lightUpdates;
    }

    public SnowBatch getSnowBatch() {
        return this.snowBatch;
    }

    public Long2ObjectMap<LongList> getLoadedColumns() {
        return this.loadedColumns;
    }

    private /* synthetic */ void lambda$removeAll$6(LongList toRemove) {
        for (int i = 0; i < toRemove.size(); ++i) {
            long currentIndex = toRemove.getLong(i);
            this.contouring.removeChunk(currentIndex);
        }
    }

    private /* synthetic */ void lambda$applyEntitySnowTracks$3(List sdfs) {
        Collections.sort(sdfs, new Comparator<SDF>(){

            @Override
            public int compare(SDF o1, SDF o2) {
                return Double.compare(Vector3d.distanceSquared((double)o1.getX(), (double)o1.getY(), (double)o1.getZ(), (double)SnowWorld.this.contouring.getPlayerPosition().x, (double)SnowWorld.this.contouring.getPlayerPosition().y, (double)SnowWorld.this.contouring.getPlayerPosition().z), Vector3d.distanceSquared((double)o2.getX(), (double)o2.getY(), (double)o2.getZ(), (double)SnowWorld.this.contouring.getPlayerPosition().x, (double)SnowWorld.this.contouring.getPlayerPosition().y, (double)SnowWorld.this.contouring.getPlayerPosition().z));
            }
        });
        for (int i = 0; i < sdfs.size() && i < ConfigClient.snowTrackEntities; ++i) {
            this.contouring.changeDensity((SDF)sdfs.get(i), (byte)127, (byte)2);
        }
    }

    private /* synthetic */ void lambda$applyLightUpdates$2(int x, int y, int z, List updates) {
        ChunkContouring chunk = (ChunkContouring)this.contouring.getChunk(x, y, z);
        if (chunk != null) {
            boolean xupdate = false;
            boolean yupdate = false;
            boolean zupdate = false;
            for (int i = 0; i < updates.size(); ++i) {
                LightUpdate update = (LightUpdate)updates.get(i);
                xupdate |= update.posX == 0;
                yupdate |= update.posY == 0;
                zupdate |= update.posZ == 0;
                chunk.setLightDataFast(update.posX << IChunk.CHUNK_MULTIPLE_BITS, update.posY << IChunk.CHUNK_MULTIPLE_BITS, update.posZ << IChunk.CHUNK_MULTIPLE_BITS, update.lightData);
            }
            chunk.setLightsUpdated(false);
            ChunkContouring neighbour = null;
            if (xupdate) {
                neighbour = (ChunkContouring)chunk.getNeighbourChunk(-1, 0, 0);
                if (neighbour != null) {
                    neighbour.setLightsUpdated(false);
                }
                if (yupdate) {
                    neighbour = (ChunkContouring)chunk.getNeighbourChunk(-1, -1, 0);
                    if (neighbour != null) {
                        neighbour.setLightsUpdated(false);
                    }
                    if (zupdate && (neighbour = (ChunkContouring)chunk.getNeighbourChunk(-1, -1, -1)) != null) {
                        neighbour.setLightsUpdated(false);
                    }
                } else if (zupdate && (neighbour = (ChunkContouring)chunk.getNeighbourChunk(-1, 0, -1)) != null) {
                    neighbour.setLightsUpdated(false);
                }
            }
            if (yupdate) {
                neighbour = (ChunkContouring)chunk.getNeighbourChunk(0, -1, 0);
                if (neighbour != null) {
                    neighbour.setLightsUpdated(false);
                }
                if (zupdate && (neighbour = (ChunkContouring)chunk.getNeighbourChunk(0, -1, -1)) != null) {
                    neighbour.setLightsUpdated(false);
                }
            }
            if (zupdate && (neighbour = (ChunkContouring)chunk.getNeighbourChunk(0, 0, -1)) != null) {
                neighbour.setLightsUpdated(false);
            }
        }
    }

    private /* synthetic */ void lambda$applyBlockUpdates$1(List updates) {
        for (BlockUpdate update : updates) {
            int rz;
            int ry;
            BlockPos pos = update.pos;
            BlockState state = update.state;
            int rx = pos.getX() * IChunk.CHUNK_MULTIPLE;
            ChunkContouring chunk = (ChunkContouring)this.contouring.getChunkWorldPos(rx, ry = pos.getY() * IChunk.CHUNK_MULTIPLE, rz = pos.getZ() * IChunk.CHUNK_MULTIPLE);
            if (chunk == null) continue;
            SnowChunkCreator.updateBlock(this.contouring, chunk, rx & IChunk.CHUNK_SIZE_BITS, ry & IChunk.CHUNK_SIZE_BITS, rz & IChunk.CHUNK_SIZE_BITS, state);
        }
    }

    private static /* synthetic */ void lambda$update$0(List events) {
        for (Runnable task : events) {
            task.run();
        }
    }

    class LightUpdate {
        byte posX;
        byte posY;
        byte posZ;
        byte lightData;

        LightUpdate(SnowWorld this$0) {
        }
    }
}

