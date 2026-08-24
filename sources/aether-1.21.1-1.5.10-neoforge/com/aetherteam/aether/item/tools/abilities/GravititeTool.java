package com.aetherteam.aether.item.tools.abilities;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.miscellaneous.FloatingBlock;
import com.aetherteam.aether.entity.block.FloatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface GravititeTool {
   default boolean floatBlock(UseOnContext context) {
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      ItemStack itemStack = context.getItemInHand();
      BlockState blockState = level.getBlockState(blockPos);
      Player player = context.getPlayer();
      InteractionHand hand = context.getHand();
      if (itemStack.getItem() instanceof TieredItem tieredItem
         && player != null
         && !player.isShiftKeyDown()
         && (itemStack.getDestroySpeed(blockState) == tieredItem.getTier().getSpeed() || itemStack.isCorrectToolForDrops(blockState))
         && FloatingBlock.isFree(level.getBlockState(blockPos.above()))
         && level.getBlockEntity(blockPos) == null
         && blockState.getDestroySpeed(level, blockPos) >= 0.0F
         && !blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
         && !blockState.is(AetherTags.Blocks.GRAVITITE_ABILITY_BLACKLIST)) {
         if (!level.isClientSide()) {
            FloatingBlockEntity entity = new FloatingBlockEntity(level, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, blockState);
            entity.setNatural(false);
            if (blockState.is(BlockTags.ANVIL)) {
               entity.setHurtsEntities(2.0F, 40);
            }

            level.addFreshEntity(entity);
            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            itemStack.hurtAndBreak(4, player, LivingEntity.getSlotForHand(hand));
         } else {
            player.swing(hand);
         }

         return true;
      } else {
         return false;
      }
   }
}
