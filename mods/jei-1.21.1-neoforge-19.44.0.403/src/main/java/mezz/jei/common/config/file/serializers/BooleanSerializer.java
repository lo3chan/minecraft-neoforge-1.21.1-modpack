/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file.serializers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;

public class BooleanSerializer
implements IJeiConfigValueSerializer<Boolean> {
    public static final BooleanSerializer INSTANCE = new BooleanSerializer();

    private BooleanSerializer() {
    }

    @Override
    public String serialize(Boolean value) {
        return value.toString();
    }

    @Override
    public DeserializeResult<Boolean> deserialize(String string) {
        if ("true".equalsIgnoreCase(string = string.trim())) {
            return new DeserializeResult<Boolean>(true);
        }
        if ("false".equalsIgnoreCase(string)) {
            return new DeserializeResult<Boolean>(false);
        }
        return new DeserializeResult<Object>(null, "string must be 'true' or 'false'");
    }

    @Override
    public String getValidValuesDescription() {
        return "[true, false]";
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    @Override
    public Optional<Collection<Boolean>> getAllValidValues() {
        return Optional.of(List.of(Boolean.valueOf(true), Boolean.valueOf(false)));
    }
}

