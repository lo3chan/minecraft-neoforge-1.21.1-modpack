package com.aetherteam.aether.world.processor;

import com.aetherteam.nitrogen.entity.BossRoomTracker;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import net.minecraft.world.phys.AABB;

public class BossRoomProcessor extends StructureProcessor {
   public static final BossRoomProcessor INSTANCE = new BossRoomProcessor();
   public static final MapCodec<BossRoomProcessor> CODEC = MapCodec.unit(INSTANCE);

   public StructureEntityInfo processEntity(
      LevelReader level,
      BlockPos seedPos,
      StructureEntityInfo rawEntityInfo,
      StructureEntityInfo entityInfo,
      StructurePlaceSettings placementSettings,
      StructureTemplate template
   ) {
      BoundingBox boundingBox = template.getBoundingBox(placementSettings, seedPos);
      BossRoomTracker<?> tracker = new BossRoomTracker(
         null,
         entityInfo.pos,
         new AABB(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(), boundingBox.maxX() + 1, boundingBox.maxY() + 1, boundingBox.maxZ() + 1),
         new ArrayList()
      );
      entityInfo.nbt.put("Dungeon", tracker.addAdditionalSaveData());
      return super.processEntity(level, seedPos, rawEntityInfo, entityInfo, placementSettings, template);
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)AetherStructureProcessors.BOSS_ROOM.get();
   }
}
