package com.aetherteam.aether.recipe.builder;

import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class AltarRepairBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Ingredient ingredient;
   private final int repairTime;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;

   private AltarRepairBuilder(RecipeCategory category, Ingredient ingredient, int repairTime) {
      this.category = category;
      this.ingredient = ingredient;
      this.repairTime = repairTime;
   }

   public static AltarRepairBuilder repair(Ingredient item, RecipeCategory category, int repairTime) {
      return new AltarRepairBuilder(category, item, repairTime);
   }

   public AltarRepairBuilder unlockedBy(String name, Criterion<?> criterion) {
      this.criteria.put(name, criterion);
      return this;
   }

   public RecipeBuilder group(@Nullable String group) {
      this.group = group;
      return this;
   }

   public Item getResult() {
      return this.ingredient.getItems()[0].getItem();
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      this.ensureValid(id);
      Builder advancement$builder = recipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement$builder::addCriterion);
      AltarRepairRecipe recipe = new AltarRepairRecipe(this.group == null ? "" : this.group, this.ingredient, this.repairTime);
      recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceLocation id) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + id);
      }
   }
}
