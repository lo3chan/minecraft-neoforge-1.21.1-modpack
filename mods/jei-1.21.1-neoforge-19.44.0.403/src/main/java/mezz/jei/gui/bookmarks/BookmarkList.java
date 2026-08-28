/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.RegistryAccess
 *  org.jetbrains.annotations.Nullable
 */
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
import mezz.jei.gui.bookmarks.BookmarkFactory;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.bookmarks.IngredientBookmark;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.config.IBookmarkConfig;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public class BookmarkList
implements IIngredientGridSource {
    private final List<IBookmark> bookmarksList = new LinkedList<IBookmark>();
    private final Set<IBookmark> bookmarksSet = new HashSet<IBookmark>();
    private final IRecipeManager recipeManager;
    private final IFocusFactory focusFactory;
    private final IIngredientManager ingredientManager;
    private final RegistryAccess registryAccess;
    private final IBookmarkConfig bookmarkConfig;
    private final IClientConfig clientConfig;
    private final IGuiHelper guiHelper;
    private final ICodecHelper codecHelper;
    private final List<IIngredientGridSource.SourceListChangedListener> listeners = new ArrayList<IIngredientGridSource.SourceListChangedListener>();
    private final BookmarkFactory bookmarkFactory;
    private final Codec<IBookmark> bookmarkCodec;

    public BookmarkList(IRecipeManager recipeManager, IFocusFactory focusFactory, IIngredientManager ingredientManager, RegistryAccess registryAccess, IBookmarkConfig bookmarkConfig, IClientConfig clientConfig, IGuiHelper guiHelper, ICodecHelper codecHelper, BookmarkFactory bookmarkFactory, Codec<IBookmark> bookmarkCodec) {
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
        }
        this.notifyListenersOfChange();
        this.bookmarkConfig.saveBookmarks(this.recipeManager, this.focusFactory, this.guiHelper, this.ingredientManager, this.registryAccess, this.codecHelper, this.bookmarksList, this.bookmarkCodec);
        return true;
    }

    public void moveBookmark(IBookmark previousBookmark, IBookmark newBookmark, int offset) {
        int j;
        if (!this.bookmarksSet.contains(newBookmark) || !this.bookmarksSet.contains(previousBookmark)) {
            return;
        }
        int i = this.bookmarksList.indexOf(previousBookmark);
        int newIndex = i + offset;
        if (newIndex == (j = this.bookmarksList.indexOf(newBookmark))) {
            return;
        }
        if (newIndex < 0) {
            newIndex += this.bookmarksList.size();
        }
        this.bookmarksList.remove(newBookmark);
        this.bookmarksList.add(newIndex %= this.bookmarksList.size(), newBookmark);
        this.notifyListenersOfChange();
        this.bookmarkConfig.saveBookmarks(this.recipeManager, this.focusFactory, this.guiHelper, this.ingredientManager, this.registryAccess, this.codecHelper, this.bookmarksList, this.bookmarkCodec);
    }

    public boolean contains(IBookmark value) {
        return this.bookmarksSet.contains(value);
    }

    public <T> boolean onElementBookmarked(IElement<T> element, UserInput input, BookmarkOverlay bookmarkOverlay) {
        if (bookmarkOverlay.isMouseOver(input.getMouseX(), input.getMouseY())) {
            return element.getBookmark().map(this::remove).orElse(false);
        }
        ITypedIngredient<T> ingredient = element.getTypedIngredient();
        IngredientBookmark<T> bookmark = this.bookmarkFactory.create(ingredient);
        return this.add(bookmark);
    }

    public <T> boolean addIngredientBookmark(ITypedIngredient<T> ingredient) {
        IngredientBookmark<T> bookmark = this.bookmarkFactory.create(ingredient);
        return this.add(bookmark);
    }

    public void toggleBookmark(IBookmark bookmark) {
        if (this.remove(bookmark)) {
            return;
        }
        this.add(bookmark);
    }

    public boolean remove(IBookmark ingredient) {
        if (!this.bookmarksSet.remove(ingredient)) {
            return false;
        }
        this.bookmarksList.remove(ingredient);
        this.notifyListenersOfChange();
        this.bookmarkConfig.saveBookmarks(this.recipeManager, this.focusFactory, this.guiHelper, this.ingredientManager, this.registryAccess, this.codecHelper, this.bookmarksList, this.bookmarkCodec);
        return true;
    }

    public void setFromConfigFile(List<IBookmark> bookmarks) {
        this.bookmarksList.clear();
        this.bookmarksSet.clear();
        for (IBookmark bookmark : bookmarks) {
            if (!this.bookmarksSet.add(bookmark)) continue;
            this.bookmarksList.add(bookmark);
        }
        this.notifyListenersOfChange();
    }

    private boolean addToListWithoutNotifying(IBookmark value, boolean addToFront) {
        if (this.contains(value)) {
            return false;
        }
        if (addToFront) {
            this.bookmarksList.addFirst(value);
            this.bookmarksSet.add(value);
        } else {
            this.bookmarksList.add(value);
            this.bookmarksSet.add(value);
        }
        return true;
    }

    @Override
    public List<IElement<?>> getElements() {
        return this.bookmarksList.stream().map(IBookmark::getElement).toList();
    }

    @Nullable
    public <R> RecipeBookmark<R, ?> getMatchingBookmark(RecipeType<R> recipeType, R recipe) {
        for (IBookmark bookmark : this.bookmarksList) {
            RecipeBookmark recipeBookmark;
            if (!(bookmark instanceof RecipeBookmark) || !(recipeBookmark = (RecipeBookmark)bookmark).isRecipe(recipeType, recipe)) continue;
            RecipeBookmark castBookmark = recipeBookmark;
            return castBookmark;
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

