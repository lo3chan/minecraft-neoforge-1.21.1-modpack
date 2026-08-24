package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@RemapPrefixForJS("kjs$")
public interface ServerLevelKJS extends LevelKJS, WithPersistentData {
   default ServerLevel kjs$self() {
      return (ServerLevel)this;
   }

   @Override
   default void kjs$spawnParticles(
      ParticleOptions options, boolean overrideLimiter, double x, double y, double z, double vx, double vy, double vz, int count, double speed
   ) {
      for (ServerPlayer player : this.kjs$self().players()) {
         this.kjs$self().sendParticles(player, options, overrideLimiter, x, y, z, count, vx, vy, vz, speed);
      }
   }
}
