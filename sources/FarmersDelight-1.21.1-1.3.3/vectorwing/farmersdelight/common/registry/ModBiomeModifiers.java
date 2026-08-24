package vectorwing.farmersdelight.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import vectorwing.farmersdelight.common.world.WildCropGeneration;
import vectorwing.farmersdelight.common.world.modifier.AddFeaturesByFilterBiomeModifier;

public class ModBiomeModifiers {
   public static DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(
      NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS, "farmersdelight"
   );
   public static Supplier<MapCodec<AddFeaturesByFilterBiomeModifier>> ADD_FEATURES_BY_FILTER = BIOME_MODIFIER_SERIALIZERS.register(
      "add_features_by_filter",
      () -> RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Biome.LIST_CODEC.fieldOf("allowed_biomes").forGetter(AddFeaturesByFilterBiomeModifier::allowedBiomes),
               Biome.LIST_CODEC.optionalFieldOf("denied_biomes").orElse(Optional.empty()).forGetter(AddFeaturesByFilterBiomeModifier::deniedBiomes),
               Codec.FLOAT.optionalFieldOf("min_temperature").orElse(Optional.empty()).forGetter(AddFeaturesByFilterBiomeModifier::minimumTemperature),
               Codec.FLOAT.optionalFieldOf("max_temperature").orElse(Optional.empty()).forGetter(AddFeaturesByFilterBiomeModifier::maximumTemperature),
               PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesByFilterBiomeModifier::features),
               Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesByFilterBiomeModifier::step)
            )
            .apply(builder, AddFeaturesByFilterBiomeModifier::new)
      )
   );
   public static ResourceKey<BiomeModifier> WILD_CABBAGES = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_cabbages")
   );
   public static ResourceKey<BiomeModifier> WILD_ONIONS = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_onions")
   );
   public static ResourceKey<BiomeModifier> WILD_TOMATOES = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_tomatoes")
   );
   public static ResourceKey<BiomeModifier> WILD_CARROTS = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_carrots")
   );
   public static ResourceKey<BiomeModifier> WILD_POTATOES = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_potatoes")
   );
   public static ResourceKey<BiomeModifier> WILD_BEETROOTS = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_beetroots")
   );
   public static ResourceKey<BiomeModifier> WILD_RICE = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "wild_rice")
   );
   public static ResourceKey<BiomeModifier> BROWN_MUSHROOM_COLONIES = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "brown_mushroom_colony")
   );
   public static ResourceKey<BiomeModifier> RED_MUSHROOM_COLONIES = ResourceKey.create(
      Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath("farmersdelight", "red_mushroom_colony")
   );

   public static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
      HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
      HolderGetter<PlacedFeature> placedFeatureGetter = context.lookup(Registries.PLACED_FEATURE);
      context.register(
         WILD_CABBAGES,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(BiomeTags.IS_BEACH),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_CABBAGES)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         WILD_ONIONS,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
            Optional.of(HolderSet.direct(new Holder[]{biomeGetter.getOrThrow(Biomes.LUSH_CAVES), biomeGetter.getOrThrow(Biomes.MUSHROOM_FIELDS)})),
            Optional.of(0.4F),
            Optional.of(0.9F),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_ONIONS)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         WILD_TOMATOES,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_HOT_OVERWORLD),
            Optional.of(biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_WET)),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_TOMATOES)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         WILD_CARROTS,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
            Optional.of(HolderSet.direct(new Holder[]{biomeGetter.getOrThrow(Biomes.LUSH_CAVES), biomeGetter.getOrThrow(Biomes.MUSHROOM_FIELDS)})),
            Optional.of(0.4F),
            Optional.of(0.9F),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_CARROTS)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         WILD_POTATOES,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
            Optional.of(biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_UNDERGROUND)),
            Optional.of(0.1F),
            Optional.of(0.3F),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_POTATOES)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         WILD_BEETROOTS,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(BiomeTags.IS_BEACH),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_BEETROOTS)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         WILD_RICE,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_WET_OVERWORLD),
            Optional.of(biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_UNDERGROUND)),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_WILD_RICE)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         BROWN_MUSHROOM_COLONIES,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_MUSHROOM),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_BROWN_MUSHROOM_COLONIES)}),
            Decoration.VEGETAL_DECORATION
         )
      );
      context.register(
         RED_MUSHROOM_COLONIES,
         new AddFeaturesByFilterBiomeModifier(
            biomeGetter.getOrThrow(net.neoforged.neoforge.common.Tags.Biomes.IS_MUSHROOM),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            HolderSet.direct(new Holder[]{placedFeatureGetter.getOrThrow(WildCropGeneration.PATCH_RED_MUSHROOM_COLONIES)}),
            Decoration.VEGETAL_DECORATION
         )
      );
   }

   private static BiomeModifier createSimpleModifier(Holder<Biome> biomeHolder, Holder<PlacedFeature> placedFeatureHolder) {
      return new AddFeaturesBiomeModifier(
         HolderSet.direct(new Holder[]{biomeHolder}), HolderSet.direct(new Holder[]{placedFeatureHolder}), Decoration.VEGETAL_DECORATION
      );
   }
}
