package software.bernie.geckolib.loading.math.function.random;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.function.MathFunction;

public final class DieRollFunction extends MathFunction {
   private final MathValue rolls;
   private final MathValue min;
   private final MathValue max;
   @Nullable
   private final MathValue seed;
   @Nullable
   private final Random random;

   public DieRollFunction(MathValue... values) {
      super(values);
      this.rolls = values[0];
      this.min = values[1];
      this.max = values[2];
      this.seed = values.length >= 4 ? values[3] : null;
      this.random = this.seed != null ? new Random() : null;
   }

   @Override
   public String getName() {
      return "math.die_roll";
   }

   @Override
   public double compute() {
      int rolls = (int)Math.floor(this.rolls.get());
      double min = this.min.get();
      double max = this.max.get();
      double sum = 0.0;
      Random random;
      if (this.random != null) {
         random = this.random;
         random.setSeed((long)this.seed.get());
      } else {
         random = ThreadLocalRandom.current();
      }

      for (int i = 0; i < rolls; i++) {
         sum += min + random.nextDouble() * (max - min);
      }

      return sum;
   }

   @Override
   public boolean isMutable(MathValue... values) {
      return values.length < 4 ? true : super.isMutable(values);
   }

   @Override
   public int getMinArgs() {
      return 3;
   }

   @Override
   public MathValue[] getArgs() {
      return this.seed != null ? new MathValue[]{this.rolls, this.min, this.max, this.seed} : new MathValue[]{this.rolls, this.min, this.max};
   }
}
