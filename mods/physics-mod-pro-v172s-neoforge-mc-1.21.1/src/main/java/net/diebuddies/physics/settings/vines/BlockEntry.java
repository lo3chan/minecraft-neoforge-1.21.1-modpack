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
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package net.diebuddies.physics.settings.vines;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class BlockEntry
extends BaseEntry {
    private final String text;
    private ItemStack itemStack;

    public BlockEntry(LegacyObjectSelectionList objectSelectionList, String text, Block block) {
        super(objectSelectionList, block);
        this.text = text;
        try {
            this.itemStack = new ItemStack((ItemLike)block);
        }
        catch (Exception exception) {
            // empty catch block
        }
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
        if (hovered) {
            label = label.withStyle(ChatFormatting.BOLD);
            guiGraphics.drawCenteredString(font, (Component)label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xFFFFFF);
        } else {
            guiGraphics.drawCenteredString(font, (Component)label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 0xC2C2C2);
        }
        try {
            guiGraphics.renderFakeItem(this.itemStack, this.objectSelectionList.getRowLeft() + 2, y + (entryHeight - 16) / 2);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public String getText() {
        return this.text;
    }

    @Override
    public Component getNarration() {
        return Component.literal((String)this.text);
    }
}

