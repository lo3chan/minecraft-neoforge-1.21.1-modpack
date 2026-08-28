/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.load;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.function.Consumer;
import mezz.jei.api.IModPlugin;
import mezz.jei.common.util.TimeUtil;
import mezz.jei.library.load.PluginCallerTimer;
import mezz.jei.library.plugins.vanilla.VanillaPlugin;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginCaller {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void callOnPlugins(String title, List<IModPlugin> plugins, Consumer<IModPlugin> func) {
        LOGGER.info("{}...", (Object)title);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (PluginCallerTimer timer = new PluginCallerTimer();){
            for (IModPlugin plugin : plugins) {
                try {
                    ResourceLocation pluginUid = plugin.getPluginUid();
                    timer.begin(title, pluginUid);
                    func.accept(plugin);
                    timer.end();
                }
                catch (LinkageError | RuntimeException e) {
                    if (plugin instanceof VanillaPlugin) {
                        throw e;
                    }
                    LOGGER.error("Caught an error from mod plugin: {} {}", plugin.getClass(), (Object)plugin.getPluginUid(), (Object)e);
                }
            }
        }
        LOGGER.info("{} took {}", (Object)title, (Object)TimeUtil.toHumanString(stopwatch.elapsed()));
    }
}

