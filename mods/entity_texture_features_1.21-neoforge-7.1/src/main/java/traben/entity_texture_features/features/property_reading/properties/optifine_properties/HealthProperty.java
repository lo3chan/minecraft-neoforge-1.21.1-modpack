/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class HealthProperty
extends FloatRangeFromStringArrayProperty {
    private final boolean isPercentage;

    protected HealthProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(HealthProperty.readPropertiesOrThrow(properties, propertyNum, "health"));
        this.isPercentage = this.originalInput.contains("%");
    }

    public static HealthProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new HealthProperty(properties, propertyNum);
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
            float health = alive.getHealth();
            return Float.valueOf(this.isPercentage ? (float)Mth.ceil((float)(health / alive.getMaxHealth() * 100.0f)) : health);
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"health"};
    }
}

