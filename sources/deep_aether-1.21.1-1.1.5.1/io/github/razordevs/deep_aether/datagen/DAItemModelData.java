package io.github.razordevs.deep_aether.datagen;

import com.aetherteam.aether.data.providers.AetherItemModelProvider;
import com.aetherteam.nitrogen.data.providers.NitrogenItemModelProvider;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAItems;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DAItemModelData extends AetherItemModelProvider {
   public DAItemModelData(PackOutput output, ExistingFileHelper helper) {
      super(output, "deep_aether", helper);
   }

   protected void registerModels() {
      this.itemBlock((Block)DABlocks.HIGHSTONE.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_LOG.get());
      this.itemBlock((Block)DABlocks.STRIPPED_ROSEROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_SLAB.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_STAIRS.get());
      this.itemFence((Block)DABlocks.ROSEROOT_FENCE.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_FENCE_GATE.get());
      this.item(((Block)DABlocks.ROSEROOT_DOOR.get()).asItem());
      this.itemBlock((Block)DABlocks.ROSEROOT_TRAPDOOR.get(), "_bottom");
      this.itemButton((Block)DABlocks.ROSEROOT_BUTTON.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_PRESSURE_PLATE.get());
      this.itemWallBlock((Block)DABlocks.ROSEROOT_WOOD_WALL.get(), (Block)DABlocks.ROSEROOT_LOG.get());
      this.itemWallBlock((Block)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.itemBlockFlat((Block)DABlocks.ROSEROOT_SAPLING.get());
      this.itemBlockFlat((Block)DABlocks.BLUE_ROSEROOT_SAPLING.get());
      this.itemBlock((Block)DABlocks.ROSEROOT_LEAVES.get());
      this.itemBlock((Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get());
      this.itemBlock((Block)DABlocks.BLUE_ROSEROOT_LEAVES.get());
      this.itemBlock((Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get());
      this.itemBlock((Block)DABlocks.AERGLOW_BLOSSOM_BLOCK.get());
      this.item((Item)DAItems.ROSEROOT_SIGN.get());
      this.item((Item)DAItems.ROSEROOT_HANGING_SIGN.get());
      this.item((Item)DAItems.ROSEROOT_BOAT.get());
      this.item((Item)DAItems.ROSEROOT_CHEST_BOAT.get());
      this.itemBlock((Block)DABlocks.YAGROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.YAGROOT_LOG.get());
      this.itemBlock((Block)DABlocks.STRIPPED_YAGROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.itemBlock((Block)DABlocks.YAGROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.YAGROOT_SLAB.get());
      this.itemBlock((Block)DABlocks.YAGROOT_STAIRS.get());
      this.itemFence((Block)DABlocks.YAGROOT_FENCE.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.YAGROOT_FENCE_GATE.get());
      this.item(((Block)DABlocks.YAGROOT_DOOR.get()).asItem());
      this.itemBlock((Block)DABlocks.YAGROOT_TRAPDOOR.get(), "_bottom");
      this.itemButton((Block)DABlocks.YAGROOT_BUTTON.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.YAGROOT_PRESSURE_PLATE.get());
      this.itemWallBlock((Block)DABlocks.YAGROOT_WOOD_WALL.get(), (Block)DABlocks.YAGROOT_LOG.get());
      this.itemWallBlock((Block)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.itemBlockFlat((Block)DABlocks.YAGROOT_SAPLING.get());
      this.itemBlock((Block)DABlocks.YAGROOT_LEAVES.get());
      this.item((Item)DAItems.YAGROOT_SIGN.get());
      this.itemBlock((Block)DABlocks.YAGROOT_ROOTS.get());
      this.itemBlock((Block)DABlocks.MUDDY_YAGROOT_ROOTS.get());
      this.item((Item)DAItems.YAGROOT_HANGING_SIGN.get());
      this.item((Item)DAItems.YAGROOT_BOAT.get());
      this.item((Item)DAItems.YAGROOT_CHEST_BOAT.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_LOG.get());
      this.itemBlock((Block)DABlocks.STRIPPED_CRUDEROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_SLAB.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_STAIRS.get());
      this.itemFence((Block)DABlocks.CRUDEROOT_FENCE.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_FENCE_GATE.get());
      this.item(((Block)DABlocks.CRUDEROOT_DOOR.get()).asItem());
      this.itemBlock((Block)DABlocks.CRUDEROOT_TRAPDOOR.get(), "_bottom");
      this.itemButton((Block)DABlocks.CRUDEROOT_BUTTON.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_PRESSURE_PLATE.get());
      this.itemWallBlock((Block)DABlocks.CRUDEROOT_WOOD_WALL.get(), (Block)DABlocks.CRUDEROOT_LOG.get());
      this.itemWallBlock((Block)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.itemBlockFlat((Block)DABlocks.CRUDEROOT_SAPLING.get());
      this.itemBlock((Block)DABlocks.CRUDEROOT_LEAVES.get());
      this.item((Item)DAItems.CRUDEROOT_SIGN.get());
      this.item((Item)DAItems.CRUDEROOT_HANGING_SIGN.get());
      this.item((Item)DAItems.CRUDEROOT_BOAT.get());
      this.item((Item)DAItems.CRUDEROOT_CHEST_BOAT.get());
      this.itemBlock((Block)DABlocks.CONBERRY_WOOD.get());
      this.itemBlock((Block)DABlocks.CONBERRY_LOG.get());
      this.itemBlock((Block)DABlocks.STRIPPED_CONBERRY_WOOD.get());
      this.itemBlock((Block)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.itemBlock((Block)DABlocks.CONBERRY_PLANKS.get());
      this.itemBlock((Block)DABlocks.CONBERRY_SLAB.get());
      this.itemBlock((Block)DABlocks.CONBERRY_STAIRS.get());
      this.itemFence((Block)DABlocks.CONBERRY_FENCE.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.itemBlock((Block)DABlocks.CONBERRY_FENCE_GATE.get());
      this.item(((Block)DABlocks.CONBERRY_DOOR.get()).asItem());
      this.itemBlock((Block)DABlocks.CONBERRY_TRAPDOOR.get(), "_bottom");
      this.itemButton((Block)DABlocks.CONBERRY_BUTTON.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.itemBlock((Block)DABlocks.CONBERRY_PRESSURE_PLATE.get());
      this.itemWallBlock((Block)DABlocks.CONBERRY_WOOD_WALL.get(), (Block)DABlocks.CONBERRY_LOG.get());
      this.itemWallBlock((Block)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.itemBlockFlat((Block)DABlocks.CONBERRY_SAPLING.get());
      this.itemBlock((Block)DABlocks.CONBERRY_LEAVES.get());
      this.item((Item)DAItems.CONBERRY_SIGN.get());
      this.item((Item)DAItems.CONBERRY_HANGING_SIGN.get());
      this.item((Item)DAItems.CONBERRY_BOAT.get());
      this.item((Item)DAItems.CONBERRY_CHEST_BOAT.get());
      this.itemBlock((Block)DABlocks.SUNROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.SUNROOT_LOG.get());
      this.itemBlock((Block)DABlocks.STRIPPED_SUNROOT_WOOD.get());
      this.itemBlock((Block)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.itemBlock((Block)DABlocks.SUNROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.SUNROOT_SLAB.get());
      this.itemBlock((Block)DABlocks.SUNROOT_STAIRS.get());
      this.itemFence((Block)DABlocks.SUNROOT_FENCE.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.SUNROOT_FENCE_GATE.get());
      this.item(((Block)DABlocks.SUNROOT_DOOR.get()).asItem());
      this.itemBlock((Block)DABlocks.SUNROOT_TRAPDOOR.get(), "_bottom");
      this.itemButton((Block)DABlocks.SUNROOT_BUTTON.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.itemBlock((Block)DABlocks.SUNROOT_PRESSURE_PLATE.get());
      this.itemWallBlock((Block)DABlocks.SUNROOT_WOOD_WALL.get(), (Block)DABlocks.SUNROOT_LOG.get());
      this.itemWallBlock((Block)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.itemBlockFlat((Block)DABlocks.SUNROOT_SAPLING.get());
      this.itemBlock((Block)DABlocks.SUNROOT_LEAVES.get());
      this.item((Item)DAItems.SUNROOT_SIGN.get());
      this.item((Item)DAItems.SUNROOT_HANGING_SIGN.get());
      this.item((Item)DAItems.SUNROOT_BOAT.get());
      this.item((Item)DAItems.SUNROOT_CHEST_BOAT.get());
      this.itemBlock((Block)DABlocks.AETHER_COARSE_DIRT.get());
      this.itemBlock((Block)DABlocks.AETHER_MUD.get());
      this.itemBlock((Block)DABlocks.PACKED_AETHER_MUD.get());
      this.itemBlock((Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.itemBlock((Block)DABlocks.AETHER_MUD_BRICKS_SLAB.get());
      this.itemBlock((Block)DABlocks.AETHER_MUD_BRICKS_STAIRS.get());
      this.itemWallBlock((Block)DABlocks.AETHER_MUD_BRICKS_WALL.get(), (Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.itemBlock((Block)DABlocks.SKYJADE_BLOCK.get());
      this.itemBlock((Block)DABlocks.SKYJADE_ORE.get());
      this.itemBlock((Block)DABlocks.STRATUS_BLOCK.get());
      this.itemBlock((Block)DABlocks.COBBLED_ASETERITE.get());
      this.itemBlock((Block)DABlocks.COBBLED_ASETERITE_STAIRS.get());
      this.itemBlock((Block)DABlocks.COBBLED_ASETERITE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.COBBLED_ASETERITE_WALL.get(), (Block)DABlocks.COBBLED_ASETERITE.get());
      this.itemBlock((Block)DABlocks.ASETERITE.get());
      this.itemBlock((Block)DABlocks.ASETERITE_STAIRS.get());
      this.itemBlock((Block)DABlocks.ASETERITE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.ASETERITE_WALL.get(), (Block)DABlocks.ASETERITE.get());
      this.itemBlock((Block)DABlocks.POLISHED_ASETERITE.get());
      this.itemBlock((Block)DABlocks.POLISHED_ASETERITE_STAIRS.get());
      this.itemBlock((Block)DABlocks.POLISHED_ASETERITE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.POLISHED_ASETERITE_WALL.get(), (Block)DABlocks.POLISHED_ASETERITE.get());
      this.itemBlock((Block)DABlocks.ASETERITE_BRICKS.get());
      this.itemBlock((Block)DABlocks.ASETERITE_BRICKS_STAIRS.get());
      this.itemBlock((Block)DABlocks.ASETERITE_BRICKS_SLAB.get());
      this.itemWallBlock((Block)DABlocks.ASETERITE_BRICKS_WALL.get(), (Block)DABlocks.ASETERITE_BRICKS.get());
      this.itemBlock((Block)DABlocks.RAW_CLORITE.get());
      this.itemBlock((Block)DABlocks.RAW_CLORITE_STAIRS.get());
      this.itemBlock((Block)DABlocks.RAW_CLORITE_SLAB.get());
      this.itemBlock((Block)DABlocks.CLORITE.get());
      this.itemBlock((Block)DABlocks.CLORITE_STAIRS.get());
      this.itemBlock((Block)DABlocks.CLORITE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.CLORITE_WALL.get(), (Block)DABlocks.CLORITE.get());
      this.itemWallBlock((Block)DABlocks.RAW_CLORITE_WALL.get(), (Block)DABlocks.RAW_CLORITE.get());
      this.itemWallBlock((Block)DABlocks.POLISHED_CLORITE_WALL.get(), (Block)DABlocks.POLISHED_CLORITE.get());
      this.itemBlock((Block)DABlocks.POLISHED_CLORITE.get());
      this.itemBlock((Block)DABlocks.POLISHED_CLORITE_STAIRS.get());
      this.itemBlock((Block)DABlocks.POLISHED_CLORITE_SLAB.get());
      this.itemBlock((Block)DABlocks.CLORITE_PILLAR.get());
      this.itemBlock((Block)DABlocks.HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.HOLYSTONE_TILE_STAIRS.get());
      this.itemBlock((Block)DABlocks.HOLYSTONE_TILE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get());
      this.itemBlock((Block)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get());
      this.itemWallBlock((Block)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(), (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.GILDED_HOLYSTONE_BRICK_STAIRS.get());
      this.itemBlock((Block)DABlocks.GILDED_HOLYSTONE_BRICK_SLAB.get());
      this.itemWallBlock((Block)DABlocks.GILDED_HOLYSTONE_BRICK_WALL.get(), (Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_STAIRS.get());
      this.itemBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_SLAB.get());
      this.itemWallBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_WALL.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get());
      this.itemBlock((Block)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get());
      this.itemWallBlock((Block)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.itemBlock((Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get());
      this.itemBlock((Block)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.GILDED_HOLYSTONE_TILE_STAIRS.get());
      this.itemBlock((Block)DABlocks.GILDED_HOLYSTONE_TILE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.GILDED_HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_STAIRS.get());
      this.itemBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_SLAB.get());
      this.itemWallBlock((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.itemBlock((Block)DABlocks.HOLYSTONE_PILLAR.get());
      this.itemBlock((Block)DABlocks.HOLYSTONE_PILLAR_UP.get());
      this.itemBlock((Block)DABlocks.HOLYSTONE_PILLAR_DOWN.get());
      this.itemBlock((Block)DABlocks.CHISELED_HOLYSTONE.get());
      this.itemBlock((Block)DABlocks.NIMBUS_STONE.get());
      this.itemBlock((Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.itemBlock((Block)DABlocks.NIMBUS_STAIRS.get());
      this.itemBlock((Block)DABlocks.NIMBUS_SLAB.get());
      this.itemWallBlock((Block)DABlocks.NIMBUS_WALL.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.itemLockedDungeonBlock((Block)DABlocks.LOCKED_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.itemLockedDungeonBlock((Block)DABlocks.LOCKED_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.itemTrappedDungeonBlock((Block)DABlocks.TRAPPED_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.itemTrappedDungeonBlock((Block)DABlocks.TRAPPED_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.itemBossDoorwayDungeonBlock((Block)DABlocks.BOSS_DOORWAY_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.itemBossDoorwayDungeonBlock((Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.itemTreasureDoorwayDungeonBlock((Block)DABlocks.TREASURE_DOORWAY_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.itemTreasureDoorwayDungeonBlock((Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.itemBlock((Block)DABlocks.NIMBUS_PILLAR.get());
      this.itemBlock((Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.itemLockedDungeonBlock((Block)DABlocks.LOCKED_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.itemLockedDungeonBlock((Block)DABlocks.LOCKED_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.itemTrappedDungeonBlock((Block)DABlocks.TRAPPED_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.itemTrappedDungeonBlock((Block)DABlocks.TRAPPED_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.itemBossDoorwayDungeonBlock((Block)DABlocks.BOSS_DOORWAY_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.itemBossDoorwayDungeonBlock((Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.itemTreasureDoorwayDungeonBlock((Block)DABlocks.TREASURE_DOORWAY_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.itemTreasureDoorwayDungeonBlock((Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.itemBlockFlat((Block)DABlocks.AERLAVENDER.get());
      this.itemBlockFlat((Block)DABlocks.TALL_AERLAVENDER.get());
      this.itemBlockFlat((Block)DABlocks.AETHER_CATTAILS.get());
      this.itemBlockFlatName((Block)DABlocks.TALL_AETHER_CATTAILS.get(), "tall_aether_cattails_top");
      this.itemBlockFlat((Block)DABlocks.RADIANT_ORCHID.get());
      this.itemBlockFlat((Block)DABlocks.LIGHTCAP_MUSHROOMS.get());
      this.itemBlock((Block)DABlocks.BLUE_SQUASH.get());
      this.itemBlock((Block)DABlocks.GREEN_SQUASH.get());
      this.itemBlock((Block)DABlocks.PURPLE_SQUASH.get());
      this.itemBlock((Block)DABlocks.CARVED_BLUE_SQUASH.get());
      this.itemBlock((Block)DABlocks.CARVED_GREEN_SQUASH.get());
      this.itemBlock((Block)DABlocks.CARVED_PURPLE_SQUASH.get());
      this.itemBlock((Block)DABlocks.AETHER_MOSS_CARPET.get());
      this.itemBlock((Block)DABlocks.CLOUDBLOOM_CARPET.get());
      this.itemBlock((Block)DABlocks.AETHER_MOSS_BLOCK.get());
      this.item((Item)DAItems.VIRULENT_QUICKSAND_BUCKET.get());
      this.item((Item)DAItems.SKYROOT_VIRULENT_QUICKSAND_BUCKET.get());
      this.aercloudItem((Block)DABlocks.RAIN_AERCLOUD.get());
      this.aercloudItem((Block)DABlocks.AERSMOG.get());
      this.aercloudItem((Block)DABlocks.STERLING_AERCLOUD.get());
      this.aercloudItem((Block)DABlocks.CHROMATIC_AERCLOUD.get());
      this.itemBlock((Block)DABlocks.AERCLOUD_ROOT_CARPET.get());
      this.itemBlockFlat((Block)DABlocks.PINK_AERCLOUD_MUSHROOMS.get());
      this.itemBlockFlat((Block)DABlocks.BLUE_AERCLOUD_MUSHROOMS.get());
      this.itemBlockFlat((Block)DABlocks.MINI_GOLDEN_GRASS.get());
      this.itemBlockFlat((Block)DABlocks.SHORT_GOLDEN_GRASS.get());
      this.itemBlockFlat((Block)DABlocks.MEDIUM_GOLDEN_GRASS.get());
      this.itemBlockFlatName((Block)DABlocks.TALL_GOLDEN_GRASS.get(), "tall_golden_grass_top");
      this.itemLogWallBlock((Block)DABlocks.ROSEROOT_LOG_WALL.get(), (Block)DABlocks.ROSEROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get(), (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.CRUDEROOT_LOG_WALL.get(), (Block)DABlocks.CRUDEROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get(), (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.YAGROOT_LOG_WALL.get(), (Block)DABlocks.YAGROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get(), (Block)DABlocks.STRIPPED_YAGROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.CONBERRY_LOG_WALL.get(), (Block)DABlocks.CONBERRY_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get(), (Block)DABlocks.STRIPPED_CONBERRY_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.SUNROOT_LOG_WALL.get(), (Block)DABlocks.SUNROOT_LOG.get(), "", "deep_aether");
      this.itemLogWallBlock((Block)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get(), (Block)DABlocks.STRIPPED_SUNROOT_LOG.get(), "", "deep_aether");
      this.item((Item)DAItems.AFTERBURNER.get());
      this.item((Item)DAItems.SUN_CORE.get());
      this.item((Item)DAItems.AERWHALE_SADDLE.get());
      this.item((Item)DAItems.SLIDER_EYE.get());
      this.item((Item)DAItems.MEDAL_OF_HONOR.get());
      this.item((Item)DAItems.SKYJADE.get());
      this.item((Item)DAItems.SKYJADE_NUGGET.get());
      this.handHeld((Item)DAItems.SKYJADE_TOOLS_SWORD.get());
      this.handHeld((Item)DAItems.SKYJADE_TOOLS_AXE.get());
      this.handHeld((Item)DAItems.SKYJADE_TOOLS_PICKAXE.get());
      this.handHeld((Item)DAItems.SKYJADE_TOOLS_SHOVEL.get());
      this.handHeld((Item)DAItems.SKYJADE_TOOLS_HOE.get());
      this.bootsItem((Item)DAItems.SKYJADE_BOOTS.get());
      this.leggingsItem((Item)DAItems.SKYJADE_LEGGINGS.get());
      this.chestplateItem((Item)DAItems.SKYJADE_CHESTPLATE.get());
      this.helmetItem((Item)DAItems.SKYJADE_HELMET.get());
      this.glovesItem((Item)DAItems.SKYJADE_GLOVES.get());
      this.item((Item)DAItems.SKYJADE_RING.get());
      this.item((Item)DAItems.SKYJADE_MOA_ARMOR.get());
      this.item((Item)DAItems.STRATUS_INGOT.get());
      this.handHeld((Item)DAItems.STRATUS_SWORD.get());
      this.handHeld((Item)DAItems.STRATUS_AXE.get());
      this.handHeld((Item)DAItems.STRATUS_PICKAXE.get());
      this.handHeld((Item)DAItems.STRATUS_SHOVEL.get());
      this.handHeld((Item)DAItems.STRATUS_HOE.get());
      this.bootsItem((Item)DAItems.STRATUS_BOOTS.get());
      this.leggingsItem((Item)DAItems.STRATUS_LEGGINGS.get());
      this.chestplateItem((Item)DAItems.STRATUS_CHESTPLATE.get());
      this.helmetItem((Item)DAItems.STRATUS_HELMET.get());
      this.glovesItem((Item)DAItems.STRATUS_GLOVES.get());
      this.item((Item)DAItems.STRATUS_RING.get());
      this.item((Item)DAItems.STRATUS_SMITHING_TEMPLATE.get());
      this.item((Item)DAItems.GRAVITITE_RING.get());
      this.bowItem((Item)DAItems.STORM_BOW.get());
      this.handHeld((Item)DAItems.STORM_SWORD.get());
      this.item((Item)DAItems.WIND_SHIELD.get());
      this.item((Item)DAItems.AERCLOUD_NECKLACE.get());
      this.translucentItem((Item)DAItems.CLOUD_CAPE.get());
      this.bootsItem((Item)DAItems.STORMFORGED_BOOTS.get());
      this.leggingsItem((Item)DAItems.STORMFORGED_LEGGINGS.get());
      this.chestplateItem((Item)DAItems.STORMFORGED_CHESTPLATE.get());
      this.helmetItem((Item)DAItems.STORMFORGED_HELMET.get());
      this.glovesItem((Item)DAItems.STORMFORGED_GLOVES.get());
      this.item((Item)DAItems.STORMFORGED_SMITHING_TEMPLATE.get());
      this.item((Item)DAItems.SQUALL_PLATE.get());
      this.itemBlock((Block)DABlocks.SQUALL_BLOCK.get());
      this.item((Item)DAItems.RAW_AERGLOW_FISH.get());
      this.item((Item)DAItems.COOKED_AERGLOW_FISH.get());
      this.item((Item)DAItems.AERGLOW_FISH_BUCKET.get());
      this.item((Item)DAItems.SKYROOT_AERGLOW_FISH_BUCKET.get());
      this.item((Item)DAItems.RAW_QUAIL.get());
      this.item((Item)DAItems.COOKED_QUAIL.get());
      this.item((Item)DAItems.QUAIL_EGG.get());
      this.item((Item)DAItems.GOLDEN_BERRIES.get());
      this.item((Item)DAItems.ANTIDOTE.get());
      this.altItem((Item)DAItems.ENCHANTED_ANTIDOTE.get(), (Item)DAItems.ANTIDOTE.get());
      this.itemBlockFlat((Block)DABlocks.MEDIUM_GOLDEN_GRASS.get());
      this.itemBlockFlat((Block)DABlocks.SHORT_GOLDEN_GRASS.get());
      this.itemBlockFlat((Block)DABlocks.MINI_GOLDEN_GRASS.get());
      this.itemBlockFlatName((Block)DABlocks.TALL_GOLDEN_GRASS.get(), "tall_golden_grass_top");
      this.itemBlock((Block)DABlocks.GOLDEN_GRASS_BLOCK.get());
      this.itemBlock((Block)DABlocks.AERCLOUD_GRASS_BLOCK.get());
      this.itemBlock((Block)DABlocks.AERCLOUD_ROOTS.get());
      this.itemBlockFlat((Block)DABlocks.FEATHER_GRASS.get());
      this.itemBlockFlatName((Block)DABlocks.TALL_FEATHER_GRASS.get(), "tall_feather_grass_top");
      this.itemBlockFlatName((Block)DABlocks.TALL_ALIEN_PLANT.get(), "tall_alien_plant_top");
      this.itemBlockFlatName((Block)DABlocks.GOLDEN_FLOWER.get(), "golden_flower_top");
      this.itemBlockFlat((Block)DABlocks.ENCHANTED_BLOSSOM.get());
      this.itemBlockFlat((Block)DABlocks.SKY_TULIPS.get());
      this.itemBlockFlat((Block)DABlocks.IASPOVE.get());
      this.itemBlockFlat((Block)DABlocks.GOLDEN_ASPESS.get());
      this.itemBlockFlat((Block)DABlocks.ECHAISY.get());
      this.item((Item)DAItems.CLOUDBLOOM_BOUQUET.get());
      this.item((Item)DAItems.GOLDEN_GRASS_SEEDS.get());
      this.item((Item)DAItems.SQUASH_SEEDS.get());
      this.item((Item)DAItems.GOLDEN_SWET_BALL.get());
      this.itemBlockFlatName((Block)DABlocks.GLOWING_SPORES.get(), "glowing_spores_item");
      this.item((Item)DAItems.GREEN_SQUASH_SLICE.get());
      this.item((Item)DAItems.BLUE_SQUASH_SLICE.get());
      this.item((Item)DAItems.PURPLE_SQUASH_SLICE.get());
      this.eggItem((Item)DAItems.AETHER_FISH_SPAWN_EGG.get());
      this.eggItem((Item)DAItems.QUAIL_SPAWN_EGG.get());
      this.eggItem((Item)DAItems.VENOMITE_SPAWN_EGG.get());
      this.eggItem((Item)DAItems.WINDFLY_SPAWN_EGG.get());
      this.eggItem((Item)DAItems.BABY_ZEPHYR_SPAWN_EGG.get());
      this.eggItem((Item)DAItems.GENTLE_WIND_SPAWN_EGG.get());
      this.item((Item)DAItems.CHAOS_EMERALD.get());
      this.item((Item)DAItems.BRASS_DUNGEON_KEY.get());
      this.item((Item)DAItems.SPOOKY_RING.get());
      this.item((Item)DAItems.BIO_CRYSTAL.get());
      this.item(DABlocks.SKYJADE_CHAIN.asItem());
      this.item(DABlocks.SKYJADE_LANTERN.asItem());
      this.item((Item)DAItems.MUSIC_DISC_NABOORU.get());
      this.item((Item)DAItems.MUSIC_DISC_A_MORNING_WISH.get());
      this.item((Item)DAItems.MUSIC_DISC_CYCLONE.get());
      this.item((Item)DAItems.MUSIC_DISC_ABOVE_THE_RAIN.get());
      this.item((Item)DAItems.AERGLOW_BLOSSOM.get());
      this.item((Item)DAItems.PLACEABLE_POISON_BUCKET.get());
      this.item((Item)DAItems.REMEDY_BUCKET.get());
      this.item((Item)DAItems.FROZEN_GOLDEN_BERRIES.get());
      this.itemBlock((Block)DABlocks.COMBINER.get());
      this.item((Item)DAItems.MUSIC_DISC_ATTA.get());
      this.item((Item)DAItems.MUSIC_DISC_FAENT.get());
      this.item((Item)DAItems.MUSIC_DISC_HIMININN.get());
   }

   public void aercloudItem(Block block) {
      ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(this.blockName(block), this.mcLoc("block/cube_all")))
            .texture("all", this.texture(this.blockName(block))))
         .renderType(ResourceLocation.withDefaultNamespace("translucent"));
   }

   public void translucentItem(Item item) {
      ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/generated")))
            .renderType(ResourceLocation.withDefaultNamespace("translucent")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item)));
   }

   public void handHeld(Item item) {
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/handheld")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item)));
   }

   public void item(Item item) {
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/generated")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item)));
   }

   public void altItem(Item item, Item texture) {
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/generated")))
         .texture("layer0", this.modLoc("item/" + this.itemName(texture)));
   }

   public void placeholder(Item item) {
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/generated"))).texture("layer0", this.modLoc("item/placeholder"));
   }

   public void itemFence(Block block, Block baseBlock) {
      ((ItemModelBuilder)this.withExistingParent(this.blockName(block), this.mcLoc("block/fence_inventory")))
         .texture("texture", this.texture(this.blockName(baseBlock)));
   }

   public void itemBlockFlatName(Block block, String location) {
      ((ItemModelBuilder)this.withExistingParent(this.blockName(block), this.mcLoc("item/generated"))).texture("layer0", this.texture(location));
   }

   public void itemButton(Block block, Block baseBlock) {
      ((ItemModelBuilder)this.withExistingParent(this.blockName(block), this.mcLoc("block/button_inventory")))
         .texture("texture", this.texture(this.blockName(baseBlock)));
   }

   public void itemWallBlock(Block block, Block baseBlock) {
      this.wallInventory(this.blockName(block), this.texture(this.blockName(baseBlock)));
   }

   public void itemBlockFlat(Block block) {
      ((ItemModelBuilder)this.withExistingParent(this.blockName(block), this.mcLoc("item/generated"))).texture("layer0", this.texture(this.blockName(block)));
   }

   public void helmetItem(Item item) {
      this.armorItem(item, "helmet");
   }

   public void chestplateItem(Item item) {
      this.armorItem(item, "chestplate");
   }

   public void leggingsItem(Item item) {
      this.armorItem(item, "leggings");
   }

   public void bootsItem(Item item) {
      this.armorItem(item, "boots");
   }

   public void armorItem(Item item, String type) {
      ItemModelBuilder builder = (ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/generated")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item)));
      double index = 0.1;

      for (ResourceKey<TrimMaterial> trimMaterial : VANILLA_TRIM_MATERIALS) {
         String material = trimMaterial.location().getPath();
         String var10000 = this.itemName(item);
         String name = var10000 + "_" + material + "_trim";
         ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(name, this.mcLoc("item/generated")))
               .texture("layer0", this.modLoc("item/" + this.itemName(item))))
            .texture("layer1", this.mcLoc("trims/items/" + type + "_trim_" + material));
         builder.override()
            .predicate(ResourceLocation.withDefaultNamespace("trim_type"), (float)index)
            .model(this.getExistingFile(this.modLoc("item/" + name)))
            .end();
         index += 0.1;
      }
   }

   public void glovesItem(Item item) {
      ItemModelBuilder builder = (ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/generated")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item)));
      double index = 0.1;

      for (ResourceKey<TrimMaterial> trimMaterial : NitrogenItemModelProvider.VANILLA_TRIM_MATERIALS) {
         String material = trimMaterial.location().getPath();
         String var10000 = this.itemName(item);
         String name = var10000 + "_" + material + "_trim";
         ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(name, this.mcLoc("item/generated")))
               .texture("layer0", this.modLoc("item/" + this.itemName(item))))
            .texture("layer1", ResourceLocation.fromNamespaceAndPath("aether", "trims/items/gloves_trim_" + material));
         builder.override()
            .predicate(ResourceLocation.withDefaultNamespace("trim_type"), (float)index)
            .model(this.getExistingFile(this.modLoc("item/" + name)))
            .end();
         index += 0.1;
      }
   }

   public void bowItem(Item item) {
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item) + "_pulling_0", this.mcLoc("item/bow")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item) + "_pulling_0"));
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item) + "_pulling_1", this.mcLoc("item/bow")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item) + "_pulling_1"));
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item) + "_pulling_2", this.mcLoc("item/bow")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item) + "_pulling_2"));
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item) + "_pulling_0_special", this.mcLoc("item/bow")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item) + "_pulling_0_special"));
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item) + "_pulling_1_special", this.mcLoc("item/bow")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item) + "_pulling_1_special"));
      ((ItemModelBuilder)this.withExistingParent(this.itemName(item) + "_pulling_2_special", this.mcLoc("item/bow")))
         .texture("layer0", this.modLoc("item/" + this.itemName(item) + "_pulling_2_special"));
      ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(this.itemName(item), this.mcLoc("item/bow")))
            .texture("layer0", this.modLoc("item/" + this.itemName(item))))
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_0")))
         .end()
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65F)
         .model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_1")))
         .end()
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9F)
         .model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_2")))
         .end()
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.fromNamespaceAndPath("deep_aether", "enchanted"), 1.0F)
         .model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_0_special")))
         .end()
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65F)
         .predicate(ResourceLocation.fromNamespaceAndPath("deep_aether", "enchanted"), 1.0F)
         .model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_1_special")))
         .end()
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9F)
         .predicate(ResourceLocation.fromNamespaceAndPath("deep_aether", "enchanted"), 1.0F)
         .model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_2_special")))
         .end();
   }

   public void itemOverlayDungeonBlock(Block block, Block baseBlock, String overlay) {
      ((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(
                        this.blockName(block), this.mcLoc("block/cube")
                     ))
                     .texture("overlay", ResourceLocation.fromNamespaceAndPath("aether", "block/dungeon/" + overlay)))
                  .texture("face", this.texture(this.blockName(baseBlock))))
               .element()
               .from(0.0F, 0.0F, 0.0F)
               .to(16.0F, 16.0F, 16.0F)
               .allFaces((direction, builder) -> builder.texture("#face").cullface(direction).end())
               .end())
            .element()
            .from(0.0F, 0.0F, -0.1F)
            .to(16.0F, 16.0F, -0.1F)
            .rotation()
            .angle(0.0F)
            .axis(Axis.Y)
            .origin(8.0F, 8.0F, 6.9F)
            .end()
            .face(Direction.NORTH)
            .texture("#overlay")
            .emissivity(15, 15)
            .end()
            .end())
         .transforms()
         .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
         .rotation(75.0F, 45.0F, 0.0F)
         .translation(0.0F, 2.5F, 0.0F)
         .scale(0.375F, 0.375F, 0.375F)
         .end()
         .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
         .rotation(75.0F, 45.0F, 0.0F)
         .translation(0.0F, 2.5F, 0.0F)
         .scale(0.375F, 0.375F, 0.375F)
         .end()
         .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
         .rotation(-90.0F, -180.0F, -45.0F)
         .scale(0.4F, 0.4F, 0.4F)
         .end()
         .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
         .rotation(-90.0F, -180.0F, -45.0F)
         .scale(0.4F, 0.4F, 0.4F)
         .end()
         .transform(ItemDisplayContext.GROUND)
         .rotation(90.0F, 0.0F, 0.0F)
         .translation(0.0F, 3.0F, 0.0F)
         .scale(0.25F, 0.25F, 0.25F)
         .end()
         .transform(ItemDisplayContext.GUI)
         .rotation(30.0F, 135.0F, 0.0F)
         .scale(0.625F, 0.625F, 0.625F)
         .end()
         .transform(ItemDisplayContext.FIXED)
         .scale(0.5F, 0.5F, 0.5F)
         .end()
         .end();
   }
}
