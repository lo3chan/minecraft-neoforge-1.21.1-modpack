package dev.isxander.yacl3.config.v3;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public abstract class AbstractReadonlyConfigEntry<T> implements ReadonlyConfigEntry<T> {
   private final String fieldName;
   private Function<T, T> getModifier;

   public AbstractReadonlyConfigEntry(String fieldName) {
      this.fieldName = fieldName;
      this.getModifier = UnaryOperator.identity();
   }

   @Override
   public String fieldName() {
      return this.fieldName;
   }

   @Override
   public T get() {
      return this.getModifier.apply(this.innerGet());
   }

   protected abstract T innerGet();

   @Override
   public ReadonlyConfigEntry<T> modifyGet(UnaryOperator<T> modifier) {
      this.getModifier = this.getModifier.andThen(modifier);
      return this;
   }
}
