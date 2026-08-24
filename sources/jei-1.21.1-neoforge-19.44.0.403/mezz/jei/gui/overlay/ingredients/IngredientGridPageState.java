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

   public int getFirstItemIndex() {
      return this.firstItemIndex;
   }

   public int updateForPageNavigation(int firstItemIndex, int itemCount, int itemsPerPage) {
      this.pageAnchorElement = null;
      this.firstItemIndex = getFirstItemIndexForValidPage(firstItemIndex, itemCount, itemsPerPage);
      return this.firstItemIndex;
   }

   public int updateKeepingPageAnchorVisible(@Nullable IElement<?> pageAnchorElement, List<IElement<?>> ingredientList, int itemsPerPage) {
      int anchorIndex = findIndexOfIngredientElement(pageAnchorElement, ingredientList);
      this.firstItemIndex = getFirstItemIndexForValidPage(anchorIndex, ingredientList.size(), itemsPerPage);
      return this.firstItemIndex;
   }

   @Nullable
   public IElement<?> getPageAnchorElement(List<IElement<?>> ingredientList) {
      if (this.pageAnchorElement != null) {
         if (findIndexOfIngredientElement(this.pageAnchorElement, ingredientList) >= 0) {
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
      } else {
         for (int i = 0; i < ingredientList.size(); i++) {
            if (isSameIngredientElement(ingredientList.get(i), element)) {
               return i;
            }
         }

         return -1;
      }
   }

   static int getFirstItemIndexForValidPage(int firstItemIndex, int itemCount, int itemsPerPage) {
      if (itemCount != 0 && itemsPerPage != 0) {
         int requestedPageStart = Math.max(0, firstItemIndex) / itemsPerPage * itemsPerPage;
         int lastPageIndex = (itemCount - 1) / itemsPerPage * itemsPerPage;
         return Math.min(requestedPageStart, lastPageIndex);
      } else {
         return 0;
      }
   }

   static boolean isSameIngredientElement(IElement<?> first, IElement<?> second) {
      if (first == second) {
         return true;
      } else {
         ITypedIngredient<?> firstIngredient = first.getTypedIngredient();
         ITypedIngredient<?> secondIngredient = second.getTypedIngredient();
         return firstIngredient == secondIngredient
            || firstIngredient.getType().equals(secondIngredient.getType()) && firstIngredient.getIngredient() == secondIngredient.getIngredient();
      }
   }

   static int getPageCount(int itemCount, int itemsPerPage) {
      if (itemsPerPage == 0) {
         return 1;
      } else {
         int pageCount = MathUtil.divideCeil(itemCount, itemsPerPage);
         return Math.max(1, pageCount);
      }
   }

   static int getPageNumberForFirstItemIndex(int firstItemIndex, int itemsPerPage, int itemCount) {
      int firstIndex = getFirstItemIndexForValidPage(firstItemIndex, itemCount, itemsPerPage);
      return itemsPerPage == 0 ? 0 : firstIndex / itemsPerPage;
   }
}
