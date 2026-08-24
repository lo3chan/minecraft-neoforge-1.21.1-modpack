package net.blay09.mods.balm.neoforge.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.balm.api.world.BiomePredicate;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.blay09.mods.balm.neoforge.world.level.biome.internal.NeoForgeBiomeModificationBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class NeoForgeBalmWorldGen implements BalmWorldGen {
   public static final MapCodec<BalmBiomeModifier> BALM_BIOME_MODIFIER_CODEC = MapCodec.unit(BalmBiomeModifier.INSTANCE);
   private static final List<BiomeModification> legacyBiomeModifications = Collections.synchronizedList(new ArrayList<>());
   private static final List<Pair<BiomePredicate, net.blay09.mods.balm.world.level.biome.BiomeModifier>> biomeModifiers = Collections.synchronizedList(
      new ArrayList<>()
   );

   public static void initializeBalmBiomeModifiers(IEventBus modBus) {
      DeferredRegister<MapCodec<? extends BiomeModifier>> registry = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, "balm");
      registry.register("balm", () -> BALM_BIOME_MODIFIER_CODEC);
      registry.register(modBus);
   }

   @Override
   public <T extends Feature<?>> DeferredObject<T> registerFeature(ResourceLocation identifier, Supplier<T> supplier) {
      DeferredRegister<Feature<?>> register = DeferredRegisters.get(Registries.FEATURE, identifier.getNamespace());
      DeferredHolder<Feature<?>, T> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public <T extends PlacementModifierType<?>> DeferredObject<T> registerPlacementModifier(ResourceLocation identifier, Supplier<T> supplier) {
      DeferredRegister<PlacementModifierType<?>> register = DeferredRegisters.get(Registries.PLACEMENT_MODIFIER_TYPE, identifier.getNamespace());
      DeferredHolder<PlacementModifierType<?>, T> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public <T extends PoiType> DeferredObject<T> registerPoiType(ResourceLocation identifier, Supplier<T> supplier) {
      DeferredRegister<PoiType> register = DeferredRegisters.get(Registries.POINT_OF_INTEREST_TYPE, identifier.getNamespace());
      DeferredHolder<PoiType, T> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public void addFeatureToBiomes(BiomePredicate biomePredicate, Decoration step, ResourceLocation placedFeatureIdentifier) {
      ResourceKey<PlacedFeature> resourceKey = ResourceKey.create(Registries.PLACED_FEATURE, placedFeatureIdentifier);
      legacyBiomeModifications.add(new BiomeModification(biomePredicate, step, resourceKey));
   }

   @Override
   public void modifyBiome(ResourceLocation id, BiomePredicate predicate, net.blay09.mods.balm.world.level.biome.BiomeModifier modifier) {
      biomeModifiers.add(Pair.of(predicate, modifier));
   }

   public void modifyBiome(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         for (BiomeModification biomeModification : legacyBiomeModifications) {
            ResourceLocation location = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
            if (location != null && biomeModification.getBiomePredicate().test(location, biome)) {
               Registry<PlacedFeature> placedFeatures = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
               placedFeatures.getHolder(biomeModification.getConfiguredFeatureKey())
                  .ifPresent(placedFeature -> builder.getGenerationSettings().addFeature(biomeModification.getStep(), placedFeature));
            }
         }

         NeoForgeBiomeModificationBuilder modificationBuilder = new NeoForgeBiomeModificationBuilder(builder);

         for (Pair<BiomePredicate, net.blay09.mods.balm.world.level.biome.BiomeModifier> biomeModifierPair : biomeModifiers) {
            ResourceLocation location = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
            if (location != null && ((BiomePredicate)biomeModifierPair.getFirst()).test(location, biome)) {
               ((net.blay09.mods.balm.world.level.biome.BiomeModifier)biomeModifierPair.getSecond()).modifyBiome(biome, modificationBuilder);
            }
         }
      }
   }
}
