package com.aetherteam.aether.integration.rei;

import com.aetherteam.aether.integration.rei.categories.ban.PlacementBanRecipeDisplay;
import com.aetherteam.aether.integration.rei.categories.item.AetherCookingRecipeDisplay;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.AmbrosiumRecipe;
import com.aetherteam.aether.recipe.recipes.block.IcestoneFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import com.aetherteam.nitrogen.integration.rei.displays.BlockStateRecipeDisplay;
import com.aetherteam.nitrogen.integration.rei.displays.FuelDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.forge.REIPluginCommon;
import net.minecraft.resources.ResourceLocation;

@REIPluginCommon
public class AetherREIServerPlugin implements REIServerPlugin {
   public static final CategoryIdentifier<PlacementBanRecipeDisplay<BlockBanRecipe>> BLOCK_PLACEMENT_BAN = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "block_placement_ban")
   );
   public static final CategoryIdentifier<PlacementBanRecipeDisplay<ItemBanRecipe>> ITEM_PLACEMENT_BAN = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "item_placement_ban")
   );
   public static final CategoryIdentifier<BlockStateRecipeDisplay<AccessoryFreezableRecipe>> ACCESSORY_FREEZABLE = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "accessory_freezable")
   );
   public static final CategoryIdentifier<BlockStateRecipeDisplay<AmbrosiumRecipe>> AMBROSIUM_ENCHANTING = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "ambrosium_enchanting")
   );
   public static final CategoryIdentifier<BlockStateRecipeDisplay<IcestoneFreezableRecipe>> ICESTONE_FREEZABLE = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "icestone_freezable")
   );
   public static final CategoryIdentifier<BlockStateRecipeDisplay<PlacementConversionRecipe>> PLACEMENT_CONVERSION = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "placement_conversion")
   );
   public static final CategoryIdentifier<BlockStateRecipeDisplay<SwetBallRecipe>> SWET_BALL_CONVERSION = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "swet_ball_conversion")
   );
   public static final CategoryIdentifier<FuelDisplay> AETHER_FUEL = CategoryIdentifier.of(ResourceLocation.fromNamespaceAndPath("aether", "fuel"));
   public static final CategoryIdentifier<AetherCookingRecipeDisplay<AltarRepairRecipe>> ALTAR_REPAIR = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "repairing")
   );
   public static final CategoryIdentifier<AetherCookingRecipeDisplay<EnchantingRecipe>> ALTAR_ENCHANTING = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "enchanting")
   );
   public static final CategoryIdentifier<AetherCookingRecipeDisplay<FreezingRecipe>> FREEZING = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "freezing")
   );
   public static final CategoryIdentifier<AetherCookingRecipeDisplay<IncubationRecipe>> INCUBATING = CategoryIdentifier.of(
      ResourceLocation.fromNamespaceAndPath("aether", "incubating")
   );

   public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
      registry.register(ALTAR_REPAIR, AetherCookingRecipeDisplay.serializer(ALTAR_REPAIR));
      registry.register(ALTAR_ENCHANTING, AetherCookingRecipeDisplay.serializer(ALTAR_ENCHANTING));
      registry.register(FREEZING, AetherCookingRecipeDisplay.serializer(FREEZING));
      registry.register(INCUBATING, AetherCookingRecipeDisplay.serializer(INCUBATING));
   }
}
