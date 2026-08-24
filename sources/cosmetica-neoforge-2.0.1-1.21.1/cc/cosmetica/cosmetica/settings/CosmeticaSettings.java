package cc.cosmetica.cosmetica.settings;

import cc.cosmetica.core.CosmeticaCoreExpectPlatform;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.gui.CosmeticaSettingsScreen;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import gg.cloaks.javaclient.model.CosmeticaUser;
import gg.cloaks.javaclient.model.ExternalCapeSetting;
import gg.cloaks.javaclient.model.Settings;
import gg.cloaks.javaclient.model.UpdateCloudSettingsDto;
import gg.cloaks.javaclient.model.UpdateExternalCapeSettingDto;
import gg.cloaks.javaclient.model.UpdateLocalSettingsDto;
import gg.cloaks.javaclient.model.ExternalCapeSetting.ServiceEnum;
import gg.cloaks.javaclient.model.Settings.TypeEnum;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public final class CosmeticaSettings {
   public static final Setting<Boolean> TOGGLE_OUTFIT_WHEEL = new BooleanSetting("setting.cosmetica.wheel", false, false);
   public static final Setting<Boolean> VERSION_CHECKER = new BooleanSetting("setting.cosmetica.versionChecker", true, true);
   public static final Setting<Boolean> SHOW_OWN_NAMETAG = new BooleanSetting("setting.cosmetica.showOwnNametag", true, true);
   public static final Setting<Boolean> USE_CLOUD_SETTINGS = new BooleanSetting("setting.cosmetica.useCloudSettings", false, true) {
      @Override
      public Component createController() {
         final boolean useCloudSettings = this.get();
         return new Div(new Component[0]) {
            private State<Boolean> disabled = new State(false);

            public List<Component> build() {
               boolean isDisabled = (Boolean)this.disabled.acquire(this);
               return Collections.singletonList(
                  new Button(
                        useCloudSettings ? Text.GUI_YES : Text.GUI_NO,
                        () -> {
                           this.disabled.set(true);
                           CosmeticaSettings.USE_CLOUD_SETTINGS.set(!useCloudSettings);
                           if (useCloudSettings) {
                              if (CosmeticaSettings.modpackSettings == null) {
                                 Logging.getInstance().error("Modpack settings should not be null if cloud settings button is visible!", new Object[0]);
                              } else {
                                 CosmeticaSettings.applyLocalSettings();
                              }
                           } else {
                              CosmeticaAPI.settings()
                                 .requestAsync(api -> api.setCloud(new UpdateCloudSettingsDto()))
                                 .thenAcceptAsync(CosmeticaSettings::updateSettings, Minecraft.getInstance());
                           }
                        }
                     )
                     .setDisabled(isDisabled)
                     .withStyle(
                        Style.create()
                           .set(
                              CommonProperties.TOOLTIP,
                              !isDisabled ? Optional.empty() : Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.updatingSettings", new String[0])))
                           )
                     )
               );
            }
         };
      }
   };
   public static final Setting<Boolean> DISABLE_RSE_PROMPT = new BooleanSetting("setting.cosmetica.disableRSEPrompt", false, true);
   public static final State<String> MODPACK_ID = new State(null);
   public static final Setting<Boolean> USE_MODPACK_ICONS = new BooleanSetting("setting.cosmetica.useModpackIcons", true, true);
   public static final Setting<Boolean> SHOW_ACCESSORIES = new BooleanSetting("setting.cosmetica.showAccessories", true, true);
   public static final Setting<Boolean> SHOW_LORE = new BooleanSetting("setting.cosmetica.showLore", true, true);
   public static final Setting<Boolean> SHOW_SPECIAL_ICONS = new BooleanSetting("setting.cosmetica.showSpecialIcons", true, true)
      .forceWhenOff(USE_MODPACK_ICONS, false);
   public static final Setting<Boolean> SHOW_OFFLINE_ICONS = new BooleanSetting("setting.cosmetica.showOfflineIcons", true, true);
   public static final Setting<Boolean> SHOW_ICONS = new BooleanSetting("setting.cosmetica.showIcons", true, true)
      .forceWhenOff(SHOW_SPECIAL_ICONS, false)
      .forceWhenOff(SHOW_OFFLINE_ICONS, false);
   public static final Setting<Boolean> SHOW_ONLINE_ACTIVITY = new BooleanSetting("setting.cosmetica.showOnlineActivity", true, true);
   public static final Setting<Boolean> VISIBILITY_OVERRIDES = new BooleanSetting("setting.cosmetica.visibilityOverrides", true, true);
   public static final List<Setting<?>> CLIENT_SETTINGS = new ArrayList<>(Arrays.asList(TOGGLE_OUTFIT_WHEEL, SHOW_OWN_NAMETAG, USE_CLOUD_SETTINGS));
   public static final List<Setting<?>> API_SETTINGS = ImmutableList.of(
      SHOW_ACCESSORIES, SHOW_LORE, SHOW_ICONS, SHOW_OFFLINE_ICONS, SHOW_SPECIAL_ICONS, USE_MODPACK_ICONS, SHOW_ONLINE_ACTIVITY, VISIBILITY_OVERRIDES
   );
   public static final State<List<Setting<?>>> DISPLAY_SETTINGS = new State(CLIENT_SETTINGS);
   @Nullable
   private static UpdateLocalSettingsDto modpackSettings;
   public static Setting<List<ExternalCapeSetting>> externalCapeSettings = new Setting<List<ExternalCapeSetting>>("", ImmutableList.of()) {
      @Override
      public Component createController() {
         throw new UnsupportedOperationException("Cannot directly create controller for external cape settings");
      }

      public Text createDescription(List<ExternalCapeSetting> value) {
         throw new UnsupportedOperationException("Cannot directly create description for external cape settings");
      }
   };
   public static final AtomicReference<Runnable> saveLocalSettingsIfNotOnSettingsScreen = new AtomicReference<>(CosmeticaSettingsScreen::saveLocalSettings);
   private static boolean loadedLocal = false;

   private CosmeticaSettings() {
   }

   public static void applyLocalSettings() {
      if (willApplyLocalSettings()) {
         Logging.getInstance().info("Applying pack overrides...", new Object[0]);
         CosmeticaAPI.settings()
            .requestAsync(api -> api.setLocal(modpackSettings))
            .thenAcceptAsync(CosmeticaSettings::updateSettings, Minecraft.getInstance())
            .exceptionally(ex -> {
               Logging.getInstance().error("Failed to apply modpack settings", ex);
               return null;
            });
      } else if (modpackSettings == null) {
         Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Cannot apply pack overrides (modpack settings is null)", new Object[0]);
      } else {
         Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Cannot apply pack overrides (cloud settings is enabled)", new Object[0]);
      }
   }

   public static boolean willApplyLocalSettings() {
      return !USE_CLOUD_SETTINGS.get() && modpackSettings != null;
   }

   public static void refreshLocalSettings() {
      Path localDir = CosmeticaCoreExpectPlatform.getConfigDirectory().resolve("cosmetica");

      try {
         Files.createDirectories(localDir);
      } catch (IOException var9) {
         Logging.getInstance().error("Error creating cosmetica config directory", var9);
      }

      UpdateLocalSettingsDto newModpackSettings = readModpackSettings(localDir);
      if (newModpackSettings != null) {
         modpackSettings = newModpackSettings;
      }

      Path file = localDir.resolve("cosmetica.properties");
      Properties properties = new Properties();
      properties.setProperty("toggle_outfit_wheel", String.valueOf(TOGGLE_OUTFIT_WHEEL.get()));
      properties.setProperty("enable_version_checker", String.valueOf(VERSION_CHECKER.get()));
      properties.setProperty("use_cloud_settings", String.valueOf(USE_CLOUD_SETTINGS.get()));
      properties.setProperty("show_own_nametag", String.valueOf(SHOW_OWN_NAMETAG.get()));
      if (!loadedLocal) {
         try (BufferedReader reader = Files.newBufferedReader(file)) {
            properties.load(reader);
         } catch (NoSuchFileException var13) {
            Logging.getInstance().info("cosmetica.properties does not exist yet", new Object[0]);
         } catch (IOException var14) {
            Logging.getInstance().error("Error reading cosmetica.properties", var14);
            return;
         }

         TOGGLE_OUTFIT_WHEEL.apiUpdate(Boolean.parseBoolean(properties.getProperty("toggle_outfit_wheel", "false")), TypeEnum.CLOUD);
         USE_CLOUD_SETTINGS.apiUpdate(Boolean.parseBoolean(properties.getProperty("use_cloud_settings", "false")), TypeEnum.CLOUD);
         VERSION_CHECKER.apiUpdate(Boolean.parseBoolean(properties.getProperty("enable_version_checker", "true")), TypeEnum.CLOUD);
         SHOW_OWN_NAMETAG.apiUpdate(Boolean.parseBoolean(properties.getProperty("show_own_nametag", "true")), TypeEnum.CLOUD);
         Cosmetics.configureOwnNametag(SHOW_OWN_NAMETAG.get(), false);
         loadedLocal = true;
      }

      try (BufferedWriter writer = Files.newBufferedWriter(file)) {
         Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Saving cosmetica.properties", new Object[0]);
         properties.store(writer, "Per-profile cosmetica settings");
      } catch (IOException var11) {
         Logging.getInstance().error("Error saving cosmetica.properties", var11);
      }
   }

   private static void packManage(UpdateLocalSettingsDto dto, BiConsumer<UpdateLocalSettingsDto, Boolean> updater, JsonObject properties, String name) {
      if (properties.get(name) != null && properties.get(name) != JsonNull.INSTANCE) {
         updater.accept(dto, properties.get(name).getAsBoolean());
      } else {
         Logging.getInstance().error("Missing boolean property \"" + name + "\" in modpack config! Defaulting to false.", new Object[0]);
         updater.accept(dto, false);
      }
   }

   @Nullable
   private static UpdateLocalSettingsDto readModpackSettings(Path parentFolder) {
      USE_CLOUD_SETTINGS.setHidden(true);
      Path modpackSettings = parentFolder.resolve("pack_settings.json");

      try {
         UpdateLocalSettingsDto var28;
         try (BufferedReader reader = Files.newBufferedReader(modpackSettings)) {
            JsonObject properties = (JsonObject)new Gson().fromJson(reader, JsonObject.class);
            String packId = properties.get("modpack_id").getAsString();
            String packName = properties.get("modpack_name").getAsString();
            Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Setting modpack id {}", new Object[]{packId});
            MODPACK_ID.set(packId);
            if (!properties.get("modpack_settings_enabled").getAsBoolean()) {
               Logging.getInstance().info("modpack_settings_enabled=false. Skipping modpack settings", new Object[0]);
               return null;
            }

            USE_CLOUD_SETTINGS.setHidden(false);
            Logging.getInstance().info("Loading modpack overrides for pack {}", new Object[]{MODPACK_ID.peek()});
            UpdateLocalSettingsDto dto = new UpdateLocalSettingsDto();
            dto.setClientName(packName);
            packManage(dto, UpdateLocalSettingsDto::setShowAccessories, properties, "show_accessories");
            packManage(dto, UpdateLocalSettingsDto::setShowLore, properties, "show_lore");
            packManage(dto, UpdateLocalSettingsDto::setShowIcons, properties, "show_icons");
            packManage(dto, UpdateLocalSettingsDto::setShowOfflineIcons, properties, "show_offline_icons");
            packManage(dto, UpdateLocalSettingsDto::setShowSpecialIcons, properties, "show_special_icons");
            packManage(dto, UpdateLocalSettingsDto::setUseModpackIcons, properties, "use_modpack_icons");
            packManage(dto, UpdateLocalSettingsDto::setShowOnlineActivity, properties, "show_online_activity");
            packManage(dto, UpdateLocalSettingsDto::setAllowVisibilityOptionOverrides, properties, "visibility_overrides");
            List<UpdateExternalCapeSettingDto> externalCapeUpdates = new ArrayList<>();

            for (JsonElement element : properties.get("external_capes").getAsJsonArray()) {
               if (element.isJsonObject()) {
                  JsonObject externalCapeSetting = element.getAsJsonObject();
                  UpdateExternalCapeSettingDto dto_ = new UpdateExternalCapeSettingDto();
                  dto_.setService(externalCapeSetting.get("service").getAsString());
                  dto_.setEnabled(externalCapeSetting.get("enabled").getAsBoolean());
                  dto_.setReplace(false);
                  externalCapeUpdates.add(dto_);
               }
            }

            dto.setExternalCapes(externalCapeUpdates);
            dto.setDisableRegionalEffectsPrompt(DISABLE_RSE_PROMPT.get());
            var28 = dto;
         }

         return var28;
      } catch (NoSuchFileException var17) {
         Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Creating/Updating cosmetica pack settings template", new Object[0]);
         JsonObject defaults = new JsonObject();
         defaults.addProperty("modpack_id", "my_modpack");
         defaults.addProperty("modpack_name", "My Modpack");
         defaults.addProperty("modpack_settings_enabled", false);
         defaults.addProperty("show_accessories", SHOW_ACCESSORIES.getUserValue());
         defaults.addProperty("show_lore", SHOW_LORE.getUserValue());
         defaults.addProperty("show_icons", SHOW_ICONS.getUserValue());
         defaults.addProperty("show_offline_icons", SHOW_OFFLINE_ICONS.getUserValue());
         defaults.addProperty("show_special_icons", SHOW_SPECIAL_ICONS.getUserValue());
         defaults.addProperty("use_modpack_icons", USE_MODPACK_ICONS.getUserValue());
         defaults.addProperty("show_online_activity", SHOW_ONLINE_ACTIVITY.getUserValue());
         defaults.addProperty("visibility_overrides", VISIBILITY_OVERRIDES.getUserValue());
         JsonArray arr = new JsonArray();

         for (ServiceEnum service : ServiceEnum.values()) {
            if (service != ServiceEnum.UNKNOWN_DEFAULT_OPEN_API) {
               JsonObject serviceObject = new JsonObject();
               serviceObject.addProperty("service", service.getValue());
               serviceObject.addProperty("enabled", true);
               arr.add(serviceObject);
            }
         }

         defaults.add("external_capes", arr);
         Path modpackSettingsTemplate = parentFolder.resolve("pack_settings.json.disabled");

         try (BufferedWriter writer = Files.newBufferedWriter(modpackSettingsTemplate)) {
            Gson g = new GsonBuilder().setPrettyPrinting().create();
            g.toJson(defaults, writer);
         } catch (IOException var15) {
            Logging.getInstance().error("Error writing pack settings template", var15);
         }
      } catch (IOException var18) {
         Logging.getInstance().error("Error reading pack settings", var18);
      }

      return null;
   }

   public static void clearSettings() {
      Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Clearing loaded settings...", new Object[0]);
      DISPLAY_SETTINGS.set(CLIENT_SETTINGS);
      externalCapeSettings.set(ImmutableList.of());
   }

   public static void updateSettings(CosmeticaUser user) {
      Settings settings = user.getActiveSettings();
      if (settings == null) {
         clearSettings();
      } else {
         Logging.getInstance().debug(CosmeticaLogCategory.SETTINGS, "Updating loaded settings for type " + settings.getType(), new Object[0]);
         SHOW_LORE.apiUpdate(settings.isShowLore(), settings.getType());
         SHOW_ACCESSORIES.apiUpdate(settings.isShowAccessories(), settings.getType());
         SHOW_ICONS.apiUpdate(settings.isShowIcons(), settings.getType());
         SHOW_SPECIAL_ICONS.apiUpdate(settings.isShowSpecialIcons(), settings.getType());
         SHOW_OFFLINE_ICONS.apiUpdate(settings.isShowOfflineIcons(), settings.getType());
         SHOW_ONLINE_ACTIVITY.apiUpdate(settings.isShowOnlineActivity(), settings.getType());
         USE_MODPACK_ICONS.apiUpdate(settings.isUseModpackIcons(), settings.getType());
         DISABLE_RSE_PROMPT.apiUpdate(settings.isDisableRegionalEffectsPrompt(), settings.getType());
         VISIBILITY_OVERRIDES.apiUpdate(settings.isAllowVisibilityOptionOverrides(), settings.getType());
         boolean differentModpackId = MODPACK_ID.peek() != null && user.getModpackId() != null && !user.getModpackId().equals(MODPACK_ID.peek());
         USE_CLOUD_SETTINGS.setSuperHidden(differentModpackId);
         if (!differentModpackId && MODPACK_ID.peek() != null && settings.getType() == TypeEnum.CLOUD) {
            USE_CLOUD_SETTINGS.set(true);
            saveLocalSettingsIfNotOnSettingsScreen.get().run();
         }

         List<Setting<?>> loggedInSettings = new ArrayList<>(CLIENT_SETTINGS);
         loggedInSettings.addAll(API_SETTINGS);
         DISPLAY_SETTINGS.set(loggedInSettings);
         externalCapeSettings.apiUpdate(settings.getExternalCapes(), settings.getType());
      }
   }
}
