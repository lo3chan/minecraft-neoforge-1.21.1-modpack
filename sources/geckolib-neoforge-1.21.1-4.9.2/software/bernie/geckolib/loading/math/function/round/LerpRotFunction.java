package software.bernie.geckolib.loading.math.function.round;

import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;
import software.bernie.geckolib.util.RenderUtil;

public final class LerpRotFunction extends MathFunction {
   private final MathValue min;
   private final MathValue max;
   private final MathValue delta;

   public LerpRotFunction(MathValue... values) {
      super(values);
      this.min = values[0];
      this.max = values[1];
      this.delta = values[2];
   }

   @Override
   public String getName() {
      return "math.lerprotate";
   }

   @Override
   public double compute() {
      return RenderUtil.lerpYaw(this.delta.get(), this.min.get(), this.max.get());
   }

   @Override
   public int getMinArgs() {
      return 3;
   }

   @Override
   public MathValue[] getArgs() {
      return new MathValue[]{this.min, this.max, this.delta};
   }
}
