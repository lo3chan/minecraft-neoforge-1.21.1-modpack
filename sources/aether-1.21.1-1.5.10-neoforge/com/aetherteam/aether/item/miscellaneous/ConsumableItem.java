package com.aetherteam.aether.item.miscellaneous;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ConsumableItem {
   default void consume(Item item, ItemStack stack, LivingEntity user) {
      if (user instanceof Player player) {
         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }

         if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(item));
         }
      }
   }
}
