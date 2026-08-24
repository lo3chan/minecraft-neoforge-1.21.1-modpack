package mezz.jei.gui.recipes.layouts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeButtonControllerFactory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipeSortUtil;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class LazyRecipeLayoutList<T> implements IRecipeLayoutList {
   private final IRecipeManager recipeManager;
   private final IRecipeCategory<T> recipeCategory;
   private final RecipesGui recipesGui;
   private final IFocusGroup focusGroup;
   private final List<IRecipeLayoutWithButtons<?>> results;
   private final List<IRecipeLayoutWithButtons<?>> craftMissing;
   private final Iterator<T> unsortedIterator;
   private final int size;
   private final boolean matchingCraftable;
   private final BookmarkList bookmarkList;

   public LazyRecipeLayoutList(
      Set<RecipeSorterStage> recipeSorterStages,
      @Nullable AbstractContainerMenu container,
      IFocusedRecipes<T> selectedRecipes,
      BookmarkList bookmarkList,
      IRecipeManager recipeManager,
      RecipesGui recipesGui,
      IFocusGroup focusGroup
   ) {
      this.bookmarkList = bookmarkList;
      boolean matchingBookmarks = recipeSorterStages.contains(RecipeSorterStage.BOOKMARKED);
      boolean matchingCraftable = recipeSorterStages.contains(RecipeSorterStage.CRAFTABLE);
      this.recipeManager = recipeManager;
      this.recipesGui = recipesGui;
      this.focusGroup = focusGroup;
      this.results = new ArrayList<>();
      this.craftMissing = new ArrayList<>();
      this.recipeCategory = selectedRecipes.getRecipeCategory();
      List<T> recipes = selectedRecipes.getRecipes();
      this.size = recipes.size();
      if (matchingCraftable && container != null) {
         IRecipeTransferManager recipeTransferManager = Internal.getJeiRuntime().getRecipeTransferManager();
         this.matchingCraftable = recipeTransferManager.getRecipeTransferHandler(container, this.recipeCategory).isPresent();
      } else {
         this.matchingCraftable = false;
      }

      if (matchingBookmarks) {
         RecipeType<T> recipeType = this.recipeCategory.getRecipeType();
         List<IRecipeButtonControllerFactory> recipeButtonControllerFactories = recipeManager.getRecipeButtonControllerFactories();
         recipes = new ArrayList<>(recipes);
         Iterator<T> iterator = recipes.iterator();

         while (iterator.hasNext()) {
            T recipe = iterator.next();
            RecipeBookmark<T, ?> recipeBookmark = bookmarkList.getMatchingBookmark(recipeType, recipe);
            if (recipeBookmark != null) {
               IRecipeLayoutDrawable<T> recipeLayout = recipeManager.createRecipeLayoutDrawableOrShowError(this.recipeCategory, recipe, focusGroup);
               IRecipeLayoutWithButtons<T> recipeLayoutWithButtons = RecipeLayoutWithButtons.create(
                  recipeLayout, recipeBookmark, bookmarkList, recipesGui, recipeButtonControllerFactories
               );
               this.results.add(recipeLayoutWithButtons);
               iterator.remove();
            }
         }
      }

      this.unsortedIterator = recipes.iterator();
   }

   private IRecipeLayoutDrawable<T> createRecipeLayout(T recipe) {
      return this.recipeManager.createRecipeLayoutDrawableOrShowError(this.recipeCategory, recipe, this.focusGroup);
   }

   private IRecipeLayoutWithButtons<T> createRecipeLayoutWithButtons(
      IRecipeLayoutDrawable<T> recipeLayoutDrawable, IIngredientManager ingredientManager, List<IRecipeButtonControllerFactory> recipeButtonControllerFactories
   ) {
      RecipeBookmark<T, ?> recipeBookmark = RecipeBookmark.create(recipeLayoutDrawable, ingredientManager);
      return RecipeLayoutWithButtons.create(recipeLayoutDrawable, recipeBookmark, this.bookmarkList, this.recipesGui, recipeButtonControllerFactories);
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public List<IRecipeLayoutWithButtons<?>> subList(int from, int to) {
      this.ensureResults(to - 1);
      return this.results.subList(from, to);
   }

   private void ensureResults(int index) {
      AbstractContainerMenu container = this.recipesGui.getParentContainerMenu();

      while (index >= this.results.size()) {
         if (!this.calculateNextResult()) {
            return;
         }
      }
   }

   @Override
   public Optional<IRecipeLayoutWithButtons<?>> findFirst() {
      this.ensureResults(0);
      return this.results.isEmpty() ? Optional.empty() : Optional.of((IRecipeLayoutWithButtons<?>)this.results.getFirst());
   }

   @Override
   public void tick() {
      this.calculateNextResult();
   }

   private boolean calculateNextResult() {
      IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
      IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
      List<IRecipeButtonControllerFactory> recipeButtonControllerFactories = this.recipeManager.getRecipeButtonControllerFactories();

      while (this.unsortedIterator.hasNext()) {
         T recipe = this.unsortedIterator.next();
         IRecipeLayoutDrawable<T> recipeLayout = this.createRecipeLayout(recipe);
         IRecipeLayoutWithButtons<T> recipeLayoutWithButtons = this.createRecipeLayoutWithButtons(
            recipeLayout, ingredientManager, recipeButtonControllerFactories
         );
         if (!this.matchingCraftable) {
            this.results.add(recipeLayoutWithButtons);
            return true;
         }

         int missingCountHint = recipeLayoutWithButtons.getMissingCountHint();
         if (missingCountHint == 0) {
            this.results.add(recipeLayoutWithButtons);
            return true;
         }

         this.craftMissing.add(recipeLayoutWithButtons);
      }

      if (!this.craftMissing.isEmpty()) {
         this.craftMissing.sort(RecipeSortUtil.getComparator());
         this.results.addAll(this.craftMissing);
         this.craftMissing.clear();
         return true;
      } else {
         return false;
      }
   }
}
