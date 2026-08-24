package com.finndog.moogs_structures.world.structures.terrainadaptation.beardifier;

import com.finndog.moogs_structures.world.structures.terrainadaptation.EnhancedTerrainAdaptation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;

public record EnhancedJigsawJunction(JigsawJunction jigsawJunction, EnhancedTerrainAdaptation pieceTerrainAdaptation) {
}
