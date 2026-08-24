package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class SmallCauldronRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final NonNullList<Ingredient> ingredients = NonNullList.create();
   private final Item result;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   private String group;
   private float experience;
   private int brewTime = 200;

   private SmallCauldronRecipeBuilder(RecipeCategory category, Ingredient firstIngredient, ItemLike result) {
      this.category = category;
      this.result = result.asItem();
      this.ingredients.add(firstIngredient);
   }

   public static SmallCauldronRecipeBuilder brew(RecipeCategory category, Ingredient firstIngredient, ItemLike result) {
      return new SmallCauldronRecipeBuilder(category, firstIngredient, result);
   }

   public SmallCauldronRecipeBuilder requiresIngredient(Ingredient ingredient) {
      this.ingredients.add(ingredient);
      return this;
   }

   public SmallCauldronRecipeBuilder experience(float recipeExperience) {
      this.experience = recipeExperience;
      return this;
   }

   public SmallCauldronRecipeBuilder brewTime(int ticks) {
      this.brewTime = ticks;
      return this;
   }

   public SmallCauldronRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      this.criteria.put(name, criterion);
      return this;
   }

   public SmallCauldronRecipeBuilder group(String recipeGroup) {
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
      NonNullList<Ingredient> recipeIngredients = NonNullList.create();
      recipeIngredients.addAll(this.ingredients);
      recipeOutput.accept(
         recipeId,
         new SmallCauldronRecipe(recipeIngredients, new ItemStack(this.result), this.experience, this.brewTime),
         advancement.build(recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"))
      );
   }

   private void ensureValid(ResourceLocation recipeId) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + recipeId);
      } else if (this.ingredients.isEmpty()) {
         throw new IllegalStateException("Small Cauldron recipe requires at least one ingredient");
      }
   }
}
