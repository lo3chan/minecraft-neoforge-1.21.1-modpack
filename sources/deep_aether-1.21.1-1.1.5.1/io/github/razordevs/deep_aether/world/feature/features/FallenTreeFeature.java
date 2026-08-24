package io.github.razordevs.deep_aether.world.feature.features;

import com.mojang.serialization.Codec;
import io.github.razordevs.deep_aether.world.feature.features.configuration.FallenTreeConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class FallenTreeFeature extends Feature<FallenTreeConfiguration> {
   public FallenTreeFeature(Codec<FallenTreeConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<FallenTreeConfiguration> context) {
      WorldGenLevel reader = context.level();
      RandomSource rand = context.random();
      BlockPos pos = context.origin();
      FallenTreeConfiguration config = (FallenTreeConfiguration)context.config();
      BlockState block = config.block().getState(context.random(), pos);
      int length = rand.nextInt(config.min(), config.max());
      int gap = rand.nextInt(2, 4);
      int MAX_DEPTH = 4;
      Direction direction = getRandomDirectionYExcluded(rand);
      if (!this.canPlace(reader, pos)) {
         return false;
      } else {
         pos = pos.relative(direction, gap);
         if (this.canPlace(reader, pos.below())) {
            return false;
         } else {
            boolean follow_terrain = rand.nextBoolean();
            if (follow_terrain) {
               BlockPos tempPos = pos;

               for (int i = 0; i < length; i++) {
                  if (this.canPlace(reader, tempPos.relative(direction, i).below()) && this.canPlace(reader, tempPos.relative(direction, i))) {
                     boolean f = false;

                     for (int ii = 1; ii < 4; ii++) {
                        if (!this.canPlace(reader, tempPos.relative(direction, i).below(ii))) {
                           tempPos = tempPos.below(ii - 1);
                           f = true;
                        }
                     }

                     if (!f) {
                        return false;
                     }
                  } else if (!this.canPlace(reader, tempPos.relative(direction, i))) {
                     return false;
                  }
               }
            } else {
               int posWithoutBlockBelow = 0;

               for (int ix = 0; ix < length; ix++) {
                  if (!this.canPlace(reader, pos.relative(direction, ix))) {
                     return false;
                  }

                  if (this.canPlace(reader, pos.relative(direction, ix).below())) {
                     posWithoutBlockBelow++;
                  } else {
                     posWithoutBlockBelow = 0;
                  }

                  if (posWithoutBlockBelow > 2) {
                     return false;
                  }
               }
            }

            this.setBlock(reader, pos.relative(direction.getOpposite(), gap), block);
            if (follow_terrain) {
               for (int ix = 0; ix < length; ix++) {
                  if (this.canPlace(reader, pos.relative(direction, ix).below())) {
                     for (int iix = 1; iix < 4; iix++) {
                        if (!this.canPlace(reader, pos.relative(direction, ix).below(iix))) {
                           pos = pos.below(iix - 1);
                        }
                     }
                  }

                  this.setBlock(reader, pos.relative(direction, ix), (BlockState)block.setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
                  this.addDecorators(reader, pos, config.decorators().getState(context.random(), pos), context.random(), direction);
               }
            } else {
               for (int ix = 0; ix < length; ix++) {
                  this.setBlock(reader, pos.relative(direction, ix), (BlockState)block.setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
                  this.addDecorators(reader, pos, config.decorators().getState(context.random(), pos), context.random(), direction);
               }
            }

            return true;
         }
      }
   }

   public boolean canPlace(LevelReader reader, BlockPos pos) {
      BlockState state = reader.getBlockState(pos);
      return reader.isEmptyBlock(pos) || state.is(BlockTags.LEAVES) || state.canBeReplaced() || !state.isCollisionShapeFullBlock(reader, pos);
   }

   public void addDecorators(WorldGenLevel reader, BlockPos pos, BlockState block, RandomSource random, Direction direction) {
      if (random.nextInt(7) == 1 && this.canPlace(reader, pos.above())) {
         this.setBlock(reader, pos.above(), block);
      }

      if (random.nextInt(4) == 1 && this.canPlace(reader, pos.relative(direction).above())) {
         this.setBlock(reader, pos.relative(direction).above(), block);
      }

      if (random.nextInt(7) == 1
         && this.canPlace(reader, pos.relative(direction.getClockWise()))
         && !this.canPlace(reader, pos.relative(direction.getClockWise()).below())) {
         this.setBlock(reader, pos.relative(direction.getClockWise()), block);
      }

      if (random.nextInt(7) == 1
         && this.canPlace(reader, pos.relative(direction.getCounterClockWise()))
         && !this.canPlace(reader, pos.relative(direction.getCounterClockWise()).below())) {
         this.setBlock(reader, pos.relative(direction.getClockWise()), block);
      }
   }

   public static Direction getRandomDirectionYExcluded(RandomSource random) {
      int a = random.nextInt(3);
      if (a == 0) {
         return Direction.NORTH;
      } else if (a == 1) {
         return Direction.SOUTH;
      } else {
         return a == 2 ? Direction.EAST : Direction.WEST;
      }
   }
}
