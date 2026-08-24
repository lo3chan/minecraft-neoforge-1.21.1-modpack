package com.aetherteam.aether.data.generators;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.miscellaneous.FacingPillarBlock;
import com.aetherteam.aether.data.providers.AetherBlockStateProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherBlockStateData extends AetherBlockStateProvider {
   public AetherBlockStateData(PackOutput output, ExistingFileHelper helper) {
      super(output, "aether", helper);
   }

   public void registerStatesAndModels() {
      this.portal((Block)AetherBlocks.AETHER_PORTAL.get());
      this.grass((Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT.get());
      this.enchantedGrass(
         (Block)AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_GRASS_BLOCK.get(), (Block)AetherBlocks.AETHER_DIRT.get()
      );
      this.randomBlockDoubleDrops((Block)AetherBlocks.AETHER_DIRT.get(), "natural/");
      this.randomBlockDoubleDrops((Block)AetherBlocks.QUICKSOIL.get(), "natural/");
      this.blockDoubleDrops((Block)AetherBlocks.HOLYSTONE.get(), "natural/");
      this.blockDoubleDrops((Block)AetherBlocks.MOSSY_HOLYSTONE.get(), "natural/");
      this.farmland((Block)AetherBlocks.AETHER_FARMLAND.get(), (Block)AetherBlocks.AETHER_DIRT.get());
      this.dirtPath((Block)AetherBlocks.AETHER_DIRT_PATH.get(), (Block)AetherBlocks.AETHER_DIRT.get());
      this.aercloudAll((Block)AetherBlocks.COLD_AERCLOUD.get(), "natural/");
      this.aercloudAll((Block)AetherBlocks.BLUE_AERCLOUD.get(), "natural/");
      this.aercloudAll((Block)AetherBlocks.GOLDEN_AERCLOUD.get(), "natural/");
      this.block((Block)AetherBlocks.ICESTONE.get(), "natural/");
      this.block((Block)AetherBlocks.AMBROSIUM_ORE.get(), "natural/");
      this.block((Block)AetherBlocks.ZANITE_ORE.get(), "natural/");
      this.block((Block)AetherBlocks.GRAVITITE_ORE.get(), "natural/");
      this.block((Block)AetherBlocks.SKYROOT_LEAVES.get(), "natural/");
      this.block((Block)AetherBlocks.GOLDEN_OAK_LEAVES.get(), "natural/");
      this.block((Block)AetherBlocks.CRYSTAL_LEAVES.get(), "natural/");
      this.block((Block)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get(), "natural/");
      this.block((Block)AetherBlocks.HOLIDAY_LEAVES.get(), "natural/");
      this.block((Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get(), "natural/");
      this.log((RotatedPillarBlock)AetherBlocks.SKYROOT_LOG.get());
      this.enchantedLog((RotatedPillarBlock)AetherBlocks.GOLDEN_OAK_LOG.get(), (RotatedPillarBlock)AetherBlocks.SKYROOT_LOG.get());
      this.log((RotatedPillarBlock)AetherBlocks.STRIPPED_SKYROOT_LOG.get());
      this.wood((RotatedPillarBlock)AetherBlocks.SKYROOT_WOOD.get(), (RotatedPillarBlock)AetherBlocks.SKYROOT_LOG.get());
      this.wood((RotatedPillarBlock)AetherBlocks.GOLDEN_OAK_WOOD.get(), (RotatedPillarBlock)AetherBlocks.GOLDEN_OAK_LOG.get());
      this.wood((RotatedPillarBlock)AetherBlocks.STRIPPED_SKYROOT_WOOD.get(), (RotatedPillarBlock)AetherBlocks.STRIPPED_SKYROOT_LOG.get());
      this.block((Block)AetherBlocks.SKYROOT_PLANKS.get(), "construction/");
      this.block((Block)AetherBlocks.HOLYSTONE_BRICKS.get(), "construction/");
      this.translucentBlock((Block)AetherBlocks.QUICKSOIL_GLASS.get(), "construction/");
      this.pane((IronBarsBlock)AetherBlocks.QUICKSOIL_GLASS_PANE.get(), (TransparentBlock)AetherBlocks.QUICKSOIL_GLASS.get(), "construction/");
      this.translucentBlock((Block)AetherBlocks.AEROGEL.get(), "construction/");
      this.block((Block)AetherBlocks.AMBROSIUM_BLOCK.get(), "construction/");
      this.block((Block)AetherBlocks.ZANITE_BLOCK.get(), "construction/");
      this.block((Block)AetherBlocks.ENCHANTED_GRAVITITE.get(), "construction/");
      this.altar((Block)AetherBlocks.ALTAR.get());
      this.freezer((Block)AetherBlocks.FREEZER.get());
      this.incubator((Block)AetherBlocks.INCUBATOR.get());
      this.torchBlock((Block)AetherBlocks.AMBROSIUM_TORCH.get(), (Block)AetherBlocks.AMBROSIUM_WALL_TORCH.get());
      this.signBlock(
         (StandingSignBlock)AetherBlocks.SKYROOT_SIGN.get(),
         (WallSignBlock)AetherBlocks.SKYROOT_WALL_SIGN.get(),
         this.texture(this.name((Block)AetherBlocks.SKYROOT_PLANKS.get()), "construction/")
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)AetherBlocks.SKYROOT_HANGING_SIGN.get(),
         (WallHangingSignBlock)AetherBlocks.SKYROOT_WALL_HANGING_SIGN.get(),
         this.texture(this.name((Block)AetherBlocks.STRIPPED_SKYROOT_LOG.get()), "natural/")
      );
      this.crossBlock((Block)AetherBlocks.BERRY_BUSH_STEM.get(), "natural/");
      this.berryBush((Block)AetherBlocks.BERRY_BUSH.get(), (Block)AetherBlocks.BERRY_BUSH_STEM.get());
      this.pottedStem((Block)AetherBlocks.POTTED_BERRY_BUSH_STEM.get(), "natural/");
      this.pottedBush((Block)AetherBlocks.POTTED_BERRY_BUSH.get(), (Block)AetherBlocks.POTTED_BERRY_BUSH_STEM.get(), "natural/");
      this.crossBlock((Block)AetherBlocks.PURPLE_FLOWER.get(), "natural/");
      this.crossBlock((Block)AetherBlocks.WHITE_FLOWER.get(), "natural/");
      this.pottedPlant((Block)AetherBlocks.POTTED_PURPLE_FLOWER.get(), (Block)AetherBlocks.PURPLE_FLOWER.get(), "natural/");
      this.pottedPlant((Block)AetherBlocks.POTTED_WHITE_FLOWER.get(), (Block)AetherBlocks.WHITE_FLOWER.get(), "natural/");
      this.saplingBlock((Block)AetherBlocks.SKYROOT_SAPLING.get(), "natural/");
      this.saplingBlock((Block)AetherBlocks.GOLDEN_OAK_SAPLING.get(), "natural/");
      this.pottedPlant((Block)AetherBlocks.POTTED_SKYROOT_SAPLING.get(), (Block)AetherBlocks.SKYROOT_SAPLING.get(), "natural/");
      this.pottedPlant((Block)AetherBlocks.POTTED_GOLDEN_OAK_SAPLING.get(), (Block)AetherBlocks.GOLDEN_OAK_SAPLING.get(), "natural/");
      this.block((Block)AetherBlocks.CARVED_STONE.get(), "dungeon/");
      this.block((Block)AetherBlocks.SENTRY_STONE.get(), "dungeon/");
      this.block((Block)AetherBlocks.ANGELIC_STONE.get(), "dungeon/");
      this.block((Block)AetherBlocks.LIGHT_ANGELIC_STONE.get(), "dungeon/");
      this.block((Block)AetherBlocks.HELLFIRE_STONE.get(), "dungeon/");
      this.block((Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get(), "dungeon/");
      this.dungeonBlock((Block)AetherBlocks.LOCKED_CARVED_STONE.get(), (Block)AetherBlocks.CARVED_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.LOCKED_SENTRY_STONE.get(), (Block)AetherBlocks.SENTRY_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(), (Block)AetherBlocks.ANGELIC_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get(), (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.LOCKED_HELLFIRE_STONE.get(), (Block)AetherBlocks.HELLFIRE_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE.get(), (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TRAPPED_CARVED_STONE.get(), (Block)AetherBlocks.CARVED_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TRAPPED_SENTRY_STONE.get(), (Block)AetherBlocks.SENTRY_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TRAPPED_ANGELIC_STONE.get(), (Block)AetherBlocks.ANGELIC_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get(), (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TRAPPED_HELLFIRE_STONE.get(), (Block)AetherBlocks.HELLFIRE_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TRAPPED_LIGHT_HELLFIRE_STONE.get(), (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get());
      this.invisibleBlock((Block)AetherBlocks.BOSS_DOORWAY_CARVED_STONE.get(), (Block)AetherBlocks.CARVED_STONE.get());
      this.invisibleBlock((Block)AetherBlocks.BOSS_DOORWAY_SENTRY_STONE.get(), (Block)AetherBlocks.SENTRY_STONE.get());
      this.invisibleBlock((Block)AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE.get(), (Block)AetherBlocks.ANGELIC_STONE.get());
      this.invisibleBlock((Block)AetherBlocks.BOSS_DOORWAY_LIGHT_ANGELIC_STONE.get(), (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get());
      this.invisibleBlock((Block)AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE.get(), (Block)AetherBlocks.HELLFIRE_STONE.get());
      this.invisibleBlock((Block)AetherBlocks.BOSS_DOORWAY_LIGHT_HELLFIRE_STONE.get(), (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TREASURE_DOORWAY_CARVED_STONE.get(), (Block)AetherBlocks.CARVED_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE.get(), (Block)AetherBlocks.SENTRY_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE.get(), (Block)AetherBlocks.ANGELIC_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_ANGELIC_STONE.get(), (Block)AetherBlocks.LIGHT_ANGELIC_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE.get(), (Block)AetherBlocks.HELLFIRE_STONE.get());
      this.dungeonBlock((Block)AetherBlocks.TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE.get(), (Block)AetherBlocks.LIGHT_HELLFIRE_STONE.get());
      this.chestMimic((Block)AetherBlocks.CHEST_MIMIC.get(), Blocks.OAK_PLANKS);
      this.treasureChest((Block)AetherBlocks.TREASURE_CHEST.get(), (Block)AetherBlocks.CARVED_STONE.get());
      this.pillar((RotatedPillarBlock)AetherBlocks.PILLAR.get());
      this.pillarTop((FacingPillarBlock)AetherBlocks.PILLAR_TOP.get());
      this.present((Block)AetherBlocks.PRESENT.get());
      this.fence((FenceBlock)AetherBlocks.SKYROOT_FENCE.get(), (Block)AetherBlocks.SKYROOT_PLANKS.get(), "construction/");
      this.fenceGateBlock((FenceGateBlock)AetherBlocks.SKYROOT_FENCE_GATE.get(), (Block)AetherBlocks.SKYROOT_PLANKS.get(), "construction/");
      this.doorBlock(
         (DoorBlock)AetherBlocks.SKYROOT_DOOR.get(),
         this.texture(this.name((Block)AetherBlocks.SKYROOT_DOOR.get()), "construction/", "_bottom"),
         this.texture(this.name((Block)AetherBlocks.SKYROOT_DOOR.get()), "construction/", "_top")
      );
      this.trapdoorBlock(
         (TrapDoorBlock)AetherBlocks.SKYROOT_TRAPDOOR.get(), this.texture(this.name((Block)AetherBlocks.SKYROOT_TRAPDOOR.get()), "construction/"), false
      );
      this.buttonBlock((ButtonBlock)AetherBlocks.SKYROOT_BUTTON.get(), this.texture(this.name((Block)AetherBlocks.SKYROOT_PLANKS.get()), "construction/"));
      this.pressurePlateBlock(
         (PressurePlateBlock)AetherBlocks.SKYROOT_PRESSURE_PLATE.get(), this.texture(this.name((Block)AetherBlocks.SKYROOT_PLANKS.get()), "construction/")
      );
      this.buttonBlock((ButtonBlock)AetherBlocks.HOLYSTONE_BUTTON.get(), this.texture(this.name((Block)AetherBlocks.HOLYSTONE.get()), "natural/"));
      this.pressurePlateBlock(
         (PressurePlateBlock)AetherBlocks.HOLYSTONE_PRESSURE_PLATE.get(), this.texture(this.name((Block)AetherBlocks.HOLYSTONE.get()), "natural/")
      );
      this.wallBlock((WallBlock)AetherBlocks.CARVED_WALL.get(), (Block)AetherBlocks.CARVED_STONE.get(), "dungeon/");
      this.wallBlock((WallBlock)AetherBlocks.ANGELIC_WALL.get(), (Block)AetherBlocks.ANGELIC_STONE.get(), "dungeon/");
      this.wallBlock((WallBlock)AetherBlocks.HELLFIRE_WALL.get(), (Block)AetherBlocks.HELLFIRE_STONE.get(), "dungeon/");
      this.wallBlock((WallBlock)AetherBlocks.HOLYSTONE_WALL.get(), (Block)AetherBlocks.HOLYSTONE.get(), "natural/");
      this.wallBlock((WallBlock)AetherBlocks.MOSSY_HOLYSTONE_WALL.get(), (Block)AetherBlocks.MOSSY_HOLYSTONE.get(), "natural/");
      this.wallBlock((WallBlock)AetherBlocks.ICESTONE_WALL.get(), (Block)AetherBlocks.ICESTONE.get(), "natural/");
      this.wallBlock((WallBlock)AetherBlocks.HOLYSTONE_BRICK_WALL.get(), (Block)AetherBlocks.HOLYSTONE_BRICKS.get(), "construction/");
      this.stairs((StairBlock)AetherBlocks.SKYROOT_STAIRS.get(), (Block)AetherBlocks.SKYROOT_PLANKS.get(), "construction/");
      this.stairs((StairBlock)AetherBlocks.CARVED_STAIRS.get(), (Block)AetherBlocks.CARVED_STONE.get(), "dungeon/");
      this.stairs((StairBlock)AetherBlocks.ANGELIC_STAIRS.get(), (Block)AetherBlocks.ANGELIC_STONE.get(), "dungeon/");
      this.stairs((StairBlock)AetherBlocks.HELLFIRE_STAIRS.get(), (Block)AetherBlocks.HELLFIRE_STONE.get(), "dungeon/");
      this.stairs((StairBlock)AetherBlocks.HOLYSTONE_STAIRS.get(), (Block)AetherBlocks.HOLYSTONE.get(), "natural/");
      this.stairs((StairBlock)AetherBlocks.MOSSY_HOLYSTONE_STAIRS.get(), (Block)AetherBlocks.MOSSY_HOLYSTONE.get(), "natural/");
      this.stairs((StairBlock)AetherBlocks.ICESTONE_STAIRS.get(), (Block)AetherBlocks.ICESTONE.get(), "natural/");
      this.stairs((StairBlock)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get(), (Block)AetherBlocks.HOLYSTONE_BRICKS.get(), "construction/");
      this.slab((SlabBlock)AetherBlocks.SKYROOT_SLAB.get(), (Block)AetherBlocks.SKYROOT_PLANKS.get(), "construction/");
      this.slab((SlabBlock)AetherBlocks.CARVED_SLAB.get(), (Block)AetherBlocks.CARVED_STONE.get(), "dungeon/");
      this.slab((SlabBlock)AetherBlocks.ANGELIC_SLAB.get(), (Block)AetherBlocks.ANGELIC_STONE.get(), "dungeon/");
      this.slab((SlabBlock)AetherBlocks.HELLFIRE_SLAB.get(), (Block)AetherBlocks.HELLFIRE_STONE.get(), "dungeon/");
      this.slab((SlabBlock)AetherBlocks.HOLYSTONE_SLAB.get(), (Block)AetherBlocks.HOLYSTONE.get(), "natural/");
      this.slab((SlabBlock)AetherBlocks.MOSSY_HOLYSTONE_SLAB.get(), (Block)AetherBlocks.MOSSY_HOLYSTONE.get(), "natural/");
      this.slab((SlabBlock)AetherBlocks.ICESTONE_SLAB.get(), (Block)AetherBlocks.ICESTONE.get(), "natural/");
      this.slab((SlabBlock)AetherBlocks.HOLYSTONE_BRICK_SLAB.get(), (Block)AetherBlocks.HOLYSTONE_BRICKS.get(), "construction/");
      this.translucentSlab((Block)AetherBlocks.AEROGEL_SLAB.get(), (Block)AetherBlocks.AEROGEL.get(), "construction/");
      this.sunAltar((Block)AetherBlocks.SUN_ALTAR.get());
      this.bookshelf((Block)AetherBlocks.SKYROOT_BOOKSHELF.get(), (Block)AetherBlocks.SKYROOT_PLANKS.get());
      this.bed((Block)AetherBlocks.SKYROOT_BED.get(), (Block)AetherBlocks.SKYROOT_PLANKS.get());
      this.frostedIce((Block)AetherBlocks.FROSTED_ICE.get(), Blocks.FROSTED_ICE);
      this.unstableObsidian((Block)AetherBlocks.UNSTABLE_OBSIDIAN.get());
   }
}
