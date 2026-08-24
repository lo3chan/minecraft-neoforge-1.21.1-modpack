package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PageFrame extends AbstractFrame {
   protected final Page page;
   private final OptionStateStore optionStateStore;
   private final OptionRowFactory optionRowFactory;
   private final OptionTooltipController tooltipController;
   private final Map<ResourceLocation, LabelWidget> groupHeaderWidgets = new HashMap<>();
   private final Map<ResourceLocation, OptionRow> optionRowWidgets = new HashMap<>();
   @Nullable
   private Consumer<ResourceLocation> groupToggleRebuildHandler;
   private PageLayout layout;

   public PageFrame(Screen screen, LayoutBounds dim, boolean renderOutline, Page page, ModOptions modOptions, OptionStateStore optionStateStore) {
      super(dim, screen, renderOutline, modOptions);
      this.page = page;
      this.optionStateStore = optionStateStore;
      this.optionRowFactory = new OptionRowFactory(screen, modOptions.theme(), this.optionRowTheme(), this.optionStateStore);
      this.tooltipController = new OptionTooltipController(dim, modOptions, this.optionStateStore, new OptionTooltipController.BoxRenderer() {
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

   public static PageFrame.Builder builder() {
      return new PageFrame.Builder();
   }

   public void setupFrame() {
      ReeseSodiumOptionsConfig.ConfigData config = ReeseSodiumOptionsConfig.config();
      this.layout = PageLayout.create(
         this.page,
         config.isHideNonMatchingOptions() && this.optionStateStore.searchActive(),
         this.optionStateStore.searchResults(),
         SearchResultOrder.DEFAULT,
         config.getDisabledOptionVisibility() == ReeseSodiumOptionsConfig.DisabledOptionVisibility.HIDDEN,
         config.isCollapsibleGroups(),
         this.optionStateStore.collapsedOptionGroups()
      );
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
         if (row instanceof PageLayout.LabelRow labelRow) {
            this.children.add(this.createLabelWidget(labelRow));
         } else if (row instanceof PageLayout.OptionRow optionRow) {
            this.addOptionRow(optionRow);
         }
      }

      super.buildFrame();
   }

   private LabelWidget createLabelWidget(PageLayout.LabelRow labelRow) {
      LayoutBounds dim = this.createRowDimension(labelRow.y());
      int labelColor = this.labelColor();
      if (!labelRow.collapsible()) {
         return new LabelWidget(dim, labelRow.text(), labelColor);
      } else {
         ResourceLocation collapseKey = labelRow.collapseKey();
         LabelWidget widget = this.groupHeaderWidgets
            .computeIfAbsent(
               collapseKey,
               key -> new LabelWidget(dim, labelRow.text(), labelColor, this.optionRowTheme(), key, () -> this.toggleGroup(key), labelRow.collapsed())
            );
         widget.setDim(dim);
         widget.setCollapsed(labelRow.collapsed());
         return widget;
      }
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
         if (child instanceof LabelWidget labelWidget && collapseKey.equals(labelWidget.collapseKey())) {
            this.setFocused(labelWidget);
            return true;
         }
      }

      return false;
   }

   private void addOptionRow(PageLayout.OptionRow row) {
      LayoutBounds rowDim = this.createRowDimension(row.y());
      this.children.add(this.getOptionRow(row.option(), rowDim));
   }

   private OptionRow getOptionRow(Option option, LayoutBounds dim) {
      ResourceLocation optionId = optionId(option);
      if (optionId == null) {
         return this.optionRowFactory.create(option, dim);
      } else {
         OptionRow optionRow = this.optionRowWidgets.get(optionId);
         if (optionRow != null && optionRow.getOption() == option) {
            if (optionRow instanceof BaseWidget widget) {
               widget.setDim(dim);
            }

            this.optionRowFactory.registerOptionBounds(optionRow, dim);
            return optionRow;
         } else {
            optionRow = this.optionRowFactory.create(option, dim);
            this.optionRowWidgets.put(optionId, optionRow);
            return optionRow;
         }
      }
   }

   @Nullable
   private static ResourceLocation optionId(Option option) {
      return option instanceof OptionExtended optionExtended ? optionExtended.rso$getId() : null;
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
         if (childIndex >= this.children.size()) {
            return;
         }

         GuiEventListener child = this.children.get(childIndex++);
         if (child instanceof BaseWidget widget) {
            LayoutBounds rowDim = this.createRowDimension(row.y());
            widget.setDim(rowDim);
            if (child instanceof OptionRow optionRow) {
               this.optionRowFactory.registerOptionBounds(optionRow, rowDim);
            }
         }
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
      return ReeseSodiumOptionsConfig.config().isColorThemes() && ReeseSodiumOptionsConfig.config().isThemedHeadersAndLabels()
         ? GuiThemes.fromSodium(this.modOptions.theme()).themeLighter
         : -1;
   }

   public static class Builder {
      private LayoutBounds dim;
      private boolean renderOutline;
      private Page page;
      private Screen screen;
      private ModOptions modOptions;
      private OptionStateStore optionStateStore;

      public PageFrame.Builder withDimension(LayoutBounds dim) {
         this.dim = dim;
         return this;
      }

      public PageFrame.Builder withRenderOutline(boolean renderOutline) {
         this.renderOutline = renderOutline;
         return this;
      }

      public PageFrame.Builder withPage(Page page) {
         this.page = page;
         return this;
      }

      public PageFrame.Builder withScreen(Screen screen) {
         this.screen = screen;
         return this;
      }

      public PageFrame.Builder withModOptions(ModOptions modConfig) {
         this.modOptions = modConfig;
         return this;
      }

      public PageFrame.Builder withOptionStateStore(OptionStateStore optionStateStore) {
         this.optionStateStore = optionStateStore;
         return this;
      }

      public PageFrame build() {
         Validate.notNull(this.dim, "Dimension must be specified", new Object[0]);
         Validate.notNull(this.page, "Option Page must be specified", new Object[0]);
         Validate.notNull(this.screen, "Screen must be specified", new Object[0]);
         Validate.notNull(this.modOptions, "Mod Options must be specified", new Object[0]);
         Validate.notNull(this.optionStateStore, "Option state store must be specified", new Object[0]);
         return new PageFrame(this.screen, this.dim, this.renderOutline, this.page, this.modOptions, this.optionStateStore);
      }
   }
}
