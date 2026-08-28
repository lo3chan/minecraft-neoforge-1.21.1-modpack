/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class LightProperty
extends SimpleIntegerArrayProperty {
    protected LightProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(LightProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "light"));
    }

    public static LightProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new LightProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"light"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (entity == null || entity.world() == null || entity.blockPos() == null) {
            return -1;
        }
        return entity.world().getMaxLocalRawBrightness(entity.blockPos());
    }
}

