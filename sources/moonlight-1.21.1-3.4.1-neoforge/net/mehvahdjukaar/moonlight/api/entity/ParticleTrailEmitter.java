package net.mehvahdjukaar.moonlight.api.entity;

import net.mehvahdjukaar.moonlight.api.misc.RollingBuffer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleTrailEmitter {
   private final double wantedSpacing;
   private final int maxParticlesPerTick;
   private final int minParticlesPerTick;
   private final double minSpeed;
   private Vec3 lastEmittedPos = null;
   private final RollingBuffer<Vec3> previousVelocities = new RollingBuffer<>(3);
   private final RollingBuffer<Vec3> previousPositions = new RollingBuffer<>(3);

   private ParticleTrailEmitter(ParticleTrailEmitter.Builder builder) {
      this.wantedSpacing = builder.idealSpacing;
      this.maxParticlesPerTick = builder.maxParticlesPerTick;
      this.minParticlesPerTick = builder.minParticlesPerTick;
      this.minSpeed = builder.minSpeed;
   }

   public void tick(Entity obj, ParticleOptions particleOptions) {
      this.tick(obj, particleOptions, true);
   }

   public void tick(Entity obj, ParticleOptions particleOptions, boolean followSpeed) {
      this.tick(obj, (ParticleTrailEmitter.Emitter)((position, velocity) -> {
         Level level = obj.level();
         if (followSpeed) {
            level.addParticle(particleOptions, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
         } else {
            level.addParticle(particleOptions, position.x, position.y, position.z, 0.0, 0.0, 0.0);
         }
      }));
   }

   public void tick(Entity obj, ParticleTrailEmitter.Emitter emitter) {
      Vec3 movement = obj.getDeltaMovement();
      this.previousVelocities.push(movement);
      this.previousPositions.push(obj.position());
      if (this.previousPositions.size() >= 2) {
         if (!(movement.lengthSqr() < this.minSpeed * this.minSpeed)) {
            Vec3 startPos = this.previousPositions.get(0);
            Vec3 endPos = this.previousPositions.get(1);
            Vec3 startVel = this.previousVelocities.get(0);
            Vec3 endVel = this.previousVelocities.get(1);
            if (this.lastEmittedPos == null) {
               this.lastEmittedPos = startPos;
            } else {
               double segmentLength = startPos.distanceTo(endPos);
               Double startT = intersectSphereSegment(this.lastEmittedPos, this.wantedSpacing, startPos, endPos);
               if (startT != null) {
                  double remainingLength = segmentLength * (1.0 - startT);
                  float spacing = (float)this.wantedSpacing;
                  int particlesToEmit = 1 + (int)(remainingLength / this.wantedSpacing);
                  if (particlesToEmit > this.maxParticlesPerTick) {
                     particlesToEmit = this.maxParticlesPerTick;
                     spacing = (float)(remainingLength / particlesToEmit);
                  } else if (particlesToEmit < this.minParticlesPerTick) {
                     particlesToEmit = this.minParticlesPerTick;
                     spacing = (float)(remainingLength / particlesToEmit);
                  }

                  float h = obj.getBbHeight() / 2.0F;

                  for (int i = 0; i < particlesToEmit; i++) {
                     double t = startT + i * spacing / (float)segmentLength;
                     if (t > 1.0) {
                        break;
                     }

                     Vec3 position = startPos.lerp(endPos, t);
                     Vec3 velocity = startVel.lerp(endVel, t);
                     emitter.emitParticle(position.add(0.0, h, 0.0), velocity);
                     this.lastEmittedPos = position;
                  }
               }
            }
         }
      }
   }

   private static Double intersectSphereSegment(Vec3 center, double radius, Vec3 start, Vec3 end) {
      Vec3 direction = end.subtract(start);
      Vec3 oldDirection = start.subtract(center);
      double a = direction.dot(direction);
      double b = 2.0 * oldDirection.dot(direction);
      double c = oldDirection.dot(oldDirection) - radius * radius;
      double discriminant = b * b - 4.0 * a * c;
      if (discriminant < 0.0) {
         return null;
      } else {
         double sqrtDiscriminant = (float)Math.sqrt(discriminant);
         double t1 = (-b - sqrtDiscriminant) / (2.0 * a);
         double t2 = (-b + sqrtDiscriminant) / (2.0 * a);
         if (t1 >= 0.0 && t1 <= 1.0) {
            return Mth.clamp(t2, 0.0, 1.0);
         } else {
            return t2 >= 0.0 && t2 <= 1.0 ? Mth.clamp(t2, 0.0, 1.0) : null;
         }
      }
   }

   public static ParticleTrailEmitter.Builder builder() {
      return new ParticleTrailEmitter.Builder();
   }

   public static class Builder {
      private double idealSpacing = 0.5;
      private int maxParticlesPerTick = 5;
      private int minParticlesPerTick = 0;
      private double minSpeed = 0.0;

      public ParticleTrailEmitter.Builder spacing(double spacing) {
         this.idealSpacing = spacing;
         return this;
      }

      public ParticleTrailEmitter.Builder maxParticlesPerTick(int max) {
         this.maxParticlesPerTick = max;
         return this;
      }

      public ParticleTrailEmitter.Builder minParticlesPerTick(int min) {
         this.minParticlesPerTick = min;
         return this;
      }

      public ParticleTrailEmitter.Builder minSpeed(double speed) {
         this.minSpeed = speed;
         return this;
      }

      public ParticleTrailEmitter build() {
         if (this.minParticlesPerTick > this.maxParticlesPerTick) {
            throw new IllegalArgumentException("minParticlesPerTick cannot be greater than maxParticlesPerTick");
         } else {
            return new ParticleTrailEmitter(this);
         }
      }
   }

   public interface Emitter {
      void emitParticle(Vec3 var1, Vec3 var2);
   }
}
