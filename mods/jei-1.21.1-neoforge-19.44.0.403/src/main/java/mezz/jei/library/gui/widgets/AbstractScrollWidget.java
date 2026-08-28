/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenPosition
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.util.Mth
 */
package mezz.jei.library.gui.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;

public abstract class AbstractScrollWidget
implements IRecipeWidget,
IJeiInputHandler {
    private static final int SCROLLBAR_PADDING = 2;
    private static final int SCROLLBAR_WIDTH = 14;
    private static final int MIN_SCROLL_MARKER_HEIGHT = 14;
    protected ImmutableRect2i area;
    protected final ImmutableRect2i contentsArea;
    private final ImmutableRect2i scrollArea;
    private final ScalableDrawable scrollbarMarker;
    private final ScalableDrawable scrollbarBackground;
    private double dragOriginY = -1.0;
    private float scrollOffsetY = 0.0f;

    public static int getScrollBoxScrollbarExtraWidth() {
        return 16;
    }

    protected static ImmutableRect2i calculateScrollArea(int width, int height) {
        return new ImmutableRect2i(width - 14, 0, 14, height);
    }

    public AbstractScrollWidget(ImmutableRect2i area) {
        this.area = area;
        this.scrollArea = AbstractScrollWidget.calculateScrollArea(area.width(), area.height());
        Textures textures = Internal.getTextures();
        this.scrollbarMarker = textures.getScrollbarMarker();
        this.scrollbarBackground = textures.getScrollbarBackground();
        this.contentsArea = new ImmutableRect2i(0, 0, area.width() - AbstractScrollWidget.getScrollBoxScrollbarExtraWidth(), area.height());
    }

    protected ImmutableRect2i calculateScrollbarMarkerArea() {
        int totalSpace = this.scrollArea.height() - 2;
        int scrollMarkerWidth = this.scrollArea.width() - 2;
        int scrollMarkerHeight = Math.round((float)totalSpace * ((float)this.getVisibleAmount() / (float)(this.getVisibleAmount() + this.getHiddenAmount())));
        scrollMarkerHeight = Math.max(scrollMarkerHeight, 14);
        int scrollbarMarkerY = Math.round((float)(totalSpace - scrollMarkerHeight) * this.scrollOffsetY);
        return new ImmutableRect2i(this.scrollArea.getX() + 1, this.scrollArea.getY() + 1 + scrollbarMarkerY, scrollMarkerWidth, scrollMarkerHeight);
    }

    protected abstract int getVisibleAmount();

    protected abstract int getHiddenAmount();

    protected abstract void drawContents(GuiGraphics var1, double var2, double var4, float var6);

    protected float getScrollOffsetY() {
        return this.scrollOffsetY;
    }

    @Override
    public final ScreenRectangle getArea() {
        return this.area.toScreenRectangle();
    }

    @Override
    public final ScreenPosition getPosition() {
        return this.area.getScreenPosition();
    }

    @Override
    public final void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.scrollbarBackground.draw(guiGraphics, this.scrollArea);
        ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
        this.scrollbarMarker.draw(guiGraphics, scrollbarMarkerArea);
        this.drawContents(guiGraphics, mouseX, mouseY, this.scrollOffsetY);
    }

    @Override
    public final boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
        if (!userInput.is(Internal.getKeyMappings().getLeftClick())) {
            return false;
        }
        if (!userInput.isSimulate()) {
            this.dragOriginY = -1.0;
        }
        if (this.scrollArea.contains(mouseX, mouseY)) {
            if (this.getHiddenAmount() == 0) {
                return false;
            }
            if (userInput.isSimulate()) {
                ImmutableRect2i scrollMarkerArea = this.calculateScrollbarMarkerArea();
                if (!scrollMarkerArea.contains(mouseX, mouseY)) {
                    this.moveScrollbarCenterTo(scrollMarkerArea, mouseY);
                    scrollMarkerArea = this.calculateScrollbarMarkerArea();
                }
                this.dragOriginY = mouseY - (double)scrollMarkerArea.y();
            }
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        if (this.getHiddenAmount() > 0) {
            this.scrollOffsetY -= this.calculateScrollAmount(scrollDeltaY);
            this.scrollOffsetY = Mth.clamp((float)this.scrollOffsetY, (float)0.0f, (float)1.0f);
        } else {
            this.scrollOffsetY = 0.0f;
        }
        return true;
    }

    @Override
    public final boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        if (this.dragOriginY < 0.0 || mouseKey.getValue() != 0) {
            return false;
        }
        ImmutableRect2i scrollbarMarkerArea = this.calculateScrollbarMarkerArea();
        double topY = mouseY - this.dragOriginY;
        this.moveScrollbarTo(scrollbarMarkerArea, topY);
        return true;
    }

    private void moveScrollbarCenterTo(ImmutableRect2i scrollMarkerArea, double centerY) {
        double topY = centerY - (double)scrollMarkerArea.height() / 2.0;
        this.moveScrollbarTo(scrollMarkerArea, topY);
    }

    private void moveScrollbarTo(ImmutableRect2i scrollMarkerArea, double topY) {
        int minY = this.scrollArea.y();
        int maxY = this.scrollArea.y() + this.scrollArea.height() - scrollMarkerArea.height();
        double relativeY = topY - (double)minY;
        int totalSpace = maxY - minY;
        this.scrollOffsetY = (float)(relativeY / (double)totalSpace);
        this.scrollOffsetY = Mth.clamp((float)this.scrollOffsetY, (float)0.0f, (float)1.0f);
    }

    protected abstract float calculateScrollAmount(double var1);
}

