package com.github.alexthe666.alexsmobs.misc;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class AMLootRegistry {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> DEF_REG = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "alexsmobs"
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> BANANA_DROP = DEF_REG.register("banana_drop", BananaLootModifier.CODEC);
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> BLOSSOM_DROP = DEF_REG.register("blossom_drop", BlossomLootModifier.CODEC);
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> ANCIENT_DART = DEF_REG.register("ancient_dart", AncientDartLootModifier.CODEC);
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> PIGSHOES = DEF_REG.register("pigshoes", PigshoesLootModifier.CODEC);
}
