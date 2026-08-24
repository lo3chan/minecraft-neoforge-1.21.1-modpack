package com.aetherteam.aether.recipe.builder;

import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class IncubationBuilder implements RecipeBuilder {
   private final Ingredient ingredient;
   private final EntityType<?> entity;
   @Nullable
   private final CompoundTag tag;
   private final int incubationTime;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;

   public IncubationBuilder(Ingredient ingredient, EntityType<?> entity, @Nullable CompoundTag tag, int incubationTime) {
      this.ingredient = ingredient;
      this.entity = entity;
      this.tag = tag;
      this.incubationTime = incubationTime;
   }

   public static IncubationBuilder incubation(Ingredient ingredient, EntityType<?> entity, CompoundTag tag, int incubationTime) {
      return new IncubationBuilder(ingredient, entity, tag, incubationTime);
   }

   public IncubationBuilder group(@Nullable String group) {
      this.group = group;
      return this;
   }

   public Item getResult() {
      return Items.AIR;
   }

   public IncubationBuilder unlockedBy(String criterionName, Criterion criterionTrigger) {
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
      recipeOutput.accept(
         id,
         new IncubationRecipe(this.group == null ? "" : this.group, this.ingredient, this.entity, Optional.ofNullable(this.tag), this.incubationTime),
         advancement$builder.build(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/incubation/" + id.getPath()))
      );
   }

   private void ensureValid(ResourceLocation id) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + id);
      }
   }
}
