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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class RegisteredIngredients {
   @Unmodifiable
   private final List<IIngredientType<?>> orderedTypes;
   @Unmodifiable
   private final Map<IIngredientType<?>, IngredientInfo<?>> typeToInfo;
   private final Map<Class<?>, IIngredientType<?>> classToType;
   private final Map<Class<?>, IIngredientTypeWithSubtypes<?, ?>> baseClassToType;

   public RegisteredIngredients(SequencedMap<IIngredientType<?>, IngredientInfo<?>> ingredientInfoList) {
      this.orderedTypes = ingredientInfoList.sequencedValues().stream().map(IngredientInfo::getIngredientType).toList();
      this.typeToInfo = new Object2ObjectArrayMap(ingredientInfoList);
      this.classToType = this.orderedTypes.stream().collect(Collectors.toMap(IIngredientType::getIngredientClass, Function.identity()));
      this.baseClassToType = this.orderedTypes
         .stream()
         .filter(IIngredientTypeWithSubtypes.class::isInstance)
         .map(IIngredientTypeWithSubtypes.class::cast)
         .collect(Collectors.toMap(IIngredientTypeWithSubtypes::getIngredientBaseClass, Function.identity()));
   }

   public <V> IngredientInfo<V> getIngredientInfo(IIngredientType<V> ingredientType) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      IngredientInfo<V> ingredientInfo = (IngredientInfo<V>)this.typeToInfo.get(ingredientType);
      if (ingredientInfo == null) {
         throw new IllegalArgumentException("Unknown ingredient type: " + ingredientType.getIngredientClass());
      } else {
         return ingredientInfo;
      }
   }

   @Unmodifiable
   public List<IIngredientType<?>> getIngredientTypes() {
      return this.orderedTypes;
   }

   @Nullable
   public <V> IIngredientType<V> getIngredientType(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      Class<? extends V> ingredientClass = (Class<? extends V>)ingredient.getClass();
      return this.getIngredientType(ingredientClass);
   }

   @Nullable
   public <V> IIngredientType<V> getIngredientType(Class<? extends V> ingredientClass) {
      ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
      IIngredientType<V> ingredientType = (IIngredientType<V>)this.classToType.get(ingredientClass);
      if (ingredientType != null) {
         return ingredientType;
      } else {
         for (IIngredientType<?> type : this.orderedTypes) {
            if (type.getIngredientClass().isAssignableFrom(ingredientClass)) {
               this.classToType.put(ingredientClass, type);
               return (IIngredientType<V>)type;
            }
         }

         return null;
      }
   }

   public <I, B> Optional<IIngredientTypeWithSubtypes<B, I>> getIngredientTypeWithSubtypesFromBase(B baseIngredient) {
      Class<?> baseIngredientClass = baseIngredient.getClass();
      IIngredientTypeWithSubtypes<B, I> ingredientType = (IIngredientTypeWithSubtypes<B, I>)this.baseClassToType.get(baseIngredientClass);
      if (ingredientType != null) {
         return Optional.of(ingredientType);
      } else {
         for (IIngredientType<?> type : this.orderedTypes) {
            if (type instanceof IIngredientTypeWithSubtypes<?, ?> typeWithSubtypes && typeWithSubtypes.getIngredientBaseClass().isInstance(baseIngredient)) {
               this.baseClassToType.put(baseIngredientClass, typeWithSubtypes);
               return Optional.of((IIngredientTypeWithSubtypes<B, I>)typeWithSubtypes);
            }
         }

         return Optional.empty();
      }
   }
}
