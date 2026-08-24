package net.cibernet.alchemancy.properties;

import java.util.UUID;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public class LoyalProperty extends Property implements IDataHolder<UUID> {
   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      if (!projectile.level().isClientSide()) {
         if (this.isReturning(projectile) && projectile.getOwner() != null) {
            Entity entity = projectile.getOwner();
            float i = 2.0F;
            Vec3 vec3 = entity.getEyePosition().subtract(projectile.position());
            double d0 = 0.05 * i;
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
            projectile.hasImpulse = true;
         }
      }
   }

   protected void dealArrowDamage(AbstractArrow arrow, Entity target) {
      float f = (float)arrow.getDeltaMovement().length();
      double d0 = arrow.getBaseDamage();
      Entity entity1 = arrow.getOwner();
      DamageSource damagesource = arrow.damageSources().arrow(arrow, (Entity)(entity1 != null ? entity1 : arrow));
      if (arrow.getWeaponItem() != null && arrow.level() instanceof ServerLevel serverlevel) {
         d0 = EnchantmentHelper.modifyDamage(serverlevel, arrow.getWeaponItem(), target, damagesource, (float)d0);
      }

      int j = Mth.ceil(Mth.clamp(f * d0, 0.0, 2.147483647E9));
      if (arrow.isCritArrow()) {
         long k = arrow.getRandom().nextInt(j / 2 + 2);
         j = (int)Math.min(k + j, 2147483647L);
      }

      if (entity1 instanceof LivingEntity livingentity1) {
         livingentity1.setLastHurtMob(target);
      }

      boolean flag = target.getType() == EntityType.ENDERMAN;
      if (arrow.isOnFire() && !flag) {
         target.igniteForSeconds(5.0F);
      }

      target.hurt(damagesource, j);
   }

   public boolean isReturning(Entity projectile) {
      return projectile.getPersistentData().getBoolean("alchemancy:loyal_returning");
   }

   public boolean canTriggerImpactEffects(Projectile projectile, HitResult hitResult) {
      return hitResult.getType() != Type.ENTITY || !this.isReturning(projectile);
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (this.isReturning(projectile)) {
         if (rayTraceResult.getType() == Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() == projectile.getOwner()) {
               ItemEntity droppedItem = new ItemEntity(
                  projectile.level(), projectile.position().x, projectile.position().y, projectile.position().z, stack.copy()
               );
               droppedItem.setNoPickUpDelay();
               projectile.level().addFreshEntity(droppedItem);
               projectile.discard();
            }

            event.setCanceled(true);
         }
      } else {
         if (rayTraceResult.getType() == Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (projectile instanceof AbstractArrow arrow) {
               this.dealArrowDamage(arrow, entity);
            } else {
               DamageSource damageSource = projectile.damageSources().thrown(projectile, projectile.getOwner());
               entity.hurt(damageSource, (float)(Property.getItemAttackDamage(stack) - 1.0));
            }
         }

         projectile.getPersistentData().putBoolean("alchemancy:loyal_returning", true);
         event.setCanceled(true);
         projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0));
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity entity) {
      if (!entity.level().isClientSide() && entity.level() instanceof ServerLevel serverLevel) {
         UUID ownerUuid = this.getData(stack);
         Entity owner = ownerUuid == null ? null : serverLevel.getEntity(ownerUuid);
         if (!entity.hasPickUpDelay() && owner != null && owner.distanceTo(entity) < 50.0F) {
            entity.setDeltaMovement(owner.position().subtract(entity.position()).normalize().scale(0.25));
            entity.hasImpulse = true;
         }
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      this.setData(stack, user.getUUID());
   }

   @Override
   public int getColor(ItemStack stack) {
      return 6735023;
   }

   public UUID readData(CompoundTag tag) {
      return tag.getUUID("owner");
   }

   public CompoundTag writeData(final UUID data) {
      return new CompoundTag() {
         {
            this.putUUID("owner", data);
         }
      };
   }

   public UUID getDefaultData() {
      return null;
   }

   @Override
   public int getPriority() {
      return 100;
   }
}
