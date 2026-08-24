package me.lucko.spark.lib.protobuf;

import java.util.logging.Logger;

public final class RuntimeVersion {
   public static final RuntimeVersion.RuntimeDomain OSS_DOMAIN = RuntimeVersion.RuntimeDomain.PUBLIC;
   public static final int OSS_MAJOR = 4;
   public static final int OSS_MINOR = 28;
   public static final int OSS_PATCH = 2;
   public static final String OSS_SUFFIX = "";
   public static final RuntimeVersion.RuntimeDomain DOMAIN = OSS_DOMAIN;
   public static final int MAJOR = 4;
   public static final int MINOR = 28;
   public static final int PATCH = 2;
   public static final String SUFFIX = "";
   private static final String VERSION_STRING = versionString(4, 28, 2, "");
   private static final Logger logger = Logger.getLogger(RuntimeVersion.class.getName());

   public static void validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain domain, int major, int minor, int patch, String suffix, String location) {
      if (!checkDisabled()) {
         validateProtobufGencodeVersionImpl(domain, major, minor, patch, suffix, location);
      }
   }

   private static void validateProtobufGencodeVersionImpl(RuntimeVersion.RuntimeDomain domain, int major, int minor, int patch, String suffix, String location) {
      if (!checkDisabled()) {
         String gencodeVersionString = versionString(major, minor, patch, suffix);
         if (major < 0 || minor < 0 || patch < 0) {
            throw new RuntimeVersion.ProtobufRuntimeVersionException("Invalid gencode version: " + gencodeVersionString);
         } else if (domain != DOMAIN) {
            throw new RuntimeVersion.ProtobufRuntimeVersionException(
               String.format(
                  "Detected mismatched Protobuf Gencode/Runtime domains when loading %s: gencode %s, runtime %s. Cross-domain usage of Protobuf is not supported.",
                  location,
                  domain,
                  DOMAIN
               )
            );
         } else {
            if (major != 4) {
               if (major != 3) {
                  throw new RuntimeVersion.ProtobufRuntimeVersionException(
                     String.format(
                        "Detected mismatched Protobuf Gencode/Runtime major versions when loading %s: gencode %s, runtime %s. Same major version is required.",
                        location,
                        gencodeVersionString,
                        VERSION_STRING
                     )
                  );
               }

               logger.warning(
                  String.format(
                     " Protobuf gencode version %s is exactly one major version older than the runtime version %s at %s. Please update the gencode to avoid compatibility violations in the next runtime release.",
                     gencodeVersionString,
                     VERSION_STRING,
                     location
                  )
               );
            }

            if (28 >= minor && (minor != 28 || 2 >= patch)) {
               if (28 > minor || 2 > patch) {
                  logger.warning(
                     String.format(
                        " Protobuf gencode version %s is older than the runtime version %s at %s. Please avoid checked-in Protobuf gencode that can be obsolete.",
                        gencodeVersionString,
                        VERSION_STRING,
                        location
                     )
                  );
               }

               if (!suffix.equals("")) {
                  throw new RuntimeVersion.ProtobufRuntimeVersionException(
                     String.format(
                        "Detected mismatched Protobuf Gencode/Runtime version suffixes when loading %s: gencode %s, runtime %s. Version suffixes must be the same.",
                        location,
                        gencodeVersionString,
                        VERSION_STRING
                     )
                  );
               }
            } else {
               throw new RuntimeVersion.ProtobufRuntimeVersionException(
                  String.format(
                     "Detected incompatible Protobuf Gencode/Runtime versions when loading %s: gencode %s, runtime %s. Runtime version cannot be older than the linked gencode version.",
                     location,
                     gencodeVersionString,
                     VERSION_STRING
                  )
               );
            }
         }
      }
   }

   private static String versionString(int major, int minor, int patch, String suffix) {
      return String.format("%d.%d.%d%s", major, minor, patch, suffix);
   }

   private static boolean checkDisabled() {
      String disableFlag = System.getenv("TEMORARILY_DISABLE_PROTOBUF_VERSION_CHECK");
      return disableFlag != null && disableFlag.equals("true");
   }

   private RuntimeVersion() {
   }

   public static final class ProtobufRuntimeVersionException extends RuntimeException {
      public ProtobufRuntimeVersionException(String message) {
         super(message);
      }
   }

   public static enum RuntimeDomain {
      GOOGLE_INTERNAL,
      PUBLIC;
   }
}
