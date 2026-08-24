package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record EnhancedBeardifierRigid(
   BoundingBox pieceBoundingBox, EnhancedTerrainAdaptation pieceTerrainAdaptation, int pieceGroundLevelDelta, Rotation rotation
) {
}
