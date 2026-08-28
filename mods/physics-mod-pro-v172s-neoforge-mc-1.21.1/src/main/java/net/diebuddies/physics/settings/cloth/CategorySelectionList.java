/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.Minecraft
 */
package net.diebuddies.physics.settings.cloth;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Collections;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.ClothDisplayScreen;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.verlet.Cloth;
import net.minecraft.client.Minecraft;

public class CategorySelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    private ClothDisplayScreen clothDisplay;

    public CategorySelectionList(ClothDisplayScreen clothDisplay, Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraft, width, height, top, bottom, itemHeight);
        this.clothDisplay = clothDisplay;
        this.x0 = 50;
        this.xOffset = 0;
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.clearEntries();
        ObjectOpenHashSet categories = new ObjectOpenHashSet();
        for (Cloth cloth : PhysicsMod.cloth.values()) {
            categories.add(cloth.rules.getCategory());
        }
        ObjectArrayList sortedCategories = new ObjectArrayList((Collection)categories);
        Collections.sort(sortedCategories);
        for (String category : sortedCategories) {
            LabelEntry entry = new LabelEntry((LegacyObjectSelectionList)this, category);
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

