package vectorwing.farmersdelight.data.recipe;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;
import vectorwing.farmersdelight.common.crafting.DoughRecipe;
import vectorwing.farmersdelight.common.crafting.FoodServingRecipe;
import vectorwing.farmersdelight.common.crafting.condition.VanillaCrateEnabledCondition;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.RecipeUtils;

public class CraftingRecipes {
   public static void register(RecipeOutput output) {
      recipesVanillaAlternatives(output);
      recipesBlocks(output);
      recipesCanvasSigns(output);
      recipesTools(output);
      recipesMaterials(output);
      recipesFoodstuffs(output);
      recipesFoodBlocks(output);
      recipesCraftedMeals(output);
      SpecialRecipeBuilder.special(FoodServingRecipe::new).save(output, "farmersdelight:food_serving");
      SpecialRecipeBuilder.special(DoughRecipe::new).save(output, "farmersdelight:wheat_dough_from_water");
   }

   public static void canvasSignDyeing(RecipeOutput output, ItemLike canvasSign, ItemLike hangingCanvasSign, TagKey<Item> dyeTag) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, canvasSign, 1)
         .requires(ModTags.Items.CANVAS_SIGNS)
         .requires(dyeTag)
         .unlockedBy("has_canvas_sign", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS_SIGN.get()}))
         .group("fd_canvas_sign")
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, hangingCanvasSign, 1)
         .requires(ModTags.Items.HANGING_CANVAS_SIGNS)
         .requires(dyeTag)
         .unlockedBy("has_hanging_canvas_sign", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.HANGING_CANVAS_SIGN.get()}))
         .group("fd_hanging_canvas_sign")
         .save(output);
   }

   private static void recipesVanillaAlternatives(RecipeOutput output) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PUMPKIN_SEEDS)
         .requires((ItemLike)ModItems.PUMPKIN_SLICE.get())
         .unlockedBy("has_pumpkin_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PUMPKIN_SLICE.get()}))
         .save(output, RecipeUtils.FDLocation("pumpkin_seeds_from_slice"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.SCAFFOLDING, 6)
         .pattern("b#b")
         .pattern("b b")
         .pattern("b b")
         .define('b', Items.BAMBOO)
         .define('#', (ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output, RecipeUtils.FDLocation("scaffolding_from_canvas"));
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.LEAD)
         .pattern("ss ")
         .pattern("ss ")
         .pattern("  s")
         .define('s', (ItemLike)ModItems.STRAW.get())
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .save(output, RecipeUtils.FDLocation("lead_from_straw"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.PAINTING)
         .pattern("sss")
         .pattern("scs")
         .pattern("sss")
         .define('s', Items.STICK)
         .define('c', (ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output, RecipeUtils.FDLocation("painting_from_canvas"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.PUMPKIN)
         .pattern("##")
         .pattern("##")
         .define('#', (ItemLike)ModItems.PUMPKIN_SLICE.get())
         .unlockedBy("has_pumpkin_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PUMPKIN_SLICE.get()}))
         .save(output, RecipeUtils.FDLocation("pumpkin_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.CAKE)
         .pattern("mmm")
         .pattern("ses")
         .pattern("www")
         .define('m', net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK)
         .define('s', Items.SUGAR)
         .define('e', net.neoforged.neoforge.common.Tags.Items.EGGS)
         .define('w', net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .unlockedBy("has_milk_bottle", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.MILK_BOTTLE.get()}))
         .group("cake")
         .save(output, RecipeUtils.FDLocation("cake_from_milk_bottle"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.CAKE)
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .requires((ItemLike)ModItems.CAKE_SLICE.get())
         .unlockedBy("has_cake_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CAKE_SLICE.get()}))
         .group("cake")
         .save(output, RecipeUtils.FDLocation("cake_from_slices"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BOOK)
         .requires(Items.PAPER)
         .requires(Items.PAPER)
         .requires(Items.PAPER)
         .requires((ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output, RecipeUtils.FDLocation("book_from_canvas"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.MILK_BUCKET)
         .requires(Items.BUCKET)
         .requires((ItemLike)ModItems.MILK_BOTTLE.get())
         .requires((ItemLike)ModItems.MILK_BOTTLE.get())
         .requires((ItemLike)ModItems.MILK_BOTTLE.get())
         .requires((ItemLike)ModItems.MILK_BOTTLE.get())
         .unlockedBy("has_milk_bottle", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.MILK_BOTTLE.get()}))
         .save(output, RecipeUtils.FDLocation("milk_bucket_from_bottles"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PAPER)
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .unlockedBy("has_tree_bark", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.TREE_BARK.get()}))
         .save(output, RecipeUtils.FDLocation("paper_from_tree_bark"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.PACKED_MUD, 2)
         .requires((ItemLike)ModItems.STRAW.get())
         .requires(Items.MUD)
         .requires(Items.MUD)
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .save(output, RecipeUtils.FDLocation("packed_mud_from_straw"));
   }

   private static void recipesCanvasSigns(RecipeOutput output) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.CANVAS_SIGN.get(), 3)
         .pattern("w#w")
         .pattern("w#w")
         .pattern(" / ")
         .define('w', ItemTags.PLANKS)
         .define('#', (ItemLike)ModItems.CANVAS.get())
         .define('/', Items.STICK)
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.HANGING_CANVAS_SIGN.get(), 6)
         .pattern("X X")
         .pattern("w#w")
         .pattern("w#w")
         .define('X', Items.CHAIN)
         .define('w', ItemTags.LOGS)
         .define('#', (ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output);
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.WHITE_CANVAS_SIGN.get(),
         (ItemLike)ModItems.WHITE_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_WHITE
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.ORANGE_CANVAS_SIGN.get(),
         (ItemLike)ModItems.ORANGE_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_ORANGE
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.MAGENTA_CANVAS_SIGN.get(),
         (ItemLike)ModItems.MAGENTA_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_MAGENTA
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.LIGHT_BLUE_CANVAS_SIGN.get(),
         (ItemLike)ModItems.LIGHT_BLUE_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_LIGHT_BLUE
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.YELLOW_CANVAS_SIGN.get(),
         (ItemLike)ModItems.YELLOW_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_YELLOW
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.LIME_CANVAS_SIGN.get(),
         (ItemLike)ModItems.LIME_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_LIME
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.PINK_CANVAS_SIGN.get(),
         (ItemLike)ModItems.PINK_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_PINK
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.GRAY_CANVAS_SIGN.get(),
         (ItemLike)ModItems.GRAY_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_GRAY
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.LIGHT_GRAY_CANVAS_SIGN.get(),
         (ItemLike)ModItems.LIGHT_GRAY_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_LIGHT_GRAY
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.CYAN_CANVAS_SIGN.get(),
         (ItemLike)ModItems.CYAN_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_CYAN
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.PURPLE_CANVAS_SIGN.get(),
         (ItemLike)ModItems.PURPLE_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_PURPLE
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.BLUE_CANVAS_SIGN.get(),
         (ItemLike)ModItems.BLUE_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_BLUE
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.BROWN_CANVAS_SIGN.get(),
         (ItemLike)ModItems.BROWN_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_BROWN
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.GREEN_CANVAS_SIGN.get(),
         (ItemLike)ModItems.GREEN_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_GREEN
      );
      canvasSignDyeing(
         output, (ItemLike)ModItems.RED_CANVAS_SIGN.get(), (ItemLike)ModItems.RED_HANGING_CANVAS_SIGN.get(), net.neoforged.neoforge.common.Tags.Items.DYES_RED
      );
      canvasSignDyeing(
         output,
         (ItemLike)ModItems.BLACK_CANVAS_SIGN.get(),
         (ItemLike)ModItems.BLACK_HANGING_CANVAS_SIGN.get(),
         net.neoforged.neoforge.common.Tags.Items.DYES_BLACK
      );
   }

   private static void recipesBlocks(RecipeOutput output) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.STOVE.get())
         .pattern("iii")
         .pattern("B B")
         .pattern("BCB")
         .define('i', net.neoforged.neoforge.common.Tags.Items.INGOTS_IRON)
         .define('B', Blocks.BRICKS)
         .define('C', Blocks.CAMPFIRE)
         .unlockedBy("has_campfire", TriggerInstance.hasItems(new ItemLike[]{Blocks.CAMPFIRE}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.COOKING_POT.get())
         .pattern("bSb")
         .pattern("iWi")
         .pattern("iii")
         .define('b', Items.BRICK)
         .define('i', net.neoforged.neoforge.common.Tags.Items.INGOTS_IRON)
         .define('S', Items.WOODEN_SHOVEL)
         .define('W', net.neoforged.neoforge.common.Tags.Items.BUCKETS_WATER)
         .unlockedBy("has_iron_ingot", TriggerInstance.hasItems(new ItemLike[]{Items.IRON_INGOT}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.WOODEN_BASKET.get())
         .pattern("/ /")
         .pattern("# #")
         .pattern("/#/")
         .define('/', Items.STICK)
         .define('#', (ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.BAMBOO_BASKET.get())
         .pattern("/ /")
         .pattern("# #")
         .pattern("/#/")
         .define('/', Items.BAMBOO)
         .define('#', (ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.CUTTING_BOARD.get())
         .pattern("/##")
         .pattern("/##")
         .define('/', Items.STICK)
         .define('#', ItemTags.PLANKS)
         .unlockedBy("has_stick", TriggerInstance.hasItems(new ItemLike[]{Items.STICK}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.SKILLET.get())
         .pattern(" ##")
         .pattern(" ##")
         .pattern("/  ")
         .define('/', Items.BRICK)
         .define('#', net.neoforged.neoforge.common.Tags.Items.INGOTS_IRON)
         .unlockedBy("has_brick", TriggerInstance.hasItems(new ItemLike[]{Items.BRICK}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.OAK_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.OAK_SLAB)
         .define('D', Items.OAK_TRAPDOOR)
         .unlockedBy("has_oak_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.OAK_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.BIRCH_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.BIRCH_SLAB)
         .define('D', Items.BIRCH_TRAPDOOR)
         .unlockedBy("has_birch_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.BIRCH_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.SPRUCE_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.SPRUCE_SLAB)
         .define('D', Items.SPRUCE_TRAPDOOR)
         .unlockedBy("has_spruce_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.SPRUCE_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.JUNGLE_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.JUNGLE_SLAB)
         .define('D', Items.JUNGLE_TRAPDOOR)
         .unlockedBy("has_jungle_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.JUNGLE_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.ACACIA_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.ACACIA_SLAB)
         .define('D', Items.ACACIA_TRAPDOOR)
         .unlockedBy("has_acacia_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.ACACIA_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.DARK_OAK_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.DARK_OAK_SLAB)
         .define('D', Items.DARK_OAK_TRAPDOOR)
         .unlockedBy("has_dark_oak_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.DARK_OAK_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.MANGROVE_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.MANGROVE_SLAB)
         .define('D', Items.MANGROVE_TRAPDOOR)
         .unlockedBy("has_mangrove_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.MANGROVE_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.CHERRY_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.CHERRY_SLAB)
         .define('D', Items.CHERRY_TRAPDOOR)
         .unlockedBy("has_cherry_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.CHERRY_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.BAMBOO_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.BAMBOO_SLAB)
         .define('D', Items.BAMBOO_TRAPDOOR)
         .unlockedBy("has_bamboo_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.BAMBOO_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.CRIMSON_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.CRIMSON_SLAB)
         .define('D', Items.CRIMSON_TRAPDOOR)
         .unlockedBy("has_crimson_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.CRIMSON_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModBlocks.WARPED_CABINET.get())
         .pattern("___")
         .pattern("D D")
         .pattern("___")
         .define('_', Items.WARPED_SLAB)
         .define('D', Items.WARPED_TRAPDOOR)
         .unlockedBy("has_warped_trapdoor", TriggerInstance.hasItems(new ItemLike[]{Items.WARPED_TRAPDOOR}))
         .group("fd_cabinet")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.ROPE.get(), 4)
         .pattern("s")
         .pattern("s")
         .define('s', (ItemLike)ModItems.STRAW.get())
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .group("fd_rope")
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)ModItems.ROPE.get(), 4)
         .requires((ItemLike)ModItems.SAFETY_NET.get())
         .unlockedBy("has_safety_net", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.SAFETY_NET.get()}))
         .group("fd_rope")
         .save(output, RecipeUtils.FDLocation("rope_from_safety_net"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.SAFETY_NET.get(), 1)
         .pattern("rr")
         .pattern("rr")
         .define('r', (ItemLike)ModItems.ROPE.get())
         .unlockedBy("has_rope", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.ROPE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.ROPE_FENCE.get(), 3)
         .pattern("r/r")
         .pattern("r/r")
         .define('/', Items.STICK)
         .define('r', (ItemLike)ModItems.ROPE.get())
         .unlockedBy("has_rope", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.ROPE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.ROPE_FENCE_GATE.get())
         .pattern("/r/")
         .pattern("/r/")
         .define('/', Items.STICK)
         .define('r', (ItemLike)ModItems.ROPE.get())
         .unlockedBy("has_rope", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.ROPE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.BEETROOT_CRATE.get())
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', Items.BEETROOT)
         .unlockedBy("has_beetroot", TriggerInstance.hasItems(new ItemLike[]{Items.BEETROOT}))
         .save(output.withConditions(new ICondition[]{VanillaCrateEnabledCondition.INSTANCE}), RecipeUtils.FDLocation("beetroot_crate"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.CARROT_CRATE.get())
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', Items.CARROT)
         .unlockedBy("has_carrot", TriggerInstance.hasItems(new ItemLike[]{Items.CARROT}))
         .save(output.withConditions(new ICondition[]{VanillaCrateEnabledCondition.INSTANCE}), RecipeUtils.FDLocation("carrot_crate"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.POTATO_CRATE.get())
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', Items.POTATO)
         .unlockedBy("has_potato", TriggerInstance.hasItems(new ItemLike[]{Items.POTATO}))
         .save(output.withConditions(new ICondition[]{VanillaCrateEnabledCondition.INSTANCE}), RecipeUtils.FDLocation("potato_crate"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.CABBAGE_CRATE.get(), 1)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', (ItemLike)ModItems.CABBAGE.get())
         .unlockedBy("has_cabbage", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CABBAGE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.TOMATO_CRATE.get(), 1)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', (ItemLike)ModItems.TOMATO.get())
         .unlockedBy("has_tomato", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.TOMATO.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.ONION_CRATE.get(), 1)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', (ItemLike)ModItems.ONION.get())
         .unlockedBy("has_onion", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.ONION.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.RICE_BALE.get(), 1)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', (ItemLike)ModItems.RICE_PANICLE.get())
         .unlockedBy("has_rice_panicle", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.RICE_PANICLE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.RICE_BAG.get(), 1)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', (ItemLike)ModItems.RICE.get())
         .unlockedBy("has_rice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.RICE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.STRAW_BALE.get(), 1)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .define('#', (ItemLike)ModItems.STRAW.get())
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)ModItems.CANVAS_RUG.get(), 2)
         .requires((ItemLike)ModItems.CANVAS.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.CANVAS.get(), 1)
         .requires((ItemLike)ModItems.CANVAS_RUG.get())
         .requires((ItemLike)ModItems.CANVAS_RUG.get())
         .unlockedBy("has_canvas_rug", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS_RUG.get()}))
         .group("fd_canvas")
         .save(output, RecipeUtils.FDLocation("canvas_from_canvas_rug"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.ORGANIC_COMPOST.get(), 1)
         .requires(Items.DIRT)
         .requires(Items.ROTTEN_FLESH)
         .requires(Items.ROTTEN_FLESH)
         .requires((ItemLike)ModItems.STRAW.get())
         .requires((ItemLike)ModItems.STRAW.get())
         .requires(Items.BONE_MEAL)
         .requires(Items.BONE_MEAL)
         .requires(Items.BONE_MEAL)
         .requires(Items.BONE_MEAL)
         .unlockedBy("has_rotten_flesh", TriggerInstance.hasItems(new ItemLike[]{Items.ROTTEN_FLESH}))
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .group("fd_organic_compost")
         .save(output, RecipeUtils.FDLocation("organic_compost_from_rotten_flesh"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.ORGANIC_COMPOST.get(), 1)
         .requires(Items.DIRT)
         .requires((ItemLike)ModItems.STRAW.get())
         .requires((ItemLike)ModItems.STRAW.get())
         .requires(Items.BONE_MEAL)
         .requires(Items.BONE_MEAL)
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .requires((ItemLike)ModItems.TREE_BARK.get())
         .unlockedBy("has_tree_bark", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.TREE_BARK.get()}))
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .group("fd_organic_compost")
         .save(output, RecipeUtils.FDLocation("organic_compost_from_tree_bark"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.TATAMI.get(), 2)
         .pattern("cs")
         .pattern("sc")
         .define('c', (ItemLike)ModItems.CANVAS.get())
         .define('s', (ItemLike)ModItems.STRAW.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .group("fd_tatami")
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)ModItems.FULL_TATAMI_MAT.get(), 2)
         .requires((ItemLike)ModItems.TATAMI.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .group("fd_full_tatami_mat")
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)ModItems.HALF_TATAMI_MAT.get(), 2)
         .requires((ItemLike)ModItems.FULL_TATAMI_MAT.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, (ItemLike)ModItems.FULL_TATAMI_MAT.get(), 1)
         .requires((ItemLike)ModItems.HALF_TATAMI_MAT.get())
         .requires((ItemLike)ModItems.HALF_TATAMI_MAT.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .group("fd_full_tatami_mat")
         .save(output, RecipeUtils.FDLocation("full_tatami_mat_from_halves"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.TATAMI.get(), 1)
         .requires((ItemLike)ModItems.FULL_TATAMI_MAT.get())
         .requires((ItemLike)ModItems.FULL_TATAMI_MAT.get())
         .unlockedBy("has_canvas", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CANVAS.get()}))
         .group("fd_tatami")
         .save(output, RecipeUtils.FDLocation("tatami_block_from_full"));
   }

   private static void recipesTools(RecipeOutput output) {
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.FLINT_KNIFE.get())
         .pattern("m")
         .pattern("s")
         .define('m', Items.FLINT)
         .define('s', Items.STICK)
         .unlockedBy("has_stick", TriggerInstance.hasItems(new ItemLike[]{Items.STICK}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.IRON_KNIFE.get())
         .pattern("m")
         .pattern("s")
         .define('m', net.neoforged.neoforge.common.Tags.Items.INGOTS_IRON)
         .define('s', Items.STICK)
         .unlockedBy("has_iron_ingot", TriggerInstance.hasItems(new ItemLike[]{Items.IRON_INGOT}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.DIAMOND_KNIFE.get())
         .pattern("m")
         .pattern("s")
         .define('m', Items.DIAMOND)
         .define('s', Items.STICK)
         .unlockedBy("has_diamond", TriggerInstance.hasItems(new ItemLike[]{Items.DIAMOND}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.GOLDEN_KNIFE.get())
         .pattern("m")
         .pattern("s")
         .define('m', Items.GOLD_INGOT)
         .define('s', Items.STICK)
         .unlockedBy("has_gold_ingot", TriggerInstance.hasItems(new ItemLike[]{Items.GOLD_INGOT}))
         .save(output);
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(new ItemLike[]{Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE}),
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DIAMOND_KNIFE.get()}),
            Ingredient.of(new ItemLike[]{Items.NETHERITE_INGOT}),
            RecipeCategory.COMBAT,
            ModItems.NETHERITE_KNIFE.get()
         )
         .unlocks("has_netherite_ingot", TriggerInstance.hasItems(new ItemLike[]{Items.NETHERITE_INGOT}))
         .save(output, "farmersdelight:netherite_knife_smithing");
   }

   private static void recipesMaterials(RecipeOutput output) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.CANVAS.get())
         .pattern("##")
         .pattern("##")
         .define('#', (ItemLike)ModItems.STRAW.get())
         .unlockedBy("has_straw", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW.get()}))
         .group("fd_canvas")
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.CARROT, 9)
         .requires((ItemLike)ModItems.CARROT_CRATE.get())
         .unlockedBy("has_carrot_crate", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CARROT_CRATE.get()}))
         .save(output, RecipeUtils.FDLocation("carrot_from_crate"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.POTATO, 9)
         .requires((ItemLike)ModItems.POTATO_CRATE.get())
         .unlockedBy("has_potato_crate", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.POTATO_CRATE.get()}))
         .save(output, RecipeUtils.FDLocation("potato_from_crate"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BEETROOT, 9)
         .requires((ItemLike)ModItems.BEETROOT_CRATE.get())
         .unlockedBy("has_beetroot_crate", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.BEETROOT_CRATE.get()}))
         .save(output, RecipeUtils.FDLocation("beetroot_from_crate"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.CABBAGE.get(), 9)
         .requires((ItemLike)ModItems.CABBAGE_CRATE.get())
         .unlockedBy("has_cabbage_crate", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CABBAGE_CRATE.get()}))
         .group("fd_cabbage")
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.TOMATO.get(), 9)
         .requires((ItemLike)ModItems.TOMATO_CRATE.get())
         .unlockedBy("has_tomato_crate", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.TOMATO_CRATE.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.ONION.get(), 9)
         .requires((ItemLike)ModItems.ONION_CRATE.get())
         .unlockedBy("has_onion_crate", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.ONION_CRATE.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.RICE_PANICLE.get(), 9)
         .requires((ItemLike)ModItems.RICE_BALE.get())
         .unlockedBy("has_rice_bale", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.RICE_BALE.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.RICE.get(), 9)
         .requires((ItemLike)ModItems.RICE_BAG.get())
         .unlockedBy("has_rice_bag", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.RICE_BAG.get()}))
         .group("fd_rice")
         .save(output, "farmersdelight:rice_from_bag");
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.STRAW.get(), 9)
         .requires((ItemLike)ModItems.STRAW_BALE.get())
         .unlockedBy("has_straw_bale", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.STRAW_BALE.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.RICE.get())
         .requires((ItemLike)ModItems.RICE_PANICLE.get())
         .unlockedBy("has_rice_panicle", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.RICE_PANICLE.get()}))
         .group("fd_rice")
         .save(output);
   }

   private static void recipesFoodstuffs(RecipeOutput output) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.TOMATO_SEEDS.get())
         .requires(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TOMATO.get(), (ItemLike)ModItems.ROTTEN_TOMATO.get()}))
         .unlockedBy("has_tomato", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.TOMATO.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.MILK_BOTTLE.get(), 4)
         .requires(Items.MILK_BUCKET)
         .requires(Items.GLASS_BOTTLE)
         .requires(Items.GLASS_BOTTLE)
         .requires(Items.GLASS_BOTTLE)
         .requires(Items.GLASS_BOTTLE)
         .unlockedBy("has_milk_bucket", TriggerInstance.hasItems(new ItemLike[]{Items.MILK_BUCKET}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.MELON_JUICE.get(), 1)
         .requires(Items.MELON_SLICE)
         .requires(Items.MELON_SLICE)
         .requires(Items.SUGAR)
         .requires(Items.MELON_SLICE)
         .requires(Items.MELON_SLICE)
         .requires(Items.GLASS_BOTTLE)
         .unlockedBy("has_melon_slice", TriggerInstance.hasItems(new ItemLike[]{Items.MELON_SLICE}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.WHEAT_DOUGH.get(), 3)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .requires(net.neoforged.neoforge.common.Tags.Items.EGGS)
         .group("fd_dough")
         .unlockedBy("has_wheat", TriggerInstance.hasItems(new ItemLike[]{Items.WHEAT}))
         .save(output, RecipeUtils.FDLocation("wheat_dough_from_egg"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.PIE_CRUST.get(), 1)
         .pattern("wMw")
         .pattern(" w ")
         .define('w', net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .define('M', net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK)
         .unlockedBy("has_wheat", TriggerInstance.hasItems(new ItemLike[]{Items.WHEAT}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.SWEET_BERRY_COOKIE.get(), 8)
         .requires(Items.SWEET_BERRIES)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .unlockedBy("has_sweet_berries", TriggerInstance.hasItems(new ItemLike[]{Items.SWEET_BERRIES}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.HONEY_COOKIE.get(), 8)
         .requires(Items.HONEY_BOTTLE)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .unlockedBy("has_honey_bottle", TriggerInstance.hasItems(new ItemLike[]{Items.HONEY_BOTTLE}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.CABBAGE.get())
         .requires((ItemLike)ModItems.CABBAGE_LEAF.get())
         .requires((ItemLike)ModItems.CABBAGE_LEAF.get())
         .unlockedBy("has_cabbage_leaf", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CABBAGE_LEAF.get()}))
         .group("fd_cabbage")
         .save(output, RecipeUtils.FDLocation("cabbage_from_leaves"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.HORSE_FEED.get(), 1)
         .requires(Ingredient.of(new ItemLike[]{Items.HAY_BLOCK, (ItemLike)ModItems.RICE_BALE.get()}))
         .requires(Items.APPLE)
         .requires(Items.APPLE)
         .requires(Items.GOLDEN_CARROT)
         .unlockedBy("has_golden_carrot", TriggerInstance.hasItems(new ItemLike[]{Items.GOLDEN_CARROT}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.MELON_POPSICLE.get(), 1)
         .pattern(" mm")
         .pattern("imm")
         .pattern("-i ")
         .define('m', Items.MELON_SLICE)
         .define('i', Items.ICE)
         .define('-', Items.STICK)
         .unlockedBy("has_melon", TriggerInstance.hasItems(new ItemLike[]{Items.MELON_SLICE}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.FRUIT_SALAD.get(), 1)
         .requires(Items.APPLE)
         .requires(Items.MELON_SLICE)
         .requires(Items.MELON_SLICE)
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BERRY)
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BERRY)
         .requires((ItemLike)ModItems.PUMPKIN_SLICE.get())
         .requires(Items.BOWL)
         .unlockedBy(
            "has_fruits", TriggerInstance.hasItems(new ItemLike[]{Items.MELON_SLICE, Items.SWEET_BERRIES, Items.APPLE, (ItemLike)ModItems.PUMPKIN_SLICE.get()})
         )
         .save(output);
   }

   private static void recipesFoodBlocks(RecipeOutput output) {
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.APPLE_PIE.get(), 1)
         .pattern("###")
         .pattern("aaa")
         .pattern("xOx")
         .define('#', net.neoforged.neoforge.common.Tags.Items.CROPS_WHEAT)
         .define('a', Items.APPLE)
         .define('x', Items.SUGAR)
         .define('O', (ItemLike)ModItems.PIE_CRUST.get())
         .unlockedBy("has_pie_crust", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PIE_CRUST.get()}))
         .group("fd_apple_pie")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.APPLE_PIE.get(), 1)
         .pattern("##")
         .pattern("##")
         .define('#', (ItemLike)ModItems.APPLE_PIE_SLICE.get())
         .unlockedBy("has_apple_pie_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.APPLE_PIE_SLICE.get()}))
         .group("fd_apple_pie")
         .save(output, RecipeUtils.FDLocation("apple_pie_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.SWEET_BERRY_CHEESECAKE.get(), 1)
         .pattern("sss")
         .pattern("sss")
         .pattern("mOm")
         .define('s', Items.SWEET_BERRIES)
         .define('m', net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK)
         .define('O', (ItemLike)ModItems.PIE_CRUST.get())
         .unlockedBy("has_pie_crust", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PIE_CRUST.get()}))
         .group("fd_sweet_berry_cheesecake")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.SWEET_BERRY_CHEESECAKE.get(), 1)
         .pattern("##")
         .pattern("##")
         .define('#', (ItemLike)ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get())
         .unlockedBy("has_sweet_berry_cheesecake_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get()}))
         .group("fd_sweet_berry_cheesecake")
         .save(output, RecipeUtils.FDLocation("sweet_berry_cheesecake_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.CHOCOLATE_PIE.get(), 1)
         .pattern("ccc")
         .pattern("mmm")
         .pattern("xOx")
         .define('c', Items.COCOA_BEANS)
         .define('m', net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK)
         .define('x', Items.SUGAR)
         .define('O', (ItemLike)ModItems.PIE_CRUST.get())
         .unlockedBy("has_pie_crust", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PIE_CRUST.get()}))
         .group("fd_chocolate_pie")
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.CHOCOLATE_PIE.get(), 1)
         .pattern("##")
         .pattern("##")
         .define('#', (ItemLike)ModItems.CHOCOLATE_PIE_SLICE.get())
         .unlockedBy("has_chocolate_pie_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.CHOCOLATE_PIE_SLICE.get()}))
         .group("fd_chocolate_pie")
         .save(output, RecipeUtils.FDLocation("chocolate_pie_from_slices"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.PUMPKIN_PIE, 2)
         .pattern("cec")
         .pattern("csc")
         .pattern(" O ")
         .define('c', (ItemLike)ModItems.PUMPKIN_SLICE.get())
         .define('e', net.neoforged.neoforge.common.Tags.Items.EGGS)
         .define('s', Items.SUGAR)
         .define('O', (ItemLike)ModItems.PIE_CRUST.get())
         .unlockedBy("has_pie_crust", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PIE_CRUST.get()}))
         .group("fd_pumpkin_pie")
         .save(output, RecipeUtils.FDLocation("pumpkin_pie_from_pie_crust"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Items.PUMPKIN_PIE, 1)
         .pattern("##")
         .pattern("##")
         .define('#', (ItemLike)ModItems.PUMPKIN_PIE_SLICE.get())
         .unlockedBy("has_pumpkin_pie_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.PUMPKIN_PIE_SLICE.get()}))
         .group("fd_pumpkin_pie")
         .save(output, RecipeUtils.FDLocation("pumpkin_pie_from_slices"));
   }

   private static void recipesCraftedMeals(RecipeOutput output) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.MIXED_SALAD.get())
         .requires(CommonTags.Items.FOODS_LEAFY_GREEN)
         .requires(CommonTags.Items.CROPS_TOMATO)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_BEETROOT)
         .requires(Items.BOWL)
         .unlockedBy("has_bowl", TriggerInstance.hasItems(new ItemLike[]{Items.BOWL}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.NETHER_SALAD.get())
         .requires(Items.CRIMSON_FUNGUS)
         .requires(Items.WARPED_FUNGUS)
         .requires(Items.BOWL)
         .unlockedBy("has_bowl", TriggerInstance.hasItems(new ItemLike[]{Items.BOWL}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.BARBECUE_STICK.get())
         .requires(CommonTags.Items.CROPS_TOMATO)
         .requires(CommonTags.Items.CROPS_ONION)
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_MEAT)
         .requires(Items.STICK)
         .unlockedBy("has_tomato", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.TOMATO.get()}))
         .unlockedBy("has_onion", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.ONION.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.EGG_SANDWICH.get())
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BREAD)
         .requires(CommonTags.Items.FOODS_COOKED_EGG)
         .requires(CommonTags.Items.FOODS_COOKED_EGG)
         .unlockedBy("has_fried_egg", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.FRIED_EGG.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.CHICKEN_SANDWICH.get())
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BREAD)
         .requires(CommonTags.Items.FOODS_COOKED_CHICKEN)
         .requires(CommonTags.Items.FOODS_LEAFY_GREEN)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_CARROT)
         .unlockedBy("has_cooked_chicken", TriggerInstance.hasItems(new ItemLike[]{Items.COOKED_CHICKEN}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.HAMBURGER.get())
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BREAD)
         .requires((ItemLike)ModItems.BEEF_PATTY.get())
         .requires(CommonTags.Items.FOODS_LEAFY_GREEN)
         .requires(CommonTags.Items.CROPS_TOMATO)
         .requires(CommonTags.Items.CROPS_ONION)
         .unlockedBy("has_beef_patty", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.BEEF_PATTY.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.BACON_SANDWICH.get())
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BREAD)
         .requires(CommonTags.Items.FOODS_COOKED_BACON)
         .requires(CommonTags.Items.FOODS_LEAFY_GREEN)
         .requires(CommonTags.Items.CROPS_TOMATO)
         .unlockedBy("has_bacon", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.COOKED_BACON.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.MUTTON_WRAP.get())
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BREAD)
         .requires(CommonTags.Items.FOODS_COOKED_MUTTON)
         .requires(CommonTags.Items.FOODS_LEAFY_GREEN)
         .requires(CommonTags.Items.CROPS_ONION)
         .unlockedBy("has_mutton", TriggerInstance.hasItems(new ItemLike[]{Items.COOKED_MUTTON}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.STUFFED_POTATO.get())
         .requires(Items.BAKED_POTATO)
         .requires(CommonTags.Items.FOODS_COOKED_BEEF)
         .requires(net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK)
         .unlockedBy("has_baked_potato", TriggerInstance.hasItems(new ItemLike[]{Items.BAKED_POTATO}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.SALMON_ROLL.get(), 2)
         .requires((ItemLike)ModItems.SALMON_SLICE.get())
         .requires((ItemLike)ModItems.SALMON_SLICE.get())
         .requires((ItemLike)ModItems.COOKED_RICE.get())
         .unlockedBy("has_salmon_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.SALMON_SLICE.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.COD_ROLL.get(), 2)
         .requires((ItemLike)ModItems.COD_SLICE.get())
         .requires((ItemLike)ModItems.COD_SLICE.get())
         .requires((ItemLike)ModItems.COOKED_RICE.get())
         .unlockedBy("has_cod_slice", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.COD_SLICE.get()}))
         .save(output);
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, (ItemLike)ModItems.KELP_ROLL.get(), 1)
         .pattern("RXR")
         .pattern("###")
         .define('#', Items.DRIED_KELP)
         .define('R', (ItemLike)ModItems.COOKED_RICE.get())
         .define('X', net.neoforged.neoforge.common.Tags.Items.FOODS_VEGETABLE)
         .unlockedBy("has_dried_kelp", TriggerInstance.hasItems(new ItemLike[]{Items.DRIED_KELP}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.GRILLED_SALMON.get())
         .requires(CommonTags.Items.FOODS_COOKED_SALMON)
         .requires(Items.SWEET_BERRIES)
         .requires(Items.BOWL)
         .requires(CommonTags.Items.CROPS_CABBAGE)
         .requires(CommonTags.Items.CROPS_ONION)
         .unlockedBy("has_salmon", TriggerInstance.hasItems(new ItemLike[]{Items.SALMON}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.STEAK_AND_POTATOES.get())
         .requires(Items.BAKED_POTATO)
         .requires(Items.COOKED_BEEF)
         .requires(Items.BOWL)
         .requires(CommonTags.Items.CROPS_ONION)
         .requires((ItemLike)ModItems.COOKED_RICE.get())
         .unlockedBy("has_baked_potato", TriggerInstance.hasItems(new ItemLike[]{Items.BAKED_POTATO}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.ROASTED_MUTTON_CHOPS.get())
         .requires((ItemLike)ModItems.COOKED_MUTTON_CHOPS.get())
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_BEETROOT)
         .requires(Items.BOWL)
         .requires((ItemLike)ModItems.COOKED_RICE.get())
         .requires(CommonTags.Items.CROPS_TOMATO)
         .unlockedBy("has_mutton", TriggerInstance.hasItems(new ItemLike[]{Items.COOKED_MUTTON}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.BACON_AND_EGGS.get())
         .requires(CommonTags.Items.FOODS_COOKED_BACON)
         .requires(CommonTags.Items.FOODS_COOKED_BACON)
         .requires(Items.BOWL)
         .requires(CommonTags.Items.FOODS_COOKED_EGG)
         .requires(CommonTags.Items.FOODS_COOKED_EGG)
         .unlockedBy("has_bacon", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.COOKED_BACON.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.ROAST_CHICKEN_BLOCK.get())
         .requires(CommonTags.Items.CROPS_ONION)
         .requires(net.neoforged.neoforge.common.Tags.Items.EGGS)
         .requires(net.neoforged.neoforge.common.Tags.Items.FOODS_BREAD)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_CARROT)
         .requires(Items.COOKED_CHICKEN)
         .requires(Items.BAKED_POTATO)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_CARROT)
         .requires(Items.BOWL)
         .requires(Items.BAKED_POTATO)
         .unlockedBy("has_cooked_chicken", TriggerInstance.hasItems(new ItemLike[]{Items.COOKED_CHICKEN}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.SHEPHERDS_PIE_BLOCK.get())
         .requires(Items.BAKED_POTATO)
         .requires(net.neoforged.neoforge.common.Tags.Items.DRINKS_MILK)
         .requires(Items.BAKED_POTATO)
         .requires(CommonTags.Items.FOODS_COOKED_MUTTON)
         .requires(CommonTags.Items.FOODS_COOKED_MUTTON)
         .requires(CommonTags.Items.FOODS_COOKED_MUTTON)
         .requires(CommonTags.Items.CROPS_ONION)
         .requires(Items.BOWL)
         .requires(CommonTags.Items.CROPS_ONION)
         .unlockedBy("has_cooked_mutton", TriggerInstance.hasItems(new ItemLike[]{Items.COOKED_MUTTON}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.HONEY_GLAZED_HAM_BLOCK.get())
         .requires(Items.SWEET_BERRIES)
         .requires(Items.HONEY_BOTTLE)
         .requires(Items.SWEET_BERRIES)
         .requires(Items.SWEET_BERRIES)
         .requires((ItemLike)ModItems.SMOKED_HAM.get())
         .requires(Items.SWEET_BERRIES)
         .requires((ItemLike)ModItems.COOKED_RICE.get())
         .requires(Items.BOWL)
         .requires((ItemLike)ModItems.COOKED_RICE.get())
         .unlockedBy("has_smoked_ham", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.SMOKED_HAM.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.GLEAMING_SALAD_BLOCK.get())
         .requires(Items.GLOW_BERRIES)
         .requires(Items.HONEY_BOTTLE)
         .requires(Items.GLOW_BERRIES)
         .requires(CommonTags.Items.CROPS_TOMATO)
         .requires(Items.GOLDEN_CARROT)
         .requires(net.neoforged.neoforge.common.Tags.Items.CROPS_BEETROOT)
         .requires((ItemLike)ModItems.CABBAGE.get())
         .requires(Items.BOWL)
         .requires((ItemLike)ModItems.CABBAGE.get())
         .unlockedBy("has_glow_berries", TriggerInstance.hasItems(new ItemLike[]{(ItemLike)ModItems.GLOW_BERRY_CUSTARD.get()}))
         .save(output);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.RICE_ROLL_MEDLEY_BLOCK.get())
         .requires((ItemLike)ModItems.KELP_ROLL_SLICE.get())
         .requires((ItemLike)ModItems.KELP_ROLL_SLICE.get())
         .requires((ItemLike)ModItems.KELP_ROLL_SLICE.get())
         .requires((ItemLike)ModItems.SALMON_ROLL.get())
         .requires((ItemLike)ModItems.SALMON_ROLL.get())
         .requires((ItemLike)ModItems.SALMON_ROLL.get())
         .requires((ItemLike)ModItems.COD_ROLL.get())
         .requires(Items.BOWL)
         .requires((ItemLike)ModItems.COD_ROLL.get())
         .unlockedBy(
            "has_rice_roll",
            TriggerInstance.hasItems(
               new ItemLike[]{(ItemLike)ModItems.SALMON_ROLL.get(), (ItemLike)ModItems.COD_ROLL.get(), (ItemLike)ModItems.KELP_ROLL_SLICE.get()}
            )
         )
         .save(output);
   }
}
