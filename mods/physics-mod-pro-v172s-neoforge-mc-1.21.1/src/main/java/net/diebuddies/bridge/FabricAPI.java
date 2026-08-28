/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package net.diebuddies.bridge;

import net.diebuddies.bridge.Event;
import net.diebuddies.bridge.EventFactory;
import net.minecraft.client.Minecraft;

public class FabricAPI {
    public static final Event<ClientStopping> CLIENT_STOPPING = EventFactory.createArrayBacked(ClientStopping.class, callbacks -> client -> {
        for (ClientStopping callback : callbacks) {
            callback.onClientStopping(client);
        }
    });

    @FunctionalInterface
    public static interface ClientStopping {
        public void onClientStopping(Minecraft var1);
    }
}

