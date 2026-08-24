package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.storage.EqualStorageType;
import net.diebuddies.physics.snow.storage.StorageContainer;
import org.joml.Vector3f;
import org.joml.Vector3i;

public abstract class IChunk<T extends IWorld> {
   public static final int ALL_NEIGHBOURS_LOADED = 8;
   public static final byte MIN_VALUE = -127;
   public static final byte MAX_VALUE = 127;
   public static final int LIGHT_SIZE = 16;
   public static final int MAX_LIGHT = 255;
   public static int CHUNK_SIZE;
   public static int CHUNK_SIZE_HALF;
   public static int CHUNK_SIZE_BITS;
   public static int CHUNK_SIZE_USED_BITS;
   public static int CHUNK_VOLUME;
   public static int CHUNK_MULTIPLE;
   public static int CHUNK_MULTIPLE_BITS;
   public static float CHUNK_MULTIPLE_INV;
   protected T world;
   public final StorageContainer dataStorage;
   public final StorageContainer lightStorage;
   public StorageContainer modulationStorage;
   public final int x;
   public final int y;
   public final int z;
   public final int xVoxel;
   public final int yVoxel;
   public final int zVoxel;
   public final int hashCode;
   protected int loadedNeighbourCount;
   public Int2ObjectMap<IntSet> activeNodes = new Int2ObjectOpenHashMap();

   public static void updateChunkSize() {
      if (ConfigClient.snowQuality == 0) {
         setChunkSize(32);
      } else {
         setChunkSize(64);
      }
   }

   private static void setChunkSize(int chunkSize) {
      CHUNK_SIZE = chunkSize;
      CHUNK_SIZE_HALF = CHUNK_SIZE / 2;
      CHUNK_SIZE_BITS = CHUNK_SIZE - 1;
      CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(CHUNK_SIZE_BITS);
      CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
      CHUNK_MULTIPLE = CHUNK_SIZE / 16;
      CHUNK_MULTIPLE_BITS = 32 - Integer.numberOfLeadingZeros(CHUNK_MULTIPLE) - 1;
      CHUNK_MULTIPLE_INV = 1.0F / CHUNK_MULTIPLE;
      Index.updateMasks();
   }

   public IChunk(int x, int y, int z, StorageContainer dataStorage, StorageContainer lightStorage) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.xVoxel = x * CHUNK_SIZE;
      this.yVoxel = y * CHUNK_SIZE;
      this.zVoxel = z * CHUNK_SIZE;
      this.dataStorage = dataStorage;
      this.lightStorage = lightStorage;
      int prime = 31;
      int chashCode = 1;
      chashCode = 31 * chashCode + x;
      chashCode = 31 * chashCode + y;
      chashCode = 31 * chashCode + z;
      this.hashCode = chashCode;
   }

   public int getLoadedNeighbourCount() {
      return this.loadedNeighbourCount;
   }

   public void setLoadedNeighbourCount(int loadedNeighbourCount) {
      this.loadedNeighbourCount = loadedNeighbourCount;
   }

   public void setWorld(T world) {
      this.world = world;
   }

   public T getWorld() {
      return this.world;
   }

   public float getData(int x, int y, int z) {
      return this.outOfBounds(x, y, z)
         ? this.world.getData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z) / 127.0F
         : this.dataStorage.getData(x, y, z) / 127.0F;
   }

   public byte getDataByte(int x, int y, int z) {
      return this.outOfBounds(x, y, z) ? this.world.getData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z) : this.dataStorage.getData(x, y, z);
   }

   public byte getDataByteFast(int x, int y, int z) {
      return this.dataStorage.getData(x, y, z);
   }

   public byte getLightDataByte(int x, int y, int z) {
      return this.outOfBounds(x, y, z) ? this.world.getLightData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z) : this.lightStorage.getData(x, y, z);
   }

   public byte getLightDataByteFast(int x, int y, int z) {
      return this.lightStorage.getData(x, y, z);
   }

   public void setData(int x, int y, int z, byte data) {
      this.dataStorage.setData(x, y, z, data);
   }

   public void setLightData(int x, int y, int z, byte data) {
      this.lightStorage.setData(x, y, z, data);
   }

   public boolean outOfBounds(int x, int y, int z) {
      return x >= CHUNK_SIZE || y >= CHUNK_SIZE || z >= CHUNK_SIZE || x < 0 || y < 0 || z < 0;
   }

   public float getDensity(float x, float y, float z) {
      int ix = (int)Math.round(Math.floor(x));
      int iy = (int)Math.round(Math.floor(y));
      int iz = (int)Math.round(Math.floor(z));
      float p1 = this.getData(ix, iy, iz);
      float p2 = this.getData(ix + 1, iy, iz);
      float p3 = this.getData(ix, iy + 1, iz);
      float p4 = this.getData(ix + 1, iy + 1, iz);
      float p5 = this.getData(ix, iy, iz + 1);
      float p6 = this.getData(ix + 1, iy, iz + 1);
      float p7 = this.getData(ix, iy + 1, iz + 1);
      float p8 = this.getData(ix + 1, iy + 1, iz + 1);
      float xFinal1 = org.joml.Math.lerp(p1, p2, x - ix);
      float xFinal2 = org.joml.Math.lerp(p3, p4, x - ix);
      float xFinal3 = org.joml.Math.lerp(p5, p6, x - ix);
      float xFinal4 = org.joml.Math.lerp(p7, p8, x - ix);
      float yFinal1 = org.joml.Math.lerp(xFinal1, xFinal2, y - iy);
      float yFinal2 = org.joml.Math.lerp(xFinal3, xFinal4, y - iy);
      return org.joml.Math.lerp(yFinal1, yFinal2, z - iz);
   }

   public Vector3f calculateNormal(float x, float y, float z, float offset, Vector3f normal) {
      normal.x = this.getDensity(x + offset, y, z) - this.getDensity(x - offset, y, z);
      normal.y = this.getDensity(x, y + offset, z) - this.getDensity(x, y - offset, z);
      normal.z = this.getDensity(x, y, z + offset) - this.getDensity(x, y, z - offset);
      if (normal.x == 0.0F && normal.y == 0.0F && normal.z == 0.0F) {
         normal.x = 0.0F;
         normal.y = 1.0F;
         normal.z = 0.0F;
      }

      normal.negate();
      normal.normalize();
      return normal;
   }

   public Vector3f calculateNormal(float x, float y, float z, float offset) {
      return this.calculateNormal(x, y, z, offset, new Vector3f());
   }

   public boolean isStorageBorderSameSign() {
      StorageContainer storage = this.dataStorage;
      if (!(storage.getStorageType() instanceof EqualStorageType)) {
         return false;
      } else {
         boolean positiveSign = false;
         byte data = this.getDataByte(0, 0, 0);
         if (data >= 0) {
            positiveSign = true;
         }

         for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
               for (int k = 0; k <= 1; k++) {
                  if (i != 0 || j != 0 || k != 0) {
                     IChunk chunk = this.world.getChunk(this.x + i, this.y + j, this.z + k);
                     if (chunk != null) {
                        StorageContainer neighbourStorage = chunk.dataStorage;
                        if (!(neighbourStorage.getStorageType() instanceof EqualStorageType)) {
                           return false;
                        }

                        if (positiveSign) {
                           if (neighbourStorage.getData(0, 0, 0) < 0) {
                              return false;
                           }
                        } else if (neighbourStorage.getData(0, 0, 0) >= 0) {
                           return false;
                        }
                     } else if (positiveSign) {
                        return false;
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean isSolid(Vector3i pos) {
      return this.isSolid(pos.x, pos.y, pos.z);
   }

   public boolean isSolid(int x, int y, int z) {
      return this.getDataByte(x, y, z) >= 0;
   }

   public IChunk getNeighbourChunk(int xOffset, int yOffset, int zOffset) {
      return this.world.getChunk(this.x + xOffset, this.y + yOffset, this.z + zOffset);
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         IChunk other = (IChunk)obj;
         if (this.x != other.x) {
            return false;
         } else {
            return this.y != other.y ? false : this.z == other.z;
         }
      }
   }

   static {
      updateChunkSize();
   }
}
