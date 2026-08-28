/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package traben.tconfig;

import net.minecraft.resources.ResourceLocation;
import traben.tconfig.gui.entries.TConfigEntryCategory;

public abstract class TConfig {
    public abstract TConfigEntryCategory getGUIOptions();

    public abstract ResourceLocation getModIcon();

    public boolean doesGUI() {
        return true;
    }

    public static class NoGUI
    extends TConfig {
        @Override
        public TConfigEntryCategory getGUIOptions() {
            return null;
        }

        @Override
        public ResourceLocation getModIcon() {
            return null;
        }

        @Override
        public boolean doesGUI() {
            return false;
        }
    }
}

