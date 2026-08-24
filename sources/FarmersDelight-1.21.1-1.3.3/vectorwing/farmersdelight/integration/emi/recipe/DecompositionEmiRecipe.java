package vectorwing.farmersdelight.integration.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;
import vectorwing.farmersdelight.common.utility.RecipeUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;
import vectorwing.farmersdelight.integration.emi.FDRecipeWorkstations;

public class DecompositionEmiRecipe implements EmiRecipe {
   public static final ResourceLocation ID = RecipeUtils.FDLocation("/decomposition/dummy");
   private static final ResourceLocation BACKGROUND = RecipeUtils.FDLocation("textures/gui/jei/decomposition.png");
   private static final EmiStack RICH_SOIL = EmiStack.of((ItemLike)ModItems.RICH_SOIL.get());
   private static final EmiIngredient ACCELERATORS = EmiIngredient.of(ModTags.Blocks.COMPOST_ACTIVATORS);
   private static final ClientTooltipComponent LIGHT_TOOLTIP = createTooltip("light");
   private static final ClientTooltipComponent FLUID_TOOLTIP = createTooltip("fluid");
   private static final ClientTooltipComponent ACCELERATORS_TOOLTIP = createTooltip("accelerators");

   public EmiRecipeCategory getCategory() {
      return FDRecipeCategories.DECOMPOSITION;
   }

   @Nullable
   public ResourceLocation getId() {
      return ID;
   }

   public List<EmiIngredient> getInputs() {
      return List.of(FDRecipeWorkstations.ORGANIC_COMPOST);
   }

   public List<EmiStack> getOutputs() {
      return List.of(RICH_SOIL);
   }

   public int getDisplayWidth() {
      return 102;
   }

   public int getDisplayHeight() {
      return 62;
   }

   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(BACKGROUND, 0, 0, 102, 41, 8, 9);
      this.addSlot(widgets, FDRecipeWorkstations.ORGANIC_COMPOST, 0, 16);
      this.addSlot(widgets, RICH_SOIL, 84, 16).recipeContext(this);
      this.addSlot(widgets, ACCELERATORS, 55, 44);
      widgets.addTooltip((mouseX, mouseY) -> {
         if (ClientRenderUtils.isCursorInsideBounds(32, 30, 11, 11, mouseX.intValue(), mouseY.intValue())) {
            return List.of(LIGHT_TOOLTIP);
         } else if (ClientRenderUtils.isCursorInsideBounds(45, 30, 11, 11, mouseX.intValue(), mouseY.intValue())) {
            return List.of(FLUID_TOOLTIP);
         } else {
            return ClientRenderUtils.isCursorInsideBounds(59, 30, 11, 11, mouseX.intValue(), mouseY.intValue()) ? List.of(ACCELERATORS_TOOLTIP) : List.of();
         }
      }, 0, 0, widgets.getWidth(), widgets.getHeight());
   }

   private SlotWidget addSlot(WidgetHolder widgets, EmiIngredient ingredient, int x, int y) {
      return widgets.addSlot(ingredient, x, y).backgroundTexture(BACKGROUND, 119, 0);
   }

   private static ClientTooltipComponent createTooltip(@NotNull String suffix) {
      return ClientTooltipComponent.create(TextUtils.JEI("decomposition." + suffix).getVisualOrderText());
   }
}
