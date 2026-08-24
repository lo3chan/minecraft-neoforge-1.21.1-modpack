package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SwetBallRecipeCategory extends AbstractBiomeParameterRecipeCategory<SwetBallRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("aether", "swet_ball_conversion");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/gui/menu/jei_render.png");
   public static final RecipeType<SwetBallRecipe> RECIPE_TYPE = RecipeType.create("aether", "swet_ball_conversion", SwetBallRecipe.class);

   public SwetBallRecipeCategory(IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper) {
      super(
         "swet_ball_conversion",
         UID,
         guiHelper.createDrawable(TEXTURE, 0, 0, 84, 28),
         guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)AetherItems.SWET_BALL.get())),
         RECIPE_TYPE,
         fluidHelper
      );
   }
}
