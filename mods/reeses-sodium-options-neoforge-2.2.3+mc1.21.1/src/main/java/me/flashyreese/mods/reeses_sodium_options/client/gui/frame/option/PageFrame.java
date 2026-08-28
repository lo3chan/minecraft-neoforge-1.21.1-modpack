/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.ModOptions
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.config.structure.Page
 *  net.caffeinemc.mods.sodium.client.gui.ColorTheme
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.commons.lang3.Validate
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRowFactory;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionTooltipController;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.PageLayout;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultOrder;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiThemes;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.LabelWidget;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.config.structure.Page;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PageFrame
extends AbstractFrame {
    protected final Page page;
    private final OptionStateStore optionStateStore;
    private final OptionRowFactory optionRowFactory;
    private final OptionTooltipController tooltipController;
    private final Map<ResourceLocation, LabelWidget> groupHeaderWidgets = new HashMap<ResourceLocation, LabelWidget>();
    private final Map<ResourceLocation, OptionRow> optionRowWidgets = new HashMap<ResourceLocation, OptionRow>();
    @Nullable
    private Consumer<ResourceLocation> groupToggleRebuildHandler;
    private PageLayout layout;

    public PageFrame(Screen screen, LayoutBounds dim, boolean renderOutline, Page page, ModOptions modOptions, OptionStateStore optionStateStore) {
        super(dim, screen, renderOutline, modOptions);
        this.page = page;
        this.optionStateStore = optionStateStore;
        this.optionRowFactory = new OptionRowFactory(screen, modOptions.theme(), this.optionRowTheme(), this.optionStateStore);
        this.tooltipController = new OptionTooltipController(dim, modOptions, this.optionStateStore, new OptionTooltipController.BoxRenderer(){

            @Override
            public void drawRect(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
                PageFrame.this.drawRect(guiGraphics, x1, y1, x2, y2, color);
            }

            @Override
            public void drawBorder(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
                PageFrame.this.drawBorder(guiGraphics, x1, y1, x2, y2, color);
            }
        });
        this.setupFrame();
        this.buildFrame();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setupFrame() {
        ReeseSodiumOptionsConfig.ConfigData config = ReeseSodiumOptionsConfig.config();
        this.layout = PageLayout.create(this.page, config.isHideNonMatchingOptions() && this.optionStateStore.searchActive(), this.optionStateStore.searchResults(), SearchResultOrder.DEFAULT, config.getDisabledOptionVisibility() == ReeseSodiumOptionsConfig.DisabledOptionVisibility.HIDDEN, config.isCollapsibleGroups(), this.optionStateStore.collapsedOptionGroups());
        this.setContentHeight(this.layout.contentHeight());
        this.optionRowFactory.registerParentBounds(this.layout, this.getFrameDim());
    }

    @Override
    public void rebuildFrameContent() {
        this.setupFrame();
        this.buildFrame();
    }

    @Override
    public void buildFrame() {
        this.children.clear();
        this.optionRows.clear();
        for (PageLayout.Row row : this.layout.rows()) {
            if (row instanceof PageLayout.LabelRow) {
                PageLayout.LabelRow labelRow = (PageLayout.LabelRow)row;
                this.children.add(this.createLabelWidget(labelRow));
                continue;
            }
            if (!(row instanceof PageLayout.OptionRow)) continue;
            PageLayout.OptionRow optionRow = (PageLayout.OptionRow)row;
            this.addOptionRow(optionRow);
        }
        super.buildFrame();
    }

    private LabelWidget createLabelWidget(PageLayout.LabelRow labelRow) {
        LayoutBounds dim = this.createRowDimension(labelRow.y());
        int labelColor = this.labelColor();
        if (!labelRow.collapsible()) {
            return new LabelWidget(dim, labelRow.text(), labelColor);
        }
        ResourceLocation collapseKey = labelRow.collapseKey();
        LabelWidget widget = this.groupHeaderWidgets.computeIfAbsent(collapseKey, key -> new LabelWidget(dim, labelRow.text(), labelColor, this.optionRowTheme(), (ResourceLocation)key, () -> this.toggleGroup((ResourceLocation)key), labelRow.collapsed()));
        widget.setDim(dim);
        widget.setCollapsed(labelRow.collapsed());
        return widget;
    }

    private void toggleGroup(ResourceLocation collapseKey) {
        Set<ResourceLocation> collapsed = this.optionStateStore.collapsedOptionGroups();
        if (!collapsed.remove(collapseKey)) {
            collapsed.add(collapseKey);
        }
        if (this.groupToggleRebuildHandler != null) {
            this.groupToggleRebuildHandler.accept(collapseKey);
        } else {
            this.rebuildFrameContent();
            this.focusGroupHeader(collapseKey);
        }
    }

    public void setGroupToggleRebuildHandler(@Nullable Consumer<ResourceLocation> groupToggleRebuildHandler) {
        this.groupToggleRebuildHandler = groupToggleRebuildHandler;
    }

    public boolean focusGroupHeader(ResourceLocation collapseKey) {
        for (GuiEventListener child : this.children) {
            LabelWidget labelWidget;
            if (!(child instanceof LabelWidget) || !collapseKey.equals((Object)(labelWidget = (LabelWidget)child).collapseKey())) continue;
            this.setFocused(labelWidget);
            return true;
        }
        return false;
    }

    private void addOptionRow(PageLayout.OptionRow row) {
        LayoutBounds rowDim = this.createRowDimension(row.y());
        this.children.add(this.getOptionRow(row.option(), rowDim));
    }

    private OptionRow getOptionRow(Option option, LayoutBounds dim) {
        ResourceLocation optionId = PageFrame.optionId(option);
        if (optionId == null) {
            return this.optionRowFactory.create(option, dim);
        }
        OptionRow optionRow = this.optionRowWidgets.get(optionId);
        if (optionRow == null || optionRow.getOption() != option) {
            optionRow = this.optionRowFactory.create(option, dim);
            this.optionRowWidgets.put(optionId, optionRow);
            return optionRow;
        }
        if (optionRow instanceof BaseWidget) {
            BaseWidget widget = (BaseWidget)((Object)optionRow);
            widget.setDim(dim);
        }
        this.optionRowFactory.registerOptionBounds(optionRow, dim);
        return optionRow;
    }

    @Nullable
    private static ResourceLocation optionId(Option option) {
        ResourceLocation resourceLocation;
        if (option instanceof OptionExtended) {
            OptionExtended optionExtended = (OptionExtended)option;
            resourceLocation = optionExtended.rso$getId();
        } else {
            resourceLocation = null;
        }
        return resourceLocation;
    }

    private LayoutBounds createRowDimension(int y) {
        return LayoutBounds.relativeTo(this.getFrameDim(), 0, y, this.getWidth(), 18);
    }

    private void setContentHeight(int height) {
        this.setDim(this.getDimensions().withHeight(height));
    }

    @Override
    public void updateFrameDim(LayoutBounds dim) {
        this.setDim(dim);
        this.relayoutRows();
    }

    private void relayoutRows() {
        this.optionRowFactory.registerParentBounds(this.layout, this.getFrameDim());
        int childIndex = 0;
        for (PageLayout.Row row : this.layout.rows()) {
            GuiEventListener child;
            if (childIndex >= this.children.size()) {
                return;
            }
            if (!((child = (GuiEventListener)this.children.get(childIndex++)) instanceof BaseWidget)) continue;
            BaseWidget widget = (BaseWidget)child;
            LayoutBounds rowDim = this.createRowDimension(row.y());
            widget.setDim(rowDim);
            if (!(child instanceof OptionRow)) continue;
            OptionRow optionRow = (OptionRow)child;
            this.optionRowFactory.registerOptionBounds(optionRow, rowDim);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.tooltipController.render(guiGraphics, this.optionRows, mouseX, mouseY);
    }

    private GuiTheme optionRowTheme() {
        return ReeseSodiumOptionsConfig.config().isColorThemes() ? GuiThemes.fromSodium(this.modOptions.theme()) : GuiThemes.DEFAULT_BUTTON;
    }

    private int labelColor() {
        return ReeseSodiumOptionsConfig.config().isColorThemes() && ReeseSodiumOptionsConfig.config().isThemedHeadersAndLabels() ? GuiThemes.fromSodium((ColorTheme)this.modOptions.theme()).themeLighter : -1;
    }

    public static class Builder {
        private LayoutBounds dim;
        private boolean renderOutline;
        private Page page;
        private Screen screen;
        private ModOptions modOptions;
        private OptionStateStore optionStateStore;

        public Builder withDimension(LayoutBounds dim) {
            this.dim = dim;
            return this;
        }

        public Builder withRenderOutline(boolean renderOutline) {
            this.renderOutline = renderOutline;
            return this;
        }

        public Builder withPage(Page page) {
            this.page = page;
            return this;
        }

        public Builder withScreen(Screen screen) {
            this.screen = screen;
            return this;
        }

        public Builder withModOptions(ModOptions modConfig) {
            this.modOptions = modConfig;
            return this;
        }

        public Builder withOptionStateStore(OptionStateStore optionStateStore) {
            this.optionStateStore = optionStateStore;
            return this;
        }

        public PageFrame build() {
            Validate.notNull((Object)this.dim, (String)"Dimension must be specified", (Object[])new Object[0]);
            Validate.notNull((Object)this.page, (String)"Option Page must be specified", (Object[])new Object[0]);
            Validate.notNull((Object)this.screen, (String)"Screen must be specified", (Object[])new Object[0]);
            Validate.notNull((Object)this.modOptions, (String)"Mod Options must be specified", (Object[])new Object[0]);
            Validate.notNull((Object)this.optionStateStore, (String)"Option state store must be specified", (Object[])new Object[0]);
            return new PageFrame(this.screen, this.dim, this.renderOutline, this.page, this.modOptions, this.optionStateStore);
        }
    }
}

