package mezz.jei.gui.overlay.ingredients;

import java.util.Set;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.ImmutableSize2i;
import mezz.jei.common.util.NavigationVisibility;
import mezz.jei.gui.util.AlignmentUtil;
import org.jetbrains.annotations.Nullable;

public final class IngredientGridScrollbarLayout {
   private IngredientGridScrollbarLayout() {
   }

   public static IngredientGridWithNavigationLayout calculate(
      IIngredientGridConfig gridConfig,
      ImmutableRect2i availableArea,
      Set<ImmutableRect2i> guiExclusionAreas,
      @Nullable ImmutablePoint2i mouseExclusionPoint,
      int ingredientCount
   ) {
      return switch ((NavigationVisibility)gridConfig.navigationVisibility().getValue()) {
         case ENABLED -> calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, true);
         case DISABLED -> calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, false);
         case AUTO_HIDE -> calculateAutoHideScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, ingredientCount);
      };
   }

   private static IngredientGridWithNavigationLayout calculateAutoHideScrollbar(
      IIngredientGridConfig gridConfig,
      ImmutableRect2i availableArea,
      Set<ImmutableRect2i> guiExclusionAreas,
      @Nullable ImmutablePoint2i mouseExclusionPoint,
      int ingredientCount
   ) {
      IngredientGridWithNavigationLayout layoutWithoutScrollbar = calculateForScrollbar(
         gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, false
      );
      int pageCountWithoutScrollbar = IngredientGridPageState.getPageCount(ingredientCount, layoutWithoutScrollbar.availableSlotCount());
      boolean scrollbarEnabled = layoutWithoutScrollbar.hasRoom() && pageCountWithoutScrollbar > 1;
      return scrollbarEnabled ? calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, true) : layoutWithoutScrollbar;
   }

   private static IngredientGridWithNavigationLayout calculateForScrollbar(
      IIngredientGridConfig gridConfig,
      ImmutableRect2i availableArea,
      Set<ImmutableRect2i> guiExclusionAreas,
      @Nullable ImmutablePoint2i mouseExclusionPoint,
      boolean scrollbarEnabled
   ) {
      ImmutableRect2i availableGridArea = IngredientGridWithNavigationLayout.getAvailableGridArea(gridConfig, availableArea, false);
      ImmutableRect2i ingredientGridArea;
      if (scrollbarEnabled) {
         ingredientGridArea = calculateScrollbarGridArea(gridConfig, availableGridArea);
      } else {
         ingredientGridArea = IngredientGridLayout.calculateBounds(gridConfig, availableGridArea);
      }

      int availableSlotCount = IngredientGridLayout.calculateAvailableSlotCount(ingredientGridArea, guiExclusionAreas, mouseExclusionPoint);
      ImmutableRect2i slotBackgroundArea = IngredientGridWithNavigationLayout.calculateSlotBackgroundArea(ingredientGridArea, gridConfig);
      return IngredientGridWithNavigationLayout.fromGridArea(
         gridConfig,
         ingredientGridArea,
         availableSlotCount,
         ImmutableRect2i.EMPTY,
         ImmutableRect2i.EMPTY,
         false,
         calculateScrollbarArea(gridConfig, ingredientGridArea, slotBackgroundArea, scrollbarEnabled),
         scrollbarEnabled
      );
   }

   private static ImmutableRect2i calculateScrollbarGridArea(IIngredientGridConfig gridConfig, ImmutableRect2i availableGridArea) {
      if (availableGridArea.isEmpty()) {
         return ImmutableRect2i.EMPTY;
      } else {
         ImmutableRect2i availableAreaWithoutScrollbar = availableGridArea.cropRight(calculateScrollbarReservedGridWidth(gridConfig));
         ImmutableSize2i ingredientGridSize = IngredientGridLayout.calculateSize(gridConfig, availableAreaWithoutScrollbar);
         return ingredientGridSize.equals(ImmutableSize2i.EMPTY)
            ? ImmutableRect2i.EMPTY
            : AlignmentUtil.align(
               ingredientGridSize, availableAreaWithoutScrollbar, gridConfig.horizontalAlignment().getValue(), gridConfig.verticalAlignment().getValue()
            );
      }
   }

   private static int calculateScrollbarExtraWidth(IIngredientGridConfig gridConfig) {
      return calculateScrollbarOffsetFromGrid(gridConfig) + 14;
   }

   private static int calculateScrollbarReservedGridWidth(IIngredientGridConfig gridConfig) {
      int reservedGridWidth = calculateScrollbarExtraWidth(gridConfig);
      return gridConfig.drawBackground().getValue() ? reservedGridWidth - 2 : reservedGridWidth;
   }

   private static int calculateScrollbarOffsetFromGrid(IIngredientGridConfig gridConfig) {
      return gridConfig.drawBackground().getValue() ? 4 : 0;
   }

   private static ImmutableRect2i calculateScrollbarArea(
      IIngredientGridConfig gridConfig, ImmutableRect2i ingredientGridArea, ImmutableRect2i slotBackgroundArea, boolean scrollbarEnabled
   ) {
      return scrollbarEnabled && !ingredientGridArea.isEmpty()
         ? new ImmutableRect2i(
            ingredientGridArea.x() + ingredientGridArea.width() + calculateScrollbarOffsetFromGrid(gridConfig),
            slotBackgroundArea.y(),
            14,
            slotBackgroundArea.height()
         )
         : ImmutableRect2i.EMPTY;
   }
}
