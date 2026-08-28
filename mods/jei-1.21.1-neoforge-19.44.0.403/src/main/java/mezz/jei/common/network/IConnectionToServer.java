/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package mezz.jei.common.network;

import mezz.jei.common.network.packets.PlayToServerPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IConnectionToServer {
    public boolean isJeiOnServer();

    public boolean isSameModLoader();

    public boolean canSendPacket(CustomPacketPayload.Type<?> var1);

    public <T extends PlayToServerPacket<T>> void sendPacketToServer(T var1);

    default public void onRuntimeStopped() {
    }
}

