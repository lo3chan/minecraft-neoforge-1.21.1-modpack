/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 */
package net.diebuddies.physics.settings.cloth;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public class LabelEntry
extends BaseEntry {
    private final String text;
    private ChatFormatting extraStyle;

    public LabelEntry(LegacyObjectSelectionList objectSelectionList, String text) {
        super(objectSelectionList, text);
        this.text = text;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        String newText;
        Font font = Minecraft.getInstance().font;
        Object text = this.text;
        if (font.width((FormattedText)Component.literal((String)text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55 && !((String)text).equalsIgnoreCase(newText = font.plainSubstrByWidth((String)text, this.objectSelectionList.getRowWidth() - 58))) {
            text = newText + "...";
        }
        MutableComponent label = Component.literal((String)text);
        if (this.extraStyle != null) {
            label = label.withStyle(this.extraStyle);
        }
        if (hovered) {
            label = label.withStyle(ChatFormatting.BOLD);
            guiGraphics.drawCenteredString(font, (Component)label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xFFFFFF);
        } else {
            guiGraphics.drawCenteredString(font, (Component)label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xC2C2C2);
        }
    }

    @Override
    public Component getNarration() {
        return Component.literal((String)this.text);
    }

    public void setExtraStyle(ChatFormatting extraStyle) {
        this.extraStyle = extraStyle;
    }
}

