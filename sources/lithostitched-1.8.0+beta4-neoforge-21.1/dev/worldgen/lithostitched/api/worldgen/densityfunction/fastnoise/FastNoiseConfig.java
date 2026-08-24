package dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import java.util.function.Function;

public abstract class FastNoiseConfig {
   public static final Codec<FastNoiseConfig> CODEC = LithostitchedBuiltInRegistries.FAST_NOISE_CONFIG_TYPE
      .byNameCodec()
      .dispatch(FastNoiseConfig::getCodec, Function.identity());
   protected final FNL fnl;
   private final float frequency;
   private final int salt;

   public abstract MapCodec<? extends FastNoiseConfig> getCodec();

   protected FastNoiseConfig(float frequency, int salt) {
      this.salt = salt;
      this.fnl = new FNL();
      this.frequency = frequency;
      this.fnl.SetFrequency(frequency);
      this.fnl.SetFractalType(FNL.FractalType.None);
   }

   public float frequency() {
      return this.frequency;
   }

   public int salt() {
      return this.salt;
   }

   public void bind(long seed) {
      this.fnl.SetSeed((int)seed + this.salt);
   }

   public double sample(double x, double y, double z) {
      return this.fnl.GetNoise(x, y, z);
   }
}
