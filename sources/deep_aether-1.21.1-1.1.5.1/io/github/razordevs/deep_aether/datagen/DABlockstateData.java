package io.github.razordevs.deep_aether.datagen;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.data.providers.AetherBlockStateProvider;
import io.github.razordevs.deep_aether.init.DABlocks;
import java.util.Map.Entry;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder.FaceRotation;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DABlockstateData extends AetherBlockStateProvider {
   public DABlockstateData(PackOutput output, ExistingFileHelper helper) {
      super(output, "deep_aether", helper);
   }

   public void registerStatesAndModels() {
      this.blockDoubleDrops((Block)DABlocks.HIGHSTONE.get());
      this.wood((RotatedPillarBlock)DABlocks.ROSEROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.ROSEROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.ROSEROOT_LOG.get());
      this.wood((RotatedPillarBlock)DABlocks.STRIPPED_ROSEROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.block((Block)DABlocks.ROSEROOT_PLANKS.get());
      this.slab((SlabBlock)DABlocks.ROSEROOT_SLAB.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.stairs((StairBlock)DABlocks.ROSEROOT_STAIRS.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.fence((FenceBlock)DABlocks.ROSEROOT_FENCE.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.fenceGateBlock((FenceGateBlock)DABlocks.ROSEROOT_FENCE_GATE.get(), (Block)DABlocks.ROSEROOT_PLANKS.get());
      this.doorBlock(
         (DoorBlock)DABlocks.ROSEROOT_DOOR.get(),
         this.texture(this.name((Block)DABlocks.ROSEROOT_DOOR.get()) + "_bottom"),
         this.texture(this.name((Block)DABlocks.ROSEROOT_DOOR.get()) + "_top")
      );
      this.trapdoorBlock((TrapDoorBlock)DABlocks.ROSEROOT_TRAPDOOR.get(), this.texture(this.name((Block)DABlocks.ROSEROOT_TRAPDOOR.get())), false);
      this.buttonBlock((ButtonBlock)DABlocks.ROSEROOT_BUTTON.get(), this.texture(this.name((Block)DABlocks.ROSEROOT_PLANKS.get())));
      this.pressurePlateBlock((PressurePlateBlock)DABlocks.ROSEROOT_PRESSURE_PLATE.get(), this.texture(this.name((Block)DABlocks.ROSEROOT_PLANKS.get())));
      this.wallBlock((WallBlock)DABlocks.ROSEROOT_WOOD_WALL.get(), (Block)DABlocks.ROSEROOT_LOG.get());
      this.wallBlock((WallBlock)DABlocks.STRIPPED_ROSEROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get());
      this.saplingBlock((Block)DABlocks.ROSEROOT_SAPLING.get());
      this.saplingBlock((Block)DABlocks.BLUE_ROSEROOT_SAPLING.get());
      this.pottedPlant((Block)DABlocks.POTTED_ROSEROOT_SAPLING.get(), (Block)DABlocks.ROSEROOT_SAPLING.get());
      this.pottedPlant((Block)DABlocks.POTTED_BLUE_ROSEROOT_SAPLING.get(), (Block)DABlocks.BLUE_ROSEROOT_SAPLING.get());
      this.block((Block)DABlocks.ROSEROOT_LEAVES.get());
      this.block((Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get());
      this.block((Block)DABlocks.BLUE_ROSEROOT_LEAVES.get());
      this.block((Block)DABlocks.FLOWERING_BLUE_ROSEROOT_LEAVES.get());
      this.block((Block)DABlocks.AERGLOW_BLOSSOM_BLOCK.get());
      this.signBlock(
         (StandingSignBlock)DABlocks.ROSEROOT_SIGN.get(),
         (WallSignBlock)DABlocks.ROSEROOT_WALL_SIGN.get(),
         this.texture(this.name((Block)DABlocks.ROSEROOT_PLANKS.get()))
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)DABlocks.ROSEROOT_HANGING_SIGN.get(),
         (WallHangingSignBlock)DABlocks.ROSEROOT_WALL_HANGING_SIGN.get(),
         this.texture(this.name((Block)DABlocks.STRIPPED_ROSEROOT_LOG.get()))
      );
      this.wood((RotatedPillarBlock)DABlocks.YAGROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.YAGROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.YAGROOT_LOG.get());
      this.wood((RotatedPillarBlock)DABlocks.STRIPPED_YAGROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.block((Block)DABlocks.YAGROOT_PLANKS.get());
      this.slab((SlabBlock)DABlocks.YAGROOT_SLAB.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.stairs((StairBlock)DABlocks.YAGROOT_STAIRS.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.fence((FenceBlock)DABlocks.YAGROOT_FENCE.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.fenceGateBlock((FenceGateBlock)DABlocks.YAGROOT_FENCE_GATE.get(), (Block)DABlocks.YAGROOT_PLANKS.get());
      this.doorBlock(
         (DoorBlock)DABlocks.YAGROOT_DOOR.get(),
         this.texture(this.name((Block)DABlocks.YAGROOT_DOOR.get()) + "_bottom"),
         this.texture(this.name((Block)DABlocks.YAGROOT_DOOR.get()) + "_top")
      );
      this.trapdoorBlock((TrapDoorBlock)DABlocks.YAGROOT_TRAPDOOR.get(), this.texture(this.name((Block)DABlocks.YAGROOT_TRAPDOOR.get())), false);
      this.buttonBlock((ButtonBlock)DABlocks.YAGROOT_BUTTON.get(), this.texture(this.name((Block)DABlocks.YAGROOT_PLANKS.get())));
      this.pressurePlateBlock((PressurePlateBlock)DABlocks.YAGROOT_PRESSURE_PLATE.get(), this.texture(this.name((Block)DABlocks.YAGROOT_PLANKS.get())));
      this.wallBlock((WallBlock)DABlocks.YAGROOT_WOOD_WALL.get(), (Block)DABlocks.YAGROOT_LOG.get());
      this.wallBlock((WallBlock)DABlocks.STRIPPED_YAGROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_YAGROOT_LOG.get());
      this.saplingBlock((Block)DABlocks.YAGROOT_SAPLING.get());
      this.pottedPlant((Block)DABlocks.POTTED_YAGROOT_SAPLING.get(), (Block)DABlocks.YAGROOT_SAPLING.get());
      this.block((Block)DABlocks.YAGROOT_LEAVES.get());
      this.signBlock(
         (StandingSignBlock)DABlocks.YAGROOT_SIGN.get(),
         (WallSignBlock)DABlocks.YAGROOT_WALL_SIGN.get(),
         this.texture(this.name((Block)DABlocks.YAGROOT_PLANKS.get()))
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)DABlocks.YAGROOT_HANGING_SIGN.get(),
         (WallHangingSignBlock)DABlocks.YAGROOT_WALL_HANGING_SIGN.get(),
         this.texture(this.name((Block)DABlocks.STRIPPED_YAGROOT_LOG.get()))
      );
      this.wood((RotatedPillarBlock)DABlocks.CRUDEROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.CRUDEROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.CRUDEROOT_LOG.get());
      this.wood((RotatedPillarBlock)DABlocks.STRIPPED_CRUDEROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.block((Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.slab((SlabBlock)DABlocks.CRUDEROOT_SLAB.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.stairs((StairBlock)DABlocks.CRUDEROOT_STAIRS.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.fence((FenceBlock)DABlocks.CRUDEROOT_FENCE.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.fenceGateBlock((FenceGateBlock)DABlocks.CRUDEROOT_FENCE_GATE.get(), (Block)DABlocks.CRUDEROOT_PLANKS.get());
      this.doorBlock(
         (DoorBlock)DABlocks.CRUDEROOT_DOOR.get(),
         this.texture(this.name((Block)DABlocks.CRUDEROOT_DOOR.get()) + "_bottom"),
         this.texture(this.name((Block)DABlocks.CRUDEROOT_DOOR.get()) + "_top")
      );
      this.trapdoorBlock((TrapDoorBlock)DABlocks.CRUDEROOT_TRAPDOOR.get(), this.texture(this.name((Block)DABlocks.CRUDEROOT_TRAPDOOR.get())), false);
      this.buttonBlock((ButtonBlock)DABlocks.CRUDEROOT_BUTTON.get(), this.texture(this.name((Block)DABlocks.CRUDEROOT_PLANKS.get())));
      this.pressurePlateBlock((PressurePlateBlock)DABlocks.CRUDEROOT_PRESSURE_PLATE.get(), this.texture(this.name((Block)DABlocks.CRUDEROOT_PLANKS.get())));
      this.wallBlock((WallBlock)DABlocks.CRUDEROOT_WOOD_WALL.get(), (Block)DABlocks.CRUDEROOT_LOG.get());
      this.wallBlock((WallBlock)DABlocks.STRIPPED_CRUDEROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get());
      this.saplingBlock((Block)DABlocks.CRUDEROOT_SAPLING.get());
      this.pottedPlant((Block)DABlocks.POTTED_CRUDEROOT_SAPLING.get(), (Block)DABlocks.CRUDEROOT_SAPLING.get());
      this.block((Block)DABlocks.CRUDEROOT_LEAVES.get());
      this.signBlock(
         (StandingSignBlock)DABlocks.CRUDEROOT_SIGN.get(),
         (WallSignBlock)DABlocks.CRUDEROOT_WALL_SIGN.get(),
         this.texture(this.name((Block)DABlocks.CRUDEROOT_PLANKS.get()))
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)DABlocks.CRUDEROOT_HANGING_SIGN.get(),
         (WallHangingSignBlock)DABlocks.CRUDEROOT_WALL_HANGING_SIGN.get(),
         this.texture(this.name((Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get()))
      );
      this.wood((RotatedPillarBlock)DABlocks.CONBERRY_WOOD.get(), (RotatedPillarBlock)DABlocks.CONBERRY_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.CONBERRY_LOG.get());
      this.wood((RotatedPillarBlock)DABlocks.STRIPPED_CONBERRY_WOOD.get(), (RotatedPillarBlock)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.block((Block)DABlocks.CONBERRY_PLANKS.get());
      this.slab((SlabBlock)DABlocks.CONBERRY_SLAB.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.stairs((StairBlock)DABlocks.CONBERRY_STAIRS.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.fence((FenceBlock)DABlocks.CONBERRY_FENCE.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.fenceGateBlock((FenceGateBlock)DABlocks.CONBERRY_FENCE_GATE.get(), (Block)DABlocks.CONBERRY_PLANKS.get());
      this.doorBlock(
         (DoorBlock)DABlocks.CONBERRY_DOOR.get(),
         this.texture(this.name((Block)DABlocks.CONBERRY_DOOR.get()) + "_bottom"),
         this.texture(this.name((Block)DABlocks.CONBERRY_DOOR.get()) + "_top")
      );
      this.trapdoorBlock((TrapDoorBlock)DABlocks.CONBERRY_TRAPDOOR.get(), this.texture(this.name((Block)DABlocks.CONBERRY_TRAPDOOR.get())), false);
      this.buttonBlock((ButtonBlock)DABlocks.CONBERRY_BUTTON.get(), this.texture(this.name((Block)DABlocks.CONBERRY_PLANKS.get())));
      this.pressurePlateBlock((PressurePlateBlock)DABlocks.CONBERRY_PRESSURE_PLATE.get(), this.texture(this.name((Block)DABlocks.CONBERRY_PLANKS.get())));
      this.wallBlock((WallBlock)DABlocks.CONBERRY_WOOD_WALL.get(), (Block)DABlocks.CONBERRY_LOG.get());
      this.wallBlock((WallBlock)DABlocks.STRIPPED_CONBERRY_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_CONBERRY_LOG.get());
      this.saplingBlock((Block)DABlocks.CONBERRY_SAPLING.get());
      this.pottedPlant((Block)DABlocks.POTTED_CONBERRY_SAPLING.get(), (Block)DABlocks.CONBERRY_SAPLING.get());
      this.block((Block)DABlocks.CONBERRY_LEAVES.get());
      this.signBlock(
         (StandingSignBlock)DABlocks.CONBERRY_SIGN.get(),
         (WallSignBlock)DABlocks.CONBERRY_WALL_SIGN.get(),
         this.texture(this.name((Block)DABlocks.CONBERRY_PLANKS.get()))
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)DABlocks.CONBERRY_HANGING_SIGN.get(),
         (WallHangingSignBlock)DABlocks.CONBERRY_WALL_HANGING_SIGN.get(),
         this.texture(this.name((Block)DABlocks.STRIPPED_CONBERRY_LOG.get()))
      );
      this.wood((RotatedPillarBlock)DABlocks.SUNROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.SUNROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.SUNROOT_LOG.get());
      this.wood((RotatedPillarBlock)DABlocks.STRIPPED_SUNROOT_WOOD.get(), (RotatedPillarBlock)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.log((RotatedPillarBlock)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.block((Block)DABlocks.SUNROOT_PLANKS.get());
      this.slab((SlabBlock)DABlocks.SUNROOT_SLAB.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.stairs((StairBlock)DABlocks.SUNROOT_STAIRS.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.fence((FenceBlock)DABlocks.SUNROOT_FENCE.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.fenceGateBlock((FenceGateBlock)DABlocks.SUNROOT_FENCE_GATE.get(), (Block)DABlocks.SUNROOT_PLANKS.get());
      this.doorBlock(
         (DoorBlock)DABlocks.SUNROOT_DOOR.get(),
         this.texture(this.name((Block)DABlocks.SUNROOT_DOOR.get()) + "_bottom"),
         this.texture(this.name((Block)DABlocks.SUNROOT_DOOR.get()) + "_top")
      );
      this.trapdoorBlock((TrapDoorBlock)DABlocks.SUNROOT_TRAPDOOR.get(), this.texture(this.name((Block)DABlocks.SUNROOT_TRAPDOOR.get())), false);
      this.buttonBlock((ButtonBlock)DABlocks.SUNROOT_BUTTON.get(), this.texture(this.name((Block)DABlocks.SUNROOT_PLANKS.get())));
      this.pressurePlateBlock((PressurePlateBlock)DABlocks.SUNROOT_PRESSURE_PLATE.get(), this.texture(this.name((Block)DABlocks.SUNROOT_PLANKS.get())));
      this.wallBlock((WallBlock)DABlocks.SUNROOT_WOOD_WALL.get(), (Block)DABlocks.SUNROOT_LOG.get());
      this.wallBlock((WallBlock)DABlocks.STRIPPED_SUNROOT_WOOD_WALL.get(), (Block)DABlocks.STRIPPED_SUNROOT_LOG.get());
      this.saplingBlock((Block)DABlocks.SUNROOT_SAPLING.get());
      this.pottedPlant((Block)DABlocks.POTTED_SUNROOT_SAPLING.get(), (Block)DABlocks.SUNROOT_SAPLING.get());
      this.signBlock(
         (StandingSignBlock)DABlocks.SUNROOT_SIGN.get(),
         (WallSignBlock)DABlocks.SUNROOT_WALL_SIGN.get(),
         this.texture(this.name((Block)DABlocks.SUNROOT_PLANKS.get()))
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)DABlocks.SUNROOT_HANGING_SIGN.get(),
         (WallHangingSignBlock)DABlocks.SUNROOT_WALL_HANGING_SIGN.get(),
         this.texture(this.name((Block)DABlocks.STRIPPED_SUNROOT_LOG.get()))
      );
      this.blockDoubleDrops((Block)DABlocks.AETHER_MUD.get());
      this.blockDoubleDrops((Block)DABlocks.AETHER_COARSE_DIRT.get());
      this.block((Block)DABlocks.PACKED_AETHER_MUD.get());
      this.block((Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.slab((SlabBlock)DABlocks.AETHER_MUD_BRICKS_SLAB.get(), (Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.stairs((StairBlock)DABlocks.AETHER_MUD_BRICKS_STAIRS.get(), (Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.wallBlock((WallBlock)DABlocks.AETHER_MUD_BRICKS_WALL.get(), (Block)DABlocks.AETHER_MUD_BRICKS.get());
      this.block((Block)DABlocks.SKYJADE_ORE.get());
      this.block((Block)DABlocks.SKYJADE_BLOCK.get());
      this.block((Block)DABlocks.STRATUS_BLOCK.get());
      this.block((Block)DABlocks.COBBLED_ASETERITE.get());
      this.stairs((StairBlock)DABlocks.COBBLED_ASETERITE_STAIRS.get(), (Block)DABlocks.COBBLED_ASETERITE.get());
      this.slab((SlabBlock)DABlocks.COBBLED_ASETERITE_SLAB.get(), (Block)DABlocks.COBBLED_ASETERITE.get());
      this.wallBlock((WallBlock)DABlocks.COBBLED_ASETERITE_WALL.get(), (Block)DABlocks.COBBLED_ASETERITE.get());
      this.blockDoubleDrops((Block)DABlocks.ASETERITE.get());
      this.stairs((StairBlock)DABlocks.ASETERITE_STAIRS.get(), (Block)DABlocks.ASETERITE.get());
      this.slab((SlabBlock)DABlocks.ASETERITE_SLAB.get(), (Block)DABlocks.ASETERITE.get());
      this.wallBlock((WallBlock)DABlocks.ASETERITE_WALL.get(), (Block)DABlocks.ASETERITE.get());
      this.block((Block)DABlocks.POLISHED_ASETERITE.get());
      this.stairs((StairBlock)DABlocks.POLISHED_ASETERITE_STAIRS.get(), (Block)DABlocks.POLISHED_ASETERITE.get());
      this.slab((SlabBlock)DABlocks.POLISHED_ASETERITE_SLAB.get(), (Block)DABlocks.POLISHED_ASETERITE.get());
      this.wallBlock((WallBlock)DABlocks.POLISHED_ASETERITE_WALL.get(), (Block)DABlocks.POLISHED_ASETERITE.get());
      this.block((Block)DABlocks.ASETERITE_BRICKS.get());
      this.stairs((StairBlock)DABlocks.ASETERITE_BRICKS_STAIRS.get(), (Block)DABlocks.ASETERITE_BRICKS.get());
      this.slab((SlabBlock)DABlocks.ASETERITE_BRICKS_SLAB.get(), (Block)DABlocks.ASETERITE_BRICKS.get());
      this.wallBlock((WallBlock)DABlocks.ASETERITE_BRICKS_WALL.get(), (Block)DABlocks.ASETERITE_BRICKS.get());
      this.blockDoubleDrops((Block)DABlocks.RAW_CLORITE.get());
      this.stairs((StairBlock)DABlocks.RAW_CLORITE_STAIRS.get(), (Block)DABlocks.RAW_CLORITE.get());
      this.slab((SlabBlock)DABlocks.RAW_CLORITE_SLAB.get(), (Block)DABlocks.RAW_CLORITE.get());
      this.block((Block)DABlocks.CLORITE.get());
      this.stairs((StairBlock)DABlocks.CLORITE_STAIRS.get(), (Block)DABlocks.CLORITE.get());
      this.slab((SlabBlock)DABlocks.CLORITE_SLAB.get(), (Block)DABlocks.CLORITE.get());
      this.wallBlock((WallBlock)DABlocks.CLORITE_WALL.get(), (Block)DABlocks.CLORITE.get());
      this.wallBlock((WallBlock)DABlocks.RAW_CLORITE_WALL.get(), (Block)DABlocks.RAW_CLORITE.get());
      this.wallBlock((WallBlock)DABlocks.POLISHED_CLORITE_WALL.get(), (Block)DABlocks.POLISHED_CLORITE.get());
      this.block((Block)DABlocks.POLISHED_CLORITE.get());
      this.stairs((StairBlock)DABlocks.POLISHED_CLORITE_STAIRS.get(), (Block)DABlocks.POLISHED_CLORITE.get());
      this.slab((SlabBlock)DABlocks.POLISHED_CLORITE_SLAB.get(), (Block)DABlocks.POLISHED_CLORITE.get());
      this.log((RotatedPillarBlock)DABlocks.CLORITE_PILLAR.get());
      this.crossBlock((Block)DABlocks.AERLAVENDER.get());
      this.crossBlock((Block)DABlocks.TALL_AERLAVENDER.get());
      this.crossBlock((Block)DABlocks.AETHER_CATTAILS.get());
      this.crossBlock((Block)DABlocks.RADIANT_ORCHID.get());
      this.crossBlock((Block)DABlocks.ENCHANTED_BLOSSOM.get());
      this.crossBlock((Block)DABlocks.SKY_TULIPS.get());
      this.crossBlock((Block)DABlocks.IASPOVE.get());
      this.crossBlock((Block)DABlocks.GOLDEN_ASPESS.get());
      this.crossBlock((Block)DABlocks.ECHAISY.get());
      this.pottedPlantFix((Block)DABlocks.POTTED_AERLAVENDER.get(), (Block)DABlocks.AERLAVENDER.get());
      this.pottedPlantFix((Block)DABlocks.POTTED_TALL_AERLAVENDER.get(), (Block)DABlocks.TALL_AERLAVENDER.get());
      this.pottedPlantFix((Block)DABlocks.POTTED_AETHER_CATTAILS.get(), (Block)DABlocks.AETHER_CATTAILS.get());
      this.pottedPlant((Block)DABlocks.POTTED_RADIANT_ORCHID.get(), (Block)DABlocks.RADIANT_ORCHID.get());
      this.pottedPlant((Block)DABlocks.POTTED_ENCHANTED_BLOSSOM.get(), (Block)DABlocks.ENCHANTED_BLOSSOM.get());
      this.pottedPlant((Block)DABlocks.POTTED_SKY_TULIPS.get(), (Block)DABlocks.SKY_TULIPS.get());
      this.pottedPlant((Block)DABlocks.POTTED_GOLDEN_ASPESS.get(), (Block)DABlocks.GOLDEN_ASPESS.get());
      this.pottedPlant((Block)DABlocks.POTTED_IASPOVE.get(), (Block)DABlocks.IASPOVE.get());
      this.pottedPlant((Block)DABlocks.POTTED_ECHAISY.get(), (Block)DABlocks.ECHAISY.get());
      this.crossBlock((Block)DABlocks.FEATHER_GRASS.get());
      this.crossBlock((Block)DABlocks.MINI_GOLDEN_GRASS.get());
      this.crossBlock((Block)DABlocks.MEDIUM_GOLDEN_GRASS.get());
      this.crossBlock((Block)DABlocks.SHORT_GOLDEN_GRASS.get());
      this.crossBlock((Block)DABlocks.LIGHTCAP_MUSHROOMS.get());
      this.block((Block)DABlocks.HOLYSTONE_TILES.get());
      this.stairs((StairBlock)DABlocks.HOLYSTONE_TILE_STAIRS.get(), (Block)DABlocks.HOLYSTONE_TILES.get());
      this.slab((SlabBlock)DABlocks.HOLYSTONE_TILE_SLAB.get(), (Block)DABlocks.HOLYSTONE_TILES.get());
      this.wallBlock((WallBlock)DABlocks.HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.HOLYSTONE_TILES.get());
      this.block((Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.stairs((StairBlock)DABlocks.MOSSY_HOLYSTONE_BRICK_STAIRS.get(), (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.slab((SlabBlock)DABlocks.MOSSY_HOLYSTONE_BRICK_SLAB.get(), (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.wallBlock((WallBlock)DABlocks.MOSSY_HOLYSTONE_BRICK_WALL.get(), (Block)DABlocks.MOSSY_HOLYSTONE_BRICKS.get());
      this.block((Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.stairs((StairBlock)DABlocks.MOSSY_HOLYSTONE_TILE_STAIRS.get(), (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.slab((SlabBlock)DABlocks.MOSSY_HOLYSTONE_TILE_SLAB.get(), (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.wallBlock((WallBlock)DABlocks.MOSSY_HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.MOSSY_HOLYSTONE_TILES.get());
      this.block((Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.stairs((StairBlock)DABlocks.GILDED_HOLYSTONE_BRICK_STAIRS.get(), (Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.slab((SlabBlock)DABlocks.GILDED_HOLYSTONE_BRICK_SLAB.get(), (Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.wallBlock((WallBlock)DABlocks.GILDED_HOLYSTONE_BRICK_WALL.get(), (Block)DABlocks.GILDED_HOLYSTONE_BRICKS.get());
      this.block((Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.stairs((StairBlock)DABlocks.GILDED_HOLYSTONE_TILE_STAIRS.get(), (Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.slab((SlabBlock)DABlocks.GILDED_HOLYSTONE_TILE_SLAB.get(), (Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.wallBlock((WallBlock)DABlocks.GILDED_HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.GILDED_HOLYSTONE_TILES.get());
      this.block((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.stairs((StairBlock)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_STAIRS.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.slab((SlabBlock)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_SLAB.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.wallBlock((WallBlock)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICK_WALL.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_BRICKS.get());
      this.block((Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.stairs((StairBlock)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_STAIRS.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.slab((SlabBlock)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_SLAB.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.wallBlock((WallBlock)DABlocks.BLIGHTMOSS_HOLYSTONE_TILE_WALL.get(), (Block)DABlocks.BLIGHTMOSS_HOLYSTONE_TILES.get());
      this.block((Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.stairs((StairBlock)DABlocks.BIG_HOLYSTONE_BRICKS_STAIRS.get(), (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.slab((SlabBlock)DABlocks.BIG_HOLYSTONE_BRICKS_SLAB.get(), (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.wallBlock((WallBlock)DABlocks.BIG_HOLYSTONE_BRICKS_WALL.get(), (Block)DABlocks.BIG_HOLYSTONE_BRICKS.get());
      this.log((RotatedPillarBlock)DABlocks.HOLYSTONE_PILLAR.get());
      this.block((Block)DABlocks.CHISELED_HOLYSTONE.get());
      this.block((Block)DABlocks.NIMBUS_STONE.get());
      this.block((Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.stairs((StairBlock)DABlocks.NIMBUS_STAIRS.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.slab((SlabBlock)DABlocks.NIMBUS_SLAB.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.wallBlock((WallBlock)DABlocks.NIMBUS_WALL.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.dungeonBlock((Block)DABlocks.LOCKED_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.dungeonBlock((Block)DABlocks.LOCKED_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.dungeonBlock((Block)DABlocks.TRAPPED_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.dungeonBlock((Block)DABlocks.TRAPPED_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.invisibleBlock((Block)DABlocks.BOSS_DOORWAY_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.invisibleBlock((Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.dungeonBlock((Block)DABlocks.TREASURE_DOORWAY_NIMBUS_STONE.get(), (Block)DABlocks.NIMBUS_STONE.get());
      this.dungeonBlock((Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_STONE.get(), (Block)DABlocks.LIGHT_NIMBUS_STONE.get());
      this.log((RotatedPillarBlock)DABlocks.NIMBUS_PILLAR.get());
      this.log((RotatedPillarBlock)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.dungeonBlock((Block)DABlocks.LOCKED_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.dungeonBlock((Block)DABlocks.LOCKED_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.dungeonBlock((Block)DABlocks.TRAPPED_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.dungeonBlock((Block)DABlocks.TRAPPED_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.invisibleBlock((Block)DABlocks.BOSS_DOORWAY_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.invisibleBlock((Block)DABlocks.BOSS_DOORWAY_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.dungeonBlock((Block)DABlocks.TREASURE_DOORWAY_NIMBUS_PILLAR.get(), (Block)DABlocks.NIMBUS_PILLAR.get());
      this.dungeonBlock((Block)DABlocks.TREASURE_DOORWAY_LIGHT_NIMBUS_PILLAR.get(), (Block)DABlocks.LIGHT_NIMBUS_PILLAR.get());
      this.aercloudAll((Block)DABlocks.RAIN_AERCLOUD.get());
      this.aercloudAll((Block)DABlocks.AERSMOG.get());
      this.aercloudAll((Block)DABlocks.CHROMATIC_AERCLOUD.get());
      this.aercloudAll((Block)DABlocks.STERLING_AERCLOUD.get());
      this.combiner((Block)DABlocks.COMBINER.get());
      this.block((Block)DABlocks.AETHER_MOSS_BLOCK.get());
      this.makeLogWalls();
      this.block((Block)DABlocks.SQUALL_BLOCK.get());
   }

   public void aercloudAll(Block block) {
      ResourceLocation texture = this.texture(this.name(block));
      this.aercloud(block, texture, texture, texture, texture, texture, texture, texture, texture, texture, texture, texture, texture, texture);
   }

   public void dungeonBlock(Block block, Block baseBlock) {
      ConfiguredModel dungeonBlock = new ConfiguredModel(this.models().cubeAll(this.name(baseBlock), this.texture(this.name(baseBlock))));
      this.getVariantBuilder(block).partialState().setModels(new ConfiguredModel[]{dungeonBlock});
   }

   public ResourceLocation texture(String name, String suffix) {
      return this.modLoc("block/" + name + suffix);
   }

   public void fence(FenceBlock block, Block baseBlock) {
      this.fenceBlock(block, this.texture(this.name(baseBlock)));
      this.fenceColumn(block, this.name(baseBlock));
   }

   public void translucentBlock(Block block) {
      this.simpleBlock(block, this.cubeAllTranslucent(block));
   }

   public ModelFile cubeAllTranslucent(Block block) {
      return ((BlockModelBuilder)this.models().cubeAll(this.name(block), this.texture(this.name(block))))
         .renderType(ResourceLocation.withDefaultNamespace("translucent"));
   }

   public void fenceColumn(CrossCollisionBlock block, String side) {
      String baseName = this.name(block);
      this.fourWayBlock(block, this.models().fencePost(baseName + "_post", this.texture(side)), this.models().fenceSide(baseName + "_side", this.texture(side)));
   }

   public void fenceGateBlock(FenceGateBlock block, Block baseBlock) {
      this.fenceGateBlockInternal(block, this.name(block), this.texture(this.name(baseBlock)));
   }

   public void log(RotatedPillarBlock block) {
      this.axisBlock(block, this.texture(this.name(block)), this.extend(this.texture(this.name(block)), "_top"));
   }

   public void wood(RotatedPillarBlock block, RotatedPillarBlock baseBlock) {
      this.axisBlock(block, this.texture(this.name(baseBlock)), this.texture(this.name(baseBlock)));
   }

   public void block(Block block) {
      this.simpleBlock(block, this.cubeAll(block));
   }

   public void crossBlock(Block block) {
      this.crossBlock(
         block,
         ((BlockModelBuilder)this.models().cross(this.name(block), this.texture(this.name(block)))).renderType(ResourceLocation.withDefaultNamespace("cutout"))
      );
   }

   public void pottedPlant(Block block, Block flower) {
      ModelFile pot = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(this.name(block), this.mcLoc("block/flower_pot_cross")))
            .texture("plant", this.modLoc("block/" + this.name(flower))))
         .renderType(ResourceLocation.withDefaultNamespace("cutout"));
      this.getVariantBuilder(block).partialState().addModels(new ConfiguredModel[]{new ConfiguredModel(pot)});
   }

   public void pottedPlantFix(Block block, Block flower) {
      ModelFile pot = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(this.name(block), this.mcLoc("block/flower_pot_cross")))
            .texture("plant", this.modLoc("block/" + this.name(flower) + "_pot")))
         .renderType(ResourceLocation.withDefaultNamespace("cutout"));
      this.getVariantBuilder(block).partialState().addModels(new ConfiguredModel[]{new ConfiguredModel(pot)});
   }

   public void saplingBlock(Block block) {
      ModelFile sapling = ((BlockModelBuilder)this.models().cross(this.name(block), this.texture(this.name(block))))
         .renderType(ResourceLocation.withDefaultNamespace("cutout"));
      this.getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(sapling).build(), new Property[]{SaplingBlock.STAGE});
   }

   public void stairs(StairBlock block, Block baseBlock) {
      this.stairsBlock(block, this.texture(this.name(baseBlock)));
   }

   public void slab(SlabBlock block, Block baseBlock) {
      this.slabBlock(block, this.texture(this.name(baseBlock)), this.texture(this.name(baseBlock)));
   }

   public void wallBlock(WallBlock block, Block baseBlock) {
      this.wallBlockInternal(block, this.name(block), this.texture(this.name(baseBlock)));
   }

   public void blockDoubleDrops(Block block) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(this.cubeAll(block)).build(), new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }

   public void combiner(Block block) {
      ModelFile combiner = this.cubeBottomTop(
         this.name(block),
         this.extend(this.texture(this.name(block)), "_side"),
         this.extend(this.texture(this.name(block)), "_bottom"),
         this.extend(this.texture(this.name(block)), "_top")
      );
      this.getVariantBuilder(block).partialState().addModels(new ConfiguredModel[]{new ConfiguredModel(combiner)});
   }

   private void makeLogWalls() {
      ModelFile postBig = this.makeWallPostModel(4, 16, "wooden_post_big");
      ModelFile postShort = this.makeWallPostModel(3, 14, "wooden_post_short");
      ModelFile postTall = this.makeWallPostModel(3, 16, "wooden_post_tall");
      ModelFile side = this.makeWallSideModel(5, 14, "wooden_side", FaceRotation.CLOCKWISE_90, 0, 5);
      ModelFile sideAlt = this.makeWallSideModel(5, 14, "wooden_side_alt", FaceRotation.COUNTERCLOCKWISE_90, 11, 16);
      ModelFile sideTall = this.makeWallSideModel(5, 16, "wooden_side_tall", FaceRotation.CLOCKWISE_90, 0, 5);
      ModelFile sideTallAlt = this.makeWallSideModel(5, 16, "wooden_side_tall_alt", FaceRotation.COUNTERCLOCKWISE_90, 11, 16);
      ModelFile sideShort = this.makeWallSideModel(4, 14, "wooden_side_short", FaceRotation.CLOCKWISE_90, 0, 4);
      ModelFile sideAltShort = this.makeWallSideModel(4, 14, "wooden_side_alt_short", FaceRotation.COUNTERCLOCKWISE_90, 12, 16);
      ModelFile sideTallShort = this.makeWallSideModel(4, 16, "wooden_side_tall_short", FaceRotation.CLOCKWISE_90, 0, 4);
      ModelFile sideTallAltShort = this.makeWallSideModel(4, 16, "wooden_side_tall_alt_short", FaceRotation.COUNTERCLOCKWISE_90, 12, 16);
      this.logWallBlock(
         (WallBlock)DABlocks.ROSEROOT_LOG_WALL.get(),
         (Block)DABlocks.ROSEROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.STRIPPED_ROSEROOT_LOG_WALL.get(),
         (Block)DABlocks.STRIPPED_ROSEROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.CRUDEROOT_LOG_WALL.get(),
         (Block)DABlocks.CRUDEROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.STRIPPED_CRUDEROOT_LOG_WALL.get(),
         (Block)DABlocks.STRIPPED_CRUDEROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.YAGROOT_LOG_WALL.get(),
         (Block)DABlocks.YAGROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.STRIPPED_YAGROOT_LOG_WALL.get(),
         (Block)DABlocks.STRIPPED_YAGROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.CONBERRY_LOG_WALL.get(),
         (Block)DABlocks.CONBERRY_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.STRIPPED_CONBERRY_LOG_WALL.get(),
         (Block)DABlocks.STRIPPED_CONBERRY_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.SUNROOT_LOG_WALL.get(),
         (Block)DABlocks.SUNROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
      this.logWallBlock(
         (WallBlock)DABlocks.STRIPPED_SUNROOT_LOG_WALL.get(),
         (Block)DABlocks.STRIPPED_SUNROOT_LOG.get(),
         "deep_aether",
         true,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
   }

   public void logWallBlock(
      WallBlock block,
      Block baseBlock,
      String modid,
      boolean postUsesTop,
      ModelFile postBig,
      ModelFile postShort,
      ModelFile postTall,
      ModelFile side,
      ModelFile sideAlt,
      ModelFile sideTall,
      ModelFile sideTallAlt,
      ModelFile sideShort,
      ModelFile sideAltShort,
      ModelFile sideTallShort,
      ModelFile sideTallAltShort
   ) {
      this.logWallBlockInternal(
         block,
         this.name(block),
         ResourceLocation.fromNamespaceAndPath(modid, "block/" + this.name(baseBlock)),
         ResourceLocation.fromNamespaceAndPath(modid, "block/" + this.name(baseBlock) + "_top"),
         postUsesTop,
         postBig,
         postShort,
         postTall,
         side,
         sideAlt,
         sideTall,
         sideTallAlt,
         sideShort,
         sideAltShort,
         sideTallShort,
         sideTallAltShort
      );
   }

   private void logWallBlockInternal(
      WallBlock block,
      String baseName,
      ResourceLocation texture,
      ResourceLocation topTexture,
      boolean postUsesTop,
      ModelFile postBig,
      ModelFile postShort,
      ModelFile postTall,
      ModelFile side,
      ModelFile sideAlt,
      ModelFile sideTall,
      ModelFile sideTallAlt,
      ModelFile sideShort,
      ModelFile sideAltShort,
      ModelFile sideTallShort,
      ModelFile sideTallAltShort
   ) {
      this.logWallBlock(
         this.getMultipartBuilder(block),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_post_short")).parent(postShort))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_post_tall")).parent(postTall))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side")).parent(side))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_alt")).parent(sideAlt))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_tall")).parent(sideTall))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_tall_alt"))
                     .parent(sideTallAlt))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture)
      );
      this.logWallBlockWithPost(
         this.getMultipartBuilder(block),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_post")).parent(postBig))
                  .texture("particle", texture))
               .texture("top", postUsesTop ? topTexture.toString() : texture.toString()))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_short")).parent(sideShort))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_alt_short"))
                     .parent(sideAltShort))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_tall_short"))
                     .parent(sideTallShort))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().getBuilder(baseName + "_side_tall_alt_short"))
                     .parent(sideTallAltShort))
                  .texture("particle", texture))
               .texture("top", texture))
            .texture("side", texture)
      );
   }

   public void logWallBlock(
      MultiPartBlockStateBuilder builder, ModelFile postShort, ModelFile postTall, ModelFile side, ModelFile sideAlt, ModelFile sideTall, ModelFile sideTallAlt
   ) {
      ((PartBuilder)((PartBuilder)((PartBuilder)((PartBuilder)builder.part().modelFile(postShort).addModel())
                  .nestedGroup()
                  .condition(WallBlock.UP, new Boolean[]{false})
                  .condition(WallBlock.EAST_WALL, new WallSide[]{WallSide.LOW})
                  .condition(WallBlock.WEST_WALL, new WallSide[]{WallSide.LOW})
                  .end()
                  .end()
                  .part()
                  .modelFile(postTall)
                  .addModel())
               .nestedGroup()
               .condition(WallBlock.UP, new Boolean[]{false})
               .condition(WallBlock.EAST_WALL, new WallSide[]{WallSide.TALL})
               .condition(WallBlock.WEST_WALL, new WallSide[]{WallSide.TALL})
               .end()
               .end()
               .part()
               .modelFile(postShort)
               .rotationY(90)
               .addModel())
            .nestedGroup()
            .condition(WallBlock.UP, new Boolean[]{false})
            .condition(WallBlock.EAST_WALL, new WallSide[]{WallSide.NONE})
            .condition(WallBlock.NORTH_WALL, new WallSide[]{WallSide.LOW})
            .condition(WallBlock.WEST_WALL, new WallSide[]{WallSide.NONE})
            .condition(WallBlock.SOUTH_WALL, new WallSide[]{WallSide.LOW})
            .end()
            .end()
            .part()
            .modelFile(postTall)
            .rotationY(90)
            .addModel())
         .nestedGroup()
         .condition(WallBlock.UP, new Boolean[]{false})
         .condition(WallBlock.EAST_WALL, new WallSide[]{WallSide.NONE})
         .condition(WallBlock.NORTH_WALL, new WallSide[]{WallSide.TALL})
         .condition(WallBlock.WEST_WALL, new WallSide[]{WallSide.NONE})
         .condition(WallBlock.SOUTH_WALL, new WallSide[]{WallSide.TALL})
         .end()
         .end();
      WALL_PROPS.entrySet().stream().filter(e -> ((Direction)e.getKey()).getAxis().isHorizontal()).forEach(e -> {
         this.logWallSidePart(builder, side, sideAlt, (Entry<Direction, Property<WallSide>>)e, WallSide.LOW, false);
         this.logWallSidePart(builder, sideTall, sideTallAlt, (Entry<Direction, Property<WallSide>>)e, WallSide.TALL, false);
      });
   }

   public void logWallBlockWithPost(
      MultiPartBlockStateBuilder builder, ModelFile postBig, ModelFile side, ModelFile sideAlt, ModelFile sideTall, ModelFile sideTallAlt
   ) {
      ((PartBuilder)builder.part().modelFile(postBig).addModel()).condition(WallBlock.UP, new Boolean[]{true}).end();
      WALL_PROPS.entrySet().stream().filter(e -> ((Direction)e.getKey()).getAxis().isHorizontal()).forEach(e -> {
         this.logWallSidePart(builder, side, sideAlt, (Entry<Direction, Property<WallSide>>)e, WallSide.LOW, true);
         this.logWallSidePart(builder, sideTall, sideTallAlt, (Entry<Direction, Property<WallSide>>)e, WallSide.TALL, true);
      });
   }

   private void logWallSidePart(
      MultiPartBlockStateBuilder builder, ModelFile model, ModelFile modelAlt, Entry<Direction, Property<WallSide>> entry, WallSide height, boolean hasPost
   ) {
      int rotation = ((int)entry.getKey().toYRot() + 180) % 360;
      ((PartBuilder)builder.part().modelFile(rotation < 180 ? model : modelAlt).rotationY(rotation).addModel())
         .condition(entry.getValue(), new WallSide[]{height})
         .condition(WallBlock.UP, new Boolean[]{hasPost});
   }
}
