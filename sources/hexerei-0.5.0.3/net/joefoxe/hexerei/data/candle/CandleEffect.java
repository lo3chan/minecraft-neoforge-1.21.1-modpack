package net.joefoxe.hexerei.data.candle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

public interface CandleEffect {
   String EMPTY = "hexerei:no_effect";
   Random random = new Random();
   List<BlockPos> area = (List<BlockPos>)Util.make(() -> {
      List<BlockPos> list = new ArrayList<>();

      for (BlockPos pos : BlockPos.betweenClosed(-3, 0, -3, 3, 3, 3)) {
         list.add(pos.immutable());
      }

      return list;
   });

   void tick(Level var1, CandleTile var2, CandleData var3);

   ParticleOptions getParticleType();

   default BlockPos getRandomPos() {
      return area.get(random.nextInt(area.size()));
   }

   default <T> AbstractCandleEffect getCopy() {
      return new AbstractCandleEffect();
   }

   default String getLocationName() {
      return "hexerei:no_effect";
   }
}
