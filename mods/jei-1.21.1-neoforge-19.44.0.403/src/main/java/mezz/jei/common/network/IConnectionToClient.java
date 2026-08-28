/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package mezz.jei.common.network;

import mezz.jei.common.network.packets.PlayToClientPacket;
import net.minecraft.server.level.ServerPlayer;

public interface IConnectionToClient {
    public <T extends PlayToClientPacket<T>> void sendPacketToClient(T var1, ServerPlayer var2);
}

