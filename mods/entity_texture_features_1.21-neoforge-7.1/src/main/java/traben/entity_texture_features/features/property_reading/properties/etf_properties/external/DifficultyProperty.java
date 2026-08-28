/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class DifficultyProperty
extends SimpleIntegerArrayProperty {
    protected DifficultyProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(DifficultyProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "difficulty"));
    }

    public static DifficultyProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new DifficultyProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"difficulty"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null) {
            return entity.world().getDifficulty().getId();
        }
        return 0;
    }
}

