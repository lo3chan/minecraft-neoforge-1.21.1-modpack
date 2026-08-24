package software.bernie.geckolib.loading.math.function;

import java.util.StringJoiner;
import software.bernie.geckolib.loading.math.MathValue;

public abstract class MathFunction implements MathValue {
   private final boolean isMutable;
   private double cachedValue = 5.0E-324;

   protected MathFunction(MathValue... values) {
      this.validate(values);
      this.isMutable = this.isMutable(values);
   }

   public abstract String getName();

   @Override
   public final double get() {
      if (this.isMutable) {
         return this.compute();
      } else {
         if (this.cachedValue == 5.0E-324) {
            this.cachedValue = this.compute();
         }

         return this.cachedValue;
      }
   }

   public abstract double compute();

   public boolean isMutable(MathValue... values) {
      for (MathValue value : values) {
         if (value.isMutable()) {
            return true;
         }
      }

      return false;
   }

   public abstract int getMinArgs();

   public abstract MathValue[] getArgs();

   public void validate(MathValue... inputs) throws IllegalArgumentException {
      int minArgs = this.getMinArgs();
      if (inputs.length < minArgs) {
         throw new IllegalArgumentException(String.format("Function '%s' at least %s arguments. Only %s given!", this.getName(), minArgs, inputs.length));
      }
   }

   @Override
   public final boolean isMutable() {
      return this.isMutable;
   }

   @Override
   public String toString() {
      MathValue[] args = this.getArgs();
      StringJoiner joiner = new StringJoiner(", ", "(", ")");

      for (MathValue arg : args) {
         joiner.add(arg.toString());
      }

      return this.getName() + joiner;
   }

   @FunctionalInterface
   public interface Factory<T extends MathFunction> {
      T create(MathValue... var1);
   }
}
