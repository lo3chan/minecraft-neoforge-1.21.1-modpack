package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.providers.AetherRecipeProvider;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.Slots;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.neoforged.neoforge.common.Tags.Items;

public class AetherRecipeData extends AetherRecipeProvider {
   public AetherRecipeData(PackOutput output, CompletableFuture<Provider> lookupProvider) {
      super(output, lookupProvider, "aether");
   }

   protected void buildRecipes(RecipeOutput consumer, Provider holderLookup) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get())
         .group("mossy_holystone")
         .requires((ItemLike)AetherBlocks.HOLYSTONE.get())
         .requires(Blocks.VINE)
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer, this.name("mossy_holystone_with_vine"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get())
         .group("mossy_holystone")
         .requires((ItemLike)AetherBlocks.HOLYSTONE.get())
         .requires(Blocks.MOSS_BLOCK)
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer, this.name("mossy_holystone_with_moss"));
      woodFromLogs(consumer, (ItemLike)AetherBlocks.SKYROOT_WOOD.get(), (ItemLike)AetherBlocks.SKYROOT_LOG.get());
      woodFromLogs(consumer, (ItemLike)AetherBlocks.GOLDEN_OAK_WOOD.get(), (ItemLike)AetherBlocks.GOLDEN_OAK_LOG.get());
      woodFromLogs(consumer, (ItemLike)AetherBlocks.STRIPPED_SKYROOT_WOOD.get(), (ItemLike)AetherBlocks.STRIPPED_SKYROOT_LOG.get());
      planksFromLogs(consumer, (ItemLike)AetherBlocks.SKYROOT_PLANKS.get(), AetherTags.Items.CRAFTS_SKYROOT_PLANKS, 4);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get(), 4)
         .define('#', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .pattern("##")
         .pattern("##")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer);
      this.oreBlockStorageRecipesRecipesWithCustomUnpacking(
         consumer,
         RecipeCategory.MISC,
         (ItemLike)AetherItems.AMBROSIUM_SHARD.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)AetherBlocks.AMBROSIUM_BLOCK.get(),
         "ambrosium_shard_from_ambrosium_block",
         "ambrosium_shard"
      );
      this.oreBlockStorageRecipesRecipesWithCustomUnpacking(
         consumer,
         RecipeCategory.MISC,
         (ItemLike)AetherItems.ZANITE_GEMSTONE.get(),
         RecipeCategory.BUILDING_BLOCKS,
         (ItemLike)AetherBlocks.ZANITE_BLOCK.get(),
         "zanite_gemstone_from_zanite_block",
         "zanite_gemstone"
      );
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.QUICKSOIL_GLASS_PANE.get(), 16)
         .define('#', (ItemLike)AetherBlocks.QUICKSOIL_GLASS.get())
         .pattern("###")
         .pattern("###")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.QUICKSOIL_GLASS.get()), has((ItemLike)AetherBlocks.QUICKSOIL_GLASS.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.ALTAR.get(), 1)
         .define('H', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .define('Z', AetherTags.Items.GEMS_ZANITE)
         .pattern("HHH")
         .pattern("HZH")
         .pattern("HHH")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.FREEZER.get(), 1)
         .define('H', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .define('I', (ItemLike)AetherBlocks.ICESTONE.get())
         .define('P', (ItemLike)AetherBlocks.SKYROOT_PLANKS.get())
         .pattern("HHH")
         .pattern("HIH")
         .pattern("PPP")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.INCUBATOR.get(), 1)
         .define('H', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .define('T', (ItemLike)AetherBlocks.AMBROSIUM_TORCH.get())
         .pattern("HHH")
         .pattern("HTH")
         .pattern("HHH")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.AMBROSIUM_TORCH.get(), 4)
         .define('A', AetherTags.Items.GEMS_AMBROSIUM)
         .define('/', AetherTags.Items.SKYROOT_STICKS)
         .pattern("A")
         .pattern("/")
         .unlockedBy(getHasName((ItemLike)AetherItems.HOLYSTONE_PICKAXE.get()), has((ItemLike)AetherItems.HOLYSTONE_PICKAXE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.SKYROOT_SIGN.get(), 3)
         .group("wooden_sign")
         .define('P', ((Block)AetherBlocks.SKYROOT_PLANKS.get()).asItem())
         .define('/', Items.RODS_WOODEN)
         .pattern("PPP")
         .pattern("PPP")
         .pattern(" / ")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.SKYROOT_HANGING_SIGN.get(), 6)
         .group("hanging_sign")
         .define('#', (ItemLike)AetherBlocks.STRIPPED_SKYROOT_LOG.get())
         .define('X', net.minecraft.world.item.Items.CHAIN)
         .pattern("X X")
         .pattern("###")
         .pattern("###")
         .unlockedBy("has_stripped_logs", has((ItemLike)AetherBlocks.STRIPPED_SKYROOT_LOG.get()))
         .save(consumer);
      this.fence(AetherBlocks.SKYROOT_FENCE, AetherBlocks.SKYROOT_PLANKS).save(consumer);
      this.fenceGate(AetherBlocks.SKYROOT_FENCE_GATE, AetherBlocks.SKYROOT_PLANKS).save(consumer);
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.CARVED_WALL.get(), (ItemLike)AetherBlocks.CARVED_STONE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.ANGELIC_WALL.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HELLFIRE_WALL.get(), (ItemLike)AetherBlocks.HELLFIRE_STONE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HOLYSTONE_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE_WALL.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.ICESTONE_WALL.get(), (ItemLike)AetherBlocks.ICESTONE.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get());
      wall(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.AEROGEL_WALL.get(), (ItemLike)AetherBlocks.AEROGEL.get());
      this.stairs(AetherBlocks.SKYROOT_STAIRS, AetherBlocks.SKYROOT_PLANKS).group("wooden_stairs").save(consumer);
      this.stairs(AetherBlocks.CARVED_STAIRS, AetherBlocks.CARVED_STONE).save(consumer);
      this.stairs(AetherBlocks.ANGELIC_STAIRS, AetherBlocks.ANGELIC_STONE).save(consumer);
      this.stairs(AetherBlocks.HELLFIRE_STAIRS, AetherBlocks.HELLFIRE_STONE).save(consumer);
      this.stairs(AetherBlocks.HOLYSTONE_STAIRS, AetherBlocks.HOLYSTONE).save(consumer);
      this.stairs(AetherBlocks.MOSSY_HOLYSTONE_STAIRS, AetherBlocks.MOSSY_HOLYSTONE).save(consumer);
      this.stairs(AetherBlocks.ICESTONE_STAIRS, AetherBlocks.ICESTONE).save(consumer);
      this.stairs(AetherBlocks.HOLYSTONE_BRICK_STAIRS, AetherBlocks.HOLYSTONE_BRICKS).save(consumer);
      this.stairs(AetherBlocks.AEROGEL_STAIRS, AetherBlocks.AEROGEL).save(consumer);
      slabBuilder(
            RecipeCategory.BUILDING_BLOCKS,
            (ItemLike)AetherBlocks.SKYROOT_SLAB.get(),
            Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.SKYROOT_PLANKS.get()})
         )
         .group("wooden_slab")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .save(consumer);
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.CARVED_SLAB.get(), (ItemLike)AetherBlocks.CARVED_STONE.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ANGELIC_SLAB.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HELLFIRE_SLAB.get(), (ItemLike)AetherBlocks.HELLFIRE_STONE.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ICESTONE_SLAB.get(), (ItemLike)AetherBlocks.ICESTONE.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get());
      slab(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.AEROGEL_SLAB.get(), (ItemLike)AetherBlocks.AEROGEL.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.SKYROOT_BOOKSHELF.get(), 1)
         .define('P', (ItemLike)AetherBlocks.SKYROOT_PLANKS.get())
         .define('B', net.minecraft.world.item.Items.BOOK)
         .pattern("PPP")
         .pattern("BBB")
         .pattern("PPP")
         .unlockedBy(getHasName(net.minecraft.world.item.Items.BOOK), has(net.minecraft.world.item.Items.BOOK))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.SKYROOT_BED.get(), 1)
         .define('W', ItemTags.WOOL)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("WWW")
         .pattern("PPP")
         .unlockedBy("has_wool", has(ItemTags.WOOL))
         .save(consumer);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.PURPLE_DYE)
         .group("purple_dye")
         .requires((ItemLike)AetherBlocks.PURPLE_FLOWER.get())
         .unlockedBy(getHasName((ItemLike)AetherBlocks.PURPLE_FLOWER.get()), has((ItemLike)AetherBlocks.PURPLE_FLOWER.get()))
         .save(consumer, this.name("flower_to_purple_dye"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.WHITE_DYE)
         .group("white_dye")
         .requires((ItemLike)AetherBlocks.WHITE_FLOWER.get())
         .unlockedBy(getHasName((ItemLike)AetherBlocks.WHITE_FLOWER.get()), has((ItemLike)AetherBlocks.WHITE_FLOWER.get()))
         .save(consumer, this.name("flower_to_white_dye"));
      this.makePickaxeWithTag(AetherItems.SKYROOT_PICKAXE, AetherTags.Items.SKYROOT_TOOL_CRAFTING, "has_planks").save(consumer);
      this.makeAxeWithTag(AetherItems.SKYROOT_AXE, AetherTags.Items.SKYROOT_TOOL_CRAFTING, "has_planks").save(consumer);
      this.makeShovelWithTag(AetherItems.SKYROOT_SHOVEL, AetherTags.Items.SKYROOT_TOOL_CRAFTING, "has_planks").save(consumer);
      this.makeHoeWithTag(AetherItems.SKYROOT_HOE, AetherTags.Items.SKYROOT_TOOL_CRAFTING, "has_planks").save(consumer);
      this.makePickaxeWithBlock(AetherItems.HOLYSTONE_PICKAXE, AetherBlocks.HOLYSTONE).save(consumer);
      this.makeAxeWithBlock(AetherItems.HOLYSTONE_AXE, AetherBlocks.HOLYSTONE).save(consumer);
      this.makeShovelWithBlock(AetherItems.HOLYSTONE_SHOVEL, AetherBlocks.HOLYSTONE).save(consumer);
      this.makeHoeWithBlock(AetherItems.HOLYSTONE_HOE, AetherBlocks.HOLYSTONE).save(consumer);
      this.makePickaxeWithTag(AetherItems.ZANITE_PICKAXE, AetherTags.Items.GEMS_ZANITE, "has_zanite").save(consumer);
      this.makeAxeWithTag(AetherItems.ZANITE_AXE, AetherTags.Items.GEMS_ZANITE, "has_zanite").save(consumer);
      this.makeShovelWithTag(AetherItems.ZANITE_SHOVEL, AetherTags.Items.GEMS_ZANITE, "has_zanite").save(consumer);
      this.makeHoeWithTag(AetherItems.ZANITE_HOE, AetherTags.Items.GEMS_ZANITE, "has_zanite").save(consumer);
      this.makePickaxeWithTag(AetherItems.GRAVITITE_PICKAXE, AetherTags.Items.PROCESSED_GRAVITITE, "has_gravitite").save(consumer);
      this.makeAxeWithTag(AetherItems.GRAVITITE_AXE, AetherTags.Items.PROCESSED_GRAVITITE, "has_gravitite").save(consumer);
      this.makeShovelWithTag(AetherItems.GRAVITITE_SHOVEL, AetherTags.Items.PROCESSED_GRAVITITE, "has_gravitite").save(consumer);
      this.makeHoeWithTag(AetherItems.GRAVITITE_HOE, AetherTags.Items.PROCESSED_GRAVITITE, "has_gravitite").save(consumer);
      this.makeSwordWithTag(AetherItems.SKYROOT_SWORD, AetherTags.Items.SKYROOT_TOOL_CRAFTING, "has_planks").save(consumer);
      this.makeSwordWithBlock(AetherItems.HOLYSTONE_SWORD, AetherBlocks.HOLYSTONE).save(consumer);
      this.makeSwordWithTag(AetherItems.ZANITE_SWORD, AetherTags.Items.GEMS_ZANITE, "has_zanite").save(consumer);
      this.makeSwordWithTag(AetherItems.GRAVITITE_SWORD, AetherTags.Items.PROCESSED_GRAVITITE, "has_gravitite").save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)AetherItems.GOLDEN_DART.get(), 4)
         .define('F', Items.FEATHERS)
         .define('/', AetherTags.Items.SKYROOT_STICKS)
         .define('G', (ItemLike)AetherItems.GOLDEN_AMBER.get())
         .pattern("F")
         .pattern("/")
         .pattern("G")
         .unlockedBy("has_feather", has(Items.FEATHERS))
         .unlockedBy(getHasName((ItemLike)AetherItems.GOLDEN_AMBER.get()), has((ItemLike)AetherItems.GOLDEN_AMBER.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get(), 1)
         .define('P', (ItemLike)AetherBlocks.SKYROOT_PLANKS.get())
         .define('G', (ItemLike)AetherItems.GOLDEN_AMBER.get())
         .pattern("P")
         .pattern("P")
         .pattern("G")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, (ItemLike)AetherItems.POISON_DART.get(), 8)
         .define('D', (ItemLike)AetherItems.GOLDEN_DART.get())
         .define('B', (ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get())
         .pattern("DDD")
         .pattern("DBD")
         .pattern("DDD")
         .unlockedBy(getHasName((ItemLike)AetherItems.GOLDEN_DART.get()), has((ItemLike)AetherItems.GOLDEN_DART.get()))
         .unlockedBy(getHasName((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get()), has((ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get()))
         .save(consumer);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, (ItemLike)AetherItems.POISON_DART_SHOOTER.get(), 1)
         .requires((ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get())
         .requires((ItemLike)AetherItems.AECHOR_PETAL.get())
         .unlockedBy(getHasName((ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get()), has((ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get()))
         .unlockedBy(getHasName((ItemLike)AetherItems.AECHOR_PETAL.get()), has((ItemLike)AetherItems.AECHOR_PETAL.get()))
         .save(consumer);
      this.makeHelmetWithTag(AetherItems.ZANITE_HELMET, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makeChestplateWithTag(AetherItems.ZANITE_CHESTPLATE, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makeLeggingsWithTag(AetherItems.ZANITE_LEGGINGS, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makeBootsWithTag(AetherItems.ZANITE_BOOTS, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makeHelmetWithTag(AetherItems.GRAVITITE_HELMET, AetherTags.Items.PROCESSED_GRAVITITE, "gravitite").save(consumer);
      this.makeChestplateWithTag(AetherItems.GRAVITITE_CHESTPLATE, AetherTags.Items.PROCESSED_GRAVITITE, "gravitite").save(consumer);
      this.makeLeggingsWithTag(AetherItems.GRAVITITE_LEGGINGS, AetherTags.Items.PROCESSED_GRAVITITE, "gravitite").save(consumer);
      this.makeBootsWithTag(AetherItems.GRAVITITE_BOOTS, AetherTags.Items.PROCESSED_GRAVITITE, "gravitite").save(consumer);
      this.makeRing(AetherItems.IRON_RING, net.minecraft.world.item.Items.IRON_INGOT).save(consumer);
      this.makeRing(AetherItems.GOLDEN_RING, net.minecraft.world.item.Items.GOLD_INGOT).save(consumer);
      this.makeRingWithTag(AetherItems.ZANITE_RING, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makePendant(AetherItems.IRON_PENDANT, net.minecraft.world.item.Items.IRON_INGOT).save(consumer);
      this.makePendant(AetherItems.GOLDEN_PENDANT, net.minecraft.world.item.Items.GOLD_INGOT).save(consumer);
      this.makePendantWithTag(AetherItems.ZANITE_PENDANT, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makeCape(AetherItems.RED_CAPE, Blocks.RED_WOOL.asItem()).save(consumer);
      this.makeCape(AetherItems.BLUE_CAPE, Blocks.BLUE_WOOL.asItem()).group("blue_cape").save(consumer, this.name("blue_cape_blue_wool"));
      this.makeCape(AetherItems.BLUE_CAPE, Blocks.LIGHT_BLUE_WOOL.asItem()).group("blue_cape").save(consumer, this.name("blue_cape_light_blue_wool"));
      this.makeCape(AetherItems.BLUE_CAPE, Blocks.CYAN_WOOL.asItem()).group("blue_cape").save(consumer, this.name("blue_cape_cyan_wool"));
      this.makeCape(AetherItems.YELLOW_CAPE, Blocks.YELLOW_WOOL.asItem()).save(consumer);
      this.makeCape(AetherItems.WHITE_CAPE, Blocks.WHITE_WOOL.asItem()).save(consumer);
      this.makeGlovesWithTag(AetherItems.LEATHER_GLOVES, Items.LEATHERS, "leather").save(consumer);
      this.makeGlovesWithTag(AetherItems.IRON_GLOVES, Items.INGOTS_IRON, "iron").save(consumer);
      this.makeGlovesWithTag(AetherItems.GOLDEN_GLOVES, Items.INGOTS_GOLD, "gold").save(consumer);
      this.makeGlovesWithTag(AetherItems.DIAMOND_GLOVES, Items.GEMS_DIAMOND, "diamond").save(consumer);
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE}),
            Ingredient.of(new ItemLike[]{(ItemLike)AetherItems.DIAMOND_GLOVES.get()}),
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.NETHERITE_INGOT}),
            RecipeCategory.COMBAT,
            (Item)AetherItems.NETHERITE_GLOVES.get()
         )
         .unlocks("has_netherite_ingot", has(net.minecraft.world.item.Items.NETHERITE_INGOT))
         .save(consumer, this.name(getItemName((ItemLike)AetherItems.NETHERITE_GLOVES.get()) + "_smithing"));
      this.makeGlovesWithTag(AetherItems.ZANITE_GLOVES, AetherTags.Items.GEMS_ZANITE, "zanite").save(consumer);
      this.makeGlovesWithTag(AetherItems.GRAVITITE_GLOVES, AetherTags.Items.PROCESSED_GRAVITITE, "gravitite").save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)AetherItems.SKYROOT_STICK.get(), 4)
         .group("sticks")
         .define('#', AetherTags.Items.SKYROOT_STICK_CRAFTING)
         .pattern("#")
         .pattern("#")
         .unlockedBy("has_planks", has(AetherTags.Items.SKYROOT_STICK_CRAFTING))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, (ItemLike)AetherItems.SKYROOT_BUCKET.get(), 1)
         .define('#', AetherTags.Items.SKYROOT_TOOL_CRAFTING)
         .pattern("# #")
         .pattern(" # ")
         .unlockedBy("has_planks", has(AetherTags.Items.SKYROOT_TOOL_CRAFTING))
         .save(consumer);
      twoByTwoPacker(consumer, RecipeCategory.TOOLS, (ItemLike)AetherItems.COLD_PARACHUTE.get(), (ItemLike)AetherBlocks.COLD_AERCLOUD.get());
      twoByTwoPacker(consumer, RecipeCategory.TOOLS, (ItemLike)AetherItems.GOLDEN_PARACHUTE.get(), (ItemLike)AetherBlocks.GOLDEN_AERCLOUD.get());
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, (ItemLike)AetherItems.NATURE_STAFF.get(), 1)
         .define('Z', AetherTags.Items.GEMS_ZANITE)
         .define('/', AetherTags.Items.SKYROOT_STICKS)
         .pattern("Z")
         .pattern("/")
         .unlockedBy("has_zanite", has(AetherTags.Items.GEMS_ZANITE))
         .save(consumer);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, (ItemLike)AetherItems.BOOK_OF_LORE.get())
         .group("book_of_lore")
         .requires(net.minecraft.world.item.Items.BOOK)
         .requires(AetherTags.Items.BOOK_OF_LORE_MATERIALS)
         .unlockedBy(getHasName(net.minecraft.world.item.Items.BOOK), has(net.minecraft.world.item.Items.BOOK))
         .save(consumer);
      woodenBoat(consumer, (ItemLike)AetherItems.SKYROOT_BOAT.get(), (ItemLike)AetherBlocks.SKYROOT_PLANKS.get());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, (ItemLike)AetherItems.SKYROOT_CHEST_BOAT.get())
         .group("chest_boat")
         .requires(Items.CHESTS_WOODEN)
         .requires((ItemLike)AetherItems.SKYROOT_BOAT.get())
         .unlockedBy("has_boat", has(ItemTags.BOATS))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, net.minecraft.world.item.Items.SADDLE, 1)
         .define('L', net.minecraft.world.item.Items.LEATHER)
         .define('S', net.minecraft.world.item.Items.STRING)
         .pattern("LLL")
         .pattern("LSL")
         .unlockedBy(getHasName(net.minecraft.world.item.Items.LEATHER), has(net.minecraft.world.item.Items.LEATHER))
         .save(consumer, this.name("aether_saddle"));
      doorBuilder((ItemLike)AetherBlocks.SKYROOT_DOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.SKYROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .group("wooden_door")
         .save(consumer);
      trapdoorBuilder((ItemLike)AetherBlocks.SKYROOT_TRAPDOOR.get(), Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.SKYROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .group("wooden_trapdoor")
         .save(consumer);
      buttonBuilder((ItemLike)AetherBlocks.SKYROOT_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.SKYROOT_PLANKS.get()}))
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .group("wooden_button")
         .save(consumer);
      buttonBuilder((ItemLike)AetherBlocks.HOLYSTONE_BUTTON.get(), Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.HOLYSTONE.get()}))
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE,
            (ItemLike)AetherBlocks.SKYROOT_PRESSURE_PLATE.get(),
            Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.SKYROOT_PLANKS.get()})
         )
         .unlockedBy(getHasName((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()), has((ItemLike)AetherBlocks.SKYROOT_PLANKS.get()))
         .group("wooden_pressure_plate")
         .save(consumer);
      pressurePlateBuilder(
            RecipeCategory.REDSTONE,
            (ItemLike)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get(),
            Ingredient.of(new ItemLike[]{(ItemLike)AetherBlocks.HOLYSTONE.get()})
         )
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE.get()), has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, net.minecraft.world.item.Items.LEAD, 2)
         .group("minecraft:lead")
         .define('B', AetherTags.Items.SWET_BALLS)
         .define('S', Items.STRINGS)
         .pattern("SS ")
         .pattern("SB ")
         .pattern("  S")
         .unlockedBy("has_swet_balls", has(AetherTags.Items.SWET_BALLS))
         .save(consumer, this.name("swet_lead"));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON, 1)
         .group("minecraft:sticky_piston")
         .define('B', AetherTags.Items.SWET_BALLS)
         .define('P', Blocks.PISTON)
         .pattern("B")
         .pattern("P")
         .unlockedBy("has_swet_balls", has(AetherTags.Items.SWET_BALLS))
         .save(consumer, this.name("swet_sticky_piston"));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.SLIME_BLOCK, 1)
         .group("minecraft:slime_block")
         .define('B', AetherTags.Items.SWET_BALLS)
         .pattern("BBB")
         .pattern("BBB")
         .pattern("BBB")
         .unlockedBy("has_swet_balls", has(AetherTags.Items.SWET_BALLS))
         .save(consumer, this.name("swet_slime_block"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.BARREL, 1)
         .group("minecraft:barrel")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('H', ItemTags.WOODEN_SLABS)
         .pattern("PHP")
         .pattern("P P")
         .pattern("PHP")
         .unlockedBy("has_planks", has(AetherTags.Items.PLANKS_CRAFTING))
         .unlockedBy("has_slabs", has(ItemTags.WOODEN_SLABS))
         .save(consumer, this.name("skyroot_barrel"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.BEEHIVE, 1)
         .group("minecraft:beehive")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('C', net.minecraft.world.item.Items.HONEYCOMB)
         .pattern("PPP")
         .pattern("CCC")
         .pattern("PPP")
         .unlockedBy(getHasName(net.minecraft.world.item.Items.HONEYCOMB), has(net.minecraft.world.item.Items.HONEYCOMB))
         .save(consumer, this.name("skyroot_beehive"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CARTOGRAPHY_TABLE, 1)
         .group("minecraft:cartography_table")
         .define('#', net.minecraft.world.item.Items.PAPER)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("##")
         .pattern("PP")
         .pattern("PP")
         .unlockedBy(getHasName(net.minecraft.world.item.Items.PAPER), has(net.minecraft.world.item.Items.PAPER))
         .save(consumer, this.name("skyroot_cartography_table"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CHEST, 1)
         .group("minecraft:chest")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("PPP")
         .pattern("P P")
         .pattern("PPP")
         .unlockedBy(
            "has_lots_of_items",
            CriteriaTriggers.INVENTORY_CHANGED
               .createCriterion(new TriggerInstance(Optional.empty(), new Slots(Ints.atLeast(10), Ints.ANY, Ints.ANY), List.of()))
         )
         .save(consumer, this.name("skyroot_chest"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.CRAFTING_TABLE, 1)
         .group("minecraft:crafting_table")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("PP")
         .pattern("PP")
         .unlockedBy("has_planks", has(AetherTags.Items.PLANKS_CRAFTING))
         .save(consumer, this.name("skyroot_crafting_table"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.FLETCHING_TABLE, 1)
         .group("minecraft:fletching_table")
         .define('F', net.minecraft.world.item.Items.FLINT)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("FF")
         .pattern("PP")
         .pattern("PP")
         .unlockedBy(getHasName(net.minecraft.world.item.Items.FLINT), has(net.minecraft.world.item.Items.FLINT))
         .save(consumer, this.name("skyroot_fletching_table"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.GRINDSTONE, 1)
         .group("minecraft:grindstone")
         .define('/', Items.RODS_WOODEN)
         .define('H', Blocks.STONE_SLAB)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("/H/")
         .pattern("P P")
         .unlockedBy(getHasName(Blocks.STONE_SLAB), has(Blocks.STONE_SLAB))
         .save(consumer, this.name("skyroot_grindstone"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.GRINDSTONE, 1)
         .group("minecraft:grindstone")
         .define('/', Items.RODS_WOODEN)
         .define('H', (ItemLike)AetherBlocks.HOLYSTONE_SLAB.get())
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("/H/")
         .pattern("P P")
         .unlockedBy(getHasName((ItemLike)AetherBlocks.HOLYSTONE_SLAB.get()), has((ItemLike)AetherBlocks.HOLYSTONE_SLAB.get()))
         .save(consumer, this.name("skyroot_grindstone_holystone_slab"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX, 1)
         .group("minecraft:jukebox")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('D', Items.GEMS_DIAMOND)
         .pattern("PPP")
         .pattern("PDP")
         .pattern("PPP")
         .unlockedBy("has_diamond", has(Items.GEMS_DIAMOND))
         .save(consumer, this.name("skyroot_jukebox"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX, 1)
         .group("minecraft:jukebox")
         .define('P', ItemTags.PLANKS)
         .define('G', AetherTags.Items.PROCESSED_GRAVITITE)
         .pattern("PPP")
         .pattern("PGP")
         .pattern("PPP")
         .unlockedBy("has_gravitite", has(AetherTags.Items.PROCESSED_GRAVITITE))
         .save(consumer, this.name("gravitite_jukebox"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX, 1)
         .group("minecraft:jukebox")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('G', AetherTags.Items.PROCESSED_GRAVITITE)
         .pattern("PPP")
         .pattern("PGP")
         .pattern("PPP")
         .unlockedBy("has_gravitite", has(AetherTags.Items.PROCESSED_GRAVITITE))
         .save(consumer, this.name("skyroot_gravitite_jukebox"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.LOOM, 1)
         .group("minecraft:loom")
         .define('T', Items.STRINGS)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("TT")
         .pattern("PP")
         .unlockedBy("has_string", has(Items.STRINGS))
         .save(consumer, this.name("skyroot_loom"));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK, 1)
         .group("minecraft:note_block")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('R', Items.DUSTS_REDSTONE)
         .pattern("PPP")
         .pattern("PRP")
         .pattern("PPP")
         .unlockedBy("has_redstone", has(Items.DUSTS_REDSTONE))
         .save(consumer, this.name("skyroot_note_block"));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PISTON, 1)
         .group("minecraft:piston")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('C', Blocks.COBBLESTONE)
         .define('I', Items.INGOTS_IRON)
         .define('R', Items.DUSTS_REDSTONE)
         .pattern("PPP")
         .pattern("CIC")
         .pattern("CRC")
         .unlockedBy("has_redstone", has(Items.DUSTS_REDSTONE))
         .save(consumer, this.name("skyroot_piston"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, net.minecraft.world.item.Items.SHIELD, 1)
         .group("minecraft:shield")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('I', Items.INGOTS_IRON)
         .pattern("PIP")
         .pattern("PPP")
         .pattern(" P ")
         .unlockedBy("has_iron_ingot", has(Items.INGOTS_IRON))
         .save(consumer, this.name("skyroot_iron_vanilla_shield"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, net.minecraft.world.item.Items.SHIELD, 1)
         .group("minecraft:shield")
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .define('Z', AetherTags.Items.GEMS_ZANITE)
         .pattern("PZP")
         .pattern("PPP")
         .pattern(" P ")
         .unlockedBy("has_zanite_gemstone", has(AetherTags.Items.GEMS_ZANITE))
         .save(consumer, this.name("skyroot_zanite_vanilla_shield"));
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, net.minecraft.world.item.Items.SHIELD, 1)
         .group("minecraft:shield")
         .define('P', ItemTags.PLANKS)
         .define('Z', AetherTags.Items.GEMS_ZANITE)
         .pattern("PZP")
         .pattern("PPP")
         .pattern(" P ")
         .unlockedBy("has_zanite_gemstone", has(AetherTags.Items.GEMS_ZANITE))
         .save(consumer, this.name("wood_zanite_vanilla_shield"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SMITHING_TABLE, 1)
         .group("minecraft:smithing_table")
         .define('I', Items.INGOTS_IRON)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("II")
         .pattern("PP")
         .pattern("PP")
         .unlockedBy("has_iron_ingot", has(Items.INGOTS_IRON))
         .save(consumer, this.name("skyroot_smithing_table"));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.TRIPWIRE_HOOK, 2)
         .group("minecraft:tripwire_hook")
         .define('I', Items.INGOTS_IRON)
         .define('/', Items.RODS_WOODEN)
         .define('P', AetherTags.Items.PLANKS_CRAFTING)
         .pattern("I")
         .pattern("/")
         .pattern("P")
         .unlockedBy("has_string", has(Items.STRINGS))
         .save(consumer, this.name("skyroot_tripwire_hook"));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.STONECUTTER)
         .group("minecraft:stonecutter")
         .define('I', net.minecraft.world.item.Items.IRON_INGOT)
         .define('#', (ItemLike)AetherBlocks.HOLYSTONE.get())
         .pattern(" I ")
         .pattern("###")
         .unlockedBy("has_holystone", has((ItemLike)AetherBlocks.HOLYSTONE.get()))
         .save(consumer, this.name("holystone_stonecutter"));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.LECTERN)
         .group("minecraft:lectern")
         .define('S', ItemTags.WOODEN_SLABS)
         .define('B', (ItemLike)AetherBlocks.SKYROOT_BOOKSHELF.get())
         .pattern("SSS")
         .pattern(" B ")
         .pattern(" S ")
         .unlockedBy("has_book", has(net.minecraft.world.item.Items.BOOK))
         .save(consumer, this.name("skyroot_lectern"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE)
         .group("minecraft:cake")
         .define('A', (ItemLike)AetherItems.SKYROOT_MILK_BUCKET.get())
         .define('B', net.minecraft.world.item.Items.SUGAR)
         .define('C', net.minecraft.world.item.Items.WHEAT)
         .define('E', net.minecraft.world.item.Items.EGG)
         .pattern("AAA")
         .pattern("BEB")
         .pattern("CCC")
         .unlockedBy("has_egg", has(net.minecraft.world.item.Items.EGG))
         .save(consumer, this.name("skyroot_milk_bucket_cake"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE)
         .group("minecraft:cake")
         .define('A', net.minecraft.world.item.Items.MILK_BUCKET)
         .define('B', net.minecraft.world.item.Items.SUGAR)
         .define('C', net.minecraft.world.item.Items.WHEAT)
         .define('E', AetherTags.Items.MOA_EGGS)
         .pattern("AAA")
         .pattern("BEB")
         .pattern("CCC")
         .unlockedBy("has_moa_egg", has(AetherTags.Items.MOA_EGGS))
         .save(consumer, this.name("moa_egg_cake"));
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE)
         .group("minecraft:cake")
         .define('A', (ItemLike)AetherItems.SKYROOT_MILK_BUCKET.get())
         .define('B', net.minecraft.world.item.Items.SUGAR)
         .define('C', net.minecraft.world.item.Items.WHEAT)
         .define('E', AetherTags.Items.MOA_EGGS)
         .pattern("AAA")
         .pattern("BEB")
         .pattern("CCC")
         .unlockedBy("has_moa_egg", has(AetherTags.Items.MOA_EGGS))
         .save(consumer, this.name("skyroot_milk_bucket_moa_egg_cake"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, net.minecraft.world.item.Items.PUMPKIN_PIE)
         .group("minecraft:pumpkin_pie")
         .requires(Blocks.PUMPKIN)
         .requires(net.minecraft.world.item.Items.SUGAR)
         .requires(AetherTags.Items.MOA_EGGS)
         .unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN))
         .unlockedBy("has_pumpkin", has(Blocks.PUMPKIN))
         .save(consumer, this.name("moa_egg_pumpkin_pie"));
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, AetherItems.createSwetBannerItemStack(holderLookup.lookupOrThrow(Registries.BANNER_PATTERN)))
         .requires(ItemTags.BANNERS)
         .requires((ItemLike)AetherItems.SWET_CAPE.get())
         .unlockedBy("has_swet_cape", has((ItemLike)AetherItems.SWET_CAPE.get()))
         .save(consumer, this.name("swet_banner"));
      this.smeltingOreRecipe((ItemLike)AetherItems.AMBROSIUM_SHARD.get(), (ItemLike)AetherBlocks.AMBROSIUM_ORE.get(), 0.1F)
         .save(consumer, this.name("ambrosium_shard_from_smelting"));
      this.blastingOreRecipe((ItemLike)AetherItems.AMBROSIUM_SHARD.get(), (ItemLike)AetherBlocks.AMBROSIUM_ORE.get(), 0.1F)
         .save(consumer, this.name("ambrosium_shard_from_blasting"));
      this.smeltingOreRecipe((ItemLike)AetherItems.ZANITE_GEMSTONE.get(), (ItemLike)AetherBlocks.ZANITE_ORE.get(), 0.7F)
         .save(consumer, this.name("zanite_gemstone_from_smelting"));
      this.blastingOreRecipe((ItemLike)AetherItems.ZANITE_GEMSTONE.get(), (ItemLike)AetherBlocks.ZANITE_ORE.get(), 0.7F)
         .save(consumer, this.name("zanite_gemstone_from_blasting"));
      SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(
               new ItemLike[]{
                  (ItemLike)AetherItems.GOLDEN_GLOVES.get(),
                  (ItemLike)AetherItems.GOLDEN_PENDANT.get(),
                  (ItemLike)AetherItems.GOLDEN_RING.get(),
                  (ItemLike)AetherItems.VICTORY_MEDAL.get()
               }
            ),
            RecipeCategory.MISC,
            net.minecraft.world.item.Items.GOLD_NUGGET,
            0.1F,
            200
         )
         .unlockedBy("has_golden_gloves", has((ItemLike)AetherItems.GOLDEN_GLOVES.get()))
         .unlockedBy("has_golden_pendant", has((ItemLike)AetherItems.GOLDEN_PENDANT.get()))
         .unlockedBy("has_golden_ring", has((ItemLike)AetherItems.GOLDEN_RING.get()))
         .unlockedBy("has_victory_medal", has((ItemLike)AetherItems.VICTORY_MEDAL.get()))
         .group(getSmeltingRecipeName(net.minecraft.world.item.Items.GOLD_NUGGET))
         .save(consumer, this.name("aether_" + getSmeltingRecipeName(net.minecraft.world.item.Items.GOLD_NUGGET)));
      SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(
               new ItemLike[]{
                  (ItemLike)AetherItems.IRON_GLOVES.get(),
                  (ItemLike)AetherItems.IRON_PENDANT.get(),
                  (ItemLike)AetherItems.IRON_RING.get(),
                  (ItemLike)AetherItems.CHAINMAIL_GLOVES.get()
               }
            ),
            RecipeCategory.MISC,
            net.minecraft.world.item.Items.IRON_NUGGET,
            0.1F,
            200
         )
         .unlockedBy("has_iron_gloves", has((ItemLike)AetherItems.IRON_GLOVES.get()))
         .unlockedBy("has_iron_pendant", has((ItemLike)AetherItems.IRON_PENDANT.get()))
         .unlockedBy("has_iron_ring", has((ItemLike)AetherItems.IRON_RING.get()))
         .unlockedBy("has_chainmail_gloves", has((ItemLike)AetherItems.CHAINMAIL_GLOVES.get()))
         .group(getSmeltingRecipeName(net.minecraft.world.item.Items.IRON_NUGGET))
         .save(consumer, this.name("aether_" + getSmeltingRecipeName(net.minecraft.world.item.Items.IRON_NUGGET)));
      SimpleCookingRecipeBuilder.blasting(
            Ingredient.of(
               new ItemLike[]{
                  (ItemLike)AetherItems.GOLDEN_GLOVES.get(),
                  (ItemLike)AetherItems.GOLDEN_PENDANT.get(),
                  (ItemLike)AetherItems.GOLDEN_RING.get(),
                  (ItemLike)AetherItems.VICTORY_MEDAL.get()
               }
            ),
            RecipeCategory.MISC,
            net.minecraft.world.item.Items.GOLD_NUGGET,
            0.1F,
            100
         )
         .unlockedBy("has_golden_gloves", has((ItemLike)AetherItems.GOLDEN_GLOVES.get()))
         .unlockedBy("has_golden_pendant", has((ItemLike)AetherItems.GOLDEN_PENDANT.get()))
         .unlockedBy("has_golden_ring", has((ItemLike)AetherItems.GOLDEN_RING.get()))
         .unlockedBy("has_victory_medal", has((ItemLike)AetherItems.VICTORY_MEDAL.get()))
         .group(getBlastingRecipeName(net.minecraft.world.item.Items.GOLD_NUGGET))
         .save(consumer, this.name("aether_" + getBlastingRecipeName(net.minecraft.world.item.Items.GOLD_NUGGET)));
      SimpleCookingRecipeBuilder.blasting(
            Ingredient.of(
               new ItemLike[]{
                  (ItemLike)AetherItems.IRON_GLOVES.get(),
                  (ItemLike)AetherItems.IRON_PENDANT.get(),
                  (ItemLike)AetherItems.IRON_RING.get(),
                  (ItemLike)AetherItems.CHAINMAIL_GLOVES.get()
               }
            ),
            RecipeCategory.MISC,
            net.minecraft.world.item.Items.IRON_NUGGET,
            0.1F,
            100
         )
         .unlockedBy("has_iron_gloves", has((ItemLike)AetherItems.IRON_GLOVES.get()))
         .unlockedBy("has_iron_pendant", has((ItemLike)AetherItems.IRON_PENDANT.get()))
         .unlockedBy("has_iron_ring", has((ItemLike)AetherItems.IRON_RING.get()))
         .unlockedBy("has_chainmail_gloves", has((ItemLike)AetherItems.CHAINMAIL_GLOVES.get()))
         .group(getBlastingRecipeName(net.minecraft.world.item.Items.IRON_NUGGET))
         .save(consumer, this.name("aether_" + getBlastingRecipeName(net.minecraft.world.item.Items.IRON_NUGGET)));
      SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(new ItemLike[]{net.minecraft.world.item.Items.MUSIC_DISC_STAL}), RecipeCategory.MISC, AetherItems.MUSIC_DISC_KLEPTO, 1.0F, 200
         )
         .unlockedBy("has_disc", has((ItemLike)AetherItems.MUSIC_DISC_KLEPTO.get()))
         .save(consumer, this.name("klepto_smelting"));
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.CARVED_WALL.get(), (ItemLike)AetherBlocks.CARVED_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.CARVED_STAIRS.get(), (ItemLike)AetherBlocks.CARVED_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.CARVED_SLAB.get(), (ItemLike)AetherBlocks.CARVED_STONE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.SENTRY_STONE.get(), (ItemLike)AetherBlocks.CARVED_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.CARVED_STONE.get(), (ItemLike)AetherBlocks.SENTRY_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.ANGELIC_WALL.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ANGELIC_STAIRS.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ANGELIC_SLAB.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get(), 2
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.PILLAR.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.PILLAR_TOP.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ANGELIC_STONE.get(), (ItemLike)AetherBlocks.PILLAR.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ANGELIC_STONE.get(), (ItemLike)AetherBlocks.PILLAR_TOP.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.PILLAR_TOP.get(), (ItemLike)AetherBlocks.PILLAR.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.PILLAR.get(), (ItemLike)AetherBlocks.PILLAR_TOP.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.LIGHT_ANGELIC_STONE.get(), (ItemLike)AetherBlocks.ANGELIC_STONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ANGELIC_STONE.get(), (ItemLike)AetherBlocks.LIGHT_ANGELIC_STONE.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HELLFIRE_WALL.get(), (ItemLike)AetherBlocks.HELLFIRE_STONE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HELLFIRE_STAIRS.get(), (ItemLike)AetherBlocks.HELLFIRE_STONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HELLFIRE_SLAB.get(), (ItemLike)AetherBlocks.HELLFIRE_STONE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.LIGHT_HELLFIRE_STONE.get(), (ItemLike)AetherBlocks.HELLFIRE_STONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HELLFIRE_STONE.get(), (ItemLike)AetherBlocks.LIGHT_HELLFIRE_STONE.get()
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HOLYSTONE_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE.get());
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE_WALL.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get(), (ItemLike)AetherBlocks.MOSSY_HOLYSTONE.get(), 2
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_WALL.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get()
      );
      this.stonecuttingRecipe(
         consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(), (ItemLike)AetherBlocks.HOLYSTONE_BRICKS.get(), 2
      );
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.ICESTONE_WALL.get(), (ItemLike)AetherBlocks.ICESTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ICESTONE_STAIRS.get(), (ItemLike)AetherBlocks.ICESTONE.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ICESTONE_SLAB.get(), (ItemLike)AetherBlocks.ICESTONE.get(), 2);
      this.stonecuttingRecipe(consumer, RecipeCategory.DECORATIONS, (ItemLike)AetherBlocks.AEROGEL_WALL.get(), (ItemLike)AetherBlocks.AEROGEL.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.AEROGEL_STAIRS.get(), (ItemLike)AetherBlocks.AEROGEL.get());
      this.stonecuttingRecipe(consumer, RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.AEROGEL_SLAB.get(), (ItemLike)AetherBlocks.AEROGEL.get(), 2);
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.FISHING_ROD, 300).save(consumer, this.name("fishing_rod_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.FLINT_AND_STEEL, 300).save(consumer, this.name("flint_and_steel_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.BOW, 600).save(consumer, this.name("bow_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.CROSSBOW, 600).save(consumer, this.name("crossbow_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.SHIELD, 600).save(consumer, this.name("shield_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.SKYROOT_PICKAXE.get(), 250)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("skyroot_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.SKYROOT_AXE.get(), 250)
         .group("altar_axe_repair")
         .save(consumer, this.name("skyroot_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.SKYROOT_SHOVEL.get(), 250)
         .group("altar_shovel_repair")
         .save(consumer, this.name("skyroot_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.SKYROOT_HOE.get(), 250)
         .group("altar_hoe_repair")
         .save(consumer, this.name("skyroot_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.HOLYSTONE_PICKAXE.get(), 500)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("holystone_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.HOLYSTONE_AXE.get(), 500)
         .group("altar_axe_repair")
         .save(consumer, this.name("holystone_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.HOLYSTONE_SHOVEL.get(), 500)
         .group("altar_shovel_repair")
         .save(consumer, this.name("holystone_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.HOLYSTONE_HOE.get(), 500)
         .group("altar_hoe_repair")
         .save(consumer, this.name("holystone_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.ZANITE_PICKAXE.get(), 750)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("zanite_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.ZANITE_AXE.get(), 750)
         .group("altar_axe_repair")
         .save(consumer, this.name("zanite_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.ZANITE_SHOVEL.get(), 750)
         .group("altar_shovel_repair")
         .save(consumer, this.name("zanite_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.ZANITE_HOE.get(), 750)
         .group("altar_hoe_repair")
         .save(consumer, this.name("zanite_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.GRAVITITE_PICKAXE.get(), 1500)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("gravitite_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.GRAVITITE_AXE.get(), 1500)
         .group("altar_axe_repair")
         .save(consumer, this.name("gravitite_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.GRAVITITE_SHOVEL.get(), 1500)
         .group("altar_shovel_repair")
         .save(consumer, this.name("gravitite_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, (ItemLike)AetherItems.GRAVITITE_HOE.get(), 1500)
         .group("altar_hoe_repair")
         .save(consumer, this.name("gravitite_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.SKYROOT_SWORD.get(), 250)
         .group("altar_sword_repair")
         .save(consumer, this.name("skyroot_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.HOLYSTONE_SWORD.get(), 500)
         .group("altar_sword_repair")
         .save(consumer, this.name("holystone_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_SWORD.get(), 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("zanite_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GRAVITITE_SWORD.get(), 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("gravitite_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_HELMET.get(), 750)
         .group("altar_helmet_repair")
         .save(consumer, this.name("zanite_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_CHESTPLATE.get(), 750)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("zanite_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_LEGGINGS.get(), 750)
         .group("altar_leggings_repair")
         .save(consumer, this.name("zanite_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_BOOTS.get(), 750)
         .group("altar_boots_repair")
         .save(consumer, this.name("zanite_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_GLOVES.get(), 750)
         .group("altar_gloves_repair")
         .save(consumer, this.name("zanite_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GRAVITITE_HELMET.get(), 1500)
         .group("altar_helmet_repair")
         .save(consumer, this.name("gravitite_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GRAVITITE_CHESTPLATE.get(), 1500)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("gravitite_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GRAVITITE_LEGGINGS.get(), 1500)
         .group("altar_leggings_repair")
         .save(consumer, this.name("gravitite_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GRAVITITE_BOOTS.get(), 1500)
         .group("altar_boots_repair")
         .save(consumer, this.name("gravitite_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GRAVITITE_GLOVES.get(), 1500)
         .group("altar_gloves_repair")
         .save(consumer, this.name("gravitite_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.WOODEN_PICKAXE, 250)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("wooden_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.WOODEN_AXE, 250)
         .group("altar_axe_repair")
         .save(consumer, this.name("wooden_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.WOODEN_SHOVEL, 250)
         .group("altar_shovel_repair")
         .save(consumer, this.name("wooden_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.WOODEN_HOE, 250)
         .group("altar_hoe_repair")
         .save(consumer, this.name("wooden_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.STONE_PICKAXE, 500)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("stone_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.STONE_AXE, 500)
         .group("altar_axe_repair")
         .save(consumer, this.name("stone_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.STONE_SHOVEL, 500)
         .group("altar_shovel_repair")
         .save(consumer, this.name("stone_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.STONE_HOE, 500)
         .group("altar_hoe_repair")
         .save(consumer, this.name("stone_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.IRON_PICKAXE, 750)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("iron_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.IRON_AXE, 750)
         .group("altar_axe_repair")
         .save(consumer, this.name("iron_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.IRON_SHOVEL, 750)
         .group("altar_shovel_repair")
         .save(consumer, this.name("iron_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.IRON_HOE, 750)
         .group("altar_hoe_repair")
         .save(consumer, this.name("iron_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.GOLDEN_PICKAXE, 300)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("golden_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.GOLDEN_AXE, 300)
         .group("altar_axe_repair")
         .save(consumer, this.name("golden_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.GOLDEN_SHOVEL, 300)
         .group("altar_shovel_repair")
         .save(consumer, this.name("golden_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.GOLDEN_HOE, 300)
         .group("altar_hoe_repair")
         .save(consumer, this.name("golden_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.DIAMOND_PICKAXE, 1500)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("diamond_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.DIAMOND_AXE, 1500)
         .group("altar_axe_repair")
         .save(consumer, this.name("diamond_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.DIAMOND_SHOVEL, 1500)
         .group("altar_shovel_repair")
         .save(consumer, this.name("diamond_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.DIAMOND_HOE, 1500)
         .group("altar_hoe_repair")
         .save(consumer, this.name("diamond_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.NETHERITE_PICKAXE, 2000)
         .group("altar_pickaxe_repair")
         .save(consumer, this.name("netherite_pickaxe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.NETHERITE_AXE, 2000)
         .group("altar_axe_repair")
         .save(consumer, this.name("netherite_axe_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.NETHERITE_SHOVEL, 2000)
         .group("altar_shovel_repair")
         .save(consumer, this.name("netherite_shovel_repairing"));
      this.repairingRecipe(RecipeCategory.TOOLS, net.minecraft.world.item.Items.NETHERITE_HOE, 2000)
         .group("altar_hoe_repair")
         .save(consumer, this.name("netherite_hoe_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.WOODEN_SWORD, 250)
         .group("altar_sword_repair")
         .save(consumer, this.name("wooden_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.STONE_SWORD, 500)
         .group("altar_sword_repair")
         .save(consumer, this.name("stone_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.IRON_SWORD, 750)
         .group("altar_sword_repair")
         .save(consumer, this.name("iron_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.GOLDEN_SWORD, 300)
         .group("altar_sword_repair")
         .save(consumer, this.name("golden_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.DIAMOND_SWORD, 1500)
         .group("altar_sword_repair")
         .save(consumer, this.name("diamond_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.NETHERITE_SWORD, 2000)
         .group("altar_sword_repair")
         .save(consumer, this.name("netherite_sword_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.LEATHER_HELMET, 250)
         .group("altar_helmet_repair")
         .save(consumer, this.name("leather_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.LEATHER_CHESTPLATE, 250)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("leather_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.LEATHER_LEGGINGS, 250)
         .group("altar_leggings_repair")
         .save(consumer, this.name("leather_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.LEATHER_BOOTS, 250)
         .group("altar_boots_repair")
         .save(consumer, this.name("leather_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.LEATHER_GLOVES.get(), 250)
         .group("altar_gloves_repair")
         .save(consumer, this.name("leather_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.IRON_HELMET, 750)
         .group("altar_helmet_repair")
         .save(consumer, this.name("iron_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.IRON_CHESTPLATE, 750)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("iron_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.IRON_LEGGINGS, 750)
         .group("altar_leggings_repair")
         .save(consumer, this.name("iron_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.IRON_BOOTS, 750)
         .group("altar_boots_repair")
         .save(consumer, this.name("iron_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.IRON_GLOVES.get(), 750)
         .group("altar_gloves_repair")
         .save(consumer, this.name("iron_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.GOLDEN_HELMET, 300)
         .group("altar_helmet_repair")
         .save(consumer, this.name("golden_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.GOLDEN_CHESTPLATE, 300)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("golden_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.GOLDEN_LEGGINGS, 300)
         .group("altar_leggings_repair")
         .save(consumer, this.name("golden_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.GOLDEN_BOOTS, 300)
         .group("altar_boots_repair")
         .save(consumer, this.name("golden_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.GOLDEN_GLOVES.get(), 300)
         .group("altar_gloves_repair")
         .save(consumer, this.name("golden_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.CHAINMAIL_HELMET, 700)
         .group("altar_helmet_repair")
         .save(consumer, this.name("chainmail_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE, 700)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("chainmail_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.CHAINMAIL_LEGGINGS, 700)
         .group("altar_leggings_repair")
         .save(consumer, this.name("chainmail_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.CHAINMAIL_BOOTS, 700)
         .group("altar_boots_repair")
         .save(consumer, this.name("chainmail_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.CHAINMAIL_GLOVES.get(), 700)
         .group("altar_gloves_repair")
         .save(consumer, this.name("chainmail_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.DIAMOND_HELMET, 1500)
         .group("altar_helmet_repair")
         .save(consumer, this.name("diamond_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.DIAMOND_CHESTPLATE, 1500)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("diamond_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.DIAMOND_LEGGINGS, 1500)
         .group("altar_leggings_repair")
         .save(consumer, this.name("diamond_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.DIAMOND_BOOTS, 1500)
         .group("altar_boots_repair")
         .save(consumer, this.name("diamond_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.DIAMOND_GLOVES.get(), 1500)
         .group("altar_gloves_repair")
         .save(consumer, this.name("diamond_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.NETHERITE_HELMET, 2000)
         .group("altar_helmet_repair")
         .save(consumer, this.name("netherite_helmet_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.NETHERITE_CHESTPLATE, 2000)
         .group("altar_chestplate_repair")
         .save(consumer, this.name("netherite_chestplate_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.NETHERITE_LEGGINGS, 2000)
         .group("altar_leggings_repair")
         .save(consumer, this.name("netherite_leggings_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, net.minecraft.world.item.Items.NETHERITE_BOOTS, 2000)
         .group("altar_boots_repair")
         .save(consumer, this.name("netherite_boots_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.NETHERITE_GLOVES.get(), 2000)
         .group("altar_gloves_repair")
         .save(consumer, this.name("netherite_gloves_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_RING.get(), 500)
         .group("altar_ring_repair")
         .save(consumer, this.name("zanite_ring_repairing"));
      this.repairingRecipe(RecipeCategory.COMBAT, (ItemLike)AetherItems.ZANITE_PENDANT.get(), 500)
         .group("altar_pendant_repair")
         .save(consumer, this.name("zanite_pendant_repairing"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)AetherItems.ENCHANTED_DART.get(), (ItemLike)AetherItems.GOLDEN_DART.get(), 0.15F, 50)
         .save(consumer, this.name("enchanted_dart_enchanting"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)AetherItems.ENCHANTED_DART_SHOOTER.get(), (ItemLike)AetherItems.GOLDEN_DART_SHOOTER.get(), 1.0F, 750)
         .save(consumer, this.name("enchanted_dart_shooter_enchanting"));
      this.enchantingRecipe(RecipeCategory.FOOD, (ItemLike)AetherItems.HEALING_STONE.get(), (ItemLike)AetherBlocks.HOLYSTONE.get(), 0.35F, 500)
         .save(consumer, this.name("healing_stone_enchanting"));
      this.enchantingRecipe(
            RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get(), (ItemLike)AetherBlocks.GRAVITITE_ORE.get(), 1.0F, 750
         )
         .save(consumer, this.name("enchanted_gravitite_enchanting"));
      this.enchantingRecipe(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.QUICKSOIL_GLASS.get(), (ItemLike)AetherBlocks.QUICKSOIL.get(), 0.1F, 250)
         .save(consumer, this.name("quicksoil_glass_enchanting"));
      this.enchantingRecipe(
            RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.GOLDEN_AERCLOUD.get(), (ItemLike)AetherBlocks.COLD_AERCLOUD.get(), 0.1F, 1000
         )
         .save(consumer, this.name("golden_aercloud_enchanting"));
      this.enchantingRecipe(RecipeCategory.FOOD, (ItemLike)AetherItems.ENCHANTED_BERRY.get(), (ItemLike)AetherItems.BLUE_BERRY.get(), 0.35F, 250)
         .save(consumer, this.name("enchanted_berry_enchanting"));
      this.enchantingRecipe(RecipeCategory.MISC, (ItemLike)AetherItems.MUSIC_DISC_AETHER_TUNE.get(), AetherTags.Items.ACCEPTED_MUSIC_DISCS, 1.0F, 500, "disc")
         .save(consumer, this.name("aether_tune_enchanting"));
      this.hiddenEnchantingRecipe(
            RecipeCategory.MISC, (ItemLike)AetherItems.MUSIC_DISC_CHINCHILLA.get(), net.minecraft.world.item.Items.MUSIC_DISC_STRAD, 1.0F, 500
         )
         .save(consumer, this.name("chinchilla_enchanting"));
      this.enchantingRecipe(
            RecipeCategory.MISC, (ItemLike)AetherItems.SKYROOT_REMEDY_BUCKET.get(), (ItemLike)AetherItems.SKYROOT_POISON_BUCKET.get(), 0.35F, 500
         )
         .save(consumer, this.name("remedy_bucket_enchanting"));
      this.freezingRecipe(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.BLUE_AERCLOUD.get(), (ItemLike)AetherBlocks.COLD_AERCLOUD.get(), 0.1F, 400)
         .save(consumer, this.name("blue_aercloud_freezing"));
      this.freezingRecipe(RecipeCategory.BUILDING_BLOCKS, (ItemLike)AetherBlocks.CRYSTAL_LEAVES.get(), (ItemLike)AetherBlocks.SKYROOT_LEAVES.get(), 0.1F, 500)
         .save(consumer, this.name("crystal_leaves_freezing"));
      this.hiddenFreezingRecipe(RecipeCategory.MISC, (ItemLike)AetherItems.MUSIC_DISC_HIGH.get(), (ItemLike)AetherItems.MUSIC_DISC_CHINCHILLA.get(), 1.0F, 500)
         .save(consumer, this.name("high_freezing"));
      this.freezingRecipeWithUnlockTag(RecipeCategory.BUILDING_BLOCKS, Blocks.BLUE_ICE, Blocks.PACKED_ICE, Items.BUCKETS_WATER, 0.1F, 500, "water_bucket")
         .save(consumer, this.name("blue_ice_freezing"));
      this.freezingRecipeWithUnlockTag(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_ICE, Blocks.ICE, Items.BUCKETS_WATER, 0.1F, 300, "water_bucket")
         .save(consumer, this.name("packed_ice_freezing"));
      this.freezingRecipeWithTag(RecipeCategory.BUILDING_BLOCKS, Blocks.ICE, Items.BUCKETS_WATER, 0.1F, 200, "water_bucket")
         .save(consumer, this.name("ice_from_bucket_freezing"));
      this.freezingRecipeWithTag(RecipeCategory.BUILDING_BLOCKS, Blocks.OBSIDIAN, Items.BUCKETS_LAVA, 0.1F, 200, "lava_bucket")
         .save(consumer, this.name("obsidian_from_bucket_freezing"));
      this.freezingRecipeWithTag(RecipeCategory.MISC, (ItemLike)AetherItems.ICE_RING.get(), AetherTags.Items.FREEZABLE_RINGS, 1.0F, 800, "ring")
         .save(consumer, this.name("ice_ring_from_freezing"));
      this.freezingRecipeWithTag(RecipeCategory.MISC, (ItemLike)AetherItems.ICE_PENDANT.get(), AetherTags.Items.FREEZABLE_PENDANTS, 1.0F, 800, "pendant")
         .save(consumer, this.name("ice_pendant_from_freezing"));
      this.moaIncubationRecipe((EntityType<?>)AetherEntityTypes.MOA.get(), AetherMoaTypes.BLUE, (ItemLike)AetherItems.BLUE_MOA_EGG.get())
         .save(consumer, this.name("blue_moa_incubation"));
      this.moaIncubationRecipe((EntityType<?>)AetherEntityTypes.MOA.get(), AetherMoaTypes.WHITE, (ItemLike)AetherItems.WHITE_MOA_EGG.get())
         .save(consumer, this.name("white_moa_incubation"));
      this.moaIncubationRecipe((EntityType<?>)AetherEntityTypes.MOA.get(), AetherMoaTypes.BLACK, (ItemLike)AetherItems.BLACK_MOA_EGG.get())
         .save(consumer, this.name("black_moa_incubation"));
      this.ambrosiumEnchanting((Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_GRASS_BLOCK.get())
         .save(consumer, this.name("ambrosium_enchant_aether_grass_to_enchanted_aether_grass"));
      this.swetBallConversion(Blocks.GRASS_BLOCK, Blocks.DIRT).save(consumer, this.name("swet_ball_dirt_to_grass"));
      this.swetBallConversion((Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT.get())
         .save(consumer, this.name("swet_ball_aether_dirt_to_aether_grass"));
      this.swetBallConversionTag(Blocks.MYCELIUM, Blocks.DIRT, AetherTags.Biomes.MYCELIUM_CONVERSION).save(consumer, this.name("swet_ball_dirt_to_mycelium"));
      this.swetBallConversionTag(Blocks.PODZOL, Blocks.GRASS_BLOCK, AetherTags.Biomes.PODZOL_CONVERSION).save(consumer, this.name("swet_ball_grass_to_podzol"));
      this.swetBallConversionTag(Blocks.CRIMSON_NYLIUM, Blocks.NETHERRACK, AetherTags.Biomes.CRIMSON_NYLIUM_CONVERSION)
         .save(consumer, this.name("swet_ball_netherrack_to_crimson_nylium"));
      this.swetBallConversionTag(Blocks.WARPED_NYLIUM, Blocks.NETHERRACK, AetherTags.Biomes.WARPED_NYLIUM_CONVERSION)
         .save(consumer, this.name("swet_ball_netherrack_to_warped_nylium"));
      this.icestoneFreezable(Blocks.ICE, Blocks.WATER).save(consumer, this.name("icestone_freeze_water"));
      this.icestoneFreezable(Blocks.OBSIDIAN, Blocks.LAVA).save(consumer, this.name("icestone_freeze_lava"));
      this.accessoryFreezable(Blocks.ICE, Blocks.WATER).save(consumer, this.name("accessory_freeze_water"));
      this.accessoryFreezable(Blocks.OBSIDIAN, Blocks.LAVA).save(consumer, this.name("accessory_freeze_lava"));
      this.convertPlacement((Block)AetherBlocks.AEROGEL.get(), Blocks.LAVA, AetherTags.Biomes.ULTRACOLD).save(consumer, this.name("aerogel_conversion"));
      this.convertPlacementWithProperties(
            Blocks.CAMPFIRE, this.map(CampfireBlock.LIT, false), Blocks.CAMPFIRE, this.map(CampfireBlock.LIT, true), AetherTags.Biomes.ULTRACOLD
         )
         .save(consumer, this.name("campfire_conversion"));

      for (Block candle : List.of(
         Blocks.CANDLE,
         Blocks.WHITE_CANDLE,
         Blocks.ORANGE_CANDLE,
         Blocks.MAGENTA_CANDLE,
         Blocks.LIGHT_BLUE_CANDLE,
         Blocks.YELLOW_CANDLE,
         Blocks.LIME_CANDLE,
         Blocks.PINK_CANDLE,
         Blocks.GRAY_CANDLE,
         Blocks.LIGHT_GRAY_CANDLE,
         Blocks.CYAN_CANDLE,
         Blocks.PURPLE_CANDLE,
         Blocks.BLUE_CANDLE,
         Blocks.BROWN_CANDLE,
         Blocks.GREEN_CANDLE,
         Blocks.RED_CANDLE,
         Blocks.BLACK_CANDLE
      )) {
         this.convertPlacementWithProperties(candle, this.map(CandleBlock.LIT, false), candle, this.map(CandleBlock.LIT, true), AetherTags.Biomes.ULTRACOLD)
            .save(consumer, this.name(candle.getDescriptionId().replace(".", "_").replace("block_minecraft_", "") + "_conversion"));
      }

      for (Block candleCake : List.of(
         Blocks.CANDLE_CAKE,
         Blocks.WHITE_CANDLE_CAKE,
         Blocks.ORANGE_CANDLE_CAKE,
         Blocks.MAGENTA_CANDLE_CAKE,
         Blocks.LIGHT_BLUE_CANDLE_CAKE,
         Blocks.YELLOW_CANDLE_CAKE,
         Blocks.LIME_CANDLE_CAKE,
         Blocks.PINK_CANDLE_CAKE,
         Blocks.GRAY_CANDLE_CAKE,
         Blocks.LIGHT_GRAY_CANDLE_CAKE,
         Blocks.CYAN_CANDLE_CAKE,
         Blocks.PURPLE_CANDLE_CAKE,
         Blocks.BLUE_CANDLE_CAKE,
         Blocks.BROWN_CANDLE_CAKE,
         Blocks.GREEN_CANDLE_CAKE,
         Blocks.RED_CANDLE_CAKE,
         Blocks.BLACK_CANDLE_CAKE
      )) {
         this.convertPlacementWithProperties(
               candleCake, this.map(CandleCakeBlock.LIT, false), candleCake, this.map(CandleCakeBlock.LIT, true), AetherTags.Biomes.ULTRACOLD
            )
            .save(consumer, this.name(candleCake.getDescriptionId().replace(".", "_").replace("block_minecraft_", "") + "_conversion"));
      }

      this.convertPlacement(Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, AetherTags.Biomes.ULTRACOLD).save(consumer, this.name("jack_o_lantern_conversion"));
      this.banItemPlacementWithBypass(net.minecraft.world.item.Items.FLINT_AND_STEEL, AetherTags.Blocks.ALLOWED_FLAMMABLES, AetherTags.Biomes.ULTRACOLD)
         .save(consumer, this.name("flint_and_steel_item_ban"));
      this.banItemPlacementWithBypass(net.minecraft.world.item.Items.FIRE_CHARGE, AetherTags.Blocks.ALLOWED_FLAMMABLES, AetherTags.Biomes.ULTRACOLD)
         .save(consumer, this.name("fire_charge_item_ban"));
      this.banItemPlacement(net.minecraft.world.item.Items.TORCH, AetherTags.Biomes.ULTRACOLD).save(consumer, this.name("torch_item_ban"));
      this.banItemPlacement(net.minecraft.world.item.Items.LANTERN, AetherTags.Biomes.ULTRACOLD).save(consumer, this.name("lantern_item_ban"));
      this.banBlockPlacementWithBypass(Blocks.FIRE, AetherTags.Blocks.ALLOWED_FLAMMABLES, AetherTags.Biomes.ULTRACOLD)
         .save(consumer, this.name("fire_block_ban"));
      this.banBlockPlacement(Blocks.TORCH, AetherTags.Biomes.ULTRACOLD).save(consumer, this.name("torch_block_ban"));
      this.banBlockPlacement(Blocks.LANTERN, AetherTags.Biomes.ULTRACOLD).save(consumer, this.name("lantern_block_ban"));
   }
}
