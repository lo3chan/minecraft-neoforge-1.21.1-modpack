package com.yungnickyoung.minecraft.betterstrongholds.world;

import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class OreChances {
   public static OreChances instance;
   private BlockStateRandomizer oreChances = new BlockStateRandomizer(Blocks.COAL_ORE.defaultBlockState())
      .addBlock(Blocks.COAL_ORE.defaultBlockState(), 0.2F)
      .addBlock(Blocks.IRON_ORE.defaultBlockState(), 0.2F)
      .addBlock(Blocks.GOLD_ORE.defaultBlockState(), 0.2F)
      .addBlock(Blocks.LAPIS_ORE.defaultBlockState(), 0.15F)
      .addBlock(Blocks.REDSTONE_ORE.defaultBlockState(), 0.15F)
      .addBlock(Blocks.EMERALD_ORE.defaultBlockState(), 0.05F)
      .addBlock(Blocks.DIAMOND_ORE.defaultBlockState(), 0.05F);

   public static OreChances get() {
      if (instance == null) {
         instance = new OreChances();
      }

      return instance;
   }

   private OreChances() {
   }

   public BlockState getRandomOre(RandomSource randomSource) {
      return this.oreChances.get(randomSource);
   }
}
