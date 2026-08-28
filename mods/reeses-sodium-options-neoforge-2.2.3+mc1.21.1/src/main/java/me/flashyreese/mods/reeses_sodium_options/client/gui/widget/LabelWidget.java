/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.widget;

import java.util.List;
import java.util.Objects;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuideProvider;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LabelWidget
extends BaseWidget
implements ControlGuideProvider {
    private static final int TEXT_PADDING = 5;
    private static final int CHEVRON_GAP = 4;
    private static final String CHEVRON_COLLAPSED = "\u25b6";
    private static final String CHEVRON_EXPANDED = "\u25bc";
    private final Component text;
    private final int color;
    @Nullable
    private final GuiTheme theme;
    @Nullable
    private final ResourceLocation collapseKey;
    @Nullable
    private final Runnable onToggle;
    private boolean collapsed;

    public LabelWidget(LayoutBounds dim, Component text, int color) {
        this(dim, text, color, null, null, null, false);
    }

    public LabelWidget(LayoutBounds dim, Component text, int color, @Nullable ResourceLocation collapseKey, @Nullable Runnable onToggle, boolean collapsed) {
        this(dim, text, color, null, collapseKey, onToggle, collapsed);
    }

    public LabelWidget(LayoutBounds dim, Component text, int color, @Nullable GuiTheme theme, @Nullable ResourceLocation collapseKey, @Nullable Runnable onToggle, boolean collapsed) {
        super(dim);
        this.text = text;
        this.color = color;
        this.theme = theme;
        this.collapseKey = collapseKey;
        this.onToggle = onToggle;
        this.collapsed = collapsed;
    }

    @Nullable
    public ResourceLocation collapseKey() {
        return this.collapseKey;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    @Override
    public List<ControlGuide> controlGuides() {
        if (this.onToggle == null || !this.isFocused()) {
            return List.of();
        }
        return List.of(ControlGuide.press((Component)Component.translatable((String)(this.collapsed ? "rso.controller.guide.expand" : "rso.controller.guide.collapse"))));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.onToggle == null) {
            int textWidth = this.getStringWidth((FormattedText)this.text);
            int x = this.getCenterX() - textWidth / 2;
            this.drawString(guiGraphics, this.text, x, this.getY(), this.color);
            return;
        }
        this.hovered = this.isMouseOver(mouseX, mouseY);
        if (this.theme != null) {
            this.drawRect(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), this.hovered ? this.theme.bgHighlight : this.theme.bgDefault);
        }
        double d = this.getY();
        int n = this.getHeight();
        Objects.requireNonNull(this.font);
        int textY = (int)(d + Math.ceil((double)(n - 9) / 2.0));
        String chevron = this.collapsed ? CHEVRON_COLLAPSED : CHEVRON_EXPANDED;
        int chevronX = this.getX() + 5;
        this.drawString(guiGraphics, chevron, chevronX, textY, this.color);
        this.drawString(guiGraphics, this.text, chevronX + this.font.width(chevron) + 4, textY, this.color);
        if (this.shouldRenderFocusBorder()) {
            this.drawBorder(guiGraphics, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -1);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.onToggle != null && button == 0 && this.isMouseOver(mouseX, mouseY)) {
            this.onToggle.run();
            this.playClickSound();
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.onToggle != null && this.isFocused() && LabelWidget.isSelectionKey(keyCode)) {
            this.onToggle.run();
            this.playClickSound();
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
        if (this.onToggle == null || this.isFocused()) {
            return null;
        }
        return ComponentPath.leaf((GuiEventListener)this);
    }

    public boolean isActive() {
        return this.onToggle != null;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        if (this.onToggle == null) {
            return;
        }
        MutableComponent state = Component.translatable((String)(this.collapsed ? "rso.narration.collapsed" : "rso.narration.expanded"));
        this.addButtonNarration(builder, (Component)CommonComponents.optionNameValue((Component)this.text, (Component)state));
    }

    @Override
    public boolean isFocused() {
        return this.onToggle != null && this.focused;
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.onToggle != null) {
            this.focused = focused;
        }
    }
}

