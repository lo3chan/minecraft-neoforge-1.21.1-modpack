package com.aetherteam.aether.world.structurepiece.silverdungeon;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class SilverDungeonPiece extends AetherTemplateStructurePiece {
   public static final RuleProcessor LOCKED_ANGELIC_STONE = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(), 0.05F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE.get()).defaultBlockState()
         ),
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.HOLYSTONE.get(), 0.034F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.MOSSY_HOLYSTONE.get()).defaultBlockState()
         )
      )
   );
   public static final RuleProcessor TRAPPED_ANGELIC_STONE = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(), 0.117F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.TRAPPED_ANGELIC_STONE.get()).defaultBlockState()
         ),
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.LOCKED_ANGELIC_STONE.get(), 0.0034F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE.get()).defaultBlockState()
         )
      )
   );

   public SilverDungeonPiece(
      StructurePieceType type,
      StructureTemplateManager manager,
      String name,
      StructurePlaceSettings settings,
      BlockPos pos,
      Holder<StructureProcessorList> processors
   ) {
      super(type, manager, makeLocation(name), settings, pos, processors);
   }

   public SilverDungeonPiece(
      StructurePieceType type,
      RegistryAccess access,
      CompoundTag tag,
      StructureTemplateManager manager,
      Function<ResourceLocation, StructurePlaceSettings> settingsFactory
   ) {
      super(type, access, tag, manager, settingsFactory);
   }

   protected static ResourceLocation makeLocation(String name) {
      return ResourceLocation.fromNamespaceAndPath("aether", "silver_dungeon/" + name);
   }
}
