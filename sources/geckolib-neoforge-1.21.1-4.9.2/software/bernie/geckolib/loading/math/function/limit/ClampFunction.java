package software.bernie.geckolib.loading.math.function.limit;

import net.minecraft.util.Mth;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class ClampFunction extends MathFunction {
   private final MathValue value;
   private final MathValue min;
   private final MathValue max;

   public ClampFunction(MathValue... values) {
      super(values);
      this.value = values[0];
      this.min = values[1];
      this.max = values[2];
   }

   @Override
   public String getName() {
      return "math.clamp";
   }

   @Override
   public double compute() {
      return Mth.clamp(this.value.get(), this.min.get(), this.max.get());
   }

   @Override
   public int getMinArgs() {
      return 3;
   }

   @Override
   public MathValue[] getArgs() {
      return new MathValue[]{this.value, this.min, this.max};
   }
}
