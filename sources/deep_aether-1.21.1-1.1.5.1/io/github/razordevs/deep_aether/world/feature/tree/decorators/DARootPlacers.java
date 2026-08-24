package io.github.razordevs.deep_aether.world.feature.tree.decorators;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DARootPlacers {
   public static final DeferredRegister<RootPlacerType<?>> ROOT_PLACERS = DeferredRegister.create(BuiltInRegistries.ROOT_PLACER_TYPE, "deep_aether");
   public static final DeferredHolder<RootPlacerType<?>, RootPlacerType<YagrootRootPlacer>> YAGROOT_ROOT_PLACER = ROOT_PLACERS.register(
      "yagroot_root_placer", () -> new RootPlacerType(YagrootRootPlacer.CODEC)
   );
}
