package software.bernie.geckolib.loading.math.function.generic;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class ATan2Function extends MathFunction {
   private final MathValue y;
   private final MathValue x;

   public ATan2Function(MathValue... values) {
      super(values);
      this.y = values[0];
      this.x = values[1];
   }

   @Override
   public String getName() {
      return "math.atan2";
   }

   @Override
   public double compute() {
      return Math.atan2(this.y.get(), this.x.get()) * 57.2957763671875;
   }

   @Override
   public int getMinArgs() {
      return 2;
   }

   @Override
   public MathValue[] getArgs() {
      return new MathValue[]{this.y, this.x};
   }
}
