package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class OptionImpl<T> implements Option<T> {
   private final Component name;
   private OptionDescription description;
   private final Controller<T> controller;
   private boolean available;
   private final ImmutableSet<OptionFlag> flags;
   private final StateManager<T> stateManager;
   private final List<OptionEventListener<T>> listeners;
   private int currentListenerDepth;

   public OptionImpl(
      @NotNull Component name,
      @NotNull Function<T, OptionDescription> descriptionFunction,
      @NotNull Function<Option<T>, Controller<T>> controlGetter,
      @NotNull StateManager<T> stateManager,
      boolean available,
      ImmutableSet<OptionFlag> flags,
      @NotNull Collection<OptionEventListener<T>> listeners
   ) {
      this.name = name;
      this.available = available;
      this.flags = flags;
      this.listeners = new ArrayList<>(listeners);
      this.stateManager = stateManager;
      this.controller = controlGetter.apply(this);
      this.stateManager.addListener((oldValue, newValue) -> this.triggerListener(OptionEventListener.Event.STATE_CHANGE, false));
      this.addEventListener((opt, event) -> this.description = descriptionFunction.apply(opt.pendingValue()));
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
      return this.description.text();
   }

   @NotNull
   @Override
   public Controller<T> controller() {
      return this.controller;
   }

   @NotNull
   @Override
   public StateManager<T> stateManager() {
      return this.stateManager;
   }

   @Deprecated
   @NotNull
   @Override
   public Binding<T> binding() {
      if (this.stateManager instanceof ProvidesBindingForDeprecation) {
         return ((ProvidesBindingForDeprecation)this.stateManager).getBinding();
      } else {
         throw new UnsupportedOperationException(
            "Binding is not available for this option - using a new state manager which does not directly expose the binding as it may not have one."
         );
      }
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

   @NotNull
   @Override
   public ImmutableSet<OptionFlag> flags() {
      return this.flags;
   }

   @Override
   public boolean changed() {
      return !this.stateManager.isSynced();
   }

   @NotNull
   @Override
   public T pendingValue() {
      return this.stateManager.get();
   }

   @Override
   public void requestSet(@NotNull T value) {
      Validate.notNull(value, "`value` cannot be null", new Object[0]);
      this.stateManager.set(value);
   }

   @Override
   public boolean applyValue() {
      if (this.changed()) {
         this.stateManager.apply();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void forgetPendingValue() {
      this.stateManager.sync();
   }

   @Override
   public void requestSetDefault() {
      this.stateManager.resetToDefault(StateManager.ResetAction.BY_OPTION);
   }

   @Override
   public boolean isPendingValueDefault() {
      return this.stateManager.isDefault();
   }

   @Override
   public void addEventListener(OptionEventListener<T> listener) {
      this.listeners.add(listener);
   }

   @Deprecated
   @Override
   public void addListener(BiConsumer<Option<T>, T> changedListener) {
      this.addEventListener((opt, event) -> changedListener.accept(opt, opt.pendingValue()));
   }

   private void triggerListener(OptionEventListener.Event event, boolean allowDepth) {
      if (allowDepth || this.currentListenerDepth == 0) {
         Validate.isTrue(
            this.currentListenerDepth <= 10,
            "Listener depth exceeded 10! Possible cyclic listener pattern: a listener triggered an event that triggered the initial event etc etc.",
            new Object[0]
         );
         this.currentListenerDepth++;

         for (OptionEventListener<T> listener : this.listeners) {
            listener.onEvent(this, event);
         }

         this.currentListenerDepth--;
      }
   }

   @Internal
   public static class BuilderImpl<T> implements Option.Builder<T> {
      private Component name = Component.literal("Name not specified!").withStyle(ChatFormatting.RED);
      private Function<T, OptionDescription> descriptionFunction = pending -> OptionDescription.EMPTY;
      private Function<Option<T>, Controller<T>> controlGetter;
      private boolean available = true;
      private final Set<OptionFlag> flags = new HashSet<>();
      private final List<OptionEventListener<T>> listeners = new ArrayList<>();
      @Nullable
      private Binding<T> binding;
      private boolean instantDeprecated = false;
      @Nullable
      private StateManager<T> stateManager;

      @Override
      public Option.Builder<T> name(@NotNull Component name) {
         Validate.notNull(name, "`name` cannot be null", new Object[0]);
         this.name = name;
         return this;
      }

      @Override
      public Option.Builder<T> description(@NotNull OptionDescription description) {
         return this.description(opt -> description);
      }

      @Override
      public Option.Builder<T> description(@NotNull Function<T, OptionDescription> descriptionFunction) {
         this.descriptionFunction = descriptionFunction;
         return this;
      }

      @Override
      public Option.Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> controllerBuilder) {
         Validate.notNull(controllerBuilder, "`controllerBuilder` cannot be null", new Object[0]);
         return this.customController(opt -> controllerBuilder.apply(opt).build());
      }

      @Override
      public Option.Builder<T> customController(@NotNull Function<Option<T>, Controller<T>> control) {
         Validate.notNull(control, "`control` cannot be null", new Object[0]);
         this.controlGetter = control;
         return this;
      }

      @Override
      public Option.Builder<T> stateManager(@NotNull StateManager<T> stateManager) {
         Validate.notNull(stateManager, "`stateManager` cannot be null", new Object[0]);
         Validate.isTrue(this.binding == null, "Cannot set state manager when binding is set", new Object[0]);
         Validate.isTrue(!this.instantDeprecated, "Cannot set state manager when instant is set", new Object[0]);
         this.stateManager = stateManager;
         return this;
      }

      @Override
      public Option.Builder<T> binding(@NotNull Binding<T> binding) {
         Validate.notNull(binding, "`binding` cannot be null", new Object[0]);
         Validate.isTrue(this.stateManager == null, "Cannot set binding when state manager is set", new Object[0]);
         this.binding = binding;
         return this;
      }

      @Override
      public Option.Builder<T> binding(@NotNull T def, @NotNull Supplier<T> getter, @NotNull Consumer<T> setter) {
         Validate.notNull(def, "`def` must not be null", new Object[0]);
         Validate.notNull(getter, "`getter` must not be null", new Object[0]);
         Validate.notNull(setter, "`setter` must not be null", new Object[0]);
         return this.binding(Binding.generic(def, getter, setter));
      }

      @Override
      public Option.Builder<T> available(boolean available) {
         this.available = available;
         return this;
      }

      @Override
      public Option.Builder<T> flag(@NotNull OptionFlag... flag) {
         Validate.notNull(flag, "`flag` must not be null", new Object[0]);
         this.flags.addAll(Arrays.asList(flag));
         return this;
      }

      @Override
      public Option.Builder<T> flags(@NotNull Collection<? extends OptionFlag> flags) {
         Validate.notNull(flags, "`flags` must not be null", new Object[0]);
         this.flags.addAll(flags);
         return this;
      }

      @Deprecated
      @Override
      public Option.Builder<T> instant(boolean instant) {
         Validate.isTrue(this.stateManager == null, "Cannot set instant when state manager is set", new Object[0]);
         YACLConstants.LOGGER
            .error("Option.Builder#instant is deprecated behaviour. Please use a custom state manager instead: `.state(StateManager.createInstant(Binding))`");
         this.instantDeprecated = instant;
         return this;
      }

      @Override
      public Option.Builder<T> addListener(@NotNull OptionEventListener<T> listener) {
         Validate.notNull(listener, "`listener` must not be null", new Object[0]);
         this.listeners.add(listener);
         return this;
      }

      @Override
      public Option.Builder<T> addListeners(@NotNull Collection<OptionEventListener<T>> optionEventListeners) {
         Validate.notNull(optionEventListeners, "`optionEventListeners` must not be null", new Object[0]);
         this.listeners.addAll(optionEventListeners);
         return this;
      }

      @Override
      public Option.Builder<T> listener(@NotNull BiConsumer<Option<T>, T> listener) {
         Validate.notNull(listener, "`listener` must not be null", new Object[0]);
         return this.addListener((opt, event) -> listener.accept(opt, opt.pendingValue()));
      }

      @Override
      public Option.Builder<T> listeners(@NotNull Collection<BiConsumer<Option<T>, T>> listeners) {
         Validate.notNull(listeners, "`listeners` must not be null", new Object[0]);
         this.addListeners(listeners.stream().map(listener -> (opt, event) -> listener.accept(opt, opt.pendingValue())).toList());
         return this;
      }

      @Override
      public Option<T> build() {
         Validate.notNull(this.controlGetter, "`control` must not be null when building `Option`", new Object[0]);
         if (this.instantDeprecated) {
            if (this.binding == null) {
               throw new IllegalStateException("Cannot build option with instant when binding is not set");
            }

            Validate.isTrue(this.flags.isEmpty(), "instant application does not support option flags", new Object[0]);
            this.stateManager = StateManager.createInstant(this.binding);
         } else if (this.binding != null) {
            this.stateManager = StateManager.createSimple(this.binding);
         }

         Validate.notNull(
            this.stateManager,
            "State manager must be set, either by using .binding() to create a simple manager or .state() to create an advanced one",
            new Object[0]
         );
         Validate.isTrue(
            !this.stateManager.isAlwaysSynced() || this.flags.isEmpty(), "Always synced state managers do not support option flags.", new Object[0]
         );
         return new OptionImpl<>(
            this.name, this.descriptionFunction, this.controlGetter, this.stateManager, this.available, ImmutableSet.copyOf(this.flags), this.listeners
         );
      }
   }
}
