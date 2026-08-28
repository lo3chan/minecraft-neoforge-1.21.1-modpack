/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 */
package mezz.jei.api.gui.widgets;

import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import net.minecraft.client.gui.navigation.ScreenRectangle;

@Deprecated(since="19.19.3", forRemoval=true)
public interface IScrollGridWidgetFactory<R>
extends ISlottedWidgetFactory<R> {
    public void setPosition(int var1, int var2);

    public ScreenRectangle getArea();
}

