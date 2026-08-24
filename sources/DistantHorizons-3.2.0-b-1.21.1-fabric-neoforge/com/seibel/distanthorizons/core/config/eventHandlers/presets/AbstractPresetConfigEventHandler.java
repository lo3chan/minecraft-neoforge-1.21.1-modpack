package com.seibel.distanthorizons.core.config.eventHandlers.presets;

import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.config.ConfigPresetOptions;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.config.IConfigGui;
import com.seibel.distanthorizons.coreapi.util.StringUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPresetConfigEventHandler<TPresetEnum extends Enum<?>> implements IConfigListener {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final long MS_DELAY_BEFORE_APPLYING_PRESET = 3000L;
   @Nullable
   private static final IConfigGui CONFIG_GUI = SingletonInjector.INSTANCE.get(IConfigGui.class);
   protected final ArrayList<ConfigPresetOptions<TPresetEnum, ?>> configList = new ArrayList<>();
   protected Timer applyPresetTimer = null;
   protected TPresetEnum waitingPresetEnum = (TPresetEnum)null;
   protected boolean changingPreset = false;

   public AbstractPresetConfigEventHandler() {
      if (CONFIG_GUI != null) {
         CONFIG_GUI.addOnScreenChangeListener(this::onConfigUiClosed);
      }
   }

   public void setUiOnlyConfigValues() {
      TPresetEnum currentQualitySetting = this.getCurrentPreset();
      this.getPresetConfigEntry().set(currentQualitySetting);
   }

   @Override
   public void onConfigValueSet() {
      if (ConfigHandler.INSTANCE.isLoaded) {
         if (!this.changingPreset) {
            TPresetEnum presetEnum = this.getPresetConfigEntry().get();
            if (presetEnum != this.getCustomPresetEnum()) {
               this.waitingPresetEnum = presetEnum;
               if (this.applyPresetTimer != null) {
                  this.applyPresetTimer.cancel();
               }

               TimerTask task = new TimerTask() {
                  @Override
                  public void run() {
                     AbstractPresetConfigEventHandler.this.applyPreset();
                  }
               };
               this.applyPresetTimer = TimerUtil.CreateTimer("ApplyConfigPresetTimer");
               this.applyPresetTimer.schedule(task, 3000L);
            }
         }
      }
   }

   private void applyPreset() {
      TPresetEnum newPresetEnum = this.waitingPresetEnum;
      this.waitingPresetEnum = null;
      if (newPresetEnum != null) {
         LOGGER.debug("changing preset to: [" + newPresetEnum + "].");
         this.changingPreset = true;

         for (ConfigPresetOptions<TPresetEnum, ?> configEntry : this.configList) {
            configEntry.updateConfigEntry(newPresetEnum);
         }

         this.setUiOnlyConfigValues();
         this.changingPreset = false;
         LOGGER.debug("preset active: [" + newPresetEnum + "].");
      }
   }

   public void onConfigValueChanged() {
      if (!this.changingPreset) {
         TPresetEnum newPreset = this.getCurrentPreset();
         TPresetEnum currentPreset = this.getPresetConfigEntry().get();
         if (newPreset != currentPreset) {
            this.getPresetConfigEntry().set(newPreset);
         }
      }
   }

   public void onConfigUiClosed() {
      if (this.applyPresetTimer != null) {
         this.applyPresetTimer.cancel();
         this.applyPreset();
      }
   }

   public TPresetEnum getCurrentPreset() {
      HashSet<TPresetEnum> possiblePresetSet = new HashSet<>(this.getPresetEnumList());

      for (ConfigPresetOptions<TPresetEnum, ?> configEntry : this.configList) {
         HashSet<TPresetEnum> optionPresetSet = configEntry.getPossibleQualitiesFromCurrentOptionValue();
         possiblePresetSet.retainAll(optionPresetSet);
      }

      ArrayList<TPresetEnum> possiblePrestList = new ArrayList<>(possiblePresetSet);
      if (possiblePrestList.size() > 1) {
         LOGGER.warn("Multiple potential preset options [" + StringUtil.join(", ", possiblePrestList) + "], defaulting to the first one.");
      }

      if (possiblePrestList.size() == 0) {
         possiblePrestList.add(this.getCustomPresetEnum());
      }

      return possiblePrestList.get(0);
   }

   protected abstract AbstractConfigBase<TPresetEnum> getPresetConfigEntry();

   protected abstract List<TPresetEnum> getPresetEnumList();

   protected abstract TPresetEnum getCustomPresetEnum();
}
