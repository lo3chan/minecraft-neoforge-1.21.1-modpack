/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.RegistryAccess
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.overlay.bookmarks.history;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.config.IJeiConfigValue;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.config.ILookupHistoryConfig;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.IIngredientGridSource;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Unmodifiable;

public class LookupHistory
implements IIngredientGridSource {
    private final List<IBookmark> elements = new LinkedList<IBookmark>();
    private final List<IIngredientGridSource.SourceListChangedListener> listeners = new ArrayList<IIngredientGridSource.SourceListChangedListener>();
    private final IRecipeManager recipeManager;
    private final IIngredientManager ingredientManager;
    private final RegistryAccess registryAccess;
    private final ICodecHelper codecHelper;
    private final IJeiConfigValue<Integer> maxElements;
    private final ILookupHistoryConfig lookupHistoryConfig;
    private final Codec<IBookmark> bookmarkCodec;

    public LookupHistory(IRecipeManager recipeManager, IIngredientManager ingredientManager, RegistryAccess registryAccess, ICodecHelper codecHelper, IJeiConfigValue<Integer> maxElements, ILookupHistoryConfig lookupHistoryConfig, Codec<IBookmark> bookmarkCodec) {
        this.recipeManager = recipeManager;
        this.ingredientManager = ingredientManager;
        this.registryAccess = registryAccess;
        this.codecHelper = codecHelper;
        this.maxElements = maxElements;
        this.lookupHistoryConfig = lookupHistoryConfig;
        this.bookmarkCodec = bookmarkCodec;
        List<IBookmark> loaded = lookupHistoryConfig.load(recipeManager, ingredientManager, registryAccess, codecHelper, bookmarkCodec);
        this.elements.addAll(loaded);
        maxElements.addListener(v -> this.trimToMaxElements());
        this.trimToMaxElements();
    }

    public void add(IBookmark element) {
        this.elements.remove(element);
        this.elements.addFirst(element);
        if (this.elements.size() > this.maxElements.getValue()) {
            this.elements.removeLast();
        }
        this.notifyListeners();
        this.save();
    }

    @Override
    public @Unmodifiable List<IElement<?>> getElements() {
        return this.elements.stream().map(IBookmark::getElement).toList();
    }

    @Override
    public void addSourceListChangedListener(IIngredientGridSource.SourceListChangedListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners() {
        for (IIngredientGridSource.SourceListChangedListener listener : this.listeners) {
            listener.onSourceListChanged();
        }
    }

    private void trimToMaxElements() {
        boolean changed = false;
        while (this.elements.size() > this.maxElements.getValue()) {
            this.elements.removeLast();
            changed = true;
        }
        if (changed) {
            this.notifyListeners();
            this.save();
        }
    }

    private void save() {
        this.lookupHistoryConfig.save(this.recipeManager, this.ingredientManager, this.registryAccess, this.codecHelper, this.elements, this.bookmarkCodec);
    }
}

