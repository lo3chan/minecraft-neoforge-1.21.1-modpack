package software.bernie.geckolib.loading.math.function.generic;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class SinFunction extends MathFunction {
   private final MathValue value;

   public SinFunction(MathValue... values) {
      super(values);
      this.value = values[0];
   }

   @Override
   public String getName() {
      return "math.sin";
   }

   @Override
   public double compute() {
      return Math.sin(this.value.get() * 0.01745329238474369);
   }

   @Override
   public int getMinArgs() {
      return 1;
   }

   @Override
   public MathValue[] getArgs() {
      return new MathValue[]{this.value};
   }
}
