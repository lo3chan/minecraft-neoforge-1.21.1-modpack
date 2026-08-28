/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package mezz.jei.common.network;

import mezz.jei.common.config.IServerConfig;
import mezz.jei.common.network.IConnectionToClient;
import net.minecraft.server.level.ServerPlayer;

public record ServerPacketContext(ServerPlayer player, IServerConfig serverConfig, IConnectionToClient connection) {
}

