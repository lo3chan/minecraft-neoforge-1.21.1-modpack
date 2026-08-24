package net.astralya.hexalia.neoforge.datagen;

import java.util.List;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.util.ModTags;
import net.astralya.hexalia.worldgen.ModPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public final class ModBiomeModifier {
   public static final ResourceKey<BiomeModifier> SPAWN_SILK_MOTH = registerKey("spawn_silk_moth");
   public static final ResourceKey<BiomeModifier> SPAWN_CACOFEY = registerKey("spawn_cacofey");
   public static final ResourceKey<BiomeModifier> ADD_CHILLBERRY = registerKey("add_chillberry");
   public static final ResourceKey<BiomeModifier> ADD_WILD_SUNFIRE_TOMATO = registerKey("add_wild_sunfire_tomato");
   public static final ResourceKey<BiomeModifier> ADD_WILD_MANDRAKE = registerKey("add_wild_mandrake");
   public static final ResourceKey<BiomeModifier> ADD_SPIRIT_BLOOM = registerKey("add_spirit_bloom");
   public static final ResourceKey<BiomeModifier> ADD_DREAMSHROOM = registerKey("add_dreamshroom");
   public static final ResourceKey<BiomeModifier> ADD_SIREN_KELP = registerKey("add_siren_kelp");
   public static final ResourceKey<BiomeModifier> ADD_GHOST_FERN = registerKey("add_ghost_fern");
   public static final ResourceKey<BiomeModifier> ADD_CELESTIAL_BLOOM = registerKey("add_celestial_bloom");
   public static final ResourceKey<BiomeModifier> ADD_SALTSPROUT = registerKey("add_saltsprout");
   public static final ResourceKey<BiomeModifier> ADD_LOTUS_FLOWER = registerKey("add_lotus_flower");
   public static final ResourceKey<BiomeModifier> ADD_WITCHWEED = registerKey("add_witchweed");
   public static final ResourceKey<BiomeModifier> ADD_PALE_MUSHROOM = registerKey("add_pale_mushroom");
   public static final ResourceKey<BiomeModifier> ADD_NIGHTSHADE = registerKey("add_nightshade");
   public static final ResourceKey<BiomeModifier> ADD_BEGONIA = registerKey("add_begonia");
   public static final ResourceKey<BiomeModifier> ADD_LAVENDER = registerKey("add_lavender");
   public static final ResourceKey<BiomeModifier> ADD_DAHLIA = registerKey("add_dahlia");
   public static final ResourceKey<BiomeModifier> ADD_DARK_OAK_COCOON = registerKey("add_dark_oak_cocoon");
   public static final ResourceKey<BiomeModifier> ADD_WILLOW = registerKey("add_willow");
   public static final ResourceKey<BiomeModifier> ADD_COTTONWOOD = registerKey("add_cottonwood");

   private ModBiomeModifier() {
   }

   public static void bootstrap(BootstrapContext<BiomeModifier> context) {
      HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
      HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
      context.register(
         SPAWN_SILK_MOTH,
         new AddSpawnsBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.SILK_MOTH_SPAWNS), List.of(new SpawnerData((EntityType)ModEntities.SILK_MOTH.get(), 2, 1, 2))
         )
      );
      context.register(
         SPAWN_CACOFEY,
         new AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.CACOFEY_SPAWNS), List.of(new SpawnerData((EntityType)ModEntities.CACOFEY.get(), 8, 1, 2)))
      );
      context.register(
         ADD_CHILLBERRY,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_COOL_BIOME_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.CHILLBERRY_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_WILD_SUNFIRE_TOMATO,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_DRY_BIOME_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.WILD_SUNFIRE_TOMATO_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_WILD_MANDRAKE,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_FLORAL_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.WILD_MANDRAKE_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_SPIRIT_BLOOM,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SWAMP_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.SPIRIT_BLOOM_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_DREAMSHROOM,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SHROOMS),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.DREAMSHROOM_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_SIREN_KELP,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SIREN_KELP),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.SIREN_KELP_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_CELESTIAL_BLOOM,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_FLORAL_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.CELESTIAL_BLOOM_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_GHOST_FERN,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SHADED_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.GHOST_FERN_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_SALTSPROUT,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_DRY_BIOME_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.SALTSPROUT_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_LOTUS_FLOWER,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SWAMP_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.LOTUS_FLOWER_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_BEGONIA,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_DECORATIVE_FLOWERS),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.BEGONIA_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_LAVENDER,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_COOL_BIOME_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.LAVENDER_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_DAHLIA,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_FLORAL_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.DAHLIA_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_WITCHWEED,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_FLORAL_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.WITCHWEED_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_PALE_MUSHROOM,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SHROOMS),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.PALE_MUSHROOM_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_NIGHTSHADE,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SHADED_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.NIGHTSHADE_BUSH_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_DARK_OAK_COCOON,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SHADED_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.DARK_OAK_COCOON_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_COTTONWOOD,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SWAMP_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.COTTONWOOD_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         ADD_WILLOW,
         new AddFeaturesBiomeModifier(
            biomes.getOrThrow(ModTags.Biomes.HAS_SWAMP_VEGETATION),
            HolderSet.direct(new Holder[]{placedFeatures.getOrThrow(ModPlacedFeatures.WILLOW_PLACED)}),
            Decoration.VEGETAL_DECORATION
         )
      );
   }

   private static ResourceKey<BiomeModifier> registerKey(String name) {
      return ResourceKey.create(Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("hexalia", name));
   }
}
