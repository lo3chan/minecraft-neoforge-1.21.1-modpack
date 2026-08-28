/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import org.jetbrains.annotations.Nullable;

public class TypedIngredientCodecs {
    private static final Map<IIngredientType<?>, Codec<ITypedIngredient<?>>> codecMapCache = new HashMap();
    @Nullable
    private static Codec<IIngredientType<?>> ingredientTypeCodec;
    @Nullable
    private static MapCodec<ITypedIngredient<?>> ingredientCodec;

    public static Codec<IIngredientType<?>> getIngredientTypeCodec(IIngredientManager ingredientManager) {
        if (ingredientTypeCodec == null) {
            ingredientTypeCodec = Codec.STRING.flatXmap(uid -> ingredientManager.getIngredientTypeForUid((String)uid).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Failed to find ingredient type for uid: " + uid)), ingredientType -> {
                String uid = ingredientType.getUid();
                return DataResult.success((Object)uid);
            });
        }
        return ingredientTypeCodec;
    }

    public static MapCodec<ITypedIngredient<?>> getIngredientCodec(IIngredientManager ingredientManager) {
        if (ingredientCodec == null) {
            Codec<IIngredientType<?>> ingredientTypeCodec = TypedIngredientCodecs.getIngredientTypeCodec(ingredientManager);
            ingredientCodec = ingredientTypeCodec.dispatchMap(ITypedIngredient::getType, type -> TypedIngredientCodecs.getIngredientCodec(type, ingredientManager).fieldOf("ingredient"));
        }
        return ingredientCodec;
    }

    public static <T> Codec<ITypedIngredient<T>> getIngredientCodec(IIngredientType<T> ingredientType, IIngredientManager ingredientManager) {
        Object result = codecMapCache.get(ingredientType);
        if (result == null) {
            Codec<T> codec = ingredientManager.getIngredientCodec(ingredientType);
            result = TypedIngredientCodecs.create(codec, ingredientManager);
            codecMapCache.put(ingredientType, (Codec<ITypedIngredient<?>>)result);
        }
        return result;
    }

    private static <T> Codec<ITypedIngredient<T>> create(Codec<T> ingredientCodec, IIngredientManager ingredientManager) {
        return ingredientCodec.flatXmap(ingredient -> {
            Optional<IIngredientType<Object>> type = ingredientManager.getIngredientTypeChecked(ingredient);
            return type.map(ingredientType -> ingredientManager.createTypedIngredient(ingredientType, ingredient, false).map(DataResult::success).orElseGet(() -> DataResult.error(() -> {
                IIngredientHelper<Object> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
                String errorInfo = ingredientHelper.getErrorInfo(ingredient);
                return "Failed to create typed ingredient: " + errorInfo;
            }))).orElseGet(() -> DataResult.error(() -> "Failed to find type for ingredient: " + String.valueOf(ingredient.getClass())));
        }, typedIngredient -> {
            Object ingredient = typedIngredient.getIngredient();
            return DataResult.success(ingredient);
        });
    }
}

