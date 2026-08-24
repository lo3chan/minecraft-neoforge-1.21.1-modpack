package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class RepelledProperty<E extends Entity> extends Property {
   final int color;
   final Class<E> targetEntities;
   final float radius;
   final boolean onUse;

   public RepelledProperty(int color, Class<E> targetEntities, float radius, boolean onUse) {
      this.color = color;
      this.targetEntities = targetEntities;
      this.radius = radius;
      this.onUse = onUse;
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (this.onUse) {
         event.getEntity().startUsingItem(event.getHand());
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return this.onUse ? 72000 : super.modifyUseDuration(stack, original, result);
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!this.onUse || user.getUseItem() == stack) {
         this.repelUser(user);
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      this.repelUser(itemEntity);
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      this.repelUser(projectile);
   }

   private void repelUser(Entity user) {
      for (Entity target : user.level().getEntitiesOfClass(this.targetEntities, CommonUtils.boundingBoxAroundPoint(user.position(), this.radius))) {
         if (!target.equals(user)) {
            double distanceTo = target.position().distanceTo(user.position());
            float strength = (float)Math.max(0.0, this.radius - distanceTo) * 0.05F;
            user.hasImpulse = true;
            Vec3 vec3 = user.getDeltaMovement();
            Vec3 vec31 = target.position().subtract(user.position()).normalize().scale(strength);
            user.setDeltaMovement(vec3.subtract(vec31));
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.color;
   }
}
