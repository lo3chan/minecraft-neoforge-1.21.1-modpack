package net.joefoxe.hexerei.block.connected;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockConnectivity {
   private Map<Block, BlockConnectivity.Entry> entries = new IdentityHashMap<>();

   public BlockConnectivity.Entry get(BlockState blockState) {
      return this.entries.get(blockState.getBlock());
   }

   public void makeBlock(Block block, CTSpriteShiftEntry casing) {
      new BlockConnectivity.Entry(block, casing, (s, f) -> true).register();
   }

   public void make(Block block, CTSpriteShiftEntry casing) {
      new BlockConnectivity.Entry(block, casing, (s, f) -> true).register();
   }

   public void make(Block block, CTSpriteShiftEntry casing, BiPredicate<BlockState, Direction> predicate) {
      new BlockConnectivity.Entry(block, casing, predicate).register();
   }

   public class Entry {
      private Block block;
      private CTSpriteShiftEntry ctSpriteShiftEntry;
      private BiPredicate<BlockState, Direction> predicate;

      private Entry(Block block, CTSpriteShiftEntry ctSpriteShiftEntry, BiPredicate<BlockState, Direction> predicate) {
         this.block = block;
         this.ctSpriteShiftEntry = ctSpriteShiftEntry;
         this.predicate = predicate;
      }

      public CTSpriteShiftEntry getCTSpriteShiftEntry() {
         return this.ctSpriteShiftEntry;
      }

      public boolean isSideValid(BlockState state, Direction face) {
         return this.predicate.test(state, face);
      }

      public void register() {
         BlockConnectivity.this.entries.put(this.block, this);
      }
   }
}
