package dev.latvian.mods.kubejs.level;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

public record ExplosionProperties(
   @Nullable Entity source,
   @Nullable DamageSource damageSource,
   @Nullable ExplosionDamageCalculator damageCalculator,
   Optional<Float> strength,
   Optional<Boolean> causesFire,
   @Nullable ExplosionInteraction mode,
   Optional<Boolean> particles,
   @Nullable ParticleOptions smallParticles,
   @Nullable ParticleOptions largeParticles,
   @Nullable Holder<SoundEvent> explosionSound
) {
   public Explosion explode(Level level, double x, double y, double z) {
      return level.explode(
         this.source,
         this.damageSource,
         this.damageCalculator,
         x,
         y,
         z,
         this.strength.orElse(3.0F),
         this.causesFire.orElse(Boolean.FALSE),
         this.mode == null ? ExplosionInteraction.NONE : this.mode,
         this.particles.orElse(Boolean.TRUE),
         (ParticleOptions)(this.smallParticles == null ? ParticleTypes.EXPLOSION : this.smallParticles),
         (ParticleOptions)(this.largeParticles == null ? ParticleTypes.EXPLOSION_EMITTER : this.largeParticles),
         (Holder)(this.explosionSound == null ? SoundEvents.GENERIC_EXPLODE : this.explosionSound)
      );
   }
}
