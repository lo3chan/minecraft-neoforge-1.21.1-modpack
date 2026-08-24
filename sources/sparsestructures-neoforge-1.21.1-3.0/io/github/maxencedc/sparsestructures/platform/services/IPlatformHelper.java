package io.github.maxencedc.sparsestructures.platform.services;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();

   default String getEnvironmentName() {
      return this.isDevelopmentEnvironment() ? "development" : "production";
   }
}
