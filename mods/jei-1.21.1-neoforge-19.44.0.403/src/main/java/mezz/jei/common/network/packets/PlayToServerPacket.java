/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package mezz.jei.common.network.packets;

import mezz.jei.common.network.ServerPacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class PlayToServerPacket<T extends PlayToServerPacket<T>>
implements CustomPacketPayload {
    public abstract CustomPacketPayload.Type<T> type();

    public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    public abstract void process(ServerPacketContext var1);
}

