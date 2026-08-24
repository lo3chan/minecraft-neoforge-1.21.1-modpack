package me.lucko.spark.common.sampler;

public class SamplerSettings {
   private final int interval;
   private final ThreadDumper threadDumper;
   private final ThreadGrouper threadGrouper;
   private final long autoEndTime;
   private final boolean runningInBackground;
   private final boolean ignoreSleeping;

   public SamplerSettings(
      int interval, ThreadDumper threadDumper, ThreadGrouper threadGrouper, long autoEndTime, boolean runningInBackground, boolean ignoreSleeping
   ) {
      this.interval = interval;
      this.threadDumper = threadDumper;
      this.threadGrouper = threadGrouper;
      this.autoEndTime = autoEndTime;
      this.runningInBackground = runningInBackground;
      this.ignoreSleeping = ignoreSleeping;
   }

   public int interval() {
      return this.interval;
   }

   public ThreadDumper threadDumper() {
      return this.threadDumper;
   }

   public ThreadGrouper threadGrouper() {
      return this.threadGrouper;
   }

   public long autoEndTime() {
      return this.autoEndTime;
   }

   public boolean runningInBackground() {
      return this.runningInBackground;
   }

   public boolean ignoreSleeping() {
      return this.ignoreSleeping;
   }
}
