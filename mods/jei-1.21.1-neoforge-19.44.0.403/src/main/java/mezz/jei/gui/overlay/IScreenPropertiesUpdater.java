/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay;

import java.util.Set;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public interface IScreenPropertiesUpdater {
    public IScreenPropertiesUpdater updateScreen(@Nullable Screen var1);

    public IScreenPropertiesUpdater updateGuiProperties(@Nullable IGuiProperties var1);

    public IScreenPropertiesUpdater updateExclusionAreas(Set<ImmutableRect2i> var1);

    public IScreenPropertiesUpdater updateMouseExclusionArea(@Nullable ImmutablePoint2i var1);

    public void update();

    public void forceUpdate();
}

