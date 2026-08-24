package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface RecipeComponentWithParent<T> extends RecipeComponent<T> {
   RecipeComponent<T> parentComponent();

   @Override
   default Codec<T> codec() {
      return this.parentComponent().codec();
   }

   @Override
   default TypeInfo typeInfo() {
      return this.parentComponent().typeInfo();
   }

   @Override
   default T wrap(RecipeScriptContext cx, Object from) {
      return this.parentComponent().wrap(cx, from);
   }

   @Override
   default boolean hasPriority(RecipeMatchContext cx, Object from) {
      return this.parentComponent().hasPriority(cx, from);
   }

   @Override
   default boolean matches(RecipeMatchContext cx, T value, ReplacementMatchInfo match) {
      return this.parentComponent().matches(cx, value, match);
   }

   @Override
   default T replace(RecipeScriptContext cx, T original, ReplacementMatchInfo match, Object with) {
      return this.parentComponent().replace(cx, original, match, with);
   }

   @Override
   default boolean allowEmpty() {
      return this.parentComponent().allowEmpty();
   }

   @Override
   default void validate(RecipeValidationContext ctx, T value) {
      this.parentComponent().validate(ctx, value);
   }

   @Override
   default boolean isEmpty(T value) {
      return this.parentComponent().isEmpty(value);
   }

   @Override
   default void buildUniqueId(UniqueIdBuilder builder, T value) {
      this.parentComponent().buildUniqueId(builder, value);
   }

   @Nullable
   @Override
   default RecipeComponentBuilder createBuilder() {
      return this.parentComponent().createBuilder();
   }

   @Override
   default List<?> spread(T value) {
      return this.parentComponent().spread(value);
   }
}
