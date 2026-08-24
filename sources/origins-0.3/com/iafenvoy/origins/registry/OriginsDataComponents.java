package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.layer.Layer;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginsDataComponents {
   public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, "origins");
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<Layer>>>> ORB_LAYERS = REGISTRY.register(
      "orb_layers", () -> DataComponentType.builder().persistent(Layer.CODEC.listOf()).build()
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemPowersComponent>> ITEM_POWERS = REGISTRY.register(
      "item_powers", () -> DataComponentType.builder().persistent(ItemPowersComponent.CODEC).build()
   );
}
