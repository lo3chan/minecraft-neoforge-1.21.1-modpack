package com.yungnickyoung.minecraft.betterendisland.world;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.SpikeFeature.EndSpike;
import org.jetbrains.annotations.NotNull;

public class SpikeCacheLoader extends CacheLoader<Long, List<EndSpike>> {
   @NotNull
   public List<EndSpike> load(@NotNull Long seed) {
      IntArrayList indexes = Util.toShuffledList(IntStream.range(0, 10), RandomSource.create(seed));
      List<EndSpike> spikes = Lists.newArrayList();
      double radius = BetterEndIslandCommon.betterEnd ? 42.0 : 54.0;

      for (int i = 0; i < 10; i++) {
         int x = Mth.floor(radius * Math.cos(2.0 * (-3.141592653589793 + 0.3141592653589793 * i)));
         int z = Mth.floor(radius * Math.sin(2.0 * (-3.141592653589793 + 0.3141592653589793 * i)));
         int index = indexes.getInt(i);
         int pillarRadius = 2 + index / 3;
         int pillarHeight = 76 + index * 3;
         boolean isGuarded = index == 1 || index == 2;
         spikes.add(new EndSpike(x, z, pillarRadius, pillarHeight, isGuarded));
      }

      return spikes;
   }
}
