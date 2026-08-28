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
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TypedIngredientSet<T>
extends AbstractSet<ITypedIngredient<T>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IIngredientHelper<T> ingredientHelper;
    private final UidContext context;
    private final Map<Object, ITypedIngredient<T>> ingredients;

    public TypedIngredientSet(IIngredientHelper<T> ingredientHelper, UidContext context) {
        this.ingredientHelper = ingredientHelper;
        this.context = context;
        this.ingredients = new LinkedHashMap<Object, ITypedIngredient<T>>();
    }

    @Nullable
    private Object getUid(ITypedIngredient<T> typedIngredient) {
        try {
            return this.ingredientHelper.getUid(typedIngredient, this.context);
        }
        catch (RuntimeException e) {
            try {
                String ingredientInfo = this.ingredientHelper.getErrorInfo(typedIngredient.getIngredient());
                LOGGER.warn("Found a broken ingredient {}", (Object)ingredientInfo, (Object)e);
            }
            catch (RuntimeException e2) {
                LOGGER.warn("Found a broken ingredient.", (Throwable)e2);
            }
            return null;
        }
    }

    @Override
    public boolean add(ITypedIngredient<T> value) {
        Object uid = this.getUid(value);
        return uid != null && this.ingredients.put(uid, value) == null;
    }

    @Override
    public boolean remove(Object value) {
        if (value instanceof ITypedIngredient) {
            ITypedIngredient typedIngredient = (ITypedIngredient)value;
            IIngredientType type = typedIngredient.getType();
            if (this.ingredientHelper.getIngredientType().equals(type)) {
                ITypedIngredient cast = typedIngredient;
                Object uid = this.getUid(cast);
                return uid != null && this.ingredients.remove(uid) != null;
            }
        }
        return false;
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
    public boolean addAll(Collection<? extends ITypedIngredient<T>> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        for (ITypedIngredient<T> value : c) {
            modified |= this.add(value);
        }
        return modified;
    }

    @Override
    public boolean contains(Object value) {
        if (value instanceof ITypedIngredient) {
            ITypedIngredient typedIngredient = (ITypedIngredient)value;
            IIngredientType type = typedIngredient.getType();
            if (this.ingredientHelper.getIngredientType().equals(type)) {
                ITypedIngredient cast = typedIngredient;
                Object uid = this.getUid(cast);
                return uid != null && this.ingredients.containsKey(uid);
            }
        }
        return false;
    }

    @Deprecated(forRemoval=true)
    public Optional<ITypedIngredient<T>> getByLegacyUid(String uid) {
        ITypedIngredient<T> v = this.ingredients.get(uid);
        if (v != null) {
            return Optional.of(v);
        }
        for (ITypedIngredient<T> typedIngredient : this.ingredients.values()) {
            String legacyUid = this.ingredientHelper.getUniqueId(typedIngredient.getIngredient(), this.context);
            if (!uid.equals(legacyUid)) continue;
            return Optional.of(typedIngredient);
        }
        return Optional.empty();
    }

    @Override
    public void clear() {
        this.ingredients.clear();
    }

    @Override
    public Iterator<ITypedIngredient<T>> iterator() {
        return this.ingredients.values().iterator();
    }

    @Override
    public int size() {
        return this.ingredients.size();
    }
}

