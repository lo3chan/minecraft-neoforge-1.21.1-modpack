/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvent
 */
package net.diebuddies.physics.settings.animation;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;

public class SoundEntry
extends BaseEntry {
    private final String soundID;
    private SoundEvent sound;

    public SoundEntry(LegacyObjectSelectionList objectSelectionList, String soundID) {
        super(objectSelectionList, soundID);
        this.soundID = soundID;
        this.sound = PhysicsMod.registeredSounds.get(soundID);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)this.sound, (float)1.0f));
        if (!this.isSelected()) {
            this.objectSelectionList.setSelected(this);
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        String newText;
        Font font = Minecraft.getInstance().font;
        Object text = this.soundID;
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
    }

    public String getText() {
        return this.soundID;
    }

    @Override
    public Component getNarration() {
        return Component.literal((String)this.soundID);
    }
}

