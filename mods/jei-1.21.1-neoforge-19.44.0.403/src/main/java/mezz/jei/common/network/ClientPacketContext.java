/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 */
package mezz.jei.common.network;

import mezz.jei.common.network.IConnectionToServer;
import net.minecraft.client.player.LocalPlayer;

public record ClientPacketContext(LocalPlayer player, IConnectionToServer connection) {
}

