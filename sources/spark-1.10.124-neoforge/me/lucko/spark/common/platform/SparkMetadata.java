package me.lucko.spark.common.platform;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import me.lucko.spark.common.platform.serverconfig.ServerConfigProvider;
import me.lucko.spark.common.sampler.source.SourceMetadata;
import me.lucko.spark.proto.SparkHeapProtos;
import me.lucko.spark.proto.SparkProtos;
import me.lucko.spark.proto.SparkSamplerProtos;

public class SparkMetadata {
   private final CommandSender.Data creator;
   private final SparkProtos.PlatformMetadata platformMetadata;
   private final SparkProtos.PlatformStatistics platformStatistics;
   private final SparkProtos.SystemStatistics systemStatistics;
   private final long generatedTime;
   private final Map<String, String> serverConfigurations;
   private final Collection<SourceMetadata> sources;
   private final Map<String, String> extraPlatformMetadata;

   public static SparkMetadata gather(SparkPlatform platform, CommandSender.Data creator, Map<String, GarbageCollectorStatistics> initialGcStats) {
      SparkProtos.PlatformMetadata platformMetadata = platform.getPlugin().getPlatformInfo().toData().toProto();
      SparkProtos.PlatformStatistics platformStatistics = null;

      try {
         platformStatistics = platform.getStatisticsProvider().getPlatformStatistics(initialGcStats, true);
      } catch (Exception var15) {
         platform.getPlugin().log(Level.WARNING, "Failed to gather platform statistics", var15);
      }

      SparkProtos.SystemStatistics systemStatistics = null;

      try {
         systemStatistics = platform.getStatisticsProvider().getSystemStatistics();
      } catch (Exception var14) {
         platform.getPlugin().log(Level.WARNING, "Failed to gather system statistics", var14);
      }

      long generatedTime = System.currentTimeMillis();
      Map<String, String> serverConfigurations = null;

      try {
         ServerConfigProvider serverConfigProvider = platform.getPlugin().createServerConfigProvider();
         if (serverConfigProvider != null) {
            serverConfigurations = serverConfigProvider.export();
         }
      } catch (Exception var13) {
         platform.getPlugin().log(Level.WARNING, "Failed to gather server configurations", var13);
      }

      Collection<SourceMetadata> sources = platform.getPlugin().getKnownSources();
      Map<String, String> extraPlatformMetadata = null;

      try {
         MetadataProvider extraMetadataProvider = platform.getPlugin().createExtraMetadataProvider();
         if (extraMetadataProvider != null) {
            extraPlatformMetadata = extraMetadataProvider.export();
         }
      } catch (Exception var12) {
         platform.getPlugin().log(Level.WARNING, "Failed to gather extra platform metadata", var12);
      }

      return new SparkMetadata(
         creator, platformMetadata, platformStatistics, systemStatistics, generatedTime, serverConfigurations, sources, extraPlatformMetadata
      );
   }

   public SparkMetadata(
      CommandSender.Data creator,
      SparkProtos.PlatformMetadata platformMetadata,
      SparkProtos.PlatformStatistics platformStatistics,
      SparkProtos.SystemStatistics systemStatistics,
      long generatedTime,
      Map<String, String> serverConfigurations,
      Collection<SourceMetadata> sources,
      Map<String, String> extraPlatformMetadata
   ) {
      this.creator = creator;
      this.platformMetadata = platformMetadata;
      this.platformStatistics = platformStatistics;
      this.systemStatistics = systemStatistics;
      this.generatedTime = generatedTime;
      this.serverConfigurations = serverConfigurations;
      this.sources = sources;
      this.extraPlatformMetadata = extraPlatformMetadata;
   }

   public void writeTo(SparkProtos.HealthMetadata.Builder builder) {
      if (this.creator != null) {
         builder.setCreator(this.creator.toProto());
      }

      if (this.platformMetadata != null) {
         builder.setPlatformMetadata(this.platformMetadata);
      }

      if (this.platformStatistics != null) {
         builder.setPlatformStatistics(this.platformStatistics);
      }

      if (this.systemStatistics != null) {
         builder.setSystemStatistics(this.systemStatistics);
      }

      builder.setGeneratedTime(this.generatedTime);
      if (this.serverConfigurations != null) {
         builder.putAllServerConfigurations(this.serverConfigurations);
      }

      if (this.sources != null) {
         for (SourceMetadata source : this.sources) {
            builder.putSources(source.getName().toLowerCase(Locale.ROOT), source.toProto());
         }
      }

      if (this.extraPlatformMetadata != null) {
         builder.putAllExtraPlatformMetadata(this.extraPlatformMetadata);
      }
   }

   public void writeTo(SparkSamplerProtos.SamplerMetadata.Builder builder) {
      if (this.creator != null) {
         builder.setCreator(this.creator.toProto());
      }

      if (this.platformMetadata != null) {
         builder.setPlatformMetadata(this.platformMetadata);
      }

      if (this.platformStatistics != null) {
         builder.setPlatformStatistics(this.platformStatistics);
      }

      if (this.systemStatistics != null) {
         builder.setSystemStatistics(this.systemStatistics);
      }

      builder.setEndTime(this.generatedTime);
      if (this.serverConfigurations != null) {
         builder.putAllServerConfigurations(this.serverConfigurations);
      }

      if (this.sources != null) {
         for (SourceMetadata source : this.sources) {
            builder.putSources(source.getName().toLowerCase(Locale.ROOT), source.toProto());
         }
      }

      if (this.extraPlatformMetadata != null) {
         builder.putAllExtraPlatformMetadata(this.extraPlatformMetadata);
      }
   }

   public void writeTo(SparkHeapProtos.HeapMetadata.Builder builder) {
      if (this.creator != null) {
         builder.setCreator(this.creator.toProto());
      }

      if (this.platformMetadata != null) {
         builder.setPlatformMetadata(this.platformMetadata);
      }

      if (this.platformStatistics != null) {
         builder.setPlatformStatistics(this.platformStatistics);
      }

      if (this.systemStatistics != null) {
         builder.setSystemStatistics(this.systemStatistics);
      }

      builder.setGeneratedTime(this.generatedTime);
      if (this.serverConfigurations != null) {
         builder.putAllServerConfigurations(this.serverConfigurations);
      }

      if (this.sources != null) {
         for (SourceMetadata source : this.sources) {
            builder.putSources(source.getName().toLowerCase(Locale.ROOT), source.toProto());
         }
      }

      if (this.extraPlatformMetadata != null) {
         builder.putAllExtraPlatformMetadata(this.extraPlatformMetadata);
      }
   }
}
