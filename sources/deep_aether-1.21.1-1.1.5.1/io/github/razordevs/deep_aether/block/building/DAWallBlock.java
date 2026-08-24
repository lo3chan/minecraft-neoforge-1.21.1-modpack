package io.github.razordevs.deep_aether.block.building;

import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class DAWallBlock extends WallBlock {
   public DAWallBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      if (context.getItemInHand().getItem() instanceof AxeItem) {
         if (state.is((Block)DABlocks.ROSEROOT_WOOD_WALL.get())) {
            return ((Block)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.ROSEROOT_LOG_WALL.get())) {
            return ((WallBlock)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.YAGROOT_WOOD_WALL.get())) {
            return ((Block)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.YAGROOT_LOG_WALL.get())) {
            return ((WallBlock)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.CRUDEROOT_WOOD_WALL.get())) {
            return ((Block)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.CRUDEROOT_LOG_WALL.get())) {
            return ((WallBlock)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.CONBERRY_WOOD_WALL.get())) {
            return ((Block)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.CONBERRY_LOG_WALL.get())) {
            return ((WallBlock)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.SUNROOT_WOOD_WALL.get())) {
            return ((Block)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get()).defaultBlockState();
         }

         if (state.is((Block)DABlocks.SUNROOT_LOG_WALL.get())) {
            return ((WallBlock)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get()).defaultBlockState();
         }
      }

      return super.getToolModifiedState(state, context, itemAbility, simulate);
   }
}
