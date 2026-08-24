package com.aetherteam.aether.item.miscellaneous.bucket;

import com.aetherteam.aether.item.AetherItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class SkyrootMobBucketItem extends MobBucketItem {
   public SkyrootMobBucketItem(EntityType<?> entitySupplier, Fluid fluidSupplier, SoundEvent soundSupplier, Properties properties) {
      super(entitySupplier, fluidSupplier, soundSupplier, properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
      if (((ItemStack)result.getObject()).is(Items.BUCKET)) {
         result = InteractionResultHolder.sidedSuccess(new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get()), level.isClientSide());
      }

      return result;
   }
}
