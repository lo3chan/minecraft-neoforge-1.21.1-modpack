package com.aetherteam.aether.item.miscellaneous.bucket;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.miscellaneous.ConsumableItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;

public class SkyrootMilkBucketItem extends MilkBucketItem implements ConsumableItem {
   public SkyrootMilkBucketItem(Properties properties) {
      super(properties);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      if (!level.isClientSide()) {
         user.removeEffectsCuredBy(EffectCures.MILK);
      }

      this.consume(this, stack, user);
      return stack.isEmpty() ? new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get()) : stack;
   }
}
