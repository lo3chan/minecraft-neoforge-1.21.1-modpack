package net.diebuddies.physics.snow.thread;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.math.Math;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowProperty;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.WorldContouring;
import net.diebuddies.physics.snow.storage.StorageContainer;
import net.diebuddies.physics.snow.storage.StorageSimple;
import net.diebuddies.physics.snow.storage.StorageVanilla;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

public class SnowChunkCreator implements ChunkCreator {
   private SnowWorld snowWorld;
   private Map<Vector3i, BlockState> snow;
   private int chunkX;
   private int chunkY;
   private int chunkZ;

   public SnowChunkCreator(SnowWorld snowWorld, Map<Vector3i, BlockState> snow, int chunkX, int chunkY, int chunkZ) {
      this.snowWorld = snowWorld;
      this.snow = snow;
      this.chunkX = chunkX;
      this.chunkY = chunkY;
      this.chunkZ = chunkZ;
   }

   @Override
   public ChunkContouring create() {
      WorldContouring contouring = this.snowWorld.contouring;
      StorageContainer lightStorage = new StorageVanilla((byte)0, IChunk.CHUNK_MULTIPLE);
      StorageContainer storage = new StorageSimple((byte)-127, IChunk.CHUNK_VOLUME);
      StorageContainer rawModulation = contouring.getModulationLayerRaw();
      if (this.snow.size() > 0) {
         byte[] modulation = this.snowWorld.contouring.getModulationLayer().getArray();
         byte[] data = Arrays.copyOf(modulation, modulation.length);
         storage = new StorageSimple(data, IChunk.CHUNK_VOLUME);
      }

      for (Entry<Vector3i, BlockState> entry : this.snow.entrySet()) {
         Vector3i pos = entry.getKey();
         BlockState state = entry.getValue();
         this.updateBlock(storage, rawModulation, pos.x, pos.y, pos.z, state);
      }

      return new ChunkContouring(contouring.getPlayerPosition(), contouring, this.chunkX, this.chunkY, this.chunkZ, storage, lightStorage);
   }

   private void updateBlock(StorageContainer storage, StorageContainer rawModulation, int rx, int ry, int rz, BlockState state) {
      if (state.getBlock() == Blocks.SNOW) {
         int snowLayers = (Integer)state.getValue(SnowLayerBlock.LAYERS);

         for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
            int currentMaxLayer = yo * (8 / IChunk.CHUNK_MULTIPLE);
            double perc = Math.remapClamp(java.lang.Math.ceil((double)(snowLayers - currentMaxLayer) / (8 / IChunk.CHUNK_MULTIPLE)), 0.0, 1.0, -1.0, 1.0);
            byte snow = (byte)(perc * 127.0);

            for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + snow, (byte)-127, (byte)127));
               }
            }
         }
      } else {
         for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + 127, (byte)-127, (byte)127));
               }
            }
         }
      }
   }

   public static void updateBlock(WorldContouring contouring, ChunkContouring storage, int rx, int ry, int rz, BlockState state) {
      StorageSimple rawModulation = contouring.getModulationLayerRaw();
      StorageSimple modulation = contouring.getModulationLayer();
      SnowProperty property = SnowSearcher.getSnowProperty(state);
      if (property == null) {
         for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, modulation.getData(rx + xo, ry + yo, rz + zo));
               }
            }
         }
      } else if (property == SnowProperty.LAYER) {
         int snowLayers = (Integer)state.getValue(SnowLayerBlock.LAYERS);

         for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
            int currentMaxLayer = yo * (8 / IChunk.CHUNK_MULTIPLE);
            double perc = Math.remapClamp(java.lang.Math.ceil((double)(snowLayers - currentMaxLayer) / (8 / IChunk.CHUNK_MULTIPLE)), 0.0, 1.0, -1.0, 1.0);
            byte snow = (byte)(perc * 127.0);

            for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + snow, (byte)-127, (byte)127));
               }
            }
         }
      } else if (property == SnowProperty.FULL) {
         for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(rawModulation.getData(rx + xo, ry + yo, rz + zo) + 127, (byte)-127, (byte)127));
               }
            }
         }
      }
   }

   @Override
   public int getX() {
      return this.chunkX;
   }

   @Override
   public int getY() {
      return this.chunkY;
   }

   @Override
   public int getZ() {
      return this.chunkZ;
   }
}
