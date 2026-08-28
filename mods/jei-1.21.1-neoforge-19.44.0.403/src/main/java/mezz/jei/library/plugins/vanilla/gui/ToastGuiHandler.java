/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.Rect2i
 */
package mezz.jei.library.plugins.vanilla.gui;

import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.renderer.Rect2i;

public class ToastGuiHandler
implements IGlobalGuiHandler {
    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        if (!clientConfig.toastReflowEnabled().getValue().booleanValue()) {
            return List.of();
        }
        IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
        ImmutableRect2i toastsArea = screenHelper.getToastsArea();
        if (toastsArea.isEmpty()) {
            return List.of();
        }
        return List.of(toastsArea.toMutable());
    }
}

