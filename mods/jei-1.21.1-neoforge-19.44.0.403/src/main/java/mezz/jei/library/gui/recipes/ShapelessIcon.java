/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package mezz.jei.library.gui.recipes;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class ShapelessIcon {
    private final IDrawable icon;
    private final ImmutableRect2i area;

    public ShapelessIcon(IDrawable icon, int x, int y) {
        this.icon = icon;
        this.area = new ImmutableRect2i(x, y, icon.getWidth(), icon.getHeight());
    }

    public void draw(GuiGraphics guiGraphics) {
        this.icon.draw(guiGraphics, this.area.getX(), this.area.getY());
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return this.area.contains(mouseX, mouseY);
    }

    public void addTooltip(JeiTooltip tooltip) {
        tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.shapeless.recipe"));
    }
}

