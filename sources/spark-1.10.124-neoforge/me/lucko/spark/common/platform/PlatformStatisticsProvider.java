package me.lucko.spark.common.platform;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.monitor.cpu.CpuInfo;
import me.lucko.spark.common.monitor.cpu.CpuMonitor;
import me.lucko.spark.common.monitor.disk.DiskUsage;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import me.lucko.spark.common.monitor.memory.MemoryInfo;
import me.lucko.spark.common.monitor.net.NetworkInterfaceAverages;
import me.lucko.spark.common.monitor.net.NetworkMonitor;
import me.lucko.spark.common.monitor.os.OperatingSystemInfo;
import me.lucko.spark.common.monitor.ping.PingStatistics;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import me.lucko.spark.common.platform.world.AsyncWorldInfoProvider;
import me.lucko.spark.common.platform.world.WorldStatisticsProvider;
import me.lucko.spark.proto.SparkProtos;

public class PlatformStatisticsProvider {
   private final SparkPlatform platform;

   public PlatformStatisticsProvider(SparkPlatform platform) {
      this.platform = platform;
   }

   public SparkProtos.SystemStatistics getSystemStatistics() {
      RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
      OperatingSystemInfo osInfo = OperatingSystemInfo.poll();
      String vmArgs = String.join(" ", runtimeBean.getInputArguments());
      SparkProtos.SystemStatistics.Builder builder = SparkProtos.SystemStatistics.newBuilder()
         .setCpu(
            SparkProtos.SystemStatistics.Cpu.newBuilder()
               .setThreads(Runtime.getRuntime().availableProcessors())
               .setProcessUsage(
                  SparkProtos.SystemStatistics.Cpu.Usage.newBuilder()
                     .setLast1M(CpuMonitor.processLoad1MinAvg())
                     .setLast15M(CpuMonitor.processLoad15MinAvg())
                     .build()
               )
               .setSystemUsage(
                  SparkProtos.SystemStatistics.Cpu.Usage.newBuilder()
                     .setLast1M(CpuMonitor.systemLoad1MinAvg())
                     .setLast15M(CpuMonitor.systemLoad15MinAvg())
                     .build()
               )
               .setModelName(CpuInfo.queryCpuModel())
               .build()
         )
         .setMemory(
            SparkProtos.SystemStatistics.Memory.newBuilder()
               .setPhysical(
                  SparkProtos.SystemStatistics.Memory.MemoryPool.newBuilder()
                     .setUsed(MemoryInfo.getUsedPhysicalMemory())
                     .setTotal(MemoryInfo.getTotalPhysicalMemory())
                     .build()
               )
               .setSwap(
                  SparkProtos.SystemStatistics.Memory.MemoryPool.newBuilder().setUsed(MemoryInfo.getUsedSwap()).setTotal(MemoryInfo.getTotalSwap()).build()
               )
               .build()
         )
         .setDisk(SparkProtos.SystemStatistics.Disk.newBuilder().setTotal(DiskUsage.getTotal()).setUsed(DiskUsage.getUsed()).build())
         .setOs(SparkProtos.SystemStatistics.Os.newBuilder().setArch(osInfo.arch()).setName(osInfo.name()).setVersion(osInfo.version()).build())
         .setJava(
            SparkProtos.SystemStatistics.Java.newBuilder()
               .setVendor(System.getProperty("java.vendor", "unknown"))
               .setVersion(System.getProperty("java.version", "unknown"))
               .setVendorVersion(System.getProperty("java.vendor.version", "unknown"))
               .setVmArgs(PlatformStatisticsProvider.VmArgRedactor.replace(vmArgs))
               .build()
         )
         .setJvm(
            SparkProtos.SystemStatistics.Jvm.newBuilder()
               .setName(System.getProperty("java.vm.name", "unknown"))
               .setVendor(System.getProperty("java.vm.vendor", "unknown"))
               .setVersion(System.getProperty("java.vm.version", "unknown"))
               .build()
         );
      long uptime = runtimeBean.getUptime();
      builder.setUptime(uptime);
      Map<String, GarbageCollectorStatistics> gcStats = GarbageCollectorStatistics.pollStats();
      gcStats.forEach(
         (name, statistics) -> builder.putGc(
            name,
            SparkProtos.SystemStatistics.Gc.newBuilder()
               .setTotal(statistics.getCollectionCount())
               .setAvgTime(statistics.getAverageCollectionTime())
               .setAvgFrequency(statistics.getAverageCollectionFrequency(uptime))
               .build()
         )
      );
      Map<String, NetworkInterfaceAverages> networkInterfaceStats = NetworkMonitor.systemAverages();
      networkInterfaceStats.forEach(
         (name, statistics) -> builder.putNet(
            name,
            SparkProtos.SystemStatistics.NetInterface.newBuilder()
               .setRxBytesPerSecond(rollingAvgProto(statistics.rxBytesPerSecond()))
               .setRxPacketsPerSecond(rollingAvgProto(statistics.rxPacketsPerSecond()))
               .setTxBytesPerSecond(rollingAvgProto(statistics.txBytesPerSecond()))
               .setTxPacketsPerSecond(rollingAvgProto(statistics.txPacketsPerSecond()))
               .build()
         )
      );
      return builder.build();
   }

   public SparkProtos.PlatformStatistics getPlatformStatistics(Map<String, GarbageCollectorStatistics> startingGcStatistics, boolean includeWorldStatistics) {
      SparkProtos.PlatformStatistics.Builder builder = SparkProtos.PlatformStatistics.newBuilder();
      SparkProtos.PlatformStatistics.Memory.Builder memory = SparkProtos.PlatformStatistics.Memory.newBuilder()
         .setHeap(memoryUsageProto(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()))
         .setNonHeap(memoryUsageProto(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()));

      for (MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
         if (memoryPool.getType() == MemoryType.HEAP) {
            MemoryUsage usage = memoryPool.getUsage();
            MemoryUsage collectionUsage = memoryPool.getCollectionUsage();
            if (usage.getMax() == -1L) {
               usage = new MemoryUsage(usage.getInit(), usage.getUsed(), usage.getCommitted(), usage.getCommitted());
            }

            memory.addPools(
               SparkProtos.PlatformStatistics.Memory.MemoryPool.newBuilder()
                  .setName(memoryPool.getName())
                  .setUsage(memoryUsageProto(usage))
                  .setCollectionUsage(memoryUsageProto(collectionUsage))
                  .build()
            );
         }
      }

      builder.setMemory(memory.build());
      long uptime = System.currentTimeMillis() - this.platform.getServerNormalOperationStartTime();
      builder.setUptime(uptime);
      if (startingGcStatistics != null) {
         Map<String, GarbageCollectorStatistics> gcStats = GarbageCollectorStatistics.pollStatsSubtractInitial(startingGcStatistics);
         gcStats.forEach(
            (name, statistics) -> builder.putGc(
               name,
               SparkProtos.PlatformStatistics.Gc.newBuilder()
                  .setTotal(statistics.getCollectionCount())
                  .setAvgTime(statistics.getAverageCollectionTime())
                  .setAvgFrequency(statistics.getAverageCollectionFrequency(uptime))
                  .build()
            )
         );
      }

      TickStatistics tickStatistics = this.platform.getTickStatistics();
      if (tickStatistics != null) {
         builder.setTps(
            SparkProtos.PlatformStatistics.Tps.newBuilder()
               .setLast1M(tickStatistics.tps1Min())
               .setLast5M(tickStatistics.tps5Min())
               .setLast15M(tickStatistics.tps15Min())
               .build()
         );
         if (tickStatistics.isDurationSupported()) {
            builder.setMspt(
               SparkProtos.PlatformStatistics.Mspt.newBuilder()
                  .setLast1M(rollingAvgProto(tickStatistics.duration1Min()))
                  .setLast5M(rollingAvgProto(tickStatistics.duration5Min()))
                  .build()
            );
         }
      }

      PingStatistics pingStatistics = this.platform.getPingStatistics();
      if (pingStatistics != null && pingStatistics.getPingAverage().getSamples() != 0) {
         builder.setPing(SparkProtos.PlatformStatistics.Ping.newBuilder().setLast15M(rollingAvgProto(pingStatistics.getPingAverage())).build());
      }

      List<CommandSender> senders = this.platform.getPlugin().getCommandSenders().collect(Collectors.toList());
      PlatformInfo.Type platformType = this.platform.getPlugin().getPlatformInfo().getType();
      if (platformType == PlatformInfo.Type.SERVER || platformType == PlatformInfo.Type.PROXY) {
         long playerCount = senders.size() - 1;
         builder.setPlayerCount(playerCount);
      }

      UUID anyOnlinePlayerUniqueId = senders.stream()
         .filter(CommandSender::isPlayer)
         .map(CommandSender::getUniqueId)
         .filter(uniqueId -> uniqueId.version() == 4 || uniqueId.version() == 3)
         .findAny()
         .orElse(null);
      builder.setOnlineMode(
         anyOnlinePlayerUniqueId == null
            ? SparkProtos.PlatformStatistics.OnlineMode.UNKNOWN
            : (anyOnlinePlayerUniqueId.version() == 4 ? SparkProtos.PlatformStatistics.OnlineMode.ONLINE : SparkProtos.PlatformStatistics.OnlineMode.OFFLINE)
      );
      if (includeWorldStatistics) {
         try {
            WorldStatisticsProvider worldStatisticsProvider = new WorldStatisticsProvider(
               new AsyncWorldInfoProvider(this.platform, this.platform.getPlugin().createWorldInfoProvider())
            );
            SparkProtos.WorldStatistics worldStatistics = worldStatisticsProvider.getWorldStatistics();
            if (worldStatistics != null) {
               builder.setWorld(worldStatistics);
            }
         } catch (Exception var15) {
            this.platform.getPlugin().log(Level.WARNING, "Failed to gather world statistics", var15);
         }
      }

      return builder.build();
   }

   public static SparkProtos.RollingAverageValues rollingAvgProto(DoubleAverageInfo info) {
      return SparkProtos.RollingAverageValues.newBuilder()
         .setMean(info.mean())
         .setMax(info.max())
         .setMin(info.min())
         .setMedian(info.median())
         .setPercentile95(info.percentile95th())
         .build();
   }

   public static SparkProtos.PlatformStatistics.Memory.MemoryUsage memoryUsageProto(MemoryUsage usage) {
      return SparkProtos.PlatformStatistics.Memory.MemoryUsage.newBuilder()
         .setUsed(usage.getUsed())
         .setCommitted(usage.getCommitted())
         .setInit(usage.getInit())
         .setMax(usage.getMax())
         .build();
   }

   static final class VmArgRedactor {
      private static final Pattern WINDOWS_USERNAME = Pattern.compile("C:\\\\Users\\\\\\w+");
      private static final Pattern MACOS_USERNAME = Pattern.compile("/Users/\\w+");
      private static final Pattern LINUX_USERNAME = Pattern.compile("/home/\\w+");

      static String replace(String input) {
         input = WINDOWS_USERNAME.matcher(input).replaceAll("C:\\\\Users\\\\<redacted>");
         input = MACOS_USERNAME.matcher(input).replaceAll("/Users/<redacted>");
         return LINUX_USERNAME.matcher(input).replaceAll("/home/<redacted>");
      }
   }
}
