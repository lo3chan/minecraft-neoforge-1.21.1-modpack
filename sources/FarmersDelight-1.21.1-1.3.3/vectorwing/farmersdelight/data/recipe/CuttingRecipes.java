package vectorwing.farmersdelight.data.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.common.utility.RecipeUtils;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

public class CuttingRecipes {
   public static Ingredient KNIVES = matchesTool(KnifeItem.KNIFE_DIG, CommonTags.Items.TOOLS_KNIFE);
   public static Ingredient PICKAXES = matchesTool(ItemAbilities.PICKAXE_DIG, ItemTags.PICKAXES);
   public static Ingredient AXES = matchesTool(ItemAbilities.AXE_DIG, ItemTags.AXES);
   public static Ingredient AXES_STRIP = matchesTool(ItemAbilities.AXE_STRIP, ItemTags.AXES);
   public static Ingredient SHOVELS = matchesTool(ItemAbilities.SHOVEL_DIG, ItemTags.SHOVELS);
   public static Ingredient HOES = matchesTool(ItemAbilities.HOE_DIG, ItemTags.HOES);
   public static Ingredient SHEARS = matchesTool(ItemAbilities.SHEARS_DIG, Items.TOOLS_SHEAR);

   public static void register(RecipeOutput output) {
      cuttingAnimalItems(output);
      cuttingVegetables(output);
      cuttingFoods(output);
      cuttingFlowers(output);
      salvagingMinerals(output);
      strippingWood(output);
      salvagingWoodenFurniture(output);
      diggingSediments(output);
      salvagingUsingShears(output);
      salvagingBlockFromVehicle(output);
   }

   private static void cuttingAnimalItems(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BEEF}), KNIVES, (ItemLike)ModItems.MINCED_BEEF.get(), 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.PORKCHOP}), KNIVES, (ItemLike)ModItems.BACON.get(), 2)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CHICKEN}), KNIVES, (ItemLike)ModItems.CHICKEN_CUTS.get(), 2
         )
         .addResult(net.minecraft.world.item.Items.BONE_MEAL)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.COOKED_CHICKEN}), KNIVES, (ItemLike)ModItems.COOKED_CHICKEN_CUTS.get(), 2
         )
         .addResult(net.minecraft.world.item.Items.BONE_MEAL)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.COD}), KNIVES, (ItemLike)ModItems.COD_SLICE.get(), 2)
         .addResult(net.minecraft.world.item.Items.BONE_MEAL)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.COOKED_COD}), KNIVES, (ItemLike)ModItems.COOKED_COD_SLICE.get(), 2
         )
         .addResult(net.minecraft.world.item.Items.BONE_MEAL)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.SALMON}), KNIVES, (ItemLike)ModItems.SALMON_SLICE.get(), 2
         )
         .addResult(net.minecraft.world.item.Items.BONE_MEAL)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.COOKED_SALMON}), KNIVES, (ItemLike)ModItems.COOKED_SALMON_SLICE.get(), 2
         )
         .addResult(net.minecraft.world.item.Items.BONE_MEAL)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.HAM.get()}), KNIVES, net.minecraft.world.item.Items.PORKCHOP, 2)
         .addResult(net.minecraft.world.item.Items.BONE)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SMOKED_HAM.get()}), KNIVES, net.minecraft.world.item.Items.COOKED_PORKCHOP, 2
         )
         .addResult(net.minecraft.world.item.Items.BONE)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.MUTTON}), KNIVES, (ItemLike)ModItems.MUTTON_CHOPS.get(), 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.COOKED_MUTTON}), KNIVES, (ItemLike)ModItems.COOKED_MUTTON_CHOPS.get(), 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.INK_SAC}), KNIVES, net.minecraft.world.item.Items.BLACK_DYE, 2
         )
         .saveToFD(output);
   }

   private static void cuttingVegetables(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CABBAGE.get()}), KNIVES, (ItemLike)ModItems.CABBAGE_LEAF.get(), 2)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.RICE_PANICLE.get()}), KNIVES, (ItemLike)ModItems.RICE.get(), 1)
         .addResult((ItemLike)ModItems.STRAW.get())
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.MELON}), KNIVES, net.minecraft.world.item.Items.MELON_SLICE, 9
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.PUMPKIN}), KNIVES, (ItemLike)ModItems.PUMPKIN_SLICE.get(), 4
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.BROWN_MUSHROOM_COLONY.get()}), KNIVES, net.minecraft.world.item.Items.BROWN_MUSHROOM, 5
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.RED_MUSHROOM_COLONY.get()}), KNIVES, net.minecraft.world.item.Items.RED_MUSHROOM, 5
         )
         .saveToFD(output);
   }

   private static void cuttingFoods(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(CommonTags.Items.FOODS_DOUGH), KNIVES, (ItemLike)ModItems.RAW_PASTA.get(), 1)
         .save(output, RecipeUtils.FDLocation("cutting/tag_dough"));
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.KELP_ROLL.get()}), KNIVES, (ItemLike)ModItems.KELP_ROLL_SLICE.get(), 3
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CAKE}), KNIVES, (ItemLike)ModItems.CAKE_SLICE.get(), 7
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.APPLE_PIE.get()}), KNIVES, (ItemLike)ModItems.APPLE_PIE_SLICE.get(), 4
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SWEET_BERRY_CHEESECAKE.get()}), KNIVES, (ItemLike)ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get(), 4
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CHOCOLATE_PIE.get()}), KNIVES, (ItemLike)ModItems.CHOCOLATE_PIE_SLICE.get(), 4
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.PUMPKIN_PIE}), KNIVES, (ItemLike)ModItems.PUMPKIN_PIE_SLICE.get(), 4
         )
         .saveToFD(output);
   }

   private static void cuttingFlowers(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.WITHER_ROSE}), KNIVES, net.minecraft.world.item.Items.BLACK_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CORNFLOWER}), KNIVES, net.minecraft.world.item.Items.BLUE_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BLUE_ORCHID}), KNIVES, net.minecraft.world.item.Items.LIGHT_BLUE_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.AZURE_BLUET}), KNIVES, net.minecraft.world.item.Items.LIGHT_GRAY_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.OXEYE_DAISY}), KNIVES, net.minecraft.world.item.Items.LIGHT_GRAY_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.WHITE_TULIP}), KNIVES, net.minecraft.world.item.Items.LIGHT_GRAY_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.ALLIUM}), KNIVES, net.minecraft.world.item.Items.MAGENTA_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.ORANGE_TULIP}), KNIVES, net.minecraft.world.item.Items.ORANGE_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.PINK_TULIP}), KNIVES, net.minecraft.world.item.Items.PINK_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.RED_TULIP}), KNIVES, net.minecraft.world.item.Items.RED_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.POPPY}), KNIVES, net.minecraft.world.item.Items.RED_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.LILY_OF_THE_VALLEY}), KNIVES, net.minecraft.world.item.Items.WHITE_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.DANDELION}), KNIVES, net.minecraft.world.item.Items.YELLOW_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.TORCHFLOWER}), KNIVES, net.minecraft.world.item.Items.ORANGE_DYE, 2
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_BEETROOTS.get()}), KNIVES, net.minecraft.world.item.Items.BEETROOT_SEEDS, 1
         )
         .addResult(net.minecraft.world.item.Items.RED_DYE)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_CABBAGES.get()}), KNIVES, (ItemLike)ModItems.CABBAGE_SEEDS.get(), 1
         )
         .addResultWithChance(net.minecraft.world.item.Items.YELLOW_DYE, 0.5F, 2)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_CARROTS.get()}), KNIVES, net.minecraft.world.item.Items.CARROT, 1
         )
         .addResultWithChance(net.minecraft.world.item.Items.LIGHT_GRAY_DYE, 0.5F, 2)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_ONIONS.get()}), KNIVES, (ItemLike)ModItems.ONION.get(), 1)
         .addResult(net.minecraft.world.item.Items.MAGENTA_DYE, 2)
         .addResultWithChance(net.minecraft.world.item.Items.LIME_DYE, 0.1F)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_POTATOES.get()}), KNIVES, net.minecraft.world.item.Items.POTATO, 1
         )
         .addResultWithChance(net.minecraft.world.item.Items.PURPLE_DYE, 0.5F, 2)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_RICE.get()}), KNIVES, (ItemLike)ModItems.RICE.get(), 1)
         .addResultWithChance((ItemLike)ModItems.STRAW.get(), 0.5F)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WILD_TOMATOES.get()}), KNIVES, (ItemLike)ModItems.TOMATO_SEEDS.get(), 1
         )
         .addResultWithChance((ItemLike)ModItems.TOMATO.get(), 0.2F)
         .addResultWithChance(net.minecraft.world.item.Items.GREEN_DYE, 0.1F)
         .saveToFD(output);
   }

   private static void salvagingMinerals(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BRICKS}), PICKAXES, net.minecraft.world.item.Items.BRICK, 4
         )
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.NETHER_BRICKS}), PICKAXES, net.minecraft.world.item.Items.NETHER_BRICK, 4
         )
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.STONE}), PICKAXES, net.minecraft.world.item.Items.COBBLESTONE, 1
         )
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.DEEPSLATE}), PICKAXES, net.minecraft.world.item.Items.COBBLED_DEEPSLATE, 1
         )
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.QUARTZ_BLOCK}), PICKAXES, net.minecraft.world.item.Items.QUARTZ, 4
         )
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.AMETHYST_BLOCK}), PICKAXES, net.minecraft.world.item.Items.AMETHYST_SHARD, 4
         )
         .salvaging()
         .saveToFD(output);
   }

   private static void strippingWood(RecipeOutput output) {
      stripLogForBark(output, net.minecraft.world.item.Items.OAK_LOG, net.minecraft.world.item.Items.STRIPPED_OAK_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.OAK_WOOD, net.minecraft.world.item.Items.STRIPPED_OAK_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.SPRUCE_LOG, net.minecraft.world.item.Items.STRIPPED_SPRUCE_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.SPRUCE_WOOD, net.minecraft.world.item.Items.STRIPPED_SPRUCE_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.BIRCH_LOG, net.minecraft.world.item.Items.STRIPPED_BIRCH_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.BIRCH_WOOD, net.minecraft.world.item.Items.STRIPPED_BIRCH_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.JUNGLE_LOG, net.minecraft.world.item.Items.STRIPPED_JUNGLE_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.JUNGLE_WOOD, net.minecraft.world.item.Items.STRIPPED_JUNGLE_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.ACACIA_LOG, net.minecraft.world.item.Items.STRIPPED_ACACIA_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.ACACIA_WOOD, net.minecraft.world.item.Items.STRIPPED_ACACIA_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.DARK_OAK_LOG, net.minecraft.world.item.Items.STRIPPED_DARK_OAK_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.DARK_OAK_WOOD, net.minecraft.world.item.Items.STRIPPED_DARK_OAK_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.MANGROVE_LOG, net.minecraft.world.item.Items.STRIPPED_MANGROVE_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.MANGROVE_WOOD, net.minecraft.world.item.Items.STRIPPED_MANGROVE_WOOD);
      stripLogForBark(output, net.minecraft.world.item.Items.CHERRY_LOG, net.minecraft.world.item.Items.STRIPPED_CHERRY_LOG);
      stripLogForBark(output, net.minecraft.world.item.Items.CHERRY_WOOD, net.minecraft.world.item.Items.STRIPPED_CHERRY_WOOD);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BAMBOO_BLOCK}), AXES_STRIP, net.minecraft.world.item.Items.STRIPPED_BAMBOO_BLOCK
         )
         .addResult((ItemLike)ModItems.STRAW.get())
         .addSound(SoundEvents.AXE_STRIP)
         .saveToFD(output);
      stripLogForBark(output, net.minecraft.world.item.Items.CRIMSON_STEM, net.minecraft.world.item.Items.STRIPPED_CRIMSON_STEM);
      stripLogForBark(output, net.minecraft.world.item.Items.CRIMSON_HYPHAE, net.minecraft.world.item.Items.STRIPPED_CRIMSON_HYPHAE);
      stripLogForBark(output, net.minecraft.world.item.Items.WARPED_STEM, net.minecraft.world.item.Items.STRIPPED_WARPED_STEM);
      stripLogForBark(output, net.minecraft.world.item.Items.WARPED_HYPHAE, net.minecraft.world.item.Items.STRIPPED_WARPED_HYPHAE);
   }

   private static void salvagingWoodenFurniture(RecipeOutput output) {
      salvagePlankFromFurniture(
         output,
         WoodType.OAK,
         net.minecraft.world.item.Items.OAK_PLANKS,
         net.minecraft.world.item.Items.OAK_DOOR,
         net.minecraft.world.item.Items.OAK_TRAPDOOR,
         net.minecraft.world.item.Items.OAK_SIGN,
         net.minecraft.world.item.Items.OAK_HANGING_SIGN,
         net.minecraft.world.item.Items.OAK_FENCE,
         net.minecraft.world.item.Items.OAK_FENCE_GATE,
         net.minecraft.world.item.Items.OAK_PRESSURE_PLATE,
         net.minecraft.world.item.Items.OAK_BUTTON,
         net.minecraft.world.item.Items.OAK_BOAT,
         (ItemLike)ModItems.OAK_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.SPRUCE,
         net.minecraft.world.item.Items.SPRUCE_PLANKS,
         net.minecraft.world.item.Items.SPRUCE_DOOR,
         net.minecraft.world.item.Items.SPRUCE_TRAPDOOR,
         net.minecraft.world.item.Items.SPRUCE_SIGN,
         net.minecraft.world.item.Items.SPRUCE_HANGING_SIGN,
         net.minecraft.world.item.Items.SPRUCE_FENCE,
         net.minecraft.world.item.Items.SPRUCE_FENCE_GATE,
         net.minecraft.world.item.Items.SPRUCE_PRESSURE_PLATE,
         net.minecraft.world.item.Items.SPRUCE_BUTTON,
         net.minecraft.world.item.Items.SPRUCE_BOAT,
         (ItemLike)ModItems.SPRUCE_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.BIRCH,
         net.minecraft.world.item.Items.BIRCH_PLANKS,
         net.minecraft.world.item.Items.BIRCH_DOOR,
         net.minecraft.world.item.Items.BIRCH_TRAPDOOR,
         net.minecraft.world.item.Items.BIRCH_SIGN,
         net.minecraft.world.item.Items.BIRCH_HANGING_SIGN,
         net.minecraft.world.item.Items.BIRCH_FENCE,
         net.minecraft.world.item.Items.BIRCH_FENCE_GATE,
         net.minecraft.world.item.Items.BIRCH_PRESSURE_PLATE,
         net.minecraft.world.item.Items.BIRCH_BUTTON,
         net.minecraft.world.item.Items.BIRCH_BOAT,
         (ItemLike)ModItems.BIRCH_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.JUNGLE,
         net.minecraft.world.item.Items.JUNGLE_PLANKS,
         net.minecraft.world.item.Items.JUNGLE_DOOR,
         net.minecraft.world.item.Items.JUNGLE_TRAPDOOR,
         net.minecraft.world.item.Items.JUNGLE_SIGN,
         net.minecraft.world.item.Items.JUNGLE_HANGING_SIGN,
         net.minecraft.world.item.Items.JUNGLE_FENCE,
         net.minecraft.world.item.Items.JUNGLE_FENCE_GATE,
         net.minecraft.world.item.Items.JUNGLE_PRESSURE_PLATE,
         net.minecraft.world.item.Items.JUNGLE_BUTTON,
         net.minecraft.world.item.Items.JUNGLE_BOAT,
         (ItemLike)ModItems.JUNGLE_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.ACACIA,
         net.minecraft.world.item.Items.ACACIA_PLANKS,
         net.minecraft.world.item.Items.ACACIA_DOOR,
         net.minecraft.world.item.Items.ACACIA_TRAPDOOR,
         net.minecraft.world.item.Items.ACACIA_SIGN,
         net.minecraft.world.item.Items.ACACIA_HANGING_SIGN,
         net.minecraft.world.item.Items.ACACIA_FENCE,
         net.minecraft.world.item.Items.ACACIA_FENCE_GATE,
         net.minecraft.world.item.Items.ACACIA_PRESSURE_PLATE,
         net.minecraft.world.item.Items.ACACIA_BUTTON,
         net.minecraft.world.item.Items.ACACIA_BOAT,
         (ItemLike)ModItems.ACACIA_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.DARK_OAK,
         net.minecraft.world.item.Items.DARK_OAK_PLANKS,
         net.minecraft.world.item.Items.DARK_OAK_DOOR,
         net.minecraft.world.item.Items.DARK_OAK_TRAPDOOR,
         net.minecraft.world.item.Items.DARK_OAK_SIGN,
         net.minecraft.world.item.Items.DARK_OAK_HANGING_SIGN,
         net.minecraft.world.item.Items.DARK_OAK_FENCE,
         net.minecraft.world.item.Items.DARK_OAK_FENCE_GATE,
         net.minecraft.world.item.Items.DARK_OAK_PRESSURE_PLATE,
         net.minecraft.world.item.Items.DARK_OAK_BUTTON,
         net.minecraft.world.item.Items.DARK_OAK_BOAT,
         (ItemLike)ModItems.DARK_OAK_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.MANGROVE,
         net.minecraft.world.item.Items.MANGROVE_PLANKS,
         net.minecraft.world.item.Items.MANGROVE_DOOR,
         net.minecraft.world.item.Items.MANGROVE_TRAPDOOR,
         net.minecraft.world.item.Items.MANGROVE_SIGN,
         net.minecraft.world.item.Items.MANGROVE_HANGING_SIGN,
         net.minecraft.world.item.Items.MANGROVE_FENCE,
         net.minecraft.world.item.Items.MANGROVE_FENCE_GATE,
         net.minecraft.world.item.Items.MANGROVE_PRESSURE_PLATE,
         net.minecraft.world.item.Items.MANGROVE_BUTTON,
         net.minecraft.world.item.Items.MANGROVE_BOAT,
         (ItemLike)ModItems.MANGROVE_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.CHERRY,
         net.minecraft.world.item.Items.CHERRY_PLANKS,
         net.minecraft.world.item.Items.CHERRY_DOOR,
         net.minecraft.world.item.Items.CHERRY_TRAPDOOR,
         net.minecraft.world.item.Items.CHERRY_SIGN,
         net.minecraft.world.item.Items.CHERRY_HANGING_SIGN,
         net.minecraft.world.item.Items.CHERRY_FENCE,
         net.minecraft.world.item.Items.CHERRY_FENCE_GATE,
         net.minecraft.world.item.Items.CHERRY_PRESSURE_PLATE,
         net.minecraft.world.item.Items.CHERRY_BUTTON,
         net.minecraft.world.item.Items.CHERRY_BOAT,
         (ItemLike)ModItems.CHERRY_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.BAMBOO,
         net.minecraft.world.item.Items.BAMBOO_PLANKS,
         net.minecraft.world.item.Items.BAMBOO_DOOR,
         net.minecraft.world.item.Items.BAMBOO_TRAPDOOR,
         net.minecraft.world.item.Items.BAMBOO_SIGN,
         net.minecraft.world.item.Items.BAMBOO_HANGING_SIGN,
         net.minecraft.world.item.Items.BAMBOO_FENCE,
         net.minecraft.world.item.Items.BAMBOO_FENCE_GATE,
         net.minecraft.world.item.Items.BAMBOO_PRESSURE_PLATE,
         net.minecraft.world.item.Items.BAMBOO_BUTTON,
         net.minecraft.world.item.Items.BAMBOO_RAFT,
         (ItemLike)ModItems.BAMBOO_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.CRIMSON,
         net.minecraft.world.item.Items.CRIMSON_PLANKS,
         net.minecraft.world.item.Items.CRIMSON_DOOR,
         net.minecraft.world.item.Items.CRIMSON_TRAPDOOR,
         net.minecraft.world.item.Items.CRIMSON_SIGN,
         net.minecraft.world.item.Items.CRIMSON_HANGING_SIGN,
         net.minecraft.world.item.Items.CRIMSON_FENCE,
         net.minecraft.world.item.Items.CRIMSON_FENCE_GATE,
         net.minecraft.world.item.Items.CRIMSON_PRESSURE_PLATE,
         net.minecraft.world.item.Items.CRIMSON_BUTTON,
         (ItemLike)ModItems.CRIMSON_CABINET.get()
      );
      salvagePlankFromFurniture(
         output,
         WoodType.WARPED,
         net.minecraft.world.item.Items.WARPED_PLANKS,
         net.minecraft.world.item.Items.WARPED_DOOR,
         net.minecraft.world.item.Items.WARPED_TRAPDOOR,
         net.minecraft.world.item.Items.WARPED_SIGN,
         net.minecraft.world.item.Items.WARPED_HANGING_SIGN,
         net.minecraft.world.item.Items.WARPED_FENCE,
         net.minecraft.world.item.Items.WARPED_FENCE_GATE,
         net.minecraft.world.item.Items.WARPED_PRESSURE_PLATE,
         net.minecraft.world.item.Items.WARPED_BUTTON,
         (ItemLike)ModItems.WARPED_CABINET.get()
      );
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WOODEN_BASKET.get()}), AXES, (ItemLike)ModItems.CANVAS.get())
         .addResult(net.minecraft.world.item.Items.STICK)
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.BAMBOO_BASKET.get()}), AXES, (ItemLike)ModItems.CANVAS.get())
         .addResult(net.minecraft.world.item.Items.BAMBOO)
         .saveToFD(output);
   }

   private static void diggingSediments(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CLAY}), SHOVELS, net.minecraft.world.item.Items.CLAY_BALL, 4
         )
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.GRAVEL}), SHOVELS, net.minecraft.world.item.Items.GRAVEL, 1
         )
         .addResultWithChance(net.minecraft.world.item.Items.FLINT, 0.1F)
         .saveToFD(output);
   }

   private static void salvagingUsingShears(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.SADDLE}), SHEARS, net.minecraft.world.item.Items.LEATHER, 2
         )
         .addResultWithChance(net.minecraft.world.item.Items.IRON_NUGGET, 0.5F, 2)
         .save(output, salvagingRecipe("saddle"));
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.LEATHER_HORSE_ARMOR}), SHEARS, net.minecraft.world.item.Items.LEATHER, 2
         )
         .save(output, salvagingRecipe("leather_horse_armor"));
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(
               new ItemLike[]{
                  net.minecraft.world.item.Items.LEATHER_HELMET,
                  net.minecraft.world.item.Items.LEATHER_CHESTPLATE,
                  net.minecraft.world.item.Items.LEATHER_LEGGINGS,
                  net.minecraft.world.item.Items.LEATHER_BOOTS
               }
            ),
            SHEARS,
            net.minecraft.world.item.Items.LEATHER,
            1
         )
         .save(output, salvagingRecipe("leather_armor"));
   }

   private static void salvagingBlockFromVehicle(RecipeOutput output) {
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CHEST_MINECART}), HOES, net.minecraft.world.item.Items.MINECART
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .addSound(SoundEvents.METAL_BREAK)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.FURNACE_MINECART}), HOES, net.minecraft.world.item.Items.MINECART
         )
         .addResult(net.minecraft.world.item.Items.FURNACE)
         .addSound(SoundEvents.METAL_BREAK)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.HOPPER_MINECART}), HOES, net.minecraft.world.item.Items.MINECART
         )
         .addResult(net.minecraft.world.item.Items.HOPPER)
         .addSound(SoundEvents.METAL_BREAK)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.TNT_MINECART}), HOES, net.minecraft.world.item.Items.MINECART
         )
         .addResult(net.minecraft.world.item.Items.TNT)
         .addSound(SoundEvents.METAL_BREAK)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.OAK_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.OAK_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.SPRUCE_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.SPRUCE_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BIRCH_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.BIRCH_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.JUNGLE_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.JUNGLE_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.ACACIA_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.ACACIA_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.DARK_OAK_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.DARK_OAK_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.MANGROVE_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.MANGROVE_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.CHERRY_CHEST_BOAT}), HOES, net.minecraft.world.item.Items.CHERRY_BOAT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
      CuttingBoardRecipeBuilder.cuttingRecipe(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.BAMBOO_CHEST_RAFT}), HOES, net.minecraft.world.item.Items.BAMBOO_RAFT
         )
         .addResult(net.minecraft.world.item.Items.CHEST)
         .salvaging()
         .saveToFD(output);
   }

   private static void salvagePlankFromFurniture(RecipeOutput output, WoodType woodType, ItemLike plank, ItemLike... furniture) {
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(furniture), AXES, plank, 1, 0.75F).save(output, salvagingRecipe(woodType.name() + "_furniture"));
   }

   private static void stripLogForBark(RecipeOutput output, ItemLike log, ItemLike strippedLog) {
      CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(new ItemLike[]{log}), AXES_STRIP, strippedLog)
         .addResult((ItemLike)ModItems.TREE_BARK.get())
         .addSound(SoundEvents.AXE_STRIP)
         .saveToFD(output);
   }

   private static Ingredient matchesTool(ItemAbility toolAction, TagKey<Item> fallbackTag) {
      return CompoundIngredient.of(new Ingredient[]{new ItemAbilityIngredient(toolAction).toVanilla(), Ingredient.of(fallbackTag)});
   }

   private static ResourceLocation salvagingRecipe(String name) {
      return ResourceLocation.fromNamespaceAndPath("farmersdelight", "salvaging/" + name);
   }
}
