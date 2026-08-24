package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class OceanSurface {
   public Long2ObjectMap<OceanMesh> meshes = new Long2ObjectOpenHashMap();
   @Nullable
   public byte[] textureData;
   public int width;
   public int height;
   public int offsetX;
   public int offsetZ;
   public ProxyOceanLayer oceanLayer;
   public boolean removeAllMeshes;
   public boolean singleMesh;

   public OceanSurface(byte[] textureData, int width, int height, int offsetX, int offsetZ, ProxyOceanLayer proxy) {
      this.oceanLayer = proxy;
      this.textureData = textureData;
      this.width = width;
      this.height = height;
      this.offsetX = offsetX;
      this.offsetZ = offsetZ;
   }

   public OceanSurface(ProxyOceanLayer proxy, boolean removeAllMeshes) {
      this.oceanLayer = proxy;
      this.removeAllMeshes = removeAllMeshes;
   }

   public void set(OceanSurface surface) {
      this.textureData = surface.textureData;
      this.width = surface.width;
      this.height = surface.height;
      this.offsetX = surface.offsetX;
      this.offsetZ = surface.offsetZ;
      this.singleMesh = surface.singleMesh;
   }

   public OceanSurface addOceanMesh(int chunkX, int layerY, int chunkZ, OceanMesh mesh) {
      this.meshes.put(BlockPos.asLong(chunkX, layerY, chunkZ), mesh);
      return this;
   }

   public Long2ObjectMap<OceanMesh> getMeshes() {
      return this.meshes;
   }
}
