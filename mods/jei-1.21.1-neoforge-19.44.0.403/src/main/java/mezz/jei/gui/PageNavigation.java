/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.RenderType
 */
package mezz.jei.gui;

import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.common.Internal;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.input.IPaged;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class PageNavigation {
    private final IPaged paged;
    private final IconButton nextButton;
    private final IconButton backButton;
    private final boolean hideOnSinglePage;
    private String pageNumDisplayString = "1/1";
    private ImmutableRect2i area = ImmutableRect2i.EMPTY;

    public PageNavigation(final IPaged paged, boolean hideOnSinglePage) {
        this.paged = paged;
        this.nextButton = new IconButton(new IIconButtonController(){

            @Override
            public boolean onPress(IJeiUserInput b) {
                return b.isSimulate() || paged.nextPage();
            }

            @Override
            public void initState(IButtonState state) {
                state.setIcon(Internal.getTextures().getArrowNext());
                this.updateState(state);
            }

            @Override
            public void updateState(IButtonState state) {
                state.setActive(paged.getPageCount() > 1);
            }
        });
        this.backButton = new IconButton(new IIconButtonController(){

            @Override
            public boolean onPress(IJeiUserInput b) {
                return b.isSimulate() || paged.previousPage();
            }

            @Override
            public void initState(IButtonState state) {
                state.setIcon(Internal.getTextures().getArrowPrevious());
                this.updateState(state);
            }

            @Override
            public void updateState(IButtonState state) {
                state.setActive(paged.getPageCount() > 1);
            }
        });
        this.hideOnSinglePage = hideOnSinglePage;
    }

    private boolean isVisible() {
        if (this.area.isEmpty()) {
            return false;
        }
        return !this.hideOnSinglePage || this.paged.hasNext() || this.paged.hasPrevious();
    }

    public void updateBounds(ImmutableRect2i area) {
        this.area = area;
        int buttonSize = Math.min(area.getHeight(), area.width() / 2);
        ImmutableRect2i backArea = area.keepLeft(buttonSize);
        this.backButton.updateBounds(backArea);
        ImmutableRect2i nextArea = area.keepRight(buttonSize);
        this.nextButton.updateBounds(nextArea);
    }

    public void updatePageNumber() {
        int pageNum = this.paged.getPageNumber();
        int pageCount = this.paged.getPageCount();
        this.pageNumDisplayString = String.format("%d/%d", pageNum + 1, pageCount);
        this.nextButton.tick();
        this.backButton.tick();
    }

    public void draw(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            guiGraphics.fill(RenderType.gui(), this.backButton.getX() + this.backButton.getWidth(), this.backButton.getY(), this.nextButton.getX(), this.nextButton.getY() + this.nextButton.getHeight(), 0x30000000);
            int availableWidth = this.area.width() - this.backButton.getWidth() - this.nextButton.getWidth();
            Font font = minecraft.font;
            ImmutableRect2i centerArea = MathUtil.centerTextArea(this.area, font, this.pageNumDisplayString);
            if (centerArea.width() <= availableWidth) {
                guiGraphics.drawString(font, this.pageNumDisplayString, centerArea.getX(), centerArea.getY(), -1);
            }
            this.nextButton.draw(guiGraphics, mouseX, mouseY, partialTicks);
            this.backButton.draw(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    public ImmutableRect2i getNextButtonArea() {
        return this.nextButton.getArea();
    }

    public ImmutableRect2i getBackButtonArea() {
        return this.backButton.getArea();
    }

    public IUserInputHandler createInputHandler() {
        return new CombinedInputHandler("PageNavigation", this.nextButton.createInputHandler(), this.backButton.createInputHandler());
    }
}

