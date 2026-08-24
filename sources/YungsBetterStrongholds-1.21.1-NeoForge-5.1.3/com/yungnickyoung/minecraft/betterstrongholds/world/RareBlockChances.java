package com.yungnickyoung.minecraft.betterstrongholds.world;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RareBlockChances {
   public static RareBlockChances instance;
   private BlockStateRandomizer blockChances = new BlockStateRandomizer(Blocks.IRON_BLOCK.defaultBlockState())
      .addBlock(Blocks.IRON_BLOCK.defaultBlockState(), 0.3F)
      .addBlock(Blocks.QUARTZ_BLOCK.defaultBlockState(), 0.3F)
      .addBlock(Blocks.GOLD_BLOCK.defaultBlockState(), 0.3F)
      .addBlock(Blocks.DIAMOND_BLOCK.defaultBlockState(), 0.1F);

   public static RareBlockChances get() {
      if (instance == null) {
         instance = new RareBlockChances();
      }

      return instance;
   }

   private RareBlockChances() {
   }

   public BlockState getRandomRareBlock(RandomSource randomSource) {
      return this.blockChances.get(randomSource);
   }
}
