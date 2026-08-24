package com.seibel.distanthorizons.api.enums.config.quickOptions;

import com.seibel.distanthorizons.api.enums.config.DisallowSelectingViaConfigGui;

public enum EDhApiThreadPreset {
   @DisallowSelectingViaConfigGui
   CUSTOM,
   MINIMAL_IMPACT,
   LOW_IMPACT,
   BALANCED,
   AGGRESSIVE,
   I_PAID_FOR_THE_WHOLE_CPU;
}
