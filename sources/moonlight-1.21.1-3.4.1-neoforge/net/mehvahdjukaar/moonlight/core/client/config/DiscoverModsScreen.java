package net.mehvahdjukaar.moonlight.core.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.ModIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.client.OurModsList;
import net.mehvahdjukaar.moonlight.core.client.RemoteIconCache;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class DiscoverModsScreen extends Screen {
   private static final int SIDE_MARGIN = 24;
   private static final int MAX_CONTENT_W = 320;
   private static final int ROW_H = 48;
   private static final int ROW_GAP = 4;
   private static final int GRID_PAD = 8;
   private static final int ICON_SIZE = 32;
   private static final int ROW_INNER_PAD = 8;
   private static final int LINE = 11;
   private static final int MAX_DESC_LINES = 2;
   private static final int NAME_INSTALLED = ConfigGuiColors.LABEL;
   private static final int NAME_MISSING = ConfigGuiColors.TEXT_SECONDARY;
   private static final int DESC_INSTALLED = ConfigGuiColors.DESCRIPTION;
   private static final int DESC_MISSING = -9803144;
   private final Screen parent;
   private final List<DiscoverModsScreen.Row> rows = new ArrayList<>();
   private LoadingDotsWidget loadingWidget;
   private boolean built;
   private double scroll;
   private int maxScroll;
   private int contentTop;
   private int contentBottom;
   private int rowX;
   private int contentW;

   public DiscoverModsScreen(Screen parent) {
      super(Component.translatable("gui.moonlight.config.discover_title"));
      this.parent = parent;
   }

   protected void init() {
      OurModsList.fetchIfNeeded();
      this.built = false;
      this.rows.clear();
      this.loadingWidget = new LoadingDotsWidget(this.font, Component.translatable("gui.moonlight.config.discover_loading"));
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> this.onClose()).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
   }

   private void buildRows() {
      this.rows.clear();
      this.contentW = Math.min(this.width - 48, 320);
      int textWidth = this.contentW - 48 - 8;

      for (OurModsList.Entry e : OurModsList.getMods()) {
         boolean installed = PlatHelper.isModLoaded(e.modId());
         List<FormattedCharSequence> desc = e.description().isBlank() ? List.of() : this.font.split(Component.literal(e.description()), textWidth);
         if (desc.size() > 2) {
            desc = desc.subList(0, 2);
         }

         this.rows.add(new DiscoverModsScreen.Row(e, installed, desc));
      }

      this.built = true;
   }

   private void computeLayout() {
      this.contentTop = 44;
      this.contentBottom = this.height - 36;
      this.rowX = (this.width - this.contentW) / 2;
      int totalHeight = this.rows.size() * 52 - 4 + 16;
      this.maxScroll = Math.max(0, totalHeight - (this.contentBottom - this.contentTop));
      this.scroll = Mth.clamp(this.scroll, 0.0, this.maxScroll);
   }

   private int rowY(int i) {
      return this.contentTop + 8 + i * 52 - (int)this.scroll;
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      GuiHelper.renderHeaderBar(graphics, this.font, this.title, this.width, 44);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.contentTop = 44;
      this.contentBottom = this.height - 36;
      GuiHelper.renderListBackground(graphics, this.contentTop, this.contentBottom, this.width, this.scroll);
      OurModsList.State state = OurModsList.getState();
      if (state == OurModsList.State.LOADED) {
         if (!this.built) {
            this.buildRows();
         }

         this.renderRows(graphics, mouseX, mouseY);
      } else if (state == OurModsList.State.FAILED) {
         graphics.drawCenteredString(
            this.font,
            Component.translatable("gui.moonlight.config.discover_offline"),
            this.width / 2,
            (this.contentTop + this.contentBottom) / 2 - 9 / 2,
            ConfigGuiColors.DESCRIPTION
         );
      } else {
         this.loadingWidget.setPosition(0, this.contentTop);
         this.loadingWidget.setSize(this.width, this.contentBottom - this.contentTop);
         this.loadingWidget.render(graphics, mouseX, mouseY, partialTick);
      }

      GuiHelper.renderFooterSeparator(graphics, this.contentBottom, this.width);
   }

   private void renderRows(GuiGraphics graphics, int mouseX, int mouseY) {
      this.computeLayout();
      boolean inViewport = mouseY >= this.contentTop && mouseY < this.contentBottom;
      graphics.enableScissor(0, this.contentTop, this.width, this.contentBottom);

      for (int i = 0; i < this.rows.size(); i++) {
         int y = this.rowY(i);
         if (y + 48 >= this.contentTop && y <= this.contentBottom) {
            boolean hover = inViewport && mouseX >= this.rowX && mouseX < this.rowX + this.contentW && mouseY >= y && mouseY < y + 48;
            this.renderRow(graphics, this.rows.get(i), y, hover);
         }
      }

      graphics.disableScissor();
      GuiHelper.renderScrollbar(graphics, this.contentTop, this.contentBottom, this.width, this.scroll, this.maxScroll);
   }

   private void renderRow(GuiGraphics graphics, DiscoverModsScreen.Row row, int y, boolean hover) {
      graphics.fill(this.rowX, y, this.rowX + this.contentW, y + 48, hover ? -13882316 : -15000800);
      graphics.renderOutline(this.rowX, y, this.contentW, 48, hover ? ConfigGuiColors.TILE_OUTLINE_HOVER : -16777216);
      boolean installed = row.installed();
      int iconX = this.rowX + 8;
      int iconY = y + 8;
      this.renderIcon(graphics, row, iconX, iconY, installed);
      int textX = iconX + 32 + 8;
      int textRight = this.rowX + this.contentW - 8;
      int nameRight = installed ? textRight - 12 : textRight;
      int nameColor = installed ? NAME_INSTALLED : NAME_MISSING;
      GuiHelper.renderScrollingText(graphics, this.font, Component.literal(row.data().name()), textX, nameRight, y + 6, 11, nameColor);
      int descColor = installed ? DESC_INSTALLED : -9803144;
      int descY = y + 6 + 11;

      for (FormattedCharSequence line : row.descLines()) {
         graphics.drawString(this.font, line, textX, descY, descColor);
         descY += 11;
      }

      if (installed) {
         graphics.blitSprite(MoonlightIcons.YES, textRight - 10, y + 7, 10, 10);
      }
   }

   private void renderIcon(GuiGraphics graphics, DiscoverModsScreen.Row row, int iconX, int iconY, boolean installed) {
      ModIcons.Icon icon = ModIcons.get(row.data().modId());
      if (icon == null && row.data().iconUrl() != null) {
         icon = RemoteIconCache.get(row.data().modId(), row.data().iconUrl());
      }

      if (icon != null) {
         if (!installed) {
            RenderSystem.enableBlend();
            graphics.setColor(1.0F, 1.0F, 1.0F, 0.35F);
         }

         graphics.blit(icon.texture(), iconX, iconY, 32, 32, 0.0F, 0.0F, icon.width(), icon.height(), icon.width(), icon.height());
         if (!installed) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
         }
      } else {
         this.renderFallbackIcon(graphics, row, iconX, iconY, installed);
      }
   }

   private void renderFallbackIcon(GuiGraphics graphics, DiscoverModsScreen.Row row, int iconX, int iconY, boolean installed) {
      GuiHelper.renderInitialTile(
         graphics,
         this.font,
         row.data().name(),
         iconX,
         iconY,
         32,
         installed ? -13619144 : -14342869,
         installed ? ConfigGuiColors.initialLetter(row.data().name()) : -9803144,
         MoonlightIcons.CONFIG
      );
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.maxScroll > 0) {
         this.scroll = Mth.clamp(this.scroll - scrollY * 24.0, 0.0, this.maxScroll);
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.built && mouseY >= this.contentTop && mouseY < this.contentBottom) {
         for (int i = 0; i < this.rows.size(); i++) {
            int y = this.rowY(i);
            if (mouseX >= this.rowX && mouseX < this.rowX + this.contentW && mouseY >= y && mouseY < y + 48 && this.openModPage(this.rows.get(i).data())) {
               return true;
            }
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   private boolean openModPage(OurModsList.Entry entry) {
      String url = entry.modrinthUrl() != null ? entry.modrinthUrl() : entry.curseforgeUrl();
      if (url == null) {
         return false;
      } else {
         GuiHelper.playClickSound();
         this.handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, url)));
         return true;
      }
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private record Row(OurModsList.Entry data, boolean installed, List<FormattedCharSequence> descLines) {
   }
}
