package io.github.razordevs.deep_aether.world.structure;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;

public class HeightSpawningChecks {
   public boolean checkHeight(GenerationContext context, int minY, int maxY) {
      ChunkPos chunkpos = context.chunkPos();
      int posTest = context.chunkGenerator()
         .getFirstOccupiedHeight(
            chunkpos.getWorldPosition().getX(), chunkpos.getWorldPosition().getZ(), Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()
         );
      return posTest > minY && posTest < maxY;
   }
}
