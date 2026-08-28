/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.locale.Language
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.common.config.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import mezz.jei.common.config.ConfigManager;
import mezz.jei.common.config.file.ConfigCategory;
import mezz.jei.common.config.file.ConfigCategoryBuilder;
import mezz.jei.common.config.file.ConfigSerializer;
import mezz.jei.common.config.file.FileWatcher;
import mezz.jei.common.config.file.IConfigSchema;
import mezz.jei.common.util.DeduplicatingRunner;
import net.minecraft.locale.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

public class ConfigSchema
implements IConfigSchema {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Duration SAVE_DELAY_TIME = Duration.ofSeconds(2L);
    private final Path path;
    private final @Unmodifiable List<ConfigCategory> categories;
    private final AtomicBoolean needsLoad = new AtomicBoolean(true);
    private final DeduplicatingRunner delayedSave = new DeduplicatingRunner(SAVE_DELAY_TIME);

    public ConfigSchema(Path path, List<ConfigCategoryBuilder> categoryBuilders) {
        this.path = path;
        this.categories = categoryBuilders.stream().map(b -> b.build(this)).toList();
    }

    @Override
    public void loadIfNeeded() {
        if (!this.needsLoad.compareAndSet(true, false)) {
            return;
        }
        if (Files.exists(this.path, new LinkOption[0])) {
            try {
                ConfigSerializer.load(this.path, this.categories);
            }
            catch (IOException e) {
                LOGGER.error("Failed to load config schema for: {}", (Object)this.path, (Object)e);
            }
        }
    }

    private void onFileChanged() {
        this.needsLoad.set(true);
    }

    @Override
    public void register(FileWatcher fileWatcher, ConfigManager configManager) {
        if (Files.exists(this.path, new LinkOption[0])) {
            this.loadIfNeeded();
        }
        this.save();
        fileWatcher.addCallback(this.path, this::onFileChanged);
        configManager.registerConfigFile(this);
    }

    private void save() {
        if (!Language.getInstance().has("jei.config")) {
            LOGGER.debug("Localization has not loaded yet, waiting to save the config file until JEI starts.");
            return;
        }
        try {
            ConfigSerializer.save(this.path, this.categories);
        }
        catch (IOException e) {
            LOGGER.error("Failed to save config file: '{}'", (Object)this.path, (Object)e);
        }
    }

    @Override
    public void markDirty() {
        this.delayedSave.run(this::save);
    }

    @Override
    public void clearListeners() {
        for (ConfigCategory configCategory : this.categories) {
            configCategory.clearListeners();
        }
    }

    public @Unmodifiable List<ConfigCategory> getCategories() {
        return this.categories;
    }

    @Override
    public Path getPath() {
        return this.path;
    }
}

