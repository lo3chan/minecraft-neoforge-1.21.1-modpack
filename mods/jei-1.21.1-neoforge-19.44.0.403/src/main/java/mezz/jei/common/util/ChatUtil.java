/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 */
package mezz.jei.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public final class ChatUtil {
    private ChatUtil() {
    }

    public static void writeChatMessage(Player player, String translationKey, ChatFormatting color) {
        MutableComponent component = Component.translatable((String)translationKey).withStyle(color);
        ChatUtil.writeChatMessage(player, (Component)component);
    }

    public static void writeChatMessage(Player player, Component component) {
        player.sendSystemMessage(component);
    }
}

