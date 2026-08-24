package vazkii.psi.client.fx;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "psi");
   public static final DeferredHolder<ParticleType<?>, WispParticleData.Type> WISP = PARTICLE_TYPES.register("wisp", WispParticleData.Type::new);
   public static final DeferredHolder<ParticleType<?>, SparkleParticleData.Type> SPARKLE = PARTICLE_TYPES.register("sparkle", SparkleParticleData.Type::new);
}
