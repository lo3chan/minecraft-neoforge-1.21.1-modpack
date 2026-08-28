/*
 * Decompiled with CFR 0.152.
 */
package net.caffeinemc.mods.sodium.desktop.utils.browse;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import net.caffeinemc.mods.sodium.desktop.utils.browse.BrowseUrlHandler;

class CrossPlatformImpl
implements BrowseUrlHandler {
    CrossPlatformImpl() {
    }

    public static boolean isSupported() {
        return Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }

    @Override
    public void browseTo(String url) throws IOException {
        Desktop.getDesktop().browse(URI.create(url));
    }
}

