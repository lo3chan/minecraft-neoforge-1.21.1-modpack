package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.StateManager;
import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class HiddenNameListOptionEntry<T> implements ListOptionEntry<T> {
   private final ListOptionEntry<T> option;

   public HiddenNameListOptionEntry(ListOptionEntry<T> option) {
      this.option = option;
   }

   @NotNull
   @Override
   public Component name() {
      return Component.empty();
   }

   @NotNull
   @Override
   public OptionDescription description() {
      return this.option.description();
   }

   @Deprecated
   @NotNull
   @Override
   public Component tooltip() {
      return this.option.tooltip();
   }

   @NotNull
   @Override
   public StateManager<T> stateManager() {
      return this.option.stateManager();
   }

   @NotNull
   @Override
   public Controller<T> controller() {
      return this.option.controller();
   }

   @NotNull
   @Override
   public Binding<T> binding() {
      return this.option.binding();
   }

   @Override
   public boolean available() {
      return this.option.available();
   }

   @Override
   public void setAvailable(boolean available) {
      this.option.setAvailable(available);
   }

   @Override
   public ListOption<T> parentGroup() {
      return this.option.parentGroup();
   }

   @NotNull
   @Override
   public ImmutableSet<OptionFlag> flags() {
      return this.option.flags();
   }

   @Override
   public boolean changed() {
      return this.option.changed();
   }

   @NotNull
   @Override
   public T pendingValue() {
      return this.option.pendingValue();
   }

   @Override
   public void requestSet(@NotNull T value) {
      this.option.requestSet(value);
   }

   @Override
   public boolean applyValue() {
      return this.option.applyValue();
   }

   @Override
   public void forgetPendingValue() {
      this.option.forgetPendingValue();
   }

   @Override
   public void requestSetDefault() {
      this.option.requestSetDefault();
   }

   @Override
   public boolean isPendingValueDefault() {
      return this.option.isPendingValueDefault();
   }

   @Override
   public boolean canResetToDefault() {
      return this.option.canResetToDefault();
   }

   @Deprecated
   @Override
   public void addListener(BiConsumer<Option<T>, T> changedListener) {
      this.option.addListener(changedListener);
   }

   @Override
   public void addEventListener(OptionEventListener<T> listener) {
      this.option.addEventListener(listener);
   }
}
