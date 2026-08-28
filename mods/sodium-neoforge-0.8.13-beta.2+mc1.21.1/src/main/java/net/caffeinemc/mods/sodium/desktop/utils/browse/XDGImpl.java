/*
 * Decompiled with CFR 0.152.
 */
package net.caffeinemc.mods.sodium.desktop.utils.browse;

import java.io.IOException;
import java.util.Locale;
import net.caffeinemc.mods.sodium.desktop.utils.browse.BrowseUrlHandler;

class XDGImpl
implements BrowseUrlHandler {
    XDGImpl() {
    }

    public static boolean isSupported() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        return os.equals("linux");
    }

    @Override
    public void browseTo(String url) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"xdg-open", url});
        try {
            int result = process.waitFor();
            if (result != 0) {
                throw new IOException("xdg-open exited with code: %d".formatted(result));
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

