/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 */
package net.diebuddies.physics.settings.cloth;

import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.ClothDisplayScreen;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.verlet.Cloth;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class ClothSelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    private String selectedCategory;
    private ClothDisplayScreen clothDisplay;

    public ClothSelectionList(ClothDisplayScreen clothDisplay, String selectedCategory, Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.selectedCategory = selectedCategory;
        this.clothDisplay = clothDisplay;
        this.x0 = 50;
        this.xOffset = 0;
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.clearEntries();
        String selectedCloth = ConfigCloth.getCategory(this.clothDisplay.getSelectedEntity(), this.selectedCategory);
        LabelEntry empty = new LabelEntry((LegacyObjectSelectionList)this, "EMPTY");
        empty.setUserData((Object)ClothObject.EMPTY);
        this.addEntry(empty);
        for (Cloth cloth : PhysicsMod.cloth.values()) {
            if (!cloth.rules.getCategory().equals(this.selectedCategory)) continue;
            String name = cloth.name;
            LabelEntry entry = new LabelEntry((LegacyObjectSelectionList)this, name);
            if (cloth.rules.isLocal()) {
                entry.setExtraStyle(ChatFormatting.GOLD);
            }
            this.addEntry(entry);
            if (!name.equalsIgnoreCase(selectedCloth)) continue;
            this.setSelected(entry);
        }
        if (this.getSelected() == null) {
            this.setSelected(empty);
        }
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
        return 160;
    }

    @Override
    public void setSelected(BaseEntry entry) {
        super.setSelected(entry);
        Object clothPiece = entry.getUserData();
        if (clothPiece == ClothObject.EMPTY) {
            ConfigCloth.setCategory(this.clothDisplay.getSelectedEntity(), this.selectedCategory, null);
        } else {
            ConfigCloth.setCategory(this.clothDisplay.getSelectedEntity(), this.selectedCategory, (String)clothPiece);
        }
        this.clothDisplay.loadCloth();
    }

    public static enum ClothObject {
        EMPTY;

    }
}

