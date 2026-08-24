package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.mixin.BlockBehaviourAccessor;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class SmallTunnel extends BetterMineshaftPiece {
   private final List<Integer> supports = new ArrayList<>();
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 8;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 7;

   public SmallTunnel(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.SMALL_TUNNEL, compoundTag);
      ListTag listTag1 = compoundTag.getList("Supports", 3);

      for (int i = 0; i < listTag1.size(); i++) {
         this.supports.add(listTag1.getInt(i));
      }
   }

   public SmallTunnel(int chunkPieceLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.SMALL_TUNNEL, chunkPieceLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
      ListTag listTag1 = new ListTag();
      this.supports.forEach(z -> listTag1.add(IntTag.valueOf(z)));
      compoundTag.put("Supports", listTag1);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 5, 5, 8, direction);
      StructurePiece intersectingPiece = structurePieceAccessor.findCollisionPiece(blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      Direction direction = this.getOrientation();
      if (direction != null) {
         switch (direction) {
            case NORTH:
            default:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX(),
                  this.boundingBox.minY(),
                  this.boundingBox.minZ() - 1,
                  direction,
                  this.genDepth
               );
               break;
            case SOUTH:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY(),
                  this.boundingBox.maxZ() + 1,
                  direction,
                  this.genDepth
               );
               break;
            case WEST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.minX() - 1,
                  this.boundingBox.minY(),
                  this.boundingBox.maxZ(),
                  direction,
                  this.genDepth
               );
               break;
            case EAST:
               BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.boundingBox.maxX() + 1,
                  this.boundingBox.minY(),
                  this.boundingBox.minZ(),
                  direction,
                  this.genDepth
               );
         }

         this.buildSupports(randomSource);
      }
   }

   public void postProcess(
      WorldGenLevel world,
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      RandomSource randomSource,
      BoundingBox box,
      ChunkPos chunkPos,
      BlockPos blockPos
   ) {
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 1, 0, 4, 4, 7, this.config.blockStateRandomizers.mainRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 4, 0, 7, this.config.blockStateRandomizers.floorRandomizer);
      this.fill(world, box, 1, 1, 0, 3, 3, 7, AIR);
      this.replaceAirOrChains(world, box, 1, 0, 0, 3, 0, 7, this.config.blockStates.mainBlockState);
      this.generateSupports(world, box, randomSource);
      this.generateRails(world, box, randomSource);
      this.generateChestCarts(world, box, randomSource);
      this.generateTntCarts(world, box, randomSource);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 3, 4, 6);
      this.addBiomeDecorations(world, box, randomSource, 1, 0, 0, 3, 3, 6);
      this.generateTorches(world, box, randomSource);
      this.generatePillarsOrChains(world, box, randomSource);
      this.replaceExistingChainsWithChainBlock(world, box);
   }

   private void generateChestCarts(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      for (int z = 0; z <= 7; z++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftChestMinecartSpawnRate) {
            BlockPos blockPos = this.getWorldPos(2, 1, z);
            if (box.isInside(blockPos) && !world.getBlockState(blockPos.below()).isAir()) {
               MinecartChest chestMinecartEntity = new MinecartChest(world.getLevel(), blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
               chestMinecartEntity.setLootTable(BuiltInLootTables.ABANDONED_MINESHAFT, randomSource.nextLong());
               world.addFreshEntity(chestMinecartEntity);
            }
         }
      }
   }

   private void generateSupports(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      float cobwebChance = (float)BetterMineshaftsCommon.CONFIG.spawnRates.cobwebSpawnRate;
      BlockState supportBlock = this.config.blockStates.supportBlockState;
      if (supportBlock.getProperties().contains(BlockStateProperties.EAST_WALL) && supportBlock.getProperties().contains(BlockStateProperties.WEST_WALL)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.EAST_WALL, WallSide.TALL))
            .setValue(BlockStateProperties.WEST_WALL, WallSide.TALL);
      } else if (supportBlock.getProperties().contains(BlockStateProperties.EAST) && supportBlock.getProperties().contains(BlockStateProperties.WEST)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.EAST, true)).setValue(BlockStateProperties.WEST, true);
      }

      for (int z : this.supports) {
         int numCovered = 0;

         for (int x = 1; x <= 3; x++) {
            BlockState blockState = this.getBlock(world, x, 4, z, box);
            if (!blockState.isAir() && !blockState.is(Blocks.CHAIN)) {
               numCovered++;
            }
         }

         if (numCovered >= 2) {
            this.fill(world, box, 1, 1, z, 1, 2, z, this.config.blockStates.supportBlockState);
            this.fill(world, box, 3, 1, z, 3, 2, z, this.config.blockStates.supportBlockState);
            this.fill(world, box, 1, 3, z, 3, 3, z, this.config.blockStates.mainBlockState);
            this.chanceReplaceNonAir(world, box, randomSource, 0.25F, 1, 3, z, 3, 3, z, supportBlock);
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 1, 3, z - 1, 1, 3, z + 1, Blocks.COBWEB.defaultBlockState());
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 3, 3, z - 1, 3, 3, z + 1, Blocks.COBWEB.defaultBlockState());
         }
      }
   }

   private void generateRails(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      MutableBlockPos mutable = new MutableBlockPos();

      for (int z = 0; z <= 7; z++) {
         mutable.set(this.getWorldX(2, z), this.getWorldY(1), this.getWorldZ(2, z));
         if (randomSource.nextFloat() < 0.5F
            && (this.getBlock(world, 2, 1, z, box).is(Blocks.AIR) || this.getBlock(world, 2, 1, z, box).is(Blocks.CAVE_AIR))
            && ((BlockBehaviourAccessor)Blocks.RAIL).callCanSurvive(AIR, world, mutable)) {
            this.placeBlock(world, Blocks.RAIL.defaultBlockState(), 2, 1, z, box);
         }
      }

      for (int n = 0; n <= 7; n++) {
         this.chanceReplaceAir(
            world, randomSource, 0.07F, (BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(PoweredRailBlock.POWERED, true), 2, 1, n, box
         );
      }
   }

   private void generateTntCarts(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      for (int z = 0; z <= 7; z++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftTntMinecartSpawnRate) {
            BlockPos blockPos = this.getWorldPos(2, 1, z);
            if (box.isInside(blockPos) && !world.getBlockState(blockPos.below()).isAir()) {
               MinecartTNT tntMinecartEntity = new MinecartTNT(world.getLevel(), blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
               world.addFreshEntity(tntMinecartEntity);
            }
         }
      }
   }

   private void generateTorches(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      BlockState torchBlock = Blocks.WALL_TORCH.defaultBlockState();

      for (int z = 0; z <= 7; z++) {
         if (!this.supports.contains(z)) {
            float r = randomSource.nextFloat();
            if (r < BetterMineshaftsCommon.CONFIG.spawnRates.torchSpawnRate / 2.0) {
               BlockPos pos = this.getWorldPos(1, 2, z);
               BlockPos adjPos = this.getWorldPos(0, 2, z);
               boolean canPlace = world.getBlockState(pos).isAir() && world.getBlockState(adjPos) != AIR;
               if (canPlace) {
                  this.replaceAirOrChains(world, box, 1, 2, z, 1, 2, z, (BlockState)torchBlock.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
               }
            } else if (r < BetterMineshaftsCommon.CONFIG.spawnRates.torchSpawnRate) {
               BlockPos pos = this.getWorldPos(3, 2, z);
               BlockPos adjPos = this.getWorldPos(4, 2, z);
               boolean canPlace = world.getBlockState(pos).isAir() && world.getBlockState(adjPos) != AIR;
               if (canPlace) {
                  this.replaceAirOrChains(world, box, 3, 2, z, 3, 2, z, (BlockState)torchBlock.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
               }
            }
         }
      }
   }

   private void generatePillarsOrChains(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      this.generatePillarDownOrChainUp(world, randomSource, box, 1, 0, 1);
      this.generatePillarDownOrChainUp(world, randomSource, box, 3, 0, 1);
      this.generatePillarDownOrChainUp(world, randomSource, box, 1, 0, 6);
      this.generatePillarDownOrChainUp(world, randomSource, box, 3, 0, 6);
   }

   private void replaceExistingChainsWithChainBlock(WorldGenLevel world, BoundingBox box) {
      for (int x = 0; x <= 4; x++) {
         for (int z = 0; z <= 7; z++) {
            if (this.getBlock(world, x, 1, z, box).is(Blocks.CHAIN)) {
               this.placeBlock(world, this.config.blockStates.supportBlockState, x, 1, z, box);
            }
         }
      }
   }

   private void buildSupports(RandomSource randomSource) {
      for (int z = 0; z <= 7; z++) {
         int r = randomSource.nextInt(7);
         if (r == 0) {
            this.supports.add(z);
            z += 5;
         }
      }
   }
}
