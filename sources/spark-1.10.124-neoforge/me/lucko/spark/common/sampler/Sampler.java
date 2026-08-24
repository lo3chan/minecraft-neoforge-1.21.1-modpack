package me.lucko.spark.common.sampler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.sampler.java.MergeStrategy;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.ws.ViewerSocket;
import me.lucko.spark.proto.SparkProtos;
import me.lucko.spark.proto.SparkSamplerProtos;

public interface Sampler {
   void start();

   void stop(boolean var1);

   void attachSocket(ViewerSocket var1);

   Collection<ViewerSocket> getAttachedSockets();

   long getStartTime();

   long getAutoEndTime();

   boolean isRunningInBackground();

   SamplerType getType();

   SamplerMode getMode();

   CompletableFuture<Sampler> getFuture();

   Map<Integer, SparkProtos.WindowStatistics> exportWindowStatistics();

   SparkSamplerProtos.SamplerData toProto(SparkPlatform var1, Sampler.ExportProps var2);

   public static final class ExportProps {
      private CommandSender.Data creator;
      private String comment;
      private MergeStrategy mergeStrategy;
      private Supplier<ClassSourceLookup> classSourceLookup;
      private SparkSamplerProtos.SocketChannelInfo channelInfo;

      public CommandSender.Data creator() {
         return this.creator;
      }

      public String comment() {
         return this.comment;
      }

      public MergeStrategy mergeStrategy() {
         return this.mergeStrategy;
      }

      public Supplier<ClassSourceLookup> classSourceLookup() {
         return this.classSourceLookup;
      }

      public SparkSamplerProtos.SocketChannelInfo channelInfo() {
         return this.channelInfo;
      }

      public Sampler.ExportProps creator(CommandSender.Data creator) {
         this.creator = creator;
         return this;
      }

      public Sampler.ExportProps comment(String comment) {
         this.comment = comment;
         return this;
      }

      public Sampler.ExportProps mergeStrategy(MergeStrategy mergeStrategy) {
         this.mergeStrategy = mergeStrategy;
         return this;
      }

      public Sampler.ExportProps classSourceLookup(Supplier<ClassSourceLookup> classSourceLookup) {
         this.classSourceLookup = classSourceLookup;
         return this;
      }

      public Sampler.ExportProps channelInfo(SparkSamplerProtos.SocketChannelInfo channelInfo) {
         this.channelInfo = channelInfo;
         return this;
      }
   }
}
