package net.astralya.hexalia.neoforge.datagen;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.RabbageCropBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModBlockStateProvider extends BlockStateProvider {
   public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "hexalia", existingFileHelper);
   }

   protected void registerStatesAndModels() {
      this.flowerWithPotBlock((Block)ModBlocks.SPIRIT_BLOOM.get(), (Block)ModBlocks.POTTED_SPIRIT_BLOOM.get());
      this.flowerWithPotBlock((Block)ModBlocks.DREAMSHROOM.get(), (Block)ModBlocks.POTTED_DREAMSHROOM.get());
      this.flowerWithPotBlock((Block)ModBlocks.GHOST_FERN.get(), (Block)ModBlocks.POTTED_GHOST_FERN.get());
      this.flowerWithPotBlock((Block)ModBlocks.CELESTIAL_BLOOM.get(), (Block)ModBlocks.POTTED_CELESTIAL_BLOOM.get());
      this.flowerWithPotBlock((Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get(), (Block)ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get());
      this.flowerWithPotBlock((Block)ModBlocks.MORPHORA.get(), (Block)ModBlocks.POTTED_MORPHORA.get());
      this.flowerWithPotBlock((Block)ModBlocks.GRIMSHADE.get(), (Block)ModBlocks.POTTED_GRIMSHADE.get());
      this.crossBlockAllStates((Block)ModBlocks.NAUTILITE.get());
      this.flowerWithPotBlock((Block)ModBlocks.WINDSONG.get(), (Block)ModBlocks.POTTED_WINDSONG.get());
      this.flowerWithPotBlock((Block)ModBlocks.ASTRYLIS.get(), (Block)ModBlocks.POTTED_ASTRYLIS.get());
      this.flowerWithPotBlock((Block)ModBlocks.LOURDES.get(), (Block)ModBlocks.POTTED_LOURDES.get());
      this.flowerWithPotBlock((Block)ModBlocks.AEGIFLORA.get(), (Block)ModBlocks.POTTED_AEGIFLORA.get());
      this.flowerWithPotBlock((Block)ModBlocks.WITHERED_AEGIFLORA.get(), (Block)ModBlocks.POTTED_WITHERED_AEGIFLORA.get());
      this.flowerWithPotBlock((Block)ModBlocks.BEGONIA.get(), (Block)ModBlocks.POTTED_BEGONIA.get());
      this.flowerWithPotBlock((Block)ModBlocks.LAVENDER.get(), (Block)ModBlocks.POTTED_LAVENDER.get());
      this.flowerWithPotBlock((Block)ModBlocks.DAHLIA.get(), (Block)ModBlocks.POTTED_DAHLIA.get());
      this.flowerWithPotBlock((Block)ModBlocks.NIGHTSHADE_BUSH.get(), (Block)ModBlocks.POTTED_NIGHTSHADE_BUSH.get());
      this.blockWithItem((Block)ModBlocks.INFUSED_DIRT.get());
      this.simpleCrossBlock((Block)ModBlocks.SIREN_KELP.get());
      this.simpleCrossBlock((Block)ModBlocks.WITCHWEED.get());
      this.cropBlock((Block)ModBlocks.RABBAGE_CROP.get(), RabbageCropBlock.AGE);
      this.blockWithItem((Block)ModBlocks.SALT_BLOCK.get());
      this.blockWithItem((Block)ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get());
      this.woodSet(
         "cottonwood",
         (Block)ModBlocks.COTTONWOOD_LOG.get(),
         (Block)ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
         (Block)ModBlocks.COTTONWOOD_WOOD.get(),
         (Block)ModBlocks.STRIPPED_COTTONWOOD_WOOD.get(),
         (Block)ModBlocks.COTTONWOOD_PLANKS.get(),
         (Block)ModBlocks.COTTONWOOD_STAIRS.get(),
         (Block)ModBlocks.COTTONWOOD_SLAB.get(),
         (Block)ModBlocks.COTTONWOOD_BUTTON.get(),
         (Block)ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(),
         (Block)ModBlocks.COTTONWOOD_FENCE.get(),
         (Block)ModBlocks.COTTONWOOD_FENCE_GATE.get(),
         (Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(),
         (Block)ModBlocks.COTTONWOOD_DOOR.get()
      );
      this.woodSet(
         "willow",
         (Block)ModBlocks.WILLOW_LOG.get(),
         (Block)ModBlocks.STRIPPED_WILLOW_LOG.get(),
         (Block)ModBlocks.WILLOW_WOOD.get(),
         (Block)ModBlocks.STRIPPED_WILLOW_WOOD.get(),
         (Block)ModBlocks.WILLOW_PLANKS.get(),
         (Block)ModBlocks.WILLOW_STAIRS.get(),
         (Block)ModBlocks.WILLOW_SLAB.get(),
         (Block)ModBlocks.WILLOW_BUTTON.get(),
         (Block)ModBlocks.WILLOW_PRESSURE_PLATE.get(),
         (Block)ModBlocks.WILLOW_FENCE.get(),
         (Block)ModBlocks.WILLOW_FENCE_GATE.get(),
         (Block)ModBlocks.WILLOW_TRAPDOOR.get(),
         (Block)ModBlocks.WILLOW_DOOR.get()
      );
      this.leavesBlock((Block)ModBlocks.COTTONWOOD_LEAVES.get());
      this.leavesBlock((Block)ModBlocks.WILLOW_LEAVES.get());
      this.flowerWithPotBlock((Block)ModBlocks.COTTONWOOD_SAPLING.get(), (Block)ModBlocks.POTTED_COTTONWOOD_SAPLING.get());
      this.flowerWithPotBlock((Block)ModBlocks.WILLOW_SAPLING.get(), (Block)ModBlocks.POTTED_WILLOW_SAPLING.get());
      this.simpleCrossBlock((Block)ModBlocks.COTTONWOOD_CATKIN.get());
   }

   private void flowerWithPotBlock(Block flower, Block flowerPot) {
      String flowerName = this.name(flower);
      String potName = this.name(flowerPot);
      ModelFile flowerModel = ((BlockModelBuilder)this.models().cross(flowerName, this.modLoc("block/" + flowerName))).renderType("cutout");
      this.getVariantBuilder(flower).forAllStates(state -> ConfiguredModel.builder().modelFile(flowerModel).build());
      this.simpleBlock(
         flowerPot,
         ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(potName, this.mcLoc("block/flower_pot_cross")))
               .texture("plant", this.modLoc("block/" + flowerName)))
            .renderType("cutout")
      );
   }

   private String name(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block).getPath();
   }

   private void simpleCrossBlock(Block block) {
      String blockName = this.name(block);
      this.simpleBlock(block, ((BlockModelBuilder)this.models().cross(blockName, this.modLoc("block/" + blockName))).renderType("cutout"));
   }

   private void crossBlockAllStates(Block block) {
      String blockName = this.name(block);
      ModelFile model = ((BlockModelBuilder)this.models().cross(blockName, this.modLoc("block/" + blockName))).renderType("cutout");
      this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
   }

   private void blockWithItem(Block block) {
      this.simpleBlockWithItem(block, this.cubeAll(block));
   }

   private void leavesBlock(Block block) {
      String blockName = this.name(block);
      this.simpleBlockWithItem(
         block, ((BlockModelBuilder)this.models().withExistingParent(blockName, this.mcLoc("block/leaves"))).texture("all", this.modLoc("block/" + blockName))
      );
   }

   private void woodSet(
      String name,
      Block log,
      Block strippedLog,
      Block wood,
      Block strippedWood,
      Block planks,
      Block stairs,
      Block slab,
      Block button,
      Block pressurePlate,
      Block fence,
      Block fenceGate,
      Block trapdoor,
      Block door
   ) {
      this.logBlock((RotatedPillarBlock)log);
      this.logBlock((RotatedPillarBlock)strippedLog);
      this.axisBlock((RotatedPillarBlock)wood, this.modLoc("block/" + name + "_log"), this.modLoc("block/" + name + "_log"));
      this.axisBlock((RotatedPillarBlock)strippedWood, this.modLoc("block/stripped_" + name + "_log"), this.modLoc("block/stripped_" + name + "_log"));
      this.simpleBlockWithItem(planks, this.cubeAll(planks));
      this.stairsBlock((StairBlock)stairs, this.blockTexture(planks));
      this.slabBlock((SlabBlock)slab, this.blockTexture(planks), this.blockTexture(planks));
      this.buttonBlock((ButtonBlock)button, this.blockTexture(planks));
      this.pressurePlateBlock((PressurePlateBlock)pressurePlate, this.blockTexture(planks));
      this.fenceBlock((FenceBlock)fence, this.blockTexture(planks));
      this.fenceGateBlock((FenceGateBlock)fenceGate, this.blockTexture(planks));
      this.trapdoorBlockWithRenderType((TrapDoorBlock)trapdoor, this.modLoc("block/" + name + "_trapdoor"), true, "cutout");
      this.doorBlockWithRenderType((DoorBlock)door, this.modLoc("block/" + name + "_door_bottom"), this.modLoc("block/" + name + "_door_top"), "cutout");
      this.itemModels().withExistingParent(this.name(log), this.modLoc("block/" + this.name(log)));
      this.itemModels().withExistingParent(this.name(strippedLog), this.modLoc("block/" + this.name(strippedLog)));
      this.itemModels().withExistingParent(this.name(wood), this.modLoc("block/" + this.name(wood)));
      this.itemModels().withExistingParent(this.name(strippedWood), this.modLoc("block/" + this.name(strippedWood)));
      ((ItemModelBuilder)this.itemModels().withExistingParent(name + "_button", this.mcLoc("block/button_inventory")))
         .texture("texture", this.blockTexture(planks));
      ((ItemModelBuilder)this.itemModels().withExistingParent(name + "_fence", this.mcLoc("block/fence_inventory")))
         .texture("texture", this.blockTexture(planks));
   }

   private void cropBlock(Block block, IntegerProperty age) {
      this.getVariantBuilder(block).forAllStates(state -> {
         int stage = (Integer)state.getValue(age);
         String name = this.name(block) + "_stage" + stage;
         return ConfiguredModel.builder().modelFile(((BlockModelBuilder)this.models().cross(name, this.modLoc("block/" + name))).renderType("cutout")).build();
      });
   }
}
