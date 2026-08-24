package com.aetherteam.aether.world.structurepiece.golddungeon;

import com.aetherteam.aether.world.structurepiece.AetherStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class GoldIsland extends GoldDungeonPiece {
   public GoldIsland(StructureTemplateManager manager, String name, BlockPos pos, Holder<StructureProcessorList> processors) {
      super((StructurePieceType)AetherStructurePieceTypes.GOLD_ISLAND.get(), manager, name, new StructurePlaceSettings(), pos, processors);
   }

   public GoldIsland(StructurePieceSerializationContext context, CompoundTag tag) {
      super(
         (StructurePieceType)AetherStructurePieceTypes.GOLD_ISLAND.get(),
         context.registryAccess(),
         tag,
         context.structureTemplateManager(),
         resourceLocation -> new StructurePlaceSettings()
      );
   }
}
