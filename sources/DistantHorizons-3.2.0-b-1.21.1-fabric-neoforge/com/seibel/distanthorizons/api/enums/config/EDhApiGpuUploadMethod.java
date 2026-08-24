package com.seibel.distanthorizons.api.enums.config;

public enum EDhApiGpuUploadMethod {
   AUTO(false, false),
   BUFFER_STORAGE(false, true),
   SUB_DATA(false, false),
   DATA(false, false);

   public final boolean useEarlyMapping;
   public final boolean useBufferStorage;

   private EDhApiGpuUploadMethod(boolean useEarlyMapping, boolean useBufferStorage) {
      this.useEarlyMapping = useEarlyMapping;
      this.useBufferStorage = useBufferStorage;
   }
}
