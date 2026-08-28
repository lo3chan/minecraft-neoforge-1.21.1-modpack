/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  java.util.SequencedMap
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.library.ingredients;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.IngredientInfo;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class RegisteredIngredients {
    private final @Unmodifiable List<IIngredientType<?>> orderedTypes;
    private final @Unmodifiable Map<IIngredientType<?>, IngredientInfo<?>> typeToInfo;
    private final Map<Class<?>, IIngredientType<?>> classToType;
    private final Map<Class<?>, IIngredientTypeWithSubtypes<?, ?>> baseClassToType;

    public RegisteredIngredients(SequencedMap<IIngredientType<?>, IngredientInfo<?>> ingredientInfoList) {
        this.orderedTypes = ingredientInfoList.sequencedValues().stream().map(IngredientInfo::getIngredientType).toList();
        this.typeToInfo = new Object2ObjectArrayMap(ingredientInfoList);
        this.classToType = this.orderedTypes.stream().collect(Collectors.toMap(IIngredientType::getIngredientClass, Function.identity()));
        this.baseClassToType = this.orderedTypes.stream().filter(IIngredientTypeWithSubtypes.class::isInstance).map(IIngredientTypeWithSubtypes.class::cast).collect(Collectors.toMap(IIngredientTypeWithSubtypes::getIngredientBaseClass, Function.identity()));
    }

    public <V> IngredientInfo<V> getIngredientInfo(IIngredientType<V> ingredientType) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        IngredientInfo<?> ingredientInfo = this.typeToInfo.get(ingredientType);
        if (ingredientInfo == null) {
            throw new IllegalArgumentException("Unknown ingredient type: " + String.valueOf(ingredientType.getIngredientClass()));
        }
        return ingredientInfo;
    }

    public @Unmodifiable List<IIngredientType<?>> getIngredientTypes() {
        return this.orderedTypes;
    }

    @Nullable
    public <V> IIngredientType<V> getIngredientType(V ingredient) {
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        Class<?> ingredientClass = ingredient.getClass();
        return this.getIngredientType(ingredientClass);
    }

    @Nullable
    public <V> IIngredientType<V> getIngredientType(Class<? extends V> ingredientClass) {
        ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
        IIngredientType<?> ingredientType = this.classToType.get(ingredientClass);
        if (ingredientType != null) {
            return ingredientType;
        }
        for (IIngredientType<?> type : this.orderedTypes) {
            if (!type.getIngredientClass().isAssignableFrom(ingredientClass)) continue;
            IIngredientType<?> castType = type;
            this.classToType.put(ingredientClass, castType);
            return castType;
        }
        return null;
    }

    public <I, B> Optional<IIngredientTypeWithSubtypes<B, I>> getIngredientTypeWithSubtypesFromBase(B baseIngredient) {
        Class<?> baseIngredientClass = baseIngredient.getClass();
        IIngredientTypeWithSubtypes<?, ?> ingredientType = this.baseClassToType.get(baseIngredientClass);
        if (ingredientType != null) {
            return Optional.of(ingredientType);
        }
        for (IIngredientType<?> type : this.orderedTypes) {
            IIngredientTypeWithSubtypes typeWithSubtypes;
            if (!(type instanceof IIngredientTypeWithSubtypes) || !(typeWithSubtypes = (IIngredientTypeWithSubtypes)type).getIngredientBaseClass().isInstance(baseIngredient)) continue;
            IIngredientTypeWithSubtypes castType = typeWithSubtypes;
            this.baseClassToType.put(baseIngredientClass, castType);
            return Optional.of(castType);
        }
        return Optional.empty();
    }
}

