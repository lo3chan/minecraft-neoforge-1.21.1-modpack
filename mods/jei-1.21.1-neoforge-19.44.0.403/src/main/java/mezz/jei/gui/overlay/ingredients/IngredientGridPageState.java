/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Nullable;

final class IngredientGridPageState {
    private int firstItemIndex = 0;
    @Nullable
    private IElement<?> pageAnchorElement;

    IngredientGridPageState() {
    }

    public int getFirstItemIndex() {
        return this.firstItemIndex;
    }

    public int updateForPageNavigation(int firstItemIndex, int itemCount, int itemsPerPage) {
        this.pageAnchorElement = null;
        this.firstItemIndex = IngredientGridPageState.getFirstItemIndexForValidPage(firstItemIndex, itemCount, itemsPerPage);
        return this.firstItemIndex;
    }

    public int updateKeepingPageAnchorVisible(@Nullable IElement<?> pageAnchorElement, List<IElement<?>> ingredientList, int itemsPerPage) {
        int anchorIndex = IngredientGridPageState.findIndexOfIngredientElement(pageAnchorElement, ingredientList);
        this.firstItemIndex = IngredientGridPageState.getFirstItemIndexForValidPage(anchorIndex, ingredientList.size(), itemsPerPage);
        return this.firstItemIndex;
    }

    @Nullable
    public IElement<?> getPageAnchorElement(List<IElement<?>> ingredientList) {
        if (this.pageAnchorElement != null) {
            if (IngredientGridPageState.findIndexOfIngredientElement(this.pageAnchorElement, ingredientList) >= 0) {
                return this.pageAnchorElement;
            }
            this.pageAnchorElement = null;
        }
        return null;
    }

    public void setPageAnchorElement(IElement<?> pageAnchorElement) {
        this.pageAnchorElement = pageAnchorElement;
    }

    static int findIndexOfIngredientElement(@Nullable IElement<?> element, List<IElement<?>> ingredientList) {
        if (element == null) {
            return -1;
        }
        for (int i = 0; i < ingredientList.size(); ++i) {
            if (!IngredientGridPageState.isSameIngredientElement(ingredientList.get(i), element)) continue;
            return i;
        }
        return -1;
    }

    static int getFirstItemIndexForValidPage(int firstItemIndex, int itemCount, int itemsPerPage) {
        if (itemCount == 0 || itemsPerPage == 0) {
            return 0;
        }
        int requestedPageStart = Math.max(0, firstItemIndex) / itemsPerPage * itemsPerPage;
        int lastPageIndex = (itemCount - 1) / itemsPerPage * itemsPerPage;
        return Math.min(requestedPageStart, lastPageIndex);
    }

    static boolean isSameIngredientElement(IElement<?> first, IElement<?> second) {
        ITypedIngredient<?> secondIngredient;
        if (first == second) {
            return true;
        }
        ITypedIngredient<?> firstIngredient = first.getTypedIngredient();
        return firstIngredient == (secondIngredient = second.getTypedIngredient()) || firstIngredient.getType().equals(secondIngredient.getType()) && firstIngredient.getIngredient() == secondIngredient.getIngredient();
    }

    static int getPageCount(int itemCount, int itemsPerPage) {
        if (itemsPerPage == 0) {
            return 1;
        }
        int pageCount = MathUtil.divideCeil(itemCount, itemsPerPage);
        pageCount = Math.max(1, pageCount);
        return pageCount;
    }

    static int getPageNumberForFirstItemIndex(int firstItemIndex, int itemsPerPage, int itemCount) {
        int firstIndex = IngredientGridPageState.getFirstItemIndexForValidPage(firstItemIndex, itemCount, itemsPerPage);
        if (itemsPerPage == 0) {
            return 0;
        }
        return firstIndex / itemsPerPage;
    }
}

