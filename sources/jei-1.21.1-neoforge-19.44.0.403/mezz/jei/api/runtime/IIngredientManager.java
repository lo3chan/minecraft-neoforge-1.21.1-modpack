package mezz.jei.api.runtime;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface IIngredientManager {
   @Unmodifiable
   default Collection<ItemStack> getAllItemStacks() {
      return this.getAllIngredients(VanillaTypes.ITEM_STACK);
   }

   @Unmodifiable
   <V> Collection<V> getAllIngredients(IIngredientType<V> var1);

   @Unmodifiable
   <V> Collection<ITypedIngredient<V>> getAllTypedIngredients(IIngredientType<V> var1);

   <V> IIngredientHelper<V> getIngredientHelper(V var1);

   <V> IIngredientHelper<V> getIngredientHelper(IIngredientType<V> var1);

   <V> IIngredientRenderer<V> getIngredientRenderer(V var1);

   <V> IIngredientRenderer<V> getIngredientRenderer(IIngredientType<V> var1);

   <V> Codec<V> getIngredientCodec(IIngredientType<V> var1);

   @Unmodifiable
   Collection<IIngredientType<?>> getRegisteredIngredientTypes();

   Optional<IIngredientType<?>> getIngredientTypeForUid(String var1);

   <V> void addIngredientsAtRuntime(IIngredientType<V> var1, Collection<V> var2);

   <V> void removeIngredientsAtRuntime(IIngredientType<V> var1, Collection<V> var2);

   @Nullable
   <V> IIngredientType<V> getIngredientType(V var1);

   <V> Optional<IIngredientType<V>> getIngredientTypeChecked(V var1);

   <B, I> Optional<IIngredientTypeWithSubtypes<B, I>> getIngredientTypeWithSubtypesFromBase(B var1);

   <V> Optional<IIngredientType<V>> getIngredientTypeChecked(Class<? extends V> var1);

   <V> Optional<ITypedIngredient<V>> createTypedIngredient(IIngredientType<V> var1, V var2, boolean var3);

   default <T> Optional<ITypedIngredient<T>> createTypedIngredient(T ingredient, boolean normalize) {
      return this.getIngredientTypeChecked(ingredient)
         .flatMap(ingredientType -> this.createTypedIngredient((IIngredientType<T>)ingredientType, ingredient, normalize));
   }

   @Deprecated(
      forRemoval = true,
      since = "19.23.0"
   )
   default <V> Optional<ITypedIngredient<V>> createTypedIngredient(IIngredientType<V> ingredientType, V ingredient) {
      return this.createTypedIngredient(ingredientType, ingredient, false);
   }

   @Deprecated(
      forRemoval = true,
      since = "19.23.0"
   )
   default <V> Optional<ITypedIngredient<V>> createTypedIngredient(V ingredient) {
      return this.createTypedIngredient(ingredient, false);
   }

   <V> ITypedIngredient<V> normalizeTypedIngredient(ITypedIngredient<V> var1);

   IClickableIngredientFactory getClickableIngredientFactory();

   @Deprecated(
      forRemoval = true,
      since = "19.23.0"
   )
   <V> Optional<IClickableIngredient<V>> createClickableIngredient(IIngredientType<V> var1, V var2, Rect2i var3, boolean var4);

   @Deprecated(
      forRemoval = true,
      since = "19.23.0"
   )
   default <V> Optional<IClickableIngredient<V>> createClickableIngredient(V ingredient, Rect2i area, boolean normalize) {
      return this.getIngredientTypeChecked(ingredient).flatMap(type -> this.createClickableIngredient((IIngredientType<V>)type, ingredient, area, normalize));
   }

   @Deprecated(
      since = "19.1.0",
      forRemoval = true
   )
   <V> Optional<V> getIngredientByUid(IIngredientType<V> var1, String var2);

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   <V> Optional<ITypedIngredient<V>> getTypedIngredientByUid(IIngredientType<V> var1, String var2);

   Collection<String> getIngredientAliases(ITypedIngredient<?> var1);

   void registerIngredientListener(IIngredientManager.IIngredientListener var1);

   public interface IIngredientListener {
      <V> void onIngredientsAdded(IIngredientHelper<V> var1, Collection<ITypedIngredient<V>> var2);

      <V> void onIngredientsRemoved(IIngredientHelper<V> var1, Collection<ITypedIngredient<V>> var2);
   }
}
