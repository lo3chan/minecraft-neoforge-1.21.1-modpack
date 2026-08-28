/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.Nullable;

public final class ClientConnectionHelper {
    private static final String UNKNOWN_SERVER_BRAND = "unknown";

    private ClientConnectionHelper() {
    }

    public static String getServerBrand() {
        ClientPacketListener clientPacketListener = ClientConnectionHelper.getConnectedClientPacketListener();
        if (clientPacketListener == null) {
            return UNKNOWN_SERVER_BRAND;
        }
        String serverBrand = clientPacketListener.serverBrand();
        if (serverBrand == null || serverBrand.isBlank()) {
            return UNKNOWN_SERVER_BRAND;
        }
        return serverBrand;
    }

    public static boolean hasServerBrand(String expectedBrand) {
        for (String serverBrand : ClientConnectionHelper.getServerBrand().split(",")) {
            if (!serverBrand.trim().equalsIgnoreCase(expectedBrand)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public static ClientPacketListener getConnectedClientPacketListener() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener clientPacketListener = minecraft.getConnection();
        if (clientPacketListener == null || !clientPacketListener.getConnection().isConnected()) {
            return null;
        }
        return clientPacketListener;
    }
}

