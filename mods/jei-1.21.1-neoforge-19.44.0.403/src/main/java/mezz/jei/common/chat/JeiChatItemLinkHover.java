/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.ChatComponent
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Style
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.chat;

import java.util.Optional;
import mezz.jei.common.chat.JeiChatItemLinks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public final class JeiChatItemLinkHover {
    private JeiChatItemLinkHover() {
    }

    public static Optional<Style> getHoveredStyle(Screen screen, double mouseX, double mouseY) {
        if (!(screen instanceof ChatScreen)) {
            return Optional.empty();
        }
        Minecraft minecraft = Minecraft.getInstance();
        ChatComponent chatComponent = minecraft.gui.getChat();
        Style style = chatComponent.getClickedComponentStyleAt(mouseX, mouseY);
        return Optional.ofNullable(style);
    }

    public static Optional<HoveredText> getHoveredText(Screen screen, double mouseX, double mouseY) {
        return JeiChatItemLinkHover.getHoveredStyle(screen, mouseX, mouseY).map(style -> {
            Rect2i area = new Rect2i((int)mouseX, (int)mouseY, 1, 1);
            return new HoveredText((Style)style, area);
        });
    }

    public static Optional<JeiChatItemLinks.IngredientLink> getIngredientLink(@Nullable Style style) {
        if (style == null) {
            return Optional.empty();
        }
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null || clickEvent.getAction() != ClickEvent.Action.RUN_COMMAND) {
            return Optional.empty();
        }
        String command = clickEvent.getValue();
        return JeiChatItemLinks.parseShowRecipeCommand(command);
    }

    public record HoveredText(Style style, Rect2i area) {
    }
}

