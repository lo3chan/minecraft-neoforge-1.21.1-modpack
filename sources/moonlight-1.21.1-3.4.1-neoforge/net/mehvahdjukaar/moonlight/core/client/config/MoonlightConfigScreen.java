package net.mehvahdjukaar.moonlight.core.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigEditSession;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.BreadcrumbWidget;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.IconButton;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigNode;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class MoonlightConfigScreen extends ConfigPageScreen {
   private static final SystemToastId RELOAD_TOAST_ID = new SystemToastId();
   private final ConfigEditSession session;
   private final ConfigCategory category;
   @Nullable
   private final MoonlightConfigScreen parentPage;
   @Nullable
   private final ResourceLocation background;
   private static final int SIDE_MARGIN = 14;
   private static final int CRUMB_Y = 25;
   private static final int SEARCH_WIDTH = 110;
   private static final int SEARCH_HEIGHT = 14;
   private static final int SEARCH_ICON_SIZE = 12;
   private static final String CRUMB_SEPARATOR = " › ";
   private Button saveButton;
   private EditBox searchBox;
   private String searchQuery = "";

   public MoonlightConfigScreen(ModConfigHolder holder, ConfigCategory root, Screen returnScreen, @Nullable ResourceLocation background) {
      this(root, null, new ConfigEditSession(holder, returnScreen), background);
   }

   public static Screen create(ModConfigHolder holder, ConfigCategory root, Screen returnScreen, @Nullable ResourceLocation background) {
      return new MoonlightConfigScreen(holder, root, returnScreen, background);
   }

   private MoonlightConfigScreen(
      ConfigCategory category, @Nullable MoonlightConfigScreen parentPage, ConfigEditSession session, @Nullable ResourceLocation background
   ) {
      super(session.holder().getReadableName());
      this.category = category;
      this.parentPage = parentPage;
      this.session = session;
      this.background = background;
   }

   private boolean isRoot() {
      return this.parentPage == null;
   }

   @Override
   public ConfigEditSession session() {
      return this.session;
   }

   @Override
   public void openCategory(ConfigCategory cat) {
      this.minecraft.setScreen(new MoonlightConfigScreen(cat, this, this.session, this.background));
   }

   @Override
   public void onValueEdited() {
      this.refreshSave();
   }

   protected void init() {
      this.overlay.clear();
      this.list = new ConfigRowList(this.minecraft, this.width, this.height - 44 - 36, 44, 24);
      this.searchBox = new EditBox(this.font, this.width - 14 - 110, 22, 110, 14, Component.translatable("gui.moonlight.config.search"));
      this.searchBox.setHint(Component.translatable("gui.moonlight.config.search").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
      this.searchBox.setValue(this.searchQuery);
      this.searchBox.setResponder(query -> {
         this.searchQuery = query;
         this.populate();
      });
      this.addRenderableWidget(this.searchBox);
      List<BreadcrumbWidget.Crumb> crumbs = new ArrayList<>();

      for (MoonlightConfigScreen s = this; s != null; s = s.parentPage) {
         Component label = (Component)(s.isRoot() ? Component.literal("⌂") : s.category.title());
         crumbs.addFirst(new BreadcrumbWidget.Crumb(label, s, s == this));
      }

      int trailRight = this.searchBox.getX() - 12 - 6;
      BreadcrumbWidget breadcrumb = new BreadcrumbWidget(14, 25, trailRight - 14, 9, this.font, crumbs, target -> {
         if (target != this) {
            this.minecraft.setScreen(target);
         }
      });
      this.addRenderableWidget(breadcrumb);
      this.populate();
      this.addRenderableWidget(this.list);
      int y = this.height - 28;
      int bw = 100;
      int gap = 4;
      if (this.isRoot()) {
         int total = 3 * bw + 2 * gap;
         int x0 = (this.width - total) / 2;
         this.addRenderableWidget(
            new IconButton(x0, y, bw, 20, Component.translatable("gui.moonlight.config.reset_all"), MoonlightIcons.RESET, 12, 12, b -> this.confirmResetAll())
         );
         this.saveButton = new IconButton(x0 + bw + gap, y, bw, 20, Component.empty(), MoonlightIcons.SAVE, 12, 12, b -> this.doSave());
         this.addRenderableWidget(this.saveButton);
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> this.onClose()).bounds(x0 + 2 * (bw + gap), y, bw, 20).build());
      } else {
         this.saveButton = new IconButton(this.width / 2 - 104, y, bw, 20, Component.empty(), MoonlightIcons.SAVE, 12, 12, b -> this.doSave());
         this.addRenderableWidget(this.saveButton);
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> this.onClose()).bounds(this.width / 2 + 4, y, bw, 20).build());
      }

      this.addRenderableWidget(new GearButton(8, y, 20, b -> this.minecraft.setScreen(new ModsTilesScreen(this, this.background))));
      this.refreshSave();
   }

   private void confirmResetAll() {
      this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
         if (confirmed) {
            this.resetAllToDefaults(this.category);
            this.session.apply();
            this.session.clearPending();
         }

         this.minecraft.setScreen(this);
      }, Component.translatable("gui.moonlight.config.reset_all.title"), Component.translatable("gui.moonlight.config.reset_all.message")));
   }

   private void resetAllToDefaults(ConfigCategory cat) {
      for (ConfigNode e : cat.entries()) {
         if (e instanceof ConfigCategory sub) {
            this.resetAllToDefaults(sub);
         } else if (e instanceof ConfigOption<?> v && !(v instanceof ConfigOption.UnsupportedValue)) {
            this.session.put(v, v.defaultValue());
         }
      }
   }

   @Override
   protected void populate() {
      List<ConfigListRow> rows = new ArrayList<>();
      String query = this.searchQuery == null ? "" : this.searchQuery.trim().toLowerCase(Locale.ROOT);
      if (query.isEmpty()) {
         for (ConfigNode e : this.category.entries()) {
            if (e instanceof ConfigCategory cat) {
               rows.add(new CategoryRow(this, cat));
            } else if (e instanceof ConfigOption<?> v) {
               this.addOption(rows, v);
            }
         }
      } else {
         List<ConfigOption<?>> matches = new ArrayList<>();
         collectMatches(this.category, query, false, matches);

         for (ConfigOption<?> v : matches) {
            rows.add(new OptionRow(this, v, this.categorySearchPathOf(v)));
            this.addDescriptionRows(rows, v);
         }
      }

      this.list.setRows(rows);
   }

   private void addOption(List<ConfigListRow> rows, ConfigOption<?> v) {
      rows.add(new OptionRow(this, v));
      this.addDescriptionRows(rows, v);
   }

   private static void collectMatches(ConfigCategory category, String query, boolean inMatchedCategory, List<ConfigOption<?>> out) {
      for (ConfigNode e : category.entries()) {
         if (e instanceof ConfigCategory cat) {
            collectMatches(cat, query, inMatchedCategory || matches(cat.title(), query), out);
         } else if (e instanceof ConfigOption<?> v && (inMatchedCategory || v != category.gate() && matches(v.title(), query))) {
            out.add(v);
         }
      }
   }

   private static boolean matches(Component text, String query) {
      return text.getString().toLowerCase(Locale.ROOT).contains(query);
   }

   @Nullable
   private Component categorySearchPathOf(ConfigOption<?> option) {
      List<Component> parts = new ArrayList<>();

      for (ConfigCategory c = option.parent(); c != null && c != this.category; c = c.parent()) {
         parts.addFirst(c.title());
      }

      if (parts.isEmpty()) {
         return null;
      } else {
         MutableComponent path = Component.empty();

         for (Component part : parts) {
            path.append(part).append(" › ");
         }

         return path.withStyle(s -> s.withColor(TextColor.fromRgb(ConfigGuiColors.CRUMB)));
      }
   }

   private void doSave() {
      this.session.apply();
      this.session.clearPending();
      this.rebuildWidgets();
   }

   private void refreshSave() {
      if (this.saveButton != null) {
         int unsaved = this.session.unsavedCount();
         Component count = Component.literal("(" + unsaved + ")").withStyle(s -> s.withColor(TextColor.fromRgb(ConfigGuiColors.MODIFIED)));
         this.saveButton
            .setMessage(
               unsaved > 0
                  ? Component.translatable("gui.moonlight.config.save_count", new Object[]{count})
                  : Component.translatable("gui.moonlight.config.save")
            );
         this.saveButton.active = unsaved > 0;
      }
   }

   public void onClose() {
      if (this.isRoot() && this.session.unsavedCount() > 0) {
         this.minecraft
            .setScreen(
               new ConfirmScreen(
                  discard -> {
                     if (discard) {
                        this.leaveConfig();
                     } else {
                        this.minecraft.setScreen(this);
                     }
                  },
                  Component.translatable("gui.moonlight.config.discard.title"),
                  Component.translatable("gui.moonlight.config.discard.message", new Object[]{this.session.unsavedCount()}),
                  Component.translatable("gui.moonlight.config.discard.confirm"),
                  CommonComponents.GUI_CANCEL
               )
            );
      } else {
         if (this.isRoot()) {
            this.leaveConfig();
         } else {
            this.minecraft.setScreen(this.parentPage);
         }
      }
   }

   private void leaveConfig() {
      ConfigReloadType reload = this.session.appliedReload();
      if (reload != ConfigReloadType.NONE) {
         Component message = Component.translatable(
            reload == ConfigReloadType.GAME_RESTART ? "gui.moonlight.config.reload_needed.game" : "gui.moonlight.config.reload_needed.world"
         );
         this.minecraft
            .getToasts()
            .addToast(SystemToast.multiline(this.minecraft, RELOAD_TOAST_ID, Component.translatable("gui.moonlight.config.reload_needed.title"), message));
      }

      this.minecraft.setScreen(this.session.returnScreen());
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      GuiHelper.renderHeaderBar(graphics, this.width, 44);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 7, ConfigGuiColors.TITLE);
      graphics.blitSprite(MoonlightIcons.SEARCH, this.searchBox.getX() - 12 - 2, this.searchBox.getY() + 1, 12, 12);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.renderOverlayOrTooltip(graphics, mouseX, mouseY);
   }
}
