package io.github.razordevs.deep_aether.world.feature.tree.decorators;

import com.mojang.serialization.MapCodec;
import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context;

public class GlowingTrunkVineDecorator extends TreeDecorator {
   public static final MapCodec<GlowingTrunkVineDecorator> CODEC = MapCodec.unit(() -> GlowingTrunkVineDecorator.INSTANCE);
   public static final GlowingTrunkVineDecorator INSTANCE = new GlowingTrunkVineDecorator();

   protected TreeDecoratorType<?> type() {
      return (TreeDecoratorType<?>)DADecoratorType.GLOWING_TRUNK_VINE.get();
   }

   public void place(Context context) {
      RandomSource randomsource = context.random();
      context.logs().forEach(pos -> {
         if (randomsource.nextInt(3) > 0) {
            BlockPos blockpos = pos.west();
            if (context.isAir(blockpos)) {
               placeVine(blockpos, VineBlock.EAST, context);
            }
         }

         if (randomsource.nextInt(3) > 0) {
            BlockPos blockpos1 = pos.east();
            if (context.isAir(blockpos1)) {
               placeVine(blockpos1, VineBlock.WEST, context);
            }
         }

         if (randomsource.nextInt(3) > 0) {
            BlockPos blockpos2 = pos.north();
            if (context.isAir(blockpos2)) {
               placeVine(blockpos2, VineBlock.SOUTH, context);
            }
         }

         if (randomsource.nextInt(3) > 0) {
            BlockPos blockpos3 = pos.south();
            if (context.isAir(blockpos3)) {
               placeVine(blockpos3, VineBlock.NORTH, context);
            }
         }
      });
   }

   private static Block getRandomVine(RandomSource random) {
      return random.nextInt(5) == 1 ? (Block)DABlocks.GLOWING_VINE.get() : Blocks.VINE;
   }

   private static void placeVine(BlockPos pPos, BooleanProperty pSideProperty, Context context) {
      context.setBlock(pPos, (BlockState)getRandomVine(context.random()).defaultBlockState().setValue(pSideProperty, Boolean.TRUE));
   }
}
