/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file;

import java.util.List;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.ConfigValue;

public interface IConfigCategoryBuilder {
    public ConfigValue<Boolean> addBoolean(String var1, boolean var2);

    public ConfigValue<Integer> addInteger(String var1, int var2, int var3, int var4);

    public <T extends Enum<T>> ConfigValue<T> addEnum(String var1, T var2);

    public <T> ConfigValue<List<T>> addList(String var1, List<T> var2, IJeiConfigValueSerializer<List<T>> var3);
}

