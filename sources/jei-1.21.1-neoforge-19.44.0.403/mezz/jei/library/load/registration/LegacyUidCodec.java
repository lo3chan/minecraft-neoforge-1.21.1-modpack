package mezz.jei.library.load.registration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.library.ingredients.IngredientInfo;

@Deprecated
public class LegacyUidCodec {
   public static <T> Codec<T> create(IngredientInfo<T> ingredientInfo) {
      return Codec.STRING
         .flatXmap(
            uid -> ingredientInfo.getIngredientByLegacyUid(uid)
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "Failed to find ingredient with uid: " + uid)),
            ingredient -> {
               IIngredientHelper<T> ingredientHelper = ingredientInfo.getIngredientHelper();
               String uniqueId = ingredientHelper.getUniqueId((T)ingredient, UidContext.Ingredient);
               return DataResult.success(uniqueId);
            }
         );
   }
}
