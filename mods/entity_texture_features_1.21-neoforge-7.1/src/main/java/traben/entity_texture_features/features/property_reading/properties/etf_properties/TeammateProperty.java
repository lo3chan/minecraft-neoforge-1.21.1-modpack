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
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class TeammateProperty
extends BooleanProperty {
    protected TeammateProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(TeammateProperty.getGenericBooleanThatCanNull(properties, propertyNum, "isTeammate", "teammate"));
    }

    public static TeammateProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TeammateProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
        ETFEntity eTFEntity;
        if (etfEntity != null && (eTFEntity = etfEntity.entity()) instanceof Entity) {
            Entity entity = (Entity)eTFEntity;
            if (Minecraft.getInstance().player != null) {
                return entity.isAlliedTo((Entity)Minecraft.getInstance().player);
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"isTeammate", "teammate"};
    }
}

