package net.bobophones.bobolib.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;

public abstract class HurtingProjectile extends AbstractHurtingProjectile {
   public HurtingProjectile(EntityType<? extends AbstractHurtingProjectile> type, LivingEntity shooter) {
      super(type, shooter.getX(), shooter.getY(), shooter.getZ(), shooter.level());
      this.setOwner(shooter);
      this.setRot(shooter.getYRot(), shooter.getXRot());
   }

   protected boolean shouldBurn() {
      return false;
   }

   protected ParticleOptions getTrailParticle() {
      return null;
   }
}
