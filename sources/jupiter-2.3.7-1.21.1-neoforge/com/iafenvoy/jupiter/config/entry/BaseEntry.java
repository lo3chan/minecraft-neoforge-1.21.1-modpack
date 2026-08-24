package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigBuilder;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ValueChangeCallback;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseEntry<T> implements ConfigEntry<T> {
   protected final Component name;
   @Nullable
   protected String key;
   @Nullable
   protected Component tooltip = null;
   protected boolean visible;
   protected final T defaultValue;
   protected T value;
   protected boolean restartRequired;
   protected final List<ValueChangeCallback<T>> callbacks = new ArrayList<>();

   protected BaseEntry(BaseEntry.Builder<T, ?, ?> builder) {
      this.name = builder.name;
      this.defaultValue = builder.defaultValue;
      this.key = builder.key;
      this.tooltip = builder.tooltip;
      this.visible = builder.visible;
      this.restartRequired = builder.restartRequired;
      this.callbacks.addAll(builder.callbacks);
      this.value = this.newDefaultValue();
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public BaseEntry(@NotNull String nameKey, T defaultValue) {
      this.name = TextUtil.translatable(nameKey);
      this.key = nameKey;
      this.defaultValue = defaultValue;
      this.value = this.newDefaultValue();
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public BaseEntry<T> visible(boolean visible) {
      this.visible = visible;
      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public BaseEntry<T> json(String jsonKey) {
      this.key = jsonKey;
      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public BaseEntry<T> callback(Consumer<T> callback) {
      this.callbacks.add((v2, b1, b2) -> callback.accept(v2));
      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public BaseEntry<T> restartRequired() {
      this.restartRequired = true;
      return this;
   }

   @Override
   public void registerCallback(ValueChangeCallback<T> callback) {
      this.callbacks.add(callback);
   }

   @Override
   public void setValue(T value) {
      T oldValue = this.value;
      this.value = value;
      this.callbacks.forEach(x -> x.onValueChange(this.value, false, Objects.equals(this.value, this.defaultValue)));
   }

   @Nullable
   @Override
   public String getKey() {
      return this.key;
   }

   @Override
   public Component getName() {
      return (Component)(this.restartRequired ? this.name.copy().append(" ").append(TextUtil.translatable("jupiter.screen.restart_required")) : this.name);
   }

   @Nullable
   @Override
   public Component getTooltip() {
      return this.tooltip;
   }

   @Override
   public T getDefaultValue() {
      return this.defaultValue;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public void reset() {
      this.value = this.newDefaultValue();
      this.callbacks.forEach(x -> x.onValueChange(this.value, true, true));
   }

   protected T newDefaultValue() {
      return this.defaultValue;
   }

   public abstract static class Builder<T, E extends BaseEntry<T>, B extends BaseEntry.Builder<T, E, B>> implements ConfigBuilder<T, E, B> {
      protected final Component name;
      protected final T defaultValue;
      protected T value;
      @Nullable
      protected String key;
      @Nullable
      protected Component tooltip;
      protected boolean visible = true;
      protected boolean restartRequired;
      protected final List<ValueChangeCallback<T>> callbacks = new ArrayList<>();

      public Builder(String nameKey, T defaultValue) {
         this(TextUtil.translatable(nameKey), defaultValue);
         this.json(nameKey);
      }

      public Builder(Component name, T defaultValue) {
         this.name = name;
         this.defaultValue = this.value = defaultValue;
      }

      public Builder(E parent) {
         this.name = parent.name;
         this.defaultValue = this.value = parent.defaultValue;
         this.key = parent.key;
         this.tooltip = parent.tooltip;
         this.visible = parent.visible;
         this.restartRequired = parent.restartRequired;
         this.callbacks.addAll(parent.callbacks);
      }

      public B visible(boolean visible) {
         this.visible = visible;
         return this.self();
      }

      @Deprecated(
         forRemoval = true
      )
      @Comment("Use key() instead")
      public B json(String jsonKey) {
         return this.key(jsonKey);
      }

      public B key(String key) {
         this.key = key;
         return this.self();
      }

      public B restartRequired() {
         this.restartRequired = true;
         return this.self();
      }

      public B tooltip(String tooltipKey) {
         return this.tooltip(TextUtil.translatable(tooltipKey));
      }

      public B tooltip(Component tooltipKey) {
         this.tooltip = tooltipKey;
         return this.self();
      }

      public B callback(ValueChangeCallback<T> callback) {
         this.callbacks.add(callback);
         return this.self();
      }

      public B value(T value) {
         this.value = value;
         return this.self();
      }

      public abstract B self();

      protected abstract E buildInternal();

      public E build() {
         E e = this.buildInternal();
         if (this.value != null) {
            e.setValue(this.value);
         }

         return e;
      }
   }
}
