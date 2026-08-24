package alternate.current.wire;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Node {
   private static final int CONDUCTOR = 1;
   private static final int SOURCE = 2;
   final ServerLevel level;
   final Node[] neighbors;
   BlockPos pos;
   BlockState state;
   boolean invalid;
   private int flags;
   Node prev_node;
   Node next_node;
   int priority;
   WireNode neighborWire;

   Node(ServerLevel level) {
      this.level = level;
      this.neighbors = new Node[WireHandler.Directions.ALL.length];
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof Node node) ? false : this.level == node.level && this.pos.equals(node.pos);
      }
   }

   @Override
   public int hashCode() {
      return this.pos.hashCode();
   }

   Node set(BlockPos pos, BlockState state, boolean clearNeighbors) {
      if (state.is(Blocks.REDSTONE_WIRE)) {
         throw new IllegalStateException("Cannot update a regular Node to a WireNode!");
      } else {
         if (clearNeighbors) {
            Arrays.fill(this.neighbors, null);
         }

         this.pos = pos.immutable();
         this.state = state;
         this.invalid = false;
         this.flags = 0;
         if (this.state.isRedstoneConductor(this.level, this.pos)) {
            this.flags |= 1;
         }

         if (this.state.isSignalSource()) {
            this.flags |= 2;
         }

         return this;
      }
   }

   int priority() {
      return this.neighborWire.priority;
   }

   public boolean isWire() {
      return false;
   }

   public boolean isConductor() {
      return (this.flags & 1) != 0;
   }

   public boolean isSignalSource() {
      return (this.flags & 2) != 0;
   }

   public WireNode asWire() {
      throw new UnsupportedOperationException("Not a WireNode!");
   }
}
