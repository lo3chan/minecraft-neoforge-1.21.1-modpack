package vectorwing.farmersdelight.data.recipe;

import java.util.stream.Stream;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.data.builder.CookingPotRecipeBuilder;

public class CookingRecipes {
   public static final int FAST_COOKING = 100;
   public static final int NORMAL_COOKING = 200;
   public static final int SLOW_COOKING = 400;
   public static final float SMALL_EXP = 0.35F;
   public static final float MEDIUM_EXP = 1.0F;
   public static final float LARGE_EXP = 2.0F;

   public static void register(RecipeOutput output) {
      cookMiscellaneous(output);
      cookMinecraftSoups(output);
      cookMeals(output);
   }

   private static Ingredient vegetablesPatch() {
      return DifferenceIngredient.of(Ingredient.of(Items.FOODS_VEGETABLE), Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.MELON_SLICE}));
   }

   private static void cookMiscellaneous(RecipeOutput output) {
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.HOT_COCOA.get(), 1, 200, 1.0F)
         .addIngredient(Items.DRINKS_MILK)
         .addIngredient(net.minecraft.world.item.Items.SUGAR)
         .addIngredient(net.minecraft.world.item.Items.COCOA_BEANS)
         .addIngredient(net.minecraft.world.item.Items.COCOA_BEANS)
         .unlockedByAnyIngredient(net.minecraft.world.item.Items.COCOA_BEANS, net.minecraft.world.item.Items.MILK_BUCKET, (ItemLike)ModItems.MILK_BOTTLE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.APPLE_CIDER.get(), 1, 200, 1.0F)
         .addIngredient(net.minecraft.world.item.Items.APPLE)
         .addIngredient(net.minecraft.world.item.Items.APPLE)
         .addIngredient(net.minecraft.world.item.Items.SUGAR)
         .unlockedByItems("has_apple", net.minecraft.world.item.Items.APPLE)
         .setRecipeBookTab(CookingPotRecipeBookTab.DRINKS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.TOMATO_SAUCE.get(), 1, 100, 0.35F)
         .addIngredient(CommonTags.Items.CROPS_TOMATO)
         .addIngredient(CommonTags.Items.CROPS_TOMATO)
         .unlockedByItems("has_tomato", (ItemLike)ModItems.TOMATO.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.DOG_FOOD.get(), 1, 200, 1.0F)
         .addIngredient(net.minecraft.world.item.Items.ROTTEN_FLESH)
         .addIngredient(net.minecraft.world.item.Items.BONE_MEAL)
         .addIngredient(Items.FOODS_RAW_MEAT)
         .addIngredient(CommonTags.Items.CROPS_RICE)
         .unlockedByAnyIngredient(net.minecraft.world.item.Items.ROTTEN_FLESH, net.minecraft.world.item.Items.BONE_MEAL, (ItemLike)ModItems.RICE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.GLOW_BERRY_CUSTARD.get(), 1, 200, 1.0F)
         .addIngredient(net.minecraft.world.item.Items.GLOW_BERRIES)
         .addIngredient(Items.DRINKS_MILK)
         .addIngredient(Items.EGGS)
         .addIngredient(net.minecraft.world.item.Items.SUGAR)
         .unlockedByAnyIngredient(net.minecraft.world.item.Items.GLOW_BERRIES, net.minecraft.world.item.Items.MILK_BUCKET, (ItemLike)ModItems.MILK_BOTTLE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
         .save(output);
   }

   private static void cookMinecraftSoups(RecipeOutput output) {
      CookingPotRecipeBuilder.cookingPotRecipe(net.minecraft.world.item.Items.MUSHROOM_STEW, 1, 200, 1.0F, net.minecraft.world.item.Items.BOWL)
         .addIngredient(net.minecraft.world.item.Items.BROWN_MUSHROOM)
         .addIngredient(net.minecraft.world.item.Items.RED_MUSHROOM)
         .unlockedByAnyIngredient(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .saveToFD(output);
      CookingPotRecipeBuilder.cookingPotRecipe(net.minecraft.world.item.Items.BEETROOT_SOUP, 1, 200, 1.0F, net.minecraft.world.item.Items.BOWL)
         .addIngredient(Items.CROPS_BEETROOT)
         .addIngredient(Items.CROPS_BEETROOT)
         .addIngredient(Items.CROPS_BEETROOT)
         .unlockedByItems("has_beetroot", net.minecraft.world.item.Items.BEETROOT)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .saveToFD(output);
      CookingPotRecipeBuilder.cookingPotRecipe(net.minecraft.world.item.Items.RABBIT_STEW, 1, 200, 1.0F, net.minecraft.world.item.Items.BOWL)
         .addIngredient(Items.CROPS_POTATO)
         .addIngredient(net.minecraft.world.item.Items.RABBIT)
         .addIngredient(Items.CROPS_CARROT)
         .addIngredient(Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BROWN_MUSHROOM, net.minecraft.world.item.Items.RED_MUSHROOM}))
         .unlockedByAnyIngredient(
            net.minecraft.world.item.Items.RABBIT,
            net.minecraft.world.item.Items.BROWN_MUSHROOM,
            net.minecraft.world.item.Items.RED_MUSHROOM,
            net.minecraft.world.item.Items.CARROT,
            net.minecraft.world.item.Items.POTATO
         )
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .saveToFD(output);
   }

   private static void cookMeals(RecipeOutput output) {
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.BAKED_COD_STEW.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_RAW_COD)
         .addIngredient(Items.CROPS_POTATO)
         .addIngredient(Items.EGGS)
         .addIngredient(CommonTags.Items.CROPS_TOMATO)
         .unlockedByAnyIngredient(
            net.minecraft.world.item.Items.COD, net.minecraft.world.item.Items.POTATO, (ItemLike)ModItems.TOMATO.get(), net.minecraft.world.item.Items.EGG
         )
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.BEEF_STEW.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_RAW_BEEF)
         .addIngredient(Items.CROPS_CARROT)
         .addIngredient(Items.CROPS_POTATO)
         .unlockedByAnyIngredient(net.minecraft.world.item.Items.BEEF, net.minecraft.world.item.Items.CARROT, net.minecraft.world.item.Items.POTATO)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.BONE_BROTH.get(), 1, 200, 0.35F)
         .addIngredient(Items.BONES)
         .addIngredient(
            Ingredient.fromValues(
               Stream.of(
                  new ItemValue(new ItemStack(net.minecraft.world.item.Items.GLOW_BERRIES)),
                  new TagValue(Items.MUSHROOMS),
                  new ItemValue(new ItemStack(net.minecraft.world.item.Items.HANGING_ROOTS)),
                  new ItemValue(new ItemStack(net.minecraft.world.item.Items.GLOW_LICHEN))
               )
            )
         )
         .unlockedByItems("has_bone", net.minecraft.world.item.Items.BONE)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.CABBAGE_ROLLS.get(), 1, 100, 0.35F)
         .addIngredient(CommonTags.Items.CROPS_CABBAGE)
         .addIngredient(
            CompoundIngredient.of(
               new Ingredient[]{
                  Ingredient.of(Items.FOODS_RAW_MEAT),
                  Ingredient.of(CommonTags.Items.FOODS_SAFE_RAW_FISH),
                  Ingredient.of(Items.FOODS_VEGETABLE),
                  Ingredient.of(Items.MUSHROOMS)
               }
            )
         )
         .unlockedByAnyIngredient((ItemLike)ModItems.CABBAGE.get(), (ItemLike)ModItems.CABBAGE_LEAF.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.CHICKEN_SOUP.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_RAW_CHICKEN)
         .addIngredient(Items.CROPS_CARROT)
         .addIngredient(CommonTags.Items.FOODS_LEAFY_GREEN)
         .addIngredient(vegetablesPatch())
         .unlockedByAnyIngredient(net.minecraft.world.item.Items.CHICKEN, net.minecraft.world.item.Items.CARROT)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.COOKED_RICE.get(), 1, 100, 0.35F)
         .addIngredient(CommonTags.Items.CROPS_RICE)
         .unlockedByItems("has_rice", (ItemLike)ModItems.RICE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.DUMPLINGS.get(), 2, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_DOUGH)
         .addIngredient(CommonTags.Items.CROPS_CABBAGE)
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .addIngredient(
            Ingredient.fromValues(
               Stream.of(
                  new TagValue(CommonTags.Items.FOODS_RAW_CHICKEN),
                  new TagValue(CommonTags.Items.FOODS_RAW_PORK),
                  new TagValue(CommonTags.Items.FOODS_RAW_BEEF),
                  new ItemValue(new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM))
               )
            )
         )
         .unlockedByAnyIngredient((ItemLike)ModItems.WHEAT_DOUGH.get(), (ItemLike)ModItems.CABBAGE.get(), (ItemLike)ModItems.ONION.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MISC)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.FISH_STEW.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_SAFE_RAW_FISH)
         .addIngredient((ItemLike)ModItems.TOMATO_SAUCE.get())
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .unlockedByAnyIngredient(
            net.minecraft.world.item.Items.SALMON,
            net.minecraft.world.item.Items.COD,
            net.minecraft.world.item.Items.TROPICAL_FISH,
            (ItemLike)ModItems.TOMATO_SAUCE.get(),
            (ItemLike)ModItems.ONION.get()
         )
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.FRIED_RICE.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.CROPS_RICE)
         .addIngredient(Items.EGGS)
         .addIngredient(Items.CROPS_CARROT)
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .unlockedByAnyIngredient(
            (ItemLike)ModItems.RICE.get(), net.minecraft.world.item.Items.EGG, net.minecraft.world.item.Items.CARROT, (ItemLike)ModItems.ONION.get()
         )
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.MUSHROOM_RICE.get(), 1, 200, 1.0F)
         .addIngredient(net.minecraft.world.item.Items.BROWN_MUSHROOM)
         .addIngredient(net.minecraft.world.item.Items.RED_MUSHROOM)
         .addIngredient(CommonTags.Items.CROPS_RICE)
         .addIngredient(Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CARROT, net.minecraft.world.item.Items.POTATO}))
         .unlockedByAnyIngredient(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, (ItemLike)ModItems.RICE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.NOODLE_SOUP.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_PASTA)
         .addIngredient(Items.EGGS)
         .addIngredient(net.minecraft.world.item.Items.DRIED_KELP)
         .addIngredient(CommonTags.Items.FOODS_RAW_PORK)
         .unlockedByAnyIngredient((ItemLike)ModItems.RAW_PASTA.get(), net.minecraft.world.item.Items.DRIED_KELP, net.minecraft.world.item.Items.PORKCHOP)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.ONION_SOUP.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .addIngredient(Items.FOODS_BREAD)
         .addIngredient(Items.DRINKS_MILK)
         .unlockedByAnyIngredient(
            (ItemLike)ModItems.ONION.get(),
            net.minecraft.world.item.Items.BREAD,
            net.minecraft.world.item.Items.MILK_BUCKET,
            (ItemLike)ModItems.MILK_BOTTLE.get()
         )
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.PASTA_WITH_MEATBALLS.get(), 1, 200, 1.0F)
         .addIngredient((ItemLike)ModItems.MINCED_BEEF.get())
         .addIngredient(CommonTags.Items.FOODS_PASTA)
         .addIngredient((ItemLike)ModItems.TOMATO_SAUCE.get())
         .unlockedByAnyIngredient((ItemLike)ModItems.RAW_PASTA.get(), net.minecraft.world.item.Items.BEEF, (ItemLike)ModItems.TOMATO_SAUCE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.PASTA_WITH_MUTTON_CHOP.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_RAW_MUTTON)
         .addIngredient(CommonTags.Items.FOODS_PASTA)
         .addIngredient((ItemLike)ModItems.TOMATO_SAUCE.get())
         .unlockedByAnyIngredient((ItemLike)ModItems.RAW_PASTA.get(), net.minecraft.world.item.Items.MUTTON, (ItemLike)ModItems.TOMATO_SAUCE.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.PUMPKIN_SOUP.get(), 1, 200, 1.0F)
         .addIngredient((ItemLike)ModItems.PUMPKIN_SLICE.get())
         .addIngredient(CommonTags.Items.FOODS_LEAFY_GREEN)
         .addIngredient(CommonTags.Items.FOODS_RAW_PORK)
         .addIngredient(Items.DRINKS_MILK)
         .unlockedByAnyIngredient(
            net.minecraft.world.item.Items.PUMPKIN,
            (ItemLike)ModItems.PUMPKIN_SLICE.get(),
            net.minecraft.world.item.Items.PORKCHOP,
            net.minecraft.world.item.Items.MILK_BUCKET,
            (ItemLike)ModItems.MILK_BOTTLE.get()
         )
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.RATATOUILLE.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.CROPS_TOMATO)
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .addIngredient(Items.CROPS_BEETROOT)
         .addIngredient(vegetablesPatch())
         .unlockedByAnyIngredient((ItemLike)ModItems.TOMATO.get(), (ItemLike)ModItems.ONION.get(), net.minecraft.world.item.Items.BEETROOT)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.SQUID_INK_PASTA.get(), 1, 200, 1.0F)
         .addIngredient(CommonTags.Items.FOODS_SAFE_RAW_FISH)
         .addIngredient(CommonTags.Items.FOODS_PASTA)
         .addIngredient(CommonTags.Items.CROPS_TOMATO)
         .addIngredient(net.minecraft.world.item.Items.INK_SAC)
         .unlockedByAnyIngredient((ItemLike)ModItems.RAW_PASTA.get(), net.minecraft.world.item.Items.INK_SAC, (ItemLike)ModItems.TOMATO.get())
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.STUFFED_PUMPKIN_BLOCK.get(), 1, 400, 2.0F, net.minecraft.world.item.Items.PUMPKIN)
         .addIngredient(CommonTags.Items.CROPS_RICE)
         .addIngredient(CommonTags.Items.CROPS_ONION)
         .addIngredient(net.minecraft.world.item.Items.BROWN_MUSHROOM)
         .addIngredient(Items.CROPS_POTATO)
         .addIngredient(Items.FOODS_BERRY)
         .addIngredient(vegetablesPatch())
         .unlockedByItems("has_pumpkin", Blocks.PUMPKIN)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.VEGETABLE_NOODLES.get(), 1, 200, 1.0F)
         .addIngredient(Items.CROPS_CARROT)
         .addIngredient(Items.MUSHROOMS)
         .addIngredient(CommonTags.Items.FOODS_PASTA)
         .addIngredient(CommonTags.Items.FOODS_LEAFY_GREEN)
         .addIngredient(vegetablesPatch())
         .unlockedByAnyIngredient((ItemLike)ModItems.RAW_PASTA.get(), net.minecraft.world.item.Items.BROWN_MUSHROOM, net.minecraft.world.item.Items.CARROT)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
      CookingPotRecipeBuilder.cookingPotRecipe((ItemLike)ModItems.VEGETABLE_SOUP.get(), 1, 200, 1.0F)
         .addIngredient(Items.CROPS_CARROT)
         .addIngredient(Items.CROPS_POTATO)
         .addIngredient(Items.CROPS_BEETROOT)
         .addIngredient(CommonTags.Items.FOODS_LEAFY_GREEN)
         .unlockedByAnyIngredient(net.minecraft.world.item.Items.CARROT, (ItemLike)ModItems.ONION.get(), net.minecraft.world.item.Items.BEETROOT)
         .setRecipeBookTab(CookingPotRecipeBookTab.MEALS)
         .save(output);
   }
}
