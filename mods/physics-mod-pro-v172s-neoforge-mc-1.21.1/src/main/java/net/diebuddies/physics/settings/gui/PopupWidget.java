/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package net.diebuddies.physics.settings.gui;

import java.util.List;
import net.diebuddies.physics.settings.ButtonSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class PopupWidget
extends AbstractWidget {
    private static final int MAX_INFO_WIDTH = 300;
    private String title;
    public List<FormattedCharSequence> info;

    public PopupWidget(String title, int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
        this.title = title;
        this.info = Minecraft.getInstance().font.split((FormattedText)Component.literal((String)title), 300);
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fill(this.getX(), this.getY(), this.width, this.height, -1090519040);
        guiGraphics.pose().translate(0.0f, 0.0f, 600.0f);
        int lineY = 0;
        for (FormattedCharSequence sequence : this.info) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, sequence, this.width / 2, this.height / 2 - 25 + lineY, 0xFFFF55);
            lineY += 10;
        }
    }

    public void updateWidgetNarration(NarrationElementOutput var1) {
    }

    public boolean mouseClicked(double d, double e, int i) {
        return true;
    }

    public static void create(String title, Screen screen, WidgetCreator creator, WidgetRemover remover, Component yes, Component no, PopupCallback callback) {
        PopupPress yesPress = new PopupPress(remover, callback, PopupResponse.YES);
        PopupPress noPress = new PopupPress(remover, callback, PopupResponse.NO);
        PopupWidget background = new PopupWidget(title, 0, 0, screen.width, screen.height, (Component)Component.literal((String)""));
        creator.addWidget(background);
        int yOffset = (background.info.size() - 1) * 10;
        Button noButton = ButtonSettings.builder(screen.width / 2 - 90, screen.height / 2 + 5 + yOffset, 80, 20, no, noPress);
        creator.addWidget((AbstractWidget)noButton);
        Button yesButton = ButtonSettings.builder(screen.width / 2 + 10, screen.height / 2 + 5 + yOffset, 80, 20, yes, yesPress);
        creator.addWidget((AbstractWidget)yesButton);
        screen.children.remove((Object)background);
        screen.children.remove(noButton);
        screen.children.remove(yesButton);
        screen.children.add(0, background);
        screen.children.add(0, noButton);
        screen.children.add(0, yesButton);
        yesPress.background = background;
        yesPress.noButton = noButton;
        yesPress.yesButton = yesButton;
        noPress.background = background;
        noPress.noButton = noButton;
        noPress.yesButton = yesButton;
    }

    public static void create(String title, Screen screen, WidgetCreator creator, WidgetRemover remover, PopupCallback callback) {
        PopupWidget.create(title, screen, creator, remover, CommonComponents.GUI_YES, CommonComponents.GUI_NO, callback);
    }

    private static class PopupPress
    implements Button.OnPress {
        public AbstractWidget background;
        public AbstractWidget noButton;
        public AbstractWidget yesButton;
        private WidgetRemover remover;
        private PopupCallback callback;
        private PopupResponse response;

        public PopupPress(WidgetRemover remover, PopupCallback callback, PopupResponse response) {
            this.remover = remover;
            this.callback = callback;
            this.response = response;
        }

        public void onPress(Button button) {
            this.remover.removeWidget(this.background);
            this.remover.removeWidget(this.noButton);
            this.remover.removeWidget(this.yesButton);
            this.callback.popupClosed(this.response);
        }
    }

    public static enum PopupResponse {
        YES,
        NO;

    }

    public static interface WidgetRemover {
        public void removeWidget(AbstractWidget var1);
    }

    public static interface PopupCallback {
        public void popupClosed(PopupResponse var1);
    }

    public static interface WidgetCreator {
        public void addWidget(AbstractWidget var1);
    }
}

