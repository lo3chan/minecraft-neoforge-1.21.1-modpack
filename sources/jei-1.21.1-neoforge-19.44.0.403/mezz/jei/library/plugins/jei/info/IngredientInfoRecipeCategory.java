package mezz.jei.library.plugins.jei.info;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiIngredientInfoRecipe;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class IngredientInfoRecipeCategory extends AbstractRecipeCategory<IJeiIngredientInfoRecipe> {
   private static final int recipeWidth = 170;
   private static final int recipeHeight = 125;

   public IngredientInfoRecipeCategory(Textures textures) {
      super(RecipeTypes.INFORMATION, Component.translatable("gui.jei.category.itemInformation"), textures.getInfoIcon(), 170, 125);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, IJeiIngredientInfoRecipe recipe, IFocusGroup focuses) {
      int xPos = 77;
      IRecipeSlotBuilder inputSlotBuilder = builder.addInputSlot(xPos, 1).setStandardSlotBackground();
      IIngredientAcceptor<?> outputSlotBuilder = builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT);

      for (ITypedIngredient<?> typedIngredient : recipe.getIngredients()) {
         inputSlotBuilder.addTypedIngredient(typedIngredient);
         outputSlotBuilder.addTypedIngredient(typedIngredient);
      }
   }

   public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiIngredientInfoRecipe recipe, IFocusGroup focuses) {
      int yPos = 22;
      int height = 125 - yPos;
      builder.addScrollBoxWidget(170, height, 0, yPos).setContents(recipe.getDescription());
   }

   @Nullable
   public ResourceLocation getRegistryName(IJeiIngredientInfoRecipe recipe) {
      return null;
   }
}
