package software.bernie.geckolib.loading.math.value;

import software.bernie.geckolib.loading.math.MathValue;

public record Group(MathValue contents) implements MathValue {
   @Override
   public double get() {
      return this.contents.get();
   }

   @Override
   public boolean isMutable() {
      return this.contents.isMutable();
   }

   @Override
   public String toString() {
      return "(" + this.contents.toString() + ")";
   }
}
