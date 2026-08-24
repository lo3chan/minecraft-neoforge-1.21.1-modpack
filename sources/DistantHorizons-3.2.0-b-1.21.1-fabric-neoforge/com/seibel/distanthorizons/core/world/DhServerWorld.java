package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelUnloadEvent;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.generation.PregenManager;
import com.seibel.distanthorizons.core.level.DhServerLevel;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class DhServerWorld extends AbstractDhServerWorld<DhServerLevel> {
   private final PregenManager pregenManager = new PregenManager();

   public PregenManager getPregenManager() {
      return this.pregenManager;
   }

   public DhServerWorld() {
      super(EWorldEnvironment.SERVER_ONLY);
      LOGGER.info("Started [" + DhServerWorld.class.getSimpleName() + "] of type [" + this.environment + "].");
   }

   public DhServerLevel getOrLoadLevel(@NotNull ILevelWrapper wrapper) {
      return !(wrapper instanceof IServerLevelWrapper)
         ? null
         : this.dhLevelByLevelWrapper
            .computeIfAbsent(
               wrapper,
               serverLevelWrapper -> {
                  try {
                     DhServerLevel level = new DhServerLevel(this.saveStructure, (IServerLevelWrapper)serverLevelWrapper, this.getServerPlayerStateManager());
                     ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelLoadEvent.class, new DhApiLevelLoadEvent.EventParam(wrapper));
                     return level;
                  } catch (Exception var7) {
                     LOGGER.fatal("Failed to load server level, error: [" + var7.getMessage() + "].", var7);
                     String r = "§c";
                     String y = "§e";
                     String cf = "§r";
                     ClientApi.INSTANCE
                        .showChatMessageNextFrame(
                           r
                              + "Distant Horizons: Server level loading failed."
                              + cf
                              + "\nUnable to load level ["
                              + y
                              + serverLevelWrapper.getDhIdentifier()
                              + cf
                              + "], LODs may not appear. See log for more information.\n"
                        );
                     return null;
                  }
               }
            );
   }

   @Override
   public boolean unloadLevel(@NotNull ILevelWrapper wrapper) {
      if (!(wrapper instanceof IServerLevelWrapper)) {
         return false;
      } else if (this.dhLevelByLevelWrapper.containsKey(wrapper)) {
         wrapper.onUnload();
         this.dhLevelByLevelWrapper.remove(wrapper).close();
         ApiEventInjector.INSTANCE.fireAllEvents(DhApiLevelUnloadEvent.class, new DhApiLevelUnloadEvent.EventParam(wrapper));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void close() {
      CompletableFuture<Void> runningPregen = this.pregenManager.getRunningPregen();
      if (runningPregen != null) {
         LOGGER.info("Stopping the running pregen task.");
         runningPregen.cancel(true);
      }

      super.close();
   }
}
