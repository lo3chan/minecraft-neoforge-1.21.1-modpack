package net.bobophones.bobolib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class Leaves extends LeavesBlock {
   private final boolean flammable;
   private final int flammability;
   private final int fire_spread;

   public Leaves(Properties props) {
      this(props, true, 60, 30);
   }

   public Leaves(Properties props, boolean flammable, int flammability, int fire_spread) {
      super(props);
      this.flammable = flammable;
      this.flammability = flammability;
      this.fire_spread = fire_spread;
   }

   public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
      return this.flammable;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
      return this.flammability;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
      return this.fire_spread;
   }
}
