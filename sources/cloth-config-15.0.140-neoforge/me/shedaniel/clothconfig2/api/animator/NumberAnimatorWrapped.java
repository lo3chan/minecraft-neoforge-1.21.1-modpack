package me.shedaniel.clothconfig2.api.animator;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
final class NumberAnimatorWrapped<T extends Number, R extends Number> extends NumberAnimator<T> {
   private final NumberAnimator<R> parent;
   private final Function<R, T> converter;

   NumberAnimatorWrapped(NumberAnimator<R> parent, Function<R, T> converter) {
      this.parent = parent;
      this.converter = converter;
   }

   @Override
   public NumberAnimator<T> setToNumber(Number value, long duration) {
      this.parent.setToNumber(value, duration);
      return this;
   }

   @Override
   public NumberAnimator<T> setTargetNumber(Number value) {
      this.parent.setTargetNumber(value);
      return this;
   }

   public T target() {
      return this.converter.apply(this.parent.target());
   }

   public T value() {
      return this.converter.apply(this.parent.value());
   }

   @Override
   public void update(double delta) {
      this.parent.update(delta);
   }

   @Override
   public int intValue() {
      return this.parent.intValue();
   }

   @Override
   public long longValue() {
      return this.parent.longValue();
   }

   @Override
   public float floatValue() {
      return this.parent.floatValue();
   }

   @Override
   public double doubleValue() {
      return this.parent.doubleValue();
   }
}
