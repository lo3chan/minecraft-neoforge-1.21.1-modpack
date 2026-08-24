package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public final class MortarAndPestleRecipeBuilder implements RecipeBuilder {
   private final NonNullList<Ingredient> inputs = NonNullList.create();
   private final ItemStack output;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

   private MortarAndPestleRecipeBuilder(ItemStack output) {
      this.output = output;
   }

   public static MortarAndPestleRecipeBuilder mortar(Ingredient input, ItemStack output) {
      MortarAndPestleRecipeBuilder builder = new MortarAndPestleRecipeBuilder(output);
      builder.inputs.add(input);
      return builder;
   }

   public MortarAndPestleRecipeBuilder requires(Ingredient input) {
      this.inputs.add(input);
      return this;
   }

   public MortarAndPestleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      this.criteria.put(name, criterion);
      return this;
   }

   public RecipeBuilder group(@Nullable String groupName) {
      return this;
   }

   public Item getResult() {
      return this.output.getItem();
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + recipeId);
      } else if (!this.inputs.isEmpty() && this.inputs.size() <= 3) {
         Builder advancement = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(recipeId))
            .requirements(Strategy.OR);
         this.criteria.forEach(advancement::addCriterion);
         NonNullList<Ingredient> recipeInputs = NonNullList.create();
         recipeInputs.addAll(this.inputs);
         recipeOutput.accept(
            recipeId, new MortarAndPestleRecipe(recipeInputs, this.output), advancement.build(recipeId.withPrefix("recipes/mortar_and_pestle/"))
         );
      } else {
         throw new IllegalStateException("Mortar and Pestle recipes require 1 to 3 ingredients");
      }
   }
}
