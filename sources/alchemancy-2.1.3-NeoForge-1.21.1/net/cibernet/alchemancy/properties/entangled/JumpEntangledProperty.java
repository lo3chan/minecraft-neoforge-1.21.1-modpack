package net.cibernet.alchemancy.properties.entangled;

import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class JumpEntangledProperty extends AbstractEntangledProperty {
   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (user instanceof Player player) {
         boolean jumping = ((LivingEntityAccessor)user).isJumping();
         if (jumping != this.getToggle(stack) && player.getInventory().getItem(inventorySlot) == stack) {
            this.setToggle(stack, jumping);
            ItemStack shiftStack = this.shift(stack, player);
            player.getInventory().setItem(inventorySlot, shiftStack);
            if (jumping && inventorySlot == 38 && !player.onGround() && stack != shiftStack && shiftStack.canElytraFly(player)) {
               player.startFallFlying();
            }
         }
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (itemEntity.getItem() == stack && itemEntity.getOwner() instanceof LivingEntity living) {
         boolean jumping = ((LivingEntityAccessor)living).isJumping();
         if (jumping != this.getToggle(stack)) {
            this.setToggle(stack, jumping);
            itemEntity.setItem(this.shift(stack, living));
            this.afterShiftingProjectile(stack, itemEntity.getItem(), itemEntity);
         }
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      if (projectile instanceof InfusedItemProjectile infusedItemProjectile
         && infusedItemProjectile.getItem() == stack
         && projectile.getOwner() instanceof LivingEntity living) {
         boolean jumping = ((LivingEntityAccessor)living).isJumping();
         if (jumping != this.getToggle(stack)) {
            this.setToggle(stack, jumping);
            infusedItemProjectile.setItem(this.shift(stack, living));
            this.afterShiftingProjectile(stack, infusedItemProjectile.getItem(), infusedItemProjectile);
         }
      }
   }
}
