package mezz.jei.gui.recipes;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.bookmarks.BookmarkFactory;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.IngredientBookmark;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistory;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import mezz.jei.gui.recipes.lookups.ILookupState;
import mezz.jei.gui.recipes.lookups.IngredientLookupState;
import mezz.jei.gui.recipes.lookups.SingleCategoryLookupState;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class RecipeGuiLogic implements IRecipeGuiLogic {
   private final IRecipeManager recipeManager;
   private final IIngredientManager ingredientManager;
   private final IRecipeTransferManager recipeTransferManager;
   private final IRecipeLogicStateListener stateListener;
   private boolean initialState = true;
   private ILookupState state;
   private final Stack<ILookupState> stateHistory = new Stack<>();
   private final LookupHistory lookupHistory;
   private final IFocusFactory focusFactory;
   private final BookmarkFactory bookmarkFactory;
   @Nullable
   private IRecipeCategory<?> cachedRecipeCategory;
   @Nullable
   private IRecipeLayoutList cachedRecipeLayoutsWithButtons;
   private int cachedContainerId = -1;
   private Set<RecipeSorterStage> cachedSorterStages = Set.of();

   public RecipeGuiLogic(
      IRecipeManager recipeManager,
      IIngredientManager ingredientManager,
      LookupHistory lookupHistory,
      IRecipeTransferManager recipeTransferManager,
      IRecipeLogicStateListener stateListener,
      IFocusFactory focusFactory,
      BookmarkFactory bookmarkFactory
   ) {
      this.recipeManager = recipeManager;
      this.ingredientManager = ingredientManager;
      this.lookupHistory = lookupHistory;
      this.recipeTransferManager = recipeTransferManager;
      this.stateListener = stateListener;
      List<IRecipeCategory<?>> recipeCategories = recipeManager.createRecipeCategoryLookup().get().toList();
      this.state = IngredientLookupState.create(recipeManager, focusFactory.getEmptyFocusGroup(), recipeCategories, recipeTransferManager);
      this.focusFactory = focusFactory;
      this.bookmarkFactory = bookmarkFactory;
   }

   @Override
   public void tick() {
      if (this.cachedRecipeLayoutsWithButtons != null) {
         this.cachedRecipeLayoutsWithButtons.tick();
      }
   }

   @Override
   public boolean showFocus(IFocusGroup focuses) {
      List<IFocus<?>> allFocuses = focuses.getAllFocuses();
      List<IRecipeCategory<?>> recipeCategories = this.recipeManager.createRecipeCategoryLookup().limitFocus(allFocuses).get().toList();
      ILookupState state = IngredientLookupState.create(this.recipeManager, focuses, recipeCategories, this.recipeTransferManager);

      for (IFocus<?> focus : allFocuses) {
         IngredientBookmark<?> ingredientBookmark = this.bookmarkFactory.create(focus.getTypedValue());
         this.lookupHistory.add(ingredientBookmark);
      }

      return this.setState(state, true);
   }

   @Override
   public boolean showRecipes(IFocusedRecipes<?> focusedRecipes, IFocusGroup focuses) {
      RecipeBookmark<?, ?> recipeBookmark = createRecipeBookmark(this.recipeManager, this.ingredientManager, focusedRecipes, focuses);
      if (recipeBookmark != null) {
         this.lookupHistory.add(recipeBookmark);
      } else {
         for (IFocus<?> focus : focuses.getAllFocuses()) {
            IngredientBookmark<?> ingredientBookmark = this.bookmarkFactory.create(focus.getTypedValue());
            this.lookupHistory.add(ingredientBookmark);
         }
      }

      ILookupState state = new SingleCategoryLookupState(focusedRecipes, focuses);
      return this.setState(state, true);
   }

   @Nullable
   private static <T> RecipeBookmark<T, ?> createRecipeBookmark(
      IRecipeManager recipeManager, IIngredientManager ingredientManager, IFocusedRecipes<T> focusedRecipes, IFocusGroup focusGroup
   ) {
      IRecipeCategory<T> recipeCategory = focusedRecipes.getRecipeCategory();
      List<T> recipes = focusedRecipes.getRecipes();
      if (recipes.size() != 1) {
         return null;
      } else {
         T recipe = (T)recipes.getFirst();
         return recipeManager.createRecipeLayoutDrawable(recipeCategory, recipe, focusGroup)
            .map(drawable -> RecipeBookmark.create((IRecipeLayoutDrawable<T>)drawable, ingredientManager))
            .orElse(null);
      }
   }

   @Override
   public boolean back() {
      if (this.stateHistory.empty()) {
         return false;
      } else {
         ILookupState state = this.stateHistory.pop();
         this.setState(state, false);
         return true;
      }
   }

   @Override
   public void clearHistory() {
      while (!this.stateHistory.empty()) {
         this.stateHistory.pop();
      }
   }

   private boolean setState(ILookupState state, boolean saveHistory) {
      List<IRecipeCategory<?>> recipeCategories = state.getRecipeCategories();
      if (recipeCategories.isEmpty()) {
         return false;
      } else {
         if (saveHistory && !this.initialState) {
            this.stateHistory.push(this.state);
         }

         this.state = state;
         this.initialState = false;
         this.cachedRecipeCategory = null;
         this.cachedRecipeLayoutsWithButtons = null;
         this.cachedContainerId = -1;
         this.stateListener.onStateChange();
         return true;
      }
   }

   @Override
   public boolean showAllRecipes() {
      IRecipeCategory<?> recipeCategory = this.getSelectedRecipeCategory();
      List<IRecipeCategory<?>> recipeCategories = this.recipeManager.createRecipeCategoryLookup().get().toList();
      ILookupState state = IngredientLookupState.create(
         this.recipeManager, this.focusFactory.getEmptyFocusGroup(), recipeCategories, this.recipeTransferManager
      );
      state.moveToRecipeCategory(recipeCategory);
      this.setState(state, true);
      return true;
   }

   @Override
   public boolean showCategories(List<RecipeType<?>> recipeTypes) {
      List<IRecipeCategory<?>> recipeCategories = this.recipeManager.createRecipeCategoryLookup().limitTypes(recipeTypes).get().toList();
      ILookupState state = IngredientLookupState.create(
         this.recipeManager, this.focusFactory.getEmptyFocusGroup(), recipeCategories, this.recipeTransferManager
      );
      if (state.getRecipeCategories().isEmpty()) {
         return false;
      } else {
         this.setState(state, true);
         return true;
      }
   }

   @Override
   public Stream<ITypedIngredient<?>> getRecipeCatalysts() {
      IRecipeCategory<?> category = this.getSelectedRecipeCategory();
      return this.getRecipeCatalysts(category);
   }

   @Override
   public Stream<ITypedIngredient<?>> getRecipeCatalysts(IRecipeCategory<?> recipeCategory) {
      RecipeType<?> recipeType = recipeCategory.getRecipeType();
      return this.recipeManager.createRecipeCatalystLookup(recipeType).get();
   }

   @Override
   public IRecipeCategory<?> getSelectedRecipeCategory() {
      return this.state.getFocusedRecipes().getRecipeCategory();
   }

   @Unmodifiable
   @Override
   public List<IRecipeCategory<?>> getRecipeCategories() {
      return this.state.getRecipeCategories();
   }

   @Override
   public List<IRecipeLayoutWithButtons<?>> getVisibleRecipeLayoutsWithButtons(
      int availableHeight, int minRecipePadding, @Nullable AbstractContainerMenu container, BookmarkList bookmarkList, RecipesGui recipesGui
   ) {
      IRecipeCategory<?> recipeCategory = this.getSelectedRecipeCategory();
      IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
      IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
      Set<RecipeSorterStage> recipeSorterStages = RecipeSorterStage.getEnabled(clientConfig);
      int containerId = container == null ? -1 : container.containerId;
      if (!recipeSorterStages.equals(this.cachedSorterStages)
         || this.cachedRecipeLayoutsWithButtons == null
         || this.cachedRecipeCategory != recipeCategory
         || this.cachedContainerId != containerId) {
         IFocusedRecipes<?> focusedRecipes = this.state.getFocusedRecipes();
         this.cachedRecipeLayoutsWithButtons = IRecipeLayoutList.create(
            recipeSorterStages, container, focusedRecipes, this.state.getFocuses(), bookmarkList, this.recipeManager, recipesGui
         );
         this.cachedRecipeCategory = recipeCategory;
         this.cachedSorterStages = Set.copyOf(recipeSorterStages);
         this.cachedContainerId = containerId;
      }

      int recipeHeight = this.cachedRecipeLayoutsWithButtons
         .findFirst()
         .map(IRecipeLayoutWithButtons::getRecipeLayout)
         .map(IRecipeLayoutDrawable::getRectWithBorder)
         .<Integer>map(Rect2i::getHeight)
         .orElseGet(recipeCategory::getHeight);
      int recipesPerPage = Math.max(1, 1 + (availableHeight - recipeHeight) / (recipeHeight + minRecipePadding));
      this.state.setRecipesPerPage(recipesPerPage);
      return this.state.getVisible(this.cachedRecipeLayoutsWithButtons);
   }

   @Override
   public int getRecipesPerPage() {
      return this.state.getRecipesPerPage();
   }

   @Override
   public boolean nextRecipeCategory() {
      if (this.state.nextRecipeCategory()) {
         this.stateListener.onStateChange();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setRecipeCategory(IRecipeCategory<?> category) {
      if (this.state.moveToRecipeCategory(category)) {
         this.stateListener.onStateChange();
      }
   }

   @Override
   public boolean hasMultiplePages() {
      List<?> recipes = this.state.getFocusedRecipes().getRecipes();
      return recipes.size() > this.state.getRecipesPerPage();
   }

   @Override
   public boolean previousRecipeCategory() {
      if (this.state.previousRecipeCategory()) {
         this.stateListener.onStateChange();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void goToFirstPage() {
      this.state.goToFirstPage();
      this.stateListener.onStateChange();
   }

   @Override
   public boolean nextPage() {
      if (this.state.nextPage()) {
         this.stateListener.onStateChange();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean previousPage() {
      if (this.state.previousPage()) {
         this.stateListener.onStateChange();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String getPageString() {
      int pageIndex = MathUtil.divideCeil(this.state.getRecipeIndex() + 1, this.state.getRecipesPerPage());
      return pageIndex + "/" + this.state.pageCount();
   }

   @Override
   public boolean hasMultipleCategories() {
      return this.state.getRecipeCategories().size() > 1;
   }

   @Override
   public boolean hasAllCategories() {
      long categoryCount = this.recipeManager.createRecipeCategoryLookup().get().count();
      return this.state.getRecipeCategories().size() == categoryCount;
   }
}
