package com.yungnickyoung.minecraft.betterdeserttemples.module;

import com.yungnickyoung.minecraft.betterdeserttemples.world.placement.BetterDesertTemplePlacement;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

@AutoRegister("betterdeserttemples")
public class StructurePlacementTypeModule {
   @AutoRegister("desert_temple")
   public static final StructurePlacementType<BetterDesertTemplePlacement> BETTER_DESERT_TEMPLE_PLACEMENT = () -> BetterDesertTemplePlacement.CODEC;
}
