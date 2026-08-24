package com.aetherteam.aether.item.miscellaneous;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class LifeShardItem extends Item implements ConsumableItem {
   public LifeShardItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (!player.isCreative()) {
         AetherPlayerAttachment aetherPlayer = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         if (aetherPlayer.getLifeShardCount() < aetherPlayer.getLifeShardLimit()) {
            player.swing(hand);
            if (!level.isClientSide()) {
               this.consume(this, heldStack, player);
               aetherPlayer.setSynched(player.getId(), Direction.CLIENT, "setLifeShardCount", aetherPlayer.getLifeShardCount() + 1);
               return InteractionResultHolder.consume(heldStack);
            }

            return InteractionResultHolder.success(heldStack);
         }

         if (aetherPlayer.getLifeShardCount() >= aetherPlayer.getLifeShardLimit()) {
            player.displayClientMessage(Component.translatable("aether.life_shard_limit", new Object[]{aetherPlayer.getLifeShardLimit()}), true);
         }
      }

      return InteractionResultHolder.pass(heldStack);
   }
}
