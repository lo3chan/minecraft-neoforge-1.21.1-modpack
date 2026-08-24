package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesCommon;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import com.yungnickyoung.minecraft.betterdeserttemples.world.ItemFrameChances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
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
      if (globalEntityInfo.nbt.getString("id").equals("minecraft:item_frame")) {
         RandomSource randomSource = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);

         String item;
         try {
            item = globalEntityInfo.nbt.getCompound("Item").get("id").toString();
         } catch (Exception var11) {
            BetterDesertTemplesCommon.LOGGER.info("Unable to randomize item frame at {}", globalEntityInfo.blockPos);
            return globalEntityInfo;
         }

         CompoundTag newNBT = globalEntityInfo.nbt.copy();
         newNBT.putInt("TileX", globalEntityInfo.blockPos.getX());
         newNBT.putInt("TileY", globalEntityInfo.blockPos.getY());
         newNBT.putInt("TileZ", globalEntityInfo.blockPos.getZ());
         if (item.equals("\"minecraft:iron_sword\"")) {
            String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getArmouryItem(randomSource)).toString();
            if (!randomItemString.equals("minecraft:air")) {
               newNBT.getCompound("Item").putString("id", randomItemString);
            } else {
               newNBT.remove("Item");
            }
         } else {
            if (!item.equals("\"minecraft:bread\"")) {
               return new StructureEntityInfo(globalEntityInfo.pos, globalEntityInfo.blockPos, newNBT);
            }

            String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getStorageItem(randomSource)).toString();
            if (!randomItemString.equals("minecraft:air")) {
               newNBT.getCompound("Item").putString("id", randomItemString);
            } else {
               newNBT.remove("Item");
            }
         }

         int randomRotation = randomSource.nextInt(8);
         newNBT.putByte("ItemRotation", (byte)randomRotation);
         globalEntityInfo = new StructureEntityInfo(globalEntityInfo.pos, globalEntityInfo.blockPos, newNBT);
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
      return StructureProcessorModule.ITEM_FRAME_PROCESSOR;
   }
}
