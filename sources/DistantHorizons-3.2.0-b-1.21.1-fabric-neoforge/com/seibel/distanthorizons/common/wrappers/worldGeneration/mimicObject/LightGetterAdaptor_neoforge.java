package com.seibel.distanthorizons.common.wrappers.worldGeneration.mimicObject;

import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IStarlightAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class LightGetterAdaptor_neoforge implements LightChunkGetter {
   private final BlockGetter heightGetter;
   public DhLitWorldGenRegion_neoforge genRegion = null;
   final boolean shouldReturnNull;

   public LightGetterAdaptor_neoforge(BlockGetter heightAccessor) {
      this.heightGetter = heightAccessor;
      this.shouldReturnNull = ModAccessorInjector.INSTANCE.get(IStarlightAccessor.class) != null;
   }

   public void setRegion(DhLitWorldGenRegion_neoforge region) {
      this.genRegion = region;
   }

   public LightChunk getChunkForLighting(int chunkX, int chunkZ) {
      if (this.genRegion == null) {
         throw new IllegalStateException("World Gen region has not been set!");
      } else {
         return this.genRegion.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
      }
   }

   public BlockGetter getLevel() {
      return (BlockGetter)(this.shouldReturnNull ? null : (this.genRegion != null ? this.genRegion : this.heightGetter));
   }

   public LevelHeightAccessor getLevelHeightAccessor() {
      return this.heightGetter;
   }
}
