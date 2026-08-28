/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file.serializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.api.runtime.config.IJeiConfigListValueSerializer;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;

public class ListSerializer<T>
implements IJeiConfigListValueSerializer<T> {
    private final IJeiConfigValueSerializer<T> valueSerializer;

    public ListSerializer(IJeiConfigValueSerializer<T> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    @Override
    public String serialize(List<T> values) {
        return values.stream().map(this.valueSerializer::serialize).collect(Collectors.joining(", "));
    }

    @Override
    public DeserializeResult<List<T>> deserialize(String string) {
        if ((string = string.trim()).startsWith("[")) {
            if (!string.endsWith("]")) {
                String errorMessage = "No closing brace found.\nList must have no braces, or be wrapped in [ and ].";
                return new DeserializeResult<Object>(null, errorMessage);
            }
            string = string.substring(1, string.length() - 1);
        }
        String[] split = string.split(",");
        ArrayList<String> errors = new ArrayList<String>();
        List results = Arrays.stream(split).map(String::trim).filter(s -> !s.isEmpty()).map(this.valueSerializer::deserialize).mapMulti((r, c) -> {
            r.getResult().ifPresent(c);
            errors.addAll(r.getErrors());
        }).toList();
        return new DeserializeResult<List<T>>(results, errors);
    }

    @Override
    public String getValidValuesDescription() {
        return "A comma-separated list containing values of:\n%s".formatted(this.valueSerializer.getValidValuesDescription());
    }

    @Override
    public boolean isValid(List<T> value) {
        return value.stream().allMatch(this.valueSerializer::isValid);
    }

    @Override
    public IJeiConfigValueSerializer<T> getListValueSerializer() {
        return this.valueSerializer;
    }

    @Override
    public Optional<Collection<List<T>>> getAllValidValues() {
        return Optional.empty();
    }
}

