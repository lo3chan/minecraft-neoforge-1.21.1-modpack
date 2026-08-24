package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ItemBloodSprayer extends Item {
   public static final Predicate<ItemStack> IS_BLOOD = stack -> stack.getItem() == AMItemRegistry.BLOOD_SAC.get();

   public ItemBloodSprayer(Properties properties) {
      super(properties);
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return this.getUseDuration(stack);
   }

   public int getUseDuration(ItemStack stack) {
      return isUsable(stack) ? 2147483647 : 0;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BOW;
   }

   public static boolean isUsable(ItemStack stack) {
      return stack.getDamageValue() < stack.getMaxDamage() - 1;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      playerIn.startUsingItem(handIn);
      if (!isUsable(itemstack)) {
         ItemStack ammo = this.findAmmo(playerIn);
         boolean flag = playerIn.isCreative();
         if (!ammo.isEmpty()) {
            ammo.shrink(1);
            flag = true;
         }

         if (flag) {
            itemstack.setDamageValue(0);
         }
      }

      return AMCompat.consume(itemstack);
   }

   public ItemStack findAmmo(Player entity) {
      if (entity.isCreative()) {
         return ItemStack.EMPTY;
      } else {
         for (int i = 0; i < entity.getInventory().getContainerSize(); i++) {
            ItemStack itemstack1 = entity.getInventory().getItem(i);
            if (IS_BLOOD.test(itemstack1)) {
               return itemstack1;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.isSameItem(oldStack, newStack);
   }

   public boolean isBarVisible(ItemStack itemStack) {
      return super.isBarVisible(itemStack) && isUsable(itemStack);
   }

   public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
      if (isUsable(stack)) {
         if (count % 2 == 0) {
            boolean left = false;
            if (livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntityIn.getMainArm() == HumanoidArm.RIGHT
               || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntityIn.getMainArm() == HumanoidArm.LEFT) {
               left = true;
            }

            EntityMosquitoSpit blood = new EntityMosquitoSpit(worldIn, livingEntityIn, !left);
            Vec3 vector3d = livingEntityIn.getViewVector(1.0F);
            RandomSource rand = worldIn.getRandom();
            livingEntityIn.gameEvent(GameEvent.ITEM_INTERACT_START);
            livingEntityIn.playSound(SoundEvents.LAVA_POP, 1.0F, 1.2F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
            blood.shoot(vector3d.x(), vector3d.y(), vector3d.z(), 1.0F, 10.0F);
            if (!worldIn.isClientSide()) {
               worldIn.addFreshEntity(blood);
            }

            AMCompat.hurtAndBreak(stack, 1, livingEntityIn, livingEntityIn.getUsedItemHand());
         }
      } else if (livingEntityIn instanceof Player) {
         ItemStack ammo = this.findAmmo((Player)livingEntityIn);
         boolean flag = ((Player)livingEntityIn).isCreative();
         if (!ammo.isEmpty()) {
            ammo.shrink(1);
            flag = true;
         }

         if (flag) {
            AMCompat.addCooldown(((Player)livingEntityIn).getCooldowns(), this, 20);
            stack.setDamageValue(0);
         }

         livingEntityIn.stopUsingItem();
      }
   }
}
