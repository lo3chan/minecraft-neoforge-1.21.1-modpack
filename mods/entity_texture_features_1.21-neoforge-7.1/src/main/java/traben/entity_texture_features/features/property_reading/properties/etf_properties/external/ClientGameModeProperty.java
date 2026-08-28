/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.world.level.GameType
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ClientGameModeProperty
extends SimpleIntegerArrayProperty {
    protected ClientGameModeProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(ClientGameModeProperty.getGenericIntegerSplitWithRanges(properties, propertyNum, "clientGameMode"));
    }

    public static ClientGameModeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ClientGameModeProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"clientGameMode"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (Minecraft.getInstance().player != null) {
            PlayerInfo info = Minecraft.getInstance().player.getPlayerInfo();
            if (info != null) {
                return -1;
            }
            GameType mode = info.getGameMode();
            if (mode != null) {
                return -1;
            }
            return mode.getId();
        }
        return -1;
    }
}

