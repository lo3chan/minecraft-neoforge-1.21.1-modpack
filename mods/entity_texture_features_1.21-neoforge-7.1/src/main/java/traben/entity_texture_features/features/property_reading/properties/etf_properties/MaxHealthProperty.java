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

public class MaxHealthProperty
extends FloatRangeFromStringArrayProperty {
    protected MaxHealthProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(MaxHealthProperty.readPropertiesOrThrow(properties, propertyNum, "maxHealth", "max_health"));
    }

    public static MaxHealthProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MaxHealthProperty(properties, propertyNum);
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
            return Float.valueOf(alive.getMaxHealth());
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"maxHealth", "max_health"};
    }
}

