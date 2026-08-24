package com.aetherteam.aether.item.food;

import com.aetherteam.aether.effect.AetherEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class WhiteAppleItem extends Item {
   public WhiteAppleItem() {
      super(new Properties().food(AetherFoods.WHITE_APPLE));
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      if (!level.isClientSide()) {
         user.addEffect(new MobEffectInstance(AetherEffects.REMEDY, 300, 0, false, false, true));
      }

      if (user instanceof Player player && player instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
         serverPlayer.awardStat(Stats.ITEM_USED.get(this));
      }

      return super.finishUsingItem(stack, level, user);
   }
}
