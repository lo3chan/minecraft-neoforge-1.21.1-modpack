/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mezz.jei.common.config.file.ConfigCategoryBuilder;
import mezz.jei.common.config.file.ConfigSchema;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchema;
import mezz.jei.common.config.file.IConfigSchemaBuilder;

public class ConfigSchemaBuilder
implements IConfigSchemaBuilder {
    private final Set<String> categoryNames = new HashSet<String>();
    private final List<ConfigCategoryBuilder> categoryBuilders = new ArrayList<ConfigCategoryBuilder>();
    private final Path configFile;
    private final String localizationPath;

    public ConfigSchemaBuilder(Path configFile, String localizationPath) {
        this.configFile = configFile;
        this.localizationPath = localizationPath;
    }

    @Override
    public IConfigCategoryBuilder addCategory(String name) {
        if (!this.categoryNames.add(name)) {
            throw new IllegalArgumentException("There is already a category named: " + name);
        }
        ConfigCategoryBuilder category = new ConfigCategoryBuilder(this.localizationPath, name);
        this.categoryBuilders.add(category);
        return category;
    }

    @Override
    public IConfigSchema build() {
        return new ConfigSchema(this.configFile, this.categoryBuilders);
    }
}

