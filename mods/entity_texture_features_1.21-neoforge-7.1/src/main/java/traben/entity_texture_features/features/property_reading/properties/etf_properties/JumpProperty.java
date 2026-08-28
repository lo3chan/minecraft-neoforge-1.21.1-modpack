/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.animal.horse.AbstractHorse
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class JumpProperty
extends FloatRangeFromStringArrayProperty {
    protected JumpProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(JumpProperty.readPropertiesOrThrow(properties, propertyNum, "jump", "jumpStrength", "jumpHeight"));
    }

    public static JumpProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new JumpProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
        ETFEntity eTFEntity;
        if (entity != null && (eTFEntity = entity.entity()) instanceof AbstractHorse) {
            AbstractHorse horse = (AbstractHorse)eTFEntity;
            return Float.valueOf(horse.playerJumpPendingScale);
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"jump", "jumpStrength", "jumpHeight"};
    }
}

