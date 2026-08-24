package at.petrak.hexcasting.common.blocks.akashic;

import at.petrak.hexcasting.api.misc.TriPredicate;
import java.util.ArrayDeque;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface AkashicFloodfiller {
   default boolean canBeFloodedThrough(BlockPos pos, BlockState state, Level world) {
      return true;
   }

   @Nullable
   static BlockPos floodFillFor(BlockPos start, Level world, TriPredicate<BlockPos, BlockState, Level> isTarget) {
      return floodFillFor(start, world, 0.0F, isTarget, 128);
   }

   @Nullable
   static BlockPos floodFillFor(BlockPos start, Level world, float skipChance, TriPredicate<BlockPos, BlockState, Level> isTarget, int maxRange) {
      HashSet<BlockPos> seenBlocks = new HashSet<>();
      ArrayDeque<BlockPos> todo = new ArrayDeque<>();
      todo.add(start);
      HashSet<BlockPos> skippedBlocks = new HashSet<>();

      while (!todo.isEmpty()) {
         BlockPos here = todo.remove();

         for (Direction dir : Direction.values()) {
            BlockPos neighbor = here.relative(dir);
            if (!(neighbor.distSqr(start) > maxRange * maxRange) && seenBlocks.add(neighbor)) {
               BlockState bs = world.getBlockState(neighbor);
               if (isTarget.test(neighbor, bs, world)) {
                  if (world.random.nextFloat() > skipChance) {
                     return neighbor;
                  }

                  skippedBlocks.add(neighbor);
               }

               if (canItBeFloodedThrough(neighbor, bs, world)) {
                  todo.add(neighbor);
               }
            }
         }
      }

      return !skippedBlocks.isEmpty() ? skippedBlocks.iterator().next() : null;
   }

   static boolean canItBeFloodedThrough(BlockPos pos, BlockState state, Level world) {
      return state.getBlock() instanceof AkashicFloodfiller flooder ? flooder.canBeFloodedThrough(pos, state, world) : false;
   }
}
