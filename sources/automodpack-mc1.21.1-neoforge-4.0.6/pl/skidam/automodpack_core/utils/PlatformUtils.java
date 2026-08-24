package pl.skidam.automodpack_core.utils;

import java.util.Locale;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.protocol.compression.CompressionCodec;
import pl.skidam.automodpack_core.protocol.compression.CompressionFactory;

public class PlatformUtils {
   public static final boolean IS_MAC;
   public static final boolean IS_WIN;
   private static Boolean zstd;

   public static boolean canUseZstd() {
      if (zstd != null) {
         return zstd;
      } else {
         synchronized (PlatformUtils.class) {
            if (zstd != null) {
               return zstd;
            } else {
               try {
                  CompressionCodec compressionCodec = CompressionFactory.getCodec((byte)1);
                  zstd = compressionCodec.isInitialized();
               } catch (Throwable var3) {
                  zstd = false;
                  GlobalVariables.LOGGER.warn("Desired compression codec failed to initialize, falling back to Gzip");
               }

               return zstd;
            }
         }
      }
   }

   static {
      String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      IS_MAC = os.contains("mac");
      IS_WIN = os.contains("win");
   }
}
