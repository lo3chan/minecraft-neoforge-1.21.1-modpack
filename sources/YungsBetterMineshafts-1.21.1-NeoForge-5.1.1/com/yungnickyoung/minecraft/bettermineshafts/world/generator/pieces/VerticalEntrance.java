package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.yungsapi.world.util.SurfaceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class VerticalEntrance extends BetterMineshaftPiece {
   private final BlockPos centerPos;
   private int yAxisLen = 0;
   private int localYEnd = 0;
   private int tunnelLength = 0;
   private int tunnelFloorAltitude = 0;
   private Direction tunnelDirection = Direction.NORTH;
   private boolean hasTunnel = false;
   private static final int SHAFT_LOCAL_XZ_START = 22;
   private static final int SHAFT_LOCAL_XZ_END = 26;

   public VerticalEntrance(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.VERTICAL_ENTRANCE, compoundTag);
      int centerPosX = compoundTag.getIntArray("centerPos")[0];
      int centerPosY = compoundTag.getIntArray("centerPos")[1];
      int centerPosZ = compoundTag.getIntArray("centerPos")[2];
      this.centerPos = new BlockPos(centerPosX, centerPosY, centerPosZ);
      this.yAxisLen = compoundTag.getInt("yAxisLen");
      this.localYEnd = this.yAxisLen - 1;
      this.tunnelLength = compoundTag.getInt("tunnelLen");
      this.tunnelFloorAltitude = compoundTag.getInt("floorAltitude");
      int tunnelDirInt = compoundTag.getInt("tunnelDir");
      this.tunnelDirection = tunnelDirInt == -1 ? null : Direction.from2DDataValue(tunnelDirInt);
      this.hasTunnel = compoundTag.getBoolean("hasTunnel");
   }

   public VerticalEntrance(int pieceChainLen, MutableBlockPos centerPos, Direction direction, BetterMineshaftConfiguration config, int maxBuildHeight) {
      super(StructurePieceTypeModule.VERTICAL_ENTRANCE, pieceChainLen, config, getInitialBoundingBox(centerPos, maxBuildHeight));
      this.setOrientation(direction);
      this.centerPos = centerPos;
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
      compoundTag.putIntArray("centerPos", new int[]{this.centerPos.getX(), this.centerPos.getY(), this.centerPos.getZ()});
      compoundTag.putInt("yAxisLen", this.yAxisLen);
      compoundTag.putInt("tunnelLen", this.tunnelLength);
      compoundTag.putInt("floorAltitude", this.tunnelFloorAltitude);
      compoundTag.putInt("tunnelDir", this.tunnelDirection.get2DDataValue());
      compoundTag.putBoolean("hasTunnel", this.hasTunnel);
   }

   private static BoundingBox getInitialBoundingBox(BlockPos centerPos, int maxBuildHeight) {
      return new BoundingBox(centerPos.getX() - 24, centerPos.getY(), centerPos.getZ() - 24, centerPos.getX() + 24, maxBuildHeight, centerPos.getZ() + 24);
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
                  this.centerPos.getX() - 4,
                  this.centerPos.getY(),
                  this.centerPos.getZ() - 3,
                  direction,
                  this.genDepth
               );
               break;
            case SOUTH:
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.centerPos.getX() + 4,
                  this.centerPos.getY(),
                  this.centerPos.getZ() + 3,
                  direction,
                  this.genDepth
               );
               break;
            case WEST:
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.centerPos.getX() - 3,
                  this.centerPos.getY(),
                  this.centerPos.getZ() + 4,
                  direction,
                  this.genDepth
               );
               break;
            case EAST:
               BetterMineshaftGenerator.generateAndAddBigTunnelPiece(
                  structurePiece,
                  structurePieceAccessor,
                  randomSource,
                  this.centerPos.getX() + 3,
                  this.centerPos.getY(),
                  this.centerPos.getZ() - 4,
                  direction,
                  this.genDepth
               );
         }
      }
   }

   public void postProcess(
      WorldGenLevel world,
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      RandomSource randomSource,
      BoundingBox boundingBox,
      ChunkPos chunkPos,
      BlockPos blockPos
   ) {
      if (!this.hasTunnel) {
         this.determineDirection(world);
      }

      if (this.hasTunnel) {
         this.generateVerticalShaft(world, randomSource, boundingBox);
         this.generateSurfaceTunnel(world, randomSource, boundingBox);
      }
   }

   private void generateVerticalShaft(WorldGenLevel world, RandomSource randomSource, BoundingBox box) {
      this.chanceReplaceNonAir(
         world, box, randomSource, this.config.replacementRate, 22, 0, 22, 26, this.localYEnd, 26, this.config.blockStateRandomizers.mainRandomizer
      );
      this.replaceAirOrChains(world, box, 22, 0, 22, 26, this.localYEnd, 26, this.config.blockStates.mainBlockState);
      this.fill(world, box, 23, 1, 23, 25, this.localYEnd - 1, 25, AIR);
      this.replaceAirOrChains(world, box, 23, 0, 23, 25, 0, 25, this.config.blockStates.mainBlockState);
      this.replaceAirOrChains(world, box, 24, 1, 22, 24, this.localYEnd - 4, 22, this.config.blockStates.mainBlockState);
      this.fill(world, box, 24, 1, 23, 24, this.localYEnd - 4, 23, Blocks.LADDER.defaultBlockState());
      this.fill(world, box, 23, 1, 26, 25, 2, 26, this.config.blockStates.stoneWallBlockState);
      this.fill(world, box, 24, 3, 26, 24, 3, 26, this.config.blockStates.stoneSlabBlockState);
      this.fill(world, box, 24, 1, 26, 24, 2, 26, AIR);
      this.addBiomeDecorations(world, box, randomSource, 23, 0, 23, 25, 1, 25);
      this.addVines(world, box, randomSource, this.config.decorationChances.vineChance, 23, 0, 23, 25, this.localYEnd - 4, 25);
      this.generateLeg(world, randomSource, box, 22, 22, this.config.blockStateRandomizers.legRandomizer);
      this.generateLeg(world, randomSource, box, 22, 26, this.config.blockStateRandomizers.legRandomizer);
      this.generateLeg(world, randomSource, box, 26, 22, this.config.blockStateRandomizers.legRandomizer);
      this.generateLeg(world, randomSource, box, 26, 26, this.config.blockStateRandomizers.legRandomizer);
   }

   private void generateSurfaceTunnel(WorldGenLevel world, RandomSource randomSource, BoundingBox box) {
      int tunnelStartX = 0;
      int tunnelStartZ = 0;
      int tunnelEndX = 0;
      int tunnelEndZ = 0;
      Direction facing = this.getOrientation();
      float rotationDifference = facing.toYRot() - this.tunnelDirection.toYRot();
      Direction relativeTunnelDir = Direction.fromYRot(Direction.NORTH.toYRot() - rotationDifference);
      if (relativeTunnelDir == Direction.NORTH) {
         tunnelStartX = 22;
         tunnelStartZ = 26;
         tunnelEndX = 26;
         tunnelEndZ = 26 + this.tunnelLength;
      } else if ((relativeTunnelDir != Direction.WEST || facing == Direction.SOUTH || facing == Direction.WEST)
         && (relativeTunnelDir != Direction.EAST || facing != Direction.SOUTH && facing != Direction.WEST)) {
         if (relativeTunnelDir == Direction.SOUTH) {
            tunnelStartX = 22;
            tunnelStartZ = 22 - this.tunnelLength;
            tunnelEndX = 26;
            tunnelEndZ = 22;
         } else if (relativeTunnelDir == Direction.EAST || relativeTunnelDir == Direction.WEST) {
            tunnelStartX = 26;
            tunnelStartZ = 22;
            tunnelEndX = 26 + this.tunnelLength;
            tunnelEndZ = 26;
         }
      } else {
         tunnelStartX = 22 - this.tunnelLength;
         tunnelStartZ = 22;
         tunnelEndX = 22;
         tunnelEndZ = 26;
      }

      this.chanceReplaceNonAir(
         world,
         box,
         randomSource,
         0.6F,
         tunnelStartX,
         this.tunnelFloorAltitude,
         tunnelStartZ,
         tunnelEndX,
         this.tunnelFloorAltitude + 4,
         tunnelEndZ,
         this.config.blockStateRandomizers.mainRandomizer
      );
      if (facing.getAxis() == this.tunnelDirection.getAxis()) {
         this.replaceAirOrChains(
            world,
            box,
            tunnelStartX + 1,
            this.tunnelFloorAltitude,
            tunnelStartZ,
            tunnelEndX - 1,
            this.tunnelFloorAltitude,
            tunnelEndZ,
            this.config.blockStates.mainBlockState
         );
         this.fill(world, box, tunnelStartX + 1, this.tunnelFloorAltitude + 1, tunnelStartZ, tunnelEndX - 1, this.tunnelFloorAltitude + 3, tunnelEndZ, AIR);
      } else {
         this.replaceAirOrChains(
            world,
            box,
            tunnelStartX,
            this.tunnelFloorAltitude,
            tunnelStartZ + 1,
            tunnelEndX,
            this.tunnelFloorAltitude,
            tunnelEndZ - 1,
            this.config.blockStates.mainBlockState
         );
         this.fill(world, box, tunnelStartX, this.tunnelFloorAltitude + 1, tunnelStartZ + 1, tunnelEndX, this.tunnelFloorAltitude + 3, tunnelEndZ - 1, AIR);
      }

      this.addVines(
         world,
         box,
         randomSource,
         this.config.decorationChances.vineChance,
         tunnelStartX + 1,
         this.tunnelFloorAltitude,
         tunnelStartZ + 1,
         tunnelEndX - 1,
         this.tunnelFloorAltitude + 4,
         tunnelEndZ - 1
      );
      boolean[] validPositions;
      if (facing.getAxis() == this.tunnelDirection.getAxis()) {
         validPositions = new boolean[tunnelEndZ - tunnelStartZ + 1];

         for (int z = 0; z < validPositions.length; z++) {
            BlockState floorBlock = this.getBlock(world, tunnelStartX + 2, this.tunnelFloorAltitude, tunnelStartZ + z, box);
            if (floorBlock.isSolid()) {
               validPositions[z] = true;
            }
         }
      } else {
         validPositions = new boolean[tunnelEndX - tunnelStartX + 1];

         for (int x = 0; x < validPositions.length; x++) {
            BlockState floorBlock = this.getBlock(world, tunnelStartX + x, this.tunnelFloorAltitude, tunnelStartZ + 2, box);
            if (floorBlock.isSolid()) {
               validPositions[x] = true;
            }
         }
      }

      if (facing.getAxis() == this.tunnelDirection.getAxis()) {
         for (int zx = tunnelStartZ; zx <= tunnelEndZ; zx++) {
            int r = randomSource.nextInt(4);
            if (r == 0 && validPositions[zx - tunnelStartZ]) {
               this.fill(
                  world,
                  box,
                  tunnelStartX + 1,
                  this.tunnelFloorAltitude + 1,
                  zx,
                  tunnelStartX + 1,
                  this.tunnelFloorAltitude + 2,
                  zx,
                  this.config.blockStates.supportBlockState
               );
               this.fill(
                  world,
                  box,
                  tunnelStartX + 3,
                  this.tunnelFloorAltitude + 1,
                  zx,
                  tunnelStartX + 3,
                  this.tunnelFloorAltitude + 2,
                  zx,
                  this.config.blockStates.supportBlockState
               );
               this.fill(
                  world,
                  box,
                  tunnelStartX + 1,
                  this.tunnelFloorAltitude + 3,
                  zx,
                  tunnelStartX + 3,
                  this.tunnelFloorAltitude + 3,
                  zx,
                  this.config.blockStates.mainBlockState
               );
               this.chanceReplaceNonAir(
                  world,
                  box,
                  randomSource,
                  0.25F,
                  tunnelStartX + 1,
                  this.tunnelFloorAltitude + 3,
                  zx,
                  tunnelStartX + 3,
                  this.tunnelFloorAltitude + 3,
                  zx,
                  this.config.blockStates.supportBlockState
               );
               this.chanceReplaceAir(
                  world,
                  box,
                  randomSource,
                  0.15F,
                  tunnelStartX + 1,
                  this.tunnelFloorAltitude + 3,
                  zx - 1,
                  tunnelStartX + 1,
                  this.tunnelFloorAltitude + 3,
                  zx + 1,
                  Blocks.COBWEB.defaultBlockState()
               );
               this.chanceReplaceAir(
                  world,
                  box,
                  randomSource,
                  0.15F,
                  tunnelStartX + 3,
                  this.tunnelFloorAltitude + 3,
                  zx - 1,
                  tunnelStartX + 3,
                  this.tunnelFloorAltitude + 3,
                  zx + 1,
                  Blocks.COBWEB.defaultBlockState()
               );
               zx += 3;
            }
         }
      } else {
         for (int xx = tunnelStartX; xx <= tunnelEndX; xx++) {
            int r = randomSource.nextInt(4);
            if (r == 0 && validPositions[xx - tunnelStartX]) {
               this.fill(
                  world,
                  box,
                  xx,
                  this.tunnelFloorAltitude + 1,
                  tunnelStartZ + 1,
                  xx,
                  this.tunnelFloorAltitude + 2,
                  tunnelStartZ + 1,
                  this.config.blockStates.supportBlockState
               );
               this.fill(
                  world,
                  box,
                  xx,
                  this.tunnelFloorAltitude + 1,
                  tunnelStartZ + 3,
                  xx,
                  this.tunnelFloorAltitude + 2,
                  tunnelStartZ + 3,
                  this.config.blockStates.supportBlockState
               );
               this.fill(
                  world,
                  box,
                  xx,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 1,
                  xx,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 3,
                  this.config.blockStates.mainBlockState
               );
               this.chanceReplaceNonAir(
                  world,
                  box,
                  randomSource,
                  0.25F,
                  xx,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 1,
                  xx,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 3,
                  this.config.blockStates.supportBlockState
               );
               this.chanceReplaceAir(
                  world,
                  box,
                  randomSource,
                  0.15F,
                  xx - 1,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 1,
                  xx + 1,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 1,
                  Blocks.COBWEB.defaultBlockState()
               );
               this.chanceReplaceAir(
                  world,
                  box,
                  randomSource,
                  0.15F,
                  xx - 1,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 3,
                  xx + 1,
                  this.tunnelFloorAltitude + 3,
                  tunnelStartZ + 3,
                  Blocks.COBWEB.defaultBlockState()
               );
               xx += 3;
            }
         }
      }
   }

   private void determineDirection(WorldGenLevel world) {
      int minSurfaceHeight = world.getMaxBuildHeight() - 1;

      for (int xOffset = -2; xOffset <= 2; xOffset++) {
         for (int zOffset = -2; zOffset <= 2; zOffset++) {
            try {
               int realX = this.centerPos.getX() + xOffset;
               int realZ = this.centerPos.getZ() + zOffset;
               int chunkX = realX >> 4;
               int chunkZ = realZ >> 4;
               int surfaceHeight = SurfaceHelper.getSurfaceHeight(world.getChunk(chunkX, chunkZ), new ColumnPos(realX, realZ));
               if (surfaceHeight > 1) {
                  minSurfaceHeight = Math.min(minSurfaceHeight, surfaceHeight);
               }
            } catch (NullPointerException var15) {
               BetterMineshaftsCommon.LOGGER.error("Unexpected YUNG's Better Mineshafts error. Please report this!");
               BetterMineshaftsCommon.LOGGER.error(var15.toString());
               BetterMineshaftsCommon.LOGGER.error(var15.getMessage());
            }
         }
      }

      if (minSurfaceHeight >= 60 && minSurfaceHeight != world.getMaxBuildHeight() - 1) {
         int ceilingHeight = minSurfaceHeight - 2;
         int floorHeight = ceilingHeight - 4;
         this.yAxisLen = ceilingHeight - this.centerPos.getY() + 1;
         this.localYEnd = this.yAxisLen - 1;
         MutableBlockPos mutable = this.centerPos.mutable();
         int radius = 8;
         int maxRadialDist = 3;

         for (int radialDist = 0; radialDist < maxRadialDist; radialDist++) {
            for (Direction direction : Direction.values()) {
               if (direction != Direction.UP && direction != Direction.DOWN) {
                  mutable.set(this.centerPos.relative(direction, radius * radialDist + 2));

                  for (int i = radialDist * radius; i < radialDist * radius + radius; i++) {
                     int surfaceHeight = SurfaceHelper.getSurfaceHeight(world.getChunk(mutable), new ColumnPos(mutable.getX(), mutable.getZ()));
                     if (surfaceHeight <= floorHeight && surfaceHeight > 1) {
                        this.hasTunnel = true;
                        this.tunnelDirection = direction;
                        this.tunnelFloorAltitude = ceilingHeight - 4 - this.boundingBox.minY();
                        this.tunnelLength = i;
                        return;
                     }

                     mutable.move(direction);
                  }
               }
            }
         }
      }
   }
}
