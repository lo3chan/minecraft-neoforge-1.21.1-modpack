package com.aetherteam.aether.integration.jei.categories.ban;

import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.nitrogen.integration.jei.BlockStateRenderer;
import com.aetherteam.nitrogen.integration.jei.FluidStateRenderer;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.input.BlockStateRecipeInput;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.common.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBanRecipeCategory extends AbstractPlacementBanRecipeCategory<BlockState, BlockStateIngredient, BlockStateRecipeInput, BlockBanRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("aether", "block_placement_ban");
   public static final RecipeType<BlockBanRecipe> RECIPE_TYPE = RecipeType.create("aether", "block_placement_ban", BlockBanRecipe.class);

   public BlockBanRecipeCategory(IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper) {
      super(
         guiHelper,
         "block_placement_ban",
         UID,
         guiHelper.createBlankDrawable(116, 18),
         guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.TORCH)),
         RECIPE_TYPE,
         fluidHelper
      );
   }

   public void setRecipe(IRecipeLayoutBuilder builder, BlockBanRecipe recipe, IFocusGroup focusGroup) {
      BlockStateIngredient ingredient = recipe.getIngredient();
      BlockPropertyPair[] pairs = ingredient.getPairs();
      List<Object> ingredients = this.setupIngredients(pairs);
      if (!recipe.getBypassBlock().isEmpty() && !recipe.getBypassBlock().get().isEmpty()) {
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredientsUnsafe(ingredients))
            .setCustomRenderer(Services.PLATFORM.getFluidHelper().getFluidIngredientType(), new FluidStateRenderer(Services.PLATFORM.getFluidHelper()))
            .setCustomRenderer(VanillaTypes.ITEM_STACK, new BlockStateRenderer(pairs))
            .addTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip));
      } else {
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 50, 1).addIngredientsUnsafe(ingredients))
            .setCustomRenderer(Services.PLATFORM.getFluidHelper().getFluidIngredientType(), new FluidStateRenderer(Services.PLATFORM.getFluidHelper()))
            .setCustomRenderer(VanillaTypes.ITEM_STACK, new BlockStateRenderer(pairs))
            .addTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip));
      }

      super.setRecipe(builder, recipe, focusGroup);
   }
}
