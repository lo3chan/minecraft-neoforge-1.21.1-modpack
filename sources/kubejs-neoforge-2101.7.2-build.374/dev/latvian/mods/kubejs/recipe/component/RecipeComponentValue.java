package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.error.InvalidRecipeComponentException;
import dev.latvian.mods.kubejs.error.MissingRequiredValueException;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.WrappedJS;
import java.util.Objects;
import java.util.Map.Entry;

public final class RecipeComponentValue<T> implements WrappedJS, Entry<RecipeKey<T>, T> {
   public static final RecipeComponentValue<?>[] EMPTY_ARRAY = new RecipeComponentValue[0];
   public final RecipeKey<T> key;
   public final int index;
   public T value;
   public boolean write;

   public RecipeComponentValue(RecipeKey<T> key, int index) {
      this.key = key;
      this.index = index;
      this.value = null;
      this.write = false;
   }

   public RecipeComponentValue<T> copy() {
      RecipeComponentValue<T> copy = new RecipeComponentValue<>(this.key, this.index);
      copy.value = this.value;
      copy.write = this.write;
      return copy;
   }

   public boolean matches(RecipeMatchContext cx, ReplacementMatchInfo match) {
      return this.value != null
         && (match.componentType().isEmpty() || this.key.component.equals(match.componentType().get()))
         && this.key.component.matches(cx, this.value, match);
   }

   public boolean replace(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
      T newValue = this.value == null ? null : this.key.component.replace(cx, this.value, match, with);
      if (this.value != newValue) {
         this.value = newValue;
         this.write();
         return true;
      } else {
         return false;
      }
   }

   public RecipeKey<T> getKey() {
      return this.key;
   }

   public int getIndex() {
      return this.index;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public T setValue(T newValue) {
      T v = this.value;
      this.value = newValue;
      return v;
   }

   public boolean shouldWrite() {
      return this.write;
   }

   public void write() {
      this.write = true;
   }

   @Override
   public String toString() {
      return "%s = %s".formatted(this.key.name, this.value);
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof Entry<?, ?> e && this.key == e.getKey() && Objects.equals(this.value, e.getValue());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.key, this.value);
   }

   public void validate(RecipeValidationContext ctx, SourceLine sourceLine) {
      if (this.value != null) {
         try {
            this.key.component.validate(ctx, this.value);
         } catch (Throwable var4) {
            throw new InvalidRecipeComponentException(this, var4).source(sourceLine);
         }
      } else if (!this.key.optional()) {
         throw new InvalidRecipeComponentException(this, new MissingRequiredValueException()).source(sourceLine);
      }
   }
}
