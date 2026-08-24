package software.bernie.geckolib.loading.math.function.generic;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class ATanFunction extends MathFunction {
   private final MathValue value;

   public ATanFunction(MathValue... values) {
      super(values);
      this.value = values[0];
   }

   @Override
   public String getName() {
      return "math.atan";
   }

   @Override
   public double compute() {
      return Math.atan(this.value.get() * 0.01745329238474369);
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
