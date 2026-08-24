package software.bernie.geckolib.loading.math.function.misc;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;
import software.bernie.geckolib.loading.math.value.Constant;

public final class PiFunction extends MathFunction {
   public PiFunction(MathValue... values) {
      super(values);
   }

   @Override
   public String getName() {
      return "math.pi";
   }

   @Override
   public double compute() {
      return 3.141592653589793;
   }

   @Override
   public boolean isMutable(MathValue... values) {
      return false;
   }

   @Override
   public int getMinArgs() {
      return 0;
   }

   @Override
   public MathValue[] getArgs() {
      return new MathValue[]{new Constant(3.141592653589793)};
   }
}
