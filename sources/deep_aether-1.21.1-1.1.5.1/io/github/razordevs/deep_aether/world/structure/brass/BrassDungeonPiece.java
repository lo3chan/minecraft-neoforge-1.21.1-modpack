package io.github.razordevs.deep_aether.world.structure.brass;

import com.aetherteam.aether.world.structurepiece.AetherTemplateStructurePiece;
import com.google.common.collect.ImmutableList;
import io.github.razordevs.deep_aether.init.DABlocks;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class BrassDungeonPiece extends AetherTemplateStructurePiece {
   public static final RuleProcessor LOCKED_NIMBUS_STONE = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)DABlocks.LOCKED_NIMBUS_STONE.get(), 0.1F),
            AlwaysTrueTest.INSTANCE,
            ((Block)DABlocks.LOCKED_LIGHT_NIMBUS_STONE.get()).defaultBlockState()
         )
      )
   );
   public static final RuleProcessor TRAPPED_SKYROOT_PLANKS = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)DABlocks.LOCKED_SKYROOT_PLANKS.get(), 0.05F),
            AlwaysTrueTest.INSTANCE,
            ((Block)DABlocks.TRAPPED_SKYROOT_PLANKS.get()).defaultBlockState()
         )
      )
   );
   public static final RuleProcessor MOSS_CARPET = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(new RandomBlockMatchTest((Block)DABlocks.AETHER_MOSS_CARPET.get(), 0.2F), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState())
      )
   );
   public static final RuleProcessor FLOWERING_ROSEROOT_LEAVES = new RuleProcessor(
      ImmutableList.of(
         new ProcessorRule(
            new RandomBlockMatchTest((Block)DABlocks.ROSEROOT_LEAVES.get(), 0.2F),
            AlwaysTrueTest.INSTANCE,
            ((Block)DABlocks.FLOWERING_ROSEROOT_LEAVES.get()).defaultBlockState()
         )
      )
   );
   public static final RuleProcessor COBWEB = new RuleProcessor(
      ImmutableList.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.COBWEB, 0.5F), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()))
   );

   public BrassDungeonPiece(
      StructurePieceType type,
      StructureTemplateManager manager,
      String name,
      StructurePlaceSettings settings,
      BlockPos pos,
      Holder<StructureProcessorList> processors
   ) {
      super(type, manager, makeLocation(name), settings, pos, processors);
   }

   public BrassDungeonPiece(
      StructurePieceType type,
      RegistryAccess access,
      CompoundTag tag,
      StructureTemplateManager manager,
      Function<ResourceLocation, StructurePlaceSettings> settingsFactory
   ) {
      super(type, access, tag, manager, settingsFactory);
   }

   protected static ResourceLocation makeLocation(String name) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", "brass_dungeon/" + name);
   }
}
