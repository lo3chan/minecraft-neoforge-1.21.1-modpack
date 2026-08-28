/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import java.nio.file.Path;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.common.config.ClientConfig;
import mezz.jei.common.config.ConfigManager;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.config.IngredientFilterConfig;
import mezz.jei.common.config.IngredientGridConfig;
import mezz.jei.common.config.file.ConfigSchemaBuilder;
import mezz.jei.common.config.file.FileWatcher;
import mezz.jei.common.config.file.IConfigSchema;

public class JeiClientConfigs
implements IJeiClientConfigs {
    private final IClientConfig clientConfig;
    private final IIngredientFilterConfig ingredientFilterConfig;
    private final IIngredientGridConfig ingredientListConfig;
    private final IIngredientGridConfig bookmarkListConfig;
    private final IConfigSchema schema;

    public JeiClientConfigs(Path configFile) {
        ConfigSchemaBuilder builder = new ConfigSchemaBuilder(configFile, "jei.config.client");
        this.clientConfig = new ClientConfig(builder);
        this.ingredientFilterConfig = new IngredientFilterConfig(builder);
        this.ingredientListConfig = new IngredientGridConfig("ingredientList", builder, HorizontalAlignment.RIGHT);
        this.bookmarkListConfig = new IngredientGridConfig("bookmarkList", builder, HorizontalAlignment.LEFT);
        this.schema = builder.build();
    }

    public void register(FileWatcher fileWatcher, ConfigManager configManager) {
        this.schema.register(fileWatcher, configManager);
    }

    @Override
    public IClientConfig getClientConfig() {
        return this.clientConfig;
    }

    @Override
    public IIngredientFilterConfig getIngredientFilterConfig() {
        return this.ingredientFilterConfig;
    }

    @Override
    public IIngredientGridConfig getIngredientListConfig() {
        return this.ingredientListConfig;
    }

    @Override
    public IIngredientGridConfig getBookmarkListConfig() {
        return this.bookmarkListConfig;
    }

    @Override
    public void onRuntimeStopped() {
        this.schema.clearListeners();
    }
}

