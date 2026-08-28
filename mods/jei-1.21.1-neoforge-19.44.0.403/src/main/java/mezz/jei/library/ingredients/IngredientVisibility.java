/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.ingredients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.library.config.EditModeConfig;
import mezz.jei.library.ingredients.IngredientBlacklistInternal;
import mezz.jei.library.ingredients.TypedIngredient;
import org.jetbrains.annotations.Nullable;

public class IngredientVisibility
implements IIngredientVisibility {
    private final IngredientBlacklistInternal blacklist;
    private final IClientToggleState toggleState;
    private final EditModeConfig editModeConfig;
    private final IIngredientManager ingredientManager;
    private final List<IIngredientVisibility.IListener> listeners = new ArrayList<IIngredientVisibility.IListener>();

    public IngredientVisibility(IngredientBlacklistInternal blacklist, IClientToggleState toggleState, EditModeConfig editModeConfig, IIngredientManager ingredientManager) {
        this.blacklist = blacklist;
        this.toggleState = toggleState;
        this.editModeConfig = editModeConfig;
        this.ingredientManager = ingredientManager;
        editModeConfig.registerListener(this);
        blacklist.registerListener(this);
    }

    @Override
    public <V> boolean isIngredientVisible(ITypedIngredient<V> typedIngredient) {
        IIngredientType<V> ingredientType = typedIngredient.getType();
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(ingredientType);
        return this.isIngredientVisible(typedIngredient, ingredientHelper);
    }

    @Override
    public <V> boolean isIngredientVisible(IIngredientType<V> ingredientType, V ingredient) {
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(ingredientType);
        @Nullable ITypedIngredient<V> typedIngredient = TypedIngredient.createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, false);
        if (typedIngredient == null) {
            return false;
        }
        return this.isIngredientVisible(typedIngredient, ingredientHelper);
    }

    public <V> boolean isIngredientVisible(ITypedIngredient<V> typedIngredient, IIngredientHelper<V> ingredientHelper) {
        if (this.blacklist.isIngredientBlacklistedByApi(typedIngredient, ingredientHelper)) {
            return false;
        }
        if (ingredientHelper.isHiddenFromRecipeViewersByTags(typedIngredient)) {
            return false;
        }
        return this.toggleState.isEditModeEnabled() || !this.editModeConfig.isIngredientHiddenUsingConfigFile(typedIngredient);
    }

    @Override
    public void registerListener(IIngredientVisibility.IListener listener) {
        this.listeners.add(listener);
    }

    public <V> void notifyListeners(Collection<ITypedIngredient<V>> ingredients, boolean visible) {
        this.listeners.forEach(listener -> listener.onIngredientsVisibilityChanged(ingredients, visible));
    }

    public void onRuntimeStopped() {
        this.listeners.clear();
    }
}

