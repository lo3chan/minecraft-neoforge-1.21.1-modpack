package com.finndog.moogs_structures.world.structures.terrainadaptation.beardifier;

import com.finndog.moogs_structures.world.structures.terrainadaptation.EnhancedTerrainAdaptation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record EnhancedBeardifierRigid(
   BoundingBox pieceBoundingBox, EnhancedTerrainAdaptation pieceTerrainAdaptation, int pieceGroundLevelDelta, Rotation rotation
) {
}
