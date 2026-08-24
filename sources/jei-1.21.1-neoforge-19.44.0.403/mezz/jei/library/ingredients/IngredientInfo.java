package mezz.jei.library.ingredients;

import com.google.common.collect.Collections2;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.collect.ListMultiMap;
import mezz.jei.library.load.registration.LegacyUidCodec;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class IngredientInfo<T> {
   private final IIngredientType<T> ingredientType;
   private final IIngredientHelper<T> ingredientHelper;
   private final IIngredientRenderer<T> ingredientRenderer;
   private final Codec<T> ingredientCodec;
   private final TypedIngredientSet<T> ingredientSet;
   private final ListMultiMap<Object, String> aliases;
   private final ListMultiMap<Object, String> baseAliases;

   public IngredientInfo(
      IIngredientType<T> ingredientType,
      Collection<ITypedIngredient<T>> ingredients,
      IIngredientHelper<T> ingredientHelper,
      IIngredientRenderer<T> ingredientRenderer,
      @Nullable Codec<T> ingredientCodec
   ) {
      if (ingredientCodec == null) {
         ingredientCodec = LegacyUidCodec.create(this);
      }

      this.ingredientType = ingredientType;
      this.ingredientHelper = ingredientHelper;
      this.ingredientRenderer = ingredientRenderer;
      this.ingredientCodec = ingredientCodec;
      this.ingredientSet = new TypedIngredientSet<>(ingredientHelper, UidContext.Ingredient);
      this.ingredientSet.addAll(ingredients);
      this.aliases = new ListMultiMap<>();
      this.baseAliases = new ListMultiMap<>(new IdentityHashMap<>(), ArrayList::new);
   }

   public IIngredientType<T> getIngredientType() {
      return this.ingredientType;
   }

   public IIngredientHelper<T> getIngredientHelper() {
      return this.ingredientHelper;
   }

   public IIngredientRenderer<T> getIngredientRenderer() {
      return this.ingredientRenderer;
   }

   public Codec<T> getIngredientCodec() {
      return this.ingredientCodec;
   }

   @Unmodifiable
   public Collection<ITypedIngredient<T>> getAllTypedIngredients() {
      return Collections.unmodifiableCollection(this.ingredientSet);
   }

   @Unmodifiable
   public Collection<T> getAllIngredients() {
      Collection<T> transform = Collections2.transform(this.ingredientSet, ITypedIngredient::getIngredient);
      return Collections.unmodifiableCollection(transform);
   }

   public void addIngredients(Collection<ITypedIngredient<T>> ingredients) {
      this.ingredientSet.addAll(ingredients);
   }

   public void removeIngredients(Collection<ITypedIngredient<T>> ingredients) {
      this.ingredientSet.removeAll(ingredients);
   }

   @Deprecated(
      forRemoval = true
   )
   public Optional<T> getIngredientByLegacyUid(String uid) {
      return this.ingredientSet.getByLegacyUid(uid).map(ITypedIngredient::getIngredient);
   }

   @Unmodifiable
   public Collection<String> getIngredientAliases(ITypedIngredient<T> ingredient) {
      Object uid = this.ingredientHelper.getUid(ingredient, UidContext.Ingredient);
      Collection<String> ingredientAliases = this.aliases.get(uid);
      Collection<String> baseIngredientAliases = this.getBaseIngredientAliases(ingredient);
      if (ingredientAliases.isEmpty()) {
         return baseIngredientAliases;
      } else if (baseIngredientAliases.isEmpty()) {
         return ingredientAliases;
      } else {
         List<String> combinedAliases = new ArrayList<>(ingredientAliases.size() + baseIngredientAliases.size());
         combinedAliases.addAll(ingredientAliases);
         combinedAliases.addAll(baseIngredientAliases);
         return Collections.unmodifiableList(combinedAliases);
      }
   }

   public void addIngredientAlias(T ingredient, String alias) {
      Object uid = this.ingredientHelper.getUid(ingredient, UidContext.Ingredient);
      this.aliases.put(uid, alias);
   }

   public void addIngredientAlias(ITypedIngredient<T> ingredient, String alias) {
      Object uid = this.ingredientHelper.getUid(ingredient, UidContext.Ingredient);
      this.aliases.put(uid, alias);
   }

   public void addIngredientAliases(T ingredient, Collection<String> aliases) {
      Object uid = this.ingredientHelper.getUid(ingredient, UidContext.Ingredient);
      this.aliases.putAll(uid, aliases);
   }

   public void addIngredientAliases(ITypedIngredient<T> ingredient, Collection<String> aliases) {
      Object uid = this.ingredientHelper.getUid(ingredient, UidContext.Ingredient);
      this.aliases.putAll(uid, aliases);
   }

   public void addBaseIngredientAlias(Object baseIngredient, String alias) {
      this.baseAliases.put(baseIngredient, alias);
   }

   public void addBaseIngredientAliases(Object baseIngredient, Collection<String> aliases) {
      this.baseAliases.putAll(baseIngredient, aliases);
   }

   @Unmodifiable
   private Collection<String> getBaseIngredientAliases(ITypedIngredient<T> ingredient) {
      if (this.ingredientType instanceof IIngredientTypeWithSubtypes<?, T> ingredientTypeWithSubtypes) {
         Object baseIngredient = ingredient.getBaseIngredient(ingredientTypeWithSubtypes);
         return this.baseAliases.get(baseIngredient);
      } else {
         return Collections.emptyList();
      }
   }
}
