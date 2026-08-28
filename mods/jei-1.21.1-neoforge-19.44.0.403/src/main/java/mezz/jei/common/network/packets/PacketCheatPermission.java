/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package mezz.jei.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.network.ClientPacketContext;
import mezz.jei.common.network.packets.PlayToClientPacket;
import mezz.jei.common.network.packets.handlers.ClientCheatPermissionHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PacketCheatPermission
extends PlayToClientPacket<PacketCheatPermission> {
    public static final CustomPacketPayload.Type<PacketCheatPermission> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"jei", (String)"cheat_permission"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketCheatPermission> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, p -> p.hasPermission, (StreamCodec)ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), p -> p.allowedCheatingMethods, PacketCheatPermission::new);
    private final boolean hasPermission;
    private final List<String> allowedCheatingMethods;

    public PacketCheatPermission(boolean hasPermission, IServerConfig serverConfig) {
        this(hasPermission, PacketCheatPermission.getAllowedCheatingMethods(serverConfig));
    }

    public PacketCheatPermission(boolean hasPermission, List<String> allowedCheatingMethods) {
        this.hasPermission = hasPermission;
        this.allowedCheatingMethods = allowedCheatingMethods;
    }

    @Override
    public CustomPacketPayload.Type<PacketCheatPermission> type() {
        return TYPE;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PacketCheatPermission> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void process(ClientPacketContext context) {
        ClientCheatPermissionHandler.handleHasCheatPermission(context, this.hasPermission, this.allowedCheatingMethods);
    }

    @NotNull
    private static List<String> getAllowedCheatingMethods(IServerConfig serverConfig) {
        ArrayList<String> allowedCheatingMethods = new ArrayList<String>();
        if (serverConfig.isCheatModeEnabledForOp()) {
            allowedCheatingMethods.add("jei.chat.error.no.cheat.permission.op");
        }
        if (serverConfig.isCheatModeEnabledForCreative()) {
            allowedCheatingMethods.add("jei.chat.error.no.cheat.permission.creative");
        }
        if (serverConfig.isCheatModeEnabledForGive()) {
            allowedCheatingMethods.add("jei.chat.error.no.cheat.permission.give");
        }
        return allowedCheatingMethods;
    }
}

