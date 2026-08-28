/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class NBTVehicleProperty
extends NBTProperty {
    protected NBTVehicleProperty(Properties properties, int propertyNum, String nbtPrefix) throws RandomProperty.RandomPropertyException {
        super(properties, propertyNum, nbtPrefix);
    }

    public static NBTProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new NBTVehicleProperty(properties, propertyNum, "nbtVehicle");
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected CompoundTag getEntityNBT(ETFEntityRenderState entity) {
        ETFEntity eTFEntity;
        if (entity != null && (eTFEntity = entity.entity()) instanceof Entity) {
            Entity e = (Entity)eTFEntity;
            ETFEntity vehicle = (ETFEntity)e.getVehicle();
            return vehicle != null ? vehicle.etf$getNbt() : INTENTIONAL_FAILURE;
        }
        return INTENTIONAL_FAILURE;
    }
}

