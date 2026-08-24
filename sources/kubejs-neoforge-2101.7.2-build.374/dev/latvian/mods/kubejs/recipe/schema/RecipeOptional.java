package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.util.Cast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@FunctionalInterface
public interface RecipeOptional<T> {
   RecipeOptional<?> DEFAULT = new RecipeOptional.Unit<Object>() {
      @Nullable
      @Override
      public Object value() {
         return null;
      }

      @Override
      public String toString() {
         return "null";
      }
   };

   T getDefaultValue(RecipeSchemaType type);

   @Nullable
   default T getInformativeValue() {
      return null;
   }

   default boolean isDefault() {
      return this == DEFAULT;
   }

   static <T> RecipeOptional<T> unit(@Nullable T value) {
      return (RecipeOptional<T>)(value == null ? Cast.to(DEFAULT) : new RecipeOptional.Unit.Impl<>(value));
   }

   /** @deprecated */
   @Internal
   public record Constant<T>(T value) implements RecipeOptional.Unit<T> {
      @NotNull
      @Override
      public String toString() {
         return String.valueOf(this.value);
      }
   }

   public interface Unit<T> extends RecipeOptional<T> {
      @Nullable
      T value();

      @Override
      default T getDefaultValue(RecipeSchemaType type) {
         return this.value();
      }

      @Nullable
      @Override
      default T getInformativeValue() {
         return this.value();
      }

      @Internal
      public record Impl<T>(T value) implements RecipeOptional.Unit<T> {
      }
   }
}
