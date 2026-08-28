/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.neoforged.neoforge.client.event.RenderTooltipEvent$Pre
 */
package mezz.jei.neoforge.chat;

import java.util.Optional;
import mezz.jei.gui.chat.ChatIngredientTooltip;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

public final class JeiChatTooltipEventHandler {
    private static boolean renderingJeiChatTooltip;

    private JeiChatTooltipEventHandler() {
    }

    public static void register(PermanentEventSubscriptions subscriptions) {
        subscriptions.register(RenderTooltipEvent.Pre.class, JeiChatTooltipEventHandler::onRenderTooltipPre);
    }

    private static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        if (renderingJeiChatTooltip) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        Optional<ChatIngredientTooltip.IngredientTooltipData<?>> optionalTooltipData = ChatIngredientTooltip.getTooltipForHoveredChatLink(screen, event.getX(), event.getY());
        if (optionalTooltipData.isEmpty()) {
            return;
        }
        ChatIngredientTooltip.IngredientTooltipData<?> tooltipData = optionalTooltipData.get();
        if (JeiChatTooltipEventHandler.renderJeiChatTooltip(event, tooltipData)) {
            event.setCanceled(true);
        }
    }

    private static <T> boolean renderJeiChatTooltip(RenderTooltipEvent.Pre event, ChatIngredientTooltip.IngredientTooltipData<T> tooltipData) {
        if (tooltipData.tooltip().isEmpty()) {
            return false;
        }
        renderingJeiChatTooltip = true;
        try {
            tooltipData.draw(event.getGraphics(), event.getX(), event.getY());
        }
        finally {
            renderingJeiChatTooltip = false;
        }
        return true;
    }
}

