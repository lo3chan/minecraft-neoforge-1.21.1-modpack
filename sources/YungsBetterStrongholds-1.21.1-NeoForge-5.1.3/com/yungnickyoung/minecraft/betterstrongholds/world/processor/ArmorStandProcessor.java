package com.yungnickyoung.minecraft.betterstrongholds.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterstrongholds.BetterStrongholdsCommon;
import com.yungnickyoung.minecraft.betterstrongholds.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.betterstrongholds.world.ArmorStandChances;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
            BetterStrongholdsCommon.LOGGER.info("Unable to randomize armor stand at {}. Missing helmet?", globalEntityInfo.blockPos);
            return globalEntityInfo;
         }

         boolean isRare = helmet.equals("\"minecraft:diamond_helmet\"");
         CompoundTag newNBT = globalEntityInfo.nbt.copy();
         String bootsString = isRare
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getRareBoots(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getCommonBoots(randomSource)).toString();
         if (!bootsString.equals("minecraft:air")) {
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(0)).putString("id", bootsString);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(0)).putByte("Count", (byte)1);
            CompoundTag bootsTagNBT = new CompoundTag();
            bootsTagNBT.putInt("Damage", 0);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(0)).put("tag", bootsTagNBT);
         }

         String leggingsString = isRare
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getRareLeggings(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getCommonLeggings(randomSource)).toString();
         if (!leggingsString.equals("minecraft:air")) {
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(1)).putString("id", leggingsString);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(1)).putByte("Count", (byte)1);
            CompoundTag leggingsTagNBT = new CompoundTag();
            leggingsTagNBT.putInt("Damage", 0);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(1)).put("tag", leggingsTagNBT);
         }

         String chesplateString = isRare
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getRareChestplate(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getCommonChestplate(randomSource)).toString();
         if (!chesplateString.equals("minecraft:air")) {
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(2)).putString("id", chesplateString);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(2)).putByte("Count", (byte)1);
            CompoundTag chestplateTagNBT = new CompoundTag();
            chestplateTagNBT.putInt("Damage", 0);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(2)).put("tag", chestplateTagNBT);
         }

         String helmetString = isRare
            ? BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getRareHelmet(randomSource)).toString()
            : BuiltInRegistries.ITEM.getKey(ArmorStandChances.get().getCommonHelmet(randomSource)).toString();
         if (!helmetString.equals("minecraft:air")) {
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(3)).putString("id", helmetString);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(3)).putByte("Count", (byte)1);
            CompoundTag helmetTagNBT = new CompoundTag();
            helmetTagNBT.putInt("Damage", 0);
            ((CompoundTag)newNBT.getList("ArmorItems", 10).get(3)).put("tag", helmetTagNBT);
            globalEntityInfo = new StructureEntityInfo(globalEntityInfo.pos, globalEntityInfo.blockPos, newNBT);
         }
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
      return StructureProcessorTypeModule.ARMORSTAND_PROCESSOR;
   }
}
