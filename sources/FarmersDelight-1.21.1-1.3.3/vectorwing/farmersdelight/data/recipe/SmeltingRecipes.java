package vectorwing.farmersdelight.data.recipe;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.registry.ModItems;

public class SmeltingRecipes {
   public static void register(RecipeOutput output) {
      foodSmeltingRecipes("fried_egg", Items.EGG, (ItemLike)ModItems.FRIED_EGG.get(), 0.35F, output);
      foodSmeltingRecipes("beef_patty", (ItemLike)ModItems.MINCED_BEEF.get(), (ItemLike)ModItems.BEEF_PATTY.get(), 0.35F, output);
      foodSmeltingRecipes("cooked_chicken_cuts", (ItemLike)ModItems.CHICKEN_CUTS.get(), (ItemLike)ModItems.COOKED_CHICKEN_CUTS.get(), 0.35F, output);
      foodSmeltingRecipes("cooked_cod_slice", (ItemLike)ModItems.COD_SLICE.get(), (ItemLike)ModItems.COOKED_COD_SLICE.get(), 0.35F, output);
      foodSmeltingRecipes("cooked_salmon_slice", (ItemLike)ModItems.SALMON_SLICE.get(), (ItemLike)ModItems.COOKED_SALMON_SLICE.get(), 0.35F, output);
      foodSmeltingRecipes("cooked_bacon", (ItemLike)ModItems.BACON.get(), (ItemLike)ModItems.COOKED_BACON.get(), 0.35F, output);
      foodSmeltingRecipes("cooked_mutton_chops", (ItemLike)ModItems.MUTTON_CHOPS.get(), (ItemLike)ModItems.COOKED_MUTTON_CHOPS.get(), 0.35F, output);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WHEAT_DOUGH.get()}), RecipeCategory.FOOD, Items.BREAD, 0.35F, 200)
         .unlockedBy("has_dough", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.WHEAT_DOUGH.get()}))
         .save(output, ResourceLocation.fromNamespaceAndPath("farmersdelight", "bread").toString() + "_from_smelting");
      SimpleCookingRecipeBuilder.smoking(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WHEAT_DOUGH.get()}), RecipeCategory.FOOD, Items.BREAD, 0.35F, 100)
         .unlockedBy("has_dough", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.WHEAT_DOUGH.get()}))
         .save(output, ResourceLocation.fromNamespaceAndPath("farmersdelight", "bread").toString() + "_from_smoking");
      SimpleCookingRecipeBuilder.smoking(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.HAM.get()}), RecipeCategory.FOOD, (ItemLike)ModItems.SMOKED_HAM.get(), 0.35F, 200
         )
         .unlockedBy("has_ham", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.HAM.get()}))
         .save(output);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.IRON_KNIFE.get()}), RecipeCategory.MISC, Items.IRON_NUGGET, 0.1F, 200)
         .unlockedBy("has_iron_knife", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.IRON_KNIFE.get()}))
         .save(output, ResourceLocation.fromNamespaceAndPath("farmersdelight", "iron_nugget_from_smelting_knife"));
      SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GOLDEN_KNIFE.get()}), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1F, 200
         )
         .unlockedBy("has_golden_knife", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.GOLDEN_KNIFE.get()}))
         .save(output, ResourceLocation.fromNamespaceAndPath("farmersdelight", "gold_nugget_from_smelting_knife"));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.IRON_KNIFE.get()}), RecipeCategory.MISC, Items.IRON_NUGGET, 0.1F, 100)
         .unlockedBy("has_iron_knife", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.IRON_KNIFE.get()}))
         .save(output, ResourceLocation.fromNamespaceAndPath("farmersdelight", "iron_nugget_from_blasting_knife"));
      SimpleCookingRecipeBuilder.blasting(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GOLDEN_KNIFE.get()}), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.1F, 100
         )
         .unlockedBy("has_golden_knife", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.GOLDEN_KNIFE.get()}))
         .save(output, ResourceLocation.fromNamespaceAndPath("farmersdelight", "gold_nugget_from_blasting_knife"));
   }

   private static void foodSmeltingRecipes(String name, ItemLike ingredient, ItemLike result, float experience, RecipeOutput output) {
      String namePrefix = ResourceLocation.fromNamespaceAndPath("farmersdelight", name).toString();
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{ingredient}), RecipeCategory.FOOD, result, experience, 200)
         .unlockedBy(name, TriggerInstance.hasItems(new ItemLike[]{ingredient}))
         .save(output);
      SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(new ItemLike[]{ingredient}), RecipeCategory.FOOD, result, experience, 600)
         .unlockedBy(name, TriggerInstance.hasItems(new ItemLike[]{ingredient}))
         .save(output, namePrefix + "_from_campfire_cooking");
      SimpleCookingRecipeBuilder.smoking(Ingredient.of(new ItemLike[]{ingredient}), RecipeCategory.FOOD, result, experience, 100)
         .unlockedBy(name, TriggerInstance.hasItems(new ItemLike[]{ingredient}))
         .save(output, namePrefix + "_from_smoking");
   }
}
