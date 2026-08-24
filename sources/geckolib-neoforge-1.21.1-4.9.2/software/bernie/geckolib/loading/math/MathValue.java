package software.bernie.geckolib.loading.math;

import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface MathValue extends DoubleSupplier {
   double get();

   default boolean isMutable() {
      return true;
   }

   @Internal
   @Override
   default double getAsDouble() {
      return this.get();
   }
}
