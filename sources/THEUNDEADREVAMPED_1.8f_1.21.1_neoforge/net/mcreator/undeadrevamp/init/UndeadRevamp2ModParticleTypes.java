package net.mcreator.undeadrevamp.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UndeadRevamp2ModParticleTypes {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, "undead_revamp2");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BOMBERGOO = REGISTRY.register("bombergoo", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACIDGOO = REGISTRY.register("acidgoo", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TOXICFUMESPINK = REGISTRY.register(
      "toxicfumespink", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CLOGGERCARCASSES = REGISTRY.register(
      "cloggercarcasses", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ZEESLEEP = REGISTRY.register("zeesleep", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLUEFUMES = REGISTRY.register("bluefumes", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPSOMNOLENCE = REGISTRY.register(
      "dripsomnolence", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PINKDUST = REGISTRY.register("pinkdust", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BRIGHTPINKDUST = REGISTRY.register(
      "brightpinkdust", () -> new SimpleParticleType(true)
   );
}
