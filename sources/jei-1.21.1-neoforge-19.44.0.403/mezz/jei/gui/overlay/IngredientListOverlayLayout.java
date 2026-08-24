package mezz.jei.gui.overlay;

import java.util.Optional;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutableRect2i;

class IngredientListOverlayLayout {
   private static final int BORDER_MARGIN = 6;
   private static final int INNER_PADDING = 2;
   private static final int BUTTON_SIZE = 20;
   private static final int SEARCH_HEIGHT = 20;
   private static final int LOOKUP_HISTORY_BOTTOM_PADDING = 6;
   private static final int LOOKUP_HISTORY_PADDING_EXTRA = 4;

   static IngredientListOverlayLayout.Layout calculate(
      IGuiProperties guiProperties,
      boolean centerSearchBarEnabled,
      boolean lookupHistoryEnabled,
      boolean lookupHistoryDisplayedOnThisSide,
      int lookupHistoryDisplayHeight
   ) {
      ImmutableRect2i displayArea = createDisplayArea(guiProperties);
      boolean searchBarCentered = isSearchBarCentered(centerSearchBarEnabled, guiProperties);
      ImmutableRect2i availableContentsArea = getAvailableContentsArea(displayArea, searchBarCentered);
      Optional<ImmutableRect2i> lookupHistoryArea = Optional.empty();
      if (lookupHistoryEnabled && lookupHistoryDisplayedOnThisSide && lookupHistoryDisplayHeight > 0) {
         ImmutableRect2i area = getLookupHistoryArea(displayArea, searchBarCentered, lookupHistoryDisplayHeight);
         availableContentsArea = cropBottomTo(availableContentsArea, area.y() - 4);
         lookupHistoryArea = Optional.of(area);
      }

      return new IngredientListOverlayLayout.Layout(guiProperties, displayArea, availableContentsArea, lookupHistoryArea, searchBarCentered);
   }

   private static ImmutableRect2i createDisplayArea(IGuiProperties guiProperties) {
      return new ImmutableRect2i(0, 0, guiProperties.screenWidth(), guiProperties.screenHeight()).cropLeft(guiProperties.guiRight());
   }

   private static boolean isSearchBarCentered(boolean centerSearchBarEnabled, IGuiProperties guiProperties) {
      return centerSearchBarEnabled && guiProperties.guiBottom() + 20 < guiProperties.screenHeight();
   }

   private static ImmutableRect2i getAvailableContentsArea(ImmutableRect2i displayArea, boolean searchBarCentered) {
      return searchBarCentered ? displayArea : displayArea.cropBottom(22);
   }

   private static ImmutableRect2i getLookupHistoryArea(ImmutableRect2i displayArea, boolean searchBarCentered, int lookupHistoryHeight) {
      int bottomReservedHeight = searchBarCentered ? 0 : 26;
      return displayArea.insetBy(6).cropBottom(bottomReservedHeight).keepBottom(lookupHistoryHeight);
   }

   private static ImmutableRect2i cropBottomTo(ImmutableRect2i area, int bottomY) {
      int cropAmount = getBottom(area) - bottomY;
      return cropAmount <= 0 ? area : area.cropBottom(cropAmount);
   }

   private static int getBottom(ImmutableRect2i area) {
      return area.y() + area.height();
   }

   record Layout(
      IGuiProperties guiProperties,
      ImmutableRect2i displayArea,
      ImmutableRect2i availableContentsArea,
      Optional<ImmutableRect2i> lookupHistoryArea,
      boolean searchBarCentered
   ) {
      IngredientListOverlayLayout.SearchAndConfigAreas getSearchAndConfigAreas(boolean contentsHasRoom, ImmutableRect2i contentsArea) {
         ImmutableRect2i searchAndConfigArea = this.getSearchAndConfigArea(contentsHasRoom, contentsArea);
         ImmutableRect2i searchArea = searchAndConfigArea.cropRight(20);
         ImmutableRect2i configButtonArea = searchAndConfigArea.keepRight(20);
         return new IngredientListOverlayLayout.SearchAndConfigAreas(searchArea, configButtonArea);
      }

      private ImmutableRect2i getSearchAndConfigArea(boolean contentsHasRoom, ImmutableRect2i contentsArea) {
         ImmutableRect2i insetDisplayArea = this.displayArea.insetBy(6);
         if (this.searchBarCentered) {
            return insetDisplayArea.keepBottom(20)
               .matchWidthAndX(
                  new ImmutableRect2i(this.guiProperties.guiLeft(), this.guiProperties.guiTop(), this.guiProperties.guiXSize(), this.guiProperties.guiYSize())
               );
         } else {
            return contentsHasRoom ? insetDisplayArea.keepBottom(20).matchWidthAndX(contentsArea) : insetDisplayArea.keepBottom(20);
         }
      }
   }

   record SearchAndConfigAreas(ImmutableRect2i searchArea, ImmutableRect2i configButtonArea) {
   }
}
