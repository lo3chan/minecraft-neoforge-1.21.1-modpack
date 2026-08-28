/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.ContainerObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.FileDialogUtil;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.IrisContainerObjectSelectionList;
import net.irisshaders.iris.gui.element.IrisElementRow;
import net.irisshaders.iris.gui.element.widget.AbstractElementWidget;
import net.irisshaders.iris.gui.element.widget.OptionMenuConstructor;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShaderPackOptionList
extends IrisContainerObjectSelectionList<BaseEntry> {
    private static final ResourceLocation MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace((String)"textures/gui/menu_background.png");
    private final List<AbstractElementWidget<?>> elementWidgets = new ArrayList();
    private final ShaderPackScreen screen;
    private final NavigationController navigation;
    private OptionMenuContainer container;

    public ShaderPackOptionList(ShaderPackScreen screen, NavigationController navigation, ShaderPack pack, Minecraft client, int width, int height, int top, int bottom, int left, int right) {
        super(client, width, bottom, top, bottom, left, right, 24);
        this.navigation = navigation;
        this.screen = screen;
        this.applyShaderPack(pack);
    }

    public void applyShaderPack(ShaderPack pack) {
        this.container = pack.getMenuContainer();
    }

    public void rebuild() {
        this.clearEntries();
        this.setScrollAmount(0.0);
        OptionMenuConstructor.constructAndApplyToScreen(this.container, this.screen, this, this.navigation);
    }

    public void refresh() {
        this.elementWidgets.forEach(widget -> widget.init(this.screen, this.navigation));
    }

    public int getRowWidth() {
        return Math.min(400, this.width - 12);
    }

    protected void renderListBackground(GuiGraphics pAbstractSelectionList0) {
        if (this.screen.listTransition.getAsFloat() < 0.02f) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.screen.listTransition.getAsFloat());
        pAbstractSelectionList0.blit(MENU_LIST_BACKGROUND, this.getX(), this.getY() + 3, (float)this.getRight(), (float)(this.getBottom() + (int)this.getScrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    protected void renderListSeparators(GuiGraphics pAbstractSelectionList0) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.screen.listTransition.getAsFloat());
        pAbstractSelectionList0.blit(CreateWorldScreen.HEADER_SEPARATOR, this.getX(), this.getY() + 2, 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
        pAbstractSelectionList0.blit(CreateWorldScreen.FOOTER_SEPARATOR, this.getX(), this.getBottom(), 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableBlend();
    }

    protected boolean isValidMouseClick(int i) {
        return i == 0 || i == 1;
    }

    public void addHeader(Component text, boolean backButton) {
        this.addEntry((AbstractSelectionList.Entry)new HeaderEntry(this, this.screen, this.navigation, text, backButton));
    }

    public void addWidgets(int columns, List<AbstractElementWidget<?>> elements) {
        this.elementWidgets.addAll(elements);
        ArrayList<AbstractElementWidget<Object>> row = new ArrayList();
        for (AbstractElementWidget<?> element : elements) {
            row.add(element);
            if (row.size() < columns) continue;
            this.addEntry((AbstractSelectionList.Entry)new ElementRowEntry(this.screen, this.navigation, row));
            row = new ArrayList();
        }
        if (!row.isEmpty()) {
            while (row.size() < columns) {
                row.add(AbstractElementWidget.EMPTY);
            }
            this.addEntry((AbstractSelectionList.Entry)new ElementRowEntry(this.screen, this.navigation, row));
        }
    }

    public NavigationController getNavigation() {
        return this.navigation;
    }

    public class HeaderEntry
    extends BaseEntry {
        public static final Component BACK_BUTTON_TEXT = Component.literal((String)"< ").append((Component)Component.translatable((String)"options.iris.back").withStyle(ChatFormatting.ITALIC));
        public static final MutableComponent RESET_BUTTON_TEXT_INACTIVE = Component.translatable((String)"options.iris.reset").withStyle(ChatFormatting.GRAY);
        public static final MutableComponent RESET_BUTTON_TEXT_ACTIVE = Component.translatable((String)"options.iris.reset").withStyle(ChatFormatting.YELLOW);
        public static final MutableComponent RESET_HOLD_SHIFT_TOOLTIP = Component.translatable((String)"options.iris.reset.tooltip.holdShift").withStyle(ChatFormatting.GOLD);
        public static final MutableComponent RESET_TOOLTIP = Component.translatable((String)"options.iris.reset.tooltip").withStyle(ChatFormatting.RED);
        public static final MutableComponent IMPORT_TOOLTIP = Component.translatable((String)"options.iris.importSettings.tooltip").withStyle(style -> style.withColor(TextColor.fromRgb((int)5089023)));
        public static final MutableComponent EXPORT_TOOLTIP = Component.translatable((String)"options.iris.exportSettings.tooltip").withStyle(style -> style.withColor(TextColor.fromRgb((int)16547133)));
        private static final int MIN_SIDE_BUTTON_WIDTH = 42;
        private static final int BUTTON_HEIGHT = 16;
        private final ShaderPackScreen screen;
        @Nullable
        private final IrisElementRow backButton;
        private final IrisElementRow utilityButtons = new IrisElementRow();
        private final IrisElementRow.TextButtonElement resetButton;
        private final IrisElementRow.IconButtonElement importButton;
        private final IrisElementRow.IconButtonElement exportButton;
        private final Component text;

        public HeaderEntry(ShaderPackOptionList this$0, ShaderPackScreen screen, NavigationController navigation, Component text, boolean hasBackButton) {
            super(navigation);
            this.backButton = hasBackButton ? new IrisElementRow().add(new IrisElementRow.TextButtonElement(BACK_BUTTON_TEXT, this::backButtonClicked), Math.max(42, Minecraft.getInstance().font.width((FormattedText)BACK_BUTTON_TEXT) + 8)) : null;
            this.resetButton = new IrisElementRow.TextButtonElement((Component)RESET_BUTTON_TEXT_INACTIVE, this::resetButtonClicked);
            this.importButton = new IrisElementRow.IconButtonElement(GuiUtil.Icon.IMPORT, GuiUtil.Icon.IMPORT_COLORED, this::importSettingsButtonClicked);
            this.exportButton = new IrisElementRow.IconButtonElement(GuiUtil.Icon.EXPORT, GuiUtil.Icon.EXPORT_COLORED, this::exportSettingsButtonClicked);
            this.utilityButtons.add(this.importButton, 15).add(this.exportButton, 15).add(this.resetButton, Math.max(42, Minecraft.getInstance().font.width((FormattedText)RESET_BUTTON_TEXT_INACTIVE) + 8));
            this.screen = screen;
            this.text = text;
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean shiftDown;
            guiGraphics.fill(x - 3, y + entryHeight - 2, x + entryWidth, y + entryHeight - 1, 0x66BEBEBE);
            Font font = Minecraft.getInstance().font;
            AbstractWidget.renderScrollingString((GuiGraphics)guiGraphics, (Font)font, (Component)this.text, (int)(x + (int)((double)entryWidth * 0.5)), (int)(x + 5), (int)(y + 5), (int)(x + entryWidth - 10 - this.utilityButtons.getWidth()), (int)(y + 15), (int)0xFFFFFF);
            GuiUtil.bindIrisWidgetsTexture();
            if (this.backButton != null) {
                this.backButton.render(guiGraphics, x, y, 16, mouseX, mouseY, tickDelta, hovered);
            }
            this.resetButton.disabled = !(shiftDown = Screen.hasShiftDown()) && !this.resetButton.isFocused();
            this.resetButton.text = !this.resetButton.disabled ? RESET_BUTTON_TEXT_ACTIVE : RESET_BUTTON_TEXT_INACTIVE;
            this.utilityButtons.renderRightAligned(guiGraphics, x + entryWidth - 3, y, 16, mouseX, mouseY, tickDelta, hovered);
            if (this.resetButton.isHovered() || this.resetButton.isFocused()) {
                MutableComponent tooltip = !this.resetButton.disabled ? RESET_TOOLTIP : RESET_HOLD_SHIFT_TOOLTIP;
                this.queueBottomRightAnchoredTooltip(guiGraphics, this.resetButton.getRectangle().getBoundInDirection(ScreenDirection.RIGHT), this.resetButton.getRectangle().position().y(), font, (Component)tooltip);
            }
            if (this.importButton.isHovered() || this.importButton.isFocused()) {
                this.queueBottomRightAnchoredTooltip(guiGraphics, this.importButton.getRectangle().getBoundInDirection(ScreenDirection.RIGHT), this.importButton.getRectangle().position().y(), font, (Component)IMPORT_TOOLTIP);
            }
            if (this.exportButton.isHovered() || this.exportButton.isFocused()) {
                this.queueBottomRightAnchoredTooltip(guiGraphics, this.exportButton.getRectangle().getBoundInDirection(ScreenDirection.RIGHT), this.exportButton.getRectangle().position().y(), font, (Component)EXPORT_TOOLTIP);
            }
        }

        private void queueBottomRightAnchoredTooltip(GuiGraphics guiGraphics, int x, int y, Font font, Component text) {
            ShaderPackScreen.TOP_LAYER_RENDER_QUEUE.add(() -> GuiUtil.drawTextPanel(font, guiGraphics, text, x - (font.width((FormattedText)text) + 10), y - 16));
        }

        public List<? extends GuiEventListener> children() {
            if (this.backButton != null) {
                return ImmutableList.copyOf((Iterable)Iterables.concat(this.utilityButtons.children(), this.backButton.children()));
            }
            return ImmutableList.copyOf(this.utilityButtons.children());
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean backButtonResult = this.backButton != null && this.backButton.mouseClicked(mouseX, mouseY, button);
            boolean utilButtonResult = this.utilityButtons.mouseClicked(mouseX, mouseY, button);
            return backButtonResult || utilButtonResult;
        }

        public boolean keyPressed(int keycode, int scancode, int modifiers) {
            if (this.backButton != null && this.backButton.keyPressed(keycode, scancode, modifiers)) {
                return true;
            }
            return this.utilityButtons.keyPressed(keycode, scancode, modifiers);
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }

        private boolean backButtonClicked(IrisElementRow.TextButtonElement button) {
            this.navigation.back();
            GuiUtil.playButtonClickSound();
            return true;
        }

        private boolean resetButtonClicked(IrisElementRow.TextButtonElement button) {
            if (Screen.hasShiftDown()) {
                Iris.resetShaderPackOptionsOnNextReload();
                this.screen.applyChanges();
                GuiUtil.playButtonClickSound();
                return true;
            }
            return false;
        }

        private boolean importSettingsButtonClicked(IrisElementRow.IconButtonElement button) {
            GuiUtil.playButtonClickSound();
            if (Iris.getCurrentPack().isEmpty()) {
                return false;
            }
            if (Minecraft.getInstance().getWindow().isFullscreen()) {
                this.screen.displayNotification((Component)Component.translatable((String)"options.iris.mustDisableFullscreen").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                return false;
            }
            ShaderPackScreen originalScreen = this.screen;
            FileDialogUtil.fileSelectDialog(FileDialogUtil.DialogType.OPEN, "Import Shader Settings from File", Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt"), "Shader Pack Settings (.txt)", "*.txt").whenComplete((path, err) -> {
                if (err != null) {
                    Iris.logger.error("Error selecting shader settings from file", (Throwable)err);
                    return;
                }
                if (Minecraft.getInstance().screen == originalScreen) {
                    path.ifPresent(originalScreen::importPackOptions);
                }
            });
            return true;
        }

        private boolean exportSettingsButtonClicked(IrisElementRow.IconButtonElement button) {
            GuiUtil.playButtonClickSound();
            if (Iris.getCurrentPack().isEmpty()) {
                return false;
            }
            if (Minecraft.getInstance().getWindow().isFullscreen()) {
                this.screen.displayNotification((Component)Component.translatable((String)"options.iris.mustDisableFullscreen").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                return false;
            }
            FileDialogUtil.fileSelectDialog(FileDialogUtil.DialogType.SAVE, "Export Shader Settings to File", Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt"), "Shader Pack Settings (.txt)", "*.txt").whenComplete((path, err) -> {
                if (err != null) {
                    Iris.logger.error("Error selecting file to export shader settings", (Throwable)err);
                    return;
                }
                path.ifPresent(p -> {
                    Properties toSave = new Properties();
                    Path sourceTxtPath = Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt");
                    if (Files.exists(sourceTxtPath, new LinkOption[0])) {
                        try (InputStream in2 = Files.newInputStream(sourceTxtPath, new OpenOption[0]);){
                            toSave.load(in2);
                        }
                        catch (IOException in2) {
                            // empty catch block
                        }
                    }
                    try (OutputStream out = Files.newOutputStream(p, new OpenOption[0]);){
                        toSave.store(out, null);
                    }
                    catch (IOException e) {
                        Iris.logger.error("Error saving properties to \"" + String.valueOf(p) + "\"", e);
                    }
                });
            });
            return true;
        }
    }

    public static class ElementRowEntry
    extends BaseEntry {
        private final List<AbstractElementWidget<?>> widgets;
        private final ShaderPackScreen screen;
        private int cachedWidth;
        private int cachedPosX;

        public ElementRowEntry(ShaderPackScreen screen, NavigationController navigation, List<AbstractElementWidget<?>> widgets) {
            super(navigation);
            this.screen = screen;
            this.widgets = widgets;
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.cachedWidth = entryWidth;
            this.cachedPosX = x;
            int totalWidthWithoutMargins = entryWidth - 2 * (this.widgets.size() - 1);
            float singleWidgetWidth = (float)(totalWidthWithoutMargins -= 3) / (float)this.widgets.size();
            for (int i = 0; i < this.widgets.size(); ++i) {
                AbstractElementWidget<?> widget = this.widgets.get(i);
                boolean widgetHovered = hovered && this.getHoveredWidget(mouseX) == i || this.getFocused() == widget;
                widget.bounds = new ScreenRectangle(x + (int)((singleWidgetWidth + 2.0f) * (float)i), y, (int)singleWidgetWidth, entryHeight + 2);
                widget.render(guiGraphics, mouseX, mouseY, tickDelta, widgetHovered);
                this.screen.setElementHoveredStatus(widget, widgetHovered);
            }
        }

        public int getHoveredWidget(int mouseX) {
            float positionAcrossWidget = (float)Mth.clamp((int)(mouseX - this.cachedPosX), (int)0, (int)this.cachedWidth) / (float)this.cachedWidth;
            return Mth.clamp((int)((int)Math.floor((float)this.widgets.size() * positionAcrossWidget)), (int)0, (int)(this.widgets.size() - 1));
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.widgets.get(this.getHoveredWidget((int)mouseX)).mouseClicked(mouseX, mouseY, button);
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.widgets.get(this.getHoveredWidget((int)mouseX)).mouseReleased(mouseX, mouseY, button);
        }

        @NotNull
        public List<? extends GuiEventListener> children() {
            return ImmutableList.copyOf(this.widgets);
        }

        @NotNull
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.copyOf(this.widgets);
        }
    }

    public static abstract class BaseEntry
    extends ContainerObjectSelectionList.Entry<BaseEntry> {
        protected final NavigationController navigation;

        protected BaseEntry(NavigationController navigation) {
            this.navigation = navigation;
        }
    }
}

