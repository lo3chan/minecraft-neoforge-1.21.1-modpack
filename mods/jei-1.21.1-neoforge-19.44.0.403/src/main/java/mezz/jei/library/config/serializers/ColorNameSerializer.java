/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package mezz.jei.library.config.serializers;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;
import mezz.jei.library.color.ColorName;
import org.apache.commons.lang3.StringUtils;

public class ColorNameSerializer
implements IJeiConfigValueSerializer<ColorName> {
    public static final ColorNameSerializer INSTANCE = new ColorNameSerializer();

    private ColorNameSerializer() {
    }

    @Override
    public String serialize(ColorName value) {
        String name = value.name();
        String color = Integer.toHexString(value.color()).toUpperCase(Locale.ROOT);
        return "%s:%s".formatted(name, StringUtils.leftPad((String)color, (int)6, (char)'0'));
    }

    @Override
    public DeserializeResult<ColorName> deserialize(String string) {
        String[] values;
        if ((string = string.trim()).startsWith("\"") && string.endsWith("\"")) {
            string = string.substring(1, string.length() - 1);
        }
        if ((values = string.split(":")).length == 2) {
            String name = values[0];
            try {
                Integer color = Integer.decode("0x" + values[1]);
                ColorName colorName = new ColorName(name, color);
                return new DeserializeResult<ColorName>(colorName);
            }
            catch (NumberFormatException e) {
                String errorMsg = "Color entry must have an RGB hex color.\nGot an exception when parsing:\n%s".formatted(e.getMessage());
                return new DeserializeResult<Object>(null, errorMsg);
            }
        }
        String errorMsg = "Color entry must be a name and an RGB hex color, separated by a ':'.";
        return new DeserializeResult<Object>(null, errorMsg);
    }

    @Override
    public String getValidValuesDescription() {
        return "Any color name and an RGB hex color, separated by a ':'";
    }

    @Override
    public boolean isValid(ColorName value) {
        return true;
    }

    @Override
    public Optional<Collection<ColorName>> getAllValidValues() {
        return Optional.empty();
    }
}

