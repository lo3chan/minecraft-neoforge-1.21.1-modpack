/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package mezz.jei.neoforge.network;

import mezz.jei.common.network.IConnectionToClient;
import mezz.jei.common.network.packets.PlayToClientPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ConnectionToClient
implements IConnectionToClient {
    @Override
    public <T extends PlayToClientPacket<T>> void sendPacketToClient(T packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer((ServerPlayer)player, packet, (CustomPacketPayload[])new CustomPacketPayload[0]);
    }
}

