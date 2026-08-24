package mezz.jei.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class IngredientsTooltipComponent implements ClientTooltipComponent, TooltipComponent {
   private static final int MAX_INGREDIENTS_PER_ROW = 16;
   private static final int INGREDIENT_SIZE = 18;
   private static final int INGREDIENT_PADDING = 1;
   private final List<IngredientsTooltipComponent.RenderElement<?>> ingredients;

   public IngredientsTooltipComponent(IRecipeLayoutDrawable<?> layout) {
      IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
      IRecipeSlotsView recipeSlotsView = layout.getRecipeSlotsView();
      Map<Object, IngredientsTooltipComponent.SummaryElement<?>> summary = new HashMap<>();
      recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT)
         .stream()
         .map(IRecipeSlotView::getDisplayedIngredient)
         .filter(Optional::isPresent)
         .map(Optional::get)
         .forEach(ingredient -> addToSummary(ingredient, ingredientManager, summary));
      Comparator<IngredientsTooltipComponent.SummaryElement<?>> comparator = Comparator.comparingLong(IngredientsTooltipComponent.SummaryElement::getAmount);
      this.ingredients = summary.values()
         .stream()
         .sorted(comparator.reversed())
         .map(e -> IngredientsTooltipComponent.RenderElement.create((IngredientsTooltipComponent.SummaryElement<?>)e, ingredientManager))
         .toList();
   }

   private static <T> void addToSummary(
      ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager, Map<Object, IngredientsTooltipComponent.SummaryElement<?>> summary
   ) {
      IIngredientType<T> type = typedIngredient.getType();
      IIngredientHelper<T> helper = ingredientManager.getIngredientHelper(type);
      T ingredient = typedIngredient.getIngredient();
      long ingredientAmount = helper.getAmount(ingredient);
      if (ingredientAmount != -1L) {
         Object uid = getUid(typedIngredient, ingredientManager);
         summary.compute(uid, (k, v) -> {
            if (v == null) {
               return IngredientsTooltipComponent.SummaryElement.create(typedIngredient, ingredientAmount);
            } else {
               long newAmount = v.getAmount() + ingredientAmount;
               v.setAmount(newAmount);
               return v;
            }
         });
      }
   }

   private static <T> Object getUid(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
      IIngredientType<T> type = typedIngredient.getType();
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(type);
      return ingredientHelper.getUid(typedIngredient, UidContext.Recipe);
   }

   public int getHeight() {
      return 4 + 18 * MathUtil.divideCeil(this.ingredients.size(), 16);
   }

   public int getWidth(Font font) {
      return 18 * Math.min(this.ingredients.size(), 16);
   }

   public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
      for (int i = 0; i < this.ingredients.size(); i++) {
         int elementX = 1 + x + i % 16 * 18;
         int elementY = 1 + y + i / 16 * 18;
         IngredientsTooltipComponent.RenderElement<?> renderElement = this.ingredients.get(i);
         PoseStack pose = guiGraphics.pose();
         pose.pushPose();
         pose.translate(elementX, elementY, 0.0F);
         renderElement.render(guiGraphics);
         pose.popPose();
      }
   }

   private record RenderElement<T>(IIngredientRenderer<T> renderer, T ingredient) {
      public static <T> IngredientsTooltipComponent.RenderElement<T> create(
         IngredientsTooltipComponent.SummaryElement<T> summaryElement, IIngredientManager ingredientManager
      ) {
         ITypedIngredient<T> typedIngredient = summaryElement.getIngredient();
         IIngredientType<T> type = typedIngredient.getType();
         IIngredientHelper<T> helper = ingredientManager.getIngredientHelper(type);
         IIngredientRenderer<T> renderer = ingredientManager.getIngredientRenderer(type);
         T ingredient = helper.copyWithAmount(typedIngredient.getIngredient(), summaryElement.getAmount());
         return new IngredientsTooltipComponent.RenderElement<>(renderer, ingredient);
      }

      public void render(GuiGraphics guiGraphics) {
         this.renderer.render(guiGraphics, this.ingredient);
      }
   }

   private static class SummaryElement<T> {
      private final ITypedIngredient<T> ingredient;
      private long amount;

      public static <T> IngredientsTooltipComponent.SummaryElement<T> create(ITypedIngredient<T> ingredient, long amount) {
         return new IngredientsTooltipComponent.SummaryElement<>(ingredient, amount);
      }

      private SummaryElement(ITypedIngredient<T> ingredient, long amount) {
         this.ingredient = ingredient;
         this.amount = amount;
      }

      public ITypedIngredient<T> getIngredient() {
         return this.ingredient;
      }

      public long getAmount() {
         return this.amount;
      }

      public void setAmount(long amount) {
         this.amount = amount;
      }
   }
}
