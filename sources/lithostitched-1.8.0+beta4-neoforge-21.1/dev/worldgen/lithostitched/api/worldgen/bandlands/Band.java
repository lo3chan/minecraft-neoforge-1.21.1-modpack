package dev.worldgen.lithostitched.api.worldgen.bandlands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface Band {
   Codec<Band> CODEC = Codec.lazyInitialized(() -> {
      Optional<? extends Registry<?>> registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistries.BANDLANDS_BAND_TYPE.location());
      if (registry.isEmpty()) {
         throw new NullPointerException("Bandlands band type registry does not exist yet!");
      } else {
         return registry.get().byNameCodec();
      }
   }).dispatch(Band::codec, Function.identity());

   void fill(BlockState[] var1, RandomSource var2);

   MapCodec<? extends Band> codec();
}
