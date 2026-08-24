package com.aetherteam.aether.item.miscellaneous;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.portal.AetherPortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AetherPortalItem extends Item {
   public AetherPortalItem(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext context) {
      Player player = context.getPlayer();
      if (player != null && this.createPortalFrame(context)) {
         player.swing(context.getHand());
         if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.FAIL;
      }
   }

   private boolean createPortalFrame(UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      if (!level.getBlockState(pos).canBeReplaced(new BlockPlaceContext(context))) {
         pos = pos.relative(context.getClickedFace());
      }

      Axis axis = context.getHorizontalDirection().getAxis();

      for (int h = -1; h < 3; h++) {
         for (int v = pos.getY(); v < pos.getY() + 5; v++) {
            BlockPos truePos = axis == Axis.X ? new BlockPos(pos.getX(), v, pos.getZ() + h) : new BlockPos(pos.getX() + h, v, pos.getZ());
            if (!level.getBlockState(truePos).canBeReplaced(new BlockPlaceContext(context))) {
               return false;
            }
         }
      }

      for (int h = -1; h < 3; h++) {
         for (int vx = pos.getY(); vx < pos.getY() + 5; vx++) {
            BlockPos truePos = axis == Axis.X ? new BlockPos(pos.getX(), vx, pos.getZ() + h) : new BlockPos(pos.getX() + h, vx, pos.getZ());
            level.setBlockAndUpdate(truePos, Blocks.GLOWSTONE.defaultBlockState());
         }
      }

      for (int h = 0; h < 2; h++) {
         for (int vx = pos.getY() + 1; vx < pos.getY() + 4; vx++) {
            BlockPos truePos = axis == Axis.X ? new BlockPos(pos.getX(), vx, pos.getZ() + h) : new BlockPos(pos.getX() + h, vx, pos.getZ());
            Axis trueAxis = axis == Axis.X ? Axis.Z : Axis.X;
            level.setBlock(
               truePos, (BlockState)((AetherPortalBlock)AetherBlocks.AETHER_PORTAL.get()).defaultBlockState().setValue(AetherPortalBlock.AXIS, trueAxis), 18
            );
         }
      }

      return true;
   }
}
