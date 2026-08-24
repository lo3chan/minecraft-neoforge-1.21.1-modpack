package io.wispforest.owo.particles;

import io.wispforest.owo.util.VectorRandomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientParticles {
   private static int particleCount = 1;
   private static boolean persist = false;
   private static Vec3 velocity = new Vec3(0.0, 0.0, 0.0);
   private static boolean randomizeVelocity = false;
   private static double randomVelocityScalar = 0.0;
   private static Axis randomizationAxis = null;

   private ClientParticles() {
   }

   public static void persist() {
      persist = true;
   }

   public static void setParticleCount(int particleCount) {
      ClientParticles.particleCount = particleCount;
   }

   public static void setVelocity(Vec3 velocity) {
      ClientParticles.velocity = velocity;
   }

   public static void randomizeVelocity(double scalar) {
      randomizeVelocity = true;
      randomVelocityScalar = scalar;
      randomizationAxis = null;
   }

   public static void randomizeVelocityOnAxis(double scalar, Axis axis) {
      randomizeVelocity = true;
      randomVelocityScalar = scalar;
      randomizationAxis = axis;
   }

   public static void reset() {
      persist = false;
      clearState();
   }

   private static void clearState() {
      if (!persist) {
         particleCount = 1;
         velocity = new Vec3(0.0, 0.0, 0.0);
         randomizeVelocity = false;
      }
   }

   private static void addParticle(ParticleOptions particle, Level world, Vec3 location) {
      if (randomizeVelocity) {
         if (randomizationAxis == null) {
            velocity = VectorRandomUtils.getRandomOffset(world, Vec3.ZERO, randomVelocityScalar);
         } else {
            double stopIt_getSomeHelp = (world.random.nextDouble() * 2.0 - 1.0) * randomVelocityScalar;

            velocity = switch (randomizationAxis) {
               case X -> new Vec3(stopIt_getSomeHelp, 0.0, 0.0);
               case Y -> new Vec3(0.0, stopIt_getSomeHelp, 0.0);
               case Z -> new Vec3(0.0, 0.0, stopIt_getSomeHelp);
               default -> throw new MatchException(null, null);
            };
         }
      }

      world.addParticle(particle, location.x, location.y, location.z, velocity.x, velocity.y, velocity.z);
   }

   public static void spawnCenteredOnBlock(ParticleOptions particle, Level world, BlockPos pos, double deviation) {
      for (int i = 0; i < particleCount; i++) {
         Vec3 location = VectorRandomUtils.getRandomCenteredOnBlock(world, pos, deviation);
         addParticle(particle, world, location);
      }

      clearState();
   }

   public static void spawnWithinBlock(ParticleOptions particle, Level world, BlockPos pos) {
      for (int i = 0; i < particleCount; i++) {
         Vec3 location = VectorRandomUtils.getRandomWithinBlock(world, pos);
         addParticle(particle, world, location);
      }

      clearState();
   }

   public static void spawnWithOffsetFromBlock(ParticleOptions particle, Level world, BlockPos pos, Vec3 offset, double deviation) {
      offset = offset.add(Vec3.atLowerCornerOf(pos));

      for (int i = 0; i < particleCount; i++) {
         Vec3 location = VectorRandomUtils.getRandomOffset(world, offset, deviation);
         addParticle(particle, world, location);
      }

      clearState();
   }

   public static void spawn(ParticleOptions particle, Level world, Vec3 pos, double deviation) {
      for (int i = 0; i < particleCount; i++) {
         Vec3 location = VectorRandomUtils.getRandomOffset(world, pos, deviation);
         addParticle(particle, world, location);
      }

      clearState();
   }

   public static void spawnPrecise(ParticleOptions particle, Level world, Vec3 pos, double deviationX, double deviationY, double deviationZ) {
      for (int i = 0; i < particleCount; i++) {
         Vec3 location = VectorRandomUtils.getRandomOffsetSpecific(world, pos, deviationX, deviationY, deviationZ);
         addParticle(particle, world, location);
      }

      clearState();
   }

   public static void spawnEnchantParticles(Level world, Vec3 origin, Vec3 destination, float deviation) {
      Vec3 particleVector = origin.subtract(destination);

      for (int i = 0; i < particleCount; i++) {
         Vec3 location = VectorRandomUtils.getRandomOffset(world, particleVector, deviation);
         world.addParticle(ParticleTypes.ENCHANT, destination.x, destination.y, destination.z, location.x, location.y, location.z);
      }

      clearState();
   }

   public static <T extends ParticleOptions> void spawnWithMaxAge(T particleType, Vec3 pos, int maxAge) {
      Particle particle = Minecraft.getInstance().particleEngine.createParticle(particleType, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
      if (particle != null) {
         particle.setLifetime(maxAge);
         clearState();
      }
   }

   public static void spawnLine(ParticleOptions particle, Level world, Vec3 start, Vec3 end, float deviation) {
      spawnLineInner(particle, world, start, end, deviation);
      clearState();
   }

   public static void spawnCubeOutline(ParticleOptions particle, Level world, Vec3 origin, float size, float deviation) {
      spawnLineInner(particle, world, origin, origin.add(size, 0.0, 0.0), deviation);
      spawnLineInner(particle, world, origin.add(size, 0.0, 0.0), origin.add(size, 0.0, size), deviation);
      spawnLineInner(particle, world, origin, origin.add(0.0, 0.0, size), deviation);
      spawnLineInner(particle, world, origin.add(0.0, 0.0, size), origin.add(size, 0.0, size), deviation);
      origin = origin.add(0.0, size, 0.0);
      spawnLineInner(particle, world, origin, origin.add(size, 0.0, 0.0), deviation);
      spawnLineInner(particle, world, origin.add(size, 0.0, 0.0), origin.add(size, 0.0, size), deviation);
      spawnLineInner(particle, world, origin, origin.add(0.0, 0.0, size), deviation);
      spawnLineInner(particle, world, origin.add(0.0, 0.0, size), origin.add(size, 0.0, size), deviation);
      spawnLineInner(particle, world, origin, origin.add(0.0, -size, 0.0), deviation);
      spawnLineInner(particle, world, origin.add(size, 0.0, 0.0), origin.add(size, -size, 0.0), deviation);
      spawnLineInner(particle, world, origin.add(0.0, 0.0, size), origin.add(0.0, -size, size), deviation);
      spawnLineInner(particle, world, origin.add(size, 0.0, size), origin.add(size, -size, size), deviation);
      clearState();
   }

   private static void spawnLineInner(ParticleOptions particle, Level world, Vec3 start, Vec3 end, float deviation) {
      Vec3 increment = end.subtract(start).scale(1.0F / particleCount);

      for (int i = 0; i < particleCount; i++) {
         start = VectorRandomUtils.getRandomOffset(world, start, deviation);
         addParticle(particle, world, start);
         start = start.add(increment);
      }
   }
}
