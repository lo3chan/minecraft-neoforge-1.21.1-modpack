package alternate.current.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

class LevelHelper {
   static boolean setWireState(ServerLevel level, BlockPos pos, BlockState state, boolean updateNeighborShapes) {
      int y = pos.getY();
      if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
         int x = pos.getX();
         int z = pos.getZ();
         int index = level.getSectionIndex(y);
         ChunkAccess chunk = level.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
         LevelChunkSection section = chunk.getSections()[index];
         if (section == null) {
            return false;
         } else {
            BlockState prevState = section.setBlockState(x & 15, y & 15, z & 15, state);
            if (state == prevState) {
               return false;
            } else {
               level.getChunkSource().blockChanged(pos);
               chunk.setUnsaved(true);
               if (updateNeighborShapes) {
                  prevState.updateIndirectNeighbourShapes(level, pos, 2);
                  state.updateNeighbourShapes(level, pos, 2);
                  state.updateIndirectNeighbourShapes(level, pos, 2);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }
}
