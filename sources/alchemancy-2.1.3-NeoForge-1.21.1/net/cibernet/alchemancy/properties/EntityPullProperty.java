package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.mixin.accessors.ProjectileAccessor;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class EntityPullProperty<E extends Entity> extends Property {
   final int color;
   final Class<E> targetEntities;
   final float radius;
   final boolean onUse;
   final float pullStrength;

   public EntityPullProperty(int color, Class<E> targetEntities, float radius, boolean onUse, float pullStrength) {
      this.color = color;
      this.targetEntities = targetEntities;
      this.radius = radius;
      this.onUse = onUse;
      this.pullStrength = pullStrength;
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
         this.vacuum(user);
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      this.vacuum(itemEntity);
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      this.vacuum(null, root.getLevel(), root.getBlockPos().getCenter());
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      this.vacuum(projectile);
   }

   private void vacuum(Entity user) {
      this.vacuum(user, user.level(), user.getEyePosition());
   }

   private void vacuum(@Nullable Entity source, Level level, Vec3 center) {
      for (E target : level.getEntitiesOfClass(this.targetEntities, CommonUtils.boundingBoxAroundPoint(center, this.radius))) {
         if (!target.equals(source)) {
            double distanceTo = target.position().distanceTo(center);
            if (source != null && target instanceof Projectile projectile) {
               if (source.equals(projectile.getOwner())) {
                  continue;
               }

               if (distanceTo < 0.5) {
                  ((ProjectileAccessor)projectile).invokeOnHit(new EntityHitResult(source));
                  continue;
               }
            }

            float strength = (float)Math.max(0.0, this.radius - distanceTo) * 0.05F * this.pullStrength;
            target.hasImpulse = true;
            Vec3 vec3 = target.getDeltaMovement();
            Vec3 vec31 = target.position().subtract(center).normalize().scale(strength);
            target.setDeltaMovement(vec3.scale(1.0 - 0.5 * (1.0 - distanceTo / this.radius)).subtract(vec31));
         }
      }
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return this.onUse ? Optional.of(UseAnim.BOW) : current;
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.color;
   }
}
