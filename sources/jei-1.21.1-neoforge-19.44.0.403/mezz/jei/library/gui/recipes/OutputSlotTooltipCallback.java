package mezz.jei.library.gui.recipes;

import java.util.Optional;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class OutputSlotTooltipCallback implements IRecipeSlotRichTooltipCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation recipeName;
   private final boolean recipeFromSameModAsCategory;

   public OutputSlotTooltipCallback(ResourceLocation recipeName, RecipeType<?> recipeType) {
      this.recipeName = recipeName;
      this.recipeFromSameModAsCategory = recipeName.getNamespace().equals(recipeType.getUid().getNamespace());
   }

   @Override
   public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
      if (recipeSlotView.getRole() == RecipeIngredientRole.OUTPUT) {
         Optional<ITypedIngredient<?>> displayedIngredient = recipeSlotView.getDisplayedIngredient();
         if (!displayedIngredient.isEmpty()) {
            this.addRecipeBy(tooltip, displayedIngredient.get());
            Minecraft minecraft = Minecraft.getInstance();
            boolean showAdvanced = minecraft.options.advancedItemTooltips || Screen.hasShiftDown();
            if (showAdvanced) {
               MutableComponent recipeId = Component.translatable("jei.tooltip.recipe.id", new Object[]{Component.literal(this.recipeName.toString())});
               tooltip.add(recipeId.withStyle(ChatFormatting.DARK_GRAY));
            }
         }
      }
   }

   private void addRecipeBy(ITooltipBuilder tooltip, ITypedIngredient<?> displayedIngredient) {
      if (!this.recipeFromSameModAsCategory) {
         IModIdHelper modIdHelper = Internal.getJeiRuntime().getJeiHelpers().getModIdHelper();
         if (modIdHelper.isDisplayingModNameEnabled()) {
            String ingredientModId = this.getDisplayModId(displayedIngredient);
            if (ingredientModId != null) {
               String recipeModId = this.recipeName.getNamespace();
               if (!recipeModId.equals(ingredientModId)) {
                  Component modName = modIdHelper.getFormattedModNameComponentForModId(recipeModId);
                  MutableComponent recipeBy = Component.translatable("jei.tooltip.recipe.by", new Object[]{modName});
                  tooltip.add(recipeBy.withStyle(ChatFormatting.GRAY));
               }
            }
         }
      }
   }

   @Nullable
   private <T> String getDisplayModId(ITypedIngredient<T> typedIngredient) {
      IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
      IIngredientType<T> type = typedIngredient.getType();
      T ingredient = typedIngredient.getIngredient();
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(type);

      try {
         return ingredientHelper.getDisplayModId(ingredient);
      } catch (RuntimeException var8) {
         String ingredientInfo = ErrorUtil.getIngredientInfo(ingredient, type, ingredientManager);
         LOGGER.error("Caught exception from ingredient without a resource location: {}", ingredientInfo, var8);
         return null;
      }
   }
}
