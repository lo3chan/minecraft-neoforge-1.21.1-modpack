package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class CelestialInfusionRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Ingredient input;
   private final Item result;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   private String group;

   private CelestialInfusionRecipeBuilder(RecipeCategory category, Ingredient input, ItemLike result) {
      this.category = category;
      this.input = input;
      this.result = result.asItem();
   }

   public static CelestialInfusionRecipeBuilder infusion(RecipeCategory category, Ingredient input, ItemLike result) {
      return new CelestialInfusionRecipeBuilder(category, input, result);
   }

   public CelestialInfusionRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      this.criteria.put(name, criterion);
      return this;
   }

   public CelestialInfusionRecipeBuilder group(String recipeGroup) {
      this.group = recipeGroup;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
      this.ensureValid(recipeId);
      Builder advancement = recipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(recipeId));
      this.criteria.forEach(advancement::addCriterion);
      recipeOutput.accept(
         recipeId,
         new CelestialInfusionRecipe(this.input, new ItemStack(this.result)),
         advancement.build(recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"))
      );
   }

   private void ensureValid(ResourceLocation recipeId) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + recipeId);
      }
   }
}
