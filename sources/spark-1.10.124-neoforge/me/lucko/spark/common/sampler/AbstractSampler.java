package me.lucko.spark.common.sampler;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import me.lucko.spark.common.platform.SparkMetadata;
import me.lucko.spark.common.sampler.aggregator.DataAggregator;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.sampler.node.exporter.NodeExporter;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.sampler.window.ProtoTimeEncoder;
import me.lucko.spark.common.sampler.window.WindowStatisticsCollector;
import me.lucko.spark.common.util.classfinder.ClassFinder;
import me.lucko.spark.common.ws.ViewerSocket;
import me.lucko.spark.proto.SparkProtos;
import me.lucko.spark.proto.SparkSamplerProtos;

public abstract class AbstractSampler implements Sampler {
   protected final SparkPlatform platform;
   protected final int interval;
   protected final ThreadDumper threadDumper;
   protected long startTime = -1L;
   protected final long autoEndTime;
   protected boolean background;
   protected final WindowStatisticsCollector windowStatisticsCollector;
   protected final CompletableFuture<Sampler> future = new CompletableFuture<>();
   protected Map<String, GarbageCollectorStatistics> initialGcStats;
   protected List<ViewerSocket> viewerSockets = new CopyOnWriteArrayList<>();

   protected AbstractSampler(SparkPlatform platform, SamplerSettings settings) {
      this.platform = platform;
      this.interval = settings.interval();
      this.threadDumper = settings.threadDumper();
      this.autoEndTime = settings.autoEndTime();
      this.background = settings.runningInBackground();
      this.windowStatisticsCollector = new WindowStatisticsCollector(platform);
   }

   @Override
   public long getStartTime() {
      if (this.startTime == -1L) {
         throw new IllegalStateException("Not yet started");
      } else {
         return this.startTime;
      }
   }

   @Override
   public long getAutoEndTime() {
      return this.autoEndTime;
   }

   @Override
   public boolean isRunningInBackground() {
      return this.background;
   }

   @Override
   public CompletableFuture<Sampler> getFuture() {
      return this.future;
   }

   protected void recordInitialGcStats() {
      this.initialGcStats = GarbageCollectorStatistics.pollStats();
   }

   protected Map<String, GarbageCollectorStatistics> getInitialGcStats() {
      return this.initialGcStats;
   }

   @Override
   public Map<Integer, SparkProtos.WindowStatistics> exportWindowStatistics() {
      return this.windowStatisticsCollector.export();
   }

   @Override
   public void start() {
      this.startTime = System.currentTimeMillis();
   }

   @Override
   public void stop(boolean cancelled) {
      this.windowStatisticsCollector.stop();

      for (ViewerSocket viewerSocket : this.viewerSockets) {
         viewerSocket.processSamplerStopped(this);
      }
   }

   @Override
   public void attachSocket(ViewerSocket socket) {
      this.viewerSockets.add(socket);
   }

   @Override
   public Collection<ViewerSocket> getAttachedSockets() {
      return this.viewerSockets;
   }

   protected void processWindowRotate() {
      this.viewerSockets.removeIf(socket -> {
         if (!socket.isOpen()) {
            return true;
         } else {
            socket.processWindowRotate(this);
            return false;
         }
      });
   }

   protected void sendStatisticsToSocket() {
      try {
         this.viewerSockets.removeIf(socket -> !socket.isOpen());
         if (this.viewerSockets.isEmpty()) {
            return;
         }

         SparkProtos.PlatformStatistics platform = this.platform.getStatisticsProvider().getPlatformStatistics(this.getInitialGcStats(), false);
         SparkProtos.SystemStatistics system = this.platform.getStatisticsProvider().getSystemStatistics();

         for (ViewerSocket viewerSocket : this.viewerSockets) {
            viewerSocket.sendUpdatedStatistics(platform, system);
         }
      } catch (Exception var5) {
         this.platform.getPlugin().log(Level.WARNING, "Exception occurred while sending statistics to viewer", var5);
      }
   }

   protected void writeMetadataToProto(
      SparkSamplerProtos.SamplerData.Builder proto, SparkPlatform platform, CommandSender.Data creator, String comment, DataAggregator dataAggregator
   ) {
      SparkSamplerProtos.SamplerMetadata.Builder metadata = SparkSamplerProtos.SamplerMetadata.newBuilder()
         .setSamplerEngine(this.getType().asProto())
         .setSamplerMode(this.getMode().asProto())
         .setStartTime(this.startTime)
         .setInterval(this.interval)
         .setThreadDumper(this.threadDumper.getMetadata())
         .setDataAggregator(dataAggregator.getMetadata());
      SparkMetadata.gather(platform, creator, this.getInitialGcStats()).writeTo(metadata);
      if (comment != null) {
         metadata.setComment(comment);
      }

      int totalTicks = this.windowStatisticsCollector.getTotalTicks();
      if (totalTicks != -1) {
         metadata.setNumberOfTicks(totalTicks);
      }

      proto.setMetadata(metadata);
   }

   protected void writeDataToProto(
      SparkSamplerProtos.SamplerData.Builder proto,
      DataAggregator dataAggregator,
      Function<ProtoTimeEncoder, NodeExporter> nodeExporterFunction,
      ClassSourceLookup classSourceLookup,
      Supplier<ClassFinder> classFinderSupplier
   ) {
      List<ThreadNode> data = dataAggregator.exportData();
      data.sort(Comparator.comparing(ThreadNode::getThreadLabel));
      ClassSourceLookup.Visitor classSourceVisitor = ClassSourceLookup.createVisitor(classSourceLookup, classFinderSupplier);
      ProtoTimeEncoder timeEncoder = new ProtoTimeEncoder(this.getMode().valueTransformer(), data);
      int[] timeWindows = timeEncoder.getKeys();

      for (int timeWindow : timeWindows) {
         proto.addTimeWindows(timeWindow);
      }

      this.windowStatisticsCollector.ensureHasStatisticsForAllWindows(timeWindows);
      proto.putAllTimeWindowStatistics(this.windowStatisticsCollector.export());
      NodeExporter exporter = nodeExporterFunction.apply(timeEncoder);

      for (ThreadNode entry : data) {
         proto.addThreads(exporter.export(entry));
         classSourceVisitor.visit(entry);
      }

      if (classSourceVisitor.hasClassSourceMappings()) {
         proto.putAllClassSources(classSourceVisitor.getClassSourceMapping());
      }

      if (classSourceVisitor.hasMethodSourceMappings()) {
         proto.putAllMethodSources(classSourceVisitor.getMethodSourceMapping());
      }

      if (classSourceVisitor.hasLineSourceMappings()) {
         proto.putAllLineSources(classSourceVisitor.getLineSourceMapping());
      }
   }
}
