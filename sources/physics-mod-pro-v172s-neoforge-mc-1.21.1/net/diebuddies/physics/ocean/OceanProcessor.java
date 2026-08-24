package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap.Entry;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.physics.ocean.storage.EqualStorageType;
import net.diebuddies.physics.ocean.storage.FullStorageType2DInt;
import net.diebuddies.physics.ocean.storage.StorageContainer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class OceanProcessor extends IWorld<OceanChunk> implements Runnable {
   private ConcurrentLinkedQueue<Runnable> events;
   public final Dynamic2DArray waves = new Dynamic2DArray(50, 50);
   public final Dynamic2DArray depth = new Dynamic2DArray(50, 50);
   public final Long2ObjectMap<FullStorageType2DInt> loadedBiomeChunks = new Long2ObjectOpenHashMap(400);
   private Thread thread;
   private volatile boolean shutdown;
   private OceanWorld oceanWorld;
   private Short2ObjectMap<OceanLayer> oceanLayers;
   private Vector4f waterUVOffsets;
   private Vector2f waterMidCoord;
   protected LongSet processChunkColumns;
   public List<Runnable> proxyEvents;
   public ObjectSet<OceanLayer> layerUpdates;
   private float[] weights = new float[2];

   public OceanProcessor(OceanWorld oceanWorld, int minChunkY, int maxChunkY, Vector4f waterUVOffsets) {
      super(minChunkY, maxChunkY);
      this.oceanWorld = oceanWorld;
      this.layerUpdates = new ObjectOpenHashSet();
      this.processChunkColumns = new LongOpenHashSet();
      this.oceanLayers = new Short2ObjectOpenHashMap();
      this.events = new ConcurrentLinkedQueue<>();
      this.proxyEvents = new ObjectArrayList();
      this.waterUVOffsets = waterUVOffsets;
      this.waterMidCoord = new Vector2f(waterUVOffsets.x + waterUVOffsets.y, waterUVOffsets.z + waterUVOffsets.w).mul(0.5F);
      this.thread = new Thread(this, "Ocean Processor Thread");
      this.thread.setDaemon(true);
   }

   public void start() {
      this.thread.start();
   }

   public void join() {
      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }
   }

   @Override
   public void run() {
      while (!this.shutdown) {
         Runnable event = null;

         while ((event = this.events.poll()) != null) {
            event.run();
         }

         LongIterator it = this.processChunkColumns.iterator();

         while (it.hasNext()) {
            long index = it.nextLong();
            this.processChunkColumn(index);
         }

         this.processChunkColumns.clear();
         if (!this.layerUpdates.isEmpty()) {
            List<OceanSurface> generatedSurfaces = new ObjectArrayList();
            Iterator<OceanLayer> layerIterator = this.layerUpdates.iterator();

            while (layerIterator.hasNext()) {
               OceanLayer oceanLayer = layerIterator.next();
               OceanSurface oceanSurface = oceanLayer.generateMesh();
               if (oceanSurface != null) {
                  generatedSurfaces.add(oceanSurface);
                  layerIterator.remove();
               }
            }

            this.oceanWorld.queueEvent(() -> this.oceanWorld.replaceOceanMeshes(generatedSurfaces));
         }

         if (!this.proxyEvents.isEmpty()) {
            List<Runnable> multipleEventsCopy = new ObjectArrayList(this.proxyEvents);
            this.oceanWorld.queueEvent(() -> {
               for (Runnable task : multipleEventsCopy) {
                  task.run();
               }
            });
            this.proxyEvents.clear();
         }

         try {
            Thread.sleep(1L);
         } catch (InterruptedException var7) {
            var7.printStackTrace();
         }
      }
   }

   private void processChunkColumn(long index) {
      int x = Index.getXFromOceanLayer(index);
      int z = Index.getZFromOceanLayer(index);
      if (this.areSurroundingsLoaded(x, 0, z)) {
         int voxelX = x * 16;
         int voxelZ = z * 16;

         for (int y = this.minChunkY; y <= this.maxChunkY; y++) {
            OceanChunk chunk = this.getChunk(x, y, z);
            int voxelY = y * 16;
            if (chunk != null) {
               StorageContainer dataStorage = chunk.dataStorage;
               if (dataStorage.getStorageType() instanceof EqualStorageType) {
                  byte data = dataStorage.getData(0, 0, 0);
                  if (data > 0) {
                     int fy = voxelY + 16 - 1;

                     for (int xo = 0; xo < 16; xo++) {
                        for (int zo = 0; zo < 16; zo++) {
                           int fx = voxelX + xo;
                           int fz = voxelZ + zo;
                           if (this.isOceanSurface(data, fx, fy, fz)) {
                              this.setToSurface(data, fx, fy, fz);
                           }
                        }
                     }
                  }
               } else {
                  for (int xo = 0; xo < 16; xo++) {
                     for (int yo = 0; yo < 16; yo++) {
                        for (int zox = 0; zox < 16; zox++) {
                           byte data = dataStorage.getData(xo, yo, zox);
                           int fx = voxelX + xo;
                           int fy = voxelY + yo;
                           int fz = voxelZ + zox;
                           if (this.isOceanSurface(data, fx, fy, fz)) {
                              this.setToSurface(data, fx, fy, fz);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void setToSurface(byte state, int x, int y, int z) {
      OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)y);
      if (oceanLayer == null) {
         oceanLayer = new OceanLayer(this, (short)y);
         this.oceanLayers.put((short)y, oceanLayer);
      }

      if (oceanLayer != null) {
         short depth = this.getWaterDepthAndHeight(x, y, z);
         oceanLayer.setWaterAndDepthAndHeight(x, z, depth);
      }
   }

   public void blockChanged(int x, int y, int z, byte previousState, byte state) {
      if (this.areSurroundingsLoaded(WorldUtil.calculateChunkPosX(x), 0, WorldUtil.calculateChunkPosZ(z))) {
         OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)y);
         if (oceanLayer == null && this.isOceanSurface(state, x, y, z)) {
            oceanLayer = new OceanLayer(this, (short)y);
            this.oceanLayers.put((short)y, oceanLayer);
         }

         if (oceanLayer != null) {
            this.updateSurface(oceanLayer, state, x, y, z);
            this.updateSurface(oceanLayer, x + 1, y, z);
            this.updateSurface(oceanLayer, x - 1, y, z);
            this.updateSurface(oceanLayer, x, y, z + 1);
            this.updateSurface(oceanLayer, x, y, z - 1);
            if (previousState > 0 || state > 0) {
               boolean hasSurfaceNeighbour = false;

               for (int xo = -1; xo <= 1 && !hasSurfaceNeighbour; xo++) {
                  for (int zo = -1; zo <= 1 && !hasSurfaceNeighbour; zo++) {
                     if (xo != 0 || zo != 0) {
                        hasSurfaceNeighbour = oceanLayer.isWater(x + xo, z + zo);
                     }
                  }
               }

               if (hasSurfaceNeighbour) {
                  oceanLayer.causeLayerUpdate(x, z);
               }
            }

            short belowPos = (short)(y - 1);
            OceanLayer belowOceanLayer = (OceanLayer)this.oceanLayers.get(belowPos);
            if (belowOceanLayer == null) {
               if (this.isOceanSurface(x, belowPos, z)) {
                  belowOceanLayer = new OceanLayer(this, belowPos);
                  this.oceanLayers.put(belowPos, belowOceanLayer);
               }
            } else {
               this.updateSurface(belowOceanLayer, x, belowPos, z);
            }
         }

         ObjectIterator var12 = this.oceanLayers.short2ObjectEntrySet().iterator();

         while (var12.hasNext()) {
            Entry<OceanLayer> entry = (Entry<OceanLayer>)var12.next();
            oceanLayer = (OceanLayer)entry.getValue();
            short layerY = entry.getShortKey();
            oceanLayer.updateDepthAndHeight(x, layerY, z);
         }
      }
   }

   private void updateSurface(OceanLayer oceanLayer, byte state, int x, int y, int z) {
      if (this.isOceanSurface(state, x, y, z)) {
         short depth = this.getWaterDepthAndHeight(x, y, z);
         oceanLayer.setWaterAndDepthAndHeight(x, z, depth);
      } else {
         oceanLayer.unsetWater(x, z);
      }
   }

   private void updateSurface(OceanLayer oceanLayer, int x, int y, int z) {
      this.updateSurface(oceanLayer, this.getData(x, y, z), x, y, z);
   }

   private boolean isOceanSurface(byte state, int x, int y, int z) {
      return state > 0 && !this.isWater(x, y + 1, z) && !this.hasWaterFlow(x, y, z);
   }

   private boolean isOceanSurface(int x, int y, int z) {
      return this.isOceanSurface(this.getData(x, y, z), x, y, z);
   }

   public short getWaterDepthAndHeight(int x, int y, int z) {
      byte height = OceanLayer.RANGE;
      byte depth = OceanLayer.RANGE;

      for (byte offset = 1; offset <= OceanLayer.RANGE; offset++) {
         if (!this.isAir(x, y + offset, z)) {
            height = offset;
            break;
         }
      }

      for (byte offsetx = 1; offsetx <= OceanLayer.RANGE; offsetx++) {
         if (!this.isWater(x, y - offsetx, z)) {
            depth = offsetx;
            break;
         }
      }

      return (short)(depth | height << 8);
   }

   @Override
   public void removeChunkColumn(int x, int z) {
      super.removeChunkColumn(x, z);
      long biomeIndex = Index.chunk(x, 0, z);
      this.loadedBiomeChunks.remove(biomeIndex);
      Iterator<Entry<OceanLayer>> it = this.oceanLayers.short2ObjectEntrySet().iterator();

      while (it.hasNext()) {
         Entry<OceanLayer> entry = it.next();
         OceanLayer oceanLayer = (OceanLayer)entry.getValue();
         boolean empty = oceanLayer.remove(x, z);
         if (empty) {
            it.remove();
         }
      }
   }

   @Override
   public void removeAll() {
      super.removeAll();
      this.loadedBiomeChunks.clear();
      ObjectIterator var1 = this.oceanLayers.values().iterator();

      while (var1.hasNext()) {
         OceanLayer layer = (OceanLayer)var1.next();
         layer.clear();
      }

      this.oceanLayers.clear();
      this.proxyEvents.add(() -> this.oceanWorld.clearOceanLayers());
   }

   public boolean isWater(int x, int y, int z) {
      byte data = this.getData(x, y, z);
      return data != -1 && data != 0;
   }

   public boolean isAir(int x, int y, int z) {
      return this.getData(x, y, z) == 0;
   }

   public boolean hasWaterFlow(int x, int y, int z) {
      int vx = 0;
      int vz = 0;
      byte currentState = this.getData(x, y, z);

      for (Direction direction : Plane.HORIZONTAL) {
         int nx = x + direction.getStepX();
         int ny = y + direction.getStepY();
         int nz = z + direction.getStepZ();
         byte neighbourState = this.getData(nx, ny, nz);
         if (this.affectsFlow(neighbourState)) {
            int neighbourHeight = this.getOwnHeight(neighbourState);
            int magnitude = 0;
            if (neighbourHeight == 0) {
               byte belowNeighbourState = this.getData(nx, ny - 1, nz);
               boolean affectsFlow = this.affectsFlow(belowNeighbourState);
               neighbourHeight = this.getOwnHeight(belowNeighbourState);
               if (neighbourState > -1 && affectsFlow && neighbourHeight > 0) {
                  magnitude = this.getOwnHeight(currentState) - (neighbourHeight - 8);
               }
            } else if (neighbourHeight > 0) {
               magnitude = this.getOwnHeight(currentState) - neighbourHeight;
            }

            if (magnitude != 0) {
               vx += direction.getStepX() * magnitude;
               vz += direction.getStepZ() * magnitude;
            }
         }
      }

      return vx != 0 || vz != 0;
   }

   private boolean affectsFlow(byte neighbourState) {
      return neighbourState > 0;
   }

   private int getOwnHeight(byte neighbourState) {
      return Math.max(0, neighbourState);
   }

   public void updateLight(int worldX, int worldY, int worldZ, byte lightData) {
      IChunk<?> chunk = this.getChunkWorldPos(worldX, --worldY, worldZ);
      if (chunk != null) {
         OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)worldY);
         if (oceanLayer != null) {
            oceanLayer.setLight(worldX, worldZ, lightData);
         }
      }
   }

   public void updateLayerLight(int chunkX, short layerY, int chunkZ, byte[] lightData) {
      IChunk<?> chunk = this.getChunkWorldPos(chunkX * 16, layerY, chunkZ * 16);
      if (chunk != null) {
         OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get(layerY);
         if (oceanLayer != null) {
            oceanLayer.setLayerLight(chunkX, chunkZ, lightData);
         }
      }
   }

   public void updateBiome(int chunkX, int chunkZ, int[] colorData) {
      IChunk<?> chunk = this.getChunkWorldPos(chunkX * 16, 0, chunkZ * 16);
      if (chunk != null) {
         long biomeIndex = Index.chunk(chunkX, 0, chunkZ);
         this.loadedBiomeChunks.put(biomeIndex, new FullStorageType2DInt(colorData));
      }
   }

   public float getHeight(int worldX, int worldY, int worldZ) {
      byte data = this.getData(worldX, worldY, worldZ);
      byte dataAbove = this.getData(worldX, worldY + 1, worldZ);
      if (data > 0) {
         return dataAbove > 0 ? 1.0F : data / 9.0F;
      } else {
         return data == 0 ? 0.0F : -1.0F;
      }
   }

   public float calculateAverageHeight(int worldX, int worldY, int worldZ, float height, float side1, float side2) {
      if (!(side2 >= 1.0F) && !(side1 >= 1.0F)) {
         this.weights[0] = 0.0F;
         this.weights[1] = 0.0F;
         if (side2 > 0.0F || side1 > 0.0F) {
            float currentHeight = this.getHeight(worldX, worldY, worldZ);
            if (currentHeight >= 1.0F) {
               return 1.0F;
            }

            this.addWeightedHeight(this.weights, currentHeight);
         }

         this.addWeightedHeight(this.weights, height);
         this.addWeightedHeight(this.weights, side2);
         this.addWeightedHeight(this.weights, side1);
         return this.weights[0] / this.weights[1];
      } else {
         return 1.0F;
      }
   }

   private void addWeightedHeight(float[] weights, float height) {
      if (height >= 0.8F) {
         weights[0] += height * 10.0F;
         weights[1] += 10.0F;
      } else if (height >= 0.0F) {
         weights[0] += height;
         weights[1]++;
      }
   }

   public void loadOceanBiomes(int chunkX, int chunkZ) {
      int[] colorData = new int[256];
      int worldX = chunkX * 16;
      int worldZ = chunkZ * 16;
      MutableBlockPos tmp = new MutableBlockPos();

      for (int xo = 0; xo < 16; xo++) {
         for (int zo = 0; zo < 16; zo++) {
            tmp.set(worldX + xo, 0, worldZ + zo);
            int color = BiomeColors.getAverageWaterColor(this.oceanWorld.getLevel(), tmp);
            int flippedColor = 0xFF000000 | color & 0xFF00;
            flippedColor |= (color & 0xFF) << 16;
            flippedColor |= (color & 0xFF0000) >> 16;
            colorData[zo * 16 + xo] = flippedColor;
         }
      }

      this.updateBiome(chunkX, chunkZ, colorData);
   }

   public int getBiomeColor(int x, int z) {
      FullStorageType2DInt c = this.getBiomeChunkWorldPos(x, z);
      return c != null ? c.getData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosZ(z)) : 0;
   }

   public FullStorageType2DInt getBiomeChunkWorldPos(int x, int z) {
      int chunkX = WorldUtil.calculateChunkPosX(x);
      int chunkZ = WorldUtil.calculateChunkPosZ(z);
      return (FullStorageType2DInt)this.loadedBiomeChunks.get(Index.chunk(chunkX, 0, chunkZ));
   }

   public OceanWorld getOceanWorld() {
      return this.oceanWorld;
   }

   public Vector4f getWaterUVOffsets() {
      return this.waterUVOffsets;
   }

   public Vector2f getWaterMidCoord() {
      return this.waterMidCoord;
   }

   public Short2ObjectMap<OceanLayer> getOceanLayers() {
      return this.oceanLayers;
   }

   public void queueEvent(Runnable runnable) {
      this.events.add(runnable);
   }

   public void shutdown() {
      this.shutdown = true;
   }
}
