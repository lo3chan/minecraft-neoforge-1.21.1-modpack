package net.mcreator.borninchaosv.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BornInChaosV1ModParticleTypes {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, "born_in_chaos_v1");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPLASHOFFLESH = REGISTRY.register(
      "splashofflesh", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TL = REGISTRY.register("tl", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DIM = REGISTRY.register("dim", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RITUAL = REGISTRY.register("ritual", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SRIRST_PART = REGISTRY.register("srirst_part", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PUMPKIN_STAFF_S = REGISTRY.register(
      "pumpkin_staff_s", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_FIRE = REGISTRY.register("soul_fire", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ANIM_FIRE = REGISTRY.register("anim_fire", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INTOXICATIND_BOMB_PART = REGISTRY.register(
      "intoxicatind_bomb_part", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAGICTRAIL = REGISTRY.register("magictrail", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SWAP = REGISTRY.register("swap", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_SLASH = REGISTRY.register("soul_slash", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DARK_SMOKE = REGISTRY.register("dark_smoke", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STUNSTARS = REGISTRY.register("stunstars", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPIKERELEASE = REGISTRY.register("spikerelease", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLESHSPLASH = REGISTRY.register("fleshsplash", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLI = REGISTRY.register("fli", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNAL_SURGE = REGISTRY.register(
      "infernal_surge", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNALTRAIL = REGISTRY.register(
      "infernaltrail", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DIMLARG = REGISTRY.register("dimlarg", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INTOXICATINGSMOKE = REGISTRY.register(
      "intoxicatingsmoke", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STIMULATINGSMOKE = REGISTRY.register(
      "stimulatingsmoke", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STIMULATINGBUBBLES = REGISTRY.register(
      "stimulatingbubbles", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> OBSESSIONPAR = REGISTRY.register("obsessionpar", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CHAOSENERGY = REGISTRY.register("chaosenergy", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DIM_LONG = REGISTRY.register("dim_long", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAGICHIT = REGISTRY.register("magichit", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PUMPKIN_TRAIL = REGISTRY.register(
      "pumpkin_trail", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PUMPKIN_EXPLOSION = REGISTRY.register(
      "pumpkin_explosion", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WEB_SPLASH = REGISTRY.register("web_splash", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPIDER_BLAST = REGISTRY.register("spider_blast", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPIDER_INFESTATION = REGISTRY.register(
      "spider_infestation", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CLOUDSOFDUST = REGISTRY.register("cloudsofdust", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ROARSPLASH = REGISTRY.register("roarsplash", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DARKMATTER = REGISTRY.register("darkmatter", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CANDY_ORANGE = REGISTRY.register("candy_orange", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CANDYGREN = REGISTRY.register("candygren", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CANDYPURPLE = REGISTRY.register("candypurple", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CHIT = REGISTRY.register("chit", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LITTLESNOWFLAKE = REGISTRY.register(
      "littlesnowflake", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WANINGSNOWFLAKE = REGISTRY.register(
      "waningsnowflake", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SNOWCLOUD = REGISTRY.register("snowcloud", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DARKSPOTS = REGISTRY.register("darkspots", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LITTLE_CARROT = REGISTRY.register(
      "little_carrot", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> XP_PARTICLE = REGISTRY.register("xp_particle", () -> new SimpleParticleType(true));
}
