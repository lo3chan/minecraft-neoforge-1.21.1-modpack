package pl.skidam.automodpack_core.utils;

public final class AutoModpackProtocol {
   private static final int LEGACY_COMPATIBLE_MAJOR = 4;
   private static final int LEGACY_COMPATIBLE_MINOR = 0;

   private AutoModpackProtocol() {
   }

   public static boolean acceptsClient(String serverVersion, String clientVersion) {
      return serverVersion != null && serverVersion.equals(clientVersion)
         || isLegacyCompatibleVersion(serverVersion) && isLegacyCompatibleVersion(clientVersion);
   }

   public static String getHandshakeVersion(String serverVersion, String clientVersion) {
      return isLegacyCompatibleVersion(serverVersion) && isLegacyCompatibleVersion(clientVersion) ? serverVersion : clientVersion;
   }

   public static boolean isLegacyCompatibleVersion(String version) {
      try {
         SemanticVersion parsed = SemanticVersion.parse(version);
         return parsed.isStable() && parsed.major() == 4 && parsed.minor() == 0;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }
}
