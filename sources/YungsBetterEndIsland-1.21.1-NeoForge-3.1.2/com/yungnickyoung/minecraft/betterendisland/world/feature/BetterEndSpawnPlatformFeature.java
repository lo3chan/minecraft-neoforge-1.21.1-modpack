package com.yungnickyoung.minecraft.betterendisland.world.feature;

import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import com.yungnickyoung.minecraft.betterendisland.world.IBetterDragonFight;
import com.yungnickyoung.minecraft.betterendisland.world.processor.ObsidianProcessor;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BetterEndSpawnPlatformFeature {
   private static final List<StructureProcessor> PROCESSORS = List.of();

   public static boolean place(ServerLevelAccessor level, BlockPos pos, boolean dropDestroyedBlocks) {
      BlockPos origin = pos.offset(0, -14, 0);
      int numberTimesDragonKilled = 0;
      if (level instanceof ServerLevel serverLevel && serverLevel.getDragonFight() != null) {
         numberTimesDragonKilled = ((IBetterDragonFight)serverLevel.getDragonFight()).getNumTimesDragonKilled();
      }

      ResourceLocation template = ResourceLocation.fromNamespaceAndPath("betterendisland", "spawn_platform");
      return placeTemplate(level, RandomSource.create(), origin, template, numberTimesDragonKilled, dropDestroyedBlocks);
   }

   private static boolean placeTemplate(
      ServerLevelAccessor level, RandomSource randomSource, BlockPos centerPos, ResourceLocation id, int numberTimesDragonKilled, boolean destroyBlocks
   ) {
      Optional<StructureTemplate> templateOptional = level.getLevel().getStructureManager().get(id);
      if (templateOptional.isEmpty()) {
         BetterEndIslandCommon.LOGGER.warn("Failed to create invalid feature {}", id);
         return false;
      } else {
         StructureTemplate template = templateOptional.get();
         BlockPos cornerPos = centerPos.offset(-template.getSize().getX() / 2, 0, -template.getSize().getZ() / 2);
         StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings();
         PROCESSORS.forEach(structurePlaceSettings::addProcessor);
         structurePlaceSettings.addProcessor(new ObsidianProcessor(numberTimesDragonKilled));
         structurePlaceSettings.setRotation(Rotation.NONE);
         structurePlaceSettings.setRotationPivot(new BlockPos(3, 0, 3));
         structurePlaceSettings.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
         template.placeInWorld(level, cornerPos, centerPos, structurePlaceSettings, randomSource, 2);
         return true;
      }
   }
}
