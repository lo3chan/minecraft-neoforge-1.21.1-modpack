/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.monster.Creeper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.monster.Creeper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ChargedCreeperProperty
extends BooleanProperty {
    protected ChargedCreeperProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(ChargedCreeperProperty.getGenericBooleanThatCanNull(properties, propertyNum, "creeperCharged", "creeper_charged"));
    }

    public static ChargedCreeperProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ChargedCreeperProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
        ETFEntity eTFEntity;
        if (etfEntity != null && (eTFEntity = etfEntity.entity()) instanceof Creeper) {
            Creeper creeper = (Creeper)eTFEntity;
            return creeper.isPowered();
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"creeperCharged", "creeper_charged"};
    }
}

