package dev.worldgen.lithostitched.api.worldgen.bandlands;

import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.BandlandsBuilderImpl;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockState;

public interface BandlandsBuilder {
   static BandlandsBuilder create(BlockState base) {
      return new BandlandsBuilderImpl(base);
   }

   default BandlandsBuilder baseBand(BlockState state) {
      return this.baseBand(UniformInt.of(6, 15), UniformInt.of(1, 3), state);
   }

   BandlandsBuilder baseBand(IntProvider var1, IntProvider var2, BlockState var3);

   BandlandsBuilder repeatBand(IntProvider var1, IntProvider var2, BlockState var3);

   BandlandsBuilder wrappedBand(IntProvider var1, IntProvider var2, float var3, BlockState var4, BlockState var5);

   Bandlands build();
}
