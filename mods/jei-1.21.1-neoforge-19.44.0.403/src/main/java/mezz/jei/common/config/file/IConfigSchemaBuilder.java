/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file;

import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchema;

public interface IConfigSchemaBuilder {
    public IConfigCategoryBuilder addCategory(String var1);

    public IConfigSchema build();
}

