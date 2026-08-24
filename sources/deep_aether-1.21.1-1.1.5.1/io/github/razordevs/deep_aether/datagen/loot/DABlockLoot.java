package io.github.razordevs.deep_aether.datagen.loot;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.providers.AetherBlockLootSubProvider;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.functions.DoubleDrops;
import com.aetherteam.aether.mixin.mixins.common.accessor.BlockLootAccessor;
import io.github.razordevs.deep_aether.block.behavior.GoldenVines;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class DABlockLoot extends AetherBlockLootSubProvider {
   private static final Set<Item> EXPLOSION_RESISTANT = Stream.of((Block)AetherBlocks.TREASURE_CHEST.get())
      .<Item>map(ItemLike::asItem)
      .collect(Collectors.toSet());

   public DABlockLoot(Provider registries) {
      super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
   }

   public Iterable<Block> getKnownBlocks() {
      return DABlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
   }

   public void generate() {
      this.dropSelf((Block)DABlocks.HIGHSTONE.get());
      this.dropOther((Block)DABlocks.TRAPPED_SKYROOT_PLANKS.get(), (ItemLike)AetherBlocks.SKYROOT_PLANKS.get());
      this.dropOther((Block)DABlocks.LOCKED_SKYROOT_PLANKS.get(), (ItemLike)AetherBlocks.SKYROOT_PLANKS.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_WOOD.get());
      this.dropSelf((Block)DABlocks.ROTTEN_ROSEROOT_LOG.get());
      this.dropSelf((Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get());
      this.dropSelfDouble((Block)DABlocks.ROSEROOT_LOG.get());
      this.dropSelf((Block)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_PLANKS.get());
      this.add((Block)DABlocks.ROSEROOT_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.ROSEROOT_STAIRS.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_FENCE.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_FENCE_GATE.get());
      this.add((Block)DABlocks.ROSEROOT_DOOR.get(), this.createDoorTable((Block)DABlocks.ROSEROOT_DOOR.get()));
      this.dropSelf((Block)DABlocks.ROSEROOT_TRAPDOOR.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_BUTTON.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_PRESSURE_PLATE.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_SAPLING.get());
      this.dropPottedContents((Block)DABlocks.POTTED_ROSEROOT_SAPLING.get());
      this.dropSelf((Block)DABlocks.BLUE_ROSEROOT_SAPLING.get());
      this.dropPottedContents((Block)DABlocks.POTTED_BLUE_ROSEROOT_SAPLING.get());
      this.add(
         (Block)DABlocks.ROSEROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)DABlocks.ROSEROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.add(
         (Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticksAndAerglowPetal(
            leaves, (Block)DABlocks.ROSEROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.add(
         (Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticksAndAerglowPetal(
            leaves, (Block)DABlocks.BLUE_ROSEROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.dropOther((Block)DABlocks.ROSEROOT_WALL_SIGN.get(), (ItemLike)DABlocks.ROSEROOT_SIGN.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_SIGN.get());
      this.dropOther((Block)DABlocks.ROSEROOT_WALL_HANGING_SIGN.get(), (ItemLike)DABlocks.ROSEROOT_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_HANGING_SIGN.get());
      this.add(
         (Block)DABlocks.BLUE_ROSEROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)DABlocks.BLUE_ROSEROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.dropSelf((Block)DABlocks.AERGLOW_BLOSSOM_BLOCK.get());
      this.dropSelf((Block)DABlocks.YAGROOT_WOOD.get());
      this.dropSelf((Block)DABlocks.STRIPPED_YAGROOT_WOOD.get());
      this.dropSelfDouble((Block)DABlocks.YAGROOT_LOG.get());
      this.dropSelf((Block)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.dropSelf((Block)DABlocks.YAGROOT_PLANKS.get());
      this.add((Block)DABlocks.YAGROOT_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.YAGROOT_STAIRS.get());
      this.dropSelf((Block)DABlocks.YAGROOT_FENCE.get());
      this.dropSelf((Block)DABlocks.YAGROOT_FENCE_GATE.get());
      this.add((Block)DABlocks.YAGROOT_DOOR.get(), this.createDoorTable((Block)DABlocks.YAGROOT_DOOR.get()));
      this.dropSelf((Block)DABlocks.YAGROOT_TRAPDOOR.get());
      this.dropSelf((Block)DABlocks.YAGROOT_BUTTON.get());
      this.dropSelf((Block)DABlocks.YAGROOT_PRESSURE_PLATE.get());
      this.dropSelf((Block)DABlocks.YAGROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.YAGROOT_SAPLING.get());
      this.dropPottedContents((Block)DABlocks.POTTED_YAGROOT_SAPLING.get());
      this.add(
         (Block)DABlocks.YAGROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)DABlocks.YAGROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.dropOther((Block)DABlocks.YAGROOT_WALL_SIGN.get(), (ItemLike)DABlocks.YAGROOT_SIGN.get());
      this.dropSelf((Block)DABlocks.YAGROOT_SIGN.get());
      this.dropOther((Block)DABlocks.YAGROOT_WALL_HANGING_SIGN.get(), (ItemLike)DABlocks.YAGROOT_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.YAGROOT_HANGING_SIGN.get());
      this.dropSelfDouble((Block)DABlocks.YAGROOT_ROOTS.get());
      this.dropSelfDouble((Block)DABlocks.AERCLOUD_ROOTS.get());
      this.dropSelf((Block)DABlocks.MUDDY_YAGROOT_ROOTS.get());
      this.add((Block)DABlocks.YAGROOT_VINE.get(), vine -> createVinesDrop((Block)DABlocks.YAGROOT_VINE.get()));
      this.add((Block)DABlocks.GLOWING_VINE.get(), vine -> createVinesDrop(Blocks.VINE));
      this.dropSelf((Block)DABlocks.CRUDEROOT_WOOD.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CRUDEROOT_WOOD.get());
      this.dropSelfDouble((Block)DABlocks.CRUDEROOT_LOG.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.add((Block)DABlocks.CRUDEROOT_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.CRUDEROOT_STAIRS.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_FENCE.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_FENCE_GATE.get());
      this.add((Block)DABlocks.CRUDEROOT_DOOR.get(), this.createDoorTable((Block)DABlocks.CRUDEROOT_DOOR.get()));
      this.dropSelf((Block)DABlocks.CRUDEROOT_TRAPDOOR.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_BUTTON.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_PRESSURE_PLATE.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_SAPLING.get());
      this.dropPottedContents((Block)DABlocks.POTTED_CRUDEROOT_SAPLING.get());
      this.add(
         (Block)DABlocks.CRUDEROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)DABlocks.CRUDEROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.dropOther((Block)DABlocks.CRUDEROOT_WALL_SIGN.get(), (ItemLike)DABlocks.CRUDEROOT_SIGN.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_SIGN.get());
      this.dropOther((Block)DABlocks.CRUDEROOT_WALL_HANGING_SIGN.get(), (ItemLike)DABlocks.CRUDEROOT_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.CONBERRY_WOOD.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CONBERRY_WOOD.get());
      this.dropSelfDouble((Block)DABlocks.CONBERRY_LOG.get());
      this.dropSelfDouble((Block)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.dropSelf((Block)DABlocks.CONBERRY_PLANKS.get());
      this.add((Block)DABlocks.CONBERRY_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.CONBERRY_STAIRS.get());
      this.dropSelf((Block)DABlocks.CONBERRY_FENCE.get());
      this.dropSelf((Block)DABlocks.CONBERRY_FENCE_GATE.get());
      this.add((Block)DABlocks.CONBERRY_DOOR.get(), this.createDoorTable((Block)DABlocks.CONBERRY_DOOR.get()));
      this.dropSelf((Block)DABlocks.CONBERRY_TRAPDOOR.get());
      this.dropSelf((Block)DABlocks.CONBERRY_BUTTON.get());
      this.dropSelf((Block)DABlocks.CONBERRY_PRESSURE_PLATE.get());
      this.dropSelf((Block)DABlocks.CONBERRY_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.CONBERRY_SAPLING.get());
      this.dropPottedContents((Block)DABlocks.POTTED_CONBERRY_SAPLING.get());
      this.add(
         (Block)DABlocks.CONBERRY_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)DABlocks.CONBERRY_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.dropOther((Block)DABlocks.CONBERRY_WALL_SIGN.get(), (ItemLike)DABlocks.CONBERRY_SIGN.get());
      this.dropSelf((Block)DABlocks.CONBERRY_SIGN.get());
      this.dropOther((Block)DABlocks.CONBERRY_WALL_HANGING_SIGN.get(), (ItemLike)DABlocks.CONBERRY_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.CONBERRY_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.SUNROOT_WOOD.get());
      this.dropSelf((Block)DABlocks.STRIPPED_SUNROOT_WOOD.get());
      this.dropSelfDouble((Block)DABlocks.SUNROOT_LOG.get());
      this.dropSelfDouble((Block)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.dropSelf((Block)DABlocks.SUNROOT_PLANKS.get());
      this.add((Block)DABlocks.SUNROOT_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.SUNROOT_STAIRS.get());
      this.dropSelf((Block)DABlocks.SUNROOT_FENCE.get());
      this.dropSelf((Block)DABlocks.SUNROOT_FENCE_GATE.get());
      this.add((Block)DABlocks.SUNROOT_DOOR.get(), this.createDoorTable((Block)DABlocks.SUNROOT_DOOR.get()));
      this.dropSelf((Block)DABlocks.SUNROOT_TRAPDOOR.get());
      this.dropSelf((Block)DABlocks.SUNROOT_BUTTON.get());
      this.dropSelf((Block)DABlocks.SUNROOT_PRESSURE_PLATE.get());
      this.dropSelf((Block)DABlocks.SUNROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get());
      this.dropSelf((Block)DABlocks.SUNROOT_SAPLING.get());
      this.dropPottedContents((Block)DABlocks.POTTED_SUNROOT_SAPLING.get());
      this.add(
         (Block)DABlocks.SUNROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)DABlocks.SUNROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.dropOther((Block)DABlocks.SUNROOT_WALL_SIGN.get(), (ItemLike)DABlocks.SUNROOT_SIGN.get());
      this.dropSelf((Block)DABlocks.SUNROOT_SIGN.get());
      this.dropOther((Block)DABlocks.SUNROOT_WALL_HANGING_SIGN.get(), (ItemLike)DABlocks.SUNROOT_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.SUNROOT_HANGING_SIGN.get());
      this.dropSelf((Block)DABlocks.SUNROOT_HANGER.get());
      this.dropSelf((Block)DABlocks.MOA_TOTEM.get());
      this.dropSelf((Block)DABlocks.ZEPHYR_TOTEM.get());
      this.dropSelf((Block)DABlocks.AERWHALE_TOTEM.get());
      this.dropSelfDouble((Block)DABlocks.AETHER_MUD.get());
      this.dropSelf((Block)DABlocks.PACKED_AETHER_MUD.get());
      this.dropSelf((Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.add((Block)DABlocks.AETHER_MUD_BRICKS_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.AETHER_MUD_BRICKS_STAIRS.get());
      this.dropSelf((Block)DABlocks.AETHER_MUD_BRICKS_WALL.get());
      this.add((Block)DABlocks.SKYJADE_ORE.get(), block -> this.createOreDrop(block, (Item)DAItems.SKYJADE.get()));
      this.dropSelf((Block)DABlocks.SKYJADE_BLOCK.get());
      this.dropSelf((Block)DABlocks.STRATUS_BLOCK.get());
      this.dropSelf((Block)DABlocks.COBBLED_ASETERITE.get());
      this.add((Block)DABlocks.COBBLED_ASETERITE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.COBBLED_ASETERITE_STAIRS.get());
      this.dropSelf((Block)DABlocks.COBBLED_ASETERITE_WALL.get());
      this.dropDoubleWithSilk((Block)DABlocks.ASETERITE.get(), (ItemLike)DABlocks.COBBLED_ASETERITE.get());
      this.add((Block)DABlocks.ASETERITE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.ASETERITE_STAIRS.get());
      this.dropSelf((Block)DABlocks.ASETERITE_WALL.get());
      this.dropSelf((Block)DABlocks.POLISHED_ASETERITE.get());
      this.add((Block)DABlocks.POLISHED_ASETERITE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.POLISHED_ASETERITE_STAIRS.get());
      this.dropSelf((Block)DABlocks.POLISHED_ASETERITE_WALL.get());
      this.dropSelf((Block)DABlocks.ASETERITE_BRICKS.get());
      this.add((Block)DABlocks.ASETERITE_BRICKS_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.ASETERITE_BRICKS_STAIRS.get());
      this.dropSelf((Block)DABlocks.ASETERITE_BRICKS_WALL.get());
      this.dropSelfDouble((Block)DABlocks.RAW_CLORITE.get());
      this.add((Block)DABlocks.RAW_CLORITE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.RAW_CLORITE_STAIRS.get());
      this.dropSelf((Block)DABlocks.RAW_CLORITE_WALL.get());
      this.dropSelf((Block)DABlocks.CLORITE.get());
      this.add((Block)DABlocks.CLORITE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.CLORITE_STAIRS.get());
      this.dropSelf((Block)DABlocks.CLORITE_WALL.get());
      this.dropSelf((Block)DABlocks.POLISHED_CLORITE.get());
      this.add((Block)DABlocks.POLISHED_CLORITE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.POLISHED_CLORITE_STAIRS.get());
      this.dropSelf((Block)DABlocks.POLISHED_CLORITE_WALL.get());
      this.dropSelf((Block)DABlocks.CLORITE_PILLAR.get());
      this.dropSelf((Block)DABlocks.HOLYSTONE_TILES.get());
      this.add((Block)DABlocks.HOLYSTONE_TILE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.HOLYSTONE_TILE_STAIRS.get());
      this.dropSelf((Block)DABlocks.HOLYSTONE_TILE_WALL.get());
      this.dropSelf((Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.dropSelf((Block)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get());
      this.add((Block)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get());
      this.dropSelf((Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.dropSelf((Block)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get());
      this.add((Block)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get());
      this.dropSelf((Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.add((Block)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get());
      this.dropSelf((Block)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get());
      this.dropSelf((Block)DABlocks.HOLYSTONE_PILLAR.get());
      this.dropSelf((Block)DABlocks.HOLYSTONE_PILLAR_UP.get());
      this.dropSelf((Block)DABlocks.HOLYSTONE_PILLAR_DOWN.get());
      this.dropSelf((Block)DABlocks.CHISELED_HOLYSTONE.get());
      this.dropSelf((Block)DABlocks.AETHER_MOSS_BLOCK.get());
      this.dropSelf((Block)DABlocks.AETHER_MOSS_CARPET.get());
      this.dropSelf((Block)DABlocks.CLOUDBLOOM_CARPET.get());
      this.dropSelf((Block)DABlocks.BLUE_SQUASH.get());
      this.dropSelf((Block)DABlocks.GREEN_SQUASH.get());
      this.dropSelf((Block)DABlocks.PURPLE_SQUASH.get());
      this.dropSelf((Block)DABlocks.CARVED_BLUE_SQUASH.get());
      this.dropSelf((Block)DABlocks.CARVED_GREEN_SQUASH.get());
      this.dropSelf((Block)DABlocks.CARVED_PURPLE_SQUASH.get());
      this.dropNone((Block)DABlocks.SQUASH_STEM.get());
      this.dropNone((Block)DABlocks.ATTACHED_SQUASH_STEM.get());
      this.add((Block)DABlocks.MINI_GOLDEN_GRASS.get(), this::createGoldenGrassDrops);
      this.add((Block)DABlocks.SHORT_GOLDEN_GRASS.get(), this::createGoldenGrassDrops);
      this.add((Block)DABlocks.MEDIUM_GOLDEN_GRASS.get(), this::createGoldenGrassDrops);
      this.add((Block)DABlocks.TALL_GOLDEN_GRASS.get(), grass -> this.createGoldenDoublePlantWithSeedDrops(grass, (Block)DABlocks.MEDIUM_GOLDEN_GRASS.get()));
      this.add(
         (Block)DABlocks.TALL_GLOWING_GRASS.get(),
         flower -> this.createSinglePropConditionTable(Blocks.TALL_GRASS, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
      );
      this.dropSelf((Block)DABlocks.TALL_ALIEN_PLANT.get());
      this.add((Block)DABlocks.FEATHER_GRASS.get(), this::createFeatherGrassDrops);
      this.add((Block)DABlocks.TALL_FEATHER_GRASS.get(), this::createFeatherGrassDrops);
      this.add(
         (Block)DABlocks.GOLDEN_FLOWER.get(),
         flower -> this.createSinglePropConditionTable((Block)DABlocks.GOLDEN_FLOWER.get(), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
      );
      this.add((Block)DABlocks.GLOWING_SPORES.get(), this.createPetalsDrops((Block)DABlocks.GLOWING_SPORES.get()));
      this.dropSelf((Block)DABlocks.ENCHANTED_BLOSSOM.get());
      this.add((Block)DABlocks.GOLDEN_VINES.get(), DABlockLoot::createGoldenVinesDrop);
      this.add((Block)DABlocks.GOLDEN_VINES_PLANT.get(), DABlockLoot::createGoldenVinesDrop);
      this.dropSelf((Block)DABlocks.LIGHTCAP_MUSHROOMS.get());
      this.add((Block)DABlocks.LIGHTCAP_MUSHROOM_BLOCK.get(), block -> this.createMushroomBlockDrop(block, (ItemLike)DABlocks.LIGHTCAP_MUSHROOMS.get()));
      this.add((Block)DABlocks.PINK_AERCLOUD_MUSHROOM_BLOCK.get(), block -> this.createMushroomBlockDrop(block, (ItemLike)DABlocks.LIGHTCAP_MUSHROOMS.get()));
      this.add((Block)DABlocks.BLUE_AERCLOUD_MUSHROOM_BLOCK.get(), block -> this.createMushroomBlockDrop(block, (ItemLike)DABlocks.LIGHTCAP_MUSHROOMS.get()));
      this.dropSelf((Block)DABlocks.AERCLOUD_ROOT_CARPET.get());
      this.dropSelf((Block)DABlocks.AERLAVENDER.get());
      this.dropSelf((Block)DABlocks.AETHER_CATTAILS.get());
      this.dropSelf((Block)DABlocks.TALL_AERLAVENDER.get());
      this.add(
         (Block)DABlocks.TALL_AETHER_CATTAILS.get(),
         flower -> this.createSinglePropConditionTable((Block)DABlocks.TALL_AETHER_CATTAILS.get(), DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
      );
      this.dropSelf((Block)DABlocks.RADIANT_ORCHID.get());
      this.dropSelf((Block)DABlocks.SKY_TULIPS.get());
      this.dropSelf((Block)DABlocks.IASPOVE.get());
      this.dropSelf((Block)DABlocks.GOLDEN_ASPESS.get());
      this.dropSelf((Block)DABlocks.ECHAISY.get());
      this.dropPottedContents((Block)DABlocks.POTTED_AERLAVENDER.get());
      this.dropPottedContents((Block)DABlocks.POTTED_TALL_AERLAVENDER.get());
      this.dropPottedContents((Block)DABlocks.POTTED_AETHER_CATTAILS.get());
      this.dropPottedContents((Block)DABlocks.POTTED_RADIANT_ORCHID.get());
      this.dropPottedContents((Block)DABlocks.POTTED_ENCHANTED_BLOSSOM.get());
      this.dropPottedContents((Block)DABlocks.POTTED_SKY_TULIPS.get());
      this.dropPottedContents((Block)DABlocks.POTTED_IASPOVE.get());
      this.dropPottedContents((Block)DABlocks.POTTED_GOLDEN_ASPESS.get());
      this.dropPottedContents((Block)DABlocks.POTTED_ECHAISY.get());
      this.dropSelf((Block)DABlocks.NIMBUS_STONE.get());
      this.dropSelf((Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.dropSelf((Block)DABlocks.NIMBUS_STAIRS.get());
      this.dropSelf((Block)DABlocks.NIMBUS_SLAB.get());
      this.dropSelf((Block)DABlocks.NIMBUS_WALL.get());
      this.dropNone((Block)DABlocks.LOCKED_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.LOCKED_LIGHT_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.TRAPPED_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.TRAPPED_LIGHT_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.BOSS_DOORWAY_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.TREASURE_DOORWAY_NIMBUS_STONE.get());
      this.dropNone((Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_STONE.get());
      this.dropSelf((Block)DABlocks.NIMBUS_PILLAR.get());
      this.dropSelf((Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.LOCKED_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.LOCKED_LIGHT_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.TRAPPED_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.TRAPPED_LIGHT_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.BOSS_DOORWAY_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.TREASURE_DOORWAY_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_PILLAR.get());
      this.dropNone((Block)DABlocks.VIRULENT_QUICKSAND.get());
      this.dropSelf((Block)DABlocks.SKYJADE_CHAIN.get());
      this.dropSelf((Block)DABlocks.SKYJADE_LANTERN.get());
      this.dropSelf((Block)DABlocks.AMBROSIUM_TIKI_TORCH.get());
      this.dropOther((Block)DABlocks.GOLDEN_DIRT_PATH.get(), (ItemLike)AetherBlocks.AETHER_DIRT.get());
      this.dropOther((Block)DABlocks.POISON_CAULDRON.get(), Blocks.CAULDRON.asItem());
      this.dropSelfDouble((Block)DABlocks.AERSMOG.get());
      this.dropSelf((Block)DABlocks.STERLING_AERCLOUD.get());
      this.dropSelf((Block)DABlocks.CHROMATIC_AERCLOUD.get());
      this.dropSelfDouble((Block)DABlocks.RAIN_AERCLOUD.get());
      this.dropDoubleWithSilk((Block)DABlocks.GOLDEN_GRASS_BLOCK.get(), (ItemLike)AetherBlocks.AETHER_DIRT.get());
      this.dropDoubleWithSilk((Block)DABlocks.AERCLOUD_GRASS_BLOCK.get(), (ItemLike)AetherBlocks.COLD_AERCLOUD.get());
      this.dropSelf((Block)DABlocks.BLUE_AERCLOUD_MUSHROOMS.get());
      this.dropSelf((Block)DABlocks.PINK_AERCLOUD_MUSHROOMS.get());
      this.dropSelfDouble((Block)DABlocks.AETHER_COARSE_DIRT.get());
      this.dropSelf((Block)DABlocks.COMBINER.get());
      this.dropSelf((Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.dropSelf((Block)DABlocks.GILDED_HOLYSTONE_BRICK_WALL.get());
      this.add((Block)DABlocks.GILDED_HOLYSTONE_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.GILDED_HOLYSTONE_BRICK_STAIRS.get());
      this.dropSelf((Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.dropSelf((Block)DABlocks.GILDED_HOLYSTONE_TILE_WALL.get());
      this.add((Block)DABlocks.GILDED_HOLYSTONE_TILE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.GILDED_HOLYSTONE_TILE_STAIRS.get());
      this.dropSelf((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.dropSelf((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_WALL.get());
      this.add((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_STAIRS.get());
      this.dropSelf((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.dropSelf((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_WALL.get());
      this.add((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.dropSelf((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_STAIRS.get());
      this.dropSelf((Block)DABlocks.ROSEROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.CRUDEROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.YAGROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.CONBERRY_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.SUNROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get());
      this.dropSelf((Block)DABlocks.SQUALL_BLOCK.get());
   }

   protected static net.minecraft.world.level.storage.loot.LootTable.Builder createGoldenVinesDrop(Block p_251070_) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .add(LootItem.lootTableItem((ItemLike)DAItems.GOLDEN_BERRIES.get()))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_251070_)
                     .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(GoldenVines.BERRIES, true))
               )
         );
   }

   protected static net.minecraft.world.level.storage.loot.LootTable.Builder createVinesDrop(Block block) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SHEARS).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block)));
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder createGoldenDoublePlantWithSeedDrops(Block block, Block p_248735_) {
      Builder<?> builder = ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(p_248735_)
            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
            .when(HAS_SHEARS))
         .otherwise(
            ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionCondition(
                  block, LootItem.lootTableItem(Items.WHEAT_SEEDS)
               ))
               .when(LootItemRandomChanceCondition.randomChance(0.125F))
         );
      Builder<?> builder1 = ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(p_248735_)
            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
            .when(HAS_SHEARS))
         .otherwise(
            ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionCondition(
                  block, LootItem.lootTableItem((ItemLike)DAItems.GOLDEN_GRASS_SEEDS.get())
               ))
               .when(LootItemRandomChanceCondition.randomChance(0.1F))
         );
      Builder<?> builder2 = ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(p_248735_)
            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
            .when(HAS_SHEARS))
         .otherwise(
            ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionCondition(
                  block, LootItem.lootTableItem((ItemLike)AetherItems.AMBROSIUM_SHARD.get())
               ))
               .when(LootItemRandomChanceCondition.randomChance(0.01F))
         );
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .add(builder)
               .add(builder1)
               .add(builder2)
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                           .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                     )
               )
               .when(
                  LocationCheck.checkLocation(
                     net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                        .setBlock(
                           net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                              .of(new Block[]{block})
                              .setProperties(
                                 net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                              )
                        ),
                     new BlockPos(0, 1, 0)
                  )
               )
         )
         .withPool(
            LootPool.lootPool()
               .add(builder)
               .add(builder1)
               .add(builder2)
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                     .setProperties(
                        net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                           .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
                     )
               )
               .when(
                  LocationCheck.checkLocation(
                     net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                        .setBlock(
                           net.minecraft.advancements.critereon.BlockPredicate.Builder.block()
                              .of(new Block[]{block})
                              .setProperties(
                                 net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
                              )
                        ),
                     new BlockPos(0, -1, 0)
                  )
               )
         );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder createGoldenGrassDrops(Block block) {
      return this.createShearsDispatchTable(
            block,
            (Builder)this.applyExplosionDecay(
               block,
               ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.WHEAT_SEEDS)
                     .when(LootItemRandomChanceCondition.randomChance(0.125F)))
                  .apply(ApplyBonusCount.addUniformBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 2))
            )
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(0.1F))
               .apply(ApplyBonusCount.addUniformBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 2))
               .add(LootItem.lootTableItem((ItemLike)DAItems.GOLDEN_GRASS_SEEDS.get()))
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(0.01F))
               .apply(ApplyBonusCount.addUniformBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 2))
               .add(LootItem.lootTableItem((ItemLike)AetherItems.AMBROSIUM_SHARD.get()))
         );
   }

   protected net.minecraft.world.level.storage.loot.LootTable.Builder createFeatherGrassDrops(Block block) {
      return this.createShearsDispatchTable(
            block,
            (Builder)this.applyExplosionDecay(
               block,
               ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)LootItem.lootTableItem(DAItems.CLOUDBLOOM_BOUQUET)
                     .when(LootItemRandomChanceCondition.randomChance(0.5F)))
                  .apply(ApplyBonusCount.addUniformBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE), 2))
            )
         )
         .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemRandomChanceCondition.randomChance(0.1F)));
   }

   public net.minecraft.world.level.storage.loot.LootTable.Builder droppingWithChancesAndSkyrootSticksAndAerglowPetal(
      Block block, Block sapling, float... chances
   ) {
      return this.createSilkTouchOrShearsDispatchTable(
            block,
            ((net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder)this.applyExplosionCondition(
                  block, LootItem.lootTableItem(sapling)
               ))
               .when(BonusLevelTableCondition.bonusLevelFlatChance(this.registries.holderOrThrow(Enchantments.FORTUNE), chances))
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(HAS_SHEARS.or(this.hasSilkTouch()).invert())
               .add(
                  (Builder)this.applyExplosionDecay(
                     block,
                     (FunctionUserBuilder)LootItem.lootTableItem((ItemLike)AetherItems.SKYROOT_STICK.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .when(
                           BonusLevelTableCondition.bonusLevelFlatChance(
                              this.registries.holderOrThrow(Enchantments.FORTUNE), new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F}
                           )
                        )
                  )
               )
               .add(
                  (Builder)this.applyExplosionDecay(
                     block,
                     LootItem.lootTableItem((ItemLike)DAItems.AERGLOW_BLOSSOM.get())
                        .apply(ApplyBonusCount.addOreBonusCount(this.registries.holderOrThrow(Enchantments.FORTUNE)))
                  )
               )
         )
         .apply(DoubleDrops.builder());
   }
}
