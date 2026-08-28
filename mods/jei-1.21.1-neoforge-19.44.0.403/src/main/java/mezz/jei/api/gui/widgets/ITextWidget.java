/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 */
package mezz.jei.api.gui.widgets;

import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.IPlaceable;
import mezz.jei.api.gui.placement.VerticalAlignment;
import net.minecraft.client.gui.Font;

public interface ITextWidget
extends IPlaceable<ITextWidget> {
    public ITextWidget setFont(Font var1);

    public ITextWidget setColor(int var1);

    public ITextWidget setLineSpacing(int var1);

    public ITextWidget setShadow(boolean var1);

    public ITextWidget setTextAlignment(HorizontalAlignment var1);

    public ITextWidget setTextAlignment(VerticalAlignment var1);

    @Deprecated(since="19.19.0", forRemoval=true)
    default public ITextWidget alignHorizontalLeft() {
        return this.setTextAlignment(HorizontalAlignment.LEFT);
    }

    @Deprecated(since="19.19.0", forRemoval=true)
    default public ITextWidget alignHorizontalCenter() {
        return this.setTextAlignment(HorizontalAlignment.CENTER);
    }

    @Deprecated(since="19.19.0", forRemoval=true)
    default public ITextWidget alignHorizontalRight() {
        return this.setTextAlignment(HorizontalAlignment.RIGHT);
    }

    @Deprecated(since="19.19.0", forRemoval=true)
    default public ITextWidget alignVerticalTop() {
        return this.setTextAlignment(VerticalAlignment.TOP);
    }

    @Deprecated(since="19.19.0", forRemoval=true)
    default public ITextWidget alignVerticalCenter() {
        return this.setTextAlignment(VerticalAlignment.CENTER);
    }

    @Deprecated(since="19.19.0", forRemoval=true)
    default public ITextWidget alignVerticalBottom() {
        return this.setTextAlignment(VerticalAlignment.BOTTOM);
    }
}

