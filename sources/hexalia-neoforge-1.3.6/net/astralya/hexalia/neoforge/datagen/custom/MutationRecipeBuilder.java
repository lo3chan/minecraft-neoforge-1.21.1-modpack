package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public final class MutationRecipeBuilder implements RecipeBuilder {
   private final Ingredient input;
   private final ItemStack output;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

   private MutationRecipeBuilder(Ingredient input, ItemStack output) {
      this.input = input;
      this.output = output;
   }

   public static MutationRecipeBuilder mutation(Ingredient input, ItemStack output) {
      return new MutationRecipeBuilder(input, output);
   }

   public MutationRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      this.criteria.put(name, criterion);
      return this;
   }

   public RecipeBuilder group(@Nullable String groupName) {
      return this;
   }

   public Item getResult() {
      return this.output.getItem();
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      Builder advancement = recipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement::addCriterion);
      recipeOutput.accept(id, new MutationRecipe(this.input, this.output), advancement.build(id.withPrefix("recipes/mutation/")));
   }
}
