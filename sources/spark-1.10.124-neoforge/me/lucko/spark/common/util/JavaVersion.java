package me.lucko.spark.common.util;

import org.jetbrains.annotations.VisibleForTesting;

public enum JavaVersion {
   private static final int JAVA_VERSION = parseJavaVersion(System.getProperty("java.version"));

   @VisibleForTesting
   static int parseJavaVersion(String version) {
      return version.startsWith("1.") ? Integer.parseInt(version.substring(2, 3)) : Integer.parseInt(version.split("\\.")[0]);
   }

   public static int getJavaVersion() {
      return JAVA_VERSION;
   }
}
