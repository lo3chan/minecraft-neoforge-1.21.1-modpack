package com.aetherteam.aether.recipe;

import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.AmbrosiumRecipe;
import com.aetherteam.aether.recipe.recipes.block.IcestoneFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherRecipeTypes {
   public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, "aether");
   public static final DeferredHolder<RecipeType<?>, RecipeType<? extends AbstractAetherCookingRecipe>> ENCHANTING = RECIPE_TYPES.register(
      "enchanting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "enchanting"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.register(
      "freezing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "freezing"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<IncubationRecipe>> INCUBATION = RECIPE_TYPES.register(
      "incubation", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "incubation"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<AmbrosiumRecipe>> AMBROSIUM_ENCHANTING = RECIPE_TYPES.register(
      "ambrosium_enchanting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "ambrosium_enchanting"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<SwetBallRecipe>> SWET_BALL_CONVERSION = RECIPE_TYPES.register(
      "swet_ball_conversion", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "swet_ball_conversion"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<IcestoneFreezableRecipe>> ICESTONE_FREEZABLE = RECIPE_TYPES.register(
      "icestone_freezable", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "icestone_freezable"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<AccessoryFreezableRecipe>> ACCESSORY_FREEZABLE = RECIPE_TYPES.register(
      "accessory_freezable", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "accessory_freezable"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<PlacementConversionRecipe>> PLACEMENT_CONVERSION = RECIPE_TYPES.register(
      "placement_conversion", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "placement_conversion"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<ItemBanRecipe>> ITEM_PLACEMENT_BAN = RECIPE_TYPES.register(
      "item_placement_ban", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "item_placement_ban"))
   );
   public static final DeferredHolder<RecipeType<?>, RecipeType<BlockBanRecipe>> BLOCK_PLACEMENT_BAN = RECIPE_TYPES.register(
      "block_placement_ban", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath("aether", "block_placement_ban"))
   );
}
