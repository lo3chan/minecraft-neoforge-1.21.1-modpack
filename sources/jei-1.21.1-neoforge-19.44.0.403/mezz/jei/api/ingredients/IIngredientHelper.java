package mezz.jei.api.ingredients;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.constants.Tags;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IIngredientHelper<V> {
   IIngredientType<V> getIngredientType();

   String getDisplayName(V var1);

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   String getUniqueId(V var1, UidContext var2);

   default Object getUid(V ingredient, UidContext context) {
      return this.getUniqueId(ingredient, context);
   }

   default Object getUid(ITypedIngredient<V> typedIngredient, UidContext context) {
      return this.getUid(typedIngredient.getIngredient(), context);
   }

   default Object getGroupingUid(V ingredient) {
      return this.getWildcardId(ingredient);
   }

   default Object getGroupingUid(ITypedIngredient<V> typedIngredient) {
      return this.getGroupingUid(typedIngredient.getIngredient());
   }

   default boolean hasSubtypes(V ingredient) {
      return this.getIngredientType() instanceof IIngredientTypeWithSubtypes;
   }

   @Deprecated(
      since = "19.13.0",
      forRemoval = true
   )
   default String getWildcardId(V ingredient) {
      return this.getUniqueId(ingredient, UidContext.Ingredient);
   }

   default String getDisplayModId(V ingredient) {
      return this.getResourceLocation(ingredient).getNamespace();
   }

   default long getAmount(V ingredient) {
      return -1L;
   }

   default V copyWithAmount(V ingredient, long amount) {
      return this.copyIngredient(ingredient);
   }

   default Iterable<Integer> getColors(V ingredient) {
      return Collections.emptyList();
   }

   ResourceLocation getResourceLocation(V var1);

   default ItemStack getCheatItemStack(V ingredient) {
      return ItemStack.EMPTY;
   }

   V copyIngredient(V var1);

   default V normalizeIngredient(V ingredient) {
      return ingredient;
   }

   default boolean isValidIngredient(V ingredient) {
      return true;
   }

   default boolean isIngredientOnServer(V ingredient) {
      return true;
   }

   default Stream<ResourceLocation> getTagStream(V ingredient) {
      return Stream.empty();
   }

   default boolean isHiddenFromRecipeViewersByTags(V ingredient) {
      return this.getTagStream(ingredient).anyMatch(Tags.HIDDEN_FROM_RECIPE_VIEWERS::equals);
   }

   default boolean isHiddenFromRecipeViewersByTags(ITypedIngredient<V> ingredient) {
      return this.isHiddenFromRecipeViewersByTags(ingredient.getIngredient());
   }

   String getErrorInfo(@Nullable V var1);

   default Optional<TagKey<?>> getTagKeyEquivalent(Collection<V> ingredients) {
      return Optional.empty();
   }

   @Deprecated(
      since = "19.5.5",
      forRemoval = true
   )
   default Optional<ResourceLocation> getTagEquivalent(Collection<V> ingredients) {
      return this.getTagKeyEquivalent(ingredients).map(TagKey::location);
   }
}
