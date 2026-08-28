/*
 * Decompiled with CFR 0.152.
 */
package net.caffeinemc.mods.sodium.desktop.utils.browse;

import java.io.IOException;
import net.caffeinemc.mods.sodium.desktop.utils.browse.CrossPlatformImpl;
import net.caffeinemc.mods.sodium.desktop.utils.browse.XDGImpl;

public interface BrowseUrlHandler {
    public void browseTo(String var1) throws IOException;

    public static BrowseUrlHandler createImplementation() {
        if (XDGImpl.isSupported()) {
            return new XDGImpl();
        }
        if (CrossPlatformImpl.isSupported()) {
            return new CrossPlatformImpl();
        }
        return null;
    }
}

