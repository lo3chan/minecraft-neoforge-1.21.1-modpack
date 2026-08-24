package corgitaco.corgilib.math.blendingfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.core.CorgiLibRegistry;
import corgitaco.corgilib.platform.ModPlatform;

public interface BlendingFunction {
   Codec<BlendingFunction> CODEC = Codec.lazyInitialized(
      () -> CorgiLibRegistry.BLENDING_FUNCTION.get().byNameCodec().dispatchStable(BlendingFunction::codec, MapCodec::assumeMapUnsafe)
   );

   Codec<? extends BlendingFunction> codec();

   double apply(double var1);

   default double apply(double factor, double min, double max) {
      double range = max - min;
      return min + range * this.apply(factor);
   }

   static void register() {
      register("ease_in_out_circ", BlendingFunction.EaseInOutCirc.CODEC);
      register("ease_out_bounce", BlendingFunction.EaseOutBounce.CODEC);
      register("ease_out_cubic", BlendingFunction.EaseOutCubic.CODEC);
      register("ease_out_elastic", BlendingFunction.EaseOutElastic.CODEC);
      register("ease_in_circ", BlendingFunction.EaseInCirc.CODEC);
      register("ease_out_quint", BlendingFunction.EaseOutQuint.CODEC);
   }

   private static void register(String name, Codec<? extends BlendingFunction> function) {
      ModPlatform.PLATFORM.register(CorgiLibRegistry.BLENDING_FUNCTION.get(), name, () -> function);
   }

   public record EaseInCirc(double exponent) implements BlendingFunction {
      public static final BlendingFunction.EaseInCirc INSTANCE = new BlendingFunction.EaseInCirc(2.0);
      public static final Codec<BlendingFunction.EaseInCirc> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(Codec.DOUBLE.fieldOf("exponent").forGetter(BlendingFunction.EaseInCirc::exponent))
            .apply(instance, BlendingFunction.EaseInCirc::new)
      );

      @Override
      public double apply(double factor) {
         return BlendingFunctions.easeInCirc(factor, this.exponent);
      }

      @Override
      public Codec<? extends BlendingFunction> codec() {
         return CODEC;
      }
   }

   public record EaseInOutCirc() implements BlendingFunction {
      public static final BlendingFunction.EaseInOutCirc INSTANCE = new BlendingFunction.EaseInOutCirc();
      public static final Codec<BlendingFunction.EaseInOutCirc> CODEC = Codec.unit(() -> INSTANCE);

      @Override
      public double apply(double factor) {
         return BlendingFunctions.easeInOutCirc(factor);
      }

      @Override
      public Codec<? extends BlendingFunction> codec() {
         return CODEC;
      }
   }

   public record EaseOutBounce() implements BlendingFunction {
      public static final BlendingFunction.EaseOutBounce INSTANCE = new BlendingFunction.EaseOutBounce();
      public static final Codec<BlendingFunction.EaseOutBounce> CODEC = Codec.unit(() -> INSTANCE);

      @Override
      public double apply(double factor) {
         return BlendingFunctions.easeOutBounce(factor);
      }

      @Override
      public Codec<? extends BlendingFunction> codec() {
         return CODEC;
      }
   }

   public record EaseOutCubic() implements BlendingFunction {
      public static final BlendingFunction.EaseOutCubic INSTANCE = new BlendingFunction.EaseOutCubic();
      public static final Codec<BlendingFunction.EaseOutCubic> CODEC = Codec.unit(() -> INSTANCE);

      @Override
      public double apply(double factor) {
         return BlendingFunctions.easeOutCubic(factor);
      }

      @Override
      public Codec<? extends BlendingFunction> codec() {
         return CODEC;
      }
   }

   public record EaseOutElastic(double intensity) implements BlendingFunction {
      public static final BlendingFunction.EaseOutElastic INSTANCE = new BlendingFunction.EaseOutElastic(10.0);
      public static final Codec<BlendingFunction.EaseOutElastic> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(Codec.DOUBLE.fieldOf("intensity").forGetter(BlendingFunction.EaseOutElastic::intensity))
            .apply(builder, BlendingFunction.EaseOutElastic::new)
      );

      @Override
      public double apply(double factor) {
         return BlendingFunctions.easeOutElastic(factor, this.intensity);
      }

      @Override
      public Codec<? extends BlendingFunction> codec() {
         return CODEC;
      }
   }

   public record EaseOutQuint() implements BlendingFunction {
      public static final BlendingFunction.EaseOutQuint INSTANCE = new BlendingFunction.EaseOutQuint();
      public static final Codec<BlendingFunction.EaseOutQuint> CODEC = Codec.unit(() -> INSTANCE);

      @Override
      public double apply(double factor) {
         return BlendingFunctions.easeOutQuint(factor);
      }

      @Override
      public Codec<? extends BlendingFunction> codec() {
         return CODEC;
      }
   }
}
