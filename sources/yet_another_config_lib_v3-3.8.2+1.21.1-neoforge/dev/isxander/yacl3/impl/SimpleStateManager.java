package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.StateManager;

public class SimpleStateManager<T> implements StateManager<T>, ProvidesBindingForDeprecation<T> {
   private T pendingValue;
   private final Binding<T> binding;
   private StateManager.StateListener<T> stateListener;

   public SimpleStateManager(Binding<T> binding) {
      this.binding = binding;
      this.pendingValue = binding.getValue();
      this.stateListener = StateManager.StateListener.noop();
   }

   @Override
   public void set(T value) {
      boolean changed = !this.pendingValue.equals(value);
      this.pendingValue = value;
      if (changed) {
         this.stateListener.onStateChange(this.pendingValue, value);
      }
   }

   @Override
   public T get() {
      return this.pendingValue;
   }

   @Override
   public void apply() {
      this.binding.setValue(this.pendingValue);
   }

   @Override
   public void resetToDefault(StateManager.ResetAction action) {
      this.set(this.binding.defaultValue());
   }

   @Override
   public void sync() {
      this.set(this.binding.getValue());
   }

   @Override
   public boolean isSynced() {
      return this.binding.getValue().equals(this.pendingValue);
   }

   @Override
   public boolean isDefault() {
      return this.binding.defaultValue().equals(this.pendingValue);
   }

   @Override
   public void addListener(StateManager.StateListener<T> stateListener) {
      this.stateListener = this.stateListener.andThen(stateListener);
   }

   @Override
   public Binding<T> getBinding() {
      return this.binding;
   }
}
