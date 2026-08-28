/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class NBTClientProperty
extends NBTProperty {
    protected NBTClientProperty(Properties properties, int propertyNum, String nbtPrefix) throws RandomProperty.RandomPropertyException {
        super(properties, propertyNum, nbtPrefix);
    }

    public static NBTProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new NBTClientProperty(properties, propertyNum, "nbtClient");
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected CompoundTag getEntityNBT(ETFEntityRenderState entity) {
        if (Minecraft.getInstance().player != null) {
            return ((ETFEntity)Minecraft.getInstance().player).etf$getNbt();
        }
        return INTENTIONAL_FAILURE;
    }
}

