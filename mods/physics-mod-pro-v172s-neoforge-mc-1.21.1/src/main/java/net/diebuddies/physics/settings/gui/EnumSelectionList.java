/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.locale.Language
 */
package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

public class EnumSelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    public String filter = "";
    private Enum<?> selectedEnum;

    public EnumSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m, Enum<?> selectedEnum) {
        super(minecraft, i, j, k, l, m);
        this.selectedEnum = selectedEnum;
        this.refreshEntries();
    }

    public void refreshEntries() {
        Enum[] enums;
        this.clearEntries();
        LabelEntry first = null;
        for (Enum cenum : enums = (Enum[])this.selectedEnum.getDeclaringClass().getEnumConstants()) {
            String name = Language.getInstance().getOrDefault(cenum.toString());
            if (!name.contains(this.filter.toLowerCase())) continue;
            LabelEntry listEntry = new LabelEntry((LegacyObjectSelectionList)this, name);
            listEntry.setUserData(cenum);
            this.addEntry(listEntry);
            if (first != null) continue;
            first = listEntry;
        }
        if (first != null) {
            this.ensureVisible(first);
        }
    }
}

