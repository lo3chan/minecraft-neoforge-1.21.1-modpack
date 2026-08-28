/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.ConnectScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.network.Connection
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.neoforged.bus.api.Event
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent$LoggingIn
 *  net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.neoforged.neoforge.client.event.RecipesUpdatedEvent
 *  net.neoforged.neoforge.client.event.ScreenEvent$Init$Pre
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
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
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StartEventObserver
implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Runnable startRunnable;
    private final Runnable stopRunnable;
    private WeakReference<Connection> currentConnection = new WeakReference<Object>(null);
    private State state = State.LISTENING;
    private boolean observedLogin;
    private boolean observedRecipeSync;

    public StartEventObserver(Runnable startRunnable, Runnable stopRunnable) {
        this.startRunnable = startRunnable;
        this.stopRunnable = stopRunnable;
    }

    public void register(PermanentEventSubscriptions subscriptions) {
        subscriptions.register(EventPriority.LOWEST, ClientPlayerNetworkEvent.LoggingIn.class, this::onLoggingIn);
        subscriptions.register(EventPriority.LOWEST, RecipesUpdatedEvent.class, this::onRecipesUpdatedEvent);
        subscriptions.register(ClientPlayerNetworkEvent.LoggingOut.class, event -> {
            if (event.getPlayer() != null) {
                StartEventObserver.logReceivedEvent(event);
                Internal.clearClientRecipes();
                this.transitionState(State.LISTENING);
            }
        });
        subscriptions.register(ScreenEvent.Init.Pre.class, event -> {
            if (this.state != State.JEI_STARTED) {
                Screen screen = event.getScreen();
                Minecraft minecraft = screen.getMinecraft();
                if (screen instanceof AbstractContainerScreen && minecraft != null && minecraft.player != null) {
                    LOGGER.error("A Screen is opening but JEI hasn't started yet.\nNormally, JEI is started after these events have fired: {}.\nSomething has caused one or more of these events to fail, so JEI is starting very late.\nMissing events: {}", (Object)this.getRequiredStartEventsString(), (Object)this.getMissingStartEventsString());
                    this.transitionState(State.LISTENING);
                    this.transitionState(State.JEI_STARTED);
                }
            }
        });
    }

    private void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!this.observeConnectionEvent(event)) {
            return;
        }
        this.observedLogin = true;
        this.startIfReady();
    }

    private void onRecipesUpdatedEvent(RecipesUpdatedEvent event) {
        if (!this.observeConnectionEvent(event)) {
            return;
        }
        this.observedRecipeSync = true;
        if (this.state == State.JEI_STARTED && Internal.hasClientSyncedRecipes()) {
            this.restart();
        } else {
            this.startIfReady();
        }
    }

    private void startIfReady() {
        if (this.state != State.LISTENING || !this.observedLogin) {
            return;
        }
        if (!this.observedRecipeSync) {
            return;
        }
        this.transitionState(State.JEI_STARTED);
    }

    private <T extends Event> boolean observeConnectionEvent(T event) {
        Connection observingConnection = (Connection)this.currentConnection.get();
        Connection currentConnection = StartEventObserver.getCurrentConnection();
        if (currentConnection != observingConnection) {
            this.clearObservedStartEvents();
            this.currentConnection = new WeakReference<Connection>(currentConnection);
        }
        if (currentConnection == null) {
            LOGGER.debug("JEI StartEventObserver received {} too early, ignoring", event.getClass());
            return false;
        }
        StartEventObserver.logReceivedEvent(event);
        return true;
    }

    private String getRequiredStartEventsString() {
        return "[%s, %s]".formatted(ClientPlayerNetworkEvent.LoggingIn.class.getName(), RecipesUpdatedEvent.class.getName());
    }

    private String getMissingStartEventsString() {
        StringBuilder missingEvents = new StringBuilder("[");
        if (!this.observedLogin) {
            missingEvents.append(ClientPlayerNetworkEvent.LoggingIn.class.getName());
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
        }
        if (minecraft.pendingConnection != null) {
            return minecraft.pendingConnection;
        }
        Screen screen = minecraft.screen;
        if (screen instanceof ConnectScreen) {
            ConnectScreen connectScreen = (ConnectScreen)screen;
            return connectScreen.connection;
        }
        return null;
    }

    public void onResourceManagerReload(ResourceManager pResourceManager) {
        LOGGER.debug("JEI StartEventObserver detected resource manager reload.");
        this.restart();
    }

    private void restart() {
        if (this.state != State.JEI_STARTED) {
            return;
        }
        this.transitionState(State.LISTENING);
        this.transitionState(State.JEI_STARTED);
    }

    private void transitionState(State newState) {
        LOGGER.debug("JEI StartEventObserver transitioning state from {} to {}", (Object)this.state, (Object)newState);
        switch (newState.ordinal()) {
            case 0: {
                if (this.state != State.JEI_STARTED) break;
                this.stopRunnable.run();
                break;
            }
            case 1: {
                if (this.state != State.LISTENING) {
                    throw new IllegalStateException("Attempted Illegal state transition from " + String.valueOf((Object)this.state) + " to " + String.valueOf((Object)newState));
                }
                this.startRunnable.run();
            }
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

