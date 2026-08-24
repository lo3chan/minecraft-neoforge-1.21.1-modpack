package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.client.TumblingAnimation;
import net.mehvahdjukaar.amendments.common.ProjectileStats;
import net.mehvahdjukaar.amendments.common.entity.IVisualTransformationProvider;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.entity.ParticleTrailEmitter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({DragonFireball.class})
public abstract class DragonFireballMixin extends AbstractHurtingProjectile implements IVisualTransformationProvider {
   @Unique
   private final ParticleTrailEmitter amendments$trailEmitter = ProjectileStats.makeDragonTrialEmitter(true);
   @Unique
   private final TumblingAnimation amendments$tumblingAnimation = ProjectileStats.makeTumbler();

   protected DragonFireballMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public Matrix4f amendments$getVisualTransformation(float partialTicks) {
      return new Matrix4f().rotate(this.amendments$tumblingAnimation.getRotation(partialTicks));
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         if (ClientConfigs.DRAGON_FIREBALL_TRAIL.get()) {
            this.amendments$trailEmitter
               .tick(
                  this,
                  (p, motion) -> {
                     if (!this.isInWater()) {
                        this.level()
                           .addParticle(
                              ParticleTypes.DRAGON_BREATH,
                              p.x,
                              p.y,
                              p.z,
                              this.random.nextGaussian() * 0.05,
                              this.random.nextGaussian() * 0.05,
                              this.random.nextGaussian() * 0.05
                           );
                     }
                  }
               );
         }

         if (ClientConfigs.CHARGES_TUMBLE.get()) {
            this.amendments$tumblingAnimation.tick(this.random);
         }
      }
   }
}
