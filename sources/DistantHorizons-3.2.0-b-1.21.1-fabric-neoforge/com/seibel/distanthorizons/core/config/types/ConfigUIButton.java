package com.seibel.distanthorizons.core.config.types;

import com.seibel.distanthorizons.core.config.types.enums.EConfigEntryAppearance;

public class ConfigUIButton extends AbstractConfigBase<Runnable> {
   public ConfigUIButton(Runnable runnable) {
      super(EConfigEntryAppearance.ONLY_IN_GUI, runnable);
   }

   public void runAction() {
      this.value.run();
   }

   public static class Builder extends AbstractConfigBase.Builder<Runnable, ConfigUIButton.Builder> {
      public ConfigUIButton.Builder setAppearance(EConfigEntryAppearance newAppearance) {
         return this;
      }

      public ConfigUIButton build() {
         return new ConfigUIButton(this.tmpValue);
      }
   }
}
