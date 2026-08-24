package io.github.razordevs.deep_aether.datagen.loot.modifiers;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class DAGlobalLootModifiers {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "deep_aether"
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> AETHER_DUNGEON_LOOT_CODEC = LOOT_MODIFIERS.register(
      "aether_dungeon_loot", DAAddDungeonLootModifier.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> AETHER_FISH_LOOT_CODEC = LOOT_MODIFIERS.register(
      "aether_fish_loot", DAFishingLootModifier.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> HALLOWEEN_LOOT_CODEC = LOOT_MODIFIERS.register(
      "halloween_loot", DAHalloweenLootModifier.CODEC
   );
}
