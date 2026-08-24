package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import net.diebuddies.opengl.VAO;
import net.minecraft.util.Mth;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class ProxyOceanLayer {
   private final short layerPosY;
   private int waveOffsetX = 2147483647;
   private int waveOffsetZ = 2147483647;
   private Long2ObjectMap<ProxyOceanStorage> oceanStorage;
   private OceanSurface oceanSurface;
   private LinkedList<RippleParticle> rippleParticles;
   private boolean needsRippleUpdate;
   private VAO rippleVAO;
   private int rippleCount;
   private Vector3d rippleOffset = new Vector3d();
   private Vector2d tmpVec2 = new Vector2d();
   private Vector3d tmpVec3 = new Vector3d();

   public ProxyOceanLayer(short layerPosY) {
      this.oceanStorage = new Long2ObjectOpenHashMap();
      this.rippleParticles = new LinkedList<>();
      this.layerPosY = layerPosY;
   }

   public void update(double diff) {
      Iterator<RippleParticle> it = this.rippleParticles.iterator();

      while (it.hasNext()) {
         RippleParticle particle = it.next();
         particle.update();
         if (particle.lifetime < 0) {
            it.remove();
         }
      }

      this.needsRippleUpdate = true;
   }

   public double calculateYOffset(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return 0.0;
      } else {
         Vector2d position = this.tmpVec2.set(x - this.waveOffsetX, z - this.waveOffsetZ);
         float factor = this.bilinearInterpolationTexture(x, y, z);
         double waveHeight = WaveFunction.waveHeight(position, 13, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime());
         return this.warpFunction(oceanWorld, waveHeight, y);
      }
   }

   public boolean isInsideOceanWater(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return false;
      } else {
         Vector2d position = this.tmpVec2.set(x - this.waveOffsetX, z - this.waveOffsetZ);
         float factor = this.bilinearInterpolationTexture(x, y, z);
         double waveHeight = WaveFunction.waveHeight(position, 13, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime());
         double vanillaSurface = this.layerPosY + 0.8888888;
         double surface = vanillaSurface + waveHeight;
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         double distance = y - this.layerPosY;
         if (distance >= 0.0) {
            if (this.getOceanHeight(ix, iz) <= distance) {
               return false;
            }
         } else if (this.getOceanDepth(ix, iz) <= -distance) {
            return false;
         }

         return y < surface;
      }
   }

   public boolean isInsideTextureOceanRange(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return false;
      } else {
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         double distance = y - this.layerPosY;
         if (distance >= 0.0) {
            if (this.getOceanHeight(ix, iz) <= distance) {
               return false;
            }
         } else if (this.getOceanDepth(ix, iz) <= -distance) {
            return false;
         }

         return true;
      }
   }

   public boolean isInsideTextureOceanRangeNoWarp(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z, 2.0 + oceanWorld.getOceanHeight() * 0.5)) {
         return false;
      } else {
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         double distance = y - this.layerPosY;
         if (distance >= 0.0) {
            if (this.getOceanHeight(ix, iz) <= distance) {
               return false;
            }
         } else if (this.getOceanDepth(ix, iz) <= -distance) {
            return false;
         }

         return true;
      }
   }

   public Vector3d calculateWaveNormal(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return null;
      } else {
         Vector2d position = this.tmpVec2.set(x - this.waveOffsetX, z - this.waveOffsetZ);
         float factor = this.bilinearInterpolationTexture(x, y, z);
         double waveHeight = WaveFunction.waveHeight(position, 13, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime());
         double vanillaSurface = this.layerPosY + 0.8888888;
         double surface = vanillaSurface + waveHeight;
         if (y > surface && y > vanillaSurface) {
            return null;
         } else {
            Vector3d waveNormal = WaveFunction.waveNormal(position, factor, oceanWorld.getOceanHeight(), oceanWorld.getOceanTime(), this.tmpVec3);
            waveNormal.y = 1.0;
            if (y < vanillaSurface) {
               if (y > surface) {
                  waveNormal.y = -waveNormal.y;
               } else {
                  waveNormal.y = 0.0;
               }
            }

            double warpFunction = this.warpFunction(oceanWorld, 1.0, y);
            waveNormal.x *= warpFunction;
            waveNormal.z *= warpFunction;
            return waveNormal;
         }
      }
   }

   public boolean isInsideOceanRange(OceanWorld oceanWorld, double x, double y, double z) {
      return this.isInsideOceanRange(oceanWorld, x, y, z, oceanWorld.getOceanHeight() * 0.5);
   }

   public boolean isInsideOceanRange(OceanWorld oceanWorld, double x, double y, double z, double oceanHeight) {
      if (this.waveOffsetX != 2147483647 && this.oceanSurface != null && this.oceanSurface.textureData != null) {
         double distance = y - this.layerPosY;
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         return !(Math.abs(distance) > oceanHeight) && this.getOcean(ix, iz) != 0;
      } else {
         return false;
      }
   }

   private double warpFunction(OceanWorld oceanWorld, double waveHeight, double y) {
      double distance = y - this.layerPosY;
      double halfHeight = oceanWorld.getOceanHeight() * 0.5 - 1.0;
      double warp = 1.0 - net.diebuddies.math.Math.clamp(Math.abs(distance) - 1.0, 0.0, halfHeight) / halfHeight;
      return waveHeight * warp;
   }

   public float bilinearInterpolationTexture(double worldX, double worldY, double worldZ) {
      double distance = worldY - this.layerPosY;
      int ix = Mth.floor(worldX);
      int iz = Mth.floor(worldZ);
      int properOffsetX = this.waveOffsetX - this.oceanSurface.offsetX;
      int properOffsetZ = this.waveOffsetZ - this.oceanSurface.offsetZ;
      int uvX = ix - properOffsetX;
      int uvY = iz - properOffsetZ;
      float fractUVX = (float)(worldX - properOffsetX - uvX);
      float fractUVY = (float)(worldZ - properOffsetZ - uvY);
      float data0 = this.textureData(uvX, uvY);
      float data1 = this.textureData(uvX + 1, uvY);
      float data2 = this.textureData(uvX, uvY + 1);
      float data3 = this.textureData(uvX + 1, uvY + 1);
      if (distance >= 0.0) {
         if (this.getOceanHeight(ix, iz) <= distance) {
            data0 = 0.0F;
            data1 = 0.0F;
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if (this.getOceanHeight(ix + 1, iz) <= distance) {
            data1 = 0.0F;
            data3 = 0.0F;
         }

         if (this.getOceanHeight(ix, iz + 1) <= distance) {
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if (this.getOceanHeight(ix + 1, iz + 1) <= distance) {
            data3 = 0.0F;
         }

         if (this.getOceanHeight(ix - 1, iz) <= distance) {
            data0 = 0.0F;
            data2 = 0.0F;
         }

         if (this.getOceanHeight(ix, iz - 1) <= distance) {
            data0 = 0.0F;
            data1 = 0.0F;
         }

         if (this.getOceanHeight(ix - 1, iz - 1) <= distance) {
            data0 = 0.0F;
         }

         if (this.getOceanHeight(ix - 1, iz + 1) <= distance) {
            data2 = 0.0F;
         }

         if (this.getOceanHeight(ix + 1, iz - 1) <= distance) {
            data1 = 0.0F;
         }
      } else {
         double depth = -distance;
         if (this.getOceanDepth(ix, iz) <= depth) {
            data0 = 0.0F;
            data1 = 0.0F;
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if (this.getOceanDepth(ix + 1, iz) <= depth) {
            data1 = 0.0F;
            data3 = 0.0F;
         }

         if (this.getOceanDepth(ix, iz + 1) <= depth) {
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if (this.getOceanDepth(ix + 1, iz + 1) <= depth) {
            data3 = 0.0F;
         }

         if (this.getOceanDepth(ix - 1, iz) <= depth) {
            data0 = 0.0F;
            data2 = 0.0F;
         }

         if (this.getOceanDepth(ix, iz - 1) <= depth) {
            data0 = 0.0F;
            data1 = 0.0F;
         }

         if (this.getOceanDepth(ix - 1, iz - 1) <= depth) {
            data0 = 0.0F;
         }

         if (this.getOceanDepth(ix - 1, iz + 1) <= depth) {
            data2 = 0.0F;
         }

         if (this.getOceanDepth(ix + 1, iz - 1) <= depth) {
            data1 = 0.0F;
         }
      }

      float data4 = org.joml.Math.lerp(data0, data1, fractUVX);
      float data5 = org.joml.Math.lerp(data2, data3, fractUVX);
      return org.joml.Math.lerp(data4, data5, fractUVY);
   }

   public float textureData(int x, int y) {
      if (this.oceanSurface.textureData.length == 1) {
         return (this.oceanSurface.textureData[0] & 255) / 255.0F;
      } else {
         x = net.diebuddies.math.Math.clamp(x, 0, this.oceanSurface.width - 1);
         y = net.diebuddies.math.Math.clamp(y, 0, this.oceanSurface.height - 1);
         return (this.oceanSurface.textureData[y * this.oceanSurface.width + x] & 255) / 255.0F;
      }
   }

   public Long2ObjectMap<ProxyOceanStorage> getOceanStorage() {
      return this.oceanStorage;
   }

   public int getOceanDepth(int x, int z) {
      ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : storage.depths.getData(x & 15, z & 15) & 0xFF;
   }

   public int getOceanHeight(int x, int z) {
      ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : storage.depths.getData(x & 15, z & 15) >> 8 & 0xFF;
   }

   public int getOcean(int x, int z) {
      ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : storage.blocks.getData(x & 15, z & 15);
   }

   public short getLayerPosY() {
      return this.layerPosY;
   }

   public void setWaveOffset(int offsetX, int offsetZ) {
      this.waveOffsetX = offsetX;
      this.waveOffsetZ = offsetZ;
   }

   public void setOceanSurface(OceanSurface oceanSurface) {
      this.oceanSurface = oceanSurface;
   }

   public OceanSurface getOceanSurface() {
      return this.oceanSurface;
   }

   public void addRippleParticle(RippleParticle particle) {
      if (this.rippleParticles.size() == 0) {
         this.rippleOffset.set(particle.x, particle.y, particle.z);
      }

      particle.x = particle.x - this.rippleOffset.x;
      particle.y = particle.y - this.rippleOffset.y;
      particle.z = particle.z - this.rippleOffset.z;
      particle.xo = particle.xo - this.rippleOffset.x;
      particle.yo = particle.yo - this.rippleOffset.y;
      particle.zo = particle.zo - this.rippleOffset.z;
      this.rippleParticles.addFirst(particle);
   }

   public Vector3d getRippleOffset() {
      return this.rippleOffset;
   }

   public LinkedList<RippleParticle> getRippleParticles() {
      return this.rippleParticles;
   }

   public VAO getRippleVAO() {
      return this.rippleVAO;
   }

   public void setRippleVAO(VAO rippleVAO) {
      this.rippleVAO = rippleVAO;
   }

   public void setRippleCount(int rippleCount) {
      this.rippleCount = rippleCount;
   }

   public int getRippleCount() {
      return this.rippleCount;
   }

   public boolean needsRippleUpdate() {
      return this.needsRippleUpdate;
   }

   public void setNeedsRippleUpdate(boolean needsRippleUpdate) {
      this.needsRippleUpdate = needsRippleUpdate;
   }

   public void destroy() {
      if (this.rippleVAO != null) {
         this.rippleVAO.destroy();
      }
   }
}
