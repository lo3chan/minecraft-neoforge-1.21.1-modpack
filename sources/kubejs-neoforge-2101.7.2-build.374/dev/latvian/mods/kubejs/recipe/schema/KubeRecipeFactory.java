package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public record KubeRecipeFactory(ResourceLocation id, TypeInfo recipeType, Supplier<? extends KubeRecipe> factory) {
   public static final KubeRecipeFactory DEFAULT = new KubeRecipeFactory(KubeJS.id("basic"), TypeInfo.of(KubeRecipe.class), KubeRecipe::new);

   public KubeRecipeFactory(ResourceLocation id, Class<?> recipeType, Supplier<? extends KubeRecipe> factory) {
      this(id, TypeInfo.of(recipeType), factory);
   }

   public KubeRecipe create(RecipeTypeFunction type, SourceLine sourceLine, boolean save) {
      KubeRecipe r = this.factory.get();
      r.sourceLine = sourceLine;
      r.type = type;
      r.initValues(save);
      return r;
   }
}
