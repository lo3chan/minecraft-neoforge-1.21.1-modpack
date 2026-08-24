package software.bernie.geckolib.loading.math.function.limit;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class MaxFunction extends MathFunction {
   private final MathValue valueA;
   private final MathValue valueB;

   public MaxFunction(MathValue... values) {
      super(values);
      this.valueA = values[0];
      this.valueB = values[1];
   }

   @Override
   public String getName() {
      return "math.max";
   }

   @Override
   public double compute() {
      return Math.max(this.valueA.get(), this.valueB.get());
   }

   @Override
   public int getMinArgs() {
      return 2;
   }

   @Override
   public MathValue[] getArgs() {
      return new MathValue[]{this.valueA, this.valueB};
   }
}
