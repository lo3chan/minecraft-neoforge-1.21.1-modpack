package net.mehvahdjukaar.amendments.common;

import net.mehvahdjukaar.amendments.client.TumblingAnimation;
import net.mehvahdjukaar.moonlight.api.entity.ParticleTrailEmitter;
import net.minecraft.world.item.ProjectileItem.DispenseConfig;

public class ProjectileStats {
   public static final DispenseConfig DISPENSER_CONFIG = DispenseConfig.DEFAULT;
   public static final float THROWN_SPEED = 1.1F;
   public static final ProjectileStats.Fire BLAZE_FIREBALL = new ProjectileStats.Fire(0.75F, 5.0F, 5, 0.0F, 0, 0.0F, 0.0F);
   public static final ProjectileStats.Fire PLAYER_FIREBALL = new ProjectileStats.Fire(0.75F, 5.0F, 5, 1.0F, 4, 0.0F, 1.0F);
   public static final ProjectileStats.Fire GHAST_FIREBALL = new ProjectileStats.Fire(2.375F, 6.0F, 5, 1.0F, 4, 1.0F, 4.0F);
   public static final ProjectileStats.Dragon DRAGON_FIREBALL = new ProjectileStats.Dragon(2.375F);
   public static final ProjectileStats.Dragon DRAGON_CHARGE = new ProjectileStats.Dragon(0.75F);

   public static TumblingAnimation makeTumbler() {
      return new TumblingAnimation(4.0F, 7.0F, 0.5F);
   }

   public static TumblingAnimation makeFasterTumbler() {
      return new TumblingAnimation(6.0F, 9.0F, 0.5F);
   }

   public static ParticleTrailEmitter makeFireballTrialEmitter() {
      return ParticleTrailEmitter.builder().spacing(0.5).maxParticlesPerTick(20).minSpeed(0.01).build();
   }

   public static ParticleTrailEmitter makeDragonTrialEmitter(boolean isLarge) {
      return ParticleTrailEmitter.builder().spacing(0.7).maxParticlesPerTick(5).minSpeed(0.0).build();
   }

   public static ParticleTrailEmitter makeSnowballTrialEmitter() {
      return ParticleTrailEmitter.builder().spacing(0.35).maxParticlesPerTick(20).minSpeed(0.01).build();
   }

   public record Dragon(float modelSize) {
   }

   public record Fire(
      float modelSize,
      float damageOnHit,
      int directHitFireSeconds,
      float fireballExpRadius,
      int indirectHitFireSeconds,
      float normalExplosionRadius,
      float soundVolume
   ) {
   }
}
