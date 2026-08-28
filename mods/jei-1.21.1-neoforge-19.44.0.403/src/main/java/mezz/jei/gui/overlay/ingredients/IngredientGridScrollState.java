/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.IngredientGridPageState;
import org.jetbrains.annotations.Nullable;

public final class IngredientGridScrollState {
    private float scrollOffsetY = 0.0f;
    @Nullable
    private IElement<?> scrollAnchorElement;
    private float scrollAnchorPositionY = 0.0f;

    public float getScrollOffsetY() {
        return this.scrollOffsetY;
    }

    public void updateForScrollOffset(float scrollOffsetY, int itemCount, int columns, int visibleRows, int visibleIngredientCount) {
        this.scrollAnchorElement = null;
        this.scrollOffsetY = IngredientGridScrollState.getValidScrollOffsetY(scrollOffsetY, itemCount, columns, visibleRows, visibleIngredientCount);
    }

    public void updateKeepingScrollAnchorVisible(@Nullable IElement<?> scrollAnchorElement, List<IElement<?>> ingredientList, int columns, int visibleRows, int visibleIngredientCount, boolean smoothScrolling, int rowHeight) {
        int anchorIndex = IngredientGridPageState.findIndexOfIngredientElement(scrollAnchorElement, ingredientList);
        if (anchorIndex < 0) {
            this.scrollOffsetY = IngredientGridScrollState.getValidScrollOffsetY(this.scrollOffsetY, ingredientList.size(), columns, visibleRows, visibleIngredientCount);
            return;
        }
        float anchorPositionY = this.getStoredScrollAnchorPositionY(scrollAnchorElement);
        this.scrollOffsetY = IngredientGridScrollState.getScrollOffsetYKeepingAnchorVisible(anchorIndex, ingredientList.size(), columns, visibleRows, visibleIngredientCount, anchorPositionY, smoothScrolling, rowHeight);
    }

    @Nullable
    public IElement<?> getScrollAnchorElement(List<IElement<?>> ingredientList) {
        if (this.scrollAnchorElement != null) {
            if (IngredientGridPageState.findIndexOfIngredientElement(this.scrollAnchorElement, ingredientList) >= 0) {
                return this.scrollAnchorElement;
            }
            this.scrollAnchorElement = null;
        }
        return null;
    }

    public void setScrollAnchorElement(IElement<?> scrollAnchorElement, float scrollAnchorPositionY) {
        this.scrollAnchorElement = scrollAnchorElement;
        this.scrollAnchorPositionY = Math.clamp((float)scrollAnchorPositionY, (float)0.0f, (float)1.0f);
    }

    private float getStoredScrollAnchorPositionY(@Nullable IElement<?> scrollAnchorElement) {
        if (scrollAnchorElement != null && this.scrollAnchorElement != null && IngredientGridPageState.isSameIngredientElement(this.scrollAnchorElement, scrollAnchorElement)) {
            return this.scrollAnchorPositionY;
        }
        return 0.0f;
    }

    public static float getValidScrollOffsetY(float scrollOffsetY, int itemCount, int columns, int visibleRows) {
        int visibleIngredientCount = columns * visibleRows;
        return IngredientGridScrollState.getValidScrollOffsetY(scrollOffsetY, itemCount, columns, visibleRows, visibleIngredientCount);
    }

    public static float getValidScrollOffsetY(float scrollOffsetY, int itemCount, int columns, int visibleRows, int visibleIngredientCount) {
        if (IngredientGridScrollState.getHiddenRows(itemCount, columns, visibleRows, visibleIngredientCount) == 0) {
            return 0.0f;
        }
        return Math.clamp((float)scrollOffsetY, (float)0.0f, (float)1.0f);
    }

    public static float getScrollOffsetYForFirstRow(int firstRow, int itemCount, int columns, int visibleRows, int visibleIngredientCount) {
        int hiddenRows = IngredientGridScrollState.getHiddenRows(itemCount, columns, visibleRows, visibleIngredientCount);
        if (hiddenRows == 0) {
            return 0.0f;
        }
        return Math.clamp((float)((float)firstRow / (float)hiddenRows), (float)0.0f, (float)1.0f);
    }

    public static float getScrollOffsetYKeepingAnchorVisible(int anchorIndex, int itemCount, int columns, int visibleRows, float anchorPositionY, boolean smoothScrolling, int rowHeight) {
        int visibleIngredientCount = columns * visibleRows;
        return IngredientGridScrollState.getScrollOffsetYKeepingAnchorVisible(anchorIndex, itemCount, columns, visibleRows, visibleIngredientCount, anchorPositionY, smoothScrolling, rowHeight);
    }

    public static float getScrollOffsetYKeepingAnchorVisible(int anchorIndex, int itemCount, int columns, int visibleRows, int visibleIngredientCount, float anchorPositionY, boolean smoothScrolling, int rowHeight) {
        int hiddenRows = IngredientGridScrollState.getHiddenRows(itemCount, columns, visibleRows, visibleIngredientCount);
        if (anchorIndex < 0 || hiddenRows == 0 || columns == 0 || visibleRows == 0) {
            return 0.0f;
        }
        int anchorRow = anchorIndex / columns;
        float validAnchorPositionY = Math.clamp((float)anchorPositionY, (float)0.0f, (float)1.0f);
        if (smoothScrolling) {
            return IngredientGridScrollState.getSmoothScrollOffsetYKeepingAnchorVisible(anchorRow, hiddenRows, visibleRows, validAnchorPositionY, rowHeight);
        }
        int targetVisibleRow = Math.round(validAnchorPositionY * (float)visibleRows);
        targetVisibleRow = Math.clamp((long)targetVisibleRow, (int)0, (int)(visibleRows - 1));
        int desiredFirstRow = anchorRow - targetVisibleRow;
        int minFirstRow = Math.max(0, anchorRow - visibleRows + 1);
        int maxFirstRow = Math.min(hiddenRows, anchorRow);
        int validFirstRow = Math.clamp((long)desiredFirstRow, (int)minFirstRow, (int)maxFirstRow);
        return (float)validFirstRow / (float)hiddenRows;
    }

    private static float getSmoothScrollOffsetYKeepingAnchorVisible(int anchorRow, int hiddenRows, int visibleRows, float anchorPositionY, int rowHeight) {
        if (rowHeight == 0) {
            return 0.0f;
        }
        int hiddenPixels = hiddenRows * rowHeight;
        int visiblePixels = visibleRows * rowHeight;
        int anchorTopPixel = anchorRow * rowHeight;
        int targetAnchorTopPixel = Math.round(anchorPositionY * (float)visiblePixels);
        int desiredScrollPixelOffset = anchorTopPixel - targetAnchorTopPixel;
        int minScrollPixelOffset = Math.max(0, anchorTopPixel - (visibleRows - 1) * rowHeight);
        int maxScrollPixelOffset = Math.min(hiddenPixels, anchorTopPixel);
        int validScrollPixelOffset = Math.clamp((long)desiredScrollPixelOffset, (int)minScrollPixelOffset, (int)maxScrollPixelOffset);
        return (float)validScrollPixelOffset / (float)hiddenPixels;
    }

    public static int getFirstItemIndexForScrollOffset(float scrollOffsetY, int itemCount, int columns, int visibleRows) {
        int visibleIngredientCount = columns * visibleRows;
        return IngredientGridScrollState.getFirstItemIndexForScrollOffset(scrollOffsetY, itemCount, columns, visibleRows, visibleIngredientCount);
    }

    public static int getFirstItemIndexForScrollOffset(float scrollOffsetY, int itemCount, int columns, int visibleRows, int visibleIngredientCount) {
        int hiddenRows = IngredientGridScrollState.getHiddenRows(itemCount, columns, visibleRows, visibleIngredientCount);
        int firstRow = IngredientGridScrollState.getFirstRowForScrollOffset(hiddenRows, scrollOffsetY);
        int firstItemIndex = IngredientGridScrollState.getFirstItemIndexForRow(firstRow, itemCount, columns);
        int maxFirstItemIndex = IngredientGridScrollState.getMaxFirstItemIndex(itemCount, columns, visibleRows, visibleIngredientCount);
        if (Float.compare(scrollOffsetY, 1.0f) >= 0) {
            return maxFirstItemIndex;
        }
        return Math.min(firstItemIndex, maxFirstItemIndex);
    }

    public static int getFirstRowForScrollOffset(int hiddenRows, float scrollOffsetY) {
        int rowIndex = (int)((double)(scrollOffsetY * (float)hiddenRows) + 0.5);
        return Math.max(rowIndex, 0);
    }

    public static int getFirstItemIndexForRow(int firstRow, int itemCount, int columns) {
        if (itemCount == 0 || columns == 0) {
            return 0;
        }
        return Math.max(0, firstRow) * columns;
    }

    public static int getSmoothScrollPixelOffset(int hiddenRows, int rowHeight, float scrollOffsetY) {
        int hiddenPixels = hiddenRows * rowHeight;
        return Math.clamp((long)Math.round((float)hiddenPixels * scrollOffsetY), (int)0, (int)hiddenPixels);
    }

    public static int getFirstRowForSmoothScrollPixelOffset(int scrollPixelOffset, int rowHeight) {
        if (rowHeight == 0) {
            return 0;
        }
        return scrollPixelOffset / rowHeight;
    }

    public static int getRowPixelOffset(int scrollPixelOffset, int rowHeight) {
        if (rowHeight == 0) {
            return 0;
        }
        return scrollPixelOffset % rowHeight;
    }

    public static int getHiddenRows(int itemCount, int columns, int visibleRows) {
        int visibleIngredientCount = columns * visibleRows;
        return IngredientGridScrollState.getHiddenRows(itemCount, columns, visibleRows, visibleIngredientCount);
    }

    public static int getHiddenRows(int itemCount, int columns, int visibleRows, int visibleIngredientCount) {
        int maxFirstItemIndex = IngredientGridScrollState.getMaxFirstItemIndex(itemCount, columns, visibleRows, visibleIngredientCount);
        return IngredientGridScrollState.getTotalRows(maxFirstItemIndex, columns);
    }

    public static int getMaxFirstItemIndex(int itemCount, int columns, int visibleRows, int visibleIngredientCount) {
        if (itemCount == 0 || columns == 0 || visibleRows == 0 || visibleIngredientCount == 0) {
            return 0;
        }
        int totalRows = IngredientGridScrollState.getTotalRows(itemCount, columns);
        int rowBasedFirstItemIndex = Math.max(0, totalRows - visibleRows) * columns;
        int slotBasedFirstItemIndex = Math.max(0, itemCount - visibleIngredientCount);
        int maxFirstItemIndex = Math.max(rowBasedFirstItemIndex, slotBasedFirstItemIndex);
        int lastItemIndex = itemCount - 1;
        return Math.min(maxFirstItemIndex, lastItemIndex);
    }

    public static int getTotalRows(int itemCount, int columns) {
        if (itemCount == 0 || columns == 0) {
            return 0;
        }
        return MathUtil.divideCeil(itemCount, columns);
    }
}

