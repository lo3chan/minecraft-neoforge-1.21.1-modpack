/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.gui.ingredient;

import java.util.List;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IRecipeSlotDrawable
extends IRecipeSlotView {
    @Deprecated(since="19.34.0", forRemoval=true)
    public void draw(GuiGraphics var1);

    default public void draw(GuiGraphics guiGraphics, boolean hovered) {
        this.draw(guiGraphics);
        if (hovered) {
            this.drawHoverOverlays(guiGraphics);
        }
    }

    @Deprecated(since="19.34.0", forRemoval=true)
    public void drawHoverOverlays(GuiGraphics var1);

    @Deprecated(since="19.22.0", forRemoval=true)
    public List<Component> getTooltip();

    @Deprecated(since="19.22.0", forRemoval=true)
    public void getTooltip(ITooltipBuilder var1);

    public void drawTooltip(GuiGraphics var1, int var2, int var3);

    public boolean isMouseOver(double var1, double var3);

    public void setPosition(int var1, int var2);

    public IIngredientConsumer createDisplayOverrides();

    public void clearDisplayOverrides();

    @Deprecated(since="19.6.0", forRemoval=true)
    public Rect2i getRect();

    @Deprecated(since="19.5.4", forRemoval=true)
    default public void addTooltipCallback(IRecipeSlotTooltipCallback tooltipCallback) {
    }

    public Rect2i getAreaIncludingBackground();
}

