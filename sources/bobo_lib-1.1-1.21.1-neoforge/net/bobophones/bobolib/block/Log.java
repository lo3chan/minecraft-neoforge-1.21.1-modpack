package net.bobophones.bobolib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;

public class Log extends RotatedPillarBlock {
   private final Block stripped;
   private final boolean flammable;
   private final int flammability;
   private final int fire_spread;

   public Log(Properties props) {
      super(props);
      this.stripped = null;
      this.flammable = true;
      this.flammability = 20;
      this.fire_spread = 5;
   }

   public Log(Properties props, Block stripped) {
      super(props);
      this.stripped = stripped;
      this.flammable = true;
      this.flammability = 20;
      this.fire_spread = 5;
   }

   public Log(Properties props, Block stripped, boolean flammable, int flammability, int fire_spread) {
      super(props);
      this.stripped = stripped;
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

   public BlockState getToolModifiedState(BlockState state, UseOnContext ctx, ItemAbility item_ability, boolean simulate) {
      return this.stripped != null && ctx.getItemInHand().getItem() instanceof AxeItem
         ? (BlockState)this.stripped.defaultBlockState().setValue(AXIS, (Axis)state.getValue(AXIS))
         : super.getToolModifiedState(state, ctx, item_ability, simulate);
   }
}
