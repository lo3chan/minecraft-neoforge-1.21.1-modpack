package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigScreenExtensions;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.ModIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.ItemCarouselWidget;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.MediaButton;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.ClientConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class MoonlightConfigSelectScreen extends Screen {
   private static final int STRIP = 20;
   private static final int PAD = 8;
   private static final int ICON_MAX = 64;
   private final String modId;
   private final Screen parent;
   @Nullable
   private final ResourceLocation background;
   private final List<ModConfigHolder> holders;
   @Nullable
   private final Component version;
   private final List<String> authors;
   private ConfigRowList list;
   private int leftPaneWidth;
   private int leftPaneBottom;
   private boolean customShowcase;

   private MoonlightConfigSelectScreen(String modId, List<ModConfigHolder> holders, Screen parent, @Nullable ResourceLocation background) {
      super(Component.literal(PlatHelper.getModName(modId)));
      this.modId = modId;
      this.parent = parent;
      this.background = background;
      this.holders = holders;
      String v = PlatHelper.getModVersion(modId);
      this.version = v == null ? null : Component.literal("v" + v);
      this.authors = PlatHelper.getModAuthors(modId);
   }

   private static List<ModConfigHolder> configsOf(String modId) {
      return ModConfigHolder.getTrackedHolders()
         .stream()
         .filter(h -> h.getModId().equals(modId))
         .sorted(Comparator.comparingInt(h -> h.getConfigType().ordinal()))
         .toList();
   }

   @Nullable
   public static Screen create(String modId, Screen parent, @Nullable ResourceLocation background) {
      return create(modId, configsOf(modId), parent, background);
   }

   @Nullable
   public static Screen create(String modId, List<ModConfigHolder> holders, Screen parent, @Nullable ResourceLocation background) {
      if (holders.isEmpty()) {
         return null;
      } else {
         return (Screen)(holders.size() == 1 && ConfigScreenExtensions.overlaysFor(modId).isEmpty() && ConfigScreenExtensions.showcaseFor(modId) == null
            ? ((ModConfigHolder)holders.getFirst()).makeScreen(parent, background)
            : new MoonlightConfigSelectScreen(modId, holders, parent, background));
      }
   }

   protected void init() {
      this.leftPaneWidth = Mth.clamp(this.width / 3, 104, 170);
      int blockWidth = this.leftPaneWidth - 16;
      ConfigScreenExtensions.Showcase showcase = ConfigScreenExtensions.showcaseFor(this.modId);
      this.customShowcase = showcase != null;
      boolean showcaseTakesCarousel = showcase != null && showcase.replacesCarousel();
      if (showcase != null) {
         AbstractWidget widget = showcase.create(
            this.modId, 8, this.iconTop(), blockWidth, this.iconBottom() - this.iconTop() + (showcaseTakesCarousel ? 24 : 0)
         );
         this.addRenderableWidget(widget);
         this.leftPaneBottom = widget.getY() + widget.getHeight();
      }

      if (!showcaseTakesCarousel) {
         ItemCarouselWidget carousel = ClientConfigs.CONFIG_ITEM_CAROUSEL.get()
            ? ItemCarouselWidget.forMod(this.modId, 8, this.iconBottom() + 4, blockWidth, 20)
            : null;
         this.leftPaneBottom = this.iconBottom() + (carousel == null ? 0 : 24);
         if (carousel != null) {
            this.addRenderableWidget(carousel.withOutline(-16777216));
         }
      }

      int paneWidth = this.width - this.leftPaneWidth;
      this.list = new ConfigRowList(this.minecraft, paneWidth, this.contentBottom() - 44, 44, 30);
      this.list.setX(this.leftPaneWidth);
      this.list.setRowWidth(Math.min(280, paneWidth - 28));
      this.list.setDrawFooterSeparator(false);
      this.list.setTopPadding((this.contentBottom() - 44 - this.holders.size() * 30) / 2 - 4);
      List<ConfigListRow> rows = new ArrayList<>();

      for (ModConfigHolder h : this.holders) {
         Component label = Component.literal(TextHelper.getReadableName(h.getId().getPath()));
         Component subtitle = Component.literal(h.getFileName());
         rows.add(
            new ConfigHolderRow(
               label, subtitle, ConfigScreenLayout.configFileIcon(h.getConfigType()), () -> this.minecraft.setScreen(h.makeScreen(this, this.background))
            )
         );
      }

      this.list.setRows(rows);
      this.addRenderableWidget(this.list);
      MediaButton.addAuthorMediaButtons(this, x$0 -> {
         Button var10000 = (Button)this.addRenderableWidget(x$0);
      }, this.width / 2, this.height - 28, 22, this.modId, this::onClose);
      this.addRenderableWidget(new GearButton(8, this.height - 28, 20, b -> this.minecraft.setScreen(new ModsTilesScreen(this, this.background))));
   }

   private int contentBottom() {
      return this.height - 36;
   }

   private int iconTop() {
      return 54;
   }

   private int iconBottom() {
      return this.iconTop() + Math.min(64, this.leftPaneWidth - 16);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      GuiHelper.renderHeaderBar(graphics, this.font, this.title, this.version, this.width, 44);
      this.renderLeftPane(graphics);
   }

   private void renderLeftPane(GuiGraphics graphics) {
      int bottom = this.contentBottom();
      GuiHelper.renderMenuBand(graphics, 0, 44, this.leftPaneWidth, bottom - 44);
      int textWidth = this.leftPaneWidth - 16;
      int iconHeight = Math.min(64, textWidth);
      if (!this.customShowcase) {
         ModIcons.Icon icon = ModIcons.get(this.modId);
         if (icon != null) {
            GuiHelper.renderModIcon(graphics, icon, 8, this.iconTop(), textWidth, iconHeight);
         } else {
            GuiHelper.renderInitialTile(
               graphics,
               this.font,
               this.title.getString(),
               8 + (textWidth - iconHeight) / 2,
               this.iconTop(),
               iconHeight,
               -13619144,
               ConfigGuiColors.initialLetter(this.modId),
               MoonlightIcons.CONFIG
            );
         }
      }

      int y = this.leftPaneBottom + 8;
      if (!this.authors.isEmpty()) {
         GuiHelper.renderSeparator(graphics, 8, y, textWidth);
         y += 8;
         int line = 9;
         graphics.drawString(this.font, Component.translatable("gui.moonlight.config.authors"), 8, y, ConfigGuiColors.DESCRIPTION);
         y += line + 1;

         for (String author : this.authors) {
            for (FormattedCharSequence row : this.font.split(Component.literal(author), textWidth)) {
               if (y + line > bottom - 2) {
                  return;
               }

               graphics.drawString(this.font, row, 8, y, ConfigGuiColors.LABEL);
               y += line;
            }
         }
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      int bottom = this.contentBottom();
      GuiHelper.renderVerticalSeparator(graphics, this.leftPaneWidth, 44, bottom);
      GuiHelper.renderFooterSeparator(graphics, bottom, this.width);
      ConfigScreenExtensions.Panel panel = this.overlayPanel();

      for (ConfigScreenExtensions.Overlay overlay : ConfigScreenExtensions.overlaysFor(this.modId)) {
         overlay.render(graphics, panel, mouseX, mouseY, partialTick);
      }

      ConfigListRow hovered = this.list.getHovered(mouseX, mouseY);
      if (hovered != null) {
         Component tooltip = hovered.getTooltip(mouseX, mouseY);
         if (tooltip != null) {
            graphics.renderTooltip(this.font, this.font.split(tooltip, 220), mouseX, mouseY);
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      ConfigScreenExtensions.Panel panel = this.overlayPanel();

      for (ConfigScreenExtensions.Overlay overlay : ConfigScreenExtensions.overlaysFor(this.modId)) {
         if (overlay.mouseClicked(panel, mouseX, mouseY, button)) {
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   private ConfigScreenExtensions.Panel overlayPanel() {
      return new ConfigScreenExtensions.Panel(this, 0, 44, this.width, this.contentBottom());
   }
}
