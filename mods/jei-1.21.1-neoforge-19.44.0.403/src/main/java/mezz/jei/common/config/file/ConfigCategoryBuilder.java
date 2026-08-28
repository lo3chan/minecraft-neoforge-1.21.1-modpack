/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.ConfigCategory;
import mezz.jei.common.config.file.ConfigSchema;
import mezz.jei.common.config.file.ConfigValue;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.serializers.BooleanSerializer;
import mezz.jei.common.config.file.serializers.EnumSerializer;
import mezz.jei.common.config.file.serializers.IntegerSerializer;

public class ConfigCategoryBuilder
implements IConfigCategoryBuilder {
    private final String name;
    private final String localizationPath;
    private final List<ConfigValue<?>> values = new ArrayList();

    public ConfigCategoryBuilder(String localizationPath, String name) {
        this.name = name;
        this.localizationPath = localizationPath + "." + name;
    }

    public String getName() {
        return this.name;
    }

    public <T> ConfigValue<T> addValue(ConfigValue<T> value) {
        this.values.add(value);
        return value;
    }

    @Override
    public ConfigValue<Boolean> addBoolean(String name, boolean defaultValue) {
        return this.addValue(new ConfigValue<Boolean>(this.localizationPath, name, defaultValue, BooleanSerializer.INSTANCE));
    }

    @Override
    public <T extends Enum<T>> ConfigValue<T> addEnum(String name, T defaultValue) {
        EnumSerializer<T> serializer = new EnumSerializer<T>(defaultValue.getDeclaringClass());
        return this.addValue(new ConfigValue<T>(this.localizationPath, name, defaultValue, serializer));
    }

    @Override
    public ConfigValue<Integer> addInteger(String name, int defaultValue, int minValue, int maxValue) {
        IntegerSerializer serializer = new IntegerSerializer(minValue, maxValue);
        return this.addValue(new ConfigValue<Integer>(this.localizationPath, name, defaultValue, serializer));
    }

    @Override
    public <T> ConfigValue<List<T>> addList(String name, List<T> defaultValue, IJeiConfigValueSerializer<List<T>> listSerializer) {
        return this.addValue(new ConfigValue<List<T>>(this.localizationPath, name, defaultValue, listSerializer));
    }

    public ConfigCategory build(ConfigSchema schema) {
        for (ConfigValue<?> value : this.values) {
            value.setSchema(schema);
        }
        return new ConfigCategory(this.name, this.values);
    }
}

