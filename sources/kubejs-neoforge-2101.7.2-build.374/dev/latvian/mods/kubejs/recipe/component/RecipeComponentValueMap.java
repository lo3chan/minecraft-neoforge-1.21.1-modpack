package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Wrapper;
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class RecipeComponentValueMap extends AbstractMap<RecipeKey<?>, Object> {
   public static final RecipeComponentValueMap EMPTY = new RecipeComponentValueMap(RecipeComponentValue.EMPTY_ARRAY);
   public final RecipeComponentValue<?>[] holders;
   private Set<Entry<RecipeKey<?>, Object>> holderSet;

   public RecipeComponentValueMap(RecipeComponentValue<?>[] holders) {
      this.holders = new RecipeComponentValue[holders.length];

      for (int i = 0; i < holders.length; i++) {
         this.holders[i] = holders[i].copy();
      }
   }

   public RecipeComponentValueMap(List<RecipeKey<?>> keys) {
      this.holders = new RecipeComponentValue[keys.size()];

      for (int i = 0; i < this.holders.length; i++) {
         this.holders[i] = new RecipeComponentValue<>(keys.get(i), i);
      }
   }

   @NotNull
   @Override
   public Set<Entry<RecipeKey<?>, Object>> entrySet() {
      if (this.holderSet == null) {
         this.holderSet = Cast.to(Set.of(this.holders));
      }

      return this.holderSet;
   }

   public Object put(RecipeKey<?> key, Object value) {
      for (RecipeComponentValue<?> holder : this.holders) {
         if (holder.key == key) {
            return ((RecipeComponentValue<Object>)holder).setValue(Cast.to(Wrapper.unwrapped(value)));
         }
      }

      throw new IllegalArgumentException("Key " + key + " is not in this map!");
   }

   public RecipeComponentValue<?> getHolder(Object key) {
      for (RecipeComponentValue<?> holder : this.holders) {
         if (holder.key == key) {
            return holder;
         }
      }

      return null;
   }

   @Override
   public Object get(Object key) {
      RecipeComponentValue<?> h = this.getHolder(key);
      return h == null ? null : h.value;
   }

   @Override
   public Object getOrDefault(Object key, Object defaultValue) {
      Object v = this.get(key);
      return v == null ? defaultValue : v;
   }

   @Override
   public int hashCode() {
      int i = 1;

      for (RecipeComponentValue<?> holder : this.holders) {
         i = 31 * i + holder.key.hashCode();
         i = 31 * i + Objects.hashCode(holder.value);
      }

      return i;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RecipeComponentValueMap map)) {
         return false;
      } else if (this.holders.length != map.holders.length) {
         return false;
      } else {
         for (int i = 0; i < this.holders.length; i++) {
            if (this.holders[i].key != map.holders[i].key || !Objects.equals(this.holders[i].value, map.holders[i].value)) {
               return false;
            }
         }

         return true;
      }
   }
}
