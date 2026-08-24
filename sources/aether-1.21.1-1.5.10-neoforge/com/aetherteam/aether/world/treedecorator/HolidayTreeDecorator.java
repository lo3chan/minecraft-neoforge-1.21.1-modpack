package com.aetherteam.aether.world.treedecorator;

import com.aetherteam.aether.AetherTags;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context;

public class HolidayTreeDecorator extends TreeDecorator {
   public static final MapCodec<HolidayTreeDecorator> CODEC = BlockStateProvider.CODEC
      .fieldOf("provider")
      .xmap(HolidayTreeDecorator::new, instance -> instance.provider);
   private final BlockStateProvider provider;

   public HolidayTreeDecorator(BlockStateProvider provider) {
      this.provider = provider;
   }

   protected TreeDecoratorType<?> type() {
      return (TreeDecoratorType<?>)AetherTreeDecoratorTypes.HOLIDAY_TREE_DECORATOR.get();
   }

   public void place(Context context) {
      List<BlockPos> logPositions = context.logs();
      if (!logPositions.isEmpty()) {
         int i = ((BlockPos)logPositions.getFirst()).getY();
         logPositions.stream().filter(logs -> logs.getY() == i).forEach(logPos -> this.placeCircle(context, logPos));
      }
   }

   private void placeCircle(Context context, BlockPos pos) {
      LevelSimulatedReader level = context.level();
      RandomSource random = context.random();
      this.placeBlockAt(context, level, random, pos, 0.0F);
      int radius = 10;

      for (int z = 1; z < radius; z++) {
         for (int x = 0; x < radius; x++) {
            if (Mth.square(x) + Mth.square(z) <= Mth.square(radius)) {
               float distance = (float)Math.sqrt(Mth.square(x) + Mth.square(z)) / Mth.square(radius);
               this.placeBlockAt(context, level, random, pos.offset(x, 0, z), distance);
               this.placeBlockAt(context, level, random, pos.offset(-x, 0, -z), distance);
               this.placeBlockAt(context, level, random, pos.offset(-z, 0, x), distance);
               this.placeBlockAt(context, level, random, pos.offset(z, 0, -x), distance);
            }
         }
      }
   }

   private void placeBlockAt(Context context, LevelSimulatedReader level, RandomSource random, BlockPos pos, float distance) {
      for (int i = 9; i >= -4; i--) {
         BlockPos blockPos = pos.above(i);
         if (context.isAir(blockPos.above())
            && (
               level.isStateAtPosition(blockPos, HolidayTreeDecorator::isAetherGrass)
                  || level.isStateAtPosition(blockPos, HolidayTreeDecorator::isLeaves)
                  || Feature.isGrassOrDirt(level, blockPos)
            )
            && context.isAir(blockPos.above(4))
            && distance <= random.nextFloat() / 2.0F * (1.0F - distance)) {
            if (level.isStateAtPosition(blockPos, HolidayTreeDecorator::isLeaves)) {
               context.setBlock(blockPos.above(), Blocks.SNOW.defaultBlockState());
            } else {
               context.setBlock(blockPos.above(), this.provider.getState(random, blockPos));
            }
         }
      }
   }

   private static boolean isAetherGrass(BlockState state) {
      return state.is(AetherTags.Blocks.AETHER_DIRT);
   }

   private static boolean isLeaves(BlockState state) {
      return state.is(BlockTags.LEAVES);
   }
}
