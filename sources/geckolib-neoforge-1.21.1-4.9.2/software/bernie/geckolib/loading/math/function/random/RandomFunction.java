package software.bernie.geckolib.loading.math.function.random;

import java.util.Random;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class RandomFunction extends MathFunction {
   private final MathValue valueA;
   @Nullable
   private final MathValue valueB;
   @Nullable
   private final MathValue seed;
   @Nullable
   private final Random random;

   public RandomFunction(MathValue... values) {
      super(values);
      this.valueA = values[0];
      this.valueB = values.length >= 2 ? values[1] : null;
      this.seed = values.length >= 3 ? values[2] : null;
      this.random = this.seed != null ? new Random() : null;
   }

   @Override
   public String getName() {
      return "math.random";
   }

   @Override
   public double compute() {
      double valueA = this.valueA.get();
      double result;
      if (this.random != null) {
         this.random.setSeed((long)this.seed.get());
         result = this.random.nextDouble();
      } else {
         result = Math.random();
      }

      if (this.valueB != null) {
         double valueB = this.valueB.get();
         double min = Math.min(valueA, valueB);
         double max = Math.max(valueA, valueB);
         result = min + result * (max - min);
      } else {
         result *= valueA;
      }

      return result;
   }

   @Override
   public boolean isMutable(MathValue... values) {
      return values.length < 3 ? true : super.isMutable(values);
   }

   @Override
   public int getMinArgs() {
      return 1;
   }

   @Override
   public MathValue[] getArgs() {
      if (this.seed != null) {
         return new MathValue[]{this.valueA, this.valueB, this.seed};
      } else {
         return this.valueB != null ? new MathValue[]{this.valueA, this.valueB} : new MathValue[]{this.valueA};
      }
   }
}
