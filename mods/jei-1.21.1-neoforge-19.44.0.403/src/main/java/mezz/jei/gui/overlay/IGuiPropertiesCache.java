/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay;

import java.util.Set;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.overlay.IScreenPropertiesUpdater;
import org.jetbrains.annotations.Nullable;

public interface IGuiPropertiesCache {
    public IScreenPropertiesUpdater createUpdater(Runnable var1);

    @Nullable
    public IGuiProperties getGuiProperties();

    public Set<ImmutableRect2i> getGuiExclusionAreas();
}

