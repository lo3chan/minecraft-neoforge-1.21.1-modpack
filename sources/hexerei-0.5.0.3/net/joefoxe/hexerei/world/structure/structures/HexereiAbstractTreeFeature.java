package net.joefoxe.hexerei.world.structure.structures;

import com.mojang.serialization.Codec;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class HexereiAbstractTreeFeature extends Feature<NoneFeatureConfiguration> {
   private static final ResourceLocation WILLOW_TREE1 = ResourceLocation.parse("hexerei:willow_tree1");
   private static final ResourceLocation WILLOW_TREE2 = ResourceLocation.parse("hexerei:willow_tree2");
   private static final ResourceLocation WILLOW_TREE3 = ResourceLocation.parse("hexerei:willow_tree3");
   private static final ResourceLocation[] WILLOW_TREE = new ResourceLocation[]{WILLOW_TREE1, WILLOW_TREE2, WILLOW_TREE3};

   public HexereiAbstractTreeFeature(Codec codec) {
      super(codec);
   }

   public static boolean isAirOrLeavesAt(LevelSimulatedReader reader, BlockPos pos) {
      return reader.isStateAtPosition(pos, state -> state.isAir() || state.is(BlockTags.LEAVES));
   }

   public static boolean isAirOrLeavesOrLogsAt(LevelSimulatedReader reader, BlockPos pos) {
      return reader.isStateAtPosition(pos, state -> state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS));
   }

   private static boolean isDirtOrFarmlandAt(LevelSimulatedReader reader, BlockPos pos) {
      return reader.isStateAtPosition(pos, state -> {
         Block block = state.getBlock();
         return isDirt(state) || block == Blocks.FARMLAND;
      });
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel reader = context.level();
      BlockPos pos = context.origin();
      RandomSource rand = context.random();
      int i = rand.nextInt(WILLOW_TREE.length);
      if (!isDirtOrFarmlandAt(reader, pos.below())) {
         return false;
      } else {
         for (int j = 0; j < 8; j++) {
            BlockPos upPos = new BlockPos(pos).above();

            for (int k = 0; k < j; k++) {
               upPos = upPos.above();
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos)) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.north())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.south())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.east())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.east().north())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.east().south())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.west())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.west().north())) {
               return false;
            }

            if (!isAirOrLeavesOrLogsAt(reader, upPos.west().south())) {
               return false;
            }
         }

         if (isAirOrLeavesOrLogsAt(reader, pos.below().north())) {
            return false;
         } else if (isAirOrLeavesOrLogsAt(reader, pos.below().south())) {
            return false;
         } else if (isAirOrLeavesOrLogsAt(reader, pos.below().east())) {
            return false;
         } else if (isAirOrLeavesOrLogsAt(reader, pos.below().west())) {
            return false;
         } else {
            new BlockRotProcessor(0.9F);
            StructureTemplateManager templatemanager = reader.getLevel().getServer().getStructureManager();
            StructureTemplate template = templatemanager.getOrCreate(WILLOW_TREE[i]);
            if (template == null) {
               Hexerei.LOGGER.error("Identifier to the specified nbt file was not found! : {}", WILLOW_TREE[i]);
               return false;
            } else {
               Rotation rotation = Rotation.getRandom(rand);
               BlockPos halfLengths = new BlockPos(template.getSize().getX() / 2, template.getSize().getY() / 2, template.getSize().getZ() / 2);
               MutableBlockPos mutable = new MutableBlockPos().set(pos);
               StructurePlaceSettings placementsettings = new StructurePlaceSettings()
                  .setRotation(rotation)
                  .setRotationPivot(halfLengths)
                  .setIgnoreEntities(false);
               BlockPos pos1 = mutable.set(pos).move(-halfLengths.getX(), 0, -halfLengths.getZ());
               template.placeInWorld(reader, pos1, pos1, placementsettings, rand, 2);
               return true;
            }
         }
      }
   }
}
