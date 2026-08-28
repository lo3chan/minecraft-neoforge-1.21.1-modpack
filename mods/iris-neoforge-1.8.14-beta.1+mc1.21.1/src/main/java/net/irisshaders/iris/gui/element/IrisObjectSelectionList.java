/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractSelectionList
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 */
package net.irisshaders.iris.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class IrisObjectSelectionList<E extends AbstractSelectionList.Entry<E>>
extends AbstractSelectionList<E> {
    public IrisObjectSelectionList(Minecraft client, int width, int height, int top, int bottom, int left, int right, int itemHeight) {
        super(client, width, height, top, itemHeight);
    }

    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    public void select(int entry) {
        this.setSelected(this.getEntry(entry));
    }

    public void updateWidgetNarration(NarrationElementOutput p0) {
    }
}

