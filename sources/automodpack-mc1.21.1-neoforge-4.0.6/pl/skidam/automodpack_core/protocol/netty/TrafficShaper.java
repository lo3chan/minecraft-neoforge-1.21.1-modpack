package pl.skidam.automodpack_core.protocol.netty;

import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import pl.skidam.automodpack_core.GlobalVariables;

public class TrafficShaper {
   private final GlobalTrafficShapingHandler trafficShapingHandler;
   private ScheduledExecutorService executor = null;
   public static TrafficShaper trafficShaper;

   public TrafficShaper(ScheduledExecutorService executor) {
      close();
      if (executor == null) {
         executor = Executors.newSingleThreadScheduledExecutor();
         this.executor = executor;
      }

      long bandwidthLimit = GlobalVariables.serverConfig.bandwidthLimit * 1024L * 1024L / 8L;
      if (bandwidthLimit < 0L) {
         bandwidthLimit = 0L;
         GlobalVariables.LOGGER
            .warn("Invalid configured bandwidth limit ({} Mbps). Setting effective limit to 0 (unlimited).", GlobalVariables.serverConfig.bandwidthLimit);
      } else if (bandwidthLimit > 0L) {
         GlobalVariables.LOGGER.info("Setting bandwidth limit to {} Mbps.", GlobalVariables.serverConfig.bandwidthLimit);
      }

      this.trafficShapingHandler = new GlobalTrafficShapingHandler(executor, bandwidthLimit, 0L);
      trafficShaper = this;
   }

   public GlobalTrafficShapingHandler getTrafficShapingHandler() {
      return this.trafficShapingHandler;
   }

   public ScheduledExecutorService getExecutor() {
      return this.executor;
   }

   public static void close() {
      if (trafficShaper != null) {
         trafficShaper.getTrafficShapingHandler().release();
         if (trafficShaper.getExecutor() != null) {
            trafficShaper.getExecutor().shutdown();
         }

         trafficShaper = null;
      }
   }
}
