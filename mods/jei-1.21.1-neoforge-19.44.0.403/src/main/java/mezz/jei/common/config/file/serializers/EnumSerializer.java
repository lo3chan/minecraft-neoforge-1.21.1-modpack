/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file.serializers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;

public class EnumSerializer<T extends Enum<T>>
implements IJeiConfigValueSerializer<T> {
    private final Class<T> enumClass;
    private final Collection<T> validValues;

    public EnumSerializer(Class<T> enumClass) {
        this.enumClass = enumClass;
        this.validValues = List.of((Enum[])enumClass.getEnumConstants());
    }

    @Override
    public String serialize(T value) {
        return ((Enum)value).name();
    }

    @Override
    public DeserializeResult<T> deserialize(String string) {
        if ((string = string.trim()).startsWith("\"") && string.endsWith("\"")) {
            string = string.substring(1, string.length() - 1);
        }
        try {
            T value = Enum.valueOf(this.enumClass, string);
            return new DeserializeResult<T>(value);
        }
        catch (IllegalArgumentException e) {
            return new DeserializeResult<Object>(null, "Invalid enum name: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public String getValidValuesDescription() {
        String names = this.validValues.stream().map(Enum::name).collect(Collectors.joining(", "));
        return "[%s]".formatted(names);
    }

    @Override
    public boolean isValid(T value) {
        return this.validValues.contains(value);
    }

    @Override
    public Optional<Collection<T>> getAllValidValues() {
        return Optional.of(this.validValues);
    }
}

