package cn.foggyhillside.ends_delight.registry;

import cn.foggyhillside.ends_delight.worldgen.ChorusSucculentFeature;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBiomeFeatures {
   public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "ends_delight");
   public static final Supplier<Feature<CountConfiguration>> CHORUS_SUCCULENT = FEATURES.register(
      "chorus_succulent", () -> new ChorusSucculentFeature(CountConfiguration.CODEC)
   );
}
