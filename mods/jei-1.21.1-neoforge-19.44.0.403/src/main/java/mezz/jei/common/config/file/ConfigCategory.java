/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.common.config.file;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.runtime.config.IJeiConfigCategory;
import mezz.jei.common.config.file.ConfigValue;
import org.jetbrains.annotations.Unmodifiable;

public class ConfigCategory
implements IJeiConfigCategory {
    private final String name;
    private final @Unmodifiable Map<String, ConfigValue<?>> valueMap;

    public ConfigCategory(String name, List<ConfigValue<?>> values) {
        this.name = name;
        LinkedHashMap map = new LinkedHashMap();
        for (ConfigValue<?> value : values) {
            map.put(value.getName(), value);
        }
        this.valueMap = Collections.unmodifiableMap(map);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Optional<ConfigValue<?>> getConfigValue(String configValueName) {
        ConfigValue<?> configValue = this.valueMap.get(configValueName);
        return Optional.ofNullable(configValue);
    }

    public @Unmodifiable Collection<ConfigValue<?>> getConfigValues() {
        return this.valueMap.values();
    }

    public Set<String> getValueNames() {
        return this.valueMap.keySet();
    }

    public void clearListeners() {
        for (ConfigValue<?> configValue : this.valueMap.values()) {
            configValue.clearListeners();
        }
    }
}

