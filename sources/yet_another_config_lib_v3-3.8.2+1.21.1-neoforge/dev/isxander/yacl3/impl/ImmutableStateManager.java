package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.StateManager;

public class ImmutableStateManager<T> implements StateManager<T> {
   private final T value;

   public ImmutableStateManager(T value) {
      this.value = value;
   }

   @Override
   public void set(T value) {
      throw new UnsupportedOperationException("Cannot set value of immutable state manager");
   }

   @Override
   public T get() {
      return this.value;
   }

   @Override
   public void apply() {
   }

   @Override
   public void resetToDefault(StateManager.ResetAction action) {
   }

   @Override
   public void sync() {
   }

   @Override
   public boolean isSynced() {
      return true;
   }

   @Override
   public boolean isAlwaysSynced() {
      return true;
   }

   @Override
   public boolean isDefault() {
      return true;
   }

   @Override
   public void addListener(StateManager.StateListener<T> stateListener) {
   }
}
