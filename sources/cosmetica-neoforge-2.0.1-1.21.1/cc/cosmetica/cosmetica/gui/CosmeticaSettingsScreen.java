package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.gui.widget.MenuEndSelection;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.settings.Setting;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.AbstractScrollContainer.ScrollbarPosition;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.gui.style.Style.MutableStyle;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import com.google.common.collect.ImmutableList;
import gg.cloaks.javaclient.api.UsersApi;
import gg.cloaks.javaclient.model.CosmeticaUser;
import gg.cloaks.javaclient.model.PlayerResponse;
import gg.cloaks.javaclient.model.UpdateCloudSettingsDto;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class CosmeticaSettingsScreen extends Screen {
   private final State<List<Setting<?>>> settings;
   public static final ResourceKey SETTINGS_SCREEN = new ResourceKey("cosmetica", "settings");

   public CosmeticaSettingsScreen(ResourceKey titleKey, State<List<Setting<?>>> settings) {
      super(titleKey);
      this.settings = settings;
   }

   protected Component[] buildScreen() {
      CosmeticaSettings.saveLocalSettingsIfNotOnSettingsScreen.set(() -> {});
      Component[] settings = ((List)this.settings.acquire(this))
         .stream()
         .filter(Setting::isVisible)
         .map(CosmeticaSettingsScreen.SettingBlock::new)
         .map(c -> c.tag(new String[]{"setting-block"}))
         .toArray(Component[]::new);
      return new Component[]{
         new Div(
               new Component[]{
                  new Div(settings)
                     .withStyle(
                        Style.create()
                           .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(5, 0, 2, 0)))
                           .set(CommonProperties.FLEX, 1)
                           .set(CommonProperties.MIN_HEIGHT, CommonProperties.fixedSize(0))
                           .set(Div.SCROLLBAR_POSITION, ScrollbarPosition.OUTSIDE)
                     ),
                  new MenuEndSelection()
                     .withStyle(
                        Style.create()
                           .set(CommonProperties.FLEX_SHRINK, 0)
                           .set(CommonProperties.MARGINS, CommonProperties.fixed(Margins.NONE))
                           .set(CommonProperties.MIN_WIDTH, CommonProperties.fixedSize(220))
                     )
               }
            )
            .withStyle(
               Style.create()
                  .set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
                  .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(5, 0, 0, 0)))
                  .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(20, 0)))
                  .set(CommonProperties.MIN_HEIGHT, CommonProperties.screen(0.0F, 100.0F))
            )
      };
   }

   @NotNull
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .tag(
            "setting-block",
            Style.create().set(CommonProperties.MIN_WIDTH, CommonProperties.fixedSize(200)).set(CommonProperties.WIDTH, CommonProperties.screen(50.0F, 0.0F))
         );
   }

   public void unmount() {
      saveLocalSettings();
      CosmeticaSettings.saveLocalSettingsIfNotOnSettingsScreen.set(CosmeticaSettingsScreen::saveLocalSettings);
      Cosmetics.configureOwnNametag(CosmeticaSettings.SHOW_OWN_NAMETAG.get(), false);
      this.updateCloudSettings();
   }

   public static void saveLocalSettings() {
      boolean modifiedLocal = CosmeticaSettings.CLIENT_SETTINGS.stream().anyMatch(Setting::isModified);
      if (modifiedLocal) {
         CosmeticaSettings.refreshLocalSettings();
         CosmeticaSettings.CLIENT_SETTINGS.forEach(Setting::clean);
      }
   }

   private void updateCloudSettings() {
      boolean modifiedApi = CosmeticaSettings.API_SETTINGS.stream().anyMatch(Setting::isModified);
      if (modifiedApi) {
         UpdateCloudSettingsDto dto = new UpdateCloudSettingsDto();
         if (CosmeticaSettings.SHOW_ACCESSORIES.isModified()) {
            dto.setShowAccessories(CosmeticaSettings.SHOW_ACCESSORIES.getUserValue());
            CosmeticaSettings.SHOW_ACCESSORIES.clean();
         }

         if (CosmeticaSettings.SHOW_LORE.isModified()) {
            dto.setShowLore(CosmeticaSettings.SHOW_LORE.getUserValue());
            CosmeticaSettings.SHOW_LORE.clean();
         }

         if (CosmeticaSettings.SHOW_ICONS.isModified()) {
            dto.setShowIcons(CosmeticaSettings.SHOW_ICONS.getUserValue());
            CosmeticaSettings.SHOW_ICONS.clean();
         }

         if (CosmeticaSettings.SHOW_OFFLINE_ICONS.isModified()) {
            dto.setShowOfflineIcons(CosmeticaSettings.SHOW_OFFLINE_ICONS.getUserValue());
            CosmeticaSettings.SHOW_OFFLINE_ICONS.clean();
         }

         if (CosmeticaSettings.SHOW_SPECIAL_ICONS.isModified()) {
            dto.setShowSpecialIcons(CosmeticaSettings.SHOW_SPECIAL_ICONS.getUserValue());
            CosmeticaSettings.SHOW_SPECIAL_ICONS.clean();
         }

         if (CosmeticaSettings.SHOW_ONLINE_ACTIVITY.isModified()) {
            dto.setShowOnlineActivity(CosmeticaSettings.SHOW_ONLINE_ACTIVITY.getUserValue());
            CosmeticaSettings.SHOW_ONLINE_ACTIVITY.clean();
         }

         CosmeticaAPI.settings().requestAsync(api -> api.setCloud(dto)).thenAcceptAsync(user -> {
            updateCosmeticsAndSettings(user);
            Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Updated settings to /cloud", new Object[0]);
         }, Minecraft.getInstance()).exceptionally(e -> {
            Logging.getInstance().error("Failed to update settings", e);
            return null;
         });
      }
   }

   static void updateCosmeticsAndSettings(CosmeticaUser user) {
      if (user.getExternalCape() != null || user.getOutfit() != null && user.getOutfit().getElytra() != null && user.getOutfit().getCloak() != null) {
         SelfCosmeticManager.update(new PlayerResponse().user(user).isUser(true));
      } else {
         CosmeticaSettings.updateSettings(user);
         CosmeticaAPI.users().requestAsync(UsersApi::getSelf).thenAcceptAsync(user_ -> {
            SelfCosmeticManager.update(new PlayerResponse().user(user_).isUser(true));
            Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Updated own external capes", new Object[0]);
         }, Minecraft.getInstance()).exceptionally(ex -> {
            Logging.getInstance().error("Error fetching own cosmetics", ex);
            return null;
         });
      }
   }

   private static class SettingBlock<T> extends Div {
      private final Setting<T> setting;

      private SettingBlock(Setting<T> setting) {
         super(new Component[0]);
         this.setting = setting;
      }

      public List<Component> build() {
         T value = this.setting.acquire(this);
         Text text = this.setting.name;
         if (this.setting.isModified()) {
            text = Text.literal(text.getDisplayString() + "*");
         }

         Component main = new Div(new Component[]{new Label(text), this.setting.createController().tag(new String[]{"controller"})})
            .tag(new String[]{"setting-display"});
         return this.setting.hasDescription()
            ? ImmutableList.of(main, new Label(this.setting.createDescription(value)).tag(new String[]{"setting-description"}))
            : ImmutableList.of(main);
      }

      public Stylesheet getStylesheet() {
         MutableStyle style = Style.create().set(CommonProperties.FLEX_SHRINK, 0).set(Div.ALIGN_ITEMS, Align.STRETCH_CENTRE);
         if (this.setting.hasDescription()) {
            style.set(CommonProperties.HEIGHT, CommonProperties.fixedSize(50));
         }

         return new Stylesheet()
            .tag("controller", Style.create().set(CommonProperties.FLEX_SHRINK, 0).set(CommonProperties.WIDTH, CommonProperties.fixed(OptionalInt.of(100))))
            .tag("setting-display", Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X).set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN))
            .tag("setting-description", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(1, 0, 0, 0))))
            .self(style);
      }
   }
}
