package net.astralya.hexalia.neoforge.datagen;

import java.util.concurrent.CompletableFuture;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.neoforge.datagen.custom.CelestialInfusionRecipeBuilder;
import net.astralya.hexalia.neoforge.datagen.custom.MortarAndPestleRecipeBuilder;
import net.astralya.hexalia.neoforge.datagen.custom.MutationRecipeBuilder;
import net.astralya.hexalia.neoforge.datagen.custom.NaturesRitualRecipeBuilder;
import net.astralya.hexalia.neoforge.datagen.custom.SmallCauldronRecipeBuilder;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

public final class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
   public ModRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries);
   }

   protected void buildRecipes(RecipeOutput recipeOutput) {
      this.buildCraftingRecipes(recipeOutput);
      this.buildCelestialInfusionRecipes(recipeOutput);
      this.buildNaturesRitualRecipes(recipeOutput);
      this.buildSmallCauldronRecipes(recipeOutput);
      this.buildMortarAndPestleRecipes(recipeOutput);
      this.buildCenserRecipes(recipeOutput);
      this.buildCropRecipes(recipeOutput);
      this.buildMutationRecipes(recipeOutput);
   }

   private void buildCraftingRecipes(RecipeOutput recipeOutput) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.SMALL_CAULDRON.get())
         .pattern("D D")
         .pattern("DCD")
         .pattern("LLL")
         .define('C', ItemTags.COALS)
         .define('D', Items.COBBLED_DEEPSLATE)
         .define('L', ItemTags.LOGS)
         .unlockedBy("has_cobbled_deepslate", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.COBBLED_DEEPSLATE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, (ItemLike)ModItems.RUSTIC_BOTTLE.get(), 3)
         .pattern("S S")
         .pattern(" P ")
         .define('P', Items.CLAY_BALL)
         .define('S', Blocks.GLASS)
         .unlockedBy("has_clay_ball", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.CLAY_BALL}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.LADLE.get())
         .pattern("  B")
         .pattern(" S ")
         .pattern("S  ")
         .define('S', Items.STICK)
         .define('B', Items.BOWL)
         .unlockedBy("has_stick", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STICK}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.ATHAME.get())
         .pattern(" S")
         .pattern("P ")
         .define('S', Items.FLINT)
         .define('P', Items.STICK)
         .unlockedBy("has_stick", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STICK}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.SILK_IDOL.get())
         .pattern(" S ")
         .pattern("SPS")
         .pattern(" S ")
         .define('S', (ItemLike)ModItems.SILK_FIBER.get())
         .define('P', ModTags.Items.CRUSHED_HERBS)
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.VERDANT_GRIMOIRE.get())
         .requires(Items.BOOK)
         .requires(ModTags.Items.HERBS)
         .unlockedBy("has_book", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BOOK}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, (ItemLike)ModItems.PURIFYING_SAC.get())
         .requires((ItemLike)ModItems.SALT.get())
         .requires((ItemLike)ModItems.LOTUS_BLOSSOM.get())
         .requires(Items.LEATHER)
         .unlockedBy("has_salt", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SALT.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, (ItemLike)ModItems.FROST_SAC.get())
         .requires(Items.SNOWBALL)
         .requires((ItemLike)ModItems.CHILLBERRIES.get())
         .requires(Items.LEATHER)
         .unlockedBy(
            "has_chillberries", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.CHILLBERRIES.get()}).build()})
         )
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, (ItemLike)ModItems.FOUL_SAC.get())
         .requires(Items.SPIDER_EYE)
         .requires((ItemLike)ModBlocks.WITCHWEED.get())
         .requires(Items.LEATHER)
         .unlockedBy("has_witchweed", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModBlocks.WITCHWEED.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, (ItemLike)ModItems.SEARING_SAC.get())
         .requires((ItemLike)ModItems.RABBAGE.get())
         .requires((ItemLike)ModItems.SUNFIRE_TOMATO.get())
         .requires(Items.LEATHER)
         .unlockedBy(
            "has_sunfire_tomato", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SUNFIRE_TOMATO.get()}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.GHOSTVEIL.get())
         .pattern("LLL")
         .pattern("FSF")
         .pattern("F F")
         .define('L', Items.LEATHER)
         .define('F', (ItemLike)ModItems.GHOST_FERN.get())
         .define('S', (ItemLike)ModItems.SILK_FIBER.get())
         .unlockedBy("has_ghost_fern", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.GHOST_FERN.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.EARPLUGS.get())
         .pattern("P P")
         .define('P', Items.LEATHER)
         .unlockedBy("has_leather", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.LEATHER}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.BOGSHADE_BOOTS.get())
         .pattern("SWS")
         .pattern("K K")
         .define('S', (ItemLike)ModItems.SILK_FIBER.get())
         .define('W', (ItemLike)ModItems.WATER_NODE.get())
         .define('K', Items.KELP)
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput);
      silkweaveArmorRecipe(recipeOutput, (Item)ModItems.SILKWEAVE_HOOD.get(), Items.LEATHER_HELMET, " S ", "SLS", " W ");
      silkweaveArmorRecipe(recipeOutput, (Item)ModItems.SILKWEAVE_MANTLE.get(), Items.LEATHER_CHESTPLATE, "TWT", "SLS", " S ");
      silkweaveArmorRecipe(recipeOutput, (Item)ModItems.SILKWEAVE_BINDINGS.get(), Items.LEATHER_LEGGINGS, " S ", "SLS", "TWT");
      silkweaveArmorRecipe(recipeOutput, (Item)ModItems.SILKWEAVE_FOOTWRAPS.get(), Items.LEATHER_BOOTS, " W ", "SLS", "TST");
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.THORNBOW.get())
         .pattern(" SF")
         .pattern("REF")
         .pattern(" SF")
         .define('S', Items.STICK)
         .define('E', (ItemLike)ModItems.EARTH_NODE.get())
         .define('R', (ItemLike)ModItems.RABBAGE.get())
         .define('F', Items.STRING)
         .unlockedBy("has_earth_node", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)ModItems.BRIAR_SICKLE.get())
         .pattern(" SS")
         .pattern("RE ")
         .pattern(" S ")
         .define('S', Items.STICK)
         .define('E', (ItemLike)ModItems.EARTH_NODE.get())
         .define('R', (ItemLike)ModItems.RABBAGE.get())
         .unlockedBy("has_earth_node", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)ModItems.SPIRITROOT_TETHER.get())
         .pattern("ES ")
         .pattern("SP ")
         .pattern("  S")
         .define('S', Items.STRING)
         .define('E', (ItemLike)ModItems.EARTH_NODE.get())
         .define('P', Items.ENDER_PEARL)
         .unlockedBy("has_earth_node", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.HEX_FOCUS.get())
         .pattern("  S")
         .pattern(" P ")
         .pattern("A  ")
         .define('P', ItemTags.LEAVES)
         .define('S', Items.AMETHYST_SHARD)
         .define('A', Items.STICK)
         .unlockedBy("has_amethyst_shard", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.AMETHYST_SHARD}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.INFUSED_DIRT.get(), 2)
         .pattern("SP")
         .pattern("PS")
         .define('S', (ItemLike)ModItems.SIREN_KELP.get())
         .define('P', Blocks.DIRT)
         .unlockedBy("has_siren_kelp", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SIREN_KELP.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.SALT_LAMP.get())
         .pattern(" A ")
         .pattern(" P ")
         .pattern(" S ")
         .define('A', Items.COPPER_INGOT)
         .define('P', Items.TORCH)
         .define('S', ModTags.Items.SALT)
         .unlockedBy("has_salt", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SALT.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.RITUAL_TABLE.get())
         .pattern("DCD")
         .pattern(" D ")
         .pattern("DDD")
         .define('D', Blocks.DEEPSLATE)
         .define('C', Blocks.MOSS_CARPET)
         .unlockedBy("has_deepslate", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Blocks.DEEPSLATE}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)ModItems.NESTING_BLOCK.get())
         .pattern("SSS")
         .pattern("PNP")
         .pattern("PPP")
         .define('S', Items.STRING)
         .define('P', ItemTags.PLANKS)
         .define('N', ItemTags.LEAVES)
         .unlockedBy("has_string", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STRING}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.SHELF.get())
         .pattern(" P ")
         .pattern("S S")
         .define('P', Items.COBBLED_DEEPSLATE_SLAB)
         .define('S', Items.STICK)
         .unlockedBy(
            "has_cobbled_deepslate_slab", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.COBBLED_DEEPSLATE_SLAB}).build()})
         )
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.DREAMCATCHER.get())
         .pattern(" S ")
         .pattern("SPS")
         .pattern("ATA")
         .define('S', Items.STICK)
         .define('A', Items.FEATHER)
         .define('P', Items.STRING)
         .define('T', (ItemLike)ModItems.FIRE_NODE.get())
         .unlockedBy("has_stick", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.STICK}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.CANDLE_SKULL.get())
         .pattern("P")
         .pattern("S")
         .define('P', Items.CANDLE)
         .define('S', Items.SKELETON_SKULL)
         .unlockedBy("has_skeleton_skull", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.SKELETON_SKULL}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.WITHER_CANDLE_SKULL.get())
         .pattern("P")
         .pattern("S")
         .define('P', Items.CANDLE)
         .define('S', Items.WITHER_SKELETON_SKULL)
         .unlockedBy("has_wither_skeleton_skull", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.WITHER_SKELETON_SKULL}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.COBWEB)
         .pattern(" S ")
         .pattern("SPS")
         .pattern(" S ")
         .define('P', (ItemLike)ModItems.SILK_FIBER.get())
         .define('S', Items.STRING)
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput, id("cobweb_from_fiber"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.SILK_FIBER.get(), 2)
         .requires((ItemLike)ModItems.SILKWORM.get())
         .requires(ItemTags.LEAVES)
         .unlockedBy("has_silkworm", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILKWORM.get()}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.LEATHER)
         .pattern(" S ")
         .pattern("SPS")
         .pattern(" S ")
         .define('P', Items.ROTTEN_FLESH)
         .define('S', ModTags.Items.SALT)
         .unlockedBy("has_salt", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SALT.get()}).build()}))
         .save(recipeOutput, id("leather_from_salt"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)ModItems.CELESTIAL_CRYSTAL_BLOCK.get())
         .pattern("PP")
         .pattern("PP")
         .define('P', (ItemLike)ModItems.CELESTIAL_CRYSTAL.get())
         .unlockedBy(
            "has_celestial_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.CELESTIAL_CRYSTAL.get(), 4)
         .requires((ItemLike)ModItems.CELESTIAL_CRYSTAL_BLOCK.get())
         .unlockedBy(
            "has_celestial_crystal_block",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL_BLOCK.get()}).build()})
         )
         .save(recipeOutput, id("celestial_crystal_from_block"));
      nineBlockStorageRecipes(
         recipeOutput,
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)ModItems.SALT.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)ModItems.SALT_BLOCK.get(),
         "hexalia:salt",
         "salt",
         "hexalia:salt_block",
         "salt"
      );
      woodSetRecipes(
         recipeOutput,
         "cottonwood",
         (Item)ModItems.COTTONWOOD_LOG.get(),
         (Item)ModItems.COTTONWOOD_WOOD.get(),
         (Item)ModItems.STRIPPED_COTTONWOOD_LOG.get(),
         (Item)ModItems.STRIPPED_COTTONWOOD_WOOD.get(),
         (Item)ModItems.COTTONWOOD_PLANKS.get(),
         (Item)ModItems.COTTONWOOD_STAIRS.get(),
         (Item)ModItems.COTTONWOOD_SLAB.get(),
         (Item)ModItems.COTTONWOOD_BUTTON.get(),
         (Item)ModItems.COTTONWOOD_PRESSURE_PLATE.get(),
         (Item)ModItems.COTTONWOOD_FENCE.get(),
         (Item)ModItems.COTTONWOOD_FENCE_GATE.get(),
         (Item)ModItems.COTTONWOOD_TRAPDOOR.get(),
         (Item)ModItems.COTTONWOOD_DOOR.get(),
         (Item)ModItems.COTTONWOOD_SIGN.get(),
         (Item)ModItems.COTTONWOOD_HANGING_SIGN.get(),
         ModTags.Items.COTTONWOOD_LOGS
      );
      boatRecipes(
         recipeOutput, "cottonwood", (Item)ModItems.COTTONWOOD_BOAT.get(), (Item)ModItems.COTTONWOOD_CHEST_BOAT.get(), (Item)ModItems.COTTONWOOD_PLANKS.get()
      );
      woodSetRecipes(
         recipeOutput,
         "willow",
         (Item)ModItems.WILLOW_LOG.get(),
         (Item)ModItems.WILLOW_WOOD.get(),
         (Item)ModItems.STRIPPED_WILLOW_LOG.get(),
         (Item)ModItems.STRIPPED_WILLOW_WOOD.get(),
         (Item)ModItems.WILLOW_PLANKS.get(),
         (Item)ModItems.WILLOW_STAIRS.get(),
         (Item)ModItems.WILLOW_SLAB.get(),
         (Item)ModItems.WILLOW_BUTTON.get(),
         (Item)ModItems.WILLOW_PRESSURE_PLATE.get(),
         (Item)ModItems.WILLOW_FENCE.get(),
         (Item)ModItems.WILLOW_FENCE_GATE.get(),
         (Item)ModItems.WILLOW_TRAPDOOR.get(),
         (Item)ModItems.WILLOW_DOOR.get(),
         (Item)ModItems.WILLOW_SIGN.get(),
         (Item)ModItems.WILLOW_HANGING_SIGN.get(),
         ModTags.Items.WILLOW_LOGS
      );
      boatRecipes(recipeOutput, "willow", (Item)ModItems.WILLOW_BOAT.get(), (Item)ModItems.WILLOW_CHEST_BOAT.get(), (Item)ModItems.WILLOW_PLANKS.get());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, (ItemLike)ModItems.CLARITY_IDOL.get())
         .requires((ItemLike)ModItems.SILK_IDOL.get())
         .requires((ItemLike)ModItems.AIR_NODE.get())
         .requires((ItemLike)ModItems.CELESTIAL_CRYSTAL.get())
         .requires(Items.SUNFLOWER)
         .unlockedBy("has_silk_idol", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_IDOL.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, (ItemLike)ModItems.RAINFALL_IDOL.get())
         .requires((ItemLike)ModItems.SILK_IDOL.get())
         .requires((ItemLike)ModItems.WATER_NODE.get())
         .requires((ItemLike)ModItems.CELESTIAL_CRYSTAL.get())
         .requires(Blocks.BLUE_ORCHID)
         .unlockedBy("has_silk_idol", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_IDOL.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, (ItemLike)ModItems.TEMPEST_IDOL.get())
         .requires((ItemLike)ModItems.SILK_IDOL.get())
         .requires((ItemLike)ModItems.WATER_NODE.get())
         .requires((ItemLike)ModItems.FIRE_NODE.get())
         .requires((ItemLike)ModItems.CELESTIAL_CRYSTAL.get())
         .unlockedBy("has_silk_idol", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_IDOL.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, (ItemLike)ModItems.PURITY_IDOL.get())
         .requires((ItemLike)ModItems.SILK_IDOL.get())
         .requires((ItemLike)ModItems.WATER_NODE.get())
         .requires((ItemLike)ModItems.LOTUS_BLOSSOM.get())
         .requires((ItemLike)ModItems.SALT.get())
         .unlockedBy("has_silk_idol", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_IDOL.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE)
         .requires((ItemLike)ModItems.LAVENDER.get())
         .unlockedBy("has_lavender", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.LAVENDER.get()}).build()}))
         .save(recipeOutput, id("purple_dye_from_begonia"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PINK_DYE)
         .requires((ItemLike)ModItems.BEGONIA.get())
         .unlockedBy("has_begonia", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.BEGONIA.get()}).build()}))
         .save(recipeOutput, id("pink_dye_from_begonia"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLACK_DYE)
         .requires((ItemLike)ModItems.NIGHTSHADE_BUSH.get())
         .unlockedBy(
            "has_nightshade_bush", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.NIGHTSHADE_BUSH.get()}).build()})
         )
         .save(recipeOutput, id("black_dye_from_nightshade"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ORANGE_DYE)
         .requires((ItemLike)ModItems.DAHLIA.get())
         .unlockedBy("has_dahlia", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.DAHLIA.get()}).build()}))
         .save(recipeOutput, id("orange_dye_from_dahlia"));
   }

   private void buildCelestialInfusionRecipes(RecipeOutput recipeOutput) {
      CelestialInfusionRecipeBuilder.infusion(RecipeCategory.FOOD, Ingredient.of(new ItemLike[]{Items.GLOW_BERRIES}), (ItemLike)ModItems.GALEBERRIES.get())
         .unlockedBy("has_glow_berries", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.GLOW_BERRIES}).build()}))
         .save(recipeOutput, id("galeberries_from_celestial_infusion"));
      CelestialInfusionRecipeBuilder.infusion(
            RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.AMETHYST_SHARD}), (ItemLike)ModItems.CELESTIAL_CRYSTAL.get()
         )
         .unlockedBy("has_amethyst_shard", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.AMETHYST_SHARD}).build()}))
         .save(recipeOutput, id("celestial_crystal_from_celestial_infusion"));
      CelestialInfusionRecipeBuilder.infusion(
            RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_HOOD.get()}), (ItemLike)ModItems.MOONWEAVE_HOOD.get()
         )
         .unlockedBy(
            "has_silkweave_hood", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_HOOD.get()}).build()})
         )
         .save(recipeOutput, id("moonweave_hood_from_celestial_infusion"));
      CelestialInfusionRecipeBuilder.infusion(
            RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_MANTLE.get()}), (ItemLike)ModItems.MOONWEAVE_MANTLE.get()
         )
         .unlockedBy(
            "has_silkweave_mantle", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_MANTLE.get()}).build()})
         )
         .save(recipeOutput, id("moonweave_mantle_from_celestial_infusion"));
      CelestialInfusionRecipeBuilder.infusion(
            RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_BINDINGS.get()}), (ItemLike)ModItems.MOONWEAVE_BINDINGS.get()
         )
         .unlockedBy(
            "has_silkweave_bindings",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_BINDINGS.get()}).build()})
         )
         .save(recipeOutput, id("moonweave_bindings_from_celestial_infusion"));
      CelestialInfusionRecipeBuilder.infusion(
            RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_FOOTWRAPS.get()}), (ItemLike)ModItems.MOONWEAVE_FOOTWRAPS.get()
         )
         .unlockedBy(
            "has_silkweave_footwraps",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILKWEAVE_FOOTWRAPS.get()}).build()})
         )
         .save(recipeOutput, id("moonweave_footwraps_from_celestial_infusion"));
   }

   private void buildNaturesRitualRecipes(RecipeOutput recipeOutput) {
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.DIAMOND}), Items.NETHER_STAR)
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.ECHO_SHARD}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.EMERALD}))
         .unlockedBy("has_diamond", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.DIAMOND}).build()}))
         .save(recipeOutput, id("debug_natures_ritual"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.AMETHYST_SHARD}), (ItemLike)ModItems.FIRE_NODE.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.COAL}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.SUNFLOWER}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("fire_node_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.AMETHYST_SHARD}), (ItemLike)ModItems.AIR_NODE.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.FEATHER}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.DANDELION}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("air_node_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.AMETHYST_SHARD}), (ItemLike)ModItems.WATER_NODE.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.LILY_PAD}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.INK_SAC}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("water_node_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.AMETHYST_SHARD}), (ItemLike)ModItems.EARTH_NODE.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.CLAY_BALL}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Blocks.BROWN_MUSHROOM}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("earth_node_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.LILY_OF_THE_VALLEY}), (ItemLike)ModItems.ASTRYLIS.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.BONE_MEAL}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.GLOWSTONE_DUST}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("astrylis_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(
            RecipeCategory.TOOLS, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.ANCIENT_SEED.get()}), (ItemLike)ModItems.KELPWEAVE_BLADE.get()
         )
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WATER_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.WOODEN_SWORD}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.KELP}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SIREN_PASTE.get()}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("kelpweave_blade_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(
            RecipeCategory.TOOLS, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.ANCIENT_SEED.get()}), (ItemLike)ModItems.ROOTSHAPER.get()
         )
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.WOODEN_PICKAXE}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.WOODEN_SHOVEL}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DREAM_PASTE.get()}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("rootshaper_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(
            RecipeCategory.TOOLS, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL.get()}), (ItemLike)ModItems.SAGE_PENDANT.get()
         )
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.GOLD_NUGGET}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.BOOK}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.EXPERIENCE_BOTTLE}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_POWDER.get()}))
         .unlockedBy(
            "has_celestial_crystal",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL.get()}).build()})
         )
         .save(recipeOutput, id("sage_pendant_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{Items.LEATHER_HELMET}), (ItemLike)ModItems.BLOOMWRAP_HAT.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.PINK_TULIP}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.MANDRAKE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.ROOTED_DIRT}))
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput, id("bloomwrap_hat_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(
            RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{Items.LEATHER_CHESTPLATE}), (ItemLike)ModItems.BLOOMWRAP_ROBES.get()
         )
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.MOSS_BLOCK}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.IRON_NUGGET}))
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput, id("bloomwrap_robes_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(
            RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{Items.LEATHER_LEGGINGS}), (ItemLike)ModItems.BLOOMWRAP_LEGGINGS.get()
         )
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.PEONY}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_BLOOM.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.HONEYCOMB}))
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput, id("bloomwrap_leggings_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.COMBAT, Ingredient.of(new ItemLike[]{Items.LEATHER_BOOTS}), (ItemLike)ModItems.BLOOMWRAP_BOOTS.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.DANDELION}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.AIR_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.SUGAR}))
         .unlockedBy("has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()}))
         .save(recipeOutput, id("bloomwrap_boots_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.AZURE_BLUET}), (ItemLike)ModItems.GRIMSHADE.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GHOST_POWDER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.WITHER_ROSE}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.BONE}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.BLACK_DYE}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("grimshade_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.BEETROOT_SEEDS}), (ItemLike)ModItems.RABBAGE_SEEDS.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DREAM_PASTE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.IRON_NUGGET}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.SWEET_BERRIES}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.POPPY}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("rabbage_seeds_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.KELP}), (ItemLike)ModItems.NAUTILITE.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SIREN_PASTE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.WATER_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.NAUTILUS_SHELL}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.PRISMARINE_CRYSTALS}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("nautilite_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.OXEYE_DAISY}), (ItemLike)ModItems.WINDSONG.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.AIR_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GHOST_POWDER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.FEATHER}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.PHANTOM_MEMBRANE}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("windsong_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.BLUE_ORCHID}), (ItemLike)ModItems.LOURDES.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.AIR_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.HONEYCOMB}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.GLISTERING_MELON_SLICE}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DREAM_PASTE.get()}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("lourdes_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.DANDELION}), (ItemLike)ModItems.AEGIFLORA.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.GUNPOWDER}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GHOST_POWDER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.LOTUS_BLOSSOM.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{Items.MOSS_BLOCK}))
         .unlockedBy("has_hex_focus", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.HEX_FOCUS.get()}).build()}))
         .save(recipeOutput, id("aegiflora_from_ritual_table"));
      NaturesRitualRecipeBuilder.ritual(RecipeCategory.MISC, Ingredient.of(new ItemLike[]{Items.POPPY}), (ItemLike)ModItems.MORPHORA.get())
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DREAM_PASTE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_POWDER.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.EARTH_NODE.get()}))
         .requiresBrazierIngredient(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()}))
         .unlockedBy("has_mutavis", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MUTAVIS.get()}).build()}))
         .save(recipeOutput, id("morphora_from_ritual_table"));
   }

   private void buildSmallCauldronRecipes(RecipeOutput recipeOutput) {
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_bloodlust_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_BLOODLUST.get(),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.MANDRAKE.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_POWDER.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()}),
         Ingredient.of(new ItemLike[]{Items.ROTTEN_FLESH})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_spikeskin_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_SPIKESKIN.get(),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CELESTIAL_CRYSTAL.get()}),
         Ingredient.of(new ItemLike[]{Items.IRON_NUGGET}),
         Ingredient.of(new ItemLike[]{Items.SWEET_BERRIES}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_slimewalker_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_SLIMEWALKER.get(),
         Ingredient.of(new ItemLike[]{Items.SLIME_BALL}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CHILLBERRIES.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()}),
         Ingredient.of(new ItemLike[]{Items.FEATHER})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_homestead_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_HOMESTEAD.get(),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()}),
         Ingredient.of(new ItemLike[]{Items.ENDER_PEARL}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_POWDER.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GALEBERRIES.get()})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_siphon_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_SIPHON.get(),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DREAM_PASTE.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SIREN_PASTE.get()}),
         Ingredient.of(new ItemLike[]{Items.IRON_INGOT}),
         Ingredient.of(new ItemLike[]{Items.REDSTONE})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_daybloom_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_DAYBLOOM.get(),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SUNFIRE_TOMATO.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_POWDER.get()}),
         Ingredient.of(new ItemLike[]{Items.GLOW_BERRIES}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModBlocks.WITCHWEED.get()})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_arachnid_grace_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_ARACHNID_GRACE.get(),
         Ingredient.of(new ItemLike[]{Items.SPIDER_EYE}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GHOST_POWDER.get()}),
         Ingredient.of(new ItemLike[]{Items.BLACK_DYE}),
         Ingredient.of(new ItemLike[]{Items.STRING})
      );
      this.smallCauldronBrew(
         recipeOutput,
         "brew_of_hollow_silence_from_small_cauldron",
         (ItemLike)ModItems.BREW_OF_HOLLOW_SILENCE.get(),
         Ingredient.of(new ItemLike[]{Items.FEATHER}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GHOST_POWDER.get()}),
         Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CHILLBERRIES.get()}),
         Ingredient.of(new ItemLike[]{Items.SCULK})
      );
   }

   private void smallCauldronBrew(
      RecipeOutput recipeOutput, String name, ItemLike result, Ingredient first, Ingredient second, Ingredient third, Ingredient fourth
   ) {
      SmallCauldronRecipeBuilder.brew(RecipeCategory.BREWING, first, result)
         .requiresIngredient(second)
         .requiresIngredient(third)
         .requiresIngredient(fourth)
         .brewTime(4800)
         .unlockedBy(
            "has_rustic_bottle", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.RUSTIC_BOTTLE.get()}).build()})
         )
         .save(recipeOutput, id(name));
   }

   private void buildMortarAndPestleRecipes(RecipeOutput recipeOutput) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.MORTAR_AND_PESTLE.get())
         .requires(Items.BOWL)
         .requires(Items.STONE)
         .unlockedBy("has_bowl", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BOWL}).build()}))
         .save(recipeOutput);
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{Items.BONE}), new ItemStack(Items.BONE_MEAL, 5))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("bone_meal_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{Items.SUGAR_CANE}), new ItemStack(Items.SUGAR, 2))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("sugar_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{Items.BLAZE_ROD}), new ItemStack(Items.BLAZE_POWDER, 3))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("blaze_powder_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SALTSPROUT.get()}), new ItemStack((ItemLike)ModItems.SALT.get()))
         .unlockedBy("has_saltsprout", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SALTSPROUT.get()}).build()}))
         .save(recipeOutput, id("salt_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()}), new ItemStack((ItemLike)ModItems.MUTAVIS.get()))
         .requires(Ingredient.of(new ItemLike[]{Items.SLIME_BALL}))
         .requires(Ingredient.of(ModTags.Items.CRUSHED_HERBS))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("mutavis_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SIREN_KELP.get()}), new ItemStack((ItemLike)ModItems.SIREN_PASTE.get())
         )
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("siren_paste_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DREAMSHROOM.get()}), new ItemStack((ItemLike)ModItems.DREAM_PASTE.get())
         )
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("dream_paste_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.SPIRIT_BLOOM.get()}), new ItemStack((ItemLike)ModItems.SPIRIT_POWDER.get())
         )
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("spirit_powder_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(
            Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GHOST_FERN.get()}), new ItemStack((ItemLike)ModItems.GHOST_POWDER.get())
         )
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("ghost_powder_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(ItemTags.SMALL_FLOWERS), new ItemStack((ItemLike)ModItems.FRAGRANT_NECTAR.get()))
         .requires(Ingredient.of(new ItemLike[]{Items.HONEYCOMB}))
         .requires(Ingredient.of(ModTags.Items.HERBS))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("fragrant_nectar_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{Items.POPPY}), new ItemStack((ItemLike)ModItems.BRAMBLEGUARD_SALVE.get()))
         .requires(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.RABBAGE.get()}))
         .requires(Ingredient.of(new ItemLike[]{Items.AZURE_BLUET}))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("brambleguard_salve_from_mortar"));
      MortarAndPestleRecipeBuilder.mortar(Ingredient.of(new ItemLike[]{Items.CORNFLOWER}), new ItemStack((ItemLike)ModItems.MENDERS_SALVE.get()))
         .requires(Ingredient.of(new ItemLike[]{(ItemLike)ModItems.TREE_RESIN.get()}))
         .requires(Ingredient.of(new ItemLike[]{Items.OXEYE_DAISY}))
         .unlockedBy(
            "has_mortar_and_pestle",
            inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MORTAR_AND_PESTLE.get()}).build()})
         )
         .save(recipeOutput, id("menders_salve_from_mortar"));
   }

   private void buildCenserRecipes(RecipeOutput recipeOutput) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)ModItems.CENSER.get())
         .pattern(" P ")
         .pattern("PAP")
         .pattern("SSS")
         .define('P', Items.BRICK)
         .define('S', ItemTags.LOGS)
         .define('A', ItemTags.COALS)
         .unlockedBy("has_brick", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{Items.BRICK}).build()}))
         .save(recipeOutput);
   }

   private void buildCropRecipes(RecipeOutput recipeOutput) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.MANDRAKE_SEEDS.get())
         .requires((ItemLike)ModItems.MANDRAKE.get())
         .unlockedBy("has_mandrake", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MANDRAKE.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)ModItems.SUNFIRE_TOMATO_SEEDS.get())
         .requires((ItemLike)ModItems.SUNFIRE_TOMATO.get())
         .unlockedBy(
            "has_sunfire_tomato", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SUNFIRE_TOMATO.get()}).build()})
         )
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.CHILLBERRY_PIE.get())
         .requires((ItemLike)ModItems.CHILLBERRIES.get())
         .requires(Items.EGG)
         .requires(Items.SUGAR)
         .requires(Items.WHEAT)
         .unlockedBy(
            "has_chillberries", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.CHILLBERRIES.get()}).build()})
         )
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.SPICY_SANDWICH.get())
         .requires((ItemLike)ModItems.SUNFIRE_TOMATO.get())
         .requires(ModTags.Items.FOODS_BREAD)
         .requires(ModTags.Items.FOODS_COOKED_MEAT)
         .unlockedBy(
            "has_sunfire_tomato", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SUNFIRE_TOMATO.get()}).build()})
         )
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.MANDRAKE_STEW.get())
         .requires((ItemLike)ModItems.MANDRAKE.get())
         .requires(Items.BOWL)
         .requires(ModTags.Items.FOODS_VEGETABLE)
         .requires(ModTags.Items.FOODS_VEGETABLE)
         .unlockedBy("has_mandrake", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MANDRAKE.get()}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, (ItemLike)ModItems.GALEBERRIES_COOKIE.get(), 4)
         .requires((ItemLike)ModItems.GALEBERRIES.get())
         .requires(Items.WHEAT)
         .requires(Items.WHEAT)
         .requires(Items.SUGAR)
         .unlockedBy("has_galeberries", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.GALEBERRIES.get()}).build()}))
         .save(recipeOutput);
   }

   private void buildMutationRecipes(RecipeOutput recipeOutput) {
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.GRANITE}), Items.ANDESITE, "andesite_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.NETHERRACK}), Items.BLACKSTONE, "blackstone_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.ICE}), Items.BLUE_ICE, "blue_ice_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.ANDESITE}), Items.DIORITE, "diorite_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.DIORITE}), Items.GRANITE, "granite_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.CLAY}), Items.MUD, "mud_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.SNOW_BLOCK}), Items.PACKED_ICE, "packed_ice_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.ROOTED_DIRT}), Items.PODZOL, "podzol_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.SAND}), Items.RED_SAND, "red_sand_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.DIRT}), Items.ROOTED_DIRT, "rooted_dirt_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.DRIPSTONE_BLOCK}), Items.TUFF, "tuff_from_mutation");
      MutationRecipeBuilder.mutation(Ingredient.of(ModTags.Items.TULIPS), new ItemStack((ItemLike)ModItems.CELESTIAL_BLOOM.get()))
         .unlockedBy("has_mutavis", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MUTAVIS.get()}).build()}))
         .save(recipeOutput, id("celestial_bloom_from_mutation"));
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.BROWN_MUSHROOM}), (Item)ModItems.DREAMSHROOM.get(), "dreamshroom_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.FERN}), (Item)ModItems.GHOST_FERN.get(), "ghost_fern_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.LILY_PAD}), (Item)ModItems.LOTUS_FLOWER.get(), "lotus_flower_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.KELP}), (Item)ModItems.SIREN_KELP.get(), "siren_kelp_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.BLUE_ORCHID}), (Item)ModItems.SPIRIT_BLOOM.get(), "spirit_bloom_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.AZURE_BLUET}), (Item)ModItems.WITCHWEED.get(), "witchweed_from_mutation");
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Items.CACTUS}), (Item)ModItems.SALTSPROUT.get(), "saltsprout_from_mutation");
      this.mutation(
         recipeOutput, Ingredient.of(new ItemLike[]{Blocks.OAK_SAPLING}), (Item)ModItems.COTTONWOOD_SAPLING.get(), "cottonwood_sapling_from_mutation"
      );
      this.mutation(recipeOutput, Ingredient.of(new ItemLike[]{Blocks.BIRCH_SAPLING}), (Item)ModItems.WILLOW_SAPLING.get(), "willow_sapling_from_mutation");
   }

   private void mutation(RecipeOutput recipeOutput, Ingredient input, Item result, String name) {
      MutationRecipeBuilder.mutation(input, new ItemStack(result))
         .unlockedBy("has_mutavis", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.MUTAVIS.get()}).build()}))
         .save(recipeOutput, id(name));
   }

   private static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", path);
   }

   private static void silkweaveArmorRecipe(RecipeOutput recipeOutput, Item result, Item leatherArmor, String top, String middle, String bottom) {
      ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result)
         .pattern(top)
         .pattern(middle)
         .pattern(bottom)
         .define('S', (ItemLike)ModItems.SILK_FIBER.get())
         .define('L', leatherArmor)
         .define('W', ItemTags.WOOL);
      if ((top + middle + bottom).indexOf(84) >= 0) {
         builder.define('T', Items.STRING);
      }

      builder.unlockedBy(
            "has_silk_fiber", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.SILK_FIBER.get()}).build()})
         )
         .save(recipeOutput);
   }

   private static void woodSetRecipes(
      RecipeOutput recipeOutput,
      String name,
      Item log,
      Item wood,
      Item strippedLog,
      Item strippedWood,
      Item planks,
      Item stairs,
      Item slab,
      Item button,
      Item pressurePlate,
      Item fence,
      Item fenceGate,
      Item trapdoor,
      Item door,
      Item sign,
      Item hangingSign,
      TagKey<Item> logsTag
   ) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
         .requires(logsTag)
         .unlockedBy("has_" + name + "_logs", inventoryTrigger(new ItemPredicate[]{Builder.item().of(logsTag).build()}))
         .save(recipeOutput, id(name + "_planks"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
         .pattern("LL")
         .pattern("LL")
         .define('L', log)
         .unlockedBy("has_" + name + "_log", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{log}).build()}))
         .save(recipeOutput, id(name + "_wood"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, strippedWood, 3)
         .pattern("LL")
         .pattern("LL")
         .define('L', strippedLog)
         .unlockedBy("has_stripped_" + name + "_log", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{strippedLog}).build()}))
         .save(recipeOutput, id("stripped_" + name + "_wood"));
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4)
         .pattern("P  ")
         .pattern("PP ")
         .pattern("PPP")
         .define('P', planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, slab, 6)
         .pattern("PPP")
         .define('P', planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, button)
         .requires(planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, pressurePlate)
         .pattern("PP")
         .define('P', planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, fence, 3)
         .pattern("PSP")
         .pattern("PSP")
         .define('P', planks)
         .define('S', Items.STICK)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, fenceGate)
         .pattern("SPS")
         .pattern("SPS")
         .define('P', planks)
         .define('S', Items.STICK)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, trapdoor, 2)
         .pattern("PPP")
         .pattern("PPP")
         .define('P', planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, door, 3)
         .pattern("PP")
         .pattern("PP")
         .pattern("PP")
         .define('P', planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign, 3)
         .pattern("PPP")
         .pattern("PPP")
         .pattern(" S ")
         .define('P', planks)
         .define('S', Items.STICK)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, hangingSign, 6)
         .pattern("C C")
         .pattern("PPP")
         .pattern("PPP")
         .define('C', Items.CHAIN)
         .define('P', strippedLog)
         .unlockedBy("has_stripped_" + name + "_log", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{strippedLog}).build()}))
         .save(recipeOutput);
   }

   private static void boatRecipes(RecipeOutput recipeOutput, String name, Item boat, Item chestBoat, Item planks) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, boat)
         .group("boat")
         .pattern("P P")
         .pattern("PPP")
         .define('P', planks)
         .unlockedBy("has_" + name + "_planks", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{planks}).build()}))
         .save(recipeOutput, id(name + "_boat"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, chestBoat)
         .group("chest_boat")
         .requires(Items.CHEST)
         .requires(boat)
         .unlockedBy("has_" + name + "_boat", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{boat}).build()}))
         .save(recipeOutput, id(name + "_chest_boat"));
   }
}
