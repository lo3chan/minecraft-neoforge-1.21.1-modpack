package mezz.jei.api.registration;

import com.mojang.serialization.Codec;
import java.util.Collection;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;

public interface IModIngredientRegistration {
   ISubtypeManager getSubtypeManager();

   IColorHelper getColorHelper();

   <V> void register(IIngredientType<V> var1, Collection<V> var2, IIngredientHelper<V> var3, IIngredientRenderer<V> var4, Codec<V> var5);

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   <V> void register(IIngredientType<V> var1, Collection<V> var2, IIngredientHelper<V> var3, IIngredientRenderer<V> var4);
}
