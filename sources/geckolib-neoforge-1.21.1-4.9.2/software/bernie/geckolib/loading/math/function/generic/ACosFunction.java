package software.bernie.geckolib.loading.math.function.generic;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class ACosFunction extends MathFunction {
   private final MathValue value;

   public ACosFunction(MathValue... values) {
      super(values);
      this.value = values[0];
   }

   @Override
   public String getName() {
      return "math.acos";
   }

   @Override
   public double compute() {
      return Math.acos((float)this.value.get() * 0.017453292F);
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
