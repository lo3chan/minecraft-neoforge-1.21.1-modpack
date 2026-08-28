/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics;

import net.diebuddies.bridge.FabricAPIServer;
import net.diebuddies.physics.ServerPhysicsMod;

public class StarterServer {
    public static void onInitializeServer() {
        ServerPhysicsMod server = new ServerPhysicsMod();
        FabricAPIServer.START_WORLD_TICK.register(server);
        FabricAPIServer.AFTER.register(server);
    }
}

