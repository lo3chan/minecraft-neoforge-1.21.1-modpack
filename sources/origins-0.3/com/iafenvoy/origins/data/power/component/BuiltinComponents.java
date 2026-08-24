package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.data.power.component.builtin.ActiveComponent;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.iafenvoy.origins.data.power.component.builtin.EmptyComponent;
import com.iafenvoy.origins.data.power.component.builtin.EntitySetComponent;
import com.iafenvoy.origins.data.power.component.builtin.InventoryComponent;
import com.iafenvoy.origins.data.power.component.builtin.ResourceComponent;
import com.iafenvoy.origins.data.power.component.builtin.ToggleComponent;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BuiltinComponents {
   public static final DeferredRegister<MapCodec<? extends PowerComponent>> REGISTRY = DeferredRegister.create(
      PowerComponentRegistries.POWER_COMPONENT_TYPE, "origins"
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<EmptyComponent>> EMPTY = REGISTRY.register(
      "empty", () -> EmptyComponent.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<ActiveComponent>> ACTIVE = REGISTRY.register(
      "active", () -> ActiveComponent.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<CooldownComponent>> COOLDOWN = REGISTRY.register(
      "cooldown", () -> CooldownComponent.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<EntitySetComponent>> ENTITY_SET = REGISTRY.register(
      "entity_set", () -> EntitySetComponent.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<InventoryComponent>> INVENTORY = REGISTRY.register(
      "inventory", () -> InventoryComponent.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<ResourceComponent>> RESOURCE = REGISTRY.register(
      "resource", () -> ResourceComponent.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends PowerComponent>, MapCodec<ToggleComponent>> TOGGLE = REGISTRY.register(
      "toggle", () -> ToggleComponent.CODEC
   );
}
