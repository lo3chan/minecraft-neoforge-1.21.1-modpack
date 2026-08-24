package com.finndog.moogs_structures.world.processors;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;

public record StructureProcessingContext(
   ServerLevelAccessor serverLevelAccessor,
   StructurePlaceSettings structurePlaceSettings,
   BlockPos structurePiecePos,
   BlockPos structurePiecePivotPos,
   List<StructureEntityInfo> rawEntityInfos
) {
}
