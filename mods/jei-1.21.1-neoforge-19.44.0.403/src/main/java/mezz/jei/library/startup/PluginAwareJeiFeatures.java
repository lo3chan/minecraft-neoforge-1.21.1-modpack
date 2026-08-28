/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.startup;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.runtime.IJeiFeatures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginAwareJeiFeatures
implements IJeiFeatures {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IJeiFeatures jeiFeatures;
    private final IModPlugin modPlugin;

    public PluginAwareJeiFeatures(IJeiFeatures jeiFeatures, IModPlugin modPlugin) {
        this.jeiFeatures = jeiFeatures;
        this.modPlugin = modPlugin;
    }

    @Override
    public void disableJeiGui() {
        LOGGER.info("JEI GUI is being disabled by {}", (Object)this.modPlugin.getPluginUid());
        this.jeiFeatures.disableJeiGui();
    }

    @Override
    public boolean isJeiGuiEnabled() {
        return this.jeiFeatures.isJeiGuiEnabled();
    }

    @Override
    public void disableInventoryEffectRendererGuiHandler() {
        LOGGER.info("JEI inventory effect renderer GUI handler is being disabled by {}", (Object)this.modPlugin.getPluginUid());
        this.jeiFeatures.disableInventoryEffectRendererGuiHandler();
    }
}

