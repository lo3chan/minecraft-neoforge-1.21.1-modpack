package net.joefoxe.hexerei.block.connected;

import javax.annotation.Nullable;
import net.joefoxe.hexerei.item.custom.CleaningClothItem;
import net.joefoxe.hexerei.item.custom.WaxBlendItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;

public class WaxedGlassPaneBlock extends IronBarsBlock implements Waxed {
   public WaxedGlassPaneBlock(Properties p_55926_) {
      super(p_55926_);
   }

   public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
      return super.skipRendering(state, adjacentBlockState, side);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      return this.getUnWaxed(state, context, itemAbility);
   }

   @Override
   public BlockState getUnWaxed(BlockState state, UseOnContext context, ItemAbility itemAbility) {
      boolean cloth = CleaningClothItem.CLOTH_WAX_OFF.equals(itemAbility);
      WaxBlendItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock());
      BlockState toReturn = state;
      if (cloth) {
         toReturn = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((Block)WaxBlendItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock()))
                        .defaultBlockState()
                        .setValue(NORTH, (Boolean)state.getValue(NORTH)))
                     .setValue(SOUTH, (Boolean)state.getValue(SOUTH)))
                  .setValue(EAST, (Boolean)state.getValue(EAST)))
               .setValue(WEST, (Boolean)state.getValue(WEST)))
            .setValue(WATERLOGGED, (Boolean)state.getValue(WATERLOGGED));
      }

      return toReturn;
   }
}
