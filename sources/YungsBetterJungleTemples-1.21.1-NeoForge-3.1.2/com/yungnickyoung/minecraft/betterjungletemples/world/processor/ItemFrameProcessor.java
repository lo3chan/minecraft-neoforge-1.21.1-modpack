package com.yungnickyoung.minecraft.betterjungletemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterjungletemples.module.StructureProcessorTypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import org.jetbrains.annotations.Nullable;

public class ItemFrameProcessor extends StructureProcessor {
   public static final ItemFrameProcessor INSTANCE = new ItemFrameProcessor();
   public static final MapCodec<StructureProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureEntityInfo processEntity(
      LevelReader levelReader,
      BlockPos structurePiecePos,
      StructureEntityInfo localEntityInfo,
      StructureEntityInfo globalEntityInfo,
      StructurePlaceSettings structurePlaceSettings,
      StructureTemplate template
   ) {
      if (globalEntityInfo.nbt.getString("id").equals("minecraft:item_frame") || globalEntityInfo.nbt.getString("id").equals("minecraft:glow_item_frame")) {
         globalEntityInfo.nbt.putInt("TileX", globalEntityInfo.blockPos.getX());
         globalEntityInfo.nbt.putInt("TileY", globalEntityInfo.blockPos.getY());
         globalEntityInfo.nbt.putInt("TileZ", globalEntityInfo.blockPos.getZ());
      }

      return globalEntityInfo;
   }

   @Nullable
   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.ITEM_FRAME_PROCESSOR;
   }
}
