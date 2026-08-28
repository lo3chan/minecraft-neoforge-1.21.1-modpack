/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.LongRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TimeOfDayProperty
extends LongRangeFromStringArrayProperty {
    protected TimeOfDayProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(TimeOfDayProperty.readPropertiesOrThrow(properties, propertyNum, "dayTime"));
    }

    public static TimeOfDayProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TimeOfDayProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Long getRangeValueFromEntity(ETFEntityRenderState entity) {
        if (entity.world() != null) {
            return entity.world().getDayTime() % 24000L;
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"dayTime"};
    }
}

