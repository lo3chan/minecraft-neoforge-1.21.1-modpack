package net.mehvahdjukaar.moonlight.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import org.jetbrains.annotations.Nullable;

public final class FileDownloadUtils {
   private static final int CONNECT_TIMEOUT = 30000;
   private static final int READ_TIMEOUT = 30000;
   private static final int MAX_ATTEMPTS = 8;

   public static void download(String urlStr, Path target) throws IOException {
      download(urlStr, target, null, null, null);
   }

   public static void download(String urlStr, Path target, @Nullable String userAgent) throws IOException {
      download(urlStr, target, userAgent, null, null);
   }

   public static void download(String urlStr, Path target, @Nullable String userAgent, @Nullable FileDownloadUtils.ProgressCallback progressCallback) throws IOException {
      download(urlStr, target, userAgent, progressCallback, null);
   }

   public static void download(
      String urlStr,
      Path target,
      @Nullable String userAgent,
      @Nullable FileDownloadUtils.ProgressCallback progressCallback,
      @Nullable FileDownloadUtils.RetryCallback retryCallback
   ) throws IOException {
      validateUrl(urlStr);
      Path tmp = target.resolveSibling(target.getFileName() + ".part");
      long downloadedBytes = Files.exists(tmp) ? Files.size(tmp) : 0L;
      Moonlight.LOGGER.info("Downloading {} ...", urlStr);
      int attempt = 0;

      while (true) {
         try {
            downloadAttempt(urlStr, tmp, downloadedBytes, userAgent, progressCallback);
            break;
         } catch (IOException var12) {
            if (var12 instanceof FileDownloadUtils.HttpStatusException hse && !hse.isRetryable()) {
               Files.deleteIfExists(tmp);
               throw var12;
            }

            if (++attempt >= 8) {
               Files.deleteIfExists(tmp);
               throw new IOException("Failed to download after 8 attempts: " + urlStr, var12);
            }

            Moonlight.LOGGER.warn("Download attempt {} failed: {}. Retrying...", attempt, var12.getMessage());
            if (retryCallback != null) {
               retryCallback.onRetry(attempt, 8, var12);
            }

            try {
               Thread.sleep(1000L * attempt);
            } catch (InterruptedException var11) {
               Thread.currentThread().interrupt();
               throw new IOException("Download interrupted", var11);
            }

            downloadedBytes = Files.exists(tmp) ? Files.size(tmp) : 0L;
         }
      }

      Moonlight.LOGGER.info("Downloaded {} bytes from {}", downloadedBytes, urlStr);
      moveFileAtomically(tmp, target);
   }

   public static byte[] readBytes(String urlStr) throws IOException {
      validateUrl(urlStr);
      HttpURLConnection conn = createConnection(urlStr, 0L, null);

      byte[] var4;
      try {
         int code = conn.getResponseCode();
         if (code < 200 || code >= 300) {
            throw new FileDownloadUtils.HttpStatusException(code, urlStr);
         }

         try (InputStream in = conn.getInputStream()) {
            var4 = in.readAllBytes();
         }
      } finally {
         conn.disconnect();
      }

      return var4;
   }

   public static String readString(String urlStr) throws IOException {
      return new String(readBytes(urlStr), StandardCharsets.UTF_8);
   }

   private static void validateUrl(String urlStr) throws IOException {
      try {
         URI uri = new URI(urlStr);
         String scheme = uri.getScheme();
         if (scheme == null || !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            throw new IOException("Unsupported protocol: " + scheme + ". Only HTTP/HTTPS are allowed.");
         }
      } catch (URISyntaxException var3) {
         throw new IOException("Malformed URL: " + urlStr, var3);
      }
   }

   private static void moveFileAtomically(Path source, Path target) throws IOException {
      try {
         Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException | UnsupportedOperationException var3) {
         Moonlight.LOGGER.info("Atomic move not supported, using standard move: {}", var3.getMessage());
         Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static HttpURLConnection createConnection(String urlStr, long startOffset, @Nullable String userAgent) throws IOException {
      URL url = URI.create(urlStr).toURL();
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setConnectTimeout(30000);
      conn.setReadTimeout(30000);
      if (userAgent != null) {
         conn.setRequestProperty("User-Agent", userAgent);
      }

      if (startOffset > 0L) {
         conn.setRequestProperty("Range", "bytes=" + startOffset + "-");
      }

      return conn;
   }

   private static void downloadAttempt(
      String urlStr, Path tmp, long startOffset, @Nullable String userAgent, @Nullable FileDownloadUtils.ProgressCallback progressCallback
   ) throws IOException {
      HttpURLConnection conn = createConnection(urlStr, startOffset, userAgent);
      long actualStartOffset = startOffset;
      boolean rangeSupported = true;

      try {
         int responseCode = conn.getResponseCode();
         if (startOffset > 0L && responseCode != 206) {
            Moonlight.LOGGER.info("Server does not support range requests (code {}). Restarting download from 0.", responseCode);
            conn.disconnect();
            rangeSupported = false;
            actualStartOffset = 0L;
            conn = createConnection(urlStr, 0L, userAgent);
            responseCode = conn.getResponseCode();
         }

         if (responseCode < 200 || responseCode >= 300) {
            throw new FileDownloadUtils.HttpStatusException(responseCode, urlStr);
         }

         long contentLength = conn.getContentLengthLong();
         long totalExpected = rangeSupported && responseCode == 206 ? contentLength + actualStartOffset : contentLength;
         boolean append = rangeSupported && actualStartOffset > 0L && responseCode == 206;
         StandardOpenOption[] writeOptions = append
            ? new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
            : new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

         try (
            InputStream in = conn.getInputStream();
            OutputStream out = Files.newOutputStream(tmp, writeOptions);
         ) {
            byte[] buffer = new byte[16384];
            long downloaded = actualStartOffset;
            int lastPercent = -1;

            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
               out.write(buffer, 0, bytesRead);
               downloaded += bytesRead;
               if (totalExpected > 0L) {
                  int percent = (int)(downloaded * 100L / totalExpected);
                  if (percent != lastPercent) {
                     Moonlight.LOGGER.info("Downloading {} ... {}%", tmp.getFileName(), percent);
                     if (progressCallback != null) {
                        progressCallback.onProgress(percent);
                     }

                     lastPercent = percent;
                  }
               }
            }
         }

         if (totalExpected > 0L && Files.size(tmp) != totalExpected) {
            throw new IOException(String.format("Incomplete download: expected %d bytes, got %d bytes", totalExpected, Files.size(tmp)));
         }
      } finally {
         conn.disconnect();
      }
   }

   public static class HttpStatusException extends IOException {
      public final int statusCode;

      public HttpStatusException(int statusCode, String url) {
         super(String.format("HTTP %d for URL: %s", statusCode, url));
         this.statusCode = statusCode;
      }

      public boolean isRetryable() {
         return this.statusCode >= 400 && this.statusCode < 500 ? this.statusCode == 408 || this.statusCode == 429 : true;
      }
   }

   @FunctionalInterface
   public interface ProgressCallback {
      void onProgress(int var1);
   }

   @FunctionalInterface
   public interface RetryCallback {
      void onRetry(int var1, int var2, IOException var3);
   }
}
