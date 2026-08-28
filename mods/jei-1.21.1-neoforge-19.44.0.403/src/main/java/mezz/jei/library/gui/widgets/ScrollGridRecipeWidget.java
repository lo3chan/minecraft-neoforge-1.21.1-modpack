/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 */
package mezz.jei.library.gui.widgets;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.common.Internal;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.ImmutableSize2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.library.gui.widgets.AbstractScrollWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class ScrollGridRecipeWidget
extends AbstractScrollWidget
implements IScrollGridWidget,
ISlottedRecipeWidget,
IJeiInputHandler {
    private final IDrawable slotBackground;
    private final int columns;
    private final int visibleRows;
    private final int hiddenRows;
    private final List<IRecipeSlotDrawable> slots;

    public static ImmutableSize2i calculateSize(int columns, int visibleRows) {
        IDrawableStatic slotBackground = Internal.getTextures().getSlot();
        return new ImmutableSize2i(columns * slotBackground.getWidth() + ScrollGridRecipeWidget.getScrollBoxScrollbarExtraWidth(), visibleRows * slotBackground.getHeight());
    }

    public static ScrollGridRecipeWidget create(List<IRecipeSlotDrawable> slots, int columns, int visibleRows) {
        ImmutableSize2i size = ScrollGridRecipeWidget.calculateSize(columns, visibleRows);
        ImmutableRect2i area = new ImmutableRect2i(0, 0, size.width(), size.height());
        return new ScrollGridRecipeWidget(area, columns, visibleRows, slots);
    }

    public ScrollGridRecipeWidget(ImmutableRect2i area, int columns, int visibleRows, List<IRecipeSlotDrawable> slots) {
        super(area);
        this.slots = slots;
        this.slotBackground = Internal.getTextures().getSlot();
        this.columns = columns;
        this.visibleRows = visibleRows;
        int totalRows = MathUtil.divideCeil(slots.size(), columns);
        this.hiddenRows = Math.max(totalRows - visibleRows, 0);
    }

    @Override
    public ScrollGridRecipeWidget setPosition(int xPos, int yPos) {
        this.area = this.area.setPosition(xPos, yPos);
        return this;
    }

    @Override
    public int getWidth() {
        return this.area.width();
    }

    @Override
    public int getHeight() {
        return this.area.height();
    }

    @Override
    public ScreenRectangle getScreenRectangle() {
        return this.area.toScreenRectangle();
    }

    @Override
    protected int getVisibleAmount() {
        return this.visibleRows;
    }

    @Override
    protected int getHiddenAmount() {
        return this.hiddenRows;
    }

    @Override
    protected void drawContents(GuiGraphics guiGraphics, double mouseX, double mouseY, float scrollOffsetY) {
        int totalSlots = this.slots.size();
        int firstRow = this.getRowIndexForScroll(this.hiddenRows, this.getScrollOffsetY());
        int firstIndex = this.columns * firstRow;
        int slotWidth = this.slotBackground.getWidth();
        int slotHeight = this.slotBackground.getHeight();
        for (int row = 0; row < this.visibleRows; ++row) {
            int y = row * slotHeight;
            for (int column = 0; column < this.columns; ++column) {
                int x = column * slotWidth;
                int slotIndex = firstIndex + row * this.columns + column;
                this.slotBackground.draw(guiGraphics, x, y);
                if (slotIndex >= totalSlots) continue;
                IRecipeSlotDrawable slot = this.slots.get(slotIndex);
                slot.setPosition(x + 1, y + 1);
                slot.draw(guiGraphics, slot.isMouseOver(mouseX, mouseY));
            }
        }
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        int firstRow = this.getRowIndexForScroll(this.hiddenRows, this.getScrollOffsetY());
        int startIndex = firstRow * this.columns;
        int endIndex = Math.min(startIndex + this.visibleRows * this.columns, this.slots.size());
        for (int i = startIndex; i < endIndex; ++i) {
            IRecipeSlotDrawable slot = this.slots.get(i);
            if (!slot.isMouseOver(mouseX, mouseY)) continue;
            return Optional.of(new RecipeSlotUnderMouse(slot, this.getPosition()));
        }
        return Optional.empty();
    }

    private int getRowIndexForScroll(int hiddenRows, float scrollOffset) {
        int rowIndex = (int)((double)(scrollOffset * (float)hiddenRows) + 0.5);
        return Math.max(rowIndex, 0);
    }

    @Override
    protected float calculateScrollAmount(double scrollDeltaY) {
        int hiddenRows = this.getHiddenAmount();
        return (float)(scrollDeltaY / (double)hiddenRows);
    }
}

