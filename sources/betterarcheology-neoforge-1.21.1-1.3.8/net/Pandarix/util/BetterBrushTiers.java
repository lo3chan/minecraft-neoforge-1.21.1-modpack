package net.Pandarix.util;

import java.text.DecimalFormat;
import net.Pandarix.config.BAConfig;

public enum BetterBrushTiers {
   IRON,
   DIAMOND,
   NETHERITE;

   public int getBrushTickRate() {
      int speed = switch (this) {
         case IRON -> BAConfig.ironBrushTickRate;
         case DIAMOND -> BAConfig.diamondBrushTickRate;
         case NETHERITE -> BAConfig.netheriteBrushTickRate;
      };
      return Math.max(1, speed);
   }

   public String getSpeedFactor() {
      DecimalFormat df = new DecimalFormat("###");
      return df.format((10.0F / this.getBrushTickRate() - 1.0F) * 100.0F);
   }
}
