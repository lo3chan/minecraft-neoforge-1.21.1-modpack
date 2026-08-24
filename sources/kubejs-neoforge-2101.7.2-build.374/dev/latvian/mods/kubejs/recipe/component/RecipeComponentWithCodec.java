package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;

public record RecipeComponentWithCodec<T>(RecipeComponent<T> parent, Codec<T> codec) implements RecipeComponentWithParent<T> {
   @Override
   public RecipeComponentType<?> type() {
      return this.parent.type();
   }

   @Override
   public RecipeComponent<T> parentComponent() {
      return this.parent;
   }

   @Override
   public String toString() {
      return this.parent + "{custom_codec}";
   }
}
