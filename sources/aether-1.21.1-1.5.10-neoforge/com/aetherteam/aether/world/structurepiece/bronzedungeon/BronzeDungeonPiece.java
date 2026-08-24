package com.aetherteam.aether.world.structurepiece.bronzedungeon;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class BronzeDungeonPiece extends AetherTemplateStructurePiece {
   private static final AxisAlignedLinearPosTest ON_FLOOR = new AxisAlignedLinearPosTest(1.0F, 0.0F, 0, 1, Axis.Y);
   public static final ProtectedBlockProcessor AVOID_DUNGEONS = new ProtectedBlockProcessor(AetherTags.Blocks.NON_BRONZE_DUNGEON_REPLACEABLE);
   public static final RuleProcessor LOCKED_SENTRY_STONE = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.LOCKED_CARVED_STONE.get(), 0.05F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.LOCKED_SENTRY_STONE.get()).defaultBlockState()
         )
      )
   );
   public static final RuleProcessor BRONZE_DUNGEON_STONE = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.CARVED_STONE.get(), 0.1F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.SENTRY_STONE.get()).defaultBlockState()
         ),
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.HOLYSTONE.get(), 0.2F),
            AlwaysTrueTest.INSTANCE,
            ((Block)AetherBlocks.MOSSY_HOLYSTONE.get()).defaultBlockState()
         )
      )
   );
   public static final RuleProcessor TRAPPED_CARVED_STONE = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.CARVED_STONE.get(), 0.13F),
            AlwaysTrueTest.INSTANCE,
            ON_FLOOR,
            ((Block)AetherBlocks.TRAPPED_CARVED_STONE.get()).defaultBlockState()
         ),
         new ProcessorRule(
            new RandomBlockMatchTest((Block)AetherBlocks.SENTRY_STONE.get(), 0.003F),
            AlwaysTrueTest.INSTANCE,
            ON_FLOOR,
            ((Block)AetherBlocks.TRAPPED_SENTRY_STONE.get()).defaultBlockState()
         )
      )
   );

   public BronzeDungeonPiece(
      StructurePieceType type,
      StructureTemplateManager manager,
      String name,
      StructurePlaceSettings settings,
      BlockPos pos,
      Holder<StructureProcessorList> processors
   ) {
      this(type, manager, makeLocation(name), settings, pos, processors);
   }

   public BronzeDungeonPiece(
      StructurePieceType type,
      StructureTemplateManager manager,
      ResourceLocation name,
      StructurePlaceSettings settings,
      BlockPos pos,
      Holder<StructureProcessorList> processors
   ) {
      super(type, manager, name, settings, pos, processors);
   }

   public BronzeDungeonPiece(
      StructurePieceType type,
      RegistryAccess access,
      CompoundTag tag,
      StructureTemplateManager manager,
      Function<ResourceLocation, StructurePlaceSettings> settingsFactory
   ) {
      super(type, access, tag, manager, settingsFactory);
   }

   protected static ResourceLocation makeLocation(String name) {
      return ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/" + name);
   }
}
