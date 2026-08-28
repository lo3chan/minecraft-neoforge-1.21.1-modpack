/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.common.platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;

public interface IPlatformConfigHelper {
    public Path getModConfigDir();

    public Optional<Screen> getConfigScreen();

    default public Path createJeiConfigDir() {
        Path configDir = this.getModConfigDir().resolve("jei");
        try {
            Files.createDirectories(configDir, new FileAttribute[0]);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to create JEI config directory: " + String.valueOf(configDir), e);
        }
        return configDir;
    }
}

