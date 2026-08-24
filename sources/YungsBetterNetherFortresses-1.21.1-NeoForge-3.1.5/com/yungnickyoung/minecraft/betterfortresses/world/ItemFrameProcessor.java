package com.yungnickyoung.minecraft.betterfortresses.world;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterfortresses.BetterFortressesCommon;
import com.yungnickyoung.minecraft.betterfortresses.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
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
         RandomSource random = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);

         String item;
         try {
            item = globalEntityInfo.nbt.getCompound("Item").get("id").toString();
         } catch (Exception var17) {
            BetterFortressesCommon.LOGGER.info("Unable to randomize item frame at {}", globalEntityInfo.blockPos);
            return globalEntityInfo;
         }

         CompoundTag newNBT = globalEntityInfo.nbt.copy();
         switch (item) {
            case "\"minecraft:stone_sword\"":
               String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getWeaponItem(random)).toString();
               if (randomItemString.equals("minecraft:air")) {
                  return null;
               }

               newNBT.getCompound("Item").putString("id", randomItemString);
               break;
            case "\"minecraft:iron_ingot\"":
               String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getLootItem(random)).toString();
               if (randomItemString.equals("minecraft:air")) {
                  return null;
               }

               newNBT.getCompound("Item").putString("id", randomItemString);
               break;
            case "\"minecraft:cobweb\"":
               String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getStudyItem(random)).toString();
               if (randomItemString.equals("minecraft:air")) {
                  return null;
               }

               if (randomItemString.equals("minecraft:enchanted_book")) {
                  float f = random.nextFloat();
                  String enchantment;
                  if (f < 0.2F) {
                     enchantment = "minecraft:fire_aspect";
                  } else if (f < 0.4F) {
                     enchantment = "minecraft:fire_protection";
                  } else if (f < 0.6F) {
                     enchantment = "minecraft:flame";
                  } else if (f < 0.8F) {
                     enchantment = "minecraft:smite";
                  } else {
                     enchantment = "minecraft:binding_curse";
                  }

                  int lvl;
                  if (!enchantment.equals("minecraft:flame") && !enchantment.equals("minecraft:binding_curse")) {
                     lvl = random.nextFloat() < 0.75F ? 1 : 2;
                  } else {
                     lvl = 1;
                  }

                  CompoundTag componentsTag = newNBT.getCompound("Item").getCompound("components");
                  componentsTag.put(
                     "minecraft:stored_enchantments",
                     (Tag)Util.make(
                        new CompoundTag(),
                        enchantmentsTag -> enchantmentsTag.put("levels", (Tag)Util.make(new CompoundTag(), levelsTag -> levelsTag.putInt(enchantment, lvl)))
                     )
                  );
                  newNBT.getCompound("Item").put("components", componentsTag);
               }

               newNBT.getCompound("Item").putString("id", randomItemString);
               break;
            case "\"minecraft:apple\"":
               String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getMessHallItem(random)).toString();
               if (randomItemString.equals("minecraft:air")) {
                  return null;
               }

               newNBT.getCompound("Item").putString("id", randomItemString);
               break;
            case "\"minecraft:nether_wart\"":
               String randomItemString = BuiltInRegistries.ITEM.getKey(ItemFrameChances.get().getAlchemyItem(random)).toString();
               if (randomItemString.equals("minecraft:air")) {
                  return null;
               }

               newNBT.getCompound("Item").putString("id", randomItemString);
               break;
            case "\"minecraft:glowstone_dust\"":
               if (!random.nextBoolean()) {
                  return null;
               }

               newNBT.getCompound("Item").putString("id", "minecraft:blaze_powder");
         }

         newNBT.putInt("TileX", globalEntityInfo.blockPos.getX());
         newNBT.putInt("TileY", globalEntityInfo.blockPos.getY());
         newNBT.putInt("TileZ", globalEntityInfo.blockPos.getZ());
         int minRotation = item.equals("\"minecraft:chiseled_nether_bricks\"") ? 1 : 0;
         int randomRotation = Mth.randomBetweenInclusive(random, minRotation, 7);
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
      return StructureProcessorTypeModule.ITEM_FRAME_PROCESSOR;
   }
}
