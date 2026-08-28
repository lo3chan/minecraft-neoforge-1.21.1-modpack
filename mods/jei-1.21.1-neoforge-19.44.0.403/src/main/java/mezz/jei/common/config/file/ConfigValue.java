/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.config.file;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import mezz.jei.api.runtime.config.IJeiConfigValue;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.IConfigSchema;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ConfigValue<T>
implements IJeiConfigValue<T>,
Supplier<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String name;
    private final Component localizedName;
    private final Component description;
    private final T defaultValue;
    private final IJeiConfigValueSerializer<T> serializer;
    @Nullable
    private List<Consumer<T>> listeners;
    private volatile T currentValue;
    @Nullable
    private IConfigSchema schema;

    public ConfigValue(String localizationPath, String name, T defaultValue, IJeiConfigValueSerializer<T> serializer) {
        this.name = name;
        String nameKey = localizationPath + "." + name;
        String descriptionKey = nameKey + ".description";
        this.localizedName = Component.translatable((String)nameKey);
        this.description = Component.translatable((String)descriptionKey);
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
        this.serializer = serializer;
    }

    public void setSchema(IConfigSchema schema) {
        this.schema = schema;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description.getString();
    }

    @Override
    public Component getLocalizedDescription() {
        return this.description;
    }

    @Override
    public Component getLocalizedName() {
        return this.localizedName;
    }

    @Override
    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public T getValue() {
        if (this.schema != null) {
            this.schema.loadIfNeeded();
        }
        return this.currentValue;
    }

    @Override
    public T get() {
        return this.getValue();
    }

    @Override
    public IJeiConfigValueSerializer<T> getSerializer() {
        return this.serializer;
    }

    public List<String> setFromSerializedValue(String value) {
        IJeiConfigValueSerializer.IDeserializeResult<T> deserializeResult = this.serializer.deserialize(value);
        deserializeResult.getResult().ifPresent(t -> {
            if (this.currentValue != t) {
                this.currentValue = t;
                if (this.listeners != null) {
                    this.listeners.forEach(listener -> listener.accept(this.currentValue));
                }
            }
        });
        return deserializeResult.getErrors();
    }

    @Override
    public boolean set(T value) {
        if (!this.serializer.isValid(value)) {
            LOGGER.error("Tried to set invalid value : {}\n{}", value, (Object)this.serializer.getValidValuesDescription());
            return false;
        }
        if (!this.currentValue.equals(value)) {
            this.currentValue = value;
            if (this.listeners != null) {
                this.listeners.forEach(listener -> listener.accept(this.currentValue));
            }
            if (this.schema != null) {
                this.schema.markDirty();
            }
            return true;
        }
        return false;
    }

    @Override
    public void addListener(Consumer<T> listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<Consumer<T>>();
        }
        this.listeners.add(listener);
    }

    public void clearListeners() {
        if (this.listeners != null) {
            this.listeners = null;
        }
    }
}

