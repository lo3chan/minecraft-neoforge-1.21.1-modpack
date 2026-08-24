package com.iafenvoy.jupiter.config;

public enum ConfigSide {
   COMMON("Common", -16181),
   CLIENT("Client", -16711681),
   SERVER("Server", -10066177),
   STARTUP("Startup", -5592406),
   UNKNOWN("Unknown", -5592406);

   private final String displayText;
   private final int color;

   private ConfigSide(String displayText, int color) {
      this.displayText = displayText;
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   public String getDisplayText() {
      return this.displayText;
   }
}
