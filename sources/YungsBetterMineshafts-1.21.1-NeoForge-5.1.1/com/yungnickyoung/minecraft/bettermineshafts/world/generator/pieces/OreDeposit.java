package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.yungsapi.world.util.BoundingBoxHelper;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

public class OreDeposit extends BetterMineshaftPiece {
   private OreDeposit.OreType oreType;
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 4;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 3;

   public OreDeposit(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.ORE_DEPOSIT, compoundTag);
      this.oreType = OreDeposit.OreType.valueOf(compoundTag.getInt("OreType"));
   }

   public OreDeposit(int chunkPieceLen, BoundingBox blockBox, Direction direction, BetterMineshaftConfiguration config) {
      super(StructurePieceTypeModule.ORE_DEPOSIT, chunkPieceLen, config, blockBox);
      this.setOrientation(direction);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
      compoundTag.putInt("OreType", this.oreType.value);
   }

   public static BoundingBox determineBoxPosition(StructurePieceAccessor structurePieceAccessor, int x, int y, int z, Direction direction) {
      BoundingBox blockBox = BoundingBoxHelper.boxFromCoordsWithRotation(x, y, z, 5, 5, 4, direction);
      StructurePiece intersectingPiece = structurePieceAccessor.findCollisionPiece(blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @Override
   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      int r = randomSource.nextInt(100);

      for (OreDeposit.OreType oreType : OreDeposit.OreType.values()) {
         if (r < oreType.threshold) {
            this.oreType = oreType;
            break;
         }
      }

      if (OreDeposit.OreType.DIAMOND.threshold != 100) {
         BetterMineshaftsCommon.LOGGER.error("Your ore spawn chances don't add up to 100! Ores won't spawn as you intend!");
      }

      if (this.oreType == null) {
         this.oreType = OreDeposit.OreType.COBBLE;
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
      BlockState COBBLE = Blocks.COBBLESTONE.defaultBlockState();
      BlockState ORE_BLOCK = this.oreType.getBlock();
      this.chanceReplaceSolid(world, box, randomSource, 0.9F, 0, 0, 0, 4, 4, 3, COBBLE);
      this.chanceReplaceSolid(world, box, randomSource, 0.65F, 1, 1, 1, 3, 3, 2, ORE_BLOCK);
      this.chanceReplaceSolid(world, box, randomSource, 0.15F, 0, 0, 0, 4, 4, 3, ORE_BLOCK);
   }

   public static enum OreType {
      COBBLE(0, Blocks.COBBLESTONE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.cobble),
      COAL,
      IRON,
      REDSTONE,
      GOLD,
      LAPIS,
      EMERALD,
      DIAMOND;

      private final int value;
      private final BlockState block;
      private final int threshold;

      private OreType(int value, BlockState block, int threshold) {
         this.value = value;
         this.block = block;
         this.threshold = threshold;
      }

      public static OreDeposit.OreType valueOf(int value) {
         return Arrays.stream(values()).filter(oreType -> oreType.value == value).findFirst().get();
      }

      public BlockState getBlock() {
         return this.block;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         COAL = new OreDeposit.OreType(1, Blocks.COAL_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.coal + COBBLE.threshold);
         IRON = new OreDeposit.OreType(2, Blocks.IRON_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.iron + COAL.threshold);
         REDSTONE = new OreDeposit.OreType(3, Blocks.REDSTONE_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.redstone + IRON.threshold);
         GOLD = new OreDeposit.OreType(4, Blocks.GOLD_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.gold + REDSTONE.threshold);
         LAPIS = new OreDeposit.OreType(5, Blocks.LAPIS_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.lapis + GOLD.threshold);
         EMERALD = new OreDeposit.OreType(6, Blocks.EMERALD_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.emerald + LAPIS.threshold);
         DIAMOND = new OreDeposit.OreType(7, Blocks.DIAMOND_ORE.defaultBlockState(), BetterMineshaftsCommon.CONFIG.ores.diamond + EMERALD.threshold);
      }
   }
}
