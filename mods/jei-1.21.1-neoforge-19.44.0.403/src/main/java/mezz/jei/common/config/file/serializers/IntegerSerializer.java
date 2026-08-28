/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.file.serializers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;

public class IntegerSerializer
implements IJeiConfigValueSerializer<Integer> {
    private final int min;
    private final int max;

    public IntegerSerializer(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String serialize(Integer value) {
        return value.toString();
    }

    @Override
    public DeserializeResult<Integer> deserialize(String string) {
        string = string.trim();
        try {
            int value = Integer.parseInt(string);
            if (!this.isValid(value)) {
                String errorMessage = "Invalid integer. Must be: " + this.getValidValuesDescription();
                return new DeserializeResult<Object>(null, errorMessage);
            }
            return new DeserializeResult<Integer>(value);
        }
        catch (NumberFormatException e) {
            String errorMessage = "Unable to parse int: '%s' with error:\n%s".formatted(string, e.getMessage());
            return new DeserializeResult<Object>(null, errorMessage);
        }
    }

    @Override
    public String getValidValuesDescription() {
        if (this.min == Integer.MIN_VALUE && this.max == Integer.MAX_VALUE) {
            return "Any integer";
        }
        if (this.max == Integer.MAX_VALUE) {
            return "Any integer greater than or equal to %s".formatted(this.min);
        }
        return "An integer in the range [%s, %s] (inclusive)".formatted(this.min, this.max);
    }

    @Override
    public boolean isValid(Integer value) {
        return value >= this.min && value <= this.max;
    }

    @Override
    public Optional<Collection<Integer>> getAllValidValues() {
        int count = this.max - this.min + 1;
        if (count > 0 && count < 20) {
            List<Integer> values = IntStream.rangeClosed(this.min, this.max).boxed().toList();
            return Optional.of(values);
        }
        return Optional.empty();
    }
}

