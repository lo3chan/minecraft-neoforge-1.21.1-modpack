package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.mixin.BlockBehaviourAccessor;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BigTunnel extends BetterMineshaftPiece {
   private final List<BlockPos> smallShaftLeftEntrances = Lists.newLinkedList();
   private final List<BlockPos> smallShaftRightEntrances = Lists.newLinkedList();
   private final List<BoundingBox> sideRoomEntrances = Lists.newLinkedList();
   private final List<Integer> bigSupports = Lists.newLinkedList();
   private final List<Integer> smallSupports = Lists.newLinkedList();
   private final List<Pair<Integer, Integer>> gravelDeposits = Lists.newLinkedList();
   private static final int SECONDARY_AXIS_LEN = 9;
   private static final int Y_AXIS_LEN = 8;
   private static final int MAIN_AXIS_LEN = 24;
   private static final int LOCAL_X_END = 8;
   private static final int LOCAL_Y_END = 7;
   private static final int LOCAL_Z_END = 23;

   public BigTunnel(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.BIG_TUNNEL, compoundTag);
      ListTag listTag1 = compoundTag.getList("SmallShaftLeftEntrances", 11);
      ListTag listTag2 = compoundTag.getList("SmallShaftRightEntrances", 11);
      ListTag listTag3 = compoundTag.getList("SideRoomEntrances", 11);
      ListTag listTag4 = compoundTag.getList("BigSupports", 3);
      ListTag listTag5 = compoundTag.getList("SmallSupports", 3);
      ListTag listTag6 = compoundTag.getList("GravelDeposits", 11);

      for (int i = 0; i < listTag1.size(); i++) {
         this.smallShaftLeftEntrances.add(new BlockPos(listTag1.getIntArray(i)[0], listTag1.getIntArray(i)[1], listTag1.getIntArray(i)[2]));
      }

      for (int i = 0; i < listTag2.size(); i++) {
         this.smallShaftRightEntrances.add(new BlockPos(listTag2.getIntArray(i)[0], listTag2.getIntArray(i)[1], listTag2.getIntArray(i)[2]));
      }

      for (int i = 0; i < listTag3.size(); i++) {
         this.sideRoomEntrances
            .add(
               new BoundingBox(
                  listTag3.getIntArray(i)[0],
                  listTag3.getIntArray(i)[1],
                  listTag3.getIntArray(i)[2],
                  listTag3.getIntArray(i)[3],
                  listTag3.getIntArray(i)[4],
                  listTag3.getIntArray(i)[5]
               )
            );
      }

      for (int i = 0; i < listTag4.size(); i++) {
         this.bigSupports.add(listTag4.getInt(i));
      }

      for (int i = 0; i < listTag5.size(); i++) {
         this.smallSupports.add(listTag5.getInt(i));
      }

      for (int i = 0; i < listTag6.size(); i++) {
         this.gravelDeposits.add(new Pair(listTag6.getIntArray(i)[0], listTag6.getIntArray(i)[1]));
      }
   }

   public BigTunnel(int chainLength, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.BIG_TUNNEL, chainLength, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
      ListTag listTag1 = new ListTag();
      ListTag listTag2 = new ListTag();
      ListTag listTag3 = new ListTag();
      ListTag listTag4 = new ListTag();
      ListTag listTag5 = new ListTag();
      ListTag listTag6 = new ListTag();
      this.smallShaftLeftEntrances.forEach(pos -> listTag1.add(new IntArrayTag(new int[]{pos.getX(), pos.getY(), pos.getZ()})));
      this.smallShaftRightEntrances.forEach(pos -> listTag2.add(new IntArrayTag(new int[]{pos.getX(), pos.getY(), pos.getZ()})));
      this.sideRoomEntrances
         .forEach(
            blockBox -> listTag3.add(
               new IntArrayTag(new int[]{blockBox.minX(), blockBox.minY(), blockBox.minZ(), blockBox.maxX(), blockBox.maxY(), blockBox.maxZ()})
            )
         );
      this.bigSupports.forEach(z -> listTag4.add(IntTag.valueOf(z)));
      this.smallSupports.forEach(z -> listTag5.add(IntTag.valueOf(z)));
      this.gravelDeposits.forEach(pair -> listTag6.add(new IntArrayTag(new int[]{(Integer)pair.getFirst(), (Integer)pair.getSecond()})));
      compoundTag.put("SmallShaftLeftEntrances", listTag1);
      compoundTag.put("SmallShaftRightEntrances", listTag2);
      compoundTag.put("SideRoomEntrances", listTag3);
      compoundTag.put("BigSupports", listTag4);
      compoundTag.put("SmallSupports", listTag5);
      compoundTag.put("GravelDeposits", listTag6);
   }

   public static BoundingBox determineBoxPosition(int x, int y, int z, Direction direction) {
      return BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 9, 8, 24, direction);
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      Direction direction = this.getOrientation();
      if (direction != null) {
         switch (direction) {
            case NORTH:
            default:
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
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
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
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
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
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
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
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

         int pieceLen = this.getOrientation().getAxis() == Axis.Z ? this.boundingBox.getZSpan() : this.boundingBox.getXSpan();
         this.buildSideRoomsLeft(structurePiece, structurePieceAccessor, randomSource, direction, pieceLen);
         this.buildSideRoomsRight(structurePiece, structurePieceAccessor, randomSource, direction, pieceLen);
         this.buildSmallShaftsLeft(structurePiece, structurePieceAccessor, randomSource, direction, pieceLen);
         this.buildSmallShaftsRight(structurePiece, structurePieceAccessor, randomSource, direction, pieceLen);
         this.buildSupports(randomSource);
         this.buildGravelDeposits(randomSource);
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
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 8, 7, 23, this.config.blockStateRandomizers.mainRandomizer);
      this.chanceReplaceNonAir(world, box, randomSource, this.config.replacementRate, 0, 0, 0, 8, 0, 23, this.config.blockStateRandomizers.floorRandomizer);
      this.fill(world, box, 1, 1, 0, 7, 4, 23, AIR);
      this.fill(world, box, 2, 4, 0, 6, 5, 23, AIR);
      this.fill(world, box, 3, 6, 0, 5, 6, 23, AIR);
      this.replaceAirOrChains(world, box, 1, 0, 0, 7, 0, 23, this.config.blockStates.mainBlockState);
      this.generateSmallShaftEntrances(world, box, randomSource);
      this.generateSideRoomOpenings(world, box, randomSource);
      this.generateLegs(world, box, randomSource);
      this.generateBigSupports(world, box, randomSource);
      this.generateSmallSupports(world, box, randomSource);
      this.generateChestCarts(world, box, randomSource);
      this.generateTntCarts(world, box, randomSource);
      this.generateGravelDeposits(world, box, randomSource);
      this.addBiomeDecorations(world, box, randomSource, 0, 0, 0, 8, 6, 23);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 1, 0, 1, 7, 7, 22);
      this.generateLanterns(world, box, randomSource);
      this.generateRails(world, box, randomSource);
   }

   private void generateSmallShaftEntrances(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      for (BlockPos entrancePos : this.smallShaftLeftEntrances) {
         int x = entrancePos.getX();
         int y = entrancePos.getY();
         int z = entrancePos.getZ();
         int numCovered = 0;

         for (int i = z; i <= z + 2; i++) {
            for (int j = x; j <= x + 1; j++) {
               BlockState blockState = this.getBlock(world, j, y + 3, i, box);
               if (!blockState.isAir() && !blockState.is(Blocks.CHAIN)) {
                  numCovered++;
               }
            }
         }

         this.fill(world, box, x, y, z, x + 1, y + 2, z + 2, AIR);
         this.replaceAirOrChains(world, box, x, y - 1, z, x + 1, y - 1, z + 2, this.config.blockStates.mainBlockState);
         if (numCovered >= 2) {
            this.placeBlock(world, this.config.blockStates.supportBlockState, x, y + 1, z, box);
            this.placeBlock(world, this.config.blockStates.supportBlockState, x, y + 1, z + 2, box);
            this.fill(world, box, x + 1, y, z, x + 1, y + 1, z, this.config.blockStates.supportBlockState);
            this.fill(world, box, x + 1, y, z + 2, x + 1, y + 1, z + 2, this.config.blockStates.supportBlockState);
            this.chanceFill(world, box, randomSource, 0.75F, x, y + 2, z, x + 1, y + 2, z + 2, this.config.blockStates.mainBlockState);
            this.fill(world, box, x, y + 1, z + 1, x + 1, y + 1, z + 1, AIR);
         }
      }

      for (BlockPos entrancePos : this.smallShaftRightEntrances) {
         int x = entrancePos.getX();
         int y = entrancePos.getY();
         int z = entrancePos.getZ();
         int numCovered = 0;

         for (int i = z; i <= z + 2; i++) {
            for (int jx = x; jx <= x + 1; jx++) {
               BlockState blockState = this.getBlock(world, jx, y + 3, i, box);
               if (!blockState.isAir() && !blockState.is(Blocks.CHAIN)) {
                  numCovered++;
               }
            }
         }

         this.fill(world, box, x, y, z, x + 1, y + 2, z + 2, AIR);
         this.replaceAirOrChains(world, box, x, y - 1, z, x + 1, y - 1, z + 2, this.config.blockStates.mainBlockState);
         if (numCovered >= 2) {
            this.placeBlock(world, this.config.blockStates.supportBlockState, x + 1, y + 1, z, box);
            this.placeBlock(world, this.config.blockStates.supportBlockState, x + 1, y + 1, z + 2, box);
            this.fill(world, box, x, y, z, x, y + 1, z, this.config.blockStates.supportBlockState);
            this.fill(world, box, x, y, z + 2, x, y + 1, z + 2, this.config.blockStates.supportBlockState);
            this.chanceFill(world, box, randomSource, 0.75F, x, y + 2, z, x + 1, y + 2, z + 2, this.config.blockStates.mainBlockState);
            this.fill(world, box, x, y + 1, z + 1, x + 1, y + 1, z + 1, AIR);
         }
      }
   }

   private void generateLegs(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      if (this.config.legVariant == BetterMineshaftConfiguration.LegVariant.EDGE) {
         this.generateLegsVariant1(world, box, randomSource);
      } else {
         this.generateLegsVariant2(world, box, randomSource);
      }
   }

   private void generateLegsVariant1(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      BlockState supportBlock = this.config.blockStates.supportBlockState;
      if (supportBlock.getProperties().contains(BlockStateProperties.NORTH_WALL) && supportBlock.getProperties().contains(BlockStateProperties.SOUTH_WALL)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.NORTH_WALL, WallSide.TALL))
            .setValue(BlockStateProperties.SOUTH_WALL, WallSide.TALL);
      } else if (supportBlock.getProperties().contains(BlockStateProperties.NORTH) && supportBlock.getProperties().contains(BlockStateProperties.SOUTH)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.NORTH, true)).setValue(BlockStateProperties.SOUTH, true);
      }

      if (supportBlock.getProperties().contains(BlockStateProperties.UP)) {
         supportBlock = (BlockState)supportBlock.setValue(BlockStateProperties.UP, false);
      }

      BlockStateRandomizer legSelector = this.config.blockStateRandomizers.legRandomizer;
      boolean generatedLeg = this.generateLegOrChain(world, randomSource, box, 1, 0, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 1, -1, 1, 1, -1, 5, supportBlock);
         this.replaceAirOrChains(world, box, 1, -2, 1, 1, -2, 3, supportBlock);
         this.replaceAirOrChains(world, box, 1, -3, 1, 1, -3, 2, supportBlock);
         this.replaceAirOrChains(world, box, 1, -5, 1, 1, -4, 1, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 1, 11, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 1, -1, 6, 1, -1, 10, supportBlock);
         this.replaceAirOrChains(world, box, 1, -2, 8, 1, -2, 10, supportBlock);
         this.replaceAirOrChains(world, box, 1, -3, 9, 1, -3, 10, supportBlock);
         this.replaceAirOrChains(world, box, 1, -5, 10, 1, -4, 10, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 1, 12, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 1, -1, 13, 1, -1, 17, supportBlock);
         this.replaceAirOrChains(world, box, 1, -2, 13, 1, -2, 15, supportBlock);
         this.replaceAirOrChains(world, box, 1, -3, 13, 1, -3, 14, supportBlock);
         this.replaceAirOrChains(world, box, 1, -5, 13, 1, -4, 13, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 1, 23, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 1, -1, 18, 1, -1, 22, supportBlock);
         this.replaceAirOrChains(world, box, 1, -2, 20, 1, -2, 22, supportBlock);
         this.replaceAirOrChains(world, box, 1, -3, 21, 1, -3, 22, supportBlock);
         this.replaceAirOrChains(world, box, 1, -5, 22, 1, -4, 22, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 7, 0, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 7, -1, 1, 7, -1, 5, supportBlock);
         this.replaceAirOrChains(world, box, 7, -2, 1, 7, -2, 3, supportBlock);
         this.replaceAirOrChains(world, box, 7, -3, 1, 7, -3, 2, supportBlock);
         this.replaceAirOrChains(world, box, 7, -5, 1, 7, -4, 1, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 7, 11, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 7, -1, 6, 7, -1, 10, supportBlock);
         this.replaceAirOrChains(world, box, 7, -2, 8, 7, -2, 10, supportBlock);
         this.replaceAirOrChains(world, box, 7, -3, 9, 7, -3, 10, supportBlock);
         this.replaceAirOrChains(world, box, 7, -5, 10, 7, -4, 10, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 7, 12, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 7, -1, 13, 7, -1, 17, supportBlock);
         this.replaceAirOrChains(world, box, 7, -2, 13, 7, -2, 15, supportBlock);
         this.replaceAirOrChains(world, box, 7, -3, 13, 7, -3, 14, supportBlock);
         this.replaceAirOrChains(world, box, 7, -5, 13, 7, -4, 13, supportBlock);
      }

      generatedLeg = this.generateLegOrChain(world, randomSource, box, 7, 23, legSelector);
      if (generatedLeg) {
         this.replaceAirOrChains(world, box, 7, -1, 18, 7, -1, 22, supportBlock);
         this.replaceAirOrChains(world, box, 7, -2, 20, 7, -2, 22, supportBlock);
         this.replaceAirOrChains(world, box, 7, -3, 21, 7, -3, 22, supportBlock);
         this.replaceAirOrChains(world, box, 7, -5, 22, 7, -4, 22, supportBlock);
      }
   }

   private void generateLegsVariant2(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      BlockStateRandomizer legSelector = this.config.blockStateRandomizers.legRandomizer;

      for (int z = 0; z <= 23; z += 7) {
         this.generateLeg(world, randomSource, box, 2, z + 1, legSelector);
         this.generateLeg(world, randomSource, box, 6, z + 1, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 1, -1, z, 7, -1, z + 2, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 2, -1, z + 3, 2, -1, z + 3, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 6, -1, z + 3, 6, -1, z + 3, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 3, -1, z + 3, 5, -1, z + 6, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 2, -1, z + 6, 2, -1, z + 6, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 6, -1, z + 6, 6, -1, z + 6, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 2, -2, z, 2, -2, z, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 6, -2, z, 6, -2, z, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 2, -2, z + 2, 2, -2, z + 2, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 6, -2, z + 2, 6, -2, z + 2, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 1, -2, z + 1, 1, -2, z + 1, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 7, -2, z + 1, 7, -2, z + 1, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 3, -2, z + 1, 3, -2, z + 1, legSelector);
         this.replaceAirOrChains(world, box, randomSource, 5, -2, z + 1, 5, -2, z + 1, legSelector);
      }
   }

   private void generateGravelDeposits(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      this.gravelDeposits.forEach(pair -> {
         int z = (Integer)pair.getFirst();
         int side = (Integer)pair.getSecond();
         switch (side) {
            case 0:
            default:
               this.replaceAirOrChains(world, box, 1, 1, z, 1, 2, z + 2, this.config.blockStates.gravelBlockState);
               this.replaceAirOrChains(world, box, 1, 3, z + 1, 1, 3 + randomSource.nextInt(2), z + 1, this.config.blockStates.gravelBlockState);
               this.chanceReplaceAir(world, box, randomSource, 0.5F, 1, 3, z, 1, 3, z + 2, this.config.blockStates.gravelBlockState);
               this.replaceAirOrChains(world, box, 2, 1, z + 1, 2, 2 + randomSource.nextInt(2), z + 1, this.config.blockStates.gravelBlockState);
               this.replaceAirOrChains(world, box, 2, 1, z, 2, 1 + randomSource.nextInt(2), z + 2, this.config.blockStates.gravelBlockState);
               this.chanceReplaceAir(world, box, randomSource, 0.5F, 3, 1, z, 3, 1, z + 2, this.config.blockStates.gravelBlockState);
               break;
            case 1:
               this.replaceAirOrChains(world, box, 7, 1, z, 7, 2, z + 2, this.config.blockStates.gravelBlockState);
               this.replaceAirOrChains(world, box, 7, 3, z + 1, 7, 3 + randomSource.nextInt(2), z + 1, this.config.blockStates.gravelBlockState);
               this.chanceReplaceAir(world, box, randomSource, 0.5F, 7, 3, z, 7, 3, z + 2, this.config.blockStates.gravelBlockState);
               this.replaceAirOrChains(world, box, 6, 1, z + 1, 6, 2 + randomSource.nextInt(2), z + 1, this.config.blockStates.gravelBlockState);
               this.replaceAirOrChains(world, box, 6, 1, z, 6, 1 + randomSource.nextInt(2), z + 2, this.config.blockStates.gravelBlockState);
               this.chanceReplaceAir(world, box, randomSource, 0.5F, 5, 1, z, 5, 1, z + 2, this.config.blockStates.gravelBlockState);
         }
      });
   }

   private void generateChestCarts(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      for (int z = 0; z <= 23; z++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.mainShaftChestMinecartSpawnRate) {
            BlockPos blockPos = this.getWorldPos(4, 1, z);
            if (box.isInside(blockPos) && !world.getBlockState(blockPos.below()).isAir()) {
               MinecartChest chestMinecartEntity = new MinecartChest(world.getLevel(), blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
               chestMinecartEntity.setLootTable(BuiltInLootTables.ABANDONED_MINESHAFT, randomSource.nextLong());
               world.addFreshEntity(chestMinecartEntity);
            }
         }
      }
   }

   private void generateTntCarts(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      for (int z = 0; z <= 23; z++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.mainShaftTntMinecartSpawnRate) {
            BlockPos blockPos = this.getWorldPos(4, 1, z);
            if (box.isInside(blockPos) && !world.getBlockState(blockPos.below()).isAir()) {
               MinecartTNT tntMinecartEntity = new MinecartTNT(world.getLevel(), blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
               world.addFreshEntity(tntMinecartEntity);
            }
         }
      }
   }

   private void generateBigSupports(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      float cobwebChance = (float)BetterMineshaftsCommon.CONFIG.spawnRates.cobwebSpawnRate;
      BlockState supportBlock = this.config.blockStates.supportBlockState;
      if (supportBlock.getProperties().contains(BlockStateProperties.EAST_WALL) && supportBlock.getProperties().contains(BlockStateProperties.WEST_WALL)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.EAST_WALL, WallSide.TALL))
            .setValue(BlockStateProperties.WEST_WALL, WallSide.TALL);
      } else if (supportBlock.getProperties().contains(BlockStateProperties.EAST) && supportBlock.getProperties().contains(BlockStateProperties.WEST)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.EAST, true)).setValue(BlockStateProperties.WEST, true);
      }

      for (int z : this.bigSupports) {
         int numCovered = 0;

         for (int x = 2; x <= 6; x++) {
            BlockState blockState = this.getBlock(world, x, 7, z, box);
            if (!blockState.isAir() && !blockState.is(Blocks.CHAIN)) {
               numCovered++;
            }
         }

         if (numCovered >= 2) {
            this.chanceFill(world, box, randomSource, 0.6F, 1, 1, z, 2, 1, z + 2, this.config.blockStates.slabBlockState);
            this.chanceFill(world, box, randomSource, 0.6F, 6, 1, z, 7, 1, z + 2, this.config.blockStates.slabBlockState);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 1, 1, z + 1, box);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 7, 1, z + 1, box);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 1, 4, z + 1, box);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 7, 4, z + 1, box);
            this.fill(world, box, 2, 5, z + 1, 6, 5, z + 1, this.config.blockStates.mainBlockState);
            this.fill(world, box, 1, 2, z + 1, 1, 3, z + 1, this.config.blockStates.supportBlockState);
            this.fill(world, box, 7, 2, z + 1, 7, 3, z + 1, this.config.blockStates.supportBlockState);
            this.chanceReplaceNonAir(world, box, randomSource, 0.4F, 2, 5, z + 1, 6, 5, z + 1, supportBlock);
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 1, 1, z, 1, 4, z + 2, Blocks.COBWEB.defaultBlockState());
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 7, 1, z, 7, 4, z + 2, Blocks.COBWEB.defaultBlockState());
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 2, 5, z, 6, 5, z + 2, Blocks.COBWEB.defaultBlockState());
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 2, 4, z + 1, 6, 4, z + 1, Blocks.COBWEB.defaultBlockState());
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 3, 6, z + 1, 5, 6, z + 1, Blocks.COBWEB.defaultBlockState());
         }
      }
   }

   private void generateSmallSupports(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      float cobwebChance = (float)BetterMineshaftsCommon.CONFIG.spawnRates.cobwebSpawnRate;
      BlockState supportBlock = this.config.blockStates.supportBlockState;
      if (supportBlock.getProperties().contains(BlockStateProperties.EAST_WALL) && supportBlock.getProperties().contains(BlockStateProperties.WEST_WALL)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.EAST_WALL, WallSide.TALL))
            .setValue(BlockStateProperties.WEST_WALL, WallSide.TALL);
      } else if (supportBlock.getProperties().contains(BlockStateProperties.EAST) && supportBlock.getProperties().contains(BlockStateProperties.WEST)) {
         supportBlock = (BlockState)((BlockState)supportBlock.setValue(BlockStateProperties.EAST, true)).setValue(BlockStateProperties.WEST, true);
      }

      for (int z : this.smallSupports) {
         int numCovered = 0;

         for (int x = 2; x <= 6; x++) {
            BlockState blockState = this.getBlock(world, x, 7, z, box);
            if (!blockState.isAir() && !blockState.is(Blocks.CHAIN)) {
               numCovered++;
            }
         }

         if (numCovered >= 2) {
            this.placeBlock(world, this.config.blockStates.mainBlockState, 2, 1, z, box);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 6, 1, z, box);
            this.placeBlock(world, this.config.blockStates.supportBlockState, 2, 2, z, box);
            this.placeBlock(world, this.config.blockStates.supportBlockState, 6, 2, z, box);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 2, 3, z, box);
            this.placeBlock(world, this.config.blockStates.mainBlockState, 6, 3, z, box);
            this.fill(world, box, 3, 4, z, 5, 4, z, this.config.blockStates.mainBlockState);
            this.chanceReplaceNonAir(world, box, randomSource, 0.5F, 3, 4, z, 5, 4, z, supportBlock);
            this.chanceFill(world, box, randomSource, 0.4F, 2, 3, z, 6, 3, z, supportBlock);
            this.placeBlock(world, supportBlock, 3, 3, z, box);
            this.placeBlock(world, supportBlock, 5, 3, z, box);
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 2, 3, z - 1, 6, 4, z + 1, Blocks.COBWEB.defaultBlockState());
            this.chanceReplaceAir(world, box, randomSource, cobwebChance, 3, 5, z, 5, 5, z, Blocks.COBWEB.defaultBlockState());
         }
      }
   }

   private void generateLanterns(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      BlockState LANTERN = (BlockState)Blocks.LANTERN.defaultBlockState().setValue(BlockStateProperties.HANGING, true);

      for (int z = 0; z <= 23; z++) {
         for (int x = 3; x <= 5; x++) {
            if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.lanternSpawnRate && LANTERN.canSurvive(world, this.getWorldPos(x, 6, z))) {
               this.placeBlock(world, LANTERN, x, 6, z, box);
               z += 20;
            }
         }
      }
   }

   private void generateRails(WorldGenLevel world, BoundingBox box, RandomSource randomSource) {
      MutableBlockPos mutable = new MutableBlockPos();

      for (int z = 0; z <= 23; z++) {
         mutable.set(this.getWorldX(4, z), this.getWorldY(1), this.getWorldZ(4, z));
         if (randomSource.nextFloat() < 0.5F
            && (this.getBlock(world, 4, 1, z, box).is(Blocks.AIR) || this.getBlock(world, 4, 1, z, box).is(Blocks.CAVE_AIR))
            && ((BlockBehaviourAccessor)Blocks.RAIL).callCanSurvive(AIR, world, mutable)) {
            this.placeBlock(world, Blocks.RAIL.defaultBlockState(), 4, 1, z, box);
         }
      }

      int blocksSinceLastRail = 0;

      for (int n = 0; n <= 23; n++) {
         blocksSinceLastRail++;
         if ((randomSource.nextInt(20) == 0 || blocksSinceLastRail > 25) && this.getBlock(world, 4, 1, n, box).getBlock() == Blocks.RAIL) {
            this.placeBlock(world, (BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, true), 4, 1, n, box);
            blocksSinceLastRail = 0;
         }
      }
   }

   private void generateSideRoomOpenings(WorldGenLevel world, BoundingBox chunkBox, RandomSource randomSource) {
      this.sideRoomEntrances
         .forEach(
            entranceBox -> {
               this.replaceAirOrChains(
                  world,
                  chunkBox,
                  randomSource,
                  entranceBox.minX(),
                  0,
                  entranceBox.minZ(),
                  entranceBox.maxX(),
                  0,
                  entranceBox.maxZ(),
                  this.config.blockStateRandomizers.brickRandomizer
               );
               switch (randomSource.nextInt(3)) {
                  case 0:
                     this.fill(
                        world,
                        chunkBox,
                        entranceBox.minX(),
                        entranceBox.minY(),
                        entranceBox.minZ() + 2,
                        entranceBox.maxX(),
                        entranceBox.maxY(),
                        entranceBox.maxZ() - 2,
                        AIR
                     );
                     return;
                  case 1:
                     this.fill(
                        world,
                        chunkBox,
                        entranceBox.minX(),
                        entranceBox.minY(),
                        entranceBox.minZ() + 2,
                        entranceBox.maxX(),
                        entranceBox.maxY() - 1,
                        entranceBox.minZ() + 2,
                        AIR
                     );
                     this.fill(
                        world,
                        chunkBox,
                        entranceBox.minX(),
                        entranceBox.minY(),
                        entranceBox.minZ() + 4,
                        entranceBox.maxX(),
                        entranceBox.maxY() - 1,
                        entranceBox.minZ() + 5,
                        AIR
                     );
                     this.fill(
                        world,
                        chunkBox,
                        entranceBox.minX(),
                        entranceBox.minY(),
                        entranceBox.minZ() + 7,
                        entranceBox.maxX(),
                        entranceBox.maxY() - 1,
                        entranceBox.minZ() + 7,
                        AIR
                     );
                     return;
                  case 2:
               }
            }
         );
   }

   private void buildGravelDeposits(RandomSource random) {
      for (int z = 0; z <= 21; z++) {
         float r = random.nextFloat();
         if (r < this.config.decorationChances.gravelPileChance / 2.0F) {
            this.gravelDeposits.add(new Pair(z, 0));
            z += 2;
         } else if (r < this.config.decorationChances.gravelPileChance) {
            this.gravelDeposits.add(new Pair(z, 1));
            z += 2;
         }
      }
   }

   private void buildSupports(RandomSource randomSource) {
      int counter = 0;
      int MAX_COUNT = 10;

      for (int z = 0; z <= 21; z++) {
         counter++;
         boolean blockingEntrance = false;

         for (BlockPos entrancePos : this.smallShaftLeftEntrances) {
            if (entrancePos.getZ() <= z + 2 && z <= entrancePos.getZ() + 2) {
               blockingEntrance = true;
               break;
            }
         }

         for (BlockPos entrancePosx : this.smallShaftRightEntrances) {
            if (entrancePosx.getZ() <= z + 2 && z <= entrancePosx.getZ() + 2) {
               blockingEntrance = true;
               break;
            }
         }

         if (!blockingEntrance) {
            int r = randomSource.nextInt(8);
            if (r == 0 || counter >= 10) {
               this.bigSupports.add(z);
               counter = 0;
               z += 3;
            } else if (r == 1) {
               this.smallSupports.add(z);
               counter = 0;
               z += 3;
            }
         }
      }
   }

   private void buildSideRoomsLeft(
      StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource, Direction direction, int pieceLen
   ) {
      for (int n = 0; n < pieceLen - 1 - 10; n++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.workstationSpawnRate) {
            switch (direction) {
               case NORTH:
               default:
                  Direction nextPieceDirectionxxx = Direction.EAST;
                  StructurePiece newPiecexxx = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() - 5,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() - n - 9,
                     nextPieceDirectionxxx,
                     this.genDepth
                  );
                  if (newPiecexxx != null) {
                     this.sideRoomEntrances.add(new BoundingBox(0, 1, n, 0, 3, n + 9));
                  }
                  break;
               case SOUTH:
                  Direction nextPieceDirectionxx = Direction.WEST;
                  StructurePiece newPiecexx = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() + 5,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() + n + 9,
                     nextPieceDirectionxx,
                     this.genDepth
                  );
                  if (newPiecexx != null) {
                     this.sideRoomEntrances.add(new BoundingBox(8, 1, n, 8, 3, n + 9));
                  }
                  break;
               case WEST:
                  Direction nextPieceDirectionx = Direction.NORTH;
                  StructurePiece newPiecex = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() - n - 9,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() + 5,
                     nextPieceDirectionx,
                     this.genDepth
                  );
                  if (newPiecex != null) {
                     this.sideRoomEntrances.add(new BoundingBox(8, 1, n, 8, 3, n + 9));
                  }
                  break;
               case EAST:
                  Direction nextPieceDirection = Direction.SOUTH;
                  StructurePiece newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() + n + 9,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() - 5,
                     nextPieceDirection,
                     this.genDepth
                  );
                  if (newPiece != null) {
                     this.sideRoomEntrances.add(new BoundingBox(0, 1, n, 0, 3, n + 9));
                  }
            }

            n += 10;
         }
      }
   }

   private void buildSideRoomsRight(
      StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource, Direction direction, int pieceLen
   ) {
      for (int n = 0; n < pieceLen - 1 - 10; n++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.workstationSpawnRate) {
            switch (direction) {
               case NORTH:
               default:
                  Direction nextPieceDirectionxxx = Direction.WEST;
                  StructurePiece newPiecexxx = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() + 5,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() - n,
                     nextPieceDirectionxxx,
                     this.genDepth
                  );
                  if (newPiecexxx != null) {
                     this.sideRoomEntrances.add(new BoundingBox(8, 1, n, 8, 3, n + 9));
                  }
                  break;
               case SOUTH:
                  Direction nextPieceDirectionxx = Direction.EAST;
                  StructurePiece newPiecexx = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() - 5,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() + n,
                     nextPieceDirectionxx,
                     this.genDepth
                  );
                  if (newPiecexx != null) {
                     this.sideRoomEntrances.add(new BoundingBox(0, 1, n, 0, 3, n + 9));
                  }
                  break;
               case WEST:
                  Direction nextPieceDirectionx = Direction.SOUTH;
                  StructurePiece newPiecex = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() - n,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() - 5,
                     nextPieceDirectionx,
                     this.genDepth
                  );
                  if (newPiecex != null) {
                     this.sideRoomEntrances.add(new BoundingBox(0, 1, n, 0, 3, n + 9));
                  }
                  break;
               case EAST:
                  Direction nextPieceDirection = Direction.NORTH;
                  StructurePiece newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() + n,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() + 5,
                     nextPieceDirection,
                     this.genDepth
                  );
                  if (newPiece != null) {
                     this.sideRoomEntrances.add(new BoundingBox(8, 1, n, 8, 3, n + 9));
                  }
            }

            n += 10;
         }
      }
   }

   private void buildSmallShaftsLeft(
      StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource, Direction direction, int pieceLen
   ) {
      for (int n = 0; n < pieceLen - 1 - 4; n++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftSpawnRate) {
            switch (direction) {
               case NORTH:
               default:
                  Direction nextPieceDirectionxxx = Direction.WEST;
                  StructurePiece newPiecexxx = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() - 1,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() - n,
                     nextPieceDirectionxxx,
                     0
                  );
                  if (newPiecexxx != null) {
                     this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n + 1));
                  }
                  break;
               case SOUTH:
                  Direction nextPieceDirectionxx = Direction.EAST;
                  StructurePiece newPiecexx = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() + 1,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() + n,
                     nextPieceDirectionxx,
                     0
                  );
                  if (newPiecexx != null) {
                     this.smallShaftRightEntrances.add(new BlockPos(7, 1, n + 1));
                  }
                  break;
               case WEST:
                  Direction nextPieceDirectionx = Direction.SOUTH;
                  StructurePiece newPiecex = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() - n,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() + 1,
                     nextPieceDirectionx,
                     0
                  );
                  if (newPiecex != null) {
                     this.smallShaftRightEntrances.add(new BlockPos(7, 1, n + 1));
                  }
                  break;
               case EAST:
                  Direction nextPieceDirection = Direction.NORTH;
                  StructurePiece newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() + n,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() - 1,
                     nextPieceDirection,
                     0
                  );
                  if (newPiece != null) {
                     this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n + 1));
                  }
            }

            n += randomSource.nextInt(7) + 5;
         }
      }
   }

   private void buildSmallShaftsRight(
      StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource, Direction direction, int pieceLen
   ) {
      for (int n = 5; n < pieceLen; n++) {
         if (randomSource.nextFloat() < BetterMineshaftsCommon.CONFIG.spawnRates.smallShaftSpawnRate) {
            switch (direction) {
               case NORTH:
               default:
                  Direction nextPieceDirectionxxx = Direction.EAST;
                  StructurePiece newPiecexxx = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() + 1,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() - n,
                     nextPieceDirectionxxx,
                     0
                  );
                  if (newPiecexxx != null) {
                     this.smallShaftRightEntrances.add(new BlockPos(7, 1, n - 3));
                  }
                  break;
               case SOUTH:
                  Direction nextPieceDirectionxx = Direction.WEST;
                  StructurePiece newPiecexx = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() - 1,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() + n,
                     nextPieceDirectionxx,
                     0
                  );
                  if (newPiecexx != null) {
                     this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n - 3));
                  }
                  break;
               case WEST:
                  Direction nextPieceDirectionx = Direction.NORTH;
                  StructurePiece newPiecex = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.maxX() - n,
                     this.boundingBox.minY(),
                     this.boundingBox.minZ() - 1,
                     nextPieceDirectionx,
                     0
                  );
                  if (newPiecex != null) {
                     this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n - 3));
                  }
                  break;
               case EAST:
                  Direction nextPieceDirection = Direction.SOUTH;
                  StructurePiece newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(
                     structurePiece,
                     structurePieceAccessor,
                     randomSource,
                     this.boundingBox.minX() + n,
                     this.boundingBox.minY(),
                     this.boundingBox.maxZ() + 1,
                     nextPieceDirection,
                     0
                  );
                  if (newPiece != null) {
                     this.smallShaftRightEntrances.add(new BlockPos(7, 1, n - 3));
                  }
            }

            n += randomSource.nextInt(7) + 5;
         }
      }
   }
}
