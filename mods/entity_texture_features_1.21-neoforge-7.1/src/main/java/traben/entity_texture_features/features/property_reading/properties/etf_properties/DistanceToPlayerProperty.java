/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class DistanceToPlayerProperty
extends FloatRangeFromStringArrayProperty {
    protected DistanceToPlayerProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(DistanceToPlayerProperty.readPropertiesOrThrow(properties, propertyNum, "distance", "distanceFromPlayer"));
    }

    public static DistanceToPlayerProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new DistanceToPlayerProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
        if (Minecraft.getInstance().player == null) {
            return null;
        }
        return Float.valueOf(entity.distanceTo((Entity)Minecraft.getInstance().player));
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"distance", "distanceFromPlayer"};
    }
}

