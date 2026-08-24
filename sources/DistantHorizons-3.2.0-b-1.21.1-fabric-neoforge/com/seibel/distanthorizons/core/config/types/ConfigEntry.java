package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.config.listeners.ConfigChangeListener;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;
import com.seibel.distanthorizons.core.config.types.enums.EConfigValidity;
import com.seibel.distanthorizons.core.util.NumberUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class ConfigEntry<T> extends AbstractConfigBase<T> {
   private final String comment;
   private T min;
   private T max;
   private final ArrayList<IConfigListener> listenerList;
   private final String chatCommandName;
   private final boolean allowApiOverride;
   @Nullable
   private T apiValue;
   @Nullable
   private T mcVersionOverrideValue;

   private ConfigEntry(
      EConfigEntryAppearance appearance,
      String comment,
      String chatCommandName,
      T value,
      T min,
      T max,
      boolean allowApiOverride,
      ArrayList<IConfigListener> listenerList
   ) {
      super(appearance, value);
      this.comment = comment;
      this.min = min;
      this.max = max;
      this.chatCommandName = chatCommandName;
      this.allowApiOverride = allowApiOverride;
      this.listenerList = listenerList;
   }

   public String getChatCommandName() {
      return this.chatCommandName;
   }

   public String getComment() {
      return this.comment;
   }

   public boolean getAllowApiOverride() {
      return this.allowApiOverride;
   }

   public T getMin() {
      return this.min;
   }

   public void setMin(T newMin) {
      this.min = newMin;
   }

   public T getMax() {
      return this.max;
   }

   public void setMax(T newMax) {
      this.max = newMax;
   }

   public void setApiValue(T newApiValue) {
      this.apiValue = newApiValue;
      synchronized (this.listenerList) {
         this.listenerList.forEach(IConfigListener::onConfigValueSet);
      }
   }

   public boolean apiIsOverriding() {
      return this.allowApiOverride && this.apiValue != null;
   }

   public void setMcVersionOverrideValue(@Nullable T value) {
      this.mcVersionOverrideValue = value;
   }

   public boolean mcVersionOverridePresent() {
      return this.mcVersionOverrideValue != null;
   }

   public void setWithoutFiringEvents(T newValue) {
      super.set(newValue);
   }

   public void setWithoutSaving(T newValue) {
      super.set(newValue);
      synchronized (this.listenerList) {
         this.listenerList.forEach(IConfigListener::onConfigValueSet);
      }
   }

   @Override
   public void set(T newValue) {
      this.setWithoutSaving(newValue);
      this.save();
   }

   public void uiSetWithoutSaving(T newValue) {
      this.setWithoutSaving(newValue);
      synchronized (this.listenerList) {
         this.listenerList.forEach(IConfigListener::onUiModify);
      }
   }

   public void uiSet(T newValue) {
      this.set(newValue);
      synchronized (this.listenerList) {
         this.listenerList.forEach(IConfigListener::onUiModify);
      }
   }

   @Override
   public T get() {
      if (this.mcVersionOverrideValue != null) {
         return this.mcVersionOverrideValue;
      } else {
         return this.allowApiOverride && this.apiValue != null ? this.apiValue : super.get();
      }
   }

   public T getTrueValue() {
      return super.get();
   }

   public T getDefaultValue() {
      return super.defaultValue;
   }

   @Nullable
   public T getApiValue() {
      return this.apiValue;
   }

   public void addValueChangeListener(Consumer<T> onValueChangeFunc) {
      ConfigChangeListener<T> changeListener = new ConfigChangeListener<>(this, onValueChangeFunc);
      this.addListener(changeListener);
   }

   public void addListener(IConfigListener newListener) {
      synchronized (this.listenerList) {
         this.listenerList.add(newListener);
      }
   }

   public void removeListener(IConfigListener oldListener) {
      synchronized (this.listenerList) {
         this.listenerList.remove(oldListener);
      }
   }

   public void clearListeners() {
      synchronized (this.listenerList) {
         this.listenerList.clear();
      }
   }

   public EConfigValidity getValidity() {
      return this.getValidity(this.value, this.min, this.max);
   }

   public EConfigValidity getValidity(@Nullable T value) {
      return this.getValidity(value, this.min, this.max);
   }

   public EConfigValidity getValidity(@Nullable T value, @Nullable T min, @Nullable T max) {
      if (!ConfigHandler.INSTANCE.runMinMaxValidation) {
         return EConfigValidity.VALID;
      } else if (min == null && max == null) {
         return EConfigValidity.VALID;
      } else if (value == null || this.value == null || value.getClass() != this.value.getClass()) {
         return EConfigValidity.INVALID;
      } else if (!(value instanceof Number)) {
         return EConfigValidity.VALID;
      } else if (max != null && NumberUtil.greaterThan((Number)value, (Number)max)) {
         return EConfigValidity.NUMBER_TOO_HIGH;
      } else {
         return min != null && NumberUtil.lessThan((Number)value, (Number)min) ? EConfigValidity.NUMBER_TOO_LOW : EConfigValidity.VALID;
      }
   }

   public void save() {
      ConfigHandler.INSTANCE.configFileHandler.saveEntry(this);
   }

   public void load() {
      ConfigHandler.INSTANCE.configFileHandler.loadEntry(this);
   }

   @Override
   public String toString() {
      return this.name + ": [" + this.get() + "]";
   }

   public boolean equals(AbstractConfigBase<?> obj) {
      return obj.getClass() == ConfigEntry.class && this.equals((ConfigEntry<?>)obj);
   }

   public boolean equals(ConfigEntry<?> obj) {
      return Number.class.isAssignableFrom(this.value.getClass()) ? this.value == obj.value : this.value.equals(obj.value);
   }

   public static class Builder<T> extends AbstractConfigBase.Builder<T, ConfigEntry.Builder<T>> {
      private String tmpComment = null;
      private T tmpMin = (T)null;
      private T tmpMax = (T)null;
      protected String tmpChatCommandName = null;
      private boolean tmpUseApiOverwrite = true;
      protected ArrayList<IConfigListener> tmpIConfigListener = new ArrayList<>();

      public ConfigEntry.Builder<T> comment(String newComment) {
         this.tmpComment = newComment;
         return this;
      }

      public ConfigEntry.Builder<T> setMinDefaultMax(T newMin, T newDefault, T newMax) {
         this.set(newDefault);
         this.setMinMax(newMin, newMax);
         return this;
      }

      public ConfigEntry.Builder<T> setMinMax(T newMin, T newMax) {
         this.tmpMin = newMin;
         this.tmpMax = newMax;
         return this;
      }

      public ConfigEntry.Builder<T> setMin(T newMin) {
         this.tmpMin = newMin;
         return this;
      }

      public ConfigEntry.Builder<T> setMax(T newMax) {
         this.tmpMax = newMax;
         return this;
      }

      public ConfigEntry.Builder<T> setChatCommandName(String name) {
         this.tmpChatCommandName = name;
         return this;
      }

      public ConfigEntry.Builder<T> setUseApiOverwrite(boolean newUseApiOverwrite) {
         this.tmpUseApiOverwrite = newUseApiOverwrite;
         return this;
      }

      public ConfigEntry.Builder<T> replaceListeners(ArrayList<IConfigListener> newConfigListener) {
         this.tmpIConfigListener = newConfigListener;
         return this;
      }

      public ConfigEntry.Builder<T> addListeners(IConfigListener... newConfigListener) {
         this.tmpIConfigListener.addAll(Arrays.asList(newConfigListener));
         return this;
      }

      public ConfigEntry.Builder<T> addListener(IConfigListener newConfigListener) {
         this.tmpIConfigListener.add(newConfigListener);
         return this;
      }

      public ConfigEntry.Builder<T> clearListeners() {
         this.tmpIConfigListener.clear();
         return this;
      }

      public ConfigEntry<T> build() {
         return new ConfigEntry<>(
            this.tmpAppearance,
            this.tmpComment,
            this.tmpChatCommandName,
            this.tmpValue,
            this.tmpMin,
            this.tmpMax,
            this.tmpUseApiOverwrite,
            this.tmpIConfigListener
         );
      }
   }
}
