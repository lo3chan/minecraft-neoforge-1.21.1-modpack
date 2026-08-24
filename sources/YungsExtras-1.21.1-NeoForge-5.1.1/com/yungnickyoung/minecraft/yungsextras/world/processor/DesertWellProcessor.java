package com.yungnickyoung.minecraft.yungsextras.world.processor;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class DesertWellProcessor implements INbtFeatureProcessor {
   private static final ResourceKey<LootTable> EXTRA = ResourceKey.create(
      Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("yungsextras", "desert/extra_archeology")
   );

   @Override
   public void processTemplate(
      StructureTemplate template, WorldGenLevel level, RandomSource random, BlockPos cornerPos, BlockPos centerPos, StructurePlaceSettings placementSettings
   ) {
      List<BlockPos> changedPositions = new ArrayList<>();
      int maxSusSand = random.nextInt(3) + 2;
      int susSandPlaced = 0;

      for (StructureBlockInfo blockInfo : template.filterBlocks(cornerPos, placementSettings, Blocks.BROWN_STAINED_GLASS)) {
         if (susSandPlaced < maxSusSand && random.nextFloat() < 0.1F) {
            this.placeSusSand(level, blockInfo.pos(), BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY);
            susSandPlaced++;
         } else {
            level.setBlock(blockInfo.pos(), Blocks.SAND.defaultBlockState(), 2);
            changedPositions.add(blockInfo.pos());
         }
      }

      if (susSandPlaced < 2) {
         for (BlockPos pos : changedPositions) {
            if (susSandPlaced >= 2) {
               break;
            }

            this.placeSusSand(level, pos, BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY);
            susSandPlaced++;
         }
      }

      int extraSusSand = random.nextInt(3) + 2;

      for (StructureBlockInfo blockInfox : template.filterBlocks(cornerPos, placementSettings, Blocks.YELLOW_STAINED_GLASS)) {
         if (extraSusSand > 0 && random.nextFloat() < 0.4F) {
            this.placeSusSand(level, blockInfox.pos(), EXTRA);
            extraSusSand--;
         } else {
            level.setBlock(blockInfox.pos(), Blocks.SAND.defaultBlockState(), 2);
         }
      }
   }

   private void placeSusSand(WorldGenLevel level, BlockPos pos, ResourceKey<LootTable> lootTable) {
      level.setBlock(pos, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
      level.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent(blockEntity -> blockEntity.setLootTable(lootTable, pos.asLong()));
   }
}
