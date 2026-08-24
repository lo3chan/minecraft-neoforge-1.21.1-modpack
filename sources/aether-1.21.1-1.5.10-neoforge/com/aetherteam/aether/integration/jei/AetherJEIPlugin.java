package com.aetherteam.aether.integration.jei;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.integration.jei.categories.ban.BlockBanRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.ban.ItemBanRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.block.AccessoryFreezableRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.block.AmbrosiumRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.block.IcestoneFreezableRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.block.PlacementConversionRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.block.SwetBallRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.fuel.AetherFuelCategory;
import com.aetherteam.aether.integration.jei.categories.fuel.AetherFuelRecipeMaker;
import com.aetherteam.aether.integration.jei.categories.item.AltarRepairRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.item.EnchantingRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.item.FreezingRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.item.IncubationRecipeCategory;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

@JeiPlugin
public class AetherJEIPlugin implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("aether", "jei");
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new EnchantingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new AltarRepairRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new IncubationRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new AetherFuelCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(
         new IRecipeCategory[]{new AmbrosiumRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())}
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{new SwetBallRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())}
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new IcestoneFreezableRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())
         }
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new AccessoryFreezableRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())
         }
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new PlacementConversionRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())
         }
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{new ItemBanRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())}
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{new BlockBanRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper())}
      );
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      List<? extends RecipeHolder<? extends AbstractAetherCookingRecipe>> unfilteredRecipes = rm.getAllRecipesFor(
         (RecipeType)AetherRecipeTypes.ENCHANTING.get()
      );
      List<EnchantingRecipe> enchantingRecipes = new ArrayList<>();
      List<AltarRepairRecipe> repairRecipes = new ArrayList<>();
      unfilteredRecipes.stream()
         .filter(recipe -> recipe.value() instanceof EnchantingRecipe)
         .forEach(recipe -> enchantingRecipes.add((EnchantingRecipe)recipe.value()));
      unfilteredRecipes.stream()
         .filter(recipe -> recipe.value() instanceof AltarRepairRecipe)
         .forEach(recipe -> repairRecipes.add((AltarRepairRecipe)recipe.value()));
      registration.addRecipes(EnchantingRecipeCategory.RECIPE_TYPE, enchantingRecipes);
      registration.addRecipes(AltarRepairRecipeCategory.RECIPE_TYPE, repairRecipes);
      registration.addRecipes(
         FreezingRecipeCategory.RECIPE_TYPE, rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.FREEZING.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         IncubationRecipeCategory.RECIPE_TYPE, rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.INCUBATION.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(AetherFuelCategory.RECIPE_TYPE, AetherFuelRecipeMaker.getFuelRecipes());
      registration.addRecipes(
         AmbrosiumRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.AMBROSIUM_ENCHANTING.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         SwetBallRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.SWET_BALL_CONVERSION.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         IcestoneFreezableRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.ICESTONE_FREEZABLE.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         AccessoryFreezableRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.ACCESSORY_FREEZABLE.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         PlacementConversionRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.PLACEMENT_CONVERSION.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         ItemBanRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.ITEM_PLACEMENT_BAN.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         BlockBanRecipeCategory.RECIPE_TYPE,
         rm.getAllRecipesFor((RecipeType)AetherRecipeTypes.BLOCK_PLACEMENT_BAN.get()).stream().map(RecipeHolder::value).toList()
      );
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.ALTAR.get()),
         new mezz.jei.api.recipe.RecipeType[]{EnchantingRecipeCategory.RECIPE_TYPE, AltarRepairRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.FREEZER.get()),
         new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.INCUBATOR.get()),
         new mezz.jei.api.recipe.RecipeType[]{IncubationRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherItems.AMBROSIUM_SHARD.get()), new mezz.jei.api.recipe.RecipeType[]{AmbrosiumRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherItems.SWET_BALL.get()), new mezz.jei.api.recipe.RecipeType[]{SwetBallRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.ICESTONE.get()), new mezz.jei.api.recipe.RecipeType[]{IcestoneFreezableRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.ICESTONE_SLAB.get()), new mezz.jei.api.recipe.RecipeType[]{IcestoneFreezableRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.ICESTONE_STAIRS.get()), new mezz.jei.api.recipe.RecipeType[]{IcestoneFreezableRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherBlocks.ICESTONE_WALL.get()), new mezz.jei.api.recipe.RecipeType[]{IcestoneFreezableRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherItems.ICE_RING.get()), new mezz.jei.api.recipe.RecipeType[]{AccessoryFreezableRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherItems.ICE_PENDANT.get()), new mezz.jei.api.recipe.RecipeType[]{AccessoryFreezableRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)AetherItems.AETHER_PORTAL_FRAME.get()), new mezz.jei.api.recipe.RecipeType[]{PlacementConversionRecipeCategory.RECIPE_TYPE}
      );
      registration.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL), new mezz.jei.api.recipe.RecipeType[]{ItemBanRecipeCategory.RECIPE_TYPE});
      registration.addRecipeCatalyst(new ItemStack(Blocks.TORCH), new mezz.jei.api.recipe.RecipeType[]{BlockBanRecipeCategory.RECIPE_TYPE});
   }
}
