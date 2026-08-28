/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ClientPlayerProperty
extends BooleanProperty {
    protected ClientPlayerProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(ClientPlayerProperty.getGenericBooleanThatCanNull(properties, propertyNum, "isClientPlayer", "clientPlayer"));
    }

    public static ClientPlayerProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ClientPlayerProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
        boolean bl;
        ETFEntity eTFEntity;
        if (etfEntity != null && (eTFEntity = etfEntity.entity()) instanceof Player) {
            Player entity = (Player)eTFEntity;
            if (Minecraft.getInstance().player != null && entity.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
                bl = true;
                return bl;
            }
        }
        bl = false;
        return bl;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"isClientPlayer", "clientPlayer"};
    }
}

