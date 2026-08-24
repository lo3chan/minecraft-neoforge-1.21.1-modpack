package net.mehvahdjukaar.moonlight.api.block;

import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundParticleAroundBlockMessage;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface IGlowable {
   boolean isGlowing();

   void setGlowing(boolean var1);

   default ItemInteractionResult tryGlowingWithItem(Level level, BlockPos pos, Player player, ItemStack stack) {
      if (stack.is(Items.GLOW_INK_SAC)) {
         if (this.isGlowing()) {
            level.playSound(player, pos, SoundEvents.WAXED_SIGN_INTERACT_FAIL, SoundSource.BLOCKS);
            return ItemInteractionResult.FAIL;
         } else {
            level.playSound(player, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS);
            stack.consume(1, player);
            this.setGlowing(true);
            if (player instanceof ServerPlayer serverPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
               player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
               NetworkHelper.sendToAllClientPlayersInParticleRange(
                  serverPlayer.serverLevel(), pos, new ClientBoundParticleAroundBlockMessage(pos, ClientBoundParticleAroundBlockMessage.Kind.GLOW_ON)
               );
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }
}
