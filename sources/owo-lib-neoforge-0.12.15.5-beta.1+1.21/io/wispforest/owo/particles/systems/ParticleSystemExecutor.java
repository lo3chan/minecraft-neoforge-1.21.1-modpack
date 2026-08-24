package io.wispforest.owo.particles.systems;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ParticleSystemExecutor<T> {
   void executeParticleSystem(Level var1, Vec3 var2, T var3);
}
