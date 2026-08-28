/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file;

import mezz.jei.api.runtime.config.IJeiConfigFile;
import mezz.jei.common.config.ConfigManager;
import mezz.jei.common.config.file.FileWatcher;

public interface IConfigSchema
extends IJeiConfigFile {
    public void register(FileWatcher var1, ConfigManager var2);

    public void loadIfNeeded();

    public void markDirty();

    public void clearListeners();
}

