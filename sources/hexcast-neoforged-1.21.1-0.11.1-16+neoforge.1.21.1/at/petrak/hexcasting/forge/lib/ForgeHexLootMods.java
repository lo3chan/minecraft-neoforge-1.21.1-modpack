package at.petrak.hexcasting.forge.lib;

import at.petrak.hexcasting.forge.loot.ForgeHexAmethystLootMod;
import at.petrak.hexcasting.forge.loot.ForgeHexLoreLootMod;
import at.petrak.hexcasting.forge.loot.ForgeHexScrollLootMod;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class ForgeHexLootMods {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "hexcasting"
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ForgeHexScrollLootMod>> INJECT_SCROLLS = REGISTRY.register(
      "inject_scrolls", ForgeHexScrollLootMod.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ForgeHexLoreLootMod>> INJECT_LORE = REGISTRY.register(
      "inject_lore", ForgeHexLoreLootMod.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ForgeHexAmethystLootMod>> AMETHYST = REGISTRY.register(
      "amethyst_cluster", ForgeHexAmethystLootMod.CODEC
   );
}
