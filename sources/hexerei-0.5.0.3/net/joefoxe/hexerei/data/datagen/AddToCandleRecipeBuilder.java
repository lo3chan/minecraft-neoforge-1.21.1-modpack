package net.joefoxe.hexerei.data.datagen;

import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.minecraft.advancements.AdvancementHolder;
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
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public class AddToCandleRecipeBuilder implements RecipeBuilder {
   private final ItemStack result;
   private final Ingredient ingredient;
   private final Builder advancement = Builder.advancement();

   public AddToCandleRecipeBuilder(ItemLike ingredient, ItemStack result) {
      this.ingredient = Ingredient.of(new ItemLike[]{ingredient});
      this.result = result;
   }

   public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> criterion) {
      this.advancement.addCriterion(pCriterionName, criterion);
      return this;
   }

   public RecipeBuilder group(@Nullable String pGroupName) {
      return this;
   }

   public Item getResult() {
      return this.result.getItem();
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
      this.save(recipeOutput, resourceLocation.toString());
   }

   public void save(RecipeOutput recipeOutput, String id) {
      AdvancementHolder holder = recipeOutput.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(ResourceLocation.parse(id)))
         .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(ResourceLocation.parse(id)))
         .requirements(Strategy.OR)
         .build(ResourceLocation.withDefaultNamespace("recipes/root"));
      FluidStack fls1 = PotionFluidHandler.getFluidFromPotion(Potions.FIRE_RESISTANCE, PotionFluid.BottleType.SPLASH, 250);
      FluidStack fls2 = PotionFluidHandler.getFluidFromPotion(Potions.WATER, PotionFluid.BottleType.SPLASH, 250);
      FluidMixingRecipe recipe = new FluidMixingRecipe(NonNullList.withSize(8, Ingredient.EMPTY), fls1, fls2);
      String path = ResourceLocation.parse(id).getPath();
      recipeOutput.accept(ResourceLocation.fromNamespaceAndPath("hexerei", path.trim()), recipe, holder);
   }
}
