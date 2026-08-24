package software.bernie.geckolib.loading.math.value;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleSupplier;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.loading.math.MathValue;

public record Variable(String name, AtomicReference<DoubleSupplier> value) implements MathValue {
   public Variable(String name, double value) {
      this(name, () -> value);
   }

   public Variable(String name, DoubleSupplier value) {
      this(name, new AtomicReference<>(value));
   }

   @Override
   public double get() {
      try {
         return this.value.get().getAsDouble();
      } catch (Exception var2) {
         GeckoLibConstants.LOGGER
            .error(
               "Attempted to use Molang variable for incompatible animatable type (" + this.name + "). An animation json needs to be fixed", var2.getMessage()
            );
         return 0.0;
      }
   }

   public void set(double value) {
      this.value.set(() -> value);
   }

   public void set(DoubleSupplier value) {
      this.value.set(value);
   }

   @Override
   public String toString() {
      return this.name + "(" + this.value.get().getAsDouble() + ")";
   }
}
