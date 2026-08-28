/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.animation;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.animation.AnimationEditScreen;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.EditButton;
import net.diebuddies.physics.settings.gui.RemoveButton;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AnimationEditList
extends LegacyObjectSelectionList<BaseEntry> {
    private Long2ObjectMap<Animation> settings;
    private Map<BaseEntry, Button> buttons1 = new Object2ObjectOpenHashMap();
    private Map<BaseEntry, Button> buttons2 = new Object2ObjectOpenHashMap();

    public AnimationEditList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.settings = ConfigAnimations.animations;
        this.refreshEntries();
        if (this.getEntry(0) != null) {
            this.setSelected((BaseEntry)this.getEntry(0));
        }
    }

    public void refreshEntries() {
        this.clearEntries();
        this.buttons1 = new Object2ObjectOpenHashMap();
        this.buttons2 = new Object2ObjectOpenHashMap();
        for (Long2ObjectMap.Entry entry : this.settings.long2ObjectEntrySet()) {
            long identifier = entry.getLongKey();
            LabelEntry labelEntry = new LabelEntry((LegacyObjectSelectionList)this, ((Animation)entry.getValue()).name);
            labelEntry.setUserData(identifier);
            this.addEntry(labelEntry);
        }
    }

    public void addSetting(long identifier, Animation animation) {
        this.settings.put(identifier, (Object)animation);
        this.refreshEntries();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton) | this.listButtons(null, mouseX, mouseY, mouseButton, 0.0f, false);
    }

    public boolean listButtons(GuiGraphics guiGraphics, double mouseX, double mouseY, int mouseButton, float tickDelta, boolean render) {
        boolean clicked = false;
        for (int i = 0; i < this.getItemCount(); ++i) {
            int entryY = this.getRowTop(i);
            int p = this.getRowBottomCustom(i);
            if (p < this.y0 || entryY > this.y1) continue;
            int entryHeight = this.itemHeight - 4;
            BaseEntry entry = (BaseEntry)this.getEntry(i);
            Button editButton = this.buttons1.computeIfAbsent(entry, key -> new EditButton(this.getRowRight() + 3, entryY, entryHeight, entryHeight - 1, (Component)Component.literal((String)""), source -> {
                Long identifier = (Long)entry.getUserData();
                if (identifier != null) {
                    this.minecraft.setScreen((Screen)new AnimationEditScreen(this.minecraft.screen, (Animation)this.settings.get(identifier.longValue()), identifier));
                }
            }));
            editButton.setX(this.getRowRight() + 3);
            editButton.setY(entryY);
            Button removeButton = this.buttons2.computeIfAbsent(entry, key -> new RemoveButton(this.getRowRight() + 26, entryY, entryHeight, entryHeight - 1, (Component)Component.literal((String)""), source -> {
                this.removeEntry(entry);
                this.settings.remove(((Long)entry.getUserData()).longValue());
                ConfigAnimations.save();
            }));
            removeButton.setX(this.getRowRight() + 26);
            removeButton.setY(entryY);
            if (!render) {
                if (editButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
                if (!removeButton.mouseClicked(mouseX, mouseY, mouseButton)) continue;
                return true;
            }
            removeButton.render(guiGraphics, (int)mouseX, (int)mouseY, tickDelta);
            editButton.render(guiGraphics, (int)mouseX, (int)mouseY, tickDelta);
        }
        return clicked;
    }

    @Override
    protected void renderList(GuiGraphics guiGraphics, int x, int scrollAmount, int mouseX, int mouseY, float tickDelta) {
        super.renderList(guiGraphics, x, scrollAmount, mouseX, mouseY, tickDelta);
        this.listButtons(guiGraphics, mouseX, mouseY, 0, tickDelta, true);
    }

    private int getRowBottomCustom(int i) {
        return this.getRowTop(i) + this.itemHeight;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 20;
    }

    @Override
    public int getRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    @Override
    public int getRowWidth() {
        return 220;
    }

    @Override
    public void setSelected(BaseEntry entry) {
        super.setSelected(entry);
    }
}

