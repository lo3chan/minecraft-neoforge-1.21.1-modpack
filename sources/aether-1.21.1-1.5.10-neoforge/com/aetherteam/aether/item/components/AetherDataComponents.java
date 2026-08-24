package com.aetherteam.aether.item.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherDataComponents {
   public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "aether");
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LOCKED = DATA_COMPONENT_TYPES.register(
      "locked", () -> DataComponentType.builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
   );
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<DungeonKind>> DUNGEON_KIND = DATA_COMPONENT_TYPES.register(
      "dungeon_kind", () -> DataComponentType.builder().persistent(DungeonKind.CODEC).networkSynchronized(DungeonKind.STREAM_CODEC).build()
   );
}
