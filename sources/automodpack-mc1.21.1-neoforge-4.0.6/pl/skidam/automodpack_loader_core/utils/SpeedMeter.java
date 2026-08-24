package pl.skidam.automodpack_loader_core.utils;

import java.util.concurrent.ConcurrentSkipListMap;

public class SpeedMeter {
   private final DownloadManager downloadManager;
   private final ConcurrentSkipListMap<Long, Long> bytesDownloadedPerSec = new ConcurrentSkipListMap<>();
   private static final int MAX_ENTRIES = 3;

   public SpeedMeter(DownloadManager downloadManager) {
      this.downloadManager = downloadManager;
   }

   public synchronized void addDownloadedBytes(long newBytes) {
      long bucketedTime = System.currentTimeMillis() / 1000L * 1000L;
      this.bytesDownloadedPerSec.merge(bucketedTime, newBytes, Long::sum);

      while (this.bytesDownloadedPerSec.size() > 3) {
         this.bytesDownloadedPerSec.pollFirstEntry();
      }
   }

   public long getAverageSpeedOfLastFewSeconds(int seconds) {
      long totalBytes = 0L;
      int count = 0;

      for (Long bytes : this.bytesDownloadedPerSec.values()) {
         totalBytes += bytes;
         count++;
      }

      return count >= seconds ? totalBytes / count : -1L;
   }

   public long getETAInSeconds() {
      long totalBytesRemaining = this.downloadManager.getTotalBytesRemaining();
      long speed = this.getAverageSpeedOfLastFewSeconds(3);
      return speed <= 0L ? -1L : totalBytesRemaining / speed;
   }

   public static String formatDownloadSpeedToMbps(long currentSpeedInBytes) {
      if (currentSpeedInBytes < 0L) {
         return "-1";
      } else {
         long bitsPerSecond = currentSpeedInBytes * 8L;
         double mbps = bitsPerSecond / 1000000.0;
         return String.format("%.2f Mbps", mbps);
      }
   }

   public static String formatETAToSeconds(long seconds) {
      if (++seconds < 1L) {
         return "-1";
      } else {
         return seconds < 3600L
            ? String.format("%02d:%02d", seconds / 60L, seconds % 60L)
            : String.format("%02d:%02d:%02d", seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
      }
   }
}
