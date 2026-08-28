/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class HardcoreProperty
extends BooleanProperty {
    protected HardcoreProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(HardcoreProperty.getGenericBooleanThatCanNull(properties, propertyNum, "hardcore"));
    }

    public static HardcoreProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new HardcoreProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null) {
            return entity.world().getLevelData().isHardcore();
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"hardcore"};
    }
}

