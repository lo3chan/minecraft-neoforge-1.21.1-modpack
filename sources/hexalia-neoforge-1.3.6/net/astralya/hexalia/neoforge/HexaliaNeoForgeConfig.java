package net.astralya.hexalia.neoforge;

import net.astralya.hexalia.HexaliaCommonConfig;
import net.astralya.hexalia.HexaliaConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

final class HexaliaNeoForgeConfig {
   private static final ModConfigSpec SPEC;
   private static final BooleanValue MUTATION_SPAWNS_ITEM_ENTITY;
   private static final DoubleValue MANDRAKE_SCREAM_RADIUS;
   private static final IntValue MANDRAKE_STUN_DURATION;
   private static final IntValue FOUL_SAC_DURATION;
   private static final IntValue FROST_SAC_DURATION;
   private static final IntValue SEARING_SAC_DURATION;
   private static final IntValue PURIFYING_SAC_DURATION;
   private static final DoubleValue SIPHON_RADIUS;
   private static final DoubleValue BLEEDING_DAMAGE;
   private static final IntValue CACOFEY_HARVEST_RADIUS;
   private static final IntValue NATURES_RITUAL_CROP_REQUIREMENT;
   private static final IntValue CENSER_EFFECT_RADIUS;
   private static final IntValue CENSER_EFFECT_DURATION;
   private static final IntValue BREWING_DURATION;
   private static final IntValue OVERCOOKED_DURATION;
   private static final IntValue DREAMCATCHER_RADIUS;
   private static final IntValue PHANTOM_IGNITE_DURATION;
   private static final IntValue EGG_CLUSTER_HATCH_DURATION;
   private static final IntValue NAUTILITE_DURATION;
   private static final IntValue NAUTILITE_EFFECT_RADIUS;
   private static final IntValue WINDSONG_DURATION;
   private static final IntValue WINDSONG_EFFECT_RADIUS;
   private static final IntValue ASTRYLIS_DURATION;
   private static final IntValue ASTRYLIS_BONEMEAL_INTERVAL;
   private static final IntValue MORPHORA_RADIUS;
   private static final IntValue GRIMSHADE_DURATION;
   private static final IntValue GRIMSHADE_EFFECT_RADIUS;
   private static final BooleanValue GHOST_FERN_EMITS_PARTICLES;
   private static final BooleanValue CELESTIAL_BLOOM_EMITS_PARTICLES;
   private static final BooleanValue DREAMSHROOM_EMITS_PARTICLES;
   private static final IntValue LOURDES_DURATION;
   private static final DoubleValue LOURDES_EFFECT_RADIUS;

   private HexaliaNeoForgeConfig() {
   }

   static void init(ModContainer modContainer, IEventBus modEventBus) {
      modContainer.registerConfig(Type.COMMON, SPEC);
      modEventBus.addListener(HexaliaNeoForgeConfig::onConfigLoading);
      modEventBus.addListener(HexaliaNeoForgeConfig::onConfigReloading);
   }

   private static void onConfigLoading(Loading event) {
      if (event.getConfig().getSpec() == SPEC) {
         apply();
      }
   }

   private static void onConfigReloading(Reloading event) {
      if (event.getConfig().getSpec() == SPEC) {
         apply();
      }
   }

   private static void apply() {
      HexaliaConfig.apply(
         new HexaliaCommonConfig.Values(
            (Boolean)MUTATION_SPAWNS_ITEM_ENTITY.get(),
            (Double)MANDRAKE_SCREAM_RADIUS.get(),
            (Integer)MANDRAKE_STUN_DURATION.get(),
            (Integer)FOUL_SAC_DURATION.get(),
            (Integer)FROST_SAC_DURATION.get(),
            (Integer)SEARING_SAC_DURATION.get(),
            (Integer)PURIFYING_SAC_DURATION.get(),
            (Double)SIPHON_RADIUS.get(),
            (Double)BLEEDING_DAMAGE.get(),
            (Integer)CENSER_EFFECT_RADIUS.get(),
            (Integer)CENSER_EFFECT_DURATION.get(),
            (Integer)BREWING_DURATION.get(),
            (Integer)OVERCOOKED_DURATION.get(),
            (Integer)DREAMCATCHER_RADIUS.get(),
            (Integer)PHANTOM_IGNITE_DURATION.get(),
            (Integer)EGG_CLUSTER_HATCH_DURATION.get(),
            (Integer)NAUTILITE_DURATION.get(),
            (Integer)NAUTILITE_EFFECT_RADIUS.get(),
            (Integer)WINDSONG_DURATION.get(),
            (Integer)WINDSONG_EFFECT_RADIUS.get(),
            (Integer)ASTRYLIS_DURATION.get(),
            (Integer)ASTRYLIS_BONEMEAL_INTERVAL.get(),
            (Integer)MORPHORA_RADIUS.get(),
            (Integer)GRIMSHADE_DURATION.get(),
            (Integer)GRIMSHADE_EFFECT_RADIUS.get(),
            (Boolean)GHOST_FERN_EMITS_PARTICLES.get(),
            (Boolean)CELESTIAL_BLOOM_EMITS_PARTICLES.get(),
            (Boolean)DREAMSHROOM_EMITS_PARTICLES.get(),
            (Integer)LOURDES_DURATION.get(),
            (Double)LOURDES_EFFECT_RADIUS.get(),
            (Integer)CACOFEY_HARVEST_RADIUS.get(),
            (Integer)NATURES_RITUAL_CROP_REQUIREMENT.get()
         )
      );
   }

   static {
      HexaliaCommonConfig.Values defaults = HexaliaCommonConfig.defaults();
      Builder builder = new Builder();
      builder.push("others");
      MUTATION_SPAWNS_ITEM_ENTITY = builder.define("mutationSpawnsItemEntity", defaults.mutationSpawnsItemEntity());
      MANDRAKE_SCREAM_RADIUS = builder.defineInRange("mandrakeScreamRadius", defaults.mandrakeScreamRadius(), 1.0, 32.0);
      MANDRAKE_STUN_DURATION = builder.defineInRange("mandrakeStunDuration", defaults.mandrakeStunDuration(), 1, 60);
      FOUL_SAC_DURATION = builder.defineInRange("foulSacDuration", defaults.foulSacDuration(), 1, 60);
      FROST_SAC_DURATION = builder.defineInRange("frostSacDuration", defaults.frostSacDuration(), 1, 60);
      SEARING_SAC_DURATION = builder.defineInRange("searingSacDuration", defaults.searingSacDuration(), 1, 60);
      PURIFYING_SAC_DURATION = builder.defineInRange("purifyingSacDuration", defaults.purifyingSacDuration(), 1, 60);
      SIPHON_RADIUS = builder.defineInRange("siphonRadius", defaults.siphonRadius(), 0.5, 64.0);
      BLEEDING_DAMAGE = builder.defineInRange("bleedingDamage", defaults.bleedingDamage(), 0.0, 10.0);
      CACOFEY_HARVEST_RADIUS = builder.defineInRange("cacofeyHarvestRadius", defaults.cacofeyHarvestRadius(), 4, 64);
      builder.pop();
      builder.push("functional_blocks");
      CENSER_EFFECT_RADIUS = builder.defineInRange("censerEffectRadius", defaults.censerEffectRadius(), 1, 64);
      CENSER_EFFECT_DURATION = builder.defineInRange("censerEffectDuration", defaults.censerEffectDuration(), 20, 24000);
      BREWING_DURATION = builder.defineInRange("brewingDuration", defaults.brewingDuration(), 20, 24000);
      OVERCOOKED_DURATION = builder.defineInRange("overcookedDuration", defaults.overcookedDuration(), 20, 24000);
      DREAMCATCHER_RADIUS = builder.defineInRange("dreamcatcherRadius", defaults.dreamcatcherRadius(), 1, 64);
      PHANTOM_IGNITE_DURATION = builder.defineInRange("phantomIgniteDuration", defaults.phantomIgniteDuration(), 0, 600);
      EGG_CLUSTER_HATCH_DURATION = builder.defineInRange("eggClusterHatchDuration", defaults.eggClusterHatchDuration(), 20, 24000);
      NATURES_RITUAL_CROP_REQUIREMENT = builder.defineInRange("naturesRitualCropRequirement", defaults.naturesRitualCropRequirement(), 0, 32);
      builder.pop();
      builder.push("plants");
      NAUTILITE_DURATION = builder.defineInRange("nautiliteDuration", defaults.nautiliteDuration(), 100, 24000);
      NAUTILITE_EFFECT_RADIUS = builder.defineInRange("nautiliteEffectRadius", defaults.nautiliteEffectRadius(), 1, 64);
      WINDSONG_DURATION = builder.defineInRange("windsongDuration", defaults.windsongDuration(), 100, 24000);
      WINDSONG_EFFECT_RADIUS = builder.defineInRange("windsongEffectRadius", defaults.windsongEffectRadius(), 1, 32);
      ASTRYLIS_DURATION = builder.defineInRange("astrylisDuration", defaults.astrylisDuration(), 100, 24000);
      ASTRYLIS_BONEMEAL_INTERVAL = builder.defineInRange("astrylisBonemealInterval", defaults.astrylisBonemealInterval(), 20, 1200);
      MORPHORA_RADIUS = builder.defineInRange("morphoraEffectRadius", defaults.morphoraRadius(), 1, 32);
      GRIMSHADE_DURATION = builder.defineInRange("grimshadeDuration", defaults.grimshadeDuration(), 100, 24000);
      GRIMSHADE_EFFECT_RADIUS = builder.defineInRange("grimshadeEffectRadius", defaults.grimshadeEffectRadius(), 1, 64);
      GHOST_FERN_EMITS_PARTICLES = builder.define("ghostFernEmitsParticles", defaults.ghostFernEmitsParticles());
      CELESTIAL_BLOOM_EMITS_PARTICLES = builder.define("celestialBloomEmitsParticles", defaults.celestialBloomEmitsParticles());
      DREAMSHROOM_EMITS_PARTICLES = builder.define("dreamshroomEmitsParticles", defaults.dreamshroomEmitsParticles());
      LOURDES_DURATION = builder.defineInRange("lourdesDuration", defaults.lourdesDuration(), 100, 24000);
      LOURDES_EFFECT_RADIUS = builder.defineInRange("lourdesEffectRadius", defaults.lourdesEffectRadius(), 1.0, 64.0);
      builder.pop();
      SPEC = builder.build();
   }
}
