package vectorwing.farmersdelight.integration.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;
import vectorwing.farmersdelight.common.utility.RecipeUtils;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;

public class CookingPotEmiRecipe implements EmiRecipe {
   private static final ResourceLocation BACKGROUND = RecipeUtils.FDLocation("textures/gui/jei/cooking_pot.png");
   private static final ResourceLocation WIDGETS = RecipeUtils.FDLocation("textures/gui/cooking_pot.png");
   private final ResourceLocation id;
   private final List<EmiIngredient> inputs;
   private final EmiStack output;
   private final EmiStack container;
   private final int cookTime;
   private final float experience;
   private final List<ClientTooltipComponent> tooltipComponents;

   public CookingPotEmiRecipe(ResourceLocation id, List<EmiIngredient> inputs, EmiStack output, EmiStack container, int cookTime, float experience) {
      this.id = id;
      this.inputs = inputs;
      this.output = output;
      this.container = container;
      this.cookTime = cookTime;
      this.experience = experience;
      this.tooltipComponents = this.createTooltipComponents();
   }

   private List<ClientTooltipComponent> createTooltipComponents() {
      List<ClientTooltipComponent> tooltipStrings = new ArrayList<>();
      if (this.cookTime > 0) {
         int cookTimeSeconds = this.cookTime / 20;
         tooltipStrings.add(ClientTooltipComponent.create(Component.translatable("emi.cooking.time", new Object[]{cookTimeSeconds}).getVisualOrderText()));
      }

      if (this.experience > 0.0F) {
         tooltipStrings.add(ClientTooltipComponent.create(Component.translatable("emi.cooking.experience", new Object[]{this.experience}).getVisualOrderText()));
      }

      return tooltipStrings;
   }

   public EmiRecipeCategory getCategory() {
      return FDRecipeCategories.COOKING;
   }

   @Nullable
   public ResourceLocation getId() {
      return this.id;
   }

   public List<EmiIngredient> getInputs() {
      return this.inputs;
   }

   public List<EmiStack> getOutputs() {
      return List.of(this.output);
   }

   public List<EmiIngredient> getCatalysts() {
      return List.of(this.container);
   }

   public int getDisplayWidth() {
      return 116;
   }

   public int getDisplayHeight() {
      return 56;
   }

   public void addWidgets(WidgetHolder widgets) {
      widgets.addTexture(BACKGROUND, 0, 0, 116, 56, 0, 0);
      int borderSlotSize = 18;

      for (int row = 0; row < 2; row++) {
         for (int column = 0; column < 3; column++) {
            int inputIndex = row * 3 + column;
            if (inputIndex < this.inputs.size()) {
               this.addSlot(widgets, this.inputs.get(inputIndex), column * borderSlotSize, row * borderSlotSize);
            }
         }
      }

      this.addSlot(widgets, this.output, 94, 9);
      this.addSlot(widgets, this.container, 62, 38);
      this.addSlot(widgets, this.output, 94, 38).recipeContext(this);
      widgets.addAnimatedTexture(WIDGETS, 60, 9, 24, 17, 176, 15, 10000, true, false, false);
      widgets.addTexture(WIDGETS, 18, 39, 17, 15, 176, 0);
      widgets.addTexture(WIDGETS, 64, 2, 8, 11, 176, 32);
      if (this.experience > 0.0F) {
         widgets.addTexture(WIDGETS, 63, 21, 9, 9, 176, 43);
      }

      widgets.addTooltip(
         (mouseX, mouseY) -> ClientRenderUtils.isCursorInsideBounds(60, 2, 22, 28, mouseX.intValue(), mouseY.intValue()) ? this.tooltipComponents : List.of(),
         0,
         0,
         widgets.getWidth(),
         widgets.getHeight()
      );
   }

   private SlotWidget addSlot(WidgetHolder widgets, EmiIngredient ingredient, int x, int y) {
      return widgets.addSlot(ingredient, x, y).drawBack(false);
   }
}
