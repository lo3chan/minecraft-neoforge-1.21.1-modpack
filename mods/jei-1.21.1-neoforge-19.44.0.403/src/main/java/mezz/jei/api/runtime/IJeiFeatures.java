/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.runtime;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IJeiFeatures {
    public void disableJeiGui();

    public boolean isJeiGuiEnabled();

    public void disableInventoryEffectRendererGuiHandler();
}

