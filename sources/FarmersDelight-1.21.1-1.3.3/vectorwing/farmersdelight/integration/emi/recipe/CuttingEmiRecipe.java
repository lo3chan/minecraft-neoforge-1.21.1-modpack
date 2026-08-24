package vectorwing.farmersdelight.integration.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.RecipeUtils;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;

public class CuttingEmiRecipe implements EmiRecipe {
   private static final ResourceLocation BACKGROUND = RecipeUtils.FDLocation("textures/gui/jei/cutting_board.png");
   public static final int OUTPUT_GRID_X = 69;
   public static final int OUTPUT_GRID_Y = 3;
   private final ResourceLocation id;
   private final EmiIngredient tool;
   private final EmiIngredient input;
   private final List<EmiStack> outputs;

   public CuttingEmiRecipe(ResourceLocation id, EmiIngredient tool, EmiIngredient input, List<EmiStack> outputs) {
      this.id = id;
      this.tool = tool;
      this.input = input;
      this.outputs = outputs;
   }

   public EmiRecipeCategory getCategory() {
      return FDRecipeCategories.CUTTING;
   }

   @Nullable
   public ResourceLocation getId() {
      return this.id;
   }

   public List<EmiIngredient> getInputs() {
      return List.of(this.input);
   }

   public List<EmiStack> getOutputs() {
      return this.outputs;
   }

   public List<EmiIngredient> getCatalysts() {
      return List.of(this.tool);
   }

   public int getDisplayWidth() {
      return 107;
   }

   public int getDisplayHeight() {
      return 44;
   }

   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(BACKGROUND, 0, 0, 107, 44, 4, 7);
      widgets.addSlot(this.tool, 11, 0).drawBack(false);
      widgets.addSlot(this.input, 11, 19).drawBack(false);
      int size = this.outputs.size();
      int centerX = size > 1 ? 1 : 10;
      int centerY = size > 2 ? 1 : 10;

      for (int i = 0; i < size; i++) {
         int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
         int yOffset = centerY + i / 2 * 19;
         EmiIngredient output = (EmiIngredient)this.outputs.get(i);
         widgets.addSlot(output, 69 + xOffset, 3 + yOffset).backgroundTexture(BACKGROUND, output.getChance() < 1.0F ? 18 : 0, 58).recipeContext(this);
      }
   }
}
