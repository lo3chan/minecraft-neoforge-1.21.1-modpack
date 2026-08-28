/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.input;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.TextHistory;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.focus.ScreenFocusHandler;
import mezz.jei.gui.input.handlers.TextFieldInputHandler;
import mezz.jei.gui.overlay.ISearchField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class GuiTextFieldFilter
extends EditBox
implements ISearchField {
    private static final int maxSearchLength = 128;
    private static final TextHistory history = new TextHistory();
    private final BooleanSupplier filterEmpty;
    private ImmutableRect2i area;
    private final ScalableDrawable background;
    private ImmutableRect2i backgroundBounds;
    @Nullable
    private ScreenFocusHandler screenUnfocusHandler;

    public GuiTextFieldFilter(BooleanSupplier filterEmpty) {
        super(Minecraft.getInstance().font, 0, 0, 0, 0, (Component)Component.translatable((String)"gui.jei.search"));
        this.filterEmpty = filterEmpty;
        this.setMaxLength(128);
        this.area = ImmutableRect2i.EMPTY;
        Textures textures = Internal.getTextures();
        this.background = textures.getSearchBackground();
        this.backgroundBounds = ImmutableRect2i.EMPTY;
        this.setBordered(false);
    }

    @Override
    public void updateBounds(ImmutableRect2i area) {
        this.backgroundBounds = area;
        this.setX(area.getX() + 4);
        this.setY(area.getY() + (area.getHeight() - 8) / 2);
        this.width = area.getWidth() - 12;
        this.height = area.getHeight();
        this.area = area;
    }

    @Override
    public void setValue(String filterText) {
        if (!filterText.equals(this.getValue())) {
            super.setValue(filterText);
        }
        int color = this.filterEmpty.getAsBoolean() ? -65536 : -1;
        this.setTextColor(color);
    }

    public Optional<String> getHistory(TextHistory.Direction direction) {
        String currentText = this.getValue();
        return history.get(direction, currentText);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.area.contains(mouseX, mouseY);
    }

    public IUserInputHandler createInputHandler() {
        return new TextFieldInputHandler(this);
    }

    @Override
    public void setFocused(boolean keyboardFocus) {
        boolean previousFocus = this.isFocused();
        super.setFocused(keyboardFocus);
        if (previousFocus != keyboardFocus) {
            Minecraft minecraft = Minecraft.getInstance();
            if (keyboardFocus) {
                Screen screen = minecraft.screen;
                if (screen != null) {
                    this.screenUnfocusHandler = ScreenFocusHandler.create(screen);
                    if (this.screenUnfocusHandler != null) {
                        this.screenUnfocusHandler.unFocus();
                    }
                }
            } else if (this.screenUnfocusHandler != null) {
                this.screenUnfocusHandler.focus();
                this.screenUnfocusHandler = null;
            }
            String text = this.getValue();
            history.add(text);
        }
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(guiGraphics);
        this.drawForeground(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void drawBackground(GuiGraphics guiGraphics) {
        if (this.isVisible()) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.background.draw(guiGraphics, this.backgroundBounds);
        }
    }

    public void drawForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
    }
}

