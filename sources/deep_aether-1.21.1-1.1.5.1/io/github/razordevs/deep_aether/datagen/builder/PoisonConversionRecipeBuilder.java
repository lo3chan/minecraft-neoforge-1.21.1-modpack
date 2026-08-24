package io.github.razordevs.deep_aether.datagen.builder;

import io.github.razordevs.deep_aether.recipe.poison.PoisonRecipe;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class PoisonConversionRecipeBuilder implements RecipeBuilder {
   private final ItemStack resultStack;
   private final Ingredient ingredient;
   private final Map<String, Criterion<?>> criteria;
   @Nullable
   private String group;

   public PoisonConversionRecipeBuilder(Ingredient ingredient, ItemStack resultStack) {
      this.resultStack = resultStack;
      this.ingredient = ingredient;
      this.criteria = new LinkedHashMap<>();
   }

   public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
      this.criteria.put(s, criterion);
      return this;
   }

   public RecipeBuilder group(@Nullable String group) {
      this.group = group;
      return this;
   }

   public Item getResult() {
      return this.resultStack.getItem();
   }

   public static PoisonConversionRecipeBuilder conversion(ItemLike ingredient, ItemLike result) {
      return conversion(Ingredient.of(new ItemLike[]{ingredient}), new ItemStack(result.asItem()));
   }

   public static PoisonConversionRecipeBuilder conversion(Ingredient ingredient, ItemStack result) {
      return new PoisonConversionRecipeBuilder(ingredient, result);
   }

   public void save(RecipeOutput output, ResourceLocation location) {
      this.ensureValid(location);
      Builder advancement$builder = output.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(location))
         .requirements(Strategy.OR);
      this.criteria.forEach(advancement$builder::addCriterion);
      PoisonRecipe recipe = new PoisonRecipe(Objects.requireNonNullElse(this.group, ""), this.ingredient, this.resultStack);
      output.accept(location, recipe, advancement$builder.build(location.withPrefix("recipe/")));
   }

   private void ensureValid(ResourceLocation location) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + location);
      }
   }
}
