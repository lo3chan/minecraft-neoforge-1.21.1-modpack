package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityPollenBall extends EntityMobProjectile {
   public EntityPollenBall(EntityType type, Level level) {
      super(type, level);
   }

   public EntityPollenBall(Level worldIn, EntityFlutter flutter) {
      super(AMEntityRegistry.POLLEN_BALL.get(), worldIn, flutter);
      Vec3 vec3 = flutter.position().add(this.calcOffsetVec(new Vec3(0.0, 0.4F * flutter.getScale(), 0.0), flutter.getFlutterPitch(), flutter.getYRot()));
      this.setPos(vec3.x, vec3.y, vec3.z);
   }

   public boolean isNoGravity() {
      return true;
   }

   @Override
   public void doBehavior() {
      Entity entity = this.getShooter();
      if (entity instanceof Mob && ((Mob)entity).getTarget() != null) {
         LivingEntity target = ((Mob)entity).getTarget();
         if (target == null) {
            AMCompat.kill(this);
         }

         double d0 = target.getX() - this.getX();
         double d1 = target.getY() + target.getBbHeight() * 0.5F - this.getY();
         double d2 = target.getZ() - this.getZ();
         float speed = 0.35F;
         this.shoot(d0, d1, d2, 0.35F, 0.0F);
         this.setYRot(-((float)Mth.atan2(d0, d2)) * 57.295776F);
      }

      if (this.level().isClientSide() && this.random.nextInt(2) == 0) {
         float r1 = (this.random.nextFloat() - 0.5F) * 0.5F;
         float r2 = (this.random.nextFloat() - 0.5F) * 0.5F;
         float r3 = (this.random.nextFloat() - 0.5F) * 0.5F;
         this.level().addParticle(ParticleTypes.FALLING_NECTAR, this.getX() + r1, this.getY() + r2, this.getZ() + r3, r1 * 0.1F, r2 * 0.1F, r3 * 0.1F);
      }
   }

   @Override
   protected float getDamage() {
      return 3.0F;
   }
}
