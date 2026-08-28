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
 *  net.minecraft.world.item.ItemStack
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
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
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketDeletePlayerItem
extends PlayToServerPacket<PacketDeletePlayerItem> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CustomPacketPayload.Type<PacketDeletePlayerItem> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"jei", (String)"delete_player_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketDeletePlayerItem> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.STREAM_CODEC, p -> p.itemStack, PacketDeletePlayerItem::new);
    private final ItemStack itemStack;

    public PacketDeletePlayerItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public CustomPacketPayload.Type<PacketDeletePlayerItem> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PacketDeletePlayerItem> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void process(ServerPacketContext context) {
        IServerConfig serverConfig;
        ServerPlayer player = context.player();
        if (ServerCommandUtil.hasPermissionForCheatMode((Player)player, serverConfig = context.serverConfig())) {
            ItemStack playerItem = player.containerMenu.getCarried();
            if (playerItem.getItem() == this.itemStack.getItem()) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            } else if (!playerItem.isEmpty()) {
                LOGGER.warn("Player '{} ({})' tried to delete ItemStack '{}' but is currently holding a different ItemStack '{}'.", (Object)player.getName(), (Object)player.getUUID(), (Object)this.itemStack.getDisplayName(), (Object)playerItem.getDisplayName());
            }
        } else {
            ItemStack playerItem = player.containerMenu.getCarried();
            LOGGER.error("Player '{} ({})' tried to delete ItemStack '{}' but does not have permission.", (Object)player.getName(), (Object)player.getUUID(), (Object)playerItem.getDisplayName());
            IConnectionToClient connection = context.connection();
            connection.sendPacketToClient(new PacketCheatPermission(false, serverConfig), player);
        }
    }
}

