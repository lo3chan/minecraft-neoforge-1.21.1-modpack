package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.ModIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.IconButton;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.core.ClientConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ModsTilesScreen extends Screen {
   private static final List<String> EXTRA_MODS = List.of("polytone", "nautilus_studio");
   private static final int GRID_PAD = 8;
   private static final int CARD_W = 88;
   private static final int CARD_PAD = 9;
   private static final int ICON_TEXT_GAP = 6;
   private static final int NAME_VER_GAP = 2;
   private static final int ICON_SIZE = 32;
   private static final int ICON_SIDE_PAD = 8;
   private static final int LINE = 9;
   private static final int CARD_H = 76;
   private static final int CARD_GAP = 6;
   private static final int SIDE_MARGIN = 24;
   private static final int VERSION_COLOR = ConfigGuiColors.DESCRIPTION;
   private static final int SEARCH_WIDTH = 110;
   private static final int SEARCH_HEIGHT = 14;
   private static final int SEARCH_ICON_SIZE = 12;
   private static final int SEARCH_ICON_GAP = 2;
   private static final int TITLE_SEARCH_GAP = 5;
   private static final int TITLE_Y_WITH_SEARCH = 7;
   private static final int SEARCH_Y = 21;
   private final Screen parent;
   @Nullable
   private final ResourceLocation background;
   private final List<ModsTilesScreen.Entry> allEntries = new ArrayList<>();
   private final List<ModsTilesScreen.Entry> entries = new ArrayList<>();
   @Nullable
   private EditBox searchBox;
   private String searchQuery = "";
   private double scroll;
   private int maxScroll;
   private int cols;
   private int contentTop;
   private int contentBottom;

   public ModsTilesScreen(Screen parent, @Nullable ResourceLocation background) {
      super(Component.translatable("gui.moonlight.config.mods_title"));
      this.parent = parent;
      this.background = background;
   }

   private static boolean isOurs(String modId) {
      if (EXTRA_MODS.contains(modId)) {
         return true;
      } else {
         for (ModConfigHolder h : ModConfigHolder.getTrackedHolders()) {
            if (h.getModId().equals(modId)) {
               return true;
            }
         }

         return false;
      }
   }

   public static Set<String> collectConfigurableMods() {
      Set<String> modIds = new LinkedHashSet<>();

      for (ModConfigHolder h : ModConfigHolder.getTrackedHolders()) {
         modIds.add(h.getModId());
      }

      for (String modId : EXTRA_MODS) {
         if (ClientHelper.hasModConfigScreen(modId)) {
            modIds.add(modId);
         }
      }

      boolean convert = ClientConfigs.CONVERT_FOREIGN_CONFIGS.get().isOn();
      if (ClientConfigs.SHOW_ALL_MOD_CONFIGS.get() || convert) {
         for (String modIdx : PlatHelper.getInstalledMods()) {
            if (ClientHelper.hasModConfigScreen(modIdx) || convert && ClientHelper.hasNativeForeignConfig(modIdx)) {
               modIds.add(modIdx);
            }
         }
      }

      return modIds;
   }

   public static boolean openModScreenOrModsScreen(String modId) {
      Screen screen = (Screen)(modId.isEmpty() ? new ModsTilesScreen(null, null) : configScreenFor(modId, null, null));
      if (screen == null) {
         return false;
      } else {
         Minecraft mc = Minecraft.getInstance();
         mc.tell(() -> mc.setScreen(screen));
         return true;
      }
   }

   @Nullable
   public static Screen configScreenFor(String modId, @Nullable Screen parent, @Nullable ResourceLocation background) {
      Screen s = MoonlightConfigSelectScreen.create(modId, parent, background);
      if (s == null && shouldConvert(modId)) {
         s = ClientHelper.getNativeForeignConfigScreen(modId, parent, background);
      }

      if (s == null) {
         s = ClientHelper.getModConfigScreen(modId, parent);
      }

      return s;
   }

   private static boolean shouldConvert(String modId) {
      return switch ((ClientConfigs.ForeignConfigMode)ClientConfigs.CONVERT_FOREIGN_CONFIGS.get()) {
         case NEVER -> false;
         case ALWAYS -> true;
         case GENERIC_ONLY -> ClientHelper.hasOnlyGenericConfigScreen(modId) && !ClientHelper.hasHiddenPerWorldConfig(modId);
      };
   }

   protected void init() {
      this.allEntries.clear();

      for (String modId : collectConfigurableMods()) {
         String name = PlatHelper.getModName(modId);
         String version = PlatHelper.getModVersion(modId);
         this.allEntries
            .add(new ModsTilesScreen.Entry(modId, Component.literal(name), version == null ? null : Component.literal("v" + version), isOurs(modId)));
      }

      this.allEntries
         .sort(
            Comparator.<ModsTilesScreen.Entry, Integer>comparing(e -> e.ours() ? 0 : 1).thenComparing(e -> e.name().getString(), String.CASE_INSENSITIVE_ORDER)
         );
      this.entries.clear();
      this.entries.addAll(this.allEntries);
      this.computeLayout();
      this.searchBox = this.maxScroll > 0 ? this.makeSearchBox() : null;
      if (this.searchBox != null) {
         this.addRenderableWidget(this.searchBox);
      }

      this.applyFilter();
      this.addRenderableWidget(
         new IconButton(
            this.width / 2 - 154,
            this.height - 28,
            140,
            20,
            Component.translatable("gui.moonlight.config.discover_mods"),
            MoonlightIcons.DISCOVER_MODS,
            12,
            12,
            b -> this.minecraft.setScreen(new DiscoverModsScreen(this))
         )
      );
      IconButton openFolder = new IconButton(
         this.width / 2 - 10, this.height - 28, 20, 20, CommonComponents.EMPTY, MoonlightIcons.FOLDER, 12, 12, b -> openConfigFolder()
      );
      openFolder.setTooltip(Tooltip.create(Component.translatable("gui.moonlight.config.open_folder")));
      this.addRenderableWidget(openFolder);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> this.onClose()).bounds(this.width / 2 + 14, this.height - 28, 140, 20).build());
   }

   private static void openConfigFolder() {
      Util.getPlatform().openPath(PlatHelper.getGamePath().resolve("config"));
   }

   private EditBox makeSearchBox() {
      Component label = Component.translatable("gui.moonlight.config.search");
      EditBox box = new EditBox(this.font, (this.width - 110) / 2, 21, 110, 14, label);
      box.setHint(label.copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
      box.setValue(this.searchQuery);
      box.setResponder(query -> {
         this.searchQuery = query;
         this.scroll = 0.0;
         this.applyFilter();
      });
      return box;
   }

   private void applyFilter() {
      String query = this.searchQuery.trim().toLowerCase(Locale.ROOT);
      this.entries.clear();

      for (ModsTilesScreen.Entry e : this.allEntries) {
         if (query.isEmpty() || e.modId().contains(query) || e.name().getString().toLowerCase(Locale.ROOT).contains(query)) {
            this.entries.add(e);
         }
      }
   }

   private void computeLayout() {
      int availWidth = this.width - 48;
      int maxCols = Math.max(1, (availWidth + 6) / 94);
      int count = this.entries.size();
      int rows = (count + maxCols - 1) / maxCols;
      this.cols = rows == 0 ? maxCols : Math.min(maxCols, (count + rows - 1) / rows);
      this.contentTop = 44;
      this.contentBottom = this.height - 36;
      int totalHeight = Math.max(0, rows * 82 - 6) + 16;
      this.maxScroll = Math.max(0, totalHeight - (this.contentBottom - this.contentTop));
      this.scroll = Mth.clamp(this.scroll, 0.0, this.maxScroll);
   }

   private int cardX(int i) {
      int cardsInRow = Math.min(this.cols, this.entries.size() - i / this.cols * this.cols);
      int rowWidth = cardsInRow * 94 - 6;
      return (this.width - rowWidth) / 2 + i % this.cols * 94;
   }

   private int cardY(int i) {
      return this.contentTop + 8 + i / this.cols * 82 - (int)this.scroll;
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      if (this.searchBox == null) {
         GuiHelper.renderHeaderBar(graphics, this.font, this.title, this.width, 44);
      } else {
         GuiHelper.renderHeaderBar(graphics, this.width, 44);
         graphics.drawCenteredString(this.font, this.title, this.width / 2, 7, ConfigGuiColors.TITLE);
         graphics.blitSprite(MoonlightIcons.SEARCH, this.searchBox.getX() - 12 - 2, 22, 12, 12);
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.computeLayout();
      GuiHelper.renderListBackground(graphics, this.contentTop, this.contentBottom, this.width, this.scroll);
      boolean inViewport = mouseY >= this.contentTop && mouseY < this.contentBottom;
      graphics.enableScissor(0, this.contentTop, this.width, this.contentBottom);

      for (int i = 0; i < this.entries.size(); i++) {
         int x = this.cardX(i);
         int y = this.cardY(i);
         if (y + 76 >= this.contentTop && y <= this.contentBottom) {
            boolean hover = inViewport && mouseX >= x && mouseX < x + 88 && mouseY >= y && mouseY < y + 76;
            this.renderCard(graphics, this.entries.get(i), x, y, hover);
         }
      }

      graphics.disableScissor();
      GuiHelper.renderFooterSeparator(graphics, this.contentBottom, this.width);
      GuiHelper.renderScrollbar(graphics, this.contentTop, this.contentBottom, this.width, this.scroll, this.maxScroll);
   }

   private void renderCard(GuiGraphics graphics, ModsTilesScreen.Entry entry, int x, int y, boolean hover) {
      graphics.fill(x, y, x + 88, y + 76, hover ? -13882316 : -15000800);
      int outline = -16777216;
      if (hover) {
         outline = entry.ours() ? ConfigGuiColors.TILE_OUTLINE_HOVER : ConfigGuiColors.TILE_OUTLINE_HOVER_FOREIGN;
      }

      graphics.renderOutline(x, y, 88, 76, outline);
      int iconX = x + 28;
      int iconY = y + 9;
      ModIcons.Icon icon = ModIcons.get(entry.modId());
      if (icon != null) {
         GuiHelper.renderModIcon(graphics, icon, x + 8, iconY, 72, 32);
      } else {
         this.renderFallbackIcon(graphics, entry, iconX, iconY);
      }

      int textCenter = x + 44;
      int nameY = iconY + 32 + 6;
      GuiHelper.renderScrollingTextCentered(graphics, this.font, entry.name(), x + 4, x + 88 - 4, nameY, 9, ConfigGuiColors.LABEL);
      if (entry.version() != null) {
         this.drawClippedCentered(graphics, entry.version(), textCenter, nameY + 9 + 2, x + 4, x + 88 - 4, VERSION_COLOR);
      }
   }

   private void drawClippedCentered(GuiGraphics graphics, Component text, int centerX, int y, int minX, int maxX, int color) {
      graphics.enableScissor(minX, y - 1, maxX, y + 9 + 1);
      graphics.drawCenteredString(this.font, text, centerX, y, color);
      graphics.disableScissor();
   }

   private void renderFallbackIcon(GuiGraphics graphics, ModsTilesScreen.Entry entry, int iconX, int iconY) {
      GuiHelper.renderInitialTile(
         graphics, this.font, entry.name().getString(), iconX, iconY, 32, -13619144, ConfigGuiColors.initialLetter(entry.modId()), MoonlightIcons.CONFIG
      );
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.maxScroll > 0) {
         this.scroll = Mth.clamp(this.scroll - scrollY * 38.0, 0.0, this.maxScroll);
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && mouseY >= this.contentTop && mouseY < this.contentBottom) {
         for (int i = 0; i < this.entries.size(); i++) {
            int x = this.cardX(i);
            int y = this.cardY(i);
            if (mouseX >= x && mouseX < x + 88 && mouseY >= y && mouseY < y + 76) {
               String modId = this.entries.get(i).modId();
               Screen s = configScreenFor(modId, this, this.background);
               if (s != null) {
                  GuiHelper.playClickSound();
                  this.minecraft.setScreen(s);
                  return true;
               }
            }
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private record Entry(String modId, Component name, @Nullable Component version, boolean ours) {
   }
}
