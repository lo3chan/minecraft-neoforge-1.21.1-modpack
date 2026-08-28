/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class MoonPhaseProperty
extends SimpleIntegerArrayProperty {
    protected MoonPhaseProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(MoonPhaseProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "moonPhase"));
    }

    public static MoonPhaseProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MoonPhaseProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"moonPhase"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (entity.world() == null) {
            return Integer.MIN_VALUE;
        }
        return entity.world().getMoonPhase();
    }
}

