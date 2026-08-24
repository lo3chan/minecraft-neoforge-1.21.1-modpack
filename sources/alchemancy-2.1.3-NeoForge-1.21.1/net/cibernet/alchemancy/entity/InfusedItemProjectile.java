package net.cibernet.alchemancy.entity;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class InfusedItemProjectile extends ThrowableItemProjectile {
   public InfusedItemProjectile(Level level) {
      super((EntityType)AlchemancyEntities.ITEM_PROJECTILE.get(), level);
   }

   public InfusedItemProjectile(double x, double y, double z, Level level) {
      super((EntityType)AlchemancyEntities.ITEM_PROJECTILE.get(), x, y, z, level);
   }

   public InfusedItemProjectile(LivingEntity shooter, Level level) {
      super((EntityType)AlchemancyEntities.ITEM_PROJECTILE.get(), shooter, level);
   }

   public InfusedItemProjectile(EntityType<? extends InfusedItemProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public Component getName() {
      Component component = this.getCustomName();
      return (Component)(component != null ? component : Component.translatable(this.getItem().getDescriptionId()));
   }

   protected Item getDefaultItem() {
      return Items.SNOWBALL;
   }

   private ParticleOptions getParticle() {
      ItemStack itemstack = this.getItem();
      return (ParticleOptions)(!itemstack.isEmpty() && !itemstack.is(this.getDefaultItem())
         ? new ItemParticleOption(ParticleTypes.ITEM, itemstack)
         : ParticleTypes.ITEM_SNOWBALL);
   }

   public void handleEntityEvent(byte id) {
      if (id == 3) {
         ParticleOptions particleoptions = this.getParticle();

         for (int i = 0; i < 8; i++) {
            this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         }
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      ItemStack stack = this.getItem();
      Entity entity = result.getEntity();
      DamageSource damageSource = this.damageSources().thrown(this, this.getOwner());
      entity.hurt(damageSource, (float)(Property.getItemAttackDamage(stack) - 1.0));
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level().isClientSide && !this.isRemoved()) {
         ItemStack stack = this.getItem();
         ItemStack copyStack = stack.copy();
         if (stack.isDamageableItem()) {
            if (this.level() instanceof ServerLevel serverLevel) {
               stack.hurtAndBreak(20, serverLevel, null, item -> {});
            }
         } else {
            stack.shrink(1);
         }

         if (stack.isEmpty()) {
            InfusedPropertiesHelper.forEachProperty(
               copyStack, propertyHolder -> ((Property)propertyHolder.value()).onEntityItemDestroyed(copyStack, this, this.damageSources().generic())
            );
            this.level().broadcastEntityEvent(this, (byte)3);
         } else {
            ItemEntity itemEntity = new ItemEntity(this.level(), this.position().x, this.position().y, this.position().z, stack);
            itemEntity.setThrower(this.getOwner());
            this.level().addFreshEntity(itemEntity);
         }

         this.discard();
      }
   }
}
