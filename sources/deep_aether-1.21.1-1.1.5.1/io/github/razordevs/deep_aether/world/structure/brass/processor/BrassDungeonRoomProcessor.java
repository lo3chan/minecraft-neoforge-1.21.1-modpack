package io.github.razordevs.deep_aether.world.structure.brass.processor;

import com.aetherteam.nitrogen.entity.BossRoomTracker;
import com.mojang.serialization.MapCodec;
import io.github.razordevs.deep_aether.world.structure.processor.DAStructureProcessor;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import net.minecraft.world.phys.AABB;

public class BrassDungeonRoomProcessor extends StructureProcessor {
   public static final BrassDungeonRoomProcessor INSTANCE = new BrassDungeonRoomProcessor();
   public static final MapCodec<BrassDungeonRoomProcessor> CODEC = MapCodec.unit(INSTANCE);

   public StructureEntityInfo processEntity(
      LevelReader level,
      BlockPos seedPos,
      StructureEntityInfo rawEntityInfo,
      StructureEntityInfo entityInfo,
      StructurePlaceSettings placementSettings,
      StructureTemplate template
   ) {
      BlockPos pos = BlockPos.containing(entityInfo.pos);
      BossRoomTracker<?> tracker = new BossRoomTracker(
         null, entityInfo.pos, new AABB(pos.getX() - 21, pos.getY() - 8, pos.getZ() - 21, pos.getX() + 22, pos.getY() + 50, pos.getZ() + 22), new ArrayList()
      );
      entityInfo.nbt.put("Dungeon", tracker.addAdditionalSaveData());
      return super.processEntity(level, seedPos, rawEntityInfo, entityInfo, placementSettings, template);
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)DAStructureProcessor.BOSS_ROOM.get();
   }
}
