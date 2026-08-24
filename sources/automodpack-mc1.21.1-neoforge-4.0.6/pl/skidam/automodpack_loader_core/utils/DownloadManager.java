package pl.skidam.automodpack_loader_core.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPInputStream;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.protocol.DownloadClient;
import pl.skidam.automodpack_core.utils.CustomFileUtils;
import pl.skidam.automodpack_core.utils.CustomThreadFactoryBuilder;
import pl.skidam.automodpack_core.utils.FileInspection;
import pl.skidam.automodpack_core.utils.Json;

public class DownloadManager {
   private static final int MAX_DOWNLOADS_IN_PROGRESS = 5;
   private static final int MAX_DOWNLOAD_ATTEMPTS = 2;
   private final ExecutorService DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(
      5, new CustomThreadFactoryBuilder().setNameFormat("AutoModpackDownload-%d").build()
   );
   private DownloadClient downloadClient = null;
   private boolean cancelled = false;
   private final Map<FileInspection.HashPathPair, DownloadManager.QueuedDownload> queuedDownloads = new ConcurrentHashMap<>();
   public final Map<FileInspection.HashPathPair, DownloadManager.DownloadData> downloadsInProgress = new ConcurrentHashMap<>();
   private long bytesDownloaded = 0L;
   private long bytesToDownload = 0L;
   private int addedToQueue = 0;
   private int downloaded = 0;
   private final Semaphore semaphore = new Semaphore(0);
   private final SpeedMeter speedMeter = new SpeedMeter(this);

   public DownloadManager() {
   }

   public DownloadManager(long bytesToDownload) {
      this.bytesToDownload = bytesToDownload;
   }

   public void attachDownloadClient(DownloadClient downloadClient) {
      this.downloadClient = downloadClient;
   }

   public void download(Path file, String sha1, List<String> urls, Runnable successCallback, Runnable failureCallback) {
      FileInspection.HashPathPair hashPathPair = new FileInspection.HashPathPair(sha1, file);
      if (!this.queuedDownloads.containsKey(hashPathPair)) {
         this.queuedDownloads.put(hashPathPair, new DownloadManager.QueuedDownload(file, urls, 0, successCallback, failureCallback));
         this.addedToQueue++;
         this.downloadNext();
      }
   }

   private void downloadTask(FileInspection.HashPathPair hashPathPair, DownloadManager.QueuedDownload queuedDownload) throws Exception {
      GlobalVariables.LOGGER.info("Downloading {} - {}", queuedDownload.file.getFileName(), queuedDownload.urls);
      int numberOfIndexes = queuedDownload.urls.size();
      int urlIndex = Math.min(queuedDownload.attempts / 2, numberOfIndexes);
      String url = "host";
      if (queuedDownload.urls.size() > urlIndex) {
         url = queuedDownload.urls.get(urlIndex);
      }

      boolean interrupted = false;

      try {
         if (url != null && !Objects.equals(url, "host") && queuedDownload.attempts < 2 * numberOfIndexes) {
            this.httpDownloadFile(url, hashPathPair, queuedDownload);
         } else if (this.downloadClient != null) {
            this.hostDownloadFile(hashPathPair, queuedDownload);
         } else {
            GlobalVariables.LOGGER.error("No download client attached, can't download file - {}", queuedDownload.file.getFileName());
         }
      } catch (InterruptedException var16) {
         interrupted = true;
      } catch (SocketTimeoutException var17) {
         GlobalVariables.LOGGER.warn("Timeout - {} - {} - {}", queuedDownload.file, var17, var17.fillInStackTrace());
      } catch (Exception var18) {
         GlobalVariables.LOGGER.warn("Error while downloading file - {} - {} - {}", queuedDownload.file, var18, var18.fillInStackTrace());
      } finally {
         this.downloadsInProgress.remove(hashPathPair);
         boolean failed = true;
         if (Files.exists(queuedDownload.file)) {
            String hash = CustomFileUtils.getHash(queuedDownload.file);
            if (Objects.equals(hash, hashPathPair.hash())) {
               failed = false;
               this.downloaded++;
               GlobalVariables.LOGGER.info("Successfully downloaded {} from {}", queuedDownload.file.getFileName(), url);
               queuedDownload.successCallback.run();
               this.semaphore.release();
            }
         }

         if (failed) {
            this.bytesToDownload = this.bytesToDownload + queuedDownload.file.toFile().length();
            CustomFileUtils.executeOrder66(queuedDownload.file);
            if (!interrupted) {
               if (queuedDownload.attempts < (numberOfIndexes + 1) * 2) {
                  GlobalVariables.LOGGER.warn("Download of {} failed, retrying!", queuedDownload.file.getFileName());
                  queuedDownload.attempts++;
                  this.queuedDownloads.put(hashPathPair, queuedDownload);
               } else {
                  GlobalVariables.LOGGER.error("Download of {} failed!", queuedDownload.file.getFileName());
                  queuedDownload.failureCallback.run();
                  this.semaphore.release();
               }
            }
         }

         if (!interrupted) {
            this.downloadNext();
         }
      }
   }

   private synchronized void downloadNext() {
      if (this.downloadsInProgress.size() < 5 && !this.queuedDownloads.isEmpty()) {
         FileInspection.HashPathPair hashAndPath = this.queuedDownloads.keySet().stream().findFirst().get();
         DownloadManager.QueuedDownload queuedDownload = this.queuedDownloads.remove(hashAndPath);
         if (queuedDownload == null) {
            return;
         }

         CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
               this.downloadTask(hashAndPath, queuedDownload);
            } catch (Exception var4) {
               GlobalVariables.LOGGER.error("Error while downloading file - {}", queuedDownload.file.getFileName(), var4);
            }
         }, this.DOWNLOAD_EXECUTOR);
         this.downloadsInProgress.put(hashAndPath, new DownloadManager.DownloadData(future, queuedDownload.file));
      }
   }

   private void hostDownloadFile(FileInspection.HashPathPair hashPathPair, DownloadManager.QueuedDownload queuedDownload) throws IOException, InterruptedException {
      Path outFile = queuedDownload.file;
      if (Files.exists(outFile)) {
         if (Objects.equals(hashPathPair.hash(), CustomFileUtils.getHash(outFile))) {
            return;
         }

         CustomFileUtils.executeOrder66(outFile);
      }

      CustomFileUtils.setupFilePaths(outFile);
      CompletableFuture<Path> future = this.downloadClient.downloadFile(hashPathPair.hash().getBytes(StandardCharsets.UTF_8), outFile, bytes -> {
         this.bytesDownloaded += bytes;
         this.speedMeter.addDownloadedBytes(bytes);
      });
      future.join();
   }

   private void httpDownloadFile(String url, FileInspection.HashPathPair hashPathPair, DownloadManager.QueuedDownload queuedDownload) throws IOException, InterruptedException {
      Path outFile = queuedDownload.file;
      if (Files.exists(outFile)) {
         if (Objects.equals(hashPathPair.hash(), CustomFileUtils.getHash(outFile))) {
            return;
         }

         CustomFileUtils.executeOrder66(outFile);
      }

      CustomFileUtils.setupFilePaths(outFile);
      URLConnection connection = this.getHttpConnection(url);

      try (
         OutputStream outputStream = new FileOutputStream(outFile.toFile());
         InputStream rawInputStream = new BufferedInputStream(connection.getInputStream(), 262144);
         InputStream inputStream = (InputStream)("gzip".equals(connection.getHeaderField("Content-Encoding"))
            ? new GZIPInputStream(rawInputStream)
            : rawInputStream);
      ) {
         byte[] buffer = new byte[262144];

         int bytesRead;
         while ((bytesRead = inputStream.read(buffer)) != -1) {
            this.bytesDownloaded += bytesRead;
            this.speedMeter.addDownloadedBytes(bytesRead);
            outputStream.write(buffer, 0, bytesRead);
            if (Thread.currentThread().isInterrupted()) {
               throw new InterruptedException("Download got cancelled");
            }
         }
      }
   }

   private URLConnection getHttpConnection(String url) throws IOException {
      GlobalVariables.LOGGER.info("Downloading from {}", url);
      URL connectionUrl = new URL(url);
      if (!isCurseForgeCdn(connectionUrl)) {
         return this.openHttpConnection(connectionUrl, false);
      } else {
         boolean authenticate = true;
         int redirects = 0;

         while (redirects < 5) {
            HttpURLConnection connection = (HttpURLConnection)this.openHttpConnection(connectionUrl, authenticate);
            connection.setInstanceFollowRedirects(false);
            int responseCode = connection.getResponseCode();
            if (responseCode >= 300 && responseCode < 400) {
               String location = connection.getHeaderField("Location");
               connection.disconnect();
               if (location != null && !location.isBlank()) {
                  connectionUrl = new URL(connectionUrl, location);
                  if (!"https".equalsIgnoreCase(connectionUrl.getProtocol())) {
                     throw new IOException("Refusing CurseForge CDN redirect to non-HTTPS URL");
                  }

                  authenticate = false;
                  redirects++;
                  continue;
               }

               throw new IOException("CurseForge CDN redirect has no location");
            }

            return connection;
         }

         throw new IOException("Too many CurseForge CDN redirects");
      }
   }

   private URLConnection openHttpConnection(URL url, boolean authenticateCurseForge) throws IOException {
      URLConnection connection = url.openConnection();
      connection.addRequestProperty("Accept-Encoding", "gzip");
      connection.addRequestProperty("User-Agent", "github/skidamek/automodpack/" + GlobalVariables.AM_VERSION);
      if (authenticateCurseForge) {
         connection.addRequestProperty("x-api-key", Json.getCurseForgeApiKey());
      }

      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      return connection;
   }

   private static boolean isCurseForgeCdn(URL url) {
      return "https".equalsIgnoreCase(url.getProtocol())
         && "edge.forgecdn.net".equalsIgnoreCase(url.getHost())
         && url.getUserInfo() == null
         && (url.getPort() == -1 || url.getPort() == 443);
   }

   public void joinAll() throws InterruptedException {
      this.semaphore.acquire(this.addedToQueue);
      if (this.DOWNLOAD_EXECUTOR.isShutdown()) {
         throw new InterruptedException();
      } else {
         this.semaphore.release(this.addedToQueue);
      }
   }

   public SpeedMeter getSpeedMeter() {
      return this.speedMeter;
   }

   public long getTotalBytesRemaining() {
      return this.bytesToDownload - this.bytesDownloaded;
   }

   public int getTotalPercentageOfFileSizeDownloaded() {
      if (this.bytesDownloaded != 0L && this.bytesToDownload != 0L) {
         int percentage = (int)(this.bytesDownloaded * 100L / this.bytesToDownload);
         return Math.max(0, Math.min(100, percentage));
      } else {
         return 0;
      }
   }

   public String getStage() {
      return this.downloaded + "/" + this.addedToQueue;
   }

   public boolean isRunning() {
      return !this.DOWNLOAD_EXECUTOR.isShutdown();
   }

   public boolean isCanceled() {
      return this.cancelled;
   }

   public void cancelAllAndShutdown() {
      this.cancelled = true;
      this.queuedDownloads.clear();
      this.downloadsInProgress.forEach((url, downloadData) -> {
         downloadData.future.cancel(true);
         CustomFileUtils.executeOrder66(downloadData.file);
      });
      this.semaphore.release(this.addedToQueue);
      this.downloadsInProgress.clear();
      this.downloaded = 0;
      this.addedToQueue = 0;
      if (this.downloadClient != null) {
         this.downloadClient.close();
      }

      this.DOWNLOAD_EXECUTOR.shutdown();
   }

   public static class DownloadData {
      public CompletableFuture<Void> future;
      public Path file;

      DownloadData(CompletableFuture<Void> future, Path file) {
         this.future = future;
         this.file = file;
      }

      public String getFileName() {
         return this.file.getFileName().toString();
      }
   }

   public static class QueuedDownload {
      private final Path file;
      private final List<String> urls;
      private int attempts;
      private final Runnable successCallback;
      private final Runnable failureCallback;

      public QueuedDownload(Path file, List<String> urls, int attempts, Runnable successCallback, Runnable failureCallback) {
         this.file = file;
         this.urls = urls;
         this.attempts = attempts;
         this.successCallback = successCallback;
         this.failureCallback = failureCallback;
      }
   }
}
