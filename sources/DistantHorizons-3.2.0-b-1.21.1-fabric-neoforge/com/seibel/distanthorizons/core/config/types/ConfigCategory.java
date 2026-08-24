package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;

public class ConfigCategory extends AbstractConfigBase<Class<?>> {
   public String destination;

   private ConfigCategory(EConfigEntryAppearance appearance, Class<?> value, String destination) {
      super(appearance, value);
      this.destination = destination;
   }

   public String getDestination() {
      return this.destination;
   }

   @Deprecated
   @Override
   public Class<?> getType() {
      return this.value;
   }

   public static class Builder extends AbstractConfigBase.Builder<Class<?>, ConfigCategory.Builder> {
      private String tmpDestination = null;

      public ConfigCategory.Builder setDestination(String newDestination) {
         this.tmpDestination = newDestination;
         return this;
      }

      public ConfigCategory.Builder setAppearance(EConfigEntryAppearance newAppearance) {
         this.tmpAppearance = newAppearance;
         return this;
      }

      public ConfigCategory build() {
         return new ConfigCategory(this.tmpAppearance, this.tmpValue, this.tmpDestination);
      }
   }
}
