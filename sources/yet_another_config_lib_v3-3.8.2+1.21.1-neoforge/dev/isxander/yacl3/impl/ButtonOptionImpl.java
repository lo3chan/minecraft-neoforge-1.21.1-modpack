package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ActionController;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ButtonOptionImpl implements ButtonOption {
   private final Component name;
   private final OptionDescription description;
   private final StateManager<BiConsumer<YACLScreen, ButtonOption>> stateManager;
   private boolean available;
   private final Controller<BiConsumer<YACLScreen, ButtonOption>> controller;

   public ButtonOptionImpl(
      @NotNull Component name,
      @Nullable OptionDescription description,
      @NotNull BiConsumer<YACLScreen, ButtonOption> action,
      @Nullable Component text,
      boolean available
   ) {
      this.name = name;
      this.description = description;
      this.stateManager = StateManager.createImmutable(action);
      this.available = available;
      this.controller = text != null ? new ActionController(this, text) : new ActionController(this);
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

   @Override
   public BiConsumer<YACLScreen, ButtonOption> action() {
      return this.stateManager().get();
   }

   @Override
   public boolean available() {
      return this.available;
   }

   @Override
   public void setAvailable(boolean available) {
      this.available = available;
   }

   @NotNull
   @Override
   public Controller<BiConsumer<YACLScreen, ButtonOption>> controller() {
      return this.controller;
   }

   @NotNull
   @Override
   public StateManager<BiConsumer<YACLScreen, ButtonOption>> stateManager() {
      return this.stateManager;
   }

   @NotNull
   @Override
   public Binding<BiConsumer<YACLScreen, ButtonOption>> binding() {
      return new ButtonOptionImpl.EmptyBinderImpl();
   }

   @NotNull
   @Override
   public ImmutableSet<OptionFlag> flags() {
      return ImmutableSet.of();
   }

   @Override
   public boolean changed() {
      return false;
   }

   @NotNull
   public BiConsumer<YACLScreen, ButtonOption> pendingValue() {
      throw new UnsupportedOperationException();
   }

   public void requestSet(@NotNull BiConsumer<YACLScreen, ButtonOption> value) {
      throw new UnsupportedOperationException();
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
      throw new UnsupportedOperationException();
   }

   @Override
   public void addEventListener(OptionEventListener<BiConsumer<YACLScreen, ButtonOption>> listener) {
   }

   @Override
   public void addListener(BiConsumer<Option<BiConsumer<YACLScreen, ButtonOption>>, BiConsumer<YACLScreen, ButtonOption>> changedListener) {
   }

   @Internal
   public static final class BuilderImpl implements ButtonOption.Builder {
      private Component name;
      private Component text = null;
      private OptionDescription description = OptionDescription.EMPTY;
      private boolean available = true;
      private BiConsumer<YACLScreen, ButtonOption> action;

      @Override
      public ButtonOption.Builder name(@NotNull Component name) {
         Validate.notNull(name, "`name` cannot be null", new Object[0]);
         this.name = name;
         return this;
      }

      @Override
      public ButtonOption.Builder text(@NotNull Component text) {
         Validate.notNull(text, "`text` cannot be null", new Object[0]);
         this.text = text;
         return this;
      }

      @Override
      public ButtonOption.Builder description(@NotNull OptionDescription description) {
         Validate.notNull(description, "`description` cannot be null", new Object[0]);
         this.description = description;
         return this;
      }

      @Override
      public ButtonOption.Builder action(@NotNull BiConsumer<YACLScreen, ButtonOption> action) {
         Validate.notNull(action, "`action` cannot be null", new Object[0]);
         this.action = action;
         return this;
      }

      @Deprecated
      @Override
      public ButtonOption.Builder action(@NotNull Consumer<YACLScreen> action) {
         Validate.notNull(action, "`action` cannot be null", new Object[0]);
         this.action = (screen, button) -> action.accept(screen);
         return this;
      }

      @Override
      public ButtonOption.Builder available(boolean available) {
         this.available = available;
         return this;
      }

      @Override
      public ButtonOption build() {
         Validate.notNull(this.name, "`name` must not be null when building `ButtonOption`", new Object[0]);
         Validate.notNull(this.action, "`action` must not be null when building `ButtonOption`", new Object[0]);
         return new ButtonOptionImpl(this.name, this.description, this.action, this.text, this.available);
      }
   }

   private static class EmptyBinderImpl implements Binding<BiConsumer<YACLScreen, ButtonOption>> {
      public void setValue(BiConsumer<YACLScreen, ButtonOption> value) {
      }

      public BiConsumer<YACLScreen, ButtonOption> getValue() {
         throw new UnsupportedOperationException();
      }

      public BiConsumer<YACLScreen, ButtonOption> defaultValue() {
         throw new UnsupportedOperationException();
      }
   }
}
