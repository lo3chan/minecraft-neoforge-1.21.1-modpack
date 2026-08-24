package DistantHorizons.libraries.electronwill.nightconfig.core.io;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public final class IoUtils {
   static String[] splitOnce(String s, char c) {
      int i = s.lastIndexOf(c);
      return i < 0 ? new String[]{s} : new String[]{s.substring(0, i), s.substring(i + 1, s.length())};
   }

   public static String tempConfigFileName(Path originalFile) {
      String filename = originalFile.getFileName().toString();
      String[] parts = splitOnce(filename, '.');
      return parts.length == 1 ? filename + ".new.tmp" : parts[0] + ".new.tmp." + parts[1];
   }

   public static void retryIfAccessDenied(String name, IoUtils.IoRunnable r) throws IOException {
      retryIfAccessDenied(name, r, IoUtils.OptionHolder.RETRY_MAX_TIMES, IoUtils.OptionHolder.RETRY_DELAY_MILLIS, TimeUnit.MILLISECONDS);
   }

   public static void retryIfAccessDenied(String name, IoUtils.IoRunnable r, int maxRetries, long retryDelay, TimeUnit delayUnit) throws IOException {
      AccessDeniedException lastException = null;
      int i = 0;

      while (i <= maxRetries) {
         try {
            r.run();
            return;
         } catch (AccessDeniedException var11) {
            lastException = var11;

            try {
               Thread.sleep(delayUnit.toMillis(retryDelay));
            } catch (InterruptedException var10) {
            }

            i++;
         } catch (IOException var12) {
            throw var12;
         }
      }

      String msg = String.format("IO operation '%s' failed after %s attempts", name, maxRetries);
      throw new IoUtils.RetryFailedException(msg, lastException);
   }

   @FunctionalInterface
   public interface IoRunnable {
      void run() throws IOException;
   }

   static class OptionHolder {
      static final long RETRY_DELAY_MILLIS;
      static final int RETRY_MAX_TIMES;

      static {
         boolean isWindows = System.getProperty("os.name", "?").trim().toLowerCase().startsWith("windows");
         String delayProps = System.getProperty("nightconfig.accessDeniedRetryDelayMillis", "?");

         long delay;
         try {
            delay = Long.parseLong(delayProps);
         } catch (NumberFormatException var7) {
            delay = 500L;
         }

         String timesProps = System.getProperty("nightconfig.accessDeniedRetryMaxTimes", "?");

         int times;
         try {
            times = Integer.parseInt(timesProps);
         } catch (NumberFormatException var8) {
            times = isWindows ? 3 : 1;
         }

         RETRY_DELAY_MILLIS = delay;
         RETRY_MAX_TIMES = times;
      }
   }

   public static final class RetryFailedException extends IOException {
      RetryFailedException(String msg, IOException cause) {
         super(msg, cause);
      }
   }
}
