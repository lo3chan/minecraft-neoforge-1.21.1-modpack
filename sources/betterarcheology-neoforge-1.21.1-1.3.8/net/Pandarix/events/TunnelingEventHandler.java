package net.Pandarix.events;

import dev.architectury.event.EventResult;
import dev.architectury.utils.value.IntValue;
import net.Pandarix.BACommon;
import net.Pandarix.config.BAConfig;
import net.Pandarix.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TunnelingEventHandler {
   public static EventResult handleTunneling(Level level, BlockPos blockPos, BlockState blockState, ServerPlayer serverPlayer, IntValue intValue) {
      if (level instanceof ServerLevel serverLevel && serverPlayer != null) {
         try {
            ItemStack stack = serverPlayer.getMainHandItem();
            Reference<Enchantment> tunneling = level.registryAccess()
               .asGetterLookup()
               .lookupOrThrow(Registries.ENCHANTMENT)
               .getOrThrow(ModEnchantments.TUNNELING_KEY);
            if (BAConfig.artifactsEnabled
               && BAConfig.tunnelingEnabled
               && !serverPlayer.isShiftKeyDown()
               && !stack.isEmpty()
               && stack.isEnchanted()
               && EnchantmentHelper.getItemEnchantmentLevel(tunneling, stack) >= 1) {
               BlockPos ba$downPos = blockPos.below();
               BlockState ba$blockStateBelow = level.getBlockState(ba$downPos);
               if (ba$blockStateBelow instanceof GameMasterBlock) {
                  return EventResult.pass();
               }

               boolean correctTool = BAConfig.tunnelingEffectiveTool
                  ? stack.isCorrectToolForDrops(blockState) && stack.isCorrectToolForDrops(ba$blockStateBelow)
                  : true;
               boolean inHardnessTolerance = Math.abs(
                     level.getBlockState(ba$downPos).getDestroySpeed(level, ba$downPos) - level.getBlockState(blockPos).getDestroySpeed(level, blockPos)
                  )
                  <= BAConfig.tunnelingTolerance;
               if (correctTool && inHardnessTolerance) {
                  if (serverPlayer.blockActionRestricted(serverLevel, ba$downPos, serverPlayer.gameMode.getGameModeForPlayer())) {
                     return null;
                  }

                  if (serverPlayer.isCreative()) {
                     performBreak(serverLevel, ba$downPos, ba$blockStateBelow);
                  } else {
                     ItemStack copiedStack = stack.copy();
                     stack.hurtAndBreak(1, serverPlayer, serverPlayer.getEquipmentSlotForItem(stack));
                     boolean successfulBreak = performBreak(serverLevel, ba$downPos, ba$blockStateBelow);
                     if (successfulBreak) {
                        BlockEntity blockEntity = serverLevel.getBlockEntity(ba$downPos);
                        ba$blockStateBelow.getBlock().playerDestroy(serverLevel, serverPlayer, ba$downPos, ba$blockStateBelow, blockEntity, copiedStack);
                     }
                  }
               }
            }
         } catch (Exception var15) {
            BACommon.LOGGER.error("Could not find Tunneling in the Enchantment Registries!", var15);
         }
      }

      return EventResult.pass();
   }

   private static boolean performBreak(ServerLevel serverLevel, BlockPos pos, BlockState blockState) {
      boolean removed = serverLevel.removeBlock(pos, false);
      if (removed) {
         blockState.getBlock().destroy(serverLevel, pos, blockState);
      }

      return removed;
   }
}
