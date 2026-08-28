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
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class RegionalDifficultyProperty
extends FloatRangeFromStringArrayProperty {
    protected RegionalDifficultyProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RegionalDifficultyProperty.readPropertiesOrThrow(properties, propertyNum, "regionalDifficulty", "regional_difficulty"));
    }

    public static RegionalDifficultyProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new RegionalDifficultyProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"regionalDifficulty", "regional_difficulty"};
    }

    @Override
    protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null && entity.world() != null) {
            return Float.valueOf(entity.world().getCurrentDifficultyAt(entity.blockPos()).getEffectiveDifficulty());
        }
        return Float.valueOf(0.0f);
    }
}

