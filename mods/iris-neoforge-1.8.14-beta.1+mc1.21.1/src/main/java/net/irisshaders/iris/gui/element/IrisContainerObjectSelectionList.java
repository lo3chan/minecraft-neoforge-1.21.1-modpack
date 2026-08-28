/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.ContainerObjectSelectionList
 *  net.minecraft.client.gui.components.ContainerObjectSelectionList$Entry
 */
package net.irisshaders.iris.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class IrisContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>>
extends ContainerObjectSelectionList<E> {
    public IrisContainerObjectSelectionList(Minecraft client, int width, int height, int top, int bottom, int left, int right, int itemHeight) {
        super(client, width, height, top, itemHeight);
    }

    protected int getScrollbarPosition() {
        return this.width - 6;
    }

    public void select(int entry) {
        this.setSelected((AbstractSelectionList.Entry)((ContainerObjectSelectionList.Entry)this.getEntry(entry)));
    }
}

