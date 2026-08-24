package software.bernie.geckolib.loading.math.function.random;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class RandomIntegerFunction extends MathFunction {
   private final MathValue valueA;
   @Nullable
   private final MathValue valueB;
   @Nullable
   private final MathValue seed;
   @Nullable
   private final Random random;

   public RandomIntegerFunction(MathValue... values) {
      super(values);
      this.valueA = values[0];
      this.valueB = values.length >= 2 ? values[1] : null;
      this.seed = values.length >= 3 ? values[2] : null;
      this.random = this.seed != null ? new Random() : null;
   }

   @Override
   public String getName() {
      return "math.random_integer";
   }

   @Override
   public double compute() {
      int valueA = (int)Math.round(this.valueA.get());
      Random random;
      if (this.random != null) {
         this.random.setSeed((long)this.seed.get());
         random = this.random;
      } else {
         random = ThreadLocalRandom.current();
      }

      int result;
      if (this.valueB != null) {
         int valueB = (int)Math.round(this.valueB.get());
         int min = Math.min(valueA, valueB);
         int max = Math.max(valueA, valueB);
         result = min + random.nextInt(max + 1 - min);
      } else {
         result = random.nextInt(0, valueA + 1);
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
