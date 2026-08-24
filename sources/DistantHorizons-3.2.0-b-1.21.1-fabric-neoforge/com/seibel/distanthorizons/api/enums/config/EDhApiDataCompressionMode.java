package com.seibel.distanthorizons.api.enums.config;

public enum EDhApiDataCompressionMode {
   @DisallowSelectingViaConfigGui
   UNCOMPRESSED(0),
   LZ4(1),
   Z_STD_BLOCK(4),
   @Deprecated
   @DisallowSelectingViaConfigGui
   Z_STD_STREAM(2),
   LZMA2(3);

   public final byte value;

   private EDhApiDataCompressionMode(int value) {
      this.value = (byte)value;
   }

   public static EDhApiDataCompressionMode getFromValue(byte value) throws IllegalArgumentException {
      EDhApiDataCompressionMode[] enumList = values();

      for (int i = 0; i < enumList.length; i++) {
         if (enumList[i].value == value) {
            return enumList[i];
         }
      }

      throw new IllegalArgumentException("No compression mode with the value [" + value + "]");
   }
}
