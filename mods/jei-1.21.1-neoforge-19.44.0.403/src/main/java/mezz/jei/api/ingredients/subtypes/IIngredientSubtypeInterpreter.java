/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.UidContext;

@Deprecated(since="19.9.0", forRemoval=true)
@FunctionalInterface
public interface IIngredientSubtypeInterpreter<T> {
    @Deprecated(since="19.9.0", forRemoval=true)
    public static final String NONE = "";

    @Deprecated(since="19.9.0", forRemoval=true)
    public String apply(T var1, UidContext var2);
}

