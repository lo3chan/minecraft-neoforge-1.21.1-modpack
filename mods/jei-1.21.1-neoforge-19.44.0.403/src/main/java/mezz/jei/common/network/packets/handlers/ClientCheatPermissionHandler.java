/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.player.Player
 */
package mezz.jei.common.network.packets.handlers;

import java.util.List;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.network.ClientPacketContext;
import mezz.jei.common.util.ChatUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ClientCheatPermissionHandler {
    public static void handleHasCheatPermission(ClientPacketContext context, boolean hasPermission, List<String> allowedCheatingMethods) {
        if (!hasPermission) {
            LocalPlayer player = context.player();
            ChatUtil.writeChatMessage((Player)player, "jei.chat.error.no.cheat.permission.1", ChatFormatting.RED);
            if (allowedCheatingMethods.isEmpty()) {
                ChatUtil.writeChatMessage((Player)player, "jei.chat.error.no.cheat.permission.disabled", ChatFormatting.RED);
            } else {
                ChatUtil.writeChatMessage((Player)player, "jei.chat.error.no.cheat.permission.enabled", ChatFormatting.RED);
                for (String allowedCheatingMethod : allowedCheatingMethods) {
                    ChatUtil.writeChatMessage((Player)player, allowedCheatingMethod, ChatFormatting.RED);
                }
            }
            IClientToggleState toggleState = Internal.getClientToggleState();
            toggleState.setCheatItemsEnabled(false);
            player.closeContainer();
        }
    }
}

