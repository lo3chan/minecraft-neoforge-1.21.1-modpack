package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class PlacementConversionRecipeCategory extends AbstractBiomeParameterRecipeCategory<PlacementConversionRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("aether", "placement_conversion");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/menu/jei_render.png");
   public static final RecipeType<PlacementConversionRecipe> RECIPE_TYPE = RecipeType.create("aether", "placement_conversion", PlacementConversionRecipe.class);

   public PlacementConversionRecipeCategory(IGuiHelper helper, IPlatformFluidHelper<?> fluidHelper) {
      super(
         "placement_conversion",
         UID,
         helper.createDrawable(TEXTURE, 0, 0, 84, 28),
         helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)AetherItems.AETHER_PORTAL_FRAME.get())),
         RECIPE_TYPE,
         fluidHelper
      );
   }
}
