package dev.isxander.yacl3.api;

import dev.isxander.yacl3.impl.ImmutableStateManager;
import dev.isxander.yacl3.impl.InstantStateManager;
import dev.isxander.yacl3.impl.SimpleStateManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public interface StateManager<T> {
   static <T> StateManager<T> createSimple(Binding<T> binding) {
      return new SimpleStateManager<>(binding);
   }

   static <T> StateManager<T> createSimple(@NotNull T def, @NotNull Supplier<T> getter, @NotNull Consumer<T> setter) {
      return new SimpleStateManager<>(Binding.generic(def, getter, setter));
   }

   static <T> StateManager<T> createInstant(Binding<T> binding) {
      return new InstantStateManager<>(binding);
   }

   static <T> StateManager<T> createInstant(@NotNull T def, @NotNull Supplier<T> getter, @NotNull Consumer<T> setter) {
      return new InstantStateManager<>(Binding.generic(def, getter, setter));
   }

   static <T> StateManager<T> createImmutable(@NotNull T value) {
      return new ImmutableStateManager<>(value);
   }

   void set(T var1);

   T get();

   void apply();

   void resetToDefault(StateManager.ResetAction var1);

   void sync();

   boolean isSynced();

   default boolean isAlwaysSynced() {
      return false;
   }

   boolean isDefault();

   void addListener(StateManager.StateListener<T> var1);

   public static enum ResetAction {
      BY_OPTION,
      BY_GLOBAL;
   }

   public interface StateListener<T> {
      static <T> StateManager.StateListener<T> noop() {
         return (oldValue, newValue) -> {};
      }

      void onStateChange(T var1, T var2);

      default StateManager.StateListener<T> andThen(StateManager.StateListener<T> after) {
         return (oldValue, newValue) -> {
            this.onStateChange(oldValue, newValue);
            after.onStateChange(oldValue, newValue);
         };
      }
   }
}
