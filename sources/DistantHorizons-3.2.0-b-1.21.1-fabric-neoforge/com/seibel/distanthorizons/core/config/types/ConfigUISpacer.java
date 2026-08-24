package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;

public class ConfigUISpacer extends AbstractConfigBase<String> {
   public ConfigUISpacer() {
      super(EConfigEntryAppearance.ONLY_IN_GUI, "");
   }

   @Override
   public void setAppearance(EConfigEntryAppearance newAppearance) {
   }

   public void set(String newValue) {
   }

   public static class Builder extends AbstractConfigBase.Builder<String, ConfigUISpacer.Builder> {
      public ConfigUISpacer.Builder setAppearance(EConfigEntryAppearance newAppearance) {
         return this;
      }

      public ConfigUISpacer.Builder set(String newValue) {
         return this;
      }

      public ConfigUISpacer build() {
         return new ConfigUISpacer();
      }
   }
}
