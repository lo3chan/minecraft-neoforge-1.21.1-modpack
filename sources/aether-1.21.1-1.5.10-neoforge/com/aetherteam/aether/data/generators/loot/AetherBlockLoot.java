package com.aetherteam.aether.data.generators.loot;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.providers.AetherBlockLootSubProvider;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.mixin.mixins.common.accessor.BlockLootAccessor;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class AetherBlockLoot extends AetherBlockLootSubProvider {
   private static final Set<Item> EXPLOSION_RESISTANT = Stream.of((Block)AetherBlocks.TREASURE_CHEST.get())
      .<Item>map(ItemLike::asItem)
      .collect(Collectors.toSet());

   public AetherBlockLoot(Provider registries) {
      super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
   }

   public void generate() {
      this.dropNone((Block)AetherBlocks.AETHER_PORTAL.get());
      this.dropDoubleWithSilk((Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (ItemLike)AetherBlocks.AETHER_DIRT.get());
      this.add(
         (Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(),
         block -> this.createSingleItemTableWithSilkTouch(block, (ItemLike)AetherBlocks.AETHER_DIRT.get())
      );
      this.dropSelfDouble((Block)AetherBlocks.AETHER_DIRT.get());
      this.dropSelfDouble((Block)AetherBlocks.QUICKSOIL.get());
      this.dropSelfDouble((Block)AetherBlocks.HOLYSTONE.get());
      this.dropSelfDouble((Block)AetherBlocks.MOSSY_HOLYSTONE.get());
      this.dropOther((Block)AetherBlocks.AETHER_FARMLAND.get(), (ItemLike)AetherBlocks.AETHER_DIRT.get());
      this.dropOther((Block)AetherBlocks.AETHER_DIRT_PATH.get(), (ItemLike)AetherBlocks.AETHER_DIRT.get());
      this.dropSelfDouble((Block)AetherBlocks.COLD_AERCLOUD.get());
      this.dropSelfDouble((Block)AetherBlocks.BLUE_AERCLOUD.get());
      this.dropSelfDouble((Block)AetherBlocks.GOLDEN_AERCLOUD.get());
      this.dropSelf((Block)AetherBlocks.ICESTONE.get());
      this.dropDoubleWithFortune((Block)AetherBlocks.AMBROSIUM_ORE.get(), (Item)AetherItems.AMBROSIUM_SHARD.get());
      this.dropWithFortune((Block)AetherBlocks.ZANITE_ORE.get(), (Item)AetherItems.ZANITE_GEMSTONE.get());
      this.dropSelf((Block)AetherBlocks.GRAVITITE_ORE.get());
      this.add(
         (Block)AetherBlocks.SKYROOT_LEAVES.get(),
         leaves -> this.droppingWithChancesAndSkyrootSticks(
            leaves, (Block)AetherBlocks.SKYROOT_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances()
         )
      );
      this.add(
         (Block)AetherBlocks.GOLDEN_OAK_LEAVES.get(),
         leaves -> this.droppingGoldenOakLeaves(leaves, (Block)AetherBlocks.GOLDEN_OAK_SAPLING.get(), BlockLootAccessor.aether$getNormalLeavesSaplingChances())
      );
      this.add((Block)AetherBlocks.CRYSTAL_LEAVES.get(), this::droppingWithSkyrootSticks);
      this.add((Block)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get(), leaves -> this.droppingWithFruitAndSkyrootSticks(leaves, (Item)AetherItems.WHITE_APPLE.get()));
      this.add((Block)AetherBlocks.HOLIDAY_LEAVES.get(), this::droppingWithSkyrootSticks);
      this.add((Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get(), this::droppingWithSkyrootSticks);
      this.dropSelfDouble((Block)AetherBlocks.SKYROOT_LOG.get());
      this.add(
         (Block)AetherBlocks.GOLDEN_OAK_LOG.get(),
         log -> this.droppingDoubleGoldenOak(log, (Block)AetherBlocks.SKYROOT_LOG.get(), (Item)AetherItems.GOLDEN_AMBER.get())
      );
      this.dropSelf((Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get());
      this.dropSelfDouble((Block)AetherBlocks.SKYROOT_WOOD.get());
      this.add(
         (Block)AetherBlocks.GOLDEN_OAK_WOOD.get(),
         wood -> this.droppingDoubleGoldenOak(wood, (Block)AetherBlocks.SKYROOT_WOOD.get(), (Item)AetherItems.GOLDEN_AMBER.get())
      );
      this.dropSelf((Block)AetherBlocks.STRIPPED_SKYROOT_WOOD.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_PLANKS.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_BRICKS.get());
      this.dropWhenSilkTouch((Block)AetherBlocks.QUICKSOIL_GLASS.get());
      this.dropWhenSilkTouch((Block)AetherBlocks.QUICKSOIL_GLASS_PANE.get());
      this.dropSelf((Block)AetherBlocks.AEROGEL.get());
      this.dropSelf((Block)AetherBlocks.AMBROSIUM_BLOCK.get());
      this.dropSelf((Block)AetherBlocks.ZANITE_BLOCK.get());
      this.dropSelf((Block)AetherBlocks.ENCHANTED_GRAVITITE.get());
      this.add((Block)AetherBlocks.ALTAR.get(), this::droppingNameableBlockEntityTable);
      this.add((Block)AetherBlocks.FREEZER.get(), this::droppingNameableBlockEntityTable);
      this.add((Block)AetherBlocks.INCUBATOR.get(), this::droppingNameableBlockEntityTable);
      this.dropOther((Block)AetherBlocks.AMBROSIUM_WALL_TORCH.get(), (ItemLike)AetherBlocks.AMBROSIUM_TORCH.get());
      this.dropSelf((Block)AetherBlocks.AMBROSIUM_TORCH.get());
      this.dropOther((Block)AetherBlocks.SKYROOT_WALL_SIGN.get(), (ItemLike)AetherBlocks.SKYROOT_SIGN.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_SIGN.get());
      this.dropOther((Block)AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get(), (ItemLike)AetherBlocks.SKYROOT_HANGING_SIGN.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_HANGING_SIGN.get());
      this.add(
         (Block)AetherBlocks.BERRY_BUSH.get(),
         bush -> this.droppingBerryBush(bush, (Block)AetherBlocks.BERRY_BUSH_STEM.get(), (Item)AetherItems.BLUE_BERRY.get())
      );
      this.dropSelfDouble((Block)AetherBlocks.BERRY_BUSH_STEM.get());
      this.dropPottedContents((Block)AetherBlocks.POTTED_BERRY_BUSH.get());
      this.dropPottedContents((Block)AetherBlocks.POTTED_BERRY_BUSH_STEM.get());
      this.dropSelf((Block)AetherBlocks.PURPLE_FLOWER.get());
      this.dropSelf((Block)AetherBlocks.WHITE_FLOWER.get());
      this.dropPottedContents((Block)AetherBlocks.POTTED_PURPLE_FLOWER.get());
      this.dropPottedContents((Block)AetherBlocks.POTTED_WHITE_FLOWER.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_SAPLING.get());
      this.dropSelf((Block)AetherBlocks.GOLDEN_OAK_SAPLING.get());
      this.dropPottedContents((Block)AetherBlocks.POTTED_SKYROOT_SAPLING.get());
      this.dropPottedContents((Block)AetherBlocks.POTTED_GOLDEN_OAK_SAPLING.get());
      this.dropSelf((Block)AetherBlocks.CARVED_STONE.get());
      this.dropSelf((Block)AetherBlocks.SENTRY_STONE.get());
      this.dropSelf((Block)AetherBlocks.ANGELIC_STONE.get());
      this.dropSelf((Block)AetherBlocks.LIGHT_ANGELIC_STONE.get());
      this.dropSelf((Block)AetherBlocks.HELLFIRE_STONE.get());
      this.dropSelf((Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.LOCKED_CARVED_STONE.get());
      this.dropNone((Block)AetherBlocks.LOCKED_SENTRY_STONE.get());
      this.dropNone((Block)AetherBlocks.LOCKED_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.LOCKED_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.TRAPPED_CARVED_STONE.get());
      this.dropNone((Block)AetherBlocks.TRAPPED_SENTRY_STONE.get());
      this.dropNone((Block)AetherBlocks.TRAPPED_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.TRAPPED_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.TRAPPED_LIGHT_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.BOSS_DOORWAY_CARVED_STONE.get());
      this.dropNone((Block)AetherBlocks.BOSS_DOORWAY_SENTRY_STONE.get());
      this.dropNone((Block)AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.BOSS_DOORWAY_LIGHT_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.BOSS_DOORWAY_LIGHT_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.TREASURE_DOORWAY_CARVED_STONE.get());
      this.dropNone((Block)AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE.get());
      this.dropNone((Block)AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_ANGELIC_STONE.get());
      this.dropNone((Block)AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE.get());
      this.dropNone((Block)AetherBlocks.CHEST_MIMIC.get());
      this.add((Block)AetherBlocks.TREASURE_CHEST.get(), this::droppingTreasureChest);
      this.dropSelf((Block)AetherBlocks.PILLAR.get());
      this.dropSelf((Block)AetherBlocks.PILLAR_TOP.get());
      this.add((Block)AetherBlocks.PRESENT.get(), this::droppingPresentLoot);
      this.dropSelf((Block)AetherBlocks.SKYROOT_FENCE.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_FENCE_GATE.get());
      this.add((Block)AetherBlocks.SKYROOT_DOOR.get(), this.createDoorTable((Block)AetherBlocks.SKYROOT_DOOR.get()));
      this.dropSelf((Block)AetherBlocks.SKYROOT_TRAPDOOR.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_BUTTON.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_PRESSURE_PLATE.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_BUTTON.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get());
      this.dropSelf((Block)AetherBlocks.CARVED_WALL.get());
      this.dropSelf((Block)AetherBlocks.ANGELIC_WALL.get());
      this.dropSelf((Block)AetherBlocks.HELLFIRE_WALL.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_WALL.get());
      this.dropSelf((Block)AetherBlocks.MOSSY_HOLYSTONE_WALL.get());
      this.dropSelf((Block)AetherBlocks.ICESTONE_WALL.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_BRICK_WALL.get());
      this.dropSelf((Block)AetherBlocks.AEROGEL_WALL.get());
      this.dropSelf((Block)AetherBlocks.SKYROOT_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.CARVED_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.ANGELIC_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.HELLFIRE_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.ICESTONE_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get());
      this.dropSelf((Block)AetherBlocks.AEROGEL_STAIRS.get());
      this.add((Block)AetherBlocks.SKYROOT_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.CARVED_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.ANGELIC_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.HELLFIRE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.HOLYSTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.ICESTONE_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.AEROGEL_SLAB.get(), x$0 -> this.createSlabItemTable(x$0));
      this.add((Block)AetherBlocks.SUN_ALTAR.get(), this::droppingNameableBlockEntityTable);
      this.add(
         (Block)AetherBlocks.SKYROOT_BOOKSHELF.get(), bookshelf -> this.createSingleItemTableWithSilkTouch(bookshelf, Items.BOOK, ConstantValue.exactly(3.0F))
      );
      this.add((Block)AetherBlocks.SKYROOT_BED.get(), bed -> this.createSinglePropConditionTable(bed, BedBlock.PART, BedPart.HEAD));
      this.dropNone((Block)AetherBlocks.FROSTED_ICE.get());
      this.dropNone((Block)AetherBlocks.UNSTABLE_OBSIDIAN.get());
   }

   public Iterable<Block> getKnownBlocks() {
      return AetherBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
   }
}
