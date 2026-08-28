/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.jetbrains.annotations.UnmodifiableView
 */
package mezz.jei.library.recipes.collect;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.UnmodifiableView;

public class IngredientToRecipesMap<R> {
    private final Map<Object, ArrayList<R>> uidToRecipes = new Object2ObjectOpenHashMap();

    public void add(R recipe, Collection<Object> ingredientUids) {
        for (Object uid : ingredientUids) {
            List recipes = this.uidToRecipes.computeIfAbsent(uid, k -> new ArrayList());
            recipes.add(recipe);
        }
    }

    public @UnmodifiableView List<R> get(Object ingredientUid) {
        List recipes = this.uidToRecipes.get(ingredientUid);
        if (recipes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(recipes);
    }

    public void compact() {
        this.uidToRecipes.values().forEach(ArrayList::trimToSize);
    }
}

