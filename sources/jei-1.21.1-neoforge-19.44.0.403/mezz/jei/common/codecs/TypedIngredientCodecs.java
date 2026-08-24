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
   private static final Map<IIngredientType<?>, Codec<ITypedIngredient<?>>> codecMapCache = new HashMap<>();
   @Nullable
   private static Codec<IIngredientType<?>> ingredientTypeCodec;
   @Nullable
   private static MapCodec<ITypedIngredient<?>> ingredientCodec;

   public static Codec<IIngredientType<?>> getIngredientTypeCodec(IIngredientManager ingredientManager) {
      if (ingredientTypeCodec == null) {
         ingredientTypeCodec = Codec.STRING
            .flatXmap(
               uid -> ingredientManager.getIngredientTypeForUid(uid)
                  .<DataResult>map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Failed to find ingredient type for uid: " + uid)),
               ingredientType -> {
                  String uid = ingredientType.getUid();
                  return DataResult.success(uid);
               }
            );
      }

      return ingredientTypeCodec;
   }

   public static MapCodec<ITypedIngredient<?>> getIngredientCodec(IIngredientManager ingredientManager) {
      if (ingredientCodec == null) {
         Codec<IIngredientType<?>> ingredientTypeCodec = getIngredientTypeCodec(ingredientManager);
         ingredientCodec = ingredientTypeCodec.dispatchMap(ITypedIngredient::getType, type -> getIngredientCodec(type, ingredientManager).fieldOf("ingredient"));
      }

      return ingredientCodec;
   }

   public static <T> Codec<ITypedIngredient<T>> getIngredientCodec(IIngredientType<T> ingredientType, IIngredientManager ingredientManager) {
      Codec<ITypedIngredient<T>> result = (Codec<ITypedIngredient<T>>)codecMapCache.get(ingredientType);
      if (result == null) {
         Codec<T> codec = ingredientManager.getIngredientCodec(ingredientType);
         result = create(codec, ingredientManager);
         codecMapCache.put(ingredientType, result);
      }

      return result;
   }

   private static <T> Codec<ITypedIngredient<T>> create(Codec<T> ingredientCodec, IIngredientManager ingredientManager) {
      return ingredientCodec.flatXmap(
         ingredient -> {
            Optional<IIngredientType<T>> type = ingredientManager.getIngredientTypeChecked((T)ingredient);
            return type.<DataResult>map(
                  ingredientType -> ingredientManager.createTypedIngredient((IIngredientType<T>)ingredientType, (T)ingredient, false)
                     .map(DataResult::success)
                     .orElseGet(() -> DataResult.error(() -> {
                        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
                        String errorInfo = ingredientHelper.getErrorInfo((T)ingredient);
                        return "Failed to create typed ingredient: " + errorInfo;
                     }))
               )
               .orElseGet(() -> DataResult.error(() -> "Failed to find type for ingredient: " + ingredient.getClass()));
         },
         typedIngredient -> {
            T ingredient = (T)typedIngredient.getIngredient();
            return DataResult.success(ingredient);
         }
      );
   }
}
