/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package net.diebuddies.physics;

import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.StarterServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value="physicsmod")
public class ModExecutor {
    public ModExecutor(IEventBus modEventBus) {
        if (FMLEnvironment.dist.isClient()) {
            StarterClient.onInitializeClient(modEventBus);
        }
        if (FMLEnvironment.dist.isDedicatedServer()) {
            StarterServer.onInitializeServer();
        }
    }
}

