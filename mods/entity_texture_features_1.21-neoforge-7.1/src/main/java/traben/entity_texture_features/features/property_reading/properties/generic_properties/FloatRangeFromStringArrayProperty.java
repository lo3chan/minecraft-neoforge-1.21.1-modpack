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
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class FloatRangeFromStringArrayProperty
extends NumberRangeFromStringArrayProperty<Float> {
    protected FloatRangeFromStringArrayProperty(String string) throws RandomProperty.RandomPropertyException {
        super(string);
    }

    @Override
    @Nullable
    protected NumberRangeFromStringArrayProperty.RangeTester<Float> getRangeTesterFromString(String possibleRange) {
        try {
            float right;
            String[] str = possibleRange.split("(?<!^|-)-");
            float left = Float.parseFloat(str[0].replaceAll("[^0-9.-]", ""));
            float f = right = str.length > 1 ? Float.parseFloat(str[1].replaceAll("[^0-9.-]", "")) : left;
            if (left == right) {
                return value -> value.floatValue() == left;
            }
            if (right > left) {
                return value -> value.floatValue() >= left && value.floatValue() <= right;
            }
            return value -> value.floatValue() >= right && value.floatValue() <= left;
        }
        catch (Exception ignored) {
            ETFUtils2.logError("number or range in [" + this.getPropertyId() + "] property could not be extracted from input: " + possibleRange);
            return null;
        }
    }
}

