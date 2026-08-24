package io.github.razordevs.deep_aether.world.feature.features;

import com.aetherteam.aether.block.AetherBlocks;
import com.mojang.serialization.Codec;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TotemFeature extends Feature<NoneFeatureConfiguration> {
   private static int excludedValue = 0;

   public TotemFeature(Codec<NoneFeatureConfiguration> pCodec) {
      super(pCodec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel reader = context.level();
      RandomSource rand = context.random();
      BlockPos pos = context.origin();
      int height = rand.nextInt(2, 4);
      Direction direction = getRandomDirectionYExcluded(rand);
      if ((!this.canPlace(reader, pos) || this.canPlace(reader, pos.below())) && rand.nextBoolean()) {
         return false;
      } else {
         for (int i = 0; i < height; i++) {
            if (this.canPlace(reader, pos)) {
               this.setBlock(
                  reader, pos, (BlockState)this.getRandomTotem(rand, false).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction)
               );
               pos = pos.above();
            }
         }

         return true;
      }
   }

   public boolean canPlace(LevelReader reader, BlockPos pos) {
      BlockState state = reader.getBlockState(pos);
      BlockState below = reader.getBlockState(pos.below());
      return (reader.isEmptyBlock(pos) || state.is(BlockTags.LEAVES) || state.canBeReplaced() || !state.isCollisionShapeFullBlock(reader, pos))
         && (below.is(AetherBlocks.AETHER_GRASS_BLOCK) || below.is(DATags.Blocks.TOTEMS));
   }

   public Block getRandomTotem(RandomSource random, boolean log) {
      return switch (this.randomNonRepeatedInteger(random, 4)) {
         case 0 -> (Block)DABlocks.MOA_TOTEM.get();
         case 1 -> (Block)DABlocks.ZEPHYR_TOTEM.get();
         case 2 -> (Block)DABlocks.AERWHALE_TOTEM.get();
         default -> log ? (Block)AetherBlocks.SKYROOT_LOG.get() : (Block)DABlocks.ZEPHYR_TOTEM.get();
      };
   }

   public static Direction getRandomDirectionYExcluded(RandomSource random) {
      return switch (random.nextInt(4)) {
         case 0 -> Direction.NORTH;
         case 1 -> Direction.SOUTH;
         case 2 -> Direction.EAST;
         default -> Direction.WEST;
      };
   }

   private int randomNonRepeatedInteger(RandomSource random, int bound) {
      int choice;
      do {
         choice = random.nextInt(bound);
      } while (choice == excludedValue);

      excludedValue = choice;
      return choice;
   }
}
