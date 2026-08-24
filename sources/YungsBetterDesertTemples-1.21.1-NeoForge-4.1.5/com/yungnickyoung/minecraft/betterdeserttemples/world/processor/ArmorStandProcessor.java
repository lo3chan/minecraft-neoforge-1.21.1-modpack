package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesCommon;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import com.yungnickyoung.minecraft.betterdeserttemples.world.ArmorStandChances;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
public class ArmorStandProcessor extends StructureProcessor {
   public static final ArmorStandProcessor INSTANCE = new ArmorStandProcessor();
   public static final MapCodec<StructureProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureEntityInfo processEntity(
      LevelReader levelReader,
      BlockPos structurePiecePos,
      StructureEntityInfo localEntityInfo,
      StructureEntityInfo globalEntityInfo,
      StructurePlaceSettings structurePlaceSettings,
      StructureTemplate template
   ) {
      if (globalEntityInfo.nbt.getString("id").equals("minecraft:armor_stand")) {
         ListTag armorItems = globalEntityInfo.nbt.getList("ArmorItems", 10);
         RandomSource randomSource = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);

         String helmet;
         try {
            helmet = ((CompoundTag)armorItems.get(3)).get("id").toString();
         } catch (Exception var17) {
            BetterDesertTemplesCommon.LOGGER.info("Unable to randomize armor stand at {}. Missing helmet?", globalEntityInfo.blockPos);
            return globalEntityInfo;
         }

         boolean isArmory = helmet.equals("\"minecraft:iron_helmet\"");
         CompoundTag newNBT = globalEntityInfo.nbt.copy();
         ListTag armorItemsList = newNBT.getList("ArmorItems", 10);
         String bootsString = isArmory
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getArmoryBoots(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getWardrobeBoots(randomSource)).toString();
         if (!bootsString.equals("minecraft:air")) {
            armorItemsList.getCompound(0).putString("id", bootsString);
            armorItemsList.getCompound(0).putByte("Count", (byte)1);
            armorItemsList.getCompound(0).put("tag", (Tag)Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
         }

         String leggingsString = isArmory
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getArmoryLeggings(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getWardrobeLeggings(randomSource)).toString();
         if (!leggingsString.equals("minecraft:air")) {
            armorItemsList.getCompound(1).putString("id", leggingsString);
            armorItemsList.getCompound(1).putByte("Count", (byte)1);
            armorItemsList.getCompound(1).put("tag", (Tag)Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
         }

         String chestplateString = isArmory
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getArmoryChestplate(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getWardrobeChestplate(randomSource)).toString();
         if (!chestplateString.equals("minecraft:air")) {
            armorItemsList.getCompound(2).putString("id", chestplateString);
            armorItemsList.getCompound(2).putByte("Count", (byte)1);
            armorItemsList.getCompound(2).put("tag", (Tag)Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
         }

         String helmetString = isArmory
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getArmoryHelmet(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getWardrobeHelmet(randomSource)).toString();
         if (!helmetString.equals("minecraft:air")) {
            armorItemsList.getCompound(3).putString("id", helmetString);
            armorItemsList.getCompound(3).putByte("Count", (byte)1);
            armorItemsList.getCompound(3).put("tag", (Tag)Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
         }

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
      return StructureProcessorModule.ARMOR_STAND_PROCESSOR;
   }
}
