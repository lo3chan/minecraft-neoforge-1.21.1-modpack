package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.VinesConfig;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class VinesFeature extends Feature<VinesConfig> {
   public static final VinesFeature FEATURE = new VinesFeature();

   public VinesFeature() {
      super(VinesConfig.CODEC);
   }

   public boolean place(FeaturePlaceContext<VinesConfig> context) {
      VinesConfig config = (VinesConfig)context.config();
      WorldGenLevel level = context.level();
      MutableBlockPos pos = context.origin().mutable();
      Optional<Block> states = config.blocks().getRandom(context.random());
      if (states.isEmpty()) {
         return false;
      } else {
         boolean anyPlaced = false;

         for (int i = 0; i < config.maxLength().sample(context.random()) && level.isEmptyBlock(pos); i++) {
            Block vine = states.get();
            boolean placed = false;

            for (Direction direction : Direction.values()) {
               if (direction != Direction.DOWN) {
                  if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction)
                     && config.canPlaceOn(level.getBlockState(pos.relative(direction)))) {
                     level.setBlock(pos, (BlockState)vine.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction), true), 2);
                     placed = true;
                  }

                  BlockState aboveState = level.getBlockState(pos.above());
                  if (aboveState.getBlock() instanceof VineBlock
                     && (
                        (Boolean)aboveState.getValue(VineBlock.NORTH)
                           || (Boolean)aboveState.getValue(VineBlock.EAST)
                           || (Boolean)aboveState.getValue(VineBlock.SOUTH)
                           || (Boolean)aboveState.getValue(VineBlock.WEST)
                     )) {
                     level.setBlock(pos, (BlockState)vine.withPropertiesOf(aboveState).setValue(VineBlock.UP, false), 2);
                     placed = true;
                  }
               }
            }

            if (!placed) {
               break;
            }

            anyPlaced = true;
            pos.move(Direction.DOWN);
         }

         return anyPlaced;
      }
   }
}
