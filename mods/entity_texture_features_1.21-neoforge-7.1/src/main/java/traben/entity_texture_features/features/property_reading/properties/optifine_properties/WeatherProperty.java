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
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class WeatherProperty
extends StringArrayOrRegexProperty {
    protected WeatherProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(WeatherProperty.readPropertiesOrThrow(properties, propertyNum, "weather"));
        if (this.ARRAY.contains("rain")) {
            this.ARRAY.add("thunder");
        }
    }

    public static WeatherProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new WeatherProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    protected String getValueFromEntity(ETFEntityRenderState entity) {
        if (entity.world() != null) {
            if (entity.world().isThundering()) {
                return "thunder";
            }
            if (entity.world().isRaining()) {
                return "rain";
            }
            return "clear";
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"weather"};
    }
}

