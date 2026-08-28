/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.IExtensionPoint
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.client.gui.IConfigScreenFactory
 */
package net.irisshaders.iris.platform;

import java.util.ArrayList;
import java.util.List;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value="iris", dist={Dist.CLIENT})
public class IrisForgeMod {
    public static List<KeyMapping> KEYLIST = new ArrayList<KeyMapping>();

    public IrisForgeMod(IEventBus bus, ModContainer modContainer) {
        bus.addListener(this::registerKeys);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (IExtensionPoint)((IConfigScreenFactory)(game, screen) -> new ShaderPackScreen(screen)));
    }

    public void registerKeys(RegisterKeyMappingsEvent event) {
        KEYLIST.forEach(arg_0 -> ((RegisterKeyMappingsEvent)event).register(arg_0));
        KEYLIST.clear();
    }
}

