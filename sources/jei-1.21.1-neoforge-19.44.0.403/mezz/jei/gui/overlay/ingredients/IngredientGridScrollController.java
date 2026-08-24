package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Nullable;

public final class IngredientGridScrollController {
   private static final IngredientGridScrollController.ScrollResult NOT_CONSUMED = new IngredientGridScrollController.ScrollResult(false, false);
   private final IngredientGridScrollState scrollState = new IngredientGridScrollState();
   private final IIngredientGridSource ingredientSource;
   private final IIngredientGrid ingredientGrid;
   private final IIngredientGridConfig gridConfig;
   private final IClientConfig clientConfig;

   public IngredientGridScrollController(
      IIngredientGridSource ingredientSource, IIngredientGrid ingredientGrid, IIngredientGridConfig gridConfig, IClientConfig clientConfig
   ) {
      this.ingredientSource = ingredientSource;
      this.ingredientGrid = ingredientGrid;
      this.gridConfig = gridConfig;
      this.clientConfig = clientConfig;
   }

   public void updateLayoutStartingAt(int firstItemIndex) {
      List<IElement<?>> ingredientList = this.ingredientSource.getElements();
      int columnCount = this.ingredientGrid.getColumnCount();
      int rowCount = this.ingredientGrid.getRowCount();
      int visibleIngredientCount = this.ingredientGrid.size();
      int firstRow = columnCount > 0 ? firstItemIndex / columnCount : 0;
      float scrollOffsetY = IngredientGridScrollState.getScrollOffsetYForFirstRow(
         firstRow, ingredientList.size(), columnCount, rowCount, visibleIngredientCount
      );
      this.scrollState.updateForScrollOffset(scrollOffsetY, ingredientList.size(), columnCount, rowCount, visibleIngredientCount);
      this.updateGridFromScrollState(ingredientList);
      this.rememberFirstVisibleElementAsScrollAnchor();
   }

   public void updateLayoutKeepingScrollAnchorVisible(@Nullable IElement<?> scrollAnchorElement) {
      List<IElement<?>> ingredientList = this.ingredientSource.getElements();
      this.scrollState
         .updateKeepingScrollAnchorVisible(
            scrollAnchorElement,
            ingredientList,
            this.ingredientGrid.getColumnCount(),
            this.ingredientGrid.getRowCount(),
            this.ingredientGrid.size(),
            this.isSmoothScrolling(),
            IngredientGridLayout.INGREDIENT_HEIGHT
         );
      this.updateGridFromScrollState(ingredientList);
   }

   @Nullable
   public IElement<?> getScrollAnchorElement() {
      return this.scrollState.getScrollAnchorElement(this.ingredientSource.getElements());
   }

   public void setScrollAnchorElement(IElement<?> scrollAnchorElement) {
      this.scrollState.setScrollAnchorElement(scrollAnchorElement, this.getScrollAnchorPositionY(scrollAnchorElement));
   }

   public boolean canScroll() {
      return this.getHiddenScrollRows() > 0;
   }

   public int getVisibleScrollRows() {
      return this.ingredientGrid.getRowCount();
   }

   public int getHiddenScrollRows() {
      return IngredientGridScrollState.getHiddenRows(
         this.ingredientSource.getElements().size(), this.ingredientGrid.getColumnCount(), this.ingredientGrid.getRowCount(), this.ingredientGrid.size()
      );
   }

   public int getVisibleScrollAmount() {
      int visibleRows = this.getVisibleScrollRows();
      return this.isSmoothScrolling() ? visibleRows * IngredientGridLayout.INGREDIENT_HEIGHT : visibleRows;
   }

   public int getHiddenScrollAmount() {
      int hiddenRows = this.getHiddenScrollRows();
      return this.isSmoothScrolling() ? hiddenRows * IngredientGridLayout.INGREDIENT_HEIGHT : hiddenRows;
   }

   public float getScrollOffsetY() {
      return !this.canScroll() ? 0.0F : this.scrollState.getScrollOffsetY();
   }

   public boolean setScrollOffsetY(float scrollOffsetY) {
      return !this.canScroll() ? false : this.updateScrollOffset(scrollOffsetY);
   }

   public IngredientGridScrollController.ScrollResult scrollByMouse(double scrollDeltaY) {
      if (!this.canScroll()) {
         return NOT_CONSUMED;
      } else {
         float scrollAmount = this.getMouseWheelScrollAmount(scrollDeltaY);
         if (scrollAmount == 0.0F) {
            return NOT_CONSUMED;
         } else {
            boolean changed = this.updateScrollOffset(this.scrollState.getScrollOffsetY() - scrollAmount);
            return new IngredientGridScrollController.ScrollResult(true, changed);
         }
      }
   }

   public boolean scrollByRows(int rows) {
      if (this.canScroll() && rows != 0) {
         List<IElement<?>> ingredientList = this.ingredientSource.getElements();
         int firstRow = this.getFirstVisibleScrollRow() + rows;
         float scrollOffsetY = IngredientGridScrollState.getScrollOffsetYForFirstRow(
            firstRow, ingredientList.size(), this.ingredientGrid.getColumnCount(), this.ingredientGrid.getRowCount(), this.ingredientGrid.size()
         );
         return this.updateScrollOffset(scrollOffsetY);
      } else {
         return false;
      }
   }

   public int getFirstVisibleScrollRow() {
      if (this.isSmoothScrolling()) {
         int scrollPixelOffset = IngredientGridScrollState.getSmoothScrollPixelOffset(
            this.getHiddenScrollRows(), IngredientGridLayout.INGREDIENT_HEIGHT, this.scrollState.getScrollOffsetY()
         );
         return IngredientGridScrollState.getFirstRowForSmoothScrollPixelOffset(scrollPixelOffset, IngredientGridLayout.INGREDIENT_HEIGHT);
      } else {
         return IngredientGridScrollState.getFirstRowForScrollOffset(this.getHiddenScrollRows(), this.scrollState.getScrollOffsetY());
      }
   }

   private boolean isSmoothScrolling() {
      return this.gridConfig.navigationMode().getValue().usesSmoothScrolling();
   }

   private float getMouseWheelScrollAmount(double scrollDeltaY) {
      if (this.isSmoothScrolling()) {
         int totalHeight = this.getTotalScrollRows() * IngredientGridLayout.INGREDIENT_HEIGHT;
         return totalHeight == 0 ? 0.0F : (float)(scrollDeltaY * this.clientConfig.smoothScrollRate().getValue().intValue() / totalHeight);
      } else {
         int hiddenRows = this.getHiddenScrollRows();
         return hiddenRows == 0 ? 0.0F : (float)(scrollDeltaY / hiddenRows);
      }
   }

   private boolean updateScrollOffset(float scrollOffsetY) {
      float oldScrollOffsetY = this.scrollState.getScrollOffsetY();
      List<IElement<?>> ingredientList = this.ingredientSource.getElements();
      int columnCount = this.ingredientGrid.getColumnCount();
      int rowCount = this.ingredientGrid.getRowCount();
      float validScrollOffsetY = IngredientGridScrollState.getValidScrollOffsetY(
         scrollOffsetY, ingredientList.size(), columnCount, rowCount, this.ingredientGrid.size()
      );
      if (Float.compare(oldScrollOffsetY, validScrollOffsetY) == 0) {
         return false;
      } else {
         this.scrollState.updateForScrollOffset(validScrollOffsetY, ingredientList.size(), columnCount, rowCount, this.ingredientGrid.size());
         this.updateGridFromScrollState(ingredientList);
         this.rememberFirstVisibleElementAsScrollAnchor();
         return true;
      }
   }

   private void rememberFirstVisibleElementAsScrollAnchor() {
      if (!this.isSmoothScrolling()) {
         this.ingredientGrid.getVisibleElements().findFirst().ifPresent(this::setScrollAnchorElement);
      }
   }

   private void updateGridFromScrollState(List<IElement<?>> ingredientList) {
      IngredientGridScrollController.ScrollRenderPosition scrollRenderPosition = this.getScrollRenderPosition(ingredientList);
      this.ingredientGrid.set(scrollRenderPosition.firstItemIndex(), scrollRenderPosition.rowPixelOffset(), ingredientList);
   }

   private IngredientGridScrollController.ScrollRenderPosition getScrollRenderPosition(List<IElement<?>> ingredientList) {
      int columnCount = this.ingredientGrid.getColumnCount();
      int rowCount = this.ingredientGrid.getRowCount();
      int visibleIngredientCount = this.ingredientGrid.size();
      int hiddenRows = IngredientGridScrollState.getHiddenRows(ingredientList.size(), columnCount, rowCount, visibleIngredientCount);
      float scrollOffsetY = this.scrollState.getScrollOffsetY();
      if (this.isSmoothScrolling()) {
         int scrollPixelOffset = IngredientGridScrollState.getSmoothScrollPixelOffset(hiddenRows, IngredientGridLayout.INGREDIENT_HEIGHT, scrollOffsetY);
         int firstRow = IngredientGridScrollState.getFirstRowForSmoothScrollPixelOffset(scrollPixelOffset, IngredientGridLayout.INGREDIENT_HEIGHT);
         int firstItemIndex = IngredientGridScrollState.getFirstItemIndexForRow(firstRow, ingredientList.size(), columnCount);
         int maxFirstItemIndex = IngredientGridScrollState.getMaxFirstItemIndex(ingredientList.size(), columnCount, rowCount, visibleIngredientCount);
         if (Float.compare(scrollOffsetY, 1.0F) >= 0) {
            firstItemIndex = maxFirstItemIndex;
         } else {
            firstItemIndex = Math.min(firstItemIndex, maxFirstItemIndex);
         }

         int rowPixelOffset = IngredientGridScrollState.getRowPixelOffset(scrollPixelOffset, IngredientGridLayout.INGREDIENT_HEIGHT);
         return new IngredientGridScrollController.ScrollRenderPosition(firstItemIndex, rowPixelOffset);
      } else {
         int firstItemIndex = IngredientGridScrollState.getFirstItemIndexForScrollOffset(
            scrollOffsetY, ingredientList.size(), columnCount, rowCount, visibleIngredientCount
         );
         return new IngredientGridScrollController.ScrollRenderPosition(firstItemIndex, 0);
      }
   }

   private int getTotalScrollRows() {
      return IngredientGridScrollState.getTotalRows(this.ingredientSource.getElements().size(), this.ingredientGrid.getColumnCount());
   }

   private float getScrollAnchorPositionY(IElement<?> element) {
      List<IElement<?>> ingredientList = this.ingredientSource.getElements();
      int anchorIndex = IngredientGridPageState.findIndexOfIngredientElement(element, ingredientList);
      int columnCount = this.ingredientGrid.getColumnCount();
      int rowCount = this.ingredientGrid.getRowCount();
      if (anchorIndex >= 0 && columnCount != 0 && rowCount != 0) {
         int visibleHeight = rowCount * IngredientGridLayout.INGREDIENT_HEIGHT;
         int anchorRow = anchorIndex / columnCount;
         int scrollPixelOffset = this.getCurrentScrollPixelOffset(ingredientList);
         int anchorTopY = anchorRow * IngredientGridLayout.INGREDIENT_HEIGHT - scrollPixelOffset;
         return Math.clamp((float)anchorTopY / visibleHeight, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   private int getCurrentScrollPixelOffset(List<IElement<?>> ingredientList) {
      int columnCount = this.ingredientGrid.getColumnCount();
      int rowCount = this.ingredientGrid.getRowCount();
      int hiddenRows = IngredientGridScrollState.getHiddenRows(ingredientList.size(), columnCount, rowCount, this.ingredientGrid.size());
      float scrollOffsetY = this.scrollState.getScrollOffsetY();
      if (this.isSmoothScrolling()) {
         return IngredientGridScrollState.getSmoothScrollPixelOffset(hiddenRows, IngredientGridLayout.INGREDIENT_HEIGHT, scrollOffsetY);
      } else {
         int firstRow = IngredientGridScrollState.getFirstRowForScrollOffset(hiddenRows, scrollOffsetY);
         return firstRow * IngredientGridLayout.INGREDIENT_HEIGHT;
      }
   }

   private record ScrollRenderPosition(int firstItemIndex, int rowPixelOffset) {
   }

   public record ScrollResult(boolean consumed, boolean changed) {
   }
}
