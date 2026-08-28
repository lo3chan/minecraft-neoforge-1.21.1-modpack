/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.library.ingredients;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.IngredientVisibility;

public class IngredientBlacklistInternal
implements IIngredientManager.IIngredientListener {
    private final Set<Object> uidBlacklist = new HashSet<Object>();
    private WeakReference<IngredientVisibility> ingredientVisibilityRef = new WeakReference<Object>(null);

    public <V> boolean isIngredientBlacklistedByApi(ITypedIngredient<V> typedIngredient, IIngredientHelper<V> ingredientHelper) {
        Object uidWild;
        Object uid = ingredientHelper.getUid(typedIngredient, UidContext.Ingredient);
        if (uid.equals(uidWild = ingredientHelper.getGroupingUid(typedIngredient))) {
            return this.uidBlacklist.contains(uid);
        }
        return this.uidBlacklist.contains(uid) || this.uidBlacklist.contains(uidWild);
    }

    public void registerListener(IngredientVisibility ingredientVisibility) {
        this.ingredientVisibilityRef = new WeakReference<IngredientVisibility>(ingredientVisibility);
    }

    @Override
    public <V> void onIngredientsAdded(IIngredientHelper<V> ingredientHelper, Collection<ITypedIngredient<V>> ingredients) {
        ArrayList changedIngredients = new ArrayList();
        for (ITypedIngredient<V> ingredient : ingredients) {
            Object uid = ingredientHelper.getUid(ingredient, UidContext.Ingredient);
            if (!this.uidBlacklist.remove(uid)) continue;
            changedIngredients.add(ingredient);
        }
        this.notifyListenersOfVisibilityChange(changedIngredients, true);
    }

    @Override
    public <V> void onIngredientsRemoved(IIngredientHelper<V> ingredientHelper, Collection<ITypedIngredient<V>> ingredients) {
        ArrayList changedIngredients = new ArrayList();
        for (ITypedIngredient<V> ingredient : ingredients) {
            Object uid = ingredientHelper.getUid(ingredient, UidContext.Ingredient);
            if (!this.uidBlacklist.add(uid)) continue;
            changedIngredients.add(ingredient);
        }
        this.notifyListenersOfVisibilityChange(changedIngredients, false);
    }

    private <T> void notifyListenersOfVisibilityChange(Collection<ITypedIngredient<T>> ingredients, boolean visible) {
        IngredientVisibility ingredientVisibility = (IngredientVisibility)this.ingredientVisibilityRef.get();
        if (ingredientVisibility != null) {
            ingredientVisibility.notifyListeners(ingredients, visible);
        }
    }
}

