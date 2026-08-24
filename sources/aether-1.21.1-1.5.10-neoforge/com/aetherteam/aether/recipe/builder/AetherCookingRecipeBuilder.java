package com.aetherteam.aether.recipe.builder;

import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.serializer.AetherCookingSerializer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class AetherCookingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final AetherBookCategory bookCategory;
   private final ItemStack result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;
   private final AetherCookingSerializer.CookieBaker<?> factory;

   public AetherCookingRecipeBuilder(
      RecipeCategory category,
      AetherBookCategory bookCategory,
      ItemStack result,
      Ingredient ingredient,
      float experience,
      int cookingTime,
      AetherCookingSerializer.CookieBaker<?> factory
   ) {
      this.category = category;
      this.bookCategory = bookCategory;
      this.result = result;
      this.ingredient = ingredient;
      this.experience = experience;
      this.cookingTime = cookingTime;
      this.factory = factory;
   }

   public static AetherCookingRecipeBuilder generic(
      Ingredient ingredient,
      RecipeCategory category,
      ItemLike result,
      float experience,
      int cookingTime,
      AetherCookingSerializer<?> serializer,
      AetherCookingSerializer.CookieBaker<?> factory
   ) {
      return new AetherCookingRecipeBuilder(
         category,
         determineRecipeCategory((RecipeSerializer<? extends AbstractCookingRecipe>)serializer, category),
         new ItemStack(result),
         ingredient,
         experience,
         cookingTime,
         factory
      );
   }

   public AetherCookingRecipeBuilder group(@Nullable String group) {
      this.group = group;
      return this;
   }

   public Item getResult() {
      return this.result.getItem();
   }

   public AetherCookingRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
      this.criteria.put(criterionName, criterionTrigger);
      return this;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      this.ensureValid(id);
      Builder advancement$builder = recipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement$builder::addCriterion);
      AbstractCookingRecipe recipe = this.factory
         .create(Objects.requireNonNullElse(this.group, ""), this.bookCategory, this.ingredient, this.result, this.experience, this.cookingTime);
      recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private static AetherBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, RecipeCategory category) {
      if (serializer == AetherRecipeSerializers.ENCHANTING.get()) {
         return switch (category) {
            case BUILDING_BLOCKS, DECORATIONS -> AetherBookCategory.ENCHANTING_BLOCKS;
            case FOOD -> AetherBookCategory.ENCHANTING_FOOD;
            default -> AetherBookCategory.ENCHANTING_MISC;
         };
      } else if (serializer == AetherRecipeSerializers.REPAIRING.get()) {
         return AetherBookCategory.ENCHANTING_REPAIR;
      } else if (serializer == AetherRecipeSerializers.FREEZING.get()) {
         return switch (category) {
            case BUILDING_BLOCKS, DECORATIONS -> AetherBookCategory.FREEZABLE_BLOCKS;
            default -> AetherBookCategory.FREEZABLE_MISC;
         };
      } else {
         throw new IllegalStateException("Unknown cooking recipe type; may not belong to the Aether");
      }
   }

   private void ensureValid(ResourceLocation id) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + id);
      }
   }
}
