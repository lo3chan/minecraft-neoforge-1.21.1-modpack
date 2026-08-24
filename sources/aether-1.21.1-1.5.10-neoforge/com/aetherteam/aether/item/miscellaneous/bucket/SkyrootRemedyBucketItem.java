package com.aetherteam.aether.item.miscellaneous.bucket;

import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.miscellaneous.ConsumableItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class SkyrootRemedyBucketItem extends Item implements ConsumableItem {
   public SkyrootRemedyBucketItem(Properties properties) {
      super(properties);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      if (!level.isClientSide()) {
         user.addEffect(new MobEffectInstance(AetherEffects.REMEDY, 200, 0, false, false, true));
      }

      this.consume(this, stack, user);
      return stack.isEmpty() ? new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get()) : stack;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      return ItemUtils.startUsingInstantly(worldIn, playerIn, handIn);
   }
}
