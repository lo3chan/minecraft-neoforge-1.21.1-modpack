package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.ocean.storage.FullStorageType2DBit;
import net.diebuddies.physics.ocean.storage.FullStorageType2DByte;
import net.diebuddies.physics.ocean.storage.FullStorageType2DShort;
import net.minecraft.util.Mth;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class OceanLayer {
   private static int counter;
   private int hashCode;
   private static final int CHUNK_GROUPING = 6;
   public static byte RANGE = 32;
   public static byte RANGE_SOLID = (byte)(RANGE + 1);
   public static float SCALE_COLOR_RANGE = 256.0F / RANGE;
   public static final int CHUNK_SIZE = 16;
   public static final int CHUNK_SIZE_BITS = 15;
   public static final int CHUNK_VOLUME = 256;
   public static final int CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(15);
   public static final byte SOLID = 0;
   private OceanProcessor processor;
   private final ProxyOceanLayer proxyLayer;
   private final short layerPosY;
   private Long2ObjectMap<OceanLayer.OceanStorage> oceanStorage;
   private int lastMinX = 2147483647;
   private int lastMinZ = 2147483647;
   private LongSet surfaceUpdatesNeeded;

   public static void updateRange(byte range) {
      RANGE = range;
      RANGE_SOLID = (byte)(RANGE + 1);
      SCALE_COLOR_RANGE = 256.0F / RANGE;
   }

   public OceanLayer(OceanProcessor processor, short layerPosY) {
      this.hashCode = counter++;
      this.processor = processor;
      this.layerPosY = layerPosY;
      this.oceanStorage = new Long2ObjectOpenHashMap();
      this.proxyLayer = new ProxyOceanLayer(layerPosY);
      this.surfaceUpdatesNeeded = new LongOpenHashSet();
      processor.proxyEvents.add(() -> processor.getOceanWorld().getOceanLayers().put(layerPosY, this.proxyLayer));
   }

   @Nullable
   public OceanSurface generateMesh() {
      if (this.oceanStorage.isEmpty()) {
         this.lastMinX = 2147483647;
         this.lastMinZ = 2147483647;
         this.surfaceUpdatesNeeded.clear();
         return new OceanSurface(this.proxyLayer, true);
      } else {
         int minX = 2147483647;
         int maxX = -2147483648;
         int minZ = 2147483647;
         int maxZ = -2147483648;
         ObjectIterator width = this.oceanStorage.long2ObjectEntrySet().iterator();

         while (width.hasNext()) {
            Entry<OceanLayer.OceanStorage> entry = (Entry<OceanLayer.OceanStorage>)width.next();
            long layerIndex = entry.getLongKey();
            OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)entry.getValue();
            if (storage.lights == null) {
               return null;
            }

            FullStorageType2DBit blocks = storage.blocks;
            int chunkX = Index.getXFromOceanLayer(layerIndex) * 16;
            int chunkZ = Index.getZFromOceanLayer(layerIndex) * 16;

            for (int xo = 0; xo < 16; xo++) {
               for (int zo = 0; zo < 16; zo++) {
                  int data = blocks.getData(xo, zo);
                  if (data != 0) {
                     int fx = chunkX + xo;
                     int fz = chunkZ + zo;
                     if (fx < minX) {
                        minX = fx;
                     }

                     if (fx > maxX) {
                        maxX = fx;
                     }

                     if (fz < minZ) {
                        minZ = fz;
                     }

                     if (fz > maxZ) {
                        maxZ = fz;
                     }
                  }
               }
            }
         }

         if (minX == 2147483647) {
            this.lastMinX = 2147483647;
            this.lastMinZ = 2147483647;
            this.surfaceUpdatesNeeded.clear();
            return new OceanSurface(this.proxyLayer, true);
         } else {
            int widthx = maxX - minX + 1;
            int height = maxZ - minZ + 1;
            Dynamic2DArray waves = this.processor.waves;
            Dynamic2DArray depth = this.processor.depth;
            waves.request(widthx, height);

            for (int x = minX; x <= maxX; x++) {
               for (int z = minZ; z <= maxZ; z++) {
                  int data = this.getOcean(x, z);
                  if (data == 0) {
                     waves.set(x - minX, z - minZ, RANGE_SOLID);
                  } else {
                     waves.set(x - minX, z - minZ, (byte)0);
                  }
               }
            }

            depth.request(widthx, height);
            boolean allZeroDepth = true;

            for (int x = minX; x <= maxX; x++) {
               for (int zx = minZ; zx <= maxZ; zx++) {
                  byte data = this.getMaxDepth(x, zx);
                  if (data > 1) {
                     allZeroDepth = false;
                  }

                  depth.set(x - minX, zx - minZ, (byte)Math.max(0, RANGE - Math.max(0, data - 1)));
               }
            }

            int maxInfluence = 0;
            boolean createSingularLayer = true;
            byte[] texture;
            if (allZeroDepth) {
               texture = new byte[1];
               int waterCount = 0;

               for (int x = 0; x < widthx; x++) {
                  for (int zx = 0; zx < height; zx++) {
                     int val = 255 - net.diebuddies.math.Math.clamp((int)(Math.max(waves.get(x, zx) + 1, depth.get(x, zx) + 1) * SCALE_COLOR_RANGE), 0, 255);
                     if (val != RANGE_SOLID) {
                        waterCount++;
                     }
                  }
               }

               if (waterCount > 1024) {
                  createSingularLayer = false;
               }
            } else {
               this.blur(waves, RANGE_SOLID);
               this.blur(depth, RANGE);
               texture = new byte[widthx * height];
               int waterCount = 0;

               for (int x = 0; x < widthx; x++) {
                  for (int zxx = 0; zxx < height; zxx++) {
                     int val = 255 - net.diebuddies.math.Math.clamp((int)(Math.max(waves.get(x, zxx) + 1, depth.get(x, zxx) + 1) * SCALE_COLOR_RANGE), 0, 255);
                     texture[zxx * widthx + x] = (byte)val;
                     maxInfluence = Math.max(maxInfluence, val);
                     if (val != RANGE_SOLID) {
                        waterCount++;
                     }
                  }
               }

               if (waterCount > 1024) {
                  createSingularLayer = false;
               }
            }

            if (this.lastMinX == 2147483647) {
               this.lastMinX = minX;
               this.lastMinZ = minZ;
               this.processor.proxyEvents.add(() -> this.proxyLayer.setWaveOffset(minX, minZ));
            }

            int waveOffsetX = this.lastMinX - minX;
            int waveOffsetZ = this.lastMinZ - minZ;
            if (Math.abs(waveOffsetX) > 36000 || Math.abs(waveOffsetZ) > 36000) {
               this.lastMinX = minX;
               this.lastMinZ = minZ;
               waveOffsetX = this.lastMinX - minX;
               waveOffsetZ = this.lastMinZ - minZ;
            }

            OceanSurface surface = new OceanSurface(texture, widthx, height, waveOffsetX, waveOffsetZ, this.proxyLayer);
            if (createSingularLayer) {
               RawMesh mesh = this.generateMesh(waves, minX, minZ, 0, 0, widthx, height);
               Matrix4d transformation = new Matrix4d().translation(minX, this.layerPosY, minZ);
               OceanMesh oceanMesh = new OceanMesh(mesh, transformation, texture, maxInfluence / 255.0F, widthx, height, waveOffsetX, waveOffsetZ, 0, 0);
               surface.singleMesh = true;
               return surface.addOceanMesh(0, this.layerPosY, 0, oceanMesh);
            } else {
               LongIterator it = this.surfaceUpdatesNeeded.iterator();

               while (it.hasNext()) {
                  long index = it.nextLong();
                  int chunkX = Index.getXFromOceanLayer(index);
                  int chunkZ = Index.getZFromOceanLayer(index);
                  int chunkSize = 96;
                  int startX = chunkX * chunkSize - minX;
                  int startZ = chunkZ * chunkSize - minZ;
                  int endX = startX + chunkSize;
                  int endZ = startZ + chunkSize;
                  Matrix4d transformation = new Matrix4d().translation(minX, this.layerPosY, minZ);
                  RawMesh mesh = this.generateMesh(waves, minX, minZ, startX, startZ, endX, endZ);
                  int textureSize = chunkSize + 1;
                  byte[] subTexture = null;
                  if (mesh != null) {
                     if (allZeroDepth) {
                        subTexture = new byte[1];
                     } else {
                        subTexture = new byte[textureSize * textureSize];

                        for (int x = 0; x < textureSize; x++) {
                           for (int zxxx = 0; zxxx < textureSize; zxxx++) {
                              int textureIndex = (startZ + zxxx) * widthx + startX + x;
                              if (textureIndex >= 0 && textureIndex < texture.length) {
                                 subTexture[zxxx * textureSize + x] = texture[textureIndex];
                              }
                           }
                        }
                     }
                  }

                  OceanMesh oceanMesh = new OceanMesh(
                     mesh, transformation, subTexture, maxInfluence / 255.0F, textureSize, textureSize, waveOffsetX, waveOffsetZ, startX, startZ
                  );
                  surface.addOceanMesh(chunkX, this.layerPosY, chunkZ, oceanMesh);
               }

               this.surfaceUpdatesNeeded.clear();
               return surface;
            }
         }
      }
   }

   private RawMesh generateMesh(Dynamic2DArray waves, int offsetX, int offsetZ, int startX, int startZ, int endX, int endZ) {
      LongList points = new LongArrayList();
      float size = 1.0F;
      startX = Math.max(0, startX);
      startZ = Math.max(0, startZ);
      endX = Math.min(waves.getWidth(), endX);
      endZ = Math.min(waves.getHeight(), endZ);

      for (int x = startX; x < endX; x++) {
         for (int z = startZ; z < endZ; z++) {
            if (x >= 0 && z >= 0 && x < waves.getWidth() && z < waves.getHeight() && waves.get(x, z) != RANGE_SOLID) {
               points.add((long)x << 32 | z & 4294967295L);
            }
         }
      }

      if (points.isEmpty()) {
         return null;
      } else {
         int positionSize = points.size() * 3 * 4 * 4;
         int colorSize = points.size() * 4 * 4;
         int uvSize = points.size() * 2 * 4 * 4;
         int lightSize = points.size() * 4 * 4;
         int vertexSize = positionSize + colorSize + uvSize + lightSize;
         RawMesh mesh = new RawMesh();
         ByteBuffer data = MemoryUtil.memAlloc(vertexSize);
         long pointer = MemoryUtil.memAddress(data);
         ByteBuffer indexData = MemoryUtil.memAlloc(points.size() * 6 * 2);
         long indexPointer = MemoryUtil.memAddress(indexData);
         Vector4f waterUVs = this.processor.getWaterUVOffsets();
         Vector2f midCoord = this.processor.getWaterMidCoord();
         int pointsSize = points.size();
         int indexCount = 0;

         for (int i = 0; i < pointsSize; i++) {
            long localIndex = points.getLong(i);
            int localX = (int)(localIndex >> 32);
            int localZ = (int)localIndex;
            int worldX = offsetX + localX;
            int worldZ = offsetZ + localZ;
            float height = this.processor.getHeight(worldX, this.layerPosY, worldZ);
            float north = this.processor.getHeight(worldX, this.layerPosY, worldZ - 1);
            float south = this.processor.getHeight(worldX, this.layerPosY, worldZ + 1);
            float east = this.processor.getHeight(worldX + 1, this.layerPosY, worldZ);
            float west = this.processor.getHeight(worldX - 1, this.layerPosY, worldZ);
            float h1 = this.processor.calculateAverageHeight(worldX + 1, this.layerPosY, worldZ - 1, height, north, east) - 0.001F;
            float h2 = this.processor.calculateAverageHeight(worldX - 1, this.layerPosY, worldZ - 1, height, north, west) - 0.001F;
            float h3 = this.processor.calculateAverageHeight(worldX + 1, this.layerPosY, worldZ + 1, height, south, east) - 0.001F;
            float h4 = this.processor.calculateAverageHeight(worldX - 1, this.layerPosY, worldZ + 1, height, south, west) - 0.001F;
            MemoryUtil.memPutFloat(pointer, localX + size);
            MemoryUtil.memPutFloat(pointer + 4L, h3);
            MemoryUtil.memPutFloat(pointer + 8L, localZ + size);
            MemoryUtil.memPutFloat(pointer + 12L, waterUVs.y);
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.w);
            MemoryUtil.memPutInt(pointer + 20L, this.calculateLight(worldX, worldZ));
            MemoryUtil.memPutInt(pointer + 24L, this.calculateBiomeColor(worldX, worldZ));
            pointer += 28L;
            MemoryUtil.memPutFloat(pointer, localX + size);
            MemoryUtil.memPutFloat(pointer + 4L, h1);
            MemoryUtil.memPutFloat(pointer + 8L, localZ);
            MemoryUtil.memPutFloat(pointer + 12L, waterUVs.y);
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.z);
            MemoryUtil.memPutInt(pointer + 20L, this.calculateLight(worldX, worldZ - 1));
            MemoryUtil.memPutInt(pointer + 24L, this.calculateBiomeColor(worldX, worldZ - 1));
            pointer += 28L;
            MemoryUtil.memPutFloat(pointer, localX);
            MemoryUtil.memPutFloat(pointer + 4L, h2);
            MemoryUtil.memPutFloat(pointer + 8L, localZ);
            MemoryUtil.memPutFloat(pointer + 12L, waterUVs.x);
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.z);
            MemoryUtil.memPutInt(pointer + 20L, this.calculateLight(worldX - 1, worldZ - 1));
            MemoryUtil.memPutInt(pointer + 24L, this.calculateBiomeColor(worldX - 1, worldZ - 1));
            pointer += 28L;
            MemoryUtil.memPutFloat(pointer, localX);
            MemoryUtil.memPutFloat(pointer + 4L, h4);
            MemoryUtil.memPutFloat(pointer + 8L, localZ + size);
            MemoryUtil.memPutFloat(pointer + 12L, waterUVs.x);
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.w);
            MemoryUtil.memPutInt(pointer + 20L, this.calculateLight(worldX - 1, worldZ));
            MemoryUtil.memPutInt(pointer + 24L, this.calculateBiomeColor(worldX - 1, worldZ));
            pointer += 28L;
            MemoryUtil.memPutShort(indexPointer, (short)indexCount);
            MemoryUtil.memPutShort(indexPointer + 2L, (short)(indexCount + 1));
            MemoryUtil.memPutShort(indexPointer + 4L, (short)(indexCount + 2));
            MemoryUtil.memPutShort(indexPointer + 6L, (short)(indexCount + 2));
            MemoryUtil.memPutShort(indexPointer + 8L, (short)(indexCount + 3));
            MemoryUtil.memPutShort(indexPointer + 10L, (short)indexCount);
            indexCount += 4;
            indexPointer += 12L;
         }

         mesh.data = data;
         mesh.indexData = indexData;
         return mesh;
      }
   }

   private int calculateLight(int worldX, int worldZ) {
      int count = 0;
      int sky = 0;
      int block = 0;

      for (int x = 0; x <= 1; x++) {
         int fx = worldX + x;

         for (int z = 0; z <= 1; z++) {
            int fz = worldZ + z;
            OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(oceanLayerChunkFromWorldPos(fx, fz));
            if (storage != null && storage.blocks.getData(fx & 15, fz & 15) != 0) {
               byte lightData = storage.lights.getData(fx & 15, fz & 15);
               sky += lightData >> 4 & 15;
               block += lightData & 15;
               count++;
            }
         }
      }

      sky /= count;
      block /= count;
      return sky << 20 | block << 4;
   }

   private int calculateBiomeColor(int worldX, int worldZ) {
      int count = 0;
      int r = 0;
      int g = 0;
      int b = 0;

      for (int x = 0; x <= 1; x++) {
         int fx = worldX + x;

         for (int z = 0; z <= 1; z++) {
            int fz = worldZ + z;
            int color = this.processor.getBiomeColor(fx, fz);
            if (color != 0) {
               r += color & 0xFF;
               g += (color & 0xFF00) >> 8;
               b += (color & 0xFF0000) >> 16;
               count++;
            }
         }
      }

      r /= count;
      g /= count;
      b /= count;
      return r | g << 8 | b << 16 | 0xFF000000;
   }

   public int getOcean(int x, int z) {
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : storage.blocks.getData(x & 15, z & 15);
   }

   public byte getMaxDepth(int x, int z) {
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(oceanLayerChunkFromWorldPos(x, z));
      if (storage == null) {
         return 1;
      } else {
         short data = storage.depths.getData(x & 15, z & 15);
         byte depth = (byte)(data & '\uffff');
         byte height = (byte)(data >> 8 & 65535);
         return depth < height ? depth : height;
      }
   }

   private void blur(Dynamic2DArray arr, byte borderValue) {
      int widthMinusOne = arr.getWidth() - 1;
      int heightMinusOne = arr.getHeight() - 1;
      int borderValueMinusOne = borderValue - 1;

      for (int y = 0; y < arr.getHeight(); y++) {
         arr.set(widthMinusOne, y, (byte)Math.max(0, Math.max(arr.get(widthMinusOne, y), borderValueMinusOne)));
         arr.set(0, y, (byte)Math.max(0, Math.max(arr.get(0, y), borderValueMinusOne)));

         for (int x = 0; x < widthMinusOne; x++) {
            arr.set(x + 1, y, (byte)Math.max(0, Math.max(arr.get(x + 1, y), arr.get(x, y) - 1)));
         }

         for (int x = widthMinusOne; x >= 1; x--) {
            arr.set(x - 1, y, (byte)Math.max(0, Math.max(arr.get(x - 1, y), arr.get(x, y) - 1)));
         }
      }

      for (int x = 0; x < arr.getWidth(); x++) {
         arr.set(x, heightMinusOne, (byte)Math.max(0, Math.max(arr.get(x, heightMinusOne), borderValueMinusOne)));
         arr.set(x, 0, (byte)Math.max(0, Math.max(arr.get(x, 0), borderValueMinusOne)));

         for (int y = 0; y < heightMinusOne; y++) {
            arr.set(x, y + 1, (byte)Math.max(0, Math.max(arr.get(x, y + 1), arr.get(x, y) - 1)));
         }

         for (int y = heightMinusOne; y >= 1; y--) {
            arr.set(x, y - 1, (byte)Math.max(0, Math.max(arr.get(x, y - 1), arr.get(x, y) - 1)));
         }
      }
   }

   public boolean remove(int chunkX, int chunkZ) {
      long index = Index.oceanLayerChunk(chunkX, chunkZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.remove(index);
      if (storage != null) {
         this.processor.layerUpdates.add(this);
         this.causeOceanSurfaceUpdate(chunkX * 16, chunkZ * 16);
         this.causeOceanSurfaceUpdate(chunkX * 16 + 15, chunkZ * 16);
         this.causeOceanSurfaceUpdate(chunkX * 16 + 15, chunkZ * 16 + 15);
         this.causeOceanSurfaceUpdate(chunkX * 16, chunkZ * 16 + 15);
         this.processor.proxyEvents.add(() -> {
            this.proxyLayer.getOceanStorage().remove(index);
            if (this.proxyLayer.getOceanStorage().isEmpty()) {
               this.processor.getOceanWorld().removeOceanLayer(this.layerPosY);
            }
         });
      }

      return this.oceanStorage.isEmpty();
   }

   public void clear() {
      this.oceanStorage.clear();
      this.processor.layerUpdates.add(this);
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   public void setWaterAndDepthAndHeight(int worldX, int worldZ, short depthAndHeight) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.computeIfAbsent(index, key -> new OceanLayer.OceanStorage(key));
      int fx = worldX & 15;
      int fz = worldZ & 15;
      boolean changed = false;
      changed |= storage.blocks.setAndCompareData(fx, fz);
      changed |= storage.depths.setAndCompareData(fx, fz, depthAndHeight);
      if (changed) {
         this.processor.layerUpdates.add(this);
         this.causeOceanSurfaceUpdate(worldX, worldZ);
         this.processor.proxyEvents.add(() -> {
            ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
            proxyStorage.blocks.setData(fx, fz);
            proxyStorage.depths.setData(fx, fz, depthAndHeight);
         });
      }
   }

   public void causeLayerUpdate(int worldX, int worldZ) {
      this.processor.layerUpdates.add(this);
      this.causeOceanSurfaceUpdate(worldX, worldZ);
   }

   private void causeOceanSurfaceUpdate(int worldX, int worldZ) {
      int grouping = 6;
      int startX = Mth.floor((double)calculateChunkPosX(worldX - RANGE_SOLID) / grouping);
      int endX = Mth.floor((double)calculateChunkPosX(worldX + RANGE_SOLID) / grouping);
      int startZ = Mth.floor((double)calculateChunkPosZ(worldZ - RANGE_SOLID) / grouping);
      int endZ = Mth.floor((double)calculateChunkPosZ(worldZ + RANGE_SOLID) / grouping);

      for (int x = startX; x <= endX; x++) {
         for (int z = startZ; z <= endZ; z++) {
            this.surfaceUpdatesNeeded.add(Index.oceanLayerChunk(x, z));
         }
      }
   }

   public void unsetWater(int worldX, int worldZ) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         int fx = worldX & 15;
         int fz = worldZ & 15;
         boolean changed = storage.blocks.unsetAndCompareData(fx, fz);
         if (changed) {
            this.processor.layerUpdates.add(this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
            this.processor.proxyEvents.add(() -> {
               ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
               proxyStorage.blocks.unsetData(fx, fz);
            });
         }
      }
   }

   public boolean isWater(int worldX, int worldZ) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         int fx = worldX & 15;
         int fz = worldZ & 15;
         return storage.blocks.getData(fx, fz) > 0;
      } else {
         return false;
      }
   }

   public void updateDepthAndHeight(int worldX, short layerY, int worldZ) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         int fx = worldX & 15;
         int fz = worldZ & 15;
         if (storage.blocks.getData(fx, fz) != 0) {
            short depth = this.processor.getWaterDepthAndHeight(worldX, layerY, worldZ);
            boolean changed = storage.depths.setAndCompareData(fx, fz, depth);
            if (changed) {
               this.processor.layerUpdates.add(this);
               this.causeOceanSurfaceUpdate(worldX, worldZ);
               this.processor.proxyEvents.add(() -> {
                  ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
                  proxyStorage.depths.setData(fx, fz, depth);
               });
            }
         }
      }
   }

   public void setLight(int worldX, int worldZ, byte light) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null && storage.lights != null) {
         boolean changed = storage.lights.setAndCompareData(worldX & 15, worldZ & 15, light);
         if (changed) {
            this.processor.layerUpdates.add(this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
         }
      }
   }

   public void setLayerLight(int chunkX, int chunkZ, byte[] light) {
      long index = Index.oceanLayerChunk(chunkX, chunkZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         storage.lights = new FullStorageType2DByte(light);
      }
   }

   public static int calculateChunkPosX(int worldX) {
      return worldX >> CHUNK_SIZE_USED_BITS;
   }

   public static int calculateChunkPosZ(int worldZ) {
      return worldZ >> CHUNK_SIZE_USED_BITS;
   }

   public static long oceanLayerChunkFromWorldPos(int worldX, int worldZ) {
      return Index.oceanLayerChunk(calculateChunkPosX(worldX), calculateChunkPosZ(worldZ));
   }

   public short getLayerPosY() {
      return this.layerPosY;
   }

   private class OceanStorage {
      public final int x;
      public final int z;
      public final int size = 256;
      public final FullStorageType2DBit blocks;
      public final FullStorageType2DShort depths;
      @Nullable
      public FullStorageType2DByte lights;

      public OceanStorage(long index) {
         this.x = Index.getXFromOceanLayer(index);
         this.z = Index.getZFromOceanLayer(index);
         this.blocks = new FullStorageType2DBit(this.size);
         this.depths = new FullStorageType2DShort(this.size);
         OceanLayer.this.processor.loadOceanBiomes(this.x, this.z);
         OceanLayer.this.processor
            .getOceanWorld()
            .queueEvent(() -> OceanLayer.this.processor.getOceanWorld().loadOceanLayerLights(this.x, OceanLayer.this.layerPosY, this.z));
         OceanLayer.this.processor.proxyEvents.add(() -> {
            OceanLayer.this.proxyLayer.getOceanStorage().put(index, new ProxyOceanStorage(index));
            OceanLayer.this.processor.getOceanWorld().getOceanLayers().put(OceanLayer.this.layerPosY, OceanLayer.this.proxyLayer);
         });
      }
   }
}
