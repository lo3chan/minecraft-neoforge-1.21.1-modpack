package net.astralya.hexalia.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import java.util.ArrayList;
import java.util.List;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.RootshaperItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class RootshaperEvents {
   private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

   private RootshaperEvents() {
   }

   public static void register() {
      InteractionEvent.LEFT_CLICK_BLOCK.register(RootshaperEvents::onLeftClickBlock);
      BlockEvent.BREAK.register(RootshaperEvents::onBlockBreak);
   }

   private static EventResult onLeftClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
      ItemStack stack = player.getItemInHand(hand);
      if (!stack.is((Item)ModItems.ROOTSHAPER.get())) {
         return EventResult.pass();
      } else {
         Level level = player.level();
         BlockState state = level.getBlockState(pos);
         int newMode = RootshaperItem.computeMode(state);
         int oldMode = RootshaperItem.getMode(stack);
         RootshaperItem.setMode(stack, newMode);
         if (newMode != oldMode && level.isClientSide()) {
            RootshaperItem.playMorphSound(level, player.blockPosition());
         }

         return EventResult.pass();
      }
   }

   private static EventResult onBlockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player, Object xp) {
      if (!BREAKING.get() && player.isShiftKeyDown()) {
         ItemStack stack = player.getMainHandItem();
         if (!(stack.getItem() instanceof RootshaperItem)) {
            return EventResult.pass();
         } else {
            Direction face = getPlayerFacing(player);
            BREAKING.set(true);

            try {
               for (BlockPos adjacentPos : get3x3Positions(pos, face)) {
                  if (!adjacentPos.equals(pos)) {
                     BlockState adjacentState = level.getBlockState(adjacentPos);
                     if (!adjacentState.isAir() && !(adjacentState.getDestroySpeed(level, adjacentPos) < 0.0F) && stack.isCorrectToolForDrops(adjacentState)) {
                        player.gameMode.destroyBlock(adjacentPos);
                     }
                  }
               }
            } finally {
               BREAKING.set(false);
            }

            return EventResult.pass();
         }
      } else {
         return EventResult.pass();
      }
   }

   private static Direction getPlayerFacing(ServerPlayer player) {
      Vec3 look = player.getLookAngle();
      double x = Math.abs(look.x);
      double y = Math.abs(look.y);
      double z = Math.abs(look.z);
      if (y > x && y > z) {
         return look.y > 0.0 ? Direction.UP : Direction.DOWN;
      } else if (x > z) {
         return look.x > 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return look.z > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   private static List<BlockPos> get3x3Positions(BlockPos center, Direction face) {
      List<BlockPos> positions = new ArrayList<>();
      Direction[] axes = getPerpendicularAxes(face);
      Direction first = axes[0];
      Direction second = axes[1];

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            positions.add(center.relative(first, i).relative(second, j));
         }
      }

      return positions;
   }

   private static Direction[] getPerpendicularAxes(Direction face) {
      return switch (face) {
         case UP, DOWN -> new Direction[]{Direction.NORTH, Direction.EAST};
         case NORTH, SOUTH -> new Direction[]{Direction.EAST, Direction.UP};
         case EAST, WEST -> new Direction[]{Direction.NORTH, Direction.UP};
         default -> throw new MatchException(null, null);
      };
   }
}
