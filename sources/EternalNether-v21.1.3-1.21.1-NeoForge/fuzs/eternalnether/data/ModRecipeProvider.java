package fuzs.eternalnether.data;

import com.google.common.collect.ImmutableMap;
import fuzs.eternalnether.init.ModBlockFamilies;
import fuzs.eternalnether.init.ModBlocks;
import fuzs.eternalnether.init.ModItems;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.BlockFamily.Variant;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class ModRecipeProvider extends AbstractRecipeProvider {
   private static final Map<Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> STONECUTTING_BUILDERS = ImmutableMap.builder()
      .put(
         Variant.CHISELED,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{itemLike2}), RecipeCategory.BUILDING_BLOCKS, itemLike
         )
      )
      .put(
         Variant.CUT,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{itemLike2}), RecipeCategory.BUILDING_BLOCKS, itemLike
         )
      )
      .put(
         Variant.SLAB,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{itemLike2}), RecipeCategory.BUILDING_BLOCKS, itemLike, 2
         )
      )
      .put(
         Variant.STAIRS,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{itemLike2}), RecipeCategory.BUILDING_BLOCKS, itemLike
         )
      )
      .put(
         Variant.POLISHED,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{itemLike2}), RecipeCategory.BUILDING_BLOCKS, itemLike
         )
      )
      .put(
         Variant.WALL,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(itemLike, itemLike2) -> SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(new ItemLike[]{itemLike2}), RecipeCategory.DECORATIONS, itemLike
         )
      )
      .build();

   public ModRecipeProvider(DataProviderContext context) {
      super(context);
   }

   public void addRecipes(RecipeOutput recipeOutput) {
      generateForEnabledBlockFamilies(recipeOutput, FeatureFlags.DEFAULT_FLAGS);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModBlocks.WARPED_NETHER_BRICKS.value())
         .define('W', Items.WARPED_ROOTS)
         .define('N', Items.NETHER_BRICK)
         .pattern("NW")
         .pattern("WN")
         .unlockedBy(getHasName(Items.WARPED_ROOTS), has(Items.WARPED_ROOTS))
         .save(recipeOutput);
      smeltingResultFromBase(recipeOutput, Blocks.BLACKSTONE, (ItemLike)ModBlocks.COBBLED_BLACKSTONE.value());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.WITHERED_BONE_MEAL.value(), 3)
         .requires((ItemLike)ModItems.WITHERED_BONE.value())
         .group(getItemName((ItemLike)ModItems.WITHERED_BONE_MEAL.value()))
         .unlockedBy(getHasName((ItemLike)ModItems.WITHERED_BONE.value()), has((ItemLike)ModItems.WITHERED_BONE.value()))
         .save(recipeOutput);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(
         recipeOutput,
         RecipeCategory.MISC,
         (ItemLike)ModItems.WITHERED_BONE_MEAL.value(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)ModItems.WITHERED_BONE_BLOCK.value(),
         getConversionRecipeName((ItemLike)ModItems.WITHERED_BONE_MEAL.value(), (ItemLike)ModItems.WITHERED_BONE_BLOCK.value()),
         getItemName((ItemLike)ModItems.WITHERED_BONE_MEAL.value())
      );
   }

   public static void generateForEnabledBlockFamilies(RecipeOutput recipeOutput, FeatureFlagSet enabledFeatures) {
      ModBlockFamilies.getAllFamilies()
         .filter(BlockFamily::shouldGenerateRecipe)
         .forEach(blockFamily -> generateRecipes(recipeOutput, blockFamily, enabledFeatures));
   }

   public static void generateRecipes(RecipeOutput recipeOutput, BlockFamily blockFamily, FeatureFlagSet requiredFeatures) {
      RecipeProvider.generateRecipes(recipeOutput, blockFamily, requiredFeatures);
      blockFamily.getVariants().forEach((variant, block) -> {
         if (block.requiredFeatures().isSubsetOf(requiredFeatures)) {
            BiFunction<ItemLike, ItemLike, RecipeBuilder> biFunction = STONECUTTING_BUILDERS.get(variant);
            ItemLike itemLike = getBaseBlock(blockFamily, variant);
            if (biFunction != null) {
               RecipeBuilder recipeBuilder = biFunction.apply(block, itemLike);
               recipeBuilder.unlockedBy(blockFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(itemLike)), has(itemLike));
               recipeBuilder.save(recipeOutput, getStonecuttingRecipeName(block, itemLike));
            }
         }
      });
   }

   @Deprecated(
      forRemoval = true
   )
   public static String getStonecuttingRecipeName(ItemLike result, ItemLike material) {
      return getConversionRecipeName(result, material) + "_stonecutting";
   }
}
