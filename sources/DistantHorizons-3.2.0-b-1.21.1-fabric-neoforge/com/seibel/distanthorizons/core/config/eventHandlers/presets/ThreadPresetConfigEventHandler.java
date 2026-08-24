package com.seibel.distanthorizons.core.config.eventHandlers.presets;

import com.seibel.distanthorizons.api.enums.config.quickOptions.EDhApiThreadPreset;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.ConfigPresetOptions;
import com.seibel.distanthorizons.core.config.listeners.ConfigChangeListener;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.util.MathUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ThreadPresetConfigEventHandler extends AbstractPresetConfigEventHandler<EDhApiThreadPreset> {
   public static final ThreadPresetConfigEventHandler INSTANCE = new ThreadPresetConfigEventHandler();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final ConfigPresetOptions<EDhApiThreadPreset, Integer> threadCount = new ConfigPresetOptions<>(
      Config.Common.MultiThreading.numberOfThreads, new HashMap<EDhApiThreadPreset, Integer>() {
         {
            this.put(EDhApiThreadPreset.MINIMAL_IMPACT, ThreadPresetConfigEventHandler.getThreadCountByPercent(0.1));
            this.put(EDhApiThreadPreset.LOW_IMPACT, ThreadPresetConfigEventHandler.getThreadCountByPercent(0.25));
            this.put(EDhApiThreadPreset.BALANCED, ThreadPresetConfigEventHandler.getDefaultThreadCount());
            this.put(EDhApiThreadPreset.AGGRESSIVE, ThreadPresetConfigEventHandler.getThreadCountByPercent(0.75));
            this.put(EDhApiThreadPreset.I_PAID_FOR_THE_WHOLE_CPU, ThreadPresetConfigEventHandler.getThreadCountByPercent(1.0));
         }
      }
   );
   private final ConfigPresetOptions<EDhApiThreadPreset, Double> threadRunTime = new ConfigPresetOptions<>(
      Config.Common.MultiThreading.threadRunTimeRatio, new HashMap<EDhApiThreadPreset, Double>() {
         {
            this.put(EDhApiThreadPreset.MINIMAL_IMPACT, 0.5);
            this.put(EDhApiThreadPreset.LOW_IMPACT, 1.0);
            this.put(EDhApiThreadPreset.BALANCED, ThreadPresetConfigEventHandler.getDefaultRunTimeRatio());
            this.put(EDhApiThreadPreset.AGGRESSIVE, 1.0);
            this.put(EDhApiThreadPreset.I_PAID_FOR_THE_WHOLE_CPU, 1.0);
         }
      }
   );

   public static int getDefaultThreadCount() {
      return getThreadCountByPercent(0.5);
   }

   public static double getDefaultRunTimeRatio() {
      return 1.0;
   }

   private ThreadPresetConfigEventHandler() {
      this.configList.add(this.threadCount);
      this.configList.add(this.threadRunTime);

      for (ConfigPresetOptions<EDhApiThreadPreset, ?> config : this.configList) {
         new ConfigChangeListener<>(config.configEntry, val -> this.onConfigValueChanged());
      }
   }

   private static int getThreadCountByPercent(double percent) throws IllegalArgumentException {
      if (!(percent <= 0.0) && !(percent > 1.0)) {
         int totalProcessorCount = Runtime.getRuntime().availableProcessors();
         int coreCount = (int)Math.ceil(totalProcessorCount * percent);
         return MathUtil.clamp(1, coreCount, totalProcessorCount);
      } else {
         throw new IllegalArgumentException("percent must be greater than 0 and less than or equal to 1.");
      }
   }

   @Override
   protected AbstractConfigBase<EDhApiThreadPreset> getPresetConfigEntry() {
      return Config.Client.threadPresetSetting;
   }

   @Override
   protected List<EDhApiThreadPreset> getPresetEnumList() {
      return Arrays.asList(EDhApiThreadPreset.values());
   }

   protected EDhApiThreadPreset getCustomPresetEnum() {
      return EDhApiThreadPreset.CUSTOM;
   }
}
