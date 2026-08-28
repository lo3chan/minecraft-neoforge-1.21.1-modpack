/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.animal.IronGolem
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.IronGolem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class PlayerCreatedProperty
extends BooleanProperty {
    protected PlayerCreatedProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(PlayerCreatedProperty.getGenericBooleanThatCanNull(properties, propertyNum, "playerCreated", "player_created"));
    }

    public static PlayerCreatedProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new PlayerCreatedProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
        ETFEntity eTFEntity;
        if (etfEntity != null && (eTFEntity = etfEntity.entity()) instanceof IronGolem) {
            IronGolem golem = (IronGolem)eTFEntity;
            return golem.isPlayerCreated();
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"playerCreated", "player_created"};
    }
}

