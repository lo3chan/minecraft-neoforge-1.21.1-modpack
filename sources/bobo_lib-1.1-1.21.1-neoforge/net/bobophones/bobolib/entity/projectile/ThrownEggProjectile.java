package net.bobophones.bobolib.entity.projectile;

import net.bobophones.bobolib.util.BU;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class ThrownEggProjectile extends ThrowableItemProjectile {
   public ThrownEggProjectile(EntityType<? extends ThrownEggProjectile> type, Level level) {
      super(type, level);
   }

   public ThrownEggProjectile(EntityType<? extends ThrownEggProjectile> type, EntityType<? extends AgeableMob> mob, LivingEntity shooter) {
      super(type, shooter, shooter.level());
   }

   public void handleEntityEvent(byte id) {
      if (id == 3) {
         for (int i = 0; i < 8; i++) {
            BU.SpawnItemParticle(this.level(), this.getItem(), this.position());
         }
      }
   }

   protected void onHitEntity(EntityHitResult hit) {
      super.onHitEntity(hit);
      hit.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), this.get_damage());
   }

   protected float get_damage() {
      return 0.0F;
   }

   protected boolean spawn_chick() {
      return true;
   }

   protected EntityType<? extends AgeableMob> get_mob() {
      return EntityType.CHICKEN;
   }

   protected void onHit(HitResult hit) {
      super.onHit(hit);
      if (!this.level().isClientSide()) {
         if (this.spawn_chick() && this.random.nextInt(8) == 0) {
            int i = 1;
            if (this.random.nextInt(32) == 0) {
               i = 4;
            }

            for (int ii = 0; ii < i; ii++) {
               AgeableMob chicken = (AgeableMob)this.get_mob().create(this.level());
               if (chicken != null) {
                  chicken.setAge(-24000);
                  chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                  this.level().addFreshEntity(chicken);
               }
            }
         }

         this.level().broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }
}
