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
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TeamProperty
extends StringArrayOrRegexProperty {
    protected TeamProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "teams", "team"));
    }

    public static TeamProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TeamProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState etfEntity) {
        if (etfEntity.scoreboardTeam() != null) {
            return etfEntity.scoreboardTeam().getName();
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"teams", "team"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }
}

