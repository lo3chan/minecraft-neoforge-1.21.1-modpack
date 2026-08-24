package mezz.jei.common.config.file.serializers;

import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;

@Deprecated
public class LegacyTypedIngredientSerializer {
   private static final String SEPARATOR = "&";
   private final IIngredientManager ingredientManager;

   public LegacyTypedIngredientSerializer(IIngredientManager ingredientManager) {
      this.ingredientManager = ingredientManager;
   }

   public IJeiConfigValueSerializer.IDeserializeResult<ITypedIngredient<?>> deserialize(String string) {
      String[] parts = string.split("&");
      if (parts.length != 2) {
         String error = "string must be two uids, separated by '&': " + string;
         return new DeserializeResult<>(null, error);
      } else {
         String typeUid = parts[0];
         String uid = parts[1];
         Optional<IIngredientType<?>> ingredientTypeForUid = this.ingredientManager.getIngredientTypeForUid(typeUid);
         if (ingredientTypeForUid.isEmpty()) {
            String error = "no ingredient type was found for uid: " + typeUid;
            return new DeserializeResult<>(null, error);
         } else {
            IIngredientType<?> ingredientType = ingredientTypeForUid.get();
            Optional<? extends ITypedIngredient<?>> ingredient = this.ingredientManager.getTypedIngredientByUid(ingredientType, uid);
            if (ingredient.isEmpty()) {
               String error = "no ingredient was found for uid: " + uid;
               return new DeserializeResult<>(null, error);
            } else {
               return new DeserializeResult<>((ITypedIngredient<?>)ingredient.get());
            }
         }
      }
   }
}
