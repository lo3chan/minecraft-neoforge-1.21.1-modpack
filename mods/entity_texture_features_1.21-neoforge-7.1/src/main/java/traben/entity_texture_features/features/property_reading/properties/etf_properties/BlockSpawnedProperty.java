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
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BlocksProperty;

public class BlockSpawnedProperty
extends BlocksProperty {
    protected BlockSpawnedProperty(Properties properties, int propertyNum, String[] ids) throws RandomProperty.RandomPropertyException {
        super(properties, propertyNum, ids);
    }

    public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new BlockSpawnedProperty(properties, propertyNum, new String[]{"blockSpawned"});
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"blockSpawned"};
    }
}

