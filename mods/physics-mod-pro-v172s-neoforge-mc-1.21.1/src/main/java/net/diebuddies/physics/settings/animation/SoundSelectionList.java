/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Minecraft
 */
package net.diebuddies.physics.settings.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.animation.SoundEntry;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;

public class SoundSelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    public String filter = "";

    public SoundSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.clearEntries();
        ObjectArrayList ids = new ObjectArrayList();
        for (String id : PhysicsMod.registeredSounds.keySet()) {
            ids.add(id);
        }
        Collections.sort(ids);
        SoundEntry first = null;
        for (String id : ids) {
            if (!id.toLowerCase().contains(this.filter.toLowerCase())) continue;
            SoundEntry entry = new SoundEntry((LegacyObjectSelectionList)this, id);
            this.addEntry(entry);
            if (first != null) continue;
            first = entry;
        }
        if (first != null) {
            this.ensureVisible(first);
        }
    }
}

