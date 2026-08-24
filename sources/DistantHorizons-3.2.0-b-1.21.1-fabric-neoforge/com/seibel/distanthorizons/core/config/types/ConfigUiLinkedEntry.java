package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;

public class ConfigUiLinkedEntry extends AbstractConfigBase<AbstractConfigBase<?>> {
   public ConfigUiLinkedEntry(AbstractConfigBase<?> value) {
      super(EConfigEntryAppearance.ONLY_IN_GUI, value);
   }

   @Override
   public void setAppearance(EConfigEntryAppearance newAppearance) {
   }

   public void set(AbstractConfigBase<?> newValue) {
   }

   public static class Builder extends AbstractConfigBase.Builder<AbstractConfigBase<?>, ConfigUiLinkedEntry.Builder> {
      public ConfigUiLinkedEntry.Builder setAppearance(EConfigEntryAppearance newAppearance) {
         return this;
      }

      public ConfigUiLinkedEntry build() {
         return new ConfigUiLinkedEntry(this.tmpValue);
      }
   }
}
