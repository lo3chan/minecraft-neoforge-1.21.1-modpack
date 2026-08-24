package io.github.razordevs.deep_aether.item.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.DataComponents;

public class DADataComponentTypes {
   public static final DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "deep_aether");
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<DungeonTracker>> DUNGEON_TRACKER = DATA_COMPONENT_TYPES.registerComponentType(
      "dungeon_tracker", builder -> builder.persistent(DungeonTracker.CODEC).networkSynchronized(DungeonTracker.STREAM_CODEC)
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<MoaFodder>> MOA_FODDER = DATA_COMPONENT_TYPES.registerComponentType(
      "moa_fodder", builder -> builder.persistent(MoaFodder.CODEC).networkSynchronized(MoaFodder.STREAM_CODEC)
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<FloatyScarf>> FLOATY_SCARF = DATA_COMPONENT_TYPES.registerComponentType(
      "floaty_scarf", builder -> builder.persistent(FloatyScarf.CODEC).networkSynchronized(FloatyScarf.STREAM_CODEC)
   );
}
