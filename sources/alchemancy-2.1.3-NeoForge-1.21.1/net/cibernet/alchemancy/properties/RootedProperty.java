package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.blocks.RootedItemBlock;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class RootedProperty extends Property {
   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      Level level = event.getLevel();
      ItemStack stack = event.getItemStack();
      BlockPos pos = event.getPos();
      BlockPlaceContext context = new BlockPlaceContext(event.getUseOnContext());
      if (!event.getLevel().getBlockState(pos).canBeReplaced(context)) {
         pos = pos.relative(event.getFace() == null ? Direction.UP : event.getFace());
      }

      if (((RootedItemBlock)AlchemancyBlocks.ROOTED_ITEM.get()).mayPlaceOn(level.getBlockState(pos.below()), level, pos.below())
         && event.getLevel().getBlockState(pos).canBeReplaced(context)) {
         BlockState rootState = ((RootedItemBlock)AlchemancyBlocks.ROOTED_ITEM.get()).getStateForPlacement(context);
         level.setBlock(pos, rootState, 3);
         RootedItemBlockEntity root = new RootedItemBlockEntity(pos, rootState);
         root.setItem(stack.split(1));
         level.setBlockEntity(root);
         event.setCanceled(true);
         event.cancelWithResult(ItemInteractionResult.SUCCESS);
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      ItemStack stack = root.getItem();
      if (canRepair(stack) && root.getTickCount() % 100 == 0) {
         repairItem(stack, 1);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 11369829;
   }
}
