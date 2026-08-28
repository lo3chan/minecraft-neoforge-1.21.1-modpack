/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TemperatureProperty
extends FloatRangeFromStringArrayProperty {
    protected TemperatureProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(TemperatureProperty.readPropertiesOrThrow(properties, propertyNum, "temperature"));
    }

    public static TemperatureProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TemperatureProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
        if (entity == null) {
            return null;
        }
        Level level = entity.world();
        if (level == null) {
            return null;
        }
        Holder biome = level.getBiome(entity.blockPos());
        return Float.valueOf(((Biome)biome.value()).getHeightAdjustedTemperature(entity.blockPos()));
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"temperature"};
    }
}

