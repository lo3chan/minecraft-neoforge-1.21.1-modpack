package fuzs.puzzleslib.api.data.v2.recipes;

import java.util.function.UnaryOperator;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public record TransformingRecipeOutput(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) implements RecipeOutput {
   public static RecipeOutput transformed(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) {
      return new TransformingRecipeOutput(recipeOutput, operator);
   }

   public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
      this.recipeOutput.accept(resourceLocation, this.operator().apply(recipe), advancement);
   }

   public Builder advancement() {
      return this.recipeOutput.advancement();
   }
}
