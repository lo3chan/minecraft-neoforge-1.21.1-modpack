package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LevelRenderer.class})
public interface LevelRendererAccessor {
   @Invoker("addParticleInternal")
   Particle invokeAddParticleInternal(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13);
}
