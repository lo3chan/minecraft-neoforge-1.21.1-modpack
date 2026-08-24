package traben.entity_texture_features.config;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.tconfig.TConfig;
import traben.tconfig.gui.entries.TConfigEntry;
import traben.tconfig.gui.entries.TConfigEntryBoolean;
import traben.tconfig.gui.entries.TConfigEntryCategory;
import traben.tconfig.gui.entries.TConfigEntryCustomButton;
import traben.tconfig.gui.entries.TConfigEntryCustomScreenOpener;
import traben.tconfig.gui.entries.TConfigEntryEnumButton;
import traben.tconfig.gui.entries.TConfigEntryEnumSlider;
import traben.tconfig.gui.entries.TConfigEntryInt;
import traben.tconfig.gui.entries.TConfigEntryText;

public final class ETFConfig extends TConfig {
   @Deprecated(
      forRemoval = true
   )
   public boolean optifine_limitRandomVariantGapsBy10 = false;
   public boolean optifine_allowWeirdSkipsInTrueRandom = true;
   public boolean optifine_preventBaseTextureInOptifineDirectory = true;
   public ETFConfig.IllegalPathMode illegalPathSupportMode = ETFConfig.IllegalPathMode.None;
   public boolean enableCustomTextures = true;
   public boolean enableCustomBlockEntities = true;
   public ETFConfig.UpdateFrequency textureUpdateFrequency_V2 = ETFConfig.UpdateFrequency.Fast;
   public boolean enableEmissiveTextures = true;
   public boolean enableEnchantedTextures = true;
   public boolean enableEmissiveBlockEntities = true;
   public ETFConfig.EmissiveRenderModes emissiveRenderMode = ETFConfig.EmissiveRenderModes.DULL;
   public boolean alwaysCheckVanillaEmissiveSuffix = true;
   public boolean enableArmorAndTrims = true;
   public boolean skinFeaturesEnabled = true;
   public ETFConfig.SkinTransparencyMode skinTransparencyMode = ETFConfig.SkinTransparencyMode.ETF_SKINS_ONLY;
   public boolean skinTransparencyInExtraPixels = true;
   @Deprecated(
      forRemoval = true
   )
   public boolean skinFeaturesEnableTransparency = true;
   @Deprecated(
      forRemoval = true
   )
   public boolean skinFeaturesEnableFullTransparency = false;
   @Deprecated(
      forRemoval = true
   )
   public boolean tryETFTransparencyForAllSkins = false;
   public boolean enableEnemyTeamPlayersSkinFeatures = true;
   public boolean enableBlinking = true;
   public int blinkFrequency = 150;
   public int blinkLength = 1;
   public double advanced_IncreaseCacheSizeModifier = 1.0;
   public ETFConfig.DebugLogMode debugLoggingMode = ETFConfig.DebugLogMode.None;
   public boolean logTextureDataInitialization = false;
   @Deprecated(
      forRemoval = true
   )
   public boolean hideConfigButton = false;
   public ETFConfig.SettingsButtonLocation configButtonLoc = ETFConfig.SettingsButtonLocation.BOTTOM_RIGHT;
   public boolean disableVanillaDirectoryVariantTextures = false;
   public boolean use3DSkinLayerPatch = true;
   public boolean enableFullBodyWardenTextures = true;
   public ETFConfig.String2BooleanNullMap entityEmissiveOverrides = new ETFConfig.String2BooleanNullMap();
   public HashSet<String> propertiesDisabled = new HashSet<>();
   public HashSet<String> propertyInvertUpdatingOverrides = new HashSet<>();
   public ETFConfig.String2BooleanNullMap entityRandomOverrides = new ETFConfig.String2BooleanNullMap();
   public ETFConfig.String2EnumNullMap<ETFConfig.EmissiveRenderModes> entityEmissiveBrightOverrides = new ETFConfig.String2EnumNullMap<>();
   public ETFConfig.String2EnumNullMap<ETFConfig.RenderLayerOverride> entityRenderLayerOverrides = new ETFConfig.String2EnumNullMap<>();
   public HashMap<String, Integer> entityLightOverrides = new HashMap<>();

   public boolean isPropertyDisabled(@NotNull RandomProperties.RandomPropertyFactory property) {
      return this.propertiesDisabled.contains(property.getPropertyId());
   }

   public boolean canPropertyUpdate(@NotNull RandomProperties.RandomPropertyFactory property) {
      return this.propertyInvertUpdatingOverrides.contains(property.getPropertyId()) != property.updatesOverTime();
   }

   public boolean canDoCustomTextures() {
      if (!this.entityRandomOverrides.isEmpty() && ETFRenderContext.getCurrentEntityState() != null) {
         String key = ETFRenderContext.getCurrentEntityState().entityKey();
         return key != null && this.entityRandomOverrides.containsKey(key) ? this.entityRandomOverrides.getOrDefault(key, false) : this.enableCustomTextures;
      } else {
         return this.enableCustomTextures;
      }
   }

   public boolean canDoEmissiveTextures() {
      if (!this.entityEmissiveOverrides.isEmpty() && ETFRenderContext.getCurrentEntityState() != null) {
         String key = ETFRenderContext.getCurrentEntityState().entityKey();
         return key != null && this.entityEmissiveOverrides.containsKey(key)
            ? this.entityEmissiveOverrides.getOrDefault(key, false)
            : this.enableEmissiveTextures;
      } else {
         return this.enableEmissiveTextures;
      }
   }

   public ETFConfig.EmissiveRenderModes getEmissiveRenderMode() {
      if (!this.entityEmissiveBrightOverrides.isEmpty() && ETFRenderContext.getCurrentEntityState() != null) {
         String key = ETFRenderContext.getCurrentEntityState().entityKey();
         return key != null && this.entityEmissiveBrightOverrides.containsKey(key) ? this.entityEmissiveBrightOverrides.get(key) : this.emissiveRenderMode;
      } else {
         return this.emissiveRenderMode;
      }
   }

   public ETFConfig.RenderLayerOverride getRenderLayerOverride() {
      if (!this.entityRenderLayerOverrides.isEmpty() && ETFRenderContext.getCurrentEntityState() != null) {
         String key = ETFRenderContext.getCurrentEntityState().entityKey();
         return key != null && this.entityRenderLayerOverrides.containsKey(key) ? this.entityRenderLayerOverrides.get(key) : null;
      } else {
         return null;
      }
   }

   public int getLightOverride(Entity entity, float tickDelta, int light) {
      if (!this.entityLightOverrides.isEmpty() && entity != null) {
         String key = ((ETFEntity)entity).etf$getEntityKey();
         if (key != null && this.entityLightOverrides.containsKey(key)) {
            int lightETF = Mth.clamp(this.entityLightOverrides.get(key), 0, 15);
            BlockPos pos = BlockPos.containing(entity.getLightProbePosition(tickDelta));
            int block = entity.level().getBrightness(LightLayer.SKY, pos);
            int sky = entity.isOnFire() ? 15 : entity.level().getBrightness(LightLayer.BLOCK, pos);
            return ETFUtils2.packLight(Math.max(block, sky), lightETF);
         } else {
            return light;
         }
      } else {
         return light;
      }
   }

   public int getLightOverrideBE(int light) {
      return this.getLightOverrideBE(light, ETFRenderContext.getCurrentEntityState());
   }

   public int getLightOverrideBE(int light, @Nullable ETFEntityRenderState state) {
      if (!this.entityLightOverrides.isEmpty() && state != null) {
         String key = state.entityKey();
         if (key != null && this.entityLightOverrides.containsKey(key)) {
            int lightETF = Mth.clamp(this.entityLightOverrides.get(key), 0, 15);
            Level world = state.world();
            BlockPos pos = state.blockPos();
            if (world != null && pos != null) {
               int block = world.getBrightness(LightLayer.BLOCK, pos);
               int sky = world.getBrightness(LightLayer.SKY, pos);
               return ETFUtils2.packLight(Math.max(block, sky), lightETF);
            } else {
               return light;
            }
         } else {
            return light;
         }
      } else {
         return light;
      }
   }

   @Override
   public TConfigEntryCategory getGUIOptions() {
      return new TConfigEntryCategory.Empty()
         .add(
            new TConfigEntry[]{
               new TConfigEntryCategory("config.entity_features.textures_main")
                  .add(
                     new TConfigEntryCategory("config.entity_texture_features.random_settings.title")
                        .add(
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.enable_custom_textures.title",
                              "config.entity_texture_features.enable_custom_textures.tooltip",
                              () -> this.enableCustomTextures,
                              aBoolean -> this.enableCustomTextures = aBoolean,
                              true
                           ),
                           new TConfigEntryEnumSlider<>(
                              "config.entity_texture_features.texture_update_frequency.title",
                              "config.entity_texture_features.texture_update_frequency.tooltip",
                              () -> this.textureUpdateFrequency_V2,
                              updateFrequency -> this.textureUpdateFrequency_V2 = updateFrequency,
                              ETFConfig.UpdateFrequency.Fast
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.custom_block_entity.title",
                              "config.entity_texture_features.custom_block_entity.tooltip",
                              () -> this.enableCustomBlockEntities,
                              aBoolean -> this.enableCustomBlockEntities = aBoolean,
                              true
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.disable_default_directory.title",
                              "config.entity_texture_features.disable_default_directory.tooltip",
                              () -> this.disableVanillaDirectoryVariantTextures,
                              aBoolean -> this.disableVanillaDirectoryVariantTextures = aBoolean,
                              false
                           )
                        ),
                     new TConfigEntryCategory("config.entity_texture_features.emissive_settings.title")
                        .add(
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.enable_emissive_textures.title",
                              "config.entity_texture_features.enable_emissive_textures.tooltip",
                              () -> this.enableEmissiveTextures,
                              aBoolean -> this.enableEmissiveTextures = aBoolean,
                              true
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.emissive_block_entity.title",
                              "config.entity_texture_features.emissive_block_entity.tooltip",
                              () -> this.enableEmissiveBlockEntities,
                              aBoolean -> this.enableEmissiveBlockEntities = aBoolean,
                              true
                           ),
                           new TConfigEntryEnumButton<>(
                              "config.entity_texture_features.emissive_mode.title",
                              "config.entity_texture_features.emissive_mode.tooltip",
                              () -> this.emissiveRenderMode,
                              renderMode -> this.emissiveRenderMode = renderMode,
                              ETFConfig.EmissiveRenderModes.DULL
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.always_check_vanilla_emissive_suffix.title",
                              "config.entity_texture_features.always_check_vanilla_emissive_suffix.tooltip",
                              () -> this.alwaysCheckVanillaEmissiveSuffix,
                              aBoolean -> this.alwaysCheckVanillaEmissiveSuffix = aBoolean,
                              true
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.armor_enable",
                              "config.entity_texture_features.armor_enable.tooltip",
                              () -> this.enableArmorAndTrims,
                              aBoolean -> this.enableArmorAndTrims = aBoolean,
                              true
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.enchanted_enable",
                              "config.entity_texture_features.enchanted_enable.tooltip",
                              () -> this.enableEnchantedTextures,
                              aBoolean -> this.enableEnchantedTextures = aBoolean,
                              true
                           )
                        ),
                     new TConfigEntryCategory("config.entity_texture_features.player_skin_settings.title")
                        .add(
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.player_skin_features.title",
                              "config.entity_texture_features.player_skin_features.tooltip",
                              () -> this.skinFeaturesEnabled,
                              aBoolean -> this.skinFeaturesEnabled = aBoolean,
                              true
                           ),
                           new TConfigEntryEnumButton<>(
                              "config.entity_texture_features.transparent_skins.title",
                              "config.entity_texture_features.transparent_skins.tooltip",
                              () -> this.skinTransparencyMode,
                              mode -> this.skinTransparencyMode = mode,
                              ETFConfig.SkinTransparencyMode.ETF_SKINS_ONLY
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.transparent_skins_extra.title",
                              "config.entity_texture_features.transparent_skins_extra.tooltip",
                              () -> this.skinTransparencyInExtraPixels,
                              aBoolean -> this.skinTransparencyInExtraPixels = aBoolean,
                              true
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.enable_enemy_team_players_skin_features.title",
                              "config.entity_texture_features.enable_enemy_team_players_skin_features.tooltip",
                              () -> this.enableEnemyTeamPlayersSkinFeatures,
                              aBoolean -> this.enableEnemyTeamPlayersSkinFeatures = aBoolean,
                              true
                           ),
                           ETF.SKIN_LAYERS_DETECTED
                              ? new TConfigEntryBoolean(
                                 "config.entity_texture_features.skin_layers_patch.title",
                                 "config.entity_texture_features.skin_layers_patch.tooltip",
                                 () -> this.use3DSkinLayerPatch,
                                 aBoolean -> this.use3DSkinLayerPatch = aBoolean,
                                 true
                              )
                              : null,
                           this.getPlayerSkinEditorButton()
                        ),
                     new TConfigEntryCategory("config.entity_texture_features.blinking_mob_settings_sub.title")
                        .add(
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.blinking_mob_settings.title",
                              "config.entity_texture_features.blinking_mob_settings.tooltip",
                              () -> this.enableBlinking,
                              aBoolean -> this.enableBlinking = aBoolean,
                              true
                           ),
                           new TConfigEntryInt(
                              "config.entity_texture_features.blink_frequency.title",
                              "config.entity_texture_features.blink_frequency.tooltip",
                              () -> this.blinkFrequency,
                              aInt -> this.blinkFrequency = aInt,
                              150,
                              1,
                              1024
                           ),
                           new TConfigEntryInt(
                              "config.entity_texture_features.blink_length.title",
                              "config.entity_texture_features.blink_length.tooltip",
                              () -> this.blinkLength,
                              aInt -> this.blinkLength = aInt,
                              1,
                              1,
                              20
                           )
                        ),
                     new TConfigEntryCategory("config.entity_texture_features.debug_screen.title")
                        .add(
                           new TConfigEntryEnumButton<>(
                              "config.entity_texture_features.debug_logging_mode.title",
                              "config.entity_texture_features.debug_logging_mode.tooltip",
                              () -> this.debugLoggingMode,
                              debugLogMode -> this.debugLoggingMode = debugLogMode,
                              ETFConfig.DebugLogMode.None
                           ),
                           new TConfigEntryBoolean(
                              "config.entity_texture_features.log_creation",
                              "config.entity_texture_features.log_creation.tooltip",
                              () -> this.logTextureDataInitialization,
                              aBoolean -> this.logTextureDataInitialization = aBoolean,
                              false
                           ),
                           new TConfigEntryCustomButton(
                              "config.entity_texture_features.debug_screen.mass_log",
                              "config.entity_texture_features.debug_screen.mass_log.tooltip",
                              button -> {
                                 ETFManager.getInstance().doTheBigBoyPrintoutKronk();
                                 button.setMessage(ETF.getTextFromTranslation("config.entity_texture_features.debug_screen.mass_log.done"));
                                 button.active = false;
                              }
                           )
                        )
                  ),
               new TConfigEntryCategory("config.entity_features.general_settings.title")
                  .add(
                     new TConfigEntryEnumButton<>(
                        "config.entity_texture_features.allow_illegal_texture_paths.title",
                        "config.entity_texture_features.allow_illegal_texture_paths.tooltip",
                        () -> this.illegalPathSupportMode,
                        illegalPathMode -> this.illegalPathSupportMode = illegalPathMode,
                        ETFConfig.IllegalPathMode.None
                     ),
                     new TConfigEntryBoolean(
                        "config.entity_texture_features.warden.title",
                        "config.entity_texture_features.warden.tooltip",
                        () -> this.enableFullBodyWardenTextures,
                        aBoolean -> this.enableFullBodyWardenTextures = aBoolean,
                        true
                     ),
                     new TConfigEntryEnumButton<>(
                        "config.entity_texture_features.settings_button_loc.title",
                        "config.entity_texture_features.settings_button_loc.tooltip",
                        () -> this.configButtonLoc,
                        settingsButtonLocation -> this.configButtonLoc = settingsButtonLocation,
                        ETFConfig.SettingsButtonLocation.BOTTOM_RIGHT
                     )
                  ),
               new TConfigEntryCategory("config.entity_texture_features.restrict_update_properties2").addAll(this.getPropertySettings()),
               this.getEntitySettings(),
               new TConfigEntryCategory("config.entity_features.optifine_settings", "config.entity_texture_features.optifine.desc")
                  .addAll(
                     TConfigEntryText.fromLongOrMultilineTranslation("config.entity_texture_features.optifine.desc", 200, TConfigEntryText.TextAlignment.LEFT)
                  )
                  .add(
                     new TConfigEntryBoolean(
                        "config.entity_texture_features.optifine.random_skips.title",
                        "config.entity_texture_features.optifine.random_skips.tooltip",
                        () -> this.optifine_allowWeirdSkipsInTrueRandom,
                        aBoolean -> this.optifine_allowWeirdSkipsInTrueRandom = aBoolean,
                        true
                     ),
                     new TConfigEntryBoolean(
                        "config.entity_texture_features.optifine.prevent_base.title",
                        "config.entity_texture_features.optifine.prevent_base.tooltip",
                        () -> this.optifine_preventBaseTextureInOptifineDirectory,
                        aBoolean -> this.optifine_preventBaseTextureInOptifineDirectory = aBoolean,
                        true
                     )
                  )
            }
         );
   }

   private TConfigEntryCategory getEntitySettings() {
      TConfigEntryCategory category = new TConfigEntryCategory("config.entity_features.per_entity_settings");

      try {
         BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            if (entityType != EntityType.PLAYER) {
               String translationKey = entityType.getDescriptionId();
               TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey);
               this.addEntityConfigs(entityCategory, translationKey);
               category.add(entityCategory);
            }
         });
         TConfigEntryText warn = new TConfigEntryText("config.entity_features.per_entity_settings.blocks");
         TConfigEntryText warn2 = new TConfigEntryText("config.entity_features.per_entity_settings.blocks2");
         category.add(warn, warn2);
         BlockEntityRenderers.PROVIDERS.keySet().forEach(entityType -> {
            String translationKey = ETFApi.getBlockEntityTypeToTranslationKey(entityType);
            TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey).add(warn, warn2);
            this.addEntityConfigs(entityCategory, translationKey);
            category.add(entityCategory);
         });
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return category;
   }

   private void addEntityConfigs(TConfigEntryCategory entityCategory, String translationKey) {
      entityCategory.add(
         new TConfigEntryCategory("config.entity_features.textures_main")
            .add(
               new TConfigEntryEnumButton<>(
                  "config.entity_texture_features.enable_emissive_textures.title",
                  "config.entity_texture_features.enable_emissive_textures.tooltip",
                  () -> this.entityEmissiveOverrides.getNullable(translationKey),
                  overrideBooleanType -> this.entityEmissiveOverrides.putNullable(translationKey, overrideBooleanType),
                  null,
                  ETFConfig.OverrideBooleanType.class
               ),
               new TConfigEntryEnumButton<>(
                  "config.entity_texture_features.enable_custom_textures.title",
                  "config.entity_texture_features.enable_custom_textures.tooltip",
                  () -> this.entityRandomOverrides.getNullable(translationKey),
                  overrideBooleanType -> this.entityRandomOverrides.putNullable(translationKey, overrideBooleanType),
                  null,
                  ETFConfig.OverrideBooleanType.class
               ),
               new TConfigEntryEnumButton<>(
                  "config.entity_texture_features.emissive_mode.title",
                  "config.entity_texture_features.emissive_mode.tooltip",
                  () -> this.entityEmissiveBrightOverrides.get(translationKey),
                  mode -> this.entityEmissiveBrightOverrides.putNullable(translationKey, mode),
                  null,
                  ETFConfig.EmissiveRenderModes.class
               ),
               new TConfigEntryEnumButton<>(
                  "config.entity_features.per_entity_settings.layer",
                  "config.entity_features.per_entity_settings.layer.tooltip",
                  () -> this.entityRenderLayerOverrides.get(translationKey),
                  layer -> this.entityRenderLayerOverrides.putNullable(translationKey, layer),
                  null,
                  ETFConfig.RenderLayerOverride.class
               )
            ),
         new TConfigEntryInt(
            "config.entity_features.per_entity_settings.light",
            "config.entity_features.per_entity_settings.light.tooltip",
            () -> this.entityLightOverrides.getOrDefault(translationKey, -1),
            light -> {
               if (light == -1) {
                  this.entityLightOverrides.remove(translationKey);
               } else {
                  this.entityLightOverrides.put(translationKey, light);
               }
            },
            -1,
            -1,
            15,
            true,
            false
         )
      );
   }

   private List<TConfigEntry> getPropertySettings() {
      ArrayList<TConfigEntry> list = new ArrayList<>();
      RandomProperties.forEachProperty(
         propertySettings -> {
            boolean defaultNoUpdate = !propertySettings.updatesOverTime();
            String id = propertySettings.getPropertyId();
            TConfigEntryCategory category = new TConfigEntryCategory(id);
            list.add(category);
            category.add(
                  new TConfigEntryBoolean(
                     "config.entity_texture_features.restrict_update_properties.allow",
                     "config.entity_texture_features.restrict_update_properties.allow.tooltip",
                     () -> !this.propertiesDisabled.contains(id),
                     aBoolean -> {
                        if (aBoolean) {
                           this.propertiesDisabled.remove(id);
                        } else {
                           this.propertiesDisabled.add(id);
                        }
                     },
                     true
                  ),
                  new TConfigEntryBoolean(
                     "config.entity_texture_features.restrict_update_properties.lock",
                     "config.entity_texture_features.restrict_update_properties.lock.tooltip",
                     () -> this.propertyInvertUpdatingOverrides.contains(id) != defaultNoUpdate,
                     aBoolean -> {
                        if (aBoolean != defaultNoUpdate) {
                           this.propertyInvertUpdatingOverrides.add(id);
                        } else {
                           this.propertyInvertUpdatingOverrides.remove(id);
                        }
                     },
                     defaultNoUpdate
                  )
               )
               .addAll(
                  TConfigEntryText.fromLongOrMultilineTranslation(propertySettings.getExplanationTranslationKey(), 200, TConfigEntryText.TextAlignment.LEFT)
               );
         }
      );
      return list;
   }

   private TConfigEntry getPlayerSkinEditorButton() {
      boolean condition1 = ETF.config().getConfig().skinFeaturesEnabled;
      boolean condition2 = !ETF.isFabric() || ETF.FABRIC_API;
      boolean condition3 = Minecraft.getInstance().player != null;
      boolean condition4 = ETFPlayerTexture.clientPlayerOriginalSkinImageForTool != null;
      boolean canLaunchSkinTool = condition1 && condition2 && condition3 && condition4;
      StringBuilder reasonText = new StringBuilder();
      if (!canLaunchSkinTool) {
         reasonText.append(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_0").getString());
         if (!condition1) {
            reasonText.append(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_1").getString());
         }

         if (!condition2) {
            reasonText.append(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_2").getString());
         }

         if (!condition3) {
            reasonText.append(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_3").getString());
         }

         if (!condition4) {
            reasonText.append(ETF.getTextFromTranslation("config.entity_texture_features.player_skin_editor.reason_4").getString());
         }
      }

      return (TConfigEntry)(canLaunchSkinTool
         ? new TConfigEntryCustomScreenOpener(
            "config.entity_texture_features.player_skin_editor.button.enabled",
            reasonText.toString(),
            () -> new ETFConfigScreenSkinTool(Minecraft.getInstance().screen),
            false
         )
         : new TConfigEntryCustomScreenOpener(
               "config.entity_texture_features.player_skin_editor.button.disabled",
               reasonText.toString(),
               () -> new ETFConfigScreenSkinTool(Minecraft.getInstance().screen),
               false
            )
            .setEnabled(false));
   }

   @Override
   public ResourceLocation getModIcon() {
      return ETFUtils2.res("entity_texture_features", "textures/gui/etf.png");
   }

   public static enum DebugLogMode {
      None("config.entity_texture_features.Debug_log_mode.none"),
      Log("config.entity_texture_features.Debug_log_mode.log"),
      Chat("config.entity_texture_features.Debug_log_mode.chat");

      private final String key;

      private DebugLogMode(@Translatable String key) {
         this.key = key;
      }

      @Override
      public String toString() {
         return ETF.getTextFromTranslation(this.key).getString();
      }
   }

   public static enum EmissiveRenderModes {
      DULL,
      BRIGHT;

      @Override
      public String toString() {
         return switch (this) {
            case DULL -> ETF.getTextFromTranslation("config.entity_texture_features.emissive_mode.dull").getString();
            case BRIGHT -> ETF.getTextFromTranslation("config.entity_texture_features.emissive_mode.bright").getString();
         };
      }
   }

   public static enum IllegalPathMode {
      None("options.off"),
      Entity("config.entity_texture_features.illegal_path_mode.entity"),
      All("config.entity_texture_features.illegal_path_mode.all");

      private final String key;

      private IllegalPathMode(@Translatable String key) {
         this.key = key;
      }

      @Override
      public String toString() {
         return ETF.getTextFromTranslation(this.key).getString();
      }
   }

   public static enum OverrideBooleanType {
      TRUE,
      FALSE;

      @Override
      public String toString() {
         return this == TRUE ? "ON" : "OFF";
      }
   }

   public static enum RenderLayerOverride {
      TRANSLUCENT("config.entity_texture_features.render_layer.translucent"),
      TRANSLUCENT_CULL("config.entity_texture_features.render_layer.translucent_cull"),
      END("config.entity_texture_features.render_layer.end"),
      OUTLINE("config.entity_texture_features.render_layer.outline");

      private final String key;

      private RenderLayerOverride(@Translatable String key) {
         this.key = key;
      }

      @Override
      public String toString() {
         return ETF.getTextFromTranslation(this.key).getString();
      }
   }

   public static enum SettingsButtonLocation {
      OFF,
      BOTTOM_RIGHT,
      TOP_RIGHT,
      TOP_LEFT,
      BOTTOM_LEFT;
   }

   public static enum SkinTransparencyMode {
      VANILLA("config.entity_texture_features.transparent_skins.vanilla"),
      ETF_SKINS_ONLY("config.entity_texture_features.transparent_skins.etf"),
      ALL("config.entity_texture_features.transparent_skins.all");

      private final String key;

      private SkinTransparencyMode(@Translatable String key) {
         this.key = key;
      }

      @Override
      public String toString() {
         return ETF.getTextFromTranslation(this.key).getString();
      }
   }

   public static class String2BooleanNullMap extends HashMap<String, Boolean> {
      public void putNullable(String s, ETFConfig.OverrideBooleanType v) {
         if (v == null) {
            this.remove(s);
         } else {
            this.put(s, v == ETFConfig.OverrideBooleanType.TRUE);
         }
      }

      public ETFConfig.OverrideBooleanType getNullable(String s) {
         if (this.getOrDefault(s, false)) {
            return ETFConfig.OverrideBooleanType.TRUE;
         } else {
            return this.containsKey(s) ? ETFConfig.OverrideBooleanType.FALSE : null;
         }
      }
   }

   public static class String2EnumNullMap<E extends Enum<E>> extends HashMap<String, E> {
      public void putNullable(String s, E v) {
         if (v == null) {
            this.remove(s);
         } else {
            this.put(s, v);
         }
      }

      @Deprecated
      public E getNullable(String s) {
         return this.get(s);
      }
   }

   public static enum UpdateFrequency {
      Never(-1, "config.entity_texture_features.update_frequency.never"),
      Slow(80, "config.entity_texture_features.update_frequency.slow"),
      Average(20, "config.entity_texture_features.update_frequency.average"),
      Fast(5, "config.entity_texture_features.update_frequency.fast"),
      Instant(1, "config.entity_texture_features.update_frequency.instant");

      private final int delay;
      private final String key;

      private UpdateFrequency(int delay, @Translatable String key) {
         this.delay = delay;
         this.key = key;
      }

      public int getDelay() {
         return this.delay;
      }

      @Override
      public String toString() {
         return ETF.getTextFromTranslation(this.key).getString();
      }
   }
}
