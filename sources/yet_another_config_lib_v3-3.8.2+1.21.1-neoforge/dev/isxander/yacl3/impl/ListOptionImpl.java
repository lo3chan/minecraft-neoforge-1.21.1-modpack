package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
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
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ListOptionImpl<T> implements ListOption<T> {
   private final Component name;
   private final OptionDescription description;
   private final StateManager<List<T>> stateManager;
   private final Supplier<T> initialValue;
   private final List<ListOptionEntry<T>> entries;
   private final boolean collapsed;
   private boolean available;
   private final int minimumNumberOfEntries;
   private final int maximumNumberOfEntries;
   private final boolean insertEntriesAtEnd;
   private final ImmutableSet<OptionFlag> flags;
   private final ListOptionImpl<T>.EntryFactory entryFactory;
   private final List<OptionEventListener<List<T>>> listeners;
   private final List<Runnable> refreshListeners;
   private int currentListenerDepth = 0;

   public ListOptionImpl(
      @NotNull Component name,
      @NotNull OptionDescription description,
      @NotNull StateManager<List<T>> stateManager,
      @NotNull Supplier<T> initialValue,
      @NotNull Function<ListOptionEntry<T>, Controller<T>> controllerFunction,
      ImmutableSet<OptionFlag> flags,
      boolean collapsed,
      boolean available,
      int minimumNumberOfEntries,
      int maximumNumberOfEntries,
      boolean insertEntriesAtEnd,
      Collection<OptionEventListener<List<T>>> listeners
   ) {
      this.name = name;
      this.description = description;
      this.stateManager = stateManager;
      this.initialValue = initialValue;
      this.entryFactory = new ListOptionImpl.EntryFactory(controllerFunction);
      this.entries = this.createEntries(this.binding().getValue());
      this.collapsed = collapsed;
      this.flags = flags;
      this.available = available;
      this.minimumNumberOfEntries = minimumNumberOfEntries;
      this.maximumNumberOfEntries = maximumNumberOfEntries;
      this.insertEntriesAtEnd = insertEntriesAtEnd;
      this.listeners = new ArrayList<>();
      this.listeners.addAll(listeners);
      this.refreshListeners = new ArrayList<>();
      this.stateManager.addListener((oldValue, newValue) -> this.triggerListener(OptionEventListener.Event.STATE_CHANGE, false));
      this.triggerListener(OptionEventListener.Event.INITIAL, false);
   }

   @NotNull
   @Override
   public Component name() {
      return this.name;
   }

   @NotNull
   @Override
   public OptionDescription description() {
      return this.description;
   }

   @NotNull
   @Override
   public Component tooltip() {
      return this.description().text();
   }

   @NotNull
   @Override
   public ImmutableList<ListOptionEntry<T>> options() {
      return ImmutableList.copyOf(this.entries);
   }

   @NotNull
   @Override
   public Controller<List<T>> controller() {
      throw new UnsupportedOperationException();
   }

   @NotNull
   @Override
   public StateManager<List<T>> stateManager() {
      return this.stateManager;
   }

   @Deprecated
   @NotNull
   @Override
   public Binding<List<T>> binding() {
      if (this.stateManager instanceof ProvidesBindingForDeprecation) {
         return ((ProvidesBindingForDeprecation)this.stateManager).getBinding();
      } else {
         throw new UnsupportedOperationException(
            "Binding is not available for this option - using a new state manager which does not directly expose the binding as it may not have one."
         );
      }
   }

   @Override
   public boolean collapsed() {
      return this.collapsed;
   }

   @NotNull
   @Override
   public ImmutableSet<OptionFlag> flags() {
      return this.flags;
   }

   @NotNull
   public ImmutableList<T> pendingValue() {
      return ImmutableList.copyOf(this.entries.stream().map(Option::pendingValue).toList());
   }

   @Override
   public void insertEntry(int index, ListOptionEntry<?> entry) {
      this.entries.add(index, (ListOptionEntry<T>)entry);
      this.onRefresh();
   }

   @Override
   public ListOptionEntry<T> insertNewEntry() {
      ListOptionEntry<T> newEntry = this.entryFactory.create(this.initialValue.get());
      if (this.insertEntriesAtEnd) {
         this.entries.add(newEntry);
      } else {
         this.entries.add(0, newEntry);
      }

      this.onRefresh();
      return newEntry;
   }

   @Override
   public void removeEntry(ListOptionEntry<?> entry) {
      if (this.entries.remove(entry)) {
         this.onRefresh();
      }
   }

   @Override
   public int indexOf(ListOptionEntry<?> entry) {
      return this.entries.indexOf(entry);
   }

   public void requestSet(@NotNull List<T> value) {
      this.entries.clear();
      this.entries.addAll(this.createEntries(value));
      this.onRefresh();
   }

   @Override
   public boolean changed() {
      return !this.binding().getValue().equals(this.pendingValue());
   }

   @Override
   public boolean applyValue() {
      if (this.changed()) {
         this.binding().setValue(this.pendingValue());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void forgetPendingValue() {
      this.requestSet(this.binding().getValue());
   }

   @Override
   public void requestSetDefault() {
      this.requestSet(this.binding().defaultValue());
   }

   @Override
   public boolean isPendingValueDefault() {
      return this.binding().defaultValue().equals(this.pendingValue());
   }

   @Override
   public boolean available() {
      return this.available;
   }

   @Override
   public void setAvailable(boolean available) {
      boolean changed = this.available != available;
      this.available = available;
      if (changed) {
         if (!available) {
            this.stateManager.sync();
         }

         this.triggerListener(OptionEventListener.Event.AVAILABILITY_CHANGE, !available);
      }
   }

   @Override
   public int numberOfEntries() {
      return this.entries.size();
   }

   @Override
   public int maximumNumberOfEntries() {
      return this.maximumNumberOfEntries;
   }

   @Override
   public int minimumNumberOfEntries() {
      return this.minimumNumberOfEntries;
   }

   @Override
   public void addEventListener(OptionEventListener<List<T>> listener) {
      this.listeners.add(listener);
   }

   @Deprecated
   @Override
   public void addListener(BiConsumer<Option<List<T>>, List<T>> changedListener) {
      this.addEventListener((opt, event) -> changedListener.accept((T)opt, opt.pendingValue()));
   }

   @Override
   public void addRefreshListener(Runnable changedListener) {
      this.refreshListeners.add(changedListener);
   }

   @Override
   public boolean isRoot() {
      return false;
   }

   private List<ListOptionEntry<T>> createEntries(Collection<T> values) {
      return values.stream().map(this.entryFactory::create).collect(Collectors.toList());
   }

   void triggerListener(OptionEventListener.Event event, boolean allowDepth) {
      if (allowDepth || this.currentListenerDepth == 0) {
         Validate.isTrue(
            this.currentListenerDepth <= 10,
            "Listener depth exceeded 10! Possible cyclic listener pattern: a listener triggered an event that triggered the initial event etc etc.",
            new Object[0]
         );
         this.currentListenerDepth++;

         for (OptionEventListener<List<T>> listener : this.listeners) {
            listener.onEvent(this, event);
         }

         this.currentListenerDepth--;
      }
   }

   private void onRefresh() {
      this.refreshListeners.forEach(Runnable::run);
      this.triggerListener(OptionEventListener.Event.OTHER, true);
   }

   @Internal
   public static final class BuilderImpl<T> implements ListOption.Builder<T> {
      private Component name = Component.empty();
      private OptionDescription description = OptionDescription.EMPTY;
      private Function<ListOptionEntry<T>, Controller<T>> controllerFunction;
      private final Set<OptionFlag> flags = new HashSet<>();
      private Supplier<T> initialValue;
      private boolean collapsed = false;
      private boolean available = true;
      private int minimumNumberOfEntries = 0;
      private int maximumNumberOfEntries = 2147483647;
      private boolean insertEntriesAtEnd = false;
      private final List<OptionEventListener<List<T>>> listeners = new ArrayList<>();
      private Binding<List<T>> binding;
      private StateManager<List<T>> stateManager;

      @Override
      public ListOption.Builder<T> name(@NotNull Component name) {
         Validate.notNull(name, "`name` must not be null", new Object[0]);
         this.name = name;
         return this;
      }

      @Override
      public ListOption.Builder<T> description(@NotNull OptionDescription description) {
         Validate.notNull(description, "`description` must not be null", new Object[0]);
         this.description = description;
         return this;
      }

      @Override
      public ListOption.Builder<T> initial(@NotNull Supplier<T> initialValue) {
         Validate.notNull(initialValue, "`initialValue` cannot be empty", new Object[0]);
         this.initialValue = initialValue;
         return this;
      }

      @Override
      public ListOption.Builder<T> initial(@NotNull T initialValue) {
         Validate.notNull(initialValue, "`initialValue` cannot be empty", new Object[0]);
         this.initialValue = () -> initialValue;
         return this;
      }

      @Override
      public ListOption.Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> controller) {
         Validate.notNull(controller, "`controller` cannot be null", new Object[0]);
         this.controllerFunction = opt -> controller.apply(opt).build();
         return this;
      }

      @Override
      public ListOption.Builder<T> customController(@NotNull Function<ListOptionEntry<T>, Controller<T>> control) {
         Validate.notNull(control, "`control` cannot be null", new Object[0]);
         this.controllerFunction = control;
         return this;
      }

      @Override
      public ListOption.Builder<T> state(@NotNull StateManager<List<T>> stateManager) {
         Validate.notNull(stateManager, "`stateManager` cannot be null", new Object[0]);
         Validate.isTrue(this.binding == null, "Cannot set state manager if binding is already set", new Object[0]);
         this.stateManager = stateManager;
         return this;
      }

      @Override
      public ListOption.Builder<T> binding(@NotNull Binding<List<T>> binding) {
         Validate.notNull(binding, "`binding` cannot be null", new Object[0]);
         Validate.isTrue(this.stateManager == null, "Cannot set binding if state manager is already set", new Object[0]);
         this.binding = binding;
         return this;
      }

      @Override
      public ListOption.Builder<T> binding(@NotNull List<T> def, @NotNull Supplier<List<T>> getter, @NotNull Consumer<List<T>> setter) {
         Validate.notNull(def, "`def` must not be null", new Object[0]);
         Validate.notNull(getter, "`getter` must not be null", new Object[0]);
         Validate.notNull(setter, "`setter` must not be null", new Object[0]);
         this.binding = Binding.generic(def, getter, setter);
         return this;
      }

      @Override
      public ListOption.Builder<T> available(boolean available) {
         this.available = available;
         return this;
      }

      @Override
      public ListOption.Builder<T> minimumNumberOfEntries(int number) {
         this.minimumNumberOfEntries = number;
         return this;
      }

      @Override
      public ListOption.Builder<T> maximumNumberOfEntries(int number) {
         this.maximumNumberOfEntries = number;
         return this;
      }

      @Override
      public ListOption.Builder<T> insertEntriesAtEnd(boolean insertAtEnd) {
         this.insertEntriesAtEnd = insertAtEnd;
         return this;
      }

      @Override
      public ListOption.Builder<T> flag(@NotNull OptionFlag... flag) {
         Validate.notNull(flag, "`flag` must not be null", new Object[0]);
         this.flags.addAll(Arrays.asList(flag));
         return this;
      }

      @Override
      public ListOption.Builder<T> flags(@NotNull Collection<OptionFlag> flags) {
         Validate.notNull(flags, "`flags` must not be null", new Object[0]);
         this.flags.addAll(flags);
         return this;
      }

      @Override
      public ListOption.Builder<T> collapsed(boolean collapsible) {
         this.collapsed = collapsible;
         return this;
      }

      @Override
      public ListOption.Builder<T> addListener(@NotNull OptionEventListener<List<T>> listener) {
         Validate.notNull(listener, "`listener` must not be null", new Object[0]);
         this.listeners.add(listener);
         return this;
      }

      @Override
      public ListOption.Builder<T> addListeners(@NotNull Collection<OptionEventListener<List<T>>> optionEventListeners) {
         Validate.notNull(optionEventListeners, "`optionEventListeners` must not be null", new Object[0]);
         this.listeners.addAll(optionEventListeners);
         return this;
      }

      @Override
      public ListOption.Builder<T> listener(@NotNull BiConsumer<Option<List<T>>, List<T>> listener) {
         Validate.notNull(listener, "`listener` must not be null", new Object[0]);
         return this.addListener((opt, event) -> listener.accept((T)opt, opt.pendingValue()));
      }

      @Override
      public ListOption.Builder<T> listeners(@NotNull Collection<BiConsumer<Option<List<T>>, List<T>>> listeners) {
         Validate.notNull(listeners, "`listeners` must not be null", new Object[0]);
         this.addListeners(listeners.stream().map(listener -> (opt, event) -> listener.accept((T)opt, opt.pendingValue())).toList());
         return this;
      }

      @Override
      public ListOption<T> build() {
         Validate.notNull(this.controllerFunction, "`controller` must not be null", new Object[0]);
         Validate.notNull(this.initialValue, "`initialValue` must not be null", new Object[0]);
         Validate.isTrue(this.stateManager != null || this.binding != null, "Either a state manager or binding must be set", new Object[0]);
         if (this.stateManager == null) {
            this.stateManager = StateManager.createSimple(this.binding);
         }

         return new ListOptionImpl<>(
            this.name,
            this.description,
            this.stateManager,
            this.initialValue,
            this.controllerFunction,
            ImmutableSet.copyOf(this.flags),
            this.collapsed,
            this.available,
            this.minimumNumberOfEntries,
            this.maximumNumberOfEntries,
            this.insertEntriesAtEnd,
            this.listeners
         );
      }
   }

   private class EntryFactory {
      private final Function<ListOptionEntry<T>, Controller<T>> controllerFunction;

      private EntryFactory(Function<ListOptionEntry<T>, Controller<T>> controllerFunction) {
         this.controllerFunction = controllerFunction;
      }

      public ListOptionEntry<T> create(T initialValue) {
         return new ListOptionEntryImpl<>(ListOptionImpl.this, initialValue, this.controllerFunction);
      }
   }
}
