/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Calendar;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class HourProperty
extends SimpleIntegerArrayProperty {
    protected HourProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(HourProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "hour"));
    }

    public static HourProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new HourProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"hour"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        return Calendar.getInstance().get(11);
    }
}

