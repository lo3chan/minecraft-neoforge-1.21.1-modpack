package traben.entity_model_features.models.animation.math.expression_tree;

import java.util.function.BooleanSupplier;

public abstract class MathValue implements MathComponent {
   public static final float TRUE = 1.0F / 0.0F;
   public static final float FALSE = -1.0F / 0.0F;
   boolean isNegative;

   MathValue(boolean isNegative) {
      this.isNegative = isNegative;
   }

   MathValue() {
      this.isNegative = false;
   }

   public static float fromBoolean(boolean value) {
      return value ? 1.0F / 0.0F : -1.0F / 0.0F;
   }

   public static boolean toBoolean(float value) {
      if (value == -1.0F / 0.0F) {
         return false;
      } else if (value == 1.0F / 0.0F) {
         return true;
      } else {
         throw new IllegalArgumentException("Value [" + value + "] is not a boolean");
      }
   }

   public static float validateBoolean(float value) {
      toBoolean(value);
      return value;
   }

   public static float invertBoolean(boolean value) {
      return fromBoolean(!value);
   }

   public static float invertBoolean(float value) {
      return fromBoolean(!toBoolean(value));
   }

   public static float invertBoolean(MathValue.ResultSupplier value) {
      return fromBoolean(!toBoolean(value.get()));
   }

   public static float fromBoolean(BooleanSupplier value) {
      return fromBoolean(value.getAsBoolean());
   }

   public static float invertBoolean(BooleanSupplier value) {
      return invertBoolean(value.getAsBoolean());
   }

   public static boolean isBoolean(float value) {
      return value == 1.0F / 0.0F || value == -1.0F / 0.0F;
   }

   abstract MathValue.ResultSupplier getResultSupplier();

   @Override
   public float getResult() {
      return this.isNegative ? -this.getResultSupplier().get() : this.getResultSupplier().get();
   }

   public interface ResultSupplier {
      float get();
   }
}
