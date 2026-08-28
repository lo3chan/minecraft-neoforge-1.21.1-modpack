/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import java.util.function.Consumer;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ScrollBarWidget
extends BaseWidget {
    public static final int SCROLL_STEP = 6;
    private final ScrollDirection mode;
    private final int contentLength;
    private final int visibleAreaLength;
    private final int maxContentOffset;
    private final Consumer<Integer> offsetChangeListener;
    private final LayoutBounds extraScrollArea;
    private int offset = 0;
    private boolean isDragging;
    private LayoutBounds scrollThumb = null;
    private int scrollThumbClickOffset;

    public ScrollBarWidget(LayoutBounds trackArea, ScrollDirection scrollDirection, int contentLength, int visibleAreaLength, Consumer<Integer> offsetChangeListener) {
        this(trackArea, scrollDirection, contentLength, visibleAreaLength, offsetChangeListener, null);
    }

    public ScrollBarWidget(LayoutBounds scrollBarArea, ScrollDirection scrollDirection, int contentLength, int visibleAreaLength, Consumer<Integer> offsetChangeListener, LayoutBounds extraScrollArea) {
        super(scrollBarArea);
        this.mode = scrollDirection;
        this.contentLength = contentLength;
        this.visibleAreaLength = visibleAreaLength;
        this.offsetChangeListener = offsetChangeListener;
        this.maxContentOffset = this.contentLength - this.visibleAreaLength;
        this.extraScrollArea = extraScrollArea;
        this.updateThumbLocation();
    }

    public void updateThumbLocation() {
        int trackSize = this.mode == ScrollDirection.VERTICAL ? this.getHeight() : this.getWidth() - 6;
        int scrollThumbLength = this.visibleAreaLength * trackSize / this.contentLength;
        int maximumScrollThumbOffset = this.visibleAreaLength - scrollThumbLength;
        int scrollThumbOffset = this.offset * maximumScrollThumbOffset / this.maxContentOffset;
        this.scrollThumb = new LayoutBounds(this.getX() + 2 + (this.mode == ScrollDirection.HORIZONTAL ? scrollThumbOffset : 0), this.getY() + 2 + (this.mode == ScrollDirection.VERTICAL ? scrollThumbOffset : 0), (this.mode == ScrollDirection.VERTICAL ? this.getWidth() : scrollThumbLength) - 4, (this.mode == ScrollDirection.VERTICAL ? scrollThumbLength : this.getHeight()) - 4);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -5592406);
        this.drawRect(guiGraphics, this.scrollThumb.x(), this.scrollThumb.y(), this.scrollThumb.getLimitX(), this.scrollThumb.getLimitY(), -5592406);
        if (this.shouldRenderFocusBorder()) {
            this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -1);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if (this.scrollThumb.contains(mouseX, mouseY)) {
                this.scrollThumbClickOffset = (int)(this.mode == ScrollDirection.VERTICAL ? mouseY - (double)this.scrollThumb.getCenterY() : mouseX - (double)this.scrollThumb.getCenterX());
                this.isDragging = true;
            } else {
                int thumbLength = this.mode == ScrollDirection.VERTICAL ? this.scrollThumb.height() : this.scrollThumb.width();
                int trackLength = this.mode == ScrollDirection.VERTICAL ? this.getHeight() : this.getWidth();
                int value = (int)(((this.mode == ScrollDirection.VERTICAL ? mouseY - (double)this.getY() : mouseX - (double)this.getX()) - (double)thumbLength / 2.0) * (double)this.maxContentOffset / (double)(trackLength - thumbLength));
                this.setOffset(value);
                this.isDragging = false;
            }
            return true;
        }
        this.isDragging = false;
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isDragging) {
            int thumbLength = this.mode == ScrollDirection.VERTICAL ? this.scrollThumb.height() : this.scrollThumb.width();
            int trackLength = this.mode == ScrollDirection.VERTICAL ? this.getHeight() : this.getWidth();
            int value = (int)(((this.mode == ScrollDirection.VERTICAL ? mouseY : mouseX) - (double)this.scrollThumbClickOffset - (double)(this.mode == ScrollDirection.VERTICAL ? this.getY() : this.getX()) - (double)thumbLength / 2.0) * (double)this.maxContentOffset / (double)(trackLength - thumbLength));
            this.setOffset(value);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.isMouseOver(mouseX, mouseY) || this.extraScrollArea != null && this.extraScrollArea.contains(mouseX, mouseY)) {
            this.setOffset(this.offset - (int)verticalAmount * 6);
            return true;
        }
        return false;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int value) {
        this.offset = Mth.clamp((int)value, (int)0, (int)this.maxContentOffset);
        this.updateThumbLocation();
        this.offsetChangeListener.accept(this.offset);
    }

    @Override
    @NotNull
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int newOffset;
        if (!this.isFocused()) {
            return false;
        }
        switch (keyCode) {
            case 265: {
                int n = this.getOffset() - 6;
                break;
            }
            case 264: {
                int n = this.getOffset() + 6;
                break;
            }
            case 263: {
                int n;
                if (this.mode == ScrollDirection.HORIZONTAL) {
                    n = this.getOffset() - 6;
                    break;
                }
                n = this.getOffset();
                break;
            }
            case 262: {
                int n;
                if (this.mode == ScrollDirection.HORIZONTAL) {
                    n = this.getOffset() + 6;
                    break;
                }
                n = this.getOffset();
                break;
            }
            default: {
                int n = newOffset = this.getOffset();
            }
        }
        if (newOffset != this.getOffset()) {
            this.setOffset(newOffset);
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return this.maxContentOffset > 0;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        MutableComponent name = Component.translatable((String)(this.mode == ScrollDirection.VERTICAL ? "rso.narration.scrollbar.vertical" : "rso.narration.scrollbar.horizontal"));
        int percentage = this.maxContentOffset <= 0 ? 0 : Math.round((float)this.offset * 100.0f / (float)this.maxContentOffset);
        builder.add(NarratedElementType.TITLE, (Component)CommonComponents.optionNameValue((Component)name, (Component)Component.literal((String)(percentage + "%"))));
        if (this.isFocused()) {
            builder.add(NarratedElementType.USAGE, (Component)Component.translatable((String)(this.mode == ScrollDirection.VERTICAL ? "rso.narration.scrollbar.usage.vertical" : "rso.narration.scrollbar.usage.horizontal")));
        }
    }

    public static enum ScrollDirection {
        HORIZONTAL,
        VERTICAL;

    }
}

