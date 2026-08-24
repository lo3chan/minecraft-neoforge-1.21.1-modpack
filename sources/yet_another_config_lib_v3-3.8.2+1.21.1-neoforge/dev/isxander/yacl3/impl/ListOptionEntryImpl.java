package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ListEntryWidget;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ListOptionEntryImpl<T> implements ListOptionEntry<T> {
   private final ListOptionImpl<T> group;
   private T value;
   private final Binding<T> binding;
   private final Controller<T> controller;

   ListOptionEntryImpl(ListOptionImpl<T> group, T initialValue, @NotNull Function<ListOptionEntry<T>, Controller<T>> controlGetter) {
      this.group = group;
      this.value = initialValue;
      this.binding = new ListOptionEntryImpl.EntryBinding();
      this.controller = new ListOptionEntryImpl.EntryController<>(controlGetter.apply(new HiddenNameListOptionEntry<>(this)), this);
   }

   @NotNull
   @Override
   public Component name() {
      return this.group.name();
   }

   @NotNull
   @Override
   public OptionDescription description() {
      return this.group.description();
   }

   @NotNull
   @Override
   public Component tooltip() {
      return this.group.tooltip();
   }

   @NotNull
   @Override
   public Controller<T> controller() {
      return this.controller;
   }

   @NotNull
   @Override
   public StateManager<T> stateManager() {
      throw new UnsupportedOperationException("ListOptionEntryImpl does not support state managers");
   }

   @NotNull
   @Override
   public Binding<T> binding() {
      return this.binding;
   }

   @Override
   public boolean available() {
      return this.parentGroup().available();
   }

   @Override
   public void setAvailable(boolean available) {
   }

   @Override
   public ListOption<T> parentGroup() {
      return this.group;
   }

   @Override
   public boolean changed() {
      return false;
   }

   @NotNull
   @Override
   public T pendingValue() {
      return this.value;
   }

   @Override
   public void requestSet(@NotNull T value) {
      this.binding.setValue(value);
   }

   @Override
   public boolean applyValue() {
      return false;
   }

   @Override
   public void forgetPendingValue() {
   }

   @Override
   public void requestSetDefault() {
   }

   @Override
   public boolean isPendingValueDefault() {
      return false;
   }

   @Override
   public boolean canResetToDefault() {
      return false;
   }

   @Override
   public void addEventListener(OptionEventListener<T> listener) {
   }

   @Override
   public void addListener(BiConsumer<Option<T>, T> changedListener) {
   }

   private class EntryBinding implements Binding<T> {
      @Override
      public void setValue(T newValue) {
         ListOptionEntryImpl.this.value = newValue;
         ListOptionEntryImpl.this.group.triggerListener(OptionEventListener.Event.OTHER, true);
      }

      @Override
      public T getValue() {
         return ListOptionEntryImpl.this.value;
      }

      @Override
      public T defaultValue() {
         throw new UnsupportedOperationException();
      }
   }

   @Internal
   public record EntryController<T>(Controller<T> controller, ListOptionEntryImpl<T> entry) implements Controller<T> {
      @Override
      public Option<T> option() {
         return this.controller.option();
      }

      @Override
      public Component formatValue() {
         return this.controller.formatValue();
      }

      @Override
      public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
         return new ListEntryWidget(screen, this.entry, this.controller.provideWidget(screen, widgetDimension));
      }
   }
}
