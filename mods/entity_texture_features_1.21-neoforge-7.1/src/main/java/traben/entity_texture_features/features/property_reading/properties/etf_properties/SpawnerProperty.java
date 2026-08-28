/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class SpawnerProperty
extends BooleanProperty {
    protected SpawnerProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(SpawnerProperty.getGenericBooleanThatCanNull(properties, propertyNum, "isSpawner", "spawner"));
    }

    public static SpawnerProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SpawnerProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
        if (etfEntity != null) {
            return etfEntity.uuid().getLeastSignificantBits() == 0x303900003039L;
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"isSpawner", "spawner"};
    }
}

