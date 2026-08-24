package mezz.jei.gui.bookmarks;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.gui.config.IBookmarkConfig;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public class BookmarkList implements IIngredientGridSource {
   private final List<IBookmark> bookmarksList = new LinkedList<>();
   private final Set<IBookmark> bookmarksSet = new HashSet<>();
   private final IRecipeManager recipeManager;
   private final IFocusFactory focusFactory;
   private final IIngredientManager ingredientManager;
   private final RegistryAccess registryAccess;
   private final IBookmarkConfig bookmarkConfig;
   private final IClientConfig clientConfig;
   private final IGuiHelper guiHelper;
   private final ICodecHelper codecHelper;
   private final List<IIngredientGridSource.SourceListChangedListener> listeners = new ArrayList<>();
   private final BookmarkFactory bookmarkFactory;
   private final Codec<IBookmark> bookmarkCodec;

   public BookmarkList(
      IRecipeManager recipeManager,
      IFocusFactory focusFactory,
      IIngredientManager ingredientManager,
      RegistryAccess registryAccess,
      IBookmarkConfig bookmarkConfig,
      IClientConfig clientConfig,
      IGuiHelper guiHelper,
      ICodecHelper codecHelper,
      BookmarkFactory bookmarkFactory,
      Codec<IBookmark> bookmarkCodec
   ) {
      this.recipeManager = recipeManager;
      this.focusFactory = focusFactory;
      this.ingredientManager = ingredientManager;
      this.registryAccess = registryAccess;
      this.bookmarkConfig = bookmarkConfig;
      this.clientConfig = clientConfig;
      this.guiHelper = guiHelper;
      this.codecHelper = codecHelper;
      this.bookmarkFactory = bookmarkFactory;
      this.bookmarkCodec = bookmarkCodec;
   }

   public boolean add(IBookmark value) {
      if (!this.addToListWithoutNotifying(value, this.clientConfig.addBookmarksToFrontEnabled().getValue())) {
         return false;
      } else {
         this.notifyListenersOfChange();
         this.bookmarkConfig
            .saveBookmarks(
               this.recipeManager,
               this.focusFactory,
               this.guiHelper,
               this.ingredientManager,
               this.registryAccess,
               this.codecHelper,
               this.bookmarksList,
               this.bookmarkCodec
            );
         return true;
      }
   }

   public void moveBookmark(IBookmark previousBookmark, IBookmark newBookmark, int offset) {
      if (this.bookmarksSet.contains(newBookmark) && this.bookmarksSet.contains(previousBookmark)) {
         int i = this.bookmarksList.indexOf(previousBookmark);
         int j = this.bookmarksList.indexOf(newBookmark);
         int newIndex = i + offset;
         if (newIndex != j) {
            if (newIndex < 0) {
               newIndex += this.bookmarksList.size();
            }

            newIndex %= this.bookmarksList.size();
            this.bookmarksList.remove(newBookmark);
            this.bookmarksList.add(newIndex, newBookmark);
            this.notifyListenersOfChange();
            this.bookmarkConfig
               .saveBookmarks(
                  this.recipeManager,
                  this.focusFactory,
                  this.guiHelper,
                  this.ingredientManager,
                  this.registryAccess,
                  this.codecHelper,
                  this.bookmarksList,
                  this.bookmarkCodec
               );
         }
      }
   }

   public boolean contains(IBookmark value) {
      return this.bookmarksSet.contains(value);
   }

   public <T> boolean onElementBookmarked(IElement<T> element, UserInput input, BookmarkOverlay bookmarkOverlay) {
      if (bookmarkOverlay.isMouseOver(input.getMouseX(), input.getMouseY())) {
         return element.getBookmark().map(this::remove).orElse(false);
      } else {
         ITypedIngredient<T> ingredient = element.getTypedIngredient();
         IBookmark bookmark = this.bookmarkFactory.create(ingredient);
         return this.add(bookmark);
      }
   }

   public <T> boolean addIngredientBookmark(ITypedIngredient<T> ingredient) {
      IBookmark bookmark = this.bookmarkFactory.create(ingredient);
      return this.add(bookmark);
   }

   public void toggleBookmark(IBookmark bookmark) {
      if (!this.remove(bookmark)) {
         this.add(bookmark);
      }
   }

   public boolean remove(IBookmark ingredient) {
      if (!this.bookmarksSet.remove(ingredient)) {
         return false;
      } else {
         this.bookmarksList.remove(ingredient);
         this.notifyListenersOfChange();
         this.bookmarkConfig
            .saveBookmarks(
               this.recipeManager,
               this.focusFactory,
               this.guiHelper,
               this.ingredientManager,
               this.registryAccess,
               this.codecHelper,
               this.bookmarksList,
               this.bookmarkCodec
            );
         return true;
      }
   }

   public void setFromConfigFile(List<IBookmark> bookmarks) {
      this.bookmarksList.clear();
      this.bookmarksSet.clear();

      for (IBookmark bookmark : bookmarks) {
         if (this.bookmarksSet.add(bookmark)) {
            this.bookmarksList.add(bookmark);
         }
      }

      this.notifyListenersOfChange();
   }

   private boolean addToListWithoutNotifying(IBookmark value, boolean addToFront) {
      if (this.contains(value)) {
         return false;
      } else {
         if (addToFront) {
            this.bookmarksList.addFirst(value);
            this.bookmarksSet.add(value);
         } else {
            this.bookmarksList.add(value);
            this.bookmarksSet.add(value);
         }

         return true;
      }
   }

   @Override
   public List<IElement<?>> getElements() {
      return this.bookmarksList.stream().map(IBookmark::getElement).toList();
   }

   @Nullable
   public <R> RecipeBookmark<R, ?> getMatchingBookmark(RecipeType<R> recipeType, R recipe) {
      for (IBookmark bookmark : this.bookmarksList) {
         if (bookmark instanceof RecipeBookmark<?, ?> recipeBookmark && recipeBookmark.isRecipe(recipeType, recipe)) {
            return (RecipeBookmark<R, ?>)recipeBookmark;
         }
      }

      return null;
   }

   public boolean isEmpty() {
      return this.bookmarksSet.isEmpty();
   }

   @Override
   public void addSourceListChangedListener(IIngredientGridSource.SourceListChangedListener listener) {
      this.listeners.add(listener);
   }

   private void notifyListenersOfChange() {
      for (IIngredientGridSource.SourceListChangedListener listener : this.listeners) {
         listener.onSourceListChanged();
      }
   }
}
