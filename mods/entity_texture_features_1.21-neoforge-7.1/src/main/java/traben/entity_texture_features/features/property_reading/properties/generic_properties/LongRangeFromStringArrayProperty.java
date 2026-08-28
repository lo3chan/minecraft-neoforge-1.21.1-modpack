/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.NumberRangeFromStringArrayProperty;

public abstract class LongRangeFromStringArrayProperty
extends NumberRangeFromStringArrayProperty<Long> {
    protected LongRangeFromStringArrayProperty(String string) throws RandomProperty.RandomPropertyException {
        super(string);
    }

    @Override
    @Nullable
    protected NumberRangeFromStringArrayProperty.RangeTester<Long> getRangeTesterFromString(String possibleRange) {
        try {
            long right;
            String[] str = possibleRange.split("(?<!^|-)-");
            long left = Long.parseLong(str[0].replaceAll("[^0-9-]", ""));
            long l = right = str.length > 1 ? Long.parseLong(str[1].replaceAll("[^0-9-]", "")) : left;
            if (left == right) {
                return value -> value == left;
            }
            if (right > left) {
                return value -> value >= left && value <= right;
            }
            return value -> value >= right && value <= left;
        }
        catch (Exception exception) {
            return null;
        }
    }
}

