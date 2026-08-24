package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.world.features.DunzhongbiggenerationFeature;
import net.mcreator.undeadrevamp.world.features.DunzhongsmallgeneratgeFeature;
import net.mcreator.undeadrevamp.world.features.Empthpillar1Feature;
import net.mcreator.undeadrevamp.world.features.InducerFeature;
import net.mcreator.undeadrevamp.world.features.SpawnpillarFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UndeadRevamp2ModFeatures {
   public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, "undead_revamp2");
   public static final DeferredHolder<Feature<?>, Feature<?>> DUNZHONGSMALLGENERATGE = REGISTRY.register(
      "dunzhongsmallgeneratge", DunzhongsmallgeneratgeFeature::new
   );
   public static final DeferredHolder<Feature<?>, Feature<?>> DUNZHONGBIGGENERATION = REGISTRY.register(
      "dunzhongbiggeneration", DunzhongbiggenerationFeature::new
   );
   public static final DeferredHolder<Feature<?>, Feature<?>> EMPTHPILLAR_1 = REGISTRY.register("empthpillar_1", Empthpillar1Feature::new);
   public static final DeferredHolder<Feature<?>, Feature<?>> SPAWNPILLAR = REGISTRY.register("spawnpillar", SpawnpillarFeature::new);
   public static final DeferredHolder<Feature<?>, Feature<?>> INDUCER = REGISTRY.register("inducer", InducerFeature::new);
}
