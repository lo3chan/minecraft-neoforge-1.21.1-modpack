package com.seibel.distanthorizons.core.jar;

public enum EPlatform {
   WINDOWS("Windows", false),
   LINUX("Linux", true),
   MACOS("macOS", true),
   BSD("BSD", true),
   UNIX("Unix", true);

   private static final EPlatform current;
   private final String name;
   private final boolean isUnix;

   private EPlatform(String name, boolean isUnix) {
      this.name = name;
      this.isUnix = isUnix;
   }

   public String getName() {
      return this.name;
   }

   public boolean isUnix() {
      return this.isUnix;
   }

   public static EPlatform get() {
      return current;
   }

   public static EPlatform.EArchitecture getArchitecture() {
      return EPlatform.EArchitecture.current;
   }

   @Override
   public String toString() {
      return this.getName();
   }

   static {
      String osName = System.getProperty("os.name").toLowerCase();
      if (osName.contains("windows")) {
         current = WINDOWS;
      } else if (osName.contains("linux")) {
         current = LINUX;
      } else if (osName.contains("mac") || osName.contains("darwin")) {
         current = MACOS;
      } else if (!osName.startsWith("bsd") && !osName.startsWith("freebsd")) {
         if (!osName.startsWith("unix")) {
            throw new LinkageError("Unknown platform: " + osName);
         }

         current = UNIX;
      } else {
         current = BSD;
      }
   }

   public static enum EArchitecture {
      X86(false),
      X64(true),
      ARM32(false),
      ARM64(true);

      static final EPlatform.EArchitecture current;
      final boolean is64Bit;

      private EArchitecture(boolean is64Bit) {
         this.is64Bit = is64Bit;
      }

      static {
         String osArch = System.getProperty("os.arch");
         boolean is64Bit = osArch.contains("64") || osArch.startsWith("armv8");
         current = !osArch.startsWith("arm") && !osArch.startsWith("aarch64") ? (is64Bit ? X64 : X86) : (is64Bit ? ARM64 : ARM32);
      }
   }
}
