/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class SpeedProperty
extends FloatRangeFromStringArrayProperty {
    protected SpeedProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(SpeedProperty.readPropertiesOrThrow(properties, propertyNum, "speed", "maxSpeed", "speeds"));
    }

    public static SpeedProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SpeedProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
        ETFEntity eTFEntity;
        if (entity != null && (eTFEntity = entity.entity()) instanceof LivingEntity) {
            LivingEntity alive = (LivingEntity)eTFEntity;
            return Float.valueOf(alive.getSpeed());
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"speed", "maxSpeed", "speeds"};
    }
}

