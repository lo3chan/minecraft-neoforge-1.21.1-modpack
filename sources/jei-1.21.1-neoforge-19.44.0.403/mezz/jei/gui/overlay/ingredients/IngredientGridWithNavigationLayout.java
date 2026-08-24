package mezz.jei.gui.overlay.ingredients;

import java.util.Set;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;

public record IngredientGridWithNavigationLayout(
   ImmutableRect2i ingredientGridArea,
   int availableSlotCount,
   ImmutableRect2i slotBackgroundArea,
   ImmutableRect2i navigationArea,
   ImmutableRect2i scrollbarArea,
   ImmutableRect2i backgroundArea,
   boolean navigationEnabled,
   boolean scrollbarEnabled
) {
   public static final int NAVIGATION_HEIGHT = 20;
   public static final int BORDER_MARGIN = 6;
   public static final int BORDER_PADDING = 5;
   public static final int INNER_PADDING = 2;

   public static ImmutableRect2i getAvailableGridArea(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea) {
      return getAvailableGridArea(gridConfig, availableArea, true);
   }

   public static ImmutableRect2i getAvailableGridArea(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, boolean reserveNavigationArea) {
      ImmutableRect2i availableGridArea = availableArea.insetBy(6);
      if (reserveNavigationArea) {
         availableGridArea = availableGridArea.cropTop(22);
      }

      if (gridConfig.drawBackground().getValue()) {
         availableGridArea = availableGridArea.insetBy(7);
      }

      ImmutableRect2i estimatedGridArea = IngredientGridLayout.calculateBounds(gridConfig, availableGridArea);
      return estimatedGridArea.isEmpty() ? ImmutableRect2i.EMPTY : availableGridArea;
   }

   public static IngredientGridWithNavigationLayout fromGridArea(
      IIngredientGridConfig gridConfig, ImmutableRect2i ingredientGridArea, boolean navigationEnabled
   ) {
      ImmutableRect2i slotBackgroundArea = calculateSlotBackgroundArea(ingredientGridArea, gridConfig);
      ImmutableRect2i navigationArea = calculateNavigationArea(slotBackgroundArea, navigationEnabled);
      return fromGridArea(
         gridConfig,
         ingredientGridArea,
         IngredientGridLayout.calculateAvailableSlotCount(ingredientGridArea, Set.of(), null),
         navigationArea,
         navigationArea,
         navigationEnabled,
         ImmutableRect2i.EMPTY,
         false
      );
   }

   static IngredientGridWithNavigationLayout fromGridArea(
      IIngredientGridConfig gridConfig,
      ImmutableRect2i ingredientGridArea,
      int availableSlotCount,
      ImmutableRect2i navigationArea,
      ImmutableRect2i backgroundNavigationArea,
      boolean navigationEnabled,
      ImmutableRect2i scrollbarArea,
      boolean scrollbarEnabled
   ) {
      ImmutableRect2i slotBackgroundArea = calculateSlotBackgroundArea(ingredientGridArea, gridConfig);
      ImmutableRect2i backgroundArea = MathUtil.union(MathUtil.union(slotBackgroundArea, backgroundNavigationArea), scrollbarArea);
      if (gridConfig.drawBackground().getValue() && !backgroundArea.isEmpty()) {
         backgroundArea = backgroundArea.expandBy(5);
      }

      return new IngredientGridWithNavigationLayout(
         ingredientGridArea, availableSlotCount, slotBackgroundArea, navigationArea, scrollbarArea, backgroundArea, navigationEnabled, scrollbarEnabled
      );
   }

   public static ImmutableRect2i calculateSlotBackgroundArea(ImmutableRect2i ingredientGridArea, IIngredientGridConfig gridConfig) {
      if (ingredientGridArea.isEmpty()) {
         return ImmutableRect2i.EMPTY;
      } else {
         return gridConfig.drawBackground().getValue() ? ingredientGridArea.expandBy(2) : ingredientGridArea;
      }
   }

   public static ImmutableRect2i calculateNavigationArea(ImmutableRect2i slotBackgroundArea, boolean navigationEnabled) {
      return !navigationEnabled ? ImmutableRect2i.EMPTY : slotBackgroundArea.keepTop(20).moveUp(22);
   }

   public boolean hasRoom() {
      return !this.ingredientGridArea.isEmpty()
         && this.availableSlotCount > 0
         && (!this.navigationEnabled || !this.navigationArea.isEmpty())
         && (!this.scrollbarEnabled || !this.scrollbarArea.isEmpty());
   }
}
