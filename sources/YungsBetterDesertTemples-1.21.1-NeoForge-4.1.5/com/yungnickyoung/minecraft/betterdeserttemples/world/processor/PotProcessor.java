package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PotProcessor extends StructureProcessor {
   public static final PotProcessor INSTANCE = new PotProcessor();
   public static final MapCodec<PotProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.DECORATED_POT) {
         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         CompoundTag newNBT = blockInfoGlobal.nbt() == null ? new CompoundTag() : blockInfoGlobal.nbt();
         ListTag sherds = new ListTag();

         for (int i = 0; i < 4; i++) {
            sherds.add(StringTag.valueOf(this.getRandomSherd(randomSource)));
         }

         newNBT.put("sherds", sherds);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.DECORATED_POT.defaultBlockState(), newNBT);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorModule.POT_PROCESSOR;
   }

   private String getRandomSherd(RandomSource random) {
      float f = random.nextFloat();
      if (f < 0.05F) {
         return "minecraft:archer_pottery_sherd";
      } else if (f < 0.1F) {
         return "minecraft:miner_pottery_sherd";
      } else if (f < 0.15F) {
         return "minecraft:prize_pottery_sherd";
      } else {
         return f < 0.2F ? "minecraft:skull_pottery_sherd" : "minecraft:brick";
      }
   }
}
