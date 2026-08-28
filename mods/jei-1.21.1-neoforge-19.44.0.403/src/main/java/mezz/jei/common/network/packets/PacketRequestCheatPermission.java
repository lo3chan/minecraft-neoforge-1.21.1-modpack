/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package mezz.jei.common.network.packets;

import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.network.IConnectionToClient;
import mezz.jei.common.network.ServerPacketContext;
import mezz.jei.common.network.packets.PacketCheatPermission;
import mezz.jei.common.network.packets.PlayToServerPacket;
import mezz.jei.common.util.ServerCommandUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PacketRequestCheatPermission
extends PlayToServerPacket<PacketRequestCheatPermission> {
    public static final PacketRequestCheatPermission INSTANCE = new PacketRequestCheatPermission();
    public static final CustomPacketPayload.Type<PacketRequestCheatPermission> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"jei", (String)"request_cheat_permission"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketRequestCheatPermission> STREAM_CODEC = StreamCodec.unit((Object)INSTANCE);

    private PacketRequestCheatPermission() {
    }

    @Override
    public CustomPacketPayload.Type<PacketRequestCheatPermission> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PacketRequestCheatPermission> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void process(ServerPacketContext context) {
        ServerPlayer player = context.player();
        IServerConfig serverConfig = context.serverConfig();
        boolean hasPermission = ServerCommandUtil.hasPermissionForCheatMode((Player)player, serverConfig);
        PacketCheatPermission packetCheatPermission = new PacketCheatPermission(hasPermission, serverConfig);
        IConnectionToClient connection = context.connection();
        connection.sendPacketToClient(packetCheatPermission, player);
    }
}

