package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.mixin.BlockBehaviourAccessor;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class BetterMineshaftPiece extends StructurePiece {
   public BetterMineshaftConfiguration config;
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();

   public BetterMineshaftPiece(StructurePieceType structurePieceType, int chainLength, BetterMineshaftConfiguration config, BoundingBox boundingBox) {
      super(structurePieceType, chainLength, boundingBox);
      this.config = config;
   }

   public BetterMineshaftPiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
      super(structurePieceType, compoundTag);
      this.config = new BetterMineshaftConfiguration(
         compoundTag.getFloat("replacementRate"),
         BetterMineshaftConfiguration.LegVariant.byId(compoundTag.getInt("legVariantIndex")),
         new BetterMineshaftConfiguration.MineshaftDecorationChances(
            compoundTag.getFloat("vineChance"),
            compoundTag.getFloat("snowChance"),
            compoundTag.getFloat("cactusChance"),
            compoundTag.getFloat("deadBushChance"),
            compoundTag.getFloat("mushroomChance"),
            compoundTag.getFloat("gravelPileChance"),
            compoundTag.getBoolean("lushDecorations"),
            compoundTag.getBoolean("dripstoneDecorations")
         ),
         new BetterMineshaftConfiguration.MineshaftBlockStates(
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("mainBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("supportBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("slabBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("gravelBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("stoneWallBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("stoneSlabBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("trapdoorBlockId")),
            (BlockState)Block.BLOCK_STATE_REGISTRY.byId(compoundTag.getInt("smallLegBlockId"))
         ),
         new BetterMineshaftConfiguration.MineshaftBlockstateRandomizers(
            new BlockStateRandomizer(compoundTag.getCompound("mainSelector")),
            new BlockStateRandomizer(compoundTag.getCompound("floorSelector")),
            new BlockStateRandomizer(compoundTag.getCompound("brickSelector")),
            new BlockStateRandomizer(compoundTag.getCompound("legSelector"))
         )
      );
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      compoundTag.putFloat("replacementRate", this.config.replacementRate);
      compoundTag.putInt("legVariantIndex", this.config.legVariant.ordinal());
      compoundTag.putFloat("vineChance", this.config.decorationChances.vineChance);
      compoundTag.putFloat("snowChance", this.config.decorationChances.snowChance);
      compoundTag.putFloat("cactusChance", this.config.decorationChances.cactusChance);
      compoundTag.putFloat("deadBushChance", this.config.decorationChances.deadBushChance);
      compoundTag.putFloat("mushroomChance", this.config.decorationChances.mushroomChance);
      compoundTag.putFloat("gravelPileChance", this.config.decorationChances.gravelPileChance);
      compoundTag.putBoolean("lushDecorations", this.config.decorationChances.lushDecorations);
      compoundTag.putBoolean("dripstoneDecorations", this.config.decorationChances.dripstoneDecorations);
      compoundTag.putInt("mainBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.mainBlockState));
      compoundTag.putInt("supportBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.supportBlockState));
      compoundTag.putInt("slabBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.slabBlockState));
      compoundTag.putInt("gravelBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.gravelBlockState));
      compoundTag.putInt("stoneWallBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.stoneWallBlockState));
      compoundTag.putInt("stoneSlabBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.stoneSlabBlockState));
      compoundTag.putInt("trapdoorBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.trapdoorBlockState));
      compoundTag.putInt("smallLegBlockId", Block.BLOCK_STATE_REGISTRY.getId(this.config.blockStates.smallLegBlockState));
      compoundTag.put("mainSelector", this.config.blockStateRandomizers.mainRandomizer.saveTag());
      compoundTag.put("floorSelector", this.config.blockStateRandomizers.floorRandomizer.saveTag());
      compoundTag.put("brickSelector", this.config.blockStateRandomizers.brickRandomizer.saveTag());
      compoundTag.put("legSelector", this.config.blockStateRandomizers.legRandomizer.saveTag());
   }

   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
   }

   protected boolean addBarrel(WorldGenLevel world, BoundingBox boundingBox, RandomSource randomSource, BlockPos pos, ResourceKey<LootTable> lootTableId) {
      if (boundingBox.isInside(pos) && world.getBlockState(pos).getBlock() != Blocks.BARREL) {
         world.setBlock(pos, (BlockState)Blocks.BARREL.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP), 2);
         if (world.getBlockEntity(pos) instanceof BarrelBlockEntity barrelBlockEntity) {
            barrelBlockEntity.setLootTable(lootTableId, randomSource.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean addBarrel(WorldGenLevel world, BoundingBox boundingBox, RandomSource randomSource, int x, int y, int z, ResourceKey<LootTable> lootTableId) {
      return this.addBarrel(world, boundingBox, randomSource, this.getWorldPos(x, y, z), lootTableId);
   }

   protected void addVines(
      WorldGenLevel world,
      BoundingBox boundingBox,
      Direction facing,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ
   ) {
      MutableBlockPos mutable = new MutableBlockPos();

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               mutable.set(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z)).move(facing);
               BlockState nextBlock = this.getBlock(world, x + facing.getStepX(), y + facing.getStepY(), z + facing.getStepZ(), boundingBox);
               if (this.getBlock(world, x, y, z, boundingBox).isAir()
                  && Block.isFaceFull(nextBlock.getCollisionShape(world, mutable), facing.getOpposite())
                  && nextBlock.getBlock().defaultBlockState() != Blocks.LADDER.defaultBlockState()
                  && randomSource.nextFloat() < chance) {
                  this.placeBlock(
                     world,
                     (BlockState)Blocks.VINE
                        .defaultBlockState()
                        .setValue(VineBlock.getPropertyForFace(facing.getAxis() == Axis.X ? facing : facing.getOpposite()), true),
                     x,
                     y,
                     z,
                     boundingBox
                  );
               }
            }
         }
      }
   }

   protected void addVines(
      WorldGenLevel world, BoundingBox boundingBox, RandomSource randomSource, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ
   ) {
      this.addVines(world, boundingBox, Direction.EAST, randomSource, chance, minX, minY, minZ, maxX, maxY, maxZ);
      this.addVines(world, boundingBox, Direction.WEST, randomSource, chance, minX, minY, minZ, maxX, maxY, maxZ);
      this.addVines(world, boundingBox, Direction.NORTH, randomSource, chance, minX, minY, minZ, maxX, maxY, maxZ);
      this.addVines(world, boundingBox, Direction.SOUTH, randomSource, chance, minX, minY, minZ, maxX, maxY, maxZ);
   }

   protected void addBiomeDecorations(
      WorldGenLevel world, BoundingBox box, RandomSource randomSource, int minX, int minY, int minZ, int maxX, int maxY, int maxZ
   ) {
      Registry<ConfiguredFeature<?, ?>> registry = (Registry<ConfiguredFeature<?, ?>>)world.registryAccess().registry(Registries.CONFIGURED_FEATURE).get();

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockPos blockPos = this.getWorldPos(x, y, z);
               BlockState state = this.getBlock(world, x, y, z, box);
               BlockState stateBelow = this.getBlock(world, x, y - 1, z, box);
               if (this.config.decorationChances.snowChance > 0.0F
                  && randomSource.nextFloat() < this.config.decorationChances.snowChance
                  && state.isAir()
                  && ((BlockBehaviourAccessor)Blocks.SNOW).callCanSurvive(AIR, world, blockPos)) {
                  this.placeBlock(
                     world, (BlockState)Blocks.SNOW.defaultBlockState().setValue(BlockStateProperties.LAYERS, randomSource.nextInt(2) + 1), x, y, z, box
                  );
               }

               if (this.config.decorationChances.lushDecorations) {
                  if (box.isInside(blockPos) && randomSource.nextFloat() < 0.005F) {
                     ((ConfiguredFeature)registry.get(CaveFeatures.MOSS_PATCH))
                        .place(world, world.getLevel().getChunkSource().getGenerator(), randomSource, blockPos);
                  }

                  if (box.isInside(blockPos) && randomSource.nextFloat() < 0.005F) {
                     ((ConfiguredFeature)registry.get(CaveFeatures.LUSH_CAVES_CLAY))
                        .place(world, world.getLevel().getChunkSource().getGenerator(), randomSource, blockPos);
                  }

                  if (box.isInside(blockPos) && randomSource.nextFloat() < 0.005F) {
                     ((ConfiguredFeature)registry.get(CaveFeatures.MOSS_PATCH_CEILING))
                        .place(world, world.getLevel().getChunkSource().getGenerator(), randomSource, blockPos);
                  }

                  if (stateBelow.is(this.config.blockStates.mainBlockState.getBlock())
                     && state.isAir()
                     && stateBelow.isFaceSturdy(world, blockPos.below(), Direction.UP)) {
                     this.placeBlock(world, Blocks.MOSS_CARPET.defaultBlockState(), x, y, z, box);
                  }
               }

               if (this.config.decorationChances.dripstoneDecorations) {
                  if (box.isInside(blockPos) && randomSource.nextFloat() < 0.02F) {
                     ((ConfiguredFeature)registry.get(CaveFeatures.DRIPSTONE_CLUSTER))
                        .place(world, world.getLevel().getChunkSource().getGenerator(), randomSource, blockPos);
                  }

                  if (box.isInside(blockPos) && randomSource.nextFloat() < 0.02F) {
                     ((ConfiguredFeature)registry.get(CaveFeatures.POINTED_DRIPSTONE))
                        .place(world, world.getLevel().getChunkSource().getGenerator(), randomSource, blockPos);
                  }
               }

               if (this.config.decorationChances.cactusChance > 0.0F
                  && randomSource.nextFloat() < this.config.decorationChances.cactusChance
                  && state.isAir()
                  && ((BlockBehaviourAccessor)Blocks.CACTUS).callCanSurvive(AIR, world, blockPos)) {
                  this.placeBlock(world, (BlockState)Blocks.CACTUS.defaultBlockState().setValue(BlockStateProperties.AGE_15, 0), x, y, z, box);
                  if (randomSource.nextFloat() < 0.5F && this.getBlock(world, x, y + 1, z, box).is(Blocks.AIR)) {
                     this.placeBlock(world, (BlockState)Blocks.CACTUS.defaultBlockState().setValue(BlockStateProperties.AGE_15, 0), x, y + 1, z, box);
                  }
               }

               if (this.config.decorationChances.deadBushChance > 0.0F
                  && randomSource.nextFloat() < this.config.decorationChances.deadBushChance
                  && state.isAir()
                  && (
                     stateBelow.is(Blocks.SAND)
                        || stateBelow.is(Blocks.RED_SAND)
                        || stateBelow.is(Blocks.TERRACOTTA)
                        || stateBelow.is(Blocks.WHITE_TERRACOTTA)
                        || stateBelow.is(Blocks.ORANGE_TERRACOTTA)
                        || stateBelow.is(Blocks.YELLOW_TERRACOTTA)
                        || stateBelow.is(Blocks.BROWN_TERRACOTTA)
                        || stateBelow.is(Blocks.DIRT)
                  )) {
                  this.placeBlock(world, Blocks.DEAD_BUSH.defaultBlockState(), x, y, z, box);
               }

               if (this.config.decorationChances.mushroomChance > 0.0F
                  && state.isAir()
                  && ((BlockBehaviourAccessor)Blocks.RED_MUSHROOM).callCanSurvive(AIR, world, blockPos)) {
                  float r = randomSource.nextFloat();
                  if (r < this.config.decorationChances.mushroomChance / 2.0F) {
                     this.placeBlock(world, Blocks.RED_MUSHROOM.defaultBlockState(), x, y, z, box);
                  } else if (r < this.config.decorationChances.mushroomChance) {
                     this.placeBlock(world, Blocks.BROWN_MUSHROOM.defaultBlockState(), x, y, z, box);
                  }
               }
            }
         }
      }
   }

   protected void generateLeg(WorldGenLevel world, RandomSource randomSource, BoundingBox box, int x, int z, BlockStateRandomizer selector) {
      MutableBlockPos mutable = new MutableBlockPos(x, -1, z);

      for (BlockState state = this.getBlock(world, mutable.getX(), mutable.getY(), mutable.getZ(), box);
         this.getWorldY(mutable.getY()) > world.getMinBuildHeight() + 1 && this.isReplaceableByStructures(state);
         state = this.getBlock(world, mutable.getX(), mutable.getY(), mutable.getZ(), box)
      ) {
         this.placeBlock(world, selector.get(randomSource), x, mutable.getY(), z, box);
         mutable.move(Direction.DOWN);
      }
   }

   protected boolean generateLegOrChain(WorldGenLevel world, RandomSource randomSource, BoundingBox box, int x, int z, BlockStateRandomizer selector) {
      MutableBlockPos mutable = new MutableBlockPos(x, -1, z);
      BlockState state = this.getBlock(world, mutable.getX(), mutable.getY(), mutable.getZ(), box);

      boolean lavaBelow;
      for (lavaBelow = false;
         this.getWorldY(mutable.getY()) > world.getMinBuildHeight() + 1 && this.isReplaceableByStructures(state);
         state = this.getBlock(world, mutable.getX(), mutable.getY(), mutable.getZ(), box)
      ) {
         if (state.is(Blocks.LAVA)) {
            lavaBelow = true;
            break;
         }

         mutable.move(Direction.DOWN);
      }

      if (!lavaBelow) {
         this.generateLeg(world, randomSource, box, x, z, selector);
         return true;
      } else {
         mutable = this.getWorldPos(x, 0, z);
         if (!this.boundingBox.isInside(mutable)) {
            return false;
         } else {
            int realChainY = this.getWorldY(0);
            int length = 1;

            for (boolean canGenerateChain = true; canGenerateChain; length++) {
               if (canGenerateChain) {
                  mutable.setY(realChainY + length);
                  BlockState currBlock = world.getBlockState(mutable);
                  boolean currBlockCanBeReplaced = this.isReplaceableByStructures(currBlock);
                  if (!currBlockCanBeReplaced && this.canHangChainBelow(world, mutable, currBlock)) {
                     world.setBlock(mutable.setY(realChainY + 1), this.config.blockStates.supportBlockState, 2);
                     fillColumnBetween(world, Blocks.CHAIN.defaultBlockState(), mutable, realChainY + 2, realChainY + length);
                     return false;
                  }

                  canGenerateChain = length <= 50 && currBlockCanBeReplaced && mutable.getY() < world.getMaxBuildHeight() - 1;
               }
            }

            return false;
         }
      }
   }

   protected void generatePillarDownOrChainUp(
      WorldGenLevel world, RandomSource randomSource, BoundingBox boundingBox, int x, int z, int pillarStartY, int chainStartY, BlockState chainBlock
   ) {
      MutableBlockPos mutable = this.getWorldPos(x, pillarStartY, z);
      if (boundingBox.isInside(mutable)) {
         int realPillarY = this.getWorldY(pillarStartY);
         int realChainY = this.getWorldY(chainStartY);
         int length = 1;
         boolean canGenerateLeg = true;

         for (boolean canGenerateChain = true; canGenerateLeg || canGenerateChain; length++) {
            if (canGenerateLeg) {
               mutable.setY(realPillarY - length);
               BlockState currBlock = world.getBlockState(mutable);
               boolean currBlockCanBeReplaced = this.isReplaceableByStructures(currBlock) && !currBlock.is(Blocks.LAVA);
               if (!currBlockCanBeReplaced && this.canPlaceColumnOnTopOf(currBlock)) {
                  fillColumnBetween(world, this.config.blockStates.smallLegBlockState, mutable, realPillarY - length + 1, realPillarY);
                  return;
               }

               canGenerateLeg = length <= 20 && currBlockCanBeReplaced && mutable.getY() > world.getMinBuildHeight() + 1;
            }

            if (canGenerateChain) {
               mutable.setY(realChainY + length);
               BlockState currBlock = world.getBlockState(mutable);
               boolean currBlockCanBeReplaced = this.isReplaceableByStructures(currBlock);
               if (!currBlockCanBeReplaced && this.canHangChainBelow(world, mutable, currBlock)) {
                  world.setBlock(mutable.setY(realChainY + 1), chainBlock, 2);
                  fillColumnBetween(world, Blocks.CHAIN.defaultBlockState(), mutable, realChainY + 2, realChainY + length);
                  return;
               }

               canGenerateChain = length <= 50 && currBlockCanBeReplaced && mutable.getY() < world.getMaxBuildHeight() - 1;
            }
         }
      }
   }

   protected void generatePillarDownOrChainUp(WorldGenLevel world, RandomSource randomSource, BoundingBox boundingBox, int x, int y, int z) {
      this.generatePillarDownOrChainUp(world, randomSource, boundingBox, x, z, y, y, this.config.blockStates.supportBlockState);
   }

   private boolean canPlaceColumnOnTopOf(BlockState blockState) {
      return !blockState.is(Blocks.RAIL) && !blockState.is(Blocks.LAVA);
   }

   private boolean canHangChainBelow(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      return Block.canSupportCenter(levelReader, blockPos, Direction.DOWN) && !(blockState.getBlock() instanceof FallingBlock);
   }

   protected boolean isReplaceableByStructures(BlockState blockState) {
      return blockState.isAir()
         || blockState.liquid()
         || blockState.is(Blocks.GLOW_LICHEN)
         || blockState.is(Blocks.SEAGRASS)
         || blockState.is(Blocks.TALL_SEAGRASS)
         || blockState.is(Blocks.POINTED_DRIPSTONE)
         || blockState.is(Blocks.CAVE_VINES)
         || blockState.is(Blocks.CAVE_VINES_PLANT)
         || blockState.is(Blocks.MOSS_CARPET)
         || blockState.is(Blocks.SNOW);
   }

   protected void fill(WorldGenLevel world, BoundingBox boundingBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (this.getBlock(world, x, y, z, boundingBox) != Blocks.CHAIN.defaultBlockState() && blockState.canSurvive(world, this.getWorldPos(x, y, z))) {
                  this.placeBlock(world, blockState, x, y, z, boundingBox);
               }
            }
         }
      }
   }

   protected void fill(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockStateRandomizer selector
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (this.getBlock(world, x, y, z, boundingBox) != Blocks.CHAIN.defaultBlockState()) {
                  BlockState blockState = selector.get(randomSource);
                  if (blockState.canSurvive(world, this.getWorldPos(x, y, z))) {
                     this.placeBlock(world, blockState, x, y, z, boundingBox);
                  }
               }
            }
         }
      }
   }

   protected void replaceAirOrChains(
      WorldGenLevel world, BoundingBox boundingBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockState
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockState currState = this.getBlockAtFixed(world, x, y, z, boundingBox);
               if (currState != null
                  && (currState.isAir() || currState == Blocks.CHAIN.defaultBlockState())
                  && blockState.canSurvive(world, this.getWorldPos(x, y, z))) {
                  this.placeBlock(world, blockState, x, y, z, boundingBox);
               }
            }
         }
      }
   }

   protected void replaceAirOrChains(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockStateRandomizer selector
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockState currState = this.getBlockAtFixed(world, x, y, z, boundingBox);
               if (currState != null && (currState.isAir() || currState == Blocks.CHAIN.defaultBlockState())) {
                  BlockState blockState = selector.get(randomSource);
                  if (blockState.canSurvive(world, this.getWorldPos(x, y, z))) {
                     this.placeBlock(world, blockState, x, y, z, boundingBox);
                  }
               }
            }
         }
      }
   }

   protected static void fillColumnBetween(WorldGenLevel worldGenLevel, BlockState blockState, MutableBlockPos mutableBlockPos, int minY, int maxY) {
      for (int y = minY; y < maxY; y++) {
         worldGenLevel.setBlock(mutableBlockPos.setY(y), blockState, 2);
      }
   }

   protected static void fillColumnBetween(
      WorldGenLevel worldGenLevel, RandomSource randomSource, BlockStateRandomizer selector, MutableBlockPos mutableBlockPos, int minY, int maxY
   ) {
      for (int y = minY; y < maxY; y++) {
         worldGenLevel.setBlock(mutableBlockPos.setY(y), selector.get(randomSource), 2);
      }
   }

   protected void chanceFill(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockState blockState
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (randomSource.nextFloat() < chance && blockState.canSurvive(world, this.getWorldPos(x, y, z))) {
                  this.placeBlock(world, blockState, x, y, z, boundingBox);
               }
            }
         }
      }
   }

   protected void chanceFill(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockStateRandomizer selector
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (randomSource.nextFloat() < chance) {
                  this.placeBlock(world, selector.get(randomSource), x, y, z, boundingBox);
               }
            }
         }
      }
   }

   protected void chanceReplaceAir(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockState blockState
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (randomSource.nextFloat() < chance) {
                  BlockState currState = this.getBlockAtFixed(world, x, y, z, boundingBox);
                  if (currState != null && currState.isAir()) {
                     this.placeBlock(world, blockState, x, y, z, boundingBox);
                  }
               }
            }
         }
      }
   }

   protected void chanceReplaceNonAir(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockState blockState
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockState currState = this.getBlockAtFixed(world, x, y, z, boundingBox);
               if (currState != null
                  && currState != Blocks.CHAIN.defaultBlockState()
                  && (currState.liquid() || randomSource.nextFloat() < chance && !currState.isAir())) {
                  this.placeBlock(world, blockState, x, y, z, boundingBox);
               }
            }
         }
      }
   }

   protected void chanceReplaceNonAir(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockStateRandomizer selector
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockState currState = this.getBlockAtFixed(world, x, y, z, boundingBox);
               if (currState != null
                  && currState != Blocks.CHAIN.defaultBlockState()
                  && (currState.liquid() || randomSource.nextFloat() < chance && !currState.isAir())) {
                  BlockState blockState = selector.get(randomSource);
                  if (currState.liquid()) {
                     for (int numAttempts = 0;
                        (blockState == Blocks.AIR.defaultBlockState() || blockState == Blocks.CAVE_AIR.defaultBlockState()) && numAttempts < 10;
                        numAttempts++
                     ) {
                        blockState = selector.get(randomSource);
                     }
                  }

                  this.placeBlock(world, blockState, x, y, z, boundingBox);
               }
            }
         }
      }
   }

   protected void chanceReplaceSolid(
      WorldGenLevel world,
      BoundingBox boundingBox,
      RandomSource randomSource,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      BlockState blockState
   ) {
      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (randomSource.nextFloat() < chance) {
                  BlockState currState = this.getBlockAtFixed(world, x, y, z, boundingBox);
                  if (currState != null && currState != Blocks.CHAIN.defaultBlockState() && currState.isSolid()) {
                     this.placeBlock(world, blockState, x, y, z, boundingBox);
                  }
               }
            }
         }
      }
   }

   protected void chanceAddBlock(WorldGenLevel world, RandomSource randomSource, float chance, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
      if (randomSource.nextFloat() < chance && block.canSurvive(world, this.getWorldPos(x, y, z))) {
         this.placeBlock(world, block, x, y, z, boundingBox);
      }
   }

   protected void chanceReplaceAir(WorldGenLevel world, RandomSource randomSource, float chance, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
      if (randomSource.nextFloat() < chance && block.canSurvive(world, this.getWorldPos(x, y, z)) && block.is(Blocks.AIR) || block.is(Blocks.CAVE_AIR)) {
         this.placeBlock(world, block, x, y, z, boundingBox);
      }
   }

   protected BlockState getBlockAtFixed(BlockGetter blockGetter, int x, int y, int z, BoundingBox boundingBox) {
      BlockPos blockPos = this.getWorldPos(x, y, z);
      return !boundingBox.isInside(blockPos) ? null : blockGetter.getBlockState(blockPos);
   }
}
