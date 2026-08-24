package com.aetherteam.aether.world.foliageplacer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherFoliagePlacerTypes {
   public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(BuiltInRegistries.FOLIAGE_PLACER_TYPE, "aether");
   public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<CrystalFoliagePlacer>> CRYSTAL_FOLIAGE_PLACER = FOLIAGE_PLACERS.register(
      "crystal_foliage_placer", () -> new FoliagePlacerType(CrystalFoliagePlacer.CODEC)
   );
   public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<HolidayFoliagePlacer>> HOLIDAY_FOLIAGE_PLACER = FOLIAGE_PLACERS.register(
      "holiday_foliage_placer", () -> new FoliagePlacerType(HolidayFoliagePlacer.CODEC)
   );
   public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<GoldenOakFoliagePlacer>> GOLDEN_OAK_FOLIAGE_PLACER = FOLIAGE_PLACERS.register(
      "golden_oak_foliage_placer", () -> new FoliagePlacerType(GoldenOakFoliagePlacer.CODEC)
   );
}
