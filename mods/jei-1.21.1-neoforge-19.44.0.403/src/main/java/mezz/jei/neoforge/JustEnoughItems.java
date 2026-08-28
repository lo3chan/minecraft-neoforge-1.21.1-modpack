/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.neoforge.common.NeoForge
 */
package mezz.jei.neoforge;

import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.util.MinecraftLocaleSupplier;
import mezz.jei.common.util.Translator;
import mezz.jei.neoforge.JustEnoughItemsClientSafeRunner;
import mezz.jei.neoforge.config.ServerConfig;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import mezz.jei.neoforge.network.NetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value="jei")
public class JustEnoughItems {
    public JustEnoughItems(IEventBus modEventBus, Dist dist) {
        Translator.setLocaleSupplier(new MinecraftLocaleSupplier());
        IEventBus eventBus = NeoForge.EVENT_BUS;
        PermanentEventSubscriptions subscriptions = new PermanentEventSubscriptions(eventBus, modEventBus);
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IServerConfig serverConfig = ServerConfig.register(modLoadingContext);
        NetworkHandler networkHandler = new NetworkHandler("3", serverConfig);
        networkHandler.registerPacketHandlers(subscriptions);
        JustEnoughItemsClientSafeRunner clientSafeRunner = new JustEnoughItemsClientSafeRunner(networkHandler, subscriptions);
        if (dist.isClient()) {
            clientSafeRunner.registerClient();
        }
    }
}

