/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.ingredients.subtypes;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import org.jetbrains.annotations.Nullable;

public class SubtypeManager
implements ISubtypeManager {
    private final SubtypeInterpreters interpreters;

    public SubtypeManager(SubtypeInterpreters interpreters) {
        this.interpreters = interpreters;
    }

    @Override
    @Nullable
    public <T> Object getSubtypeData(IIngredientTypeWithSubtypes<?, T> ingredientType, T ingredient, UidContext context) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        ErrorUtil.checkNotNull(context, "type");
        ISubtypeInterpreter<T> interpreter = this.interpreters.get(ingredientType, ingredient);
        if (interpreter == null) {
            return null;
        }
        return interpreter.getSubtypeData(ingredient, context);
    }

    @Override
    @Nullable
    public <B, T> Object getSubtypeData(IIngredientTypeWithSubtypes<B, T> ingredientType, ITypedIngredient<T> typedIngredient, UidContext context) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
        ErrorUtil.checkNotNull(context, "type");
        B ingredientBase = typedIngredient.getBaseIngredient(ingredientType);
        ISubtypeInterpreter<T> interpreter = this.interpreters.getFromBase(ingredientType, ingredientBase);
        if (interpreter == null) {
            return null;
        }
        T ingredient = typedIngredient.getIngredient();
        return interpreter.getSubtypeData(ingredient, context);
    }

    @Override
    public <T> String getSubtypeInfo(IIngredientTypeWithSubtypes<?, T> ingredientType, T ingredient, UidContext context) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        ErrorUtil.checkNotNull(context, "context");
        ISubtypeInterpreter<T> interpreter = this.interpreters.get(ingredientType, ingredient);
        if (interpreter == null) {
            return "";
        }
        return interpreter.getLegacyStringSubtypeInfo(ingredient, context);
    }

    @Override
    public <T, B> boolean hasSubtypes(IIngredientTypeWithSubtypes<B, T> ingredientType, T ingredient) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        return this.interpreters.contains(ingredientType, ingredient);
    }
}

