package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.world.features.InfectedDeepslateDiamondOreFeatureFeature;
import net.mcreator.borninchaosv.world.features.InfectedDiamondOreFeatureFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BornInChaosV1ModFeatures {
   public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, "born_in_chaos_v1");
   public static final DeferredHolder<Feature<?>, Feature<?>> INFECTED_DIAMOND_ORE_FEATURE = REGISTRY.register(
      "infected_diamond_ore_feature", InfectedDiamondOreFeatureFeature::new
   );
   public static final DeferredHolder<Feature<?>, Feature<?>> INFECTED_DEEPSLATE_DIAMOND_ORE_FEATURE = REGISTRY.register(
      "infected_deepslate_diamond_ore_feature", InfectedDeepslateDiamondOreFeatureFeature::new
   );
}
