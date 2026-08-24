package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import net.diebuddies.physics.snow.math.SDF;
import net.diebuddies.physics.snow.math.SDFBoxRound;
import net.diebuddies.physics.snow.thread.ChunkCreator;
import net.diebuddies.physics.snow.thread.SnowChunkCreator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
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
   private MutableBlockPos pos = new MutableBlockPos();

   public SnowWorld(Level level) {
      this.level = level;
      this.blockUpdates = new ObjectOpenHashSet();
      this.viewProjection = new Matrix4f();
      this.chunks = new Long2ObjectOpenHashMap();
      this.queue = new ConcurrentLinkedQueue<>();
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
      return this.snowVertexData == null ? 0 : this.snowVertexData.getTotalSize();
   }

   private void createGLObjects() {
      this.snowVAO = GL32C.glGenVertexArrays();
      if (StarterClient.iris) {
         this.format = new VertexFormat(Data.POSITION, Data.NORMAL, Data.LIGHT, Data.TANGENT_SHADER);
      } else if (StarterClient.optifabric) {
         this.format = new VertexFormat(Data.POSITION, Data.NORMAL, Data.LIGHT, Data.TANGENT_OPTIFINE);
      } else {
         this.format = new VertexFormat(Data.POSITION, Data.NORMAL, Data.LIGHT);
      }

      this.snowVertexData = new ArenaBuffer(1048576 * this.format.getStride());
      if (ConfigClient.snowSmoothShading) {
         this.snowIndexData = new ArenaBuffer(4194304, 34963);
      }

      StateTracker.bindVertexArray(this.snowVAO);
   }

   public void bindForRendering() {
      if (this.snowVAO != -1) {
         StateTracker.bindVertexArray(this.snowVAO);
         this.snowVertexData.bind();
         this.format.bindAttributeFormat();
         if (this.snowIndexData != null) {
            this.snowIndexData.bind();
         }
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
      } else {
         this.snowBatch.update();
         List<Runnable> events = new ObjectArrayList();
         this.applyBlockUpdates(events);
         this.applyLightUpdates(events);
         if (ConfigClient.snowTracks && diff > 0.0) {
            this.applyEntitySnowTracks(diff, events);
         }

         if (!events.isEmpty()) {
            this.contouring.queueEvent(() -> {
               for (Runnable task : events) {
                  task.run();
               }
            });
         }

         this.contouring.uploadInformation();
      }
   }

   private void applyBlockUpdates(List<Runnable> events) {
      if (!this.blockUpdates.isEmpty()) {
         List<BlockUpdate> updates = new ObjectArrayList(this.blockUpdates);

         for (BlockUpdate update : updates) {
            BlockPos pos = update.pos;
            int cx = SectionPos.blockToSectionCoord(pos.getX());
            int cy = SectionPos.blockToSectionCoord(pos.getY());
            int cz = SectionPos.blockToSectionCoord(pos.getZ());
            SnowSearcher.queueLightUpdates(this, this.getLightUpdates(SectionPos.asLong(cx, cy, cz)), pos.getX(), pos.getY(), pos.getZ());
         }

         events.add(
            () -> {
               for (BlockUpdate updatex : updates) {
                  BlockPos posx = updatex.pos;
                  BlockState state = updatex.state;
                  int rx = posx.getX() * IChunk.CHUNK_MULTIPLE;
                  int ry = posx.getY() * IChunk.CHUNK_MULTIPLE;
                  int rz = posx.getZ() * IChunk.CHUNK_MULTIPLE;
                  ChunkContouring chunk = this.contouring.getChunkWorldPos(rx, ry, rz);
                  if (chunk != null) {
                     SnowChunkCreator.updateBlock(
                        this.contouring, chunk, rx & IChunk.CHUNK_SIZE_BITS, ry & IChunk.CHUNK_SIZE_BITS, rz & IChunk.CHUNK_SIZE_BITS, state
                     );
                  }
               }
            }
         );
         this.blockUpdates.clear();
      }
   }

   private void applyLightUpdates(List<Runnable> events) {
      if (!this.fetchLightUpdates.isEmpty()) {
         Iterator<Entry<ShortSet>> it = this.fetchLightUpdates.long2ObjectEntrySet().iterator();
         LevelLightEngine levelLightEngine = this.level.getLightEngine();

         while (it.hasNext()) {
            Entry<ShortSet> entry = it.next();
            long chunkIndex = entry.getLongKey();
            int x = SectionPos.x(chunkIndex);
            int y = SectionPos.y(chunkIndex);
            int z = SectionPos.z(chunkIndex);
            ShortSet positions = (ShortSet)entry.getValue();
            if (!positions.isEmpty() && this.areSurroundingsLoaded(this.level, x, y, z)) {
               SectionPos sectionPos = SectionPos.of(x, y, z);
               if (levelLightEngine.lightOnInSection(sectionPos)) {
                  ShortIterator blockIt = positions.iterator();
                  List<SnowWorld.LightUpdate> updates = new ObjectArrayList();

                  while (blockIt.hasNext()) {
                     short localPos = blockIt.nextShort();
                     byte lx = (byte)(localPos >> 8 & 15);
                     byte ly = (byte)(localPos >> 4 & 15);
                     byte lz = (byte)(localPos & 15);
                     int bx = x * 16 + lx;
                     int by = y * 16 + ly;
                     int bz = z * 16 + lz;
                     if (by >= this.level.getMinBuildHeight() && by < this.level.getMaxBuildHeight()) {
                        this.pos.set(bx, by, bz);
                        int sky = Math.min(this.level.getBrightness(LightLayer.SKY, this.pos), 15);
                        int block = Math.min(this.level.getBrightness(LightLayer.BLOCK, this.pos), 15);
                        SnowWorld.LightUpdate update = new SnowWorld.LightUpdate();
                        update.posX = lx;
                        update.posY = ly;
                        update.posZ = lz;
                        update.lightData = (byte)(sky << 4 | block);
                        updates.add(update);
                        blockIt.remove();
                     } else {
                        blockIt.remove();
                     }
                  }

                  if (positions.isEmpty()) {
                     it.remove();
                  }

                  if (!updates.isEmpty()) {
                     events.add(
                        () -> {
                           ChunkContouring chunk = this.contouring.getChunk(x, y, z);
                           if (chunk != null) {
                              boolean xupdate = false;
                              boolean yupdate = false;
                              boolean zupdate = false;

                              for (int i = 0; i < updates.size(); i++) {
                                 SnowWorld.LightUpdate updatex = updates.get(i);
                                 xupdate |= updatex.posX == 0;
                                 yupdate |= updatex.posY == 0;
                                 zupdate |= updatex.posZ == 0;
                                 chunk.setLightDataFast(
                                    updatex.posX << IChunk.CHUNK_MULTIPLE_BITS,
                                    updatex.posY << IChunk.CHUNK_MULTIPLE_BITS,
                                    updatex.posZ << IChunk.CHUNK_MULTIPLE_BITS,
                                    updatex.lightData
                                 );
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

                                    if (zupdate) {
                                       neighbour = (ChunkContouring)chunk.getNeighbourChunk(-1, -1, -1);
                                       if (neighbour != null) {
                                          neighbour.setLightsUpdated(false);
                                       }
                                    }
                                 } else if (zupdate) {
                                    neighbour = (ChunkContouring)chunk.getNeighbourChunk(-1, 0, -1);
                                    if (neighbour != null) {
                                       neighbour.setLightsUpdated(false);
                                    }
                                 }
                              }

                              if (yupdate) {
                                 neighbour = (ChunkContouring)chunk.getNeighbourChunk(0, -1, 0);
                                 if (neighbour != null) {
                                    neighbour.setLightsUpdated(false);
                                 }

                                 if (zupdate) {
                                    neighbour = (ChunkContouring)chunk.getNeighbourChunk(0, -1, -1);
                                    if (neighbour != null) {
                                       neighbour.setLightsUpdated(false);
                                    }
                                 }
                              }

                              if (zupdate) {
                                 neighbour = (ChunkContouring)chunk.getNeighbourChunk(0, 0, -1);
                                 if (neighbour != null) {
                                    neighbour.setLightsUpdated(false);
                                 }
                              }
                           }
                        }
                     );
                  }
               }
            } else {
               it.remove();
            }
         }
      }
   }

   private boolean areSurroundingsLoaded(Level level, int x, int y, int z) {
      for (int xo = -1; xo <= 1; xo++) {
         for (int zo = -1; zo <= 1; zo++) {
            if (level.getChunkSource().hasChunk(x + xo, z + zo)) {
               return true;
            }
         }
      }

      return false;
   }

   private void applyEntitySnowTracks(double diff, List<Runnable> events) {
      this.trackTime += diff;
      if (this.trackTime >= 0.1) {
         this.trackTime = 0.0;
         ClientLevel clientLevel = (ClientLevel)this.level;
         ObjectArrayList sdfs = new ObjectArrayList();
         double maxTrackDistanceSquared = ConfigClient.snowTrackDistance * ConfigClient.snowTrackDistance * 3.0;
         Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
         Iterator var9 = clientLevel.entitiesForRendering().iterator();

         while (true) {
            AABB boundingBox;
            while (true) {
               if (!var9.hasNext()) {
                  events.add(
                     () -> {
                        Collections.sort(
                           sdfs,
                           new Comparator<SDF>() {
                              public int compare(SDF o1, SDF o2) {
                                 return Double.compare(
                                    Vector3d.distanceSquared(
                                       o1.getX(),
                                       o1.getY(),
                                       o1.getZ(),
                                       SnowWorld.this.contouring.getPlayerPosition().x,
                                       SnowWorld.this.contouring.getPlayerPosition().y,
                                       SnowWorld.this.contouring.getPlayerPosition().z
                                    ),
                                    Vector3d.distanceSquared(
                                       o2.getX(),
                                       o2.getY(),
                                       o2.getZ(),
                                       SnowWorld.this.contouring.getPlayerPosition().x,
                                       SnowWorld.this.contouring.getPlayerPosition().y,
                                       SnowWorld.this.contouring.getPlayerPosition().z
                                    )
                                 );
                              }
                           }
                        );

                        for (int i = 0; i < sdfs.size() && i < ConfigClient.snowTrackEntities; i++) {
                           this.contouring.changeDensity((SDF)sdfs.get(i), (byte)127, (byte)2);
                        }
                     }
                  );
                  return;
               }

               Entity entity = (Entity)var9.next();
               if (entity instanceof LivingEntity living && !(living.distanceToSqr(cameraPos) > maxTrackDistanceSquared)) {
                  boundingBox = living.getBoundingBox();

                  try {
                     if (boundingBox != null && !boundingBox.hasNaN() && !living.isSpectator()) {
                        break;
                     }
                  } catch (Exception var26) {
                     break;
                  }
               }
            }

            double halfWidth = (boundingBox.maxX - boundingBox.minX) * 0.5 * IChunk.CHUNK_MULTIPLE;
            double halfHeight = (boundingBox.maxY - boundingBox.minY) * 0.5 * IChunk.CHUNK_MULTIPLE;
            double halfDepth = (boundingBox.maxZ - boundingBox.minZ) * 0.5 * IChunk.CHUNK_MULTIPLE;
            double centerX = ((boundingBox.maxX + boundingBox.minX) * 0.5 - 0.5 / IChunk.CHUNK_MULTIPLE) * IChunk.CHUNK_MULTIPLE;
            double centerY = ((boundingBox.maxY + boundingBox.minY) * 0.5 - 0.5 / IChunk.CHUNK_MULTIPLE) * IChunk.CHUNK_MULTIPLE;
            double centerZ = ((boundingBox.maxZ + boundingBox.minZ) * 0.5 - 0.5 / IChunk.CHUNK_MULTIPLE) * IChunk.CHUNK_MULTIPLE;
            SDFBoxRound sdf = new SDFBoxRound(halfWidth, halfHeight, halfDepth, 0.1 * IChunk.CHUNK_MULTIPLE);
            sdf.setTransformation(this.tmp.identity().translate(centerX, centerY, centerZ).rotate(living.yBodyRot, Y_AXIS));
            if (living instanceof Player) {
               sdf.setPriority(true);
            }

            sdfs.add(sdf);
         }
      }
   }

   public Vec3 getCameraTranslation() {
      return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
   }

   public Matrix4f getCameraViewProjectionMatrix() {
      return this.viewProjection;
   }

   public void addChunkEntity(ChunkEntity entity, int chunkX, int chunkY, int chunkZ) {
      this.chunks.put(Index.chunk(chunkX, chunkY, chunkZ), entity);
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
         this.loadedColumns.put(index, columns);
      }

      for (int i = 0; i < asyncChunkCreation.size(); i++) {
         columns.add(Index.chunk(chunkX, asyncChunkCreation.get(i).getY(), chunkZ));
      }

      this.contouring.queueEvent(() -> {
         for (int ix = 0; ix < asyncChunkCreation.size(); ix++) {
            this.contouring.addChunk(asyncChunkCreation.get(ix).create());
         }
      });
   }

   public void removeChunkColumn(int chunkX, int chunkZ) {
      long index = Index.chunk(chunkX, 0, chunkZ);
      LongList columns = (LongList)this.loadedColumns.remove(index);
      if (columns != null) {
         this.contouring.queueEvent(() -> {
            for (int i = 0; i < columns.size(); i++) {
               long currentIndex = columns.getLong(i);
               this.contouring.removeChunk(currentIndex);
            }
         });
      }
   }

   public void removeAll() {
      LongList toRemove = new LongArrayList();
      ObjectIterator var2 = this.loadedColumns.values().iterator();

      while (var2.hasNext()) {
         LongList column = (LongList)var2.next();

         for (int i = 0; i < column.size(); i++) {
            toRemove.add(column.getLong(i));
         }
      }

      if (toRemove.size() > 0) {
         this.contouring.queueEvent(() -> {
            for (int ix = 0; ix < toRemove.size(); ix++) {
               long currentIndex = toRemove.getLong(ix);
               this.contouring.removeChunk(currentIndex);
            }
         });
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
         GL32C.glDeleteVertexArrays(this.snowVAO);
      }

      this.chunks.clear();
   }

   public ShortSet getLightUpdates(long chunkIndex) {
      ShortSet lightUpdates = (ShortSet)this.fetchLightUpdates.get(chunkIndex);
      if (lightUpdates == null) {
         lightUpdates = new ShortOpenHashSet();
         this.fetchLightUpdates.put(chunkIndex, lightUpdates);
      }

      return lightUpdates;
   }

   public SnowBatch getSnowBatch() {
      return this.snowBatch;
   }

   public Long2ObjectMap<LongList> getLoadedColumns() {
      return this.loadedColumns;
   }

   class LightUpdate {
      byte posX;
      byte posY;
      byte posZ;
      byte lightData;
   }
}
