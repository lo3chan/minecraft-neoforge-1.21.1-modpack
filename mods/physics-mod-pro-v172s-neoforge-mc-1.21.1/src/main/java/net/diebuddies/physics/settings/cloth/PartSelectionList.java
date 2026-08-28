/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.geom.ModelPart
 */
package net.diebuddies.physics.settings.cloth;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.settings.cloth.ClothDisplayScreen;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class PartSelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    private ClothDisplayScreen clothDisplay;

    public PartSelectionList(ClothDisplayScreen clothDisplay, Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.clothDisplay = clothDisplay;
        this.x0 = 50;
        this.xOffset = 0;
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.clearEntries();
        for (ModelPart part : ClothConstants.getModelParts(this.clothDisplay.entityType)) {
            String name = ((ModelPartParent)part).physicsmod$getName();
            if (name == null) continue;
            LabelEntry entry = new LabelEntry((LegacyObjectSelectionList)this, name);
            this.addEntry(entry);
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
        if (entry != null) {
            this.clothDisplay.goToClothScreen((String)entry.getUserData());
        }
    }
}

