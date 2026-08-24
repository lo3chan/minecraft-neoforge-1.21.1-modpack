package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.gui.IConfigGuiInfo;
import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;

public abstract class AbstractConfigBase<T> {
   public String category = "";
   public String name;
   protected final T defaultValue;
   protected final boolean isFloatingPointNumber;
   protected T value;
   public IConfigGuiInfo guiValue;
   protected EConfigEntryAppearance appearance;

   protected AbstractConfigBase(EConfigEntryAppearance appearance, T defaultValue) {
      this.defaultValue = defaultValue;
      if (this.defaultValue == null) {
         throw new IllegalArgumentException("defaultValue cannot be null");
      } else {
         this.value = defaultValue;
         this.appearance = appearance;
         Class<?> defaultValueClass = defaultValue.getClass();
         this.isFloatingPointNumber = defaultValueClass == Double.class || defaultValueClass == Float.class;
      }
   }

   public T get() {
      return this.value;
   }

   public void set(T newValue) {
      this.value = newValue;
   }

   public EConfigEntryAppearance getAppearance() {
      return this.appearance;
   }

   public void setAppearance(EConfigEntryAppearance newAppearance) {
      this.appearance = newAppearance;
   }

   public String getCategory() {
      return this.category;
   }

   public String getName() {
      return this.name;
   }

   public String getNameAndCategory() {
      return (this.category.isEmpty() ? "" : this.category + ".") + this.name;
   }

   public Class<?> getType() {
      return this.defaultValue.getClass();
   }

   public boolean typeIsFloatingPointNumber() {
      return this.isFloatingPointNumber;
   }

   protected abstract static class Builder<T, S> {
      protected EConfigEntryAppearance tmpAppearance = EConfigEntryAppearance.ALL;
      protected T tmpValue;

      public S setAppearance(EConfigEntryAppearance newAppearance) {
         this.tmpAppearance = newAppearance;
         return (S)this;
      }

      public S set(T newValue) {
         this.tmpValue = newValue;
         return (S)this;
      }
   }
}
