package mezz.jei.neoforge.startup;

import java.lang.ref.WeakReference;
import mezz.jei.common.Internal;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Pre;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StartEventObserver implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Runnable startRunnable;
   private final Runnable stopRunnable;
   private WeakReference<Connection> currentConnection = new WeakReference<>(null);
   private StartEventObserver.State state = StartEventObserver.State.LISTENING;
   private boolean observedLogin;
   private boolean observedRecipeSync;

   public StartEventObserver(Runnable startRunnable, Runnable stopRunnable) {
      this.startRunnable = startRunnable;
      this.stopRunnable = stopRunnable;
   }

   public void register(PermanentEventSubscriptions subscriptions) {
      subscriptions.register(EventPriority.LOWEST, LoggingIn.class, this::onLoggingIn);
      subscriptions.register(EventPriority.LOWEST, RecipesUpdatedEvent.class, this::onRecipesUpdatedEvent);
      subscriptions.register(LoggingOut.class, event -> {
         if (event.getPlayer() != null) {
            logReceivedEvent(event);
            Internal.clearClientRecipes();
            this.transitionState(StartEventObserver.State.LISTENING);
         }
      });
      subscriptions.register(
         Pre.class,
         event -> {
            if (this.state != StartEventObserver.State.JEI_STARTED) {
               Screen screen = event.getScreen();
               Minecraft minecraft = screen.getMinecraft();
               if (screen instanceof AbstractContainerScreen && minecraft != null && minecraft.player != null) {
                  LOGGER.error(
                     "A Screen is opening but JEI hasn't started yet.\nNormally, JEI is started after these events have fired: {}.\nSomething has caused one or more of these events to fail, so JEI is starting very late.\nMissing events: {}",
                     this.getRequiredStartEventsString(),
                     this.getMissingStartEventsString()
                  );
                  this.transitionState(StartEventObserver.State.LISTENING);
                  this.transitionState(StartEventObserver.State.JEI_STARTED);
               }
            }
         }
      );
   }

   private void onLoggingIn(LoggingIn event) {
      if (this.observeConnectionEvent(event)) {
         this.observedLogin = true;
         this.startIfReady();
      }
   }

   private void onRecipesUpdatedEvent(RecipesUpdatedEvent event) {
      if (this.observeConnectionEvent(event)) {
         this.observedRecipeSync = true;
         if (this.state == StartEventObserver.State.JEI_STARTED && Internal.hasClientSyncedRecipes()) {
            this.restart();
         } else {
            this.startIfReady();
         }
      }
   }

   private void startIfReady() {
      if (this.state == StartEventObserver.State.LISTENING && this.observedLogin) {
         if (this.observedRecipeSync) {
            this.transitionState(StartEventObserver.State.JEI_STARTED);
         }
      }
   }

   private <T extends Event> boolean observeConnectionEvent(T event) {
      Connection observingConnection = this.currentConnection.get();
      Connection currentConnection = getCurrentConnection();
      if (currentConnection != observingConnection) {
         this.clearObservedStartEvents();
         this.currentConnection = new WeakReference<>(currentConnection);
      }

      if (currentConnection == null) {
         LOGGER.debug("JEI StartEventObserver received {} too early, ignoring", event.getClass());
         return false;
      } else {
         logReceivedEvent(event);
         return true;
      }
   }

   private String getRequiredStartEventsString() {
      return "[%s, %s]".formatted(LoggingIn.class.getName(), RecipesUpdatedEvent.class.getName());
   }

   private String getMissingStartEventsString() {
      StringBuilder missingEvents = new StringBuilder("[");
      if (!this.observedLogin) {
         missingEvents.append(LoggingIn.class.getName());
      }

      if (!this.observedRecipeSync) {
         if (missingEvents.length() > 1) {
            missingEvents.append(", ");
         }

         missingEvents.append(RecipesUpdatedEvent.class.getName());
      }

      return missingEvents.append("]").toString();
   }

   private static <T extends Event> void logReceivedEvent(T event) {
      LOGGER.debug("JEI StartEventObserver received event: {}", event.getClass());
   }

   @Nullable
   private static Connection getCurrentConnection() {
      Minecraft minecraft = Minecraft.getInstance();
      ClientPacketListener packetListener = minecraft.getConnection();
      if (packetListener != null) {
         return packetListener.getConnection();
      } else if (minecraft.pendingConnection != null) {
         return minecraft.pendingConnection;
      } else {
         return minecraft.screen instanceof ConnectScreen connectScreen ? connectScreen.connection : null;
      }
   }

   public void onResourceManagerReload(ResourceManager pResourceManager) {
      LOGGER.debug("JEI StartEventObserver detected resource manager reload.");
      this.restart();
   }

   private void restart() {
      if (this.state == StartEventObserver.State.JEI_STARTED) {
         this.transitionState(StartEventObserver.State.LISTENING);
         this.transitionState(StartEventObserver.State.JEI_STARTED);
      }
   }

   private void transitionState(StartEventObserver.State newState) {
      LOGGER.debug("JEI StartEventObserver transitioning state from {} to {}", this.state, newState);
      switch (newState) {
         case LISTENING:
            if (this.state == StartEventObserver.State.JEI_STARTED) {
               this.stopRunnable.run();
            }
            break;
         case JEI_STARTED:
            if (this.state != StartEventObserver.State.LISTENING) {
               throw new IllegalStateException("Attempted Illegal state transition from " + this.state + " to " + newState);
            }

            this.startRunnable.run();
      }

      this.state = newState;
      this.clearObservedStartEvents();
   }

   private void clearObservedStartEvents() {
      this.observedLogin = false;
      this.observedRecipeSync = false;
   }

   private static enum State {
      LISTENING,
      JEI_STARTED;
   }
}
