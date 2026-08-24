package io.github.razordevs.deep_aether.world.feature.tree.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.razordevs.deep_aether.block.natural.YagrootVineBlock;
import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context;

public class YagrootVineDecorator extends TreeDecorator {
   public static final MapCodec<YagrootVineDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
      .fieldOf("probability")
      .xmap(YagrootVineDecorator::new, vineDecorator -> vineDecorator.probability)
      .stable();
   private final float probability;

   protected TreeDecoratorType<?> type() {
      return (TreeDecoratorType<?>)DADecoratorType.YAGVINES.get();
   }

   public YagrootVineDecorator(float probability) {
      this.probability = probability;
   }

   public void place(Context context) {
      RandomSource randomsource = context.random();
      context.leaves().forEach(blockPos -> {
         if (randomsource.nextFloat() < this.probability) {
            BlockPos blockpos = blockPos.west();
            if (context.isAir(blockpos)) {
               this.addHangingVine(blockpos, VineBlock.EAST, context);
            }
         }

         if (randomsource.nextFloat() < this.probability) {
            BlockPos blockpos1 = blockPos.east();
            if (context.isAir(blockpos1)) {
               this.addHangingVine(blockpos1, VineBlock.WEST, context);
            }
         }

         if (randomsource.nextFloat() < this.probability) {
            BlockPos blockpos2 = blockPos.north();
            if (context.isAir(blockpos2)) {
               this.addHangingVine(blockpos2, VineBlock.SOUTH, context);
            }
         }

         if (randomsource.nextFloat() < this.probability) {
            BlockPos blockpos3 = blockPos.south();
            if (context.isAir(blockpos3)) {
               this.addHangingVine(blockpos3, VineBlock.NORTH, context);
            }
         }
      });
   }

   private void addHangingVine(BlockPos blockPos, BooleanProperty property, Context context) {
      this.placeVine(blockPos, property, context, !context.isAir(blockPos.below()));
      int i = 4;

      for (BlockPos blockpos = blockPos.below(); context.isAir(blockpos) && i > 0; i--) {
         this.placeVine(blockpos, property, context, !context.isAir(blockPos.below()));
         blockpos = blockpos.below();
      }
   }

   public void placeVine(BlockPos blockPos, BooleanProperty property, Context context, Boolean isBottom) {
      context.setBlock(
         blockPos,
         (BlockState)((BlockState)((Block)DABlocks.YAGROOT_VINE.get()).defaultBlockState().setValue(property, Boolean.TRUE))
            .setValue(YagrootVineBlock.BOTTOM, isBottom)
      );
   }
}
