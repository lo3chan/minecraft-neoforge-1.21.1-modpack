package io.github.razordevs.deep_aether.entity.projectile;

import io.github.razordevs.deep_aether.entity.living.quail.Quail;
import io.github.razordevs.deep_aether.entity.living.quail.QuailVariants;
import io.github.razordevs.deep_aether.init.DAEntities;
import io.github.razordevs.deep_aether.init.DAItems;
import net.minecraft.Util;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownQuailEgg extends ThrowableItemProjectile {
   public ThrownQuailEgg(EntityType<ThrownQuailEgg> entityType, Level level) {
      super(entityType, level);
   }

   public ThrownQuailEgg(Level level, LivingEntity livingEntity) {
      super((EntityType)DAEntities.QUAIL_EGG.get(), livingEntity, level);
   }

   public void handleEntityEvent(byte b) {
      if (b == 3) {
         for (int i = 0; i < 8; i++) {
            this.level()
               .addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  (this.random.nextFloat() - 0.5) * 0.08,
                  (this.random.nextFloat() - 0.5) * 0.08,
                  (this.random.nextFloat() - 0.5) * 0.08
               );
         }
      }
   }

   protected void onHitEntity(EntityHitResult hitResult) {
      super.onHitEntity(hitResult);
      hitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
   }

   protected void onHit(HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide) {
         if (this.random.nextInt(8) == 0) {
            int i = 1;
            if (this.random.nextInt(32) == 0) {
               i = 4;
            }

            for (int j = 0; j < i; j++) {
               Quail quail = (Quail)((EntityType)DAEntities.QUAIL.get()).create(this.level());
               if (quail != null) {
                  QuailVariants variant = (QuailVariants)Util.getRandom(QuailVariants.values(), this.random);
                  quail.setVariant(variant);
                  quail.setAge(-24000);
                  quail.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                  this.level().addFreshEntity(quail);
               }
            }
         }

         this.level().broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }

   protected Item getDefaultItem() {
      return (Item)DAItems.QUAIL_EGG.get();
   }
}
