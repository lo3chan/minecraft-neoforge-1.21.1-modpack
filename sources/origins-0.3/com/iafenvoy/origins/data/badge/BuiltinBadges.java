package com.iafenvoy.origins.data.badge;

import com.iafenvoy.origins.data.badge.builtin.CraftingRecipeBadge;
import com.iafenvoy.origins.data.badge.builtin.EmptyBadge;
import com.iafenvoy.origins.data.badge.builtin.KeybindBadge;
import com.iafenvoy.origins.data.badge.builtin.SpriteBadge;
import com.iafenvoy.origins.data.badge.builtin.TooltipBadge;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BuiltinBadges {
   public static final DeferredRegister<MapCodec<? extends Badge>> REGISTRY = DeferredRegister.create(BadgeRegistries.BADGE_TYPE, "origins");
   public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<EmptyBadge>> EMPTY = REGISTRY.register("empty", () -> EmptyBadge.CODEC);
   public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<CraftingRecipeBadge>> CRAFTING_RECIPE = REGISTRY.register(
      "crafting_recipe", () -> CraftingRecipeBadge.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<KeybindBadge>> KEYBIND = REGISTRY.register("keybind", () -> KeybindBadge.CODEC);
   public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<SpriteBadge>> SPRITE = REGISTRY.register("sprite", () -> SpriteBadge.CODEC);
   public static final DeferredHolder<MapCodec<? extends Badge>, MapCodec<TooltipBadge>> TOOLTIP = REGISTRY.register("tooltip", () -> TooltipBadge.CODEC);
}
