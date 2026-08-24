package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.loading.math.MathValue;

public record Negative(MathValue value) implements MathValue {
   @Override
   public double get() {
      return -this.value.get();
   }

   @Override
   public boolean isMutable() {
      return this.value.isMutable();
   }

   @Override
   public String toString() {
      return this.value instanceof Constant ? "-" + this.value : "-(" + this.value + ")";
   }
}
