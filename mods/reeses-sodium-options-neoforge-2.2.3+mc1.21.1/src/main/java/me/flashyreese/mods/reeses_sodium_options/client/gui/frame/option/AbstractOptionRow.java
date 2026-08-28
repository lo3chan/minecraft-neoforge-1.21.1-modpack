/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.config.structure.StatefulOption
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ComponentPath
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.ContainerEventHandler
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.navigation.FocusNavigationEvent
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuideProvider;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionActionButtonController;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionUndoAction;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionUiState;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

abstract class AbstractOptionRow
extends BaseWidget
implements ContainerEventHandler,
OptionRow,
ControlGuideProvider {
    protected static final int CONTROL_RIGHT_PADDING = 6;
    private static final int SEARCH_RESULT_MARKER = 0x66FFFFFF;
    private static final int SEARCH_RESULT_MARKER_WIDTH = 2;
    protected final OptionActionButtonController actionButtons;
    protected final GuiTheme theme;
    private final OptionStateStore optionStateStore;
    private final Option option;
    @Nullable
    private final StatefulOption<?> statefulOption;
    private boolean rowFocused;
    private boolean dragging;

    AbstractOptionRow(LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, Option option) {
        super(dim);
        StatefulOption statefulOption;
        this.theme = theme;
        this.optionStateStore = optionStateStore;
        this.option = option;
        this.statefulOption = option instanceof StatefulOption ? (statefulOption = (StatefulOption)option) : null;
        this.actionButtons = new OptionActionButtonController(this::visibleBounds, () -> this.statefulOption, () -> this.playClickSound());
    }

    @Override
    public Option getOption() {
        return this.option;
    }

    private int getContentWidth() {
        return this.controlContentWidth() + this.actionButtons.actionButtonWidth();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.statefulOption != null) {
            OptionUndoAction.normalizeEquivalentChange(this.statefulOption);
        }
        if (!AbstractOptionRow.isLeftMouseButtonDown()) {
            this.releaseMouseHold();
            this.actionButtons.releaseLayoutHold();
        }
        this.prepareRender(mouseX, mouseY, delta);
        this.renderRow(guiGraphics, mouseX, mouseY);
        this.renderControl(guiGraphics, mouseX, mouseY, delta);
        this.actionButtons.render(guiGraphics, mouseX, mouseY);
    }

    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.actionButtons.mouseClicked(mouseX, mouseY, button) || this.controlMouseClicked(mouseX, mouseY, button);
    }

    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.actionButtons.keyPressedFocusedChild(keyCode, scanCode, modifiers) || this.controlKeyPressed(keyCode, scanCode, modifiers);
    }

    protected boolean controlMouseClicked(double mouseX, double mouseY, int button) {
        return this.tryActivateControl(mouseX, mouseY, button);
    }

    protected boolean controlKeyPressed(int keyCode, int scanCode, int modifiers) {
        return this.rowFocused && AbstractOptionRow.isSelectionKey(keyCode) && this.activateControl();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.actionButtons.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isFocused() {
        return this.rowFocused || this.actionButtons.getFocused() != null;
    }

    @Override
    public void setFocused(boolean focused) {
        this.rowFocused = focused;
        if (!focused) {
            this.actionButtons.clearFocus();
            this.onControlFocusLost();
        }
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
        OptionActionButtonController.FocusPathResult result = this.actionButtons.nextFocusPath(this, this, this.rowFocused, navigation);
        if (result.handled()) {
            return result.path();
        }
        if (!this.getOption().isEnabled() || this.isFocused()) {
            return null;
        }
        return ComponentPath.leaf((GuiEventListener)this);
    }

    @Nullable
    public ComponentPath getCurrentFocusPath() {
        return this.actionButtons.currentFocusPath(this, this, this.rowFocused);
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.rowFocused) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        return this.hovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public List<NarratableEntry> collectNarratables() {
        ArrayList<NarratableEntry> narratables = new ArrayList<NarratableEntry>();
        narratables.add(this);
        for (GuiEventListener child : this.actionButtons.children()) {
            if (!(child instanceof NarratableEntry)) continue;
            NarratableEntry narratable = (NarratableEntry)child;
            narratables.add(narratable);
        }
        return narratables;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        Component value = this.narrationValue();
        Component title = value == null ? this.getOption().getName() : CommonComponents.optionNameValue((Component)this.getOption().getName(), (Component)value);
        builder.add(NarratedElementType.TITLE, title);
        this.updateControlNarration(builder);
        this.updateTooltipNarration(builder);
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    public GuiEventListener getFocused() {
        return this.actionButtons.getFocused();
    }

    public void setFocused(@Nullable GuiEventListener focused) {
        this.actionButtons.setFocused(focused);
        if (focused != null) {
            this.rowFocused = false;
            this.onControlFocusLost();
        }
    }

    @NotNull
    public List<? extends GuiEventListener> children() {
        return this.actionButtons.children();
    }

    @Override
    public void releaseActionButtonLayoutHold() {
        this.actionButtons.releaseLayoutHold();
    }

    @Override
    public boolean handleBackNavigation() {
        return false;
    }

    @Override
    public boolean undoFocusedActionButton() {
        return this.actionButtons.undoFocusedButton();
    }

    @Override
    public void clearActionButtonFocus() {
        this.actionButtons.clearFocus();
    }

    protected int controlLimitX() {
        return this.getLimitX() - this.actionButtons.actionButtonWidth();
    }

    protected boolean isRowFocused() {
        return this.rowFocused;
    }

    @Override
    public List<ControlGuide> controlGuides() {
        return List.of();
    }

    protected boolean canShowControlGuide() {
        return this.isRowFocused() && this.getOption().isEnabled() && this.optionShowsControl();
    }

    protected boolean isMouseOverRow(double mouseX, double mouseY) {
        return mouseX >= (double)this.getX() && mouseX < (double)this.controlLimitX() && mouseY >= (double)this.getY() && mouseY < (double)this.getLimitY();
    }

    protected int centeredTextY() {
        double d = this.getY();
        int n = this.getHeight();
        Objects.requireNonNull(this.font);
        return (int)(d + Math.ceil((double)(n - 9) / 2.0));
    }

    protected int rightAlignedControlX(int width) {
        return this.controlLimitX() - 6 - width;
    }

    protected void requestPointerCursorIfHovered(GuiGraphics guiGraphics) {
    }

    protected void prepareRender(int mouseX, int mouseY, float delta) {
    }

    protected void releaseMouseHold() {
    }

    protected void onControlFocusLost() {
    }

    protected MutableComponent formatDisabledControlValue(Component value) {
        return value.copy().withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(Boolean.valueOf(true)));
    }

    @Nullable
    protected Component narrationValue() {
        return null;
    }

    protected void updateControlNarration(NarrationElementOutput builder) {
        if (this.getOption().isEnabled() && this.optionShowsControl()) {
            this.addButtonUsageNarration(builder);
        } else if (!this.getOption().isEnabled()) {
            builder.add(NarratedElementType.HINT, (Component)Component.translatable((String)"rso.narration.option_unavailable"));
        }
    }

    protected boolean optionShowsControl() {
        return this.statefulOption == null || this.statefulOption.showControl();
    }

    private void updateTooltipNarration(NarrationElementOutput builder) {
        Component tooltip = this.getOption().getTooltip();
        if (tooltip != null && !tooltip.getString().isBlank()) {
            builder.add(NarratedElementType.HINT, tooltip);
        }
    }

    protected abstract int controlContentWidth();

    protected abstract void renderControl(GuiGraphics var1, int var2, int var3, float var4);

    protected abstract boolean activateControl();

    private void renderRow(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Option option = this.getOption();
        String label = this.truncateLabel(option.getName().getString());
        String formattedLabel = this.formatLabel(option, label);
        int rowLimitX = this.controlLimitX();
        OptionUiState optionUiState = this.optionUiState();
        boolean selectedSearchResult = this.isSelectedSearchResult(optionUiState);
        this.hovered = this.isMouseOverRow(mouseX, mouseY);
        this.drawRect(guiGraphics, this.getX(), this.getY(), rowLimitX, this.getLimitY(), this.hovered || selectedSearchResult ? -536870912 : 0x40000000);
        this.renderSearchResultMarker(guiGraphics, optionUiState);
        this.drawString(guiGraphics, formattedLabel, this.getX() + 6, this.centeredTextY(), -1);
        int borderColor = this.rowBorderColor(optionUiState);
        if (borderColor != 0) {
            this.drawBorder(guiGraphics, this.getX(), this.getY(), rowLimitX, this.getLimitY(), borderColor);
        }
    }

    private void renderSearchResultMarker(GuiGraphics guiGraphics, @Nullable OptionUiState optionUiState) {
        if (optionUiState == null || !optionUiState.isHighlighted() || optionUiState.isSelected()) {
            return;
        }
        this.drawRect(guiGraphics, this.getX(), this.getY(), this.getX() + 2, this.getLimitY(), 0x66FFFFFF);
    }

    private int rowBorderColor(@Nullable OptionUiState optionUiState) {
        if (this.shouldRenderFocusBorder(this.isRowFocused())) {
            return -1;
        }
        return this.isSelectedSearchResult(optionUiState) ? -1 : 0;
    }

    private boolean isSelectedSearchResult(@Nullable OptionUiState optionUiState) {
        return optionUiState != null && optionUiState.isHighlighted() && optionUiState.isSelected();
    }

    @Nullable
    private OptionUiState optionUiState() {
        Option option = this.option;
        if (!(option instanceof OptionExtended)) {
            return null;
        }
        OptionExtended optionExtended = (OptionExtended)option;
        return this.optionStateStore.optionUiState(optionExtended.rso$getId());
    }

    private String truncateLabel(String label) {
        return this.truncateTextToFit(label, this.getWidth() - this.getContentWidth() - 20);
    }

    private String formatLabel(Option option, String label) {
        String formattedLabel = option.isEnabled() ? (option.hasChanged() ? String.valueOf(ChatFormatting.ITALIC) + label : String.valueOf(ChatFormatting.WHITE) + label) : ChatFormatting.GRAY.toString() + String.valueOf(ChatFormatting.STRIKETHROUGH) + label;
        return formattedLabel;
    }

    private boolean tryActivateControl(double mouseX, double mouseY, int button) {
        return button == 0 && this.isMouseOverRow(mouseX, mouseY) && this.activateControl();
    }

    private LayoutBounds visibleBounds() {
        return this.getDimensions();
    }

    private static boolean isLeftMouseButtonDown() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        return GLFW.glfwGetMouseButton((long)window, (int)0) == 1;
    }
}

