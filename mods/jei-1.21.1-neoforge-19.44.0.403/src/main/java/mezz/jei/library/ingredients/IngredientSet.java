/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.ingredients;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class IngredientSet<V>
extends AbstractSet<V> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IIngredientHelper<V> ingredientHelper;
    private final UidContext context;
    private final Map<Object, V> ingredients;

    public IngredientSet(IIngredientHelper<V> ingredientHelper, UidContext context) {
        this.ingredientHelper = ingredientHelper;
        this.context = context;
        this.ingredients = new LinkedHashMap<Object, V>();
    }

    @Nullable
    private Object getUid(V ingredient) {
        try {
            return this.ingredientHelper.getUid(ingredient, this.context);
        }
        catch (RuntimeException e) {
            try {
                String ingredientInfo = this.ingredientHelper.getErrorInfo(ingredient);
                LOGGER.warn("Found a broken ingredient {}", (Object)ingredientInfo, (Object)e);
            }
            catch (RuntimeException e2) {
                LOGGER.warn("Found a broken ingredient.", (Throwable)e2);
            }
            return null;
        }
    }

    @Override
    public boolean add(V value) {
        Object uid = this.getUid(value);
        return uid != null && this.ingredients.put(uid, value) == null;
    }

    @Override
    public boolean remove(Object value) {
        Object cast = value;
        Object uid = this.getUid(cast);
        return uid != null && this.ingredients.remove(uid) != null;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (Object value : c) {
            modified |= this.remove(value);
        }
        return modified;
    }

    @Override
    public boolean contains(Object o) {
        IIngredientType<V> ingredientType = this.ingredientHelper.getIngredientType();
        Class<V> ingredientClass = ingredientType.getIngredientClass();
        if (!ingredientClass.isInstance(o)) {
            return false;
        }
        V v = ingredientClass.cast(o);
        Object uid = this.getUid(v);
        return uid != null && this.ingredients.containsKey(uid);
    }

    @Deprecated(forRemoval=true)
    public Optional<V> getByLegacyUid(String uid) {
        V v = this.ingredients.get(uid);
        if (v != null) {
            return Optional.of(v);
        }
        for (V ingredient : this.ingredients.values()) {
            String legacyUid = this.ingredientHelper.getUniqueId(ingredient, this.context);
            if (!uid.equals(legacyUid)) continue;
            return Optional.of(ingredient);
        }
        return Optional.empty();
    }

    @Override
    public void clear() {
        this.ingredients.clear();
    }

    @Override
    public Iterator<V> iterator() {
        return this.ingredients.values().iterator();
    }

    @Override
    public int size() {
        return this.ingredients.size();
    }
}

