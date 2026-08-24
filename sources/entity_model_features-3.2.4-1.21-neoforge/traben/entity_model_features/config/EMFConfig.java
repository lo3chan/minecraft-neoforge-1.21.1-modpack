package traben.entity_model_features.config;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.IEMFUnmodifiedLayerRootGetter;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig.String2EnumNullMap;
import traben.entity_texture_features.config.ETFConfig.UpdateFrequency;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreenList.Align;
import traben.tconfig.gui.TConfigScreenList.Renderable;
import traben.tconfig.gui.entries.TConfigEntry;
import traben.tconfig.gui.entries.TConfigEntryBoolean;
import traben.tconfig.gui.entries.TConfigEntryCategory;
import traben.tconfig.gui.entries.TConfigEntryCustomButton;
import traben.tconfig.gui.entries.TConfigEntryEnumButton;
import traben.tconfig.gui.entries.TConfigEntryEnumSlider;
import traben.tconfig.gui.entries.TConfigEntryInt;
import traben.tconfig.gui.entries.TConfigEntryText;
import traben.tconfig.gui.entries.TConfigEntryCategory.Empty;
import traben.tconfig.gui.entries.TConfigEntryText.TextAlignment;
import traben.tconfig.gui.entries.TConfigEntryText.TwoLines;

public class EMFConfig extends TConfig {
   public EMFConfig.CustomEntityModelSupportMode allowedCEM = EMFConfig.CustomEntityModelSupportMode.ALL;
   public boolean logModelCreationData = false;
   public boolean debugOnRightClick = false;
   public EMFConfig.RenderModeChoice renderModeChoice = EMFConfig.RenderModeChoice.NORMAL;
   public EMFConfig.VanillaModelRenderMode vanillaModelHologramRenderMode_2 = EMFConfig.VanillaModelRenderMode.OFF;
   @Deprecated(
      forRemoval = true
   )
   public EMFConfig.ModelPrintMode modelExportMode = EMFConfig.ModelPrintMode.NONE;
   public boolean automaticModelExporting = false;
   @Deprecated(
      forRemoval = true
   )
   public EMFConfig.PhysicsModCompatChoice attemptPhysicsModPatch_2 = EMFConfig.PhysicsModCompatChoice.CUSTOM;
   public UpdateFrequency modelUpdateFrequency = UpdateFrequency.Average;
   public String2EnumNullMap<EMFConfig.RenderModeChoice> entityRenderModeOverrides = new String2EnumNullMap();
   @Deprecated(
      forRemoval = true
   )
   public String2EnumNullMap<EMFConfig.PhysicsModCompatChoice> entityPhysicsModPatchOverrides = new String2EnumNullMap();
   public String2EnumNullMap<EMFConfig.VanillaModelRenderMode> entityVanillaHologramOverrides = new String2EnumNullMap();
   public Set<String> modelsNamesDisabled = new HashSet<>();
   public boolean allowEBEModConfigModify = true;
   public int animationLODDistance = 20;
   public boolean retainDetailOnLowFps = true;
   public boolean retainDetailOnLargerMobs = true;
   public boolean animationFrameSkipDuringIrisShadowPass = true;
   public boolean preventFirstPersonHandAnimating = false;
   public boolean onlyClientPlayerModel = false;
   public boolean doubleChestAnimFix = true;
   @Deprecated(
      since = "2.4.1",
      forRemoval = true
   )
   public boolean enforceOptifineVariationRequiresDefaultModel = false;
   public boolean enforceOptifineVariationRequiresDefaultModel_v2 = false;
   @Deprecated(
      since = "2.4.1",
      forRemoval = true
   )
   public boolean resetPlayerModelEachRender = true;
   public boolean resetPlayerModelEachRender_v2 = true;
   public boolean onlyDebugRenderOnHover = false;
   public boolean enforceOptifineSubFoldersVariantOnly = false;
   public boolean enforceOptiFineAnimSyntaxLimits = true;
   public boolean allowOptifineFallbackProperties = true;
   public boolean showReloadErrorToast = true;
   public boolean exportRotations = false;
   public boolean asmMaths = true;
   public boolean logASM = false;

   public EMFConfig.RenderModeChoice getRenderModeFor(EMFEntity entity) {
      String typeString = getTypeString(entity);
      return typeString == null
         ? this.renderModeChoice
         : Objects.requireNonNullElseGet((EMFConfig.RenderModeChoice)this.entityRenderModeOverrides.get(typeString), () -> this.renderModeChoice);
   }

   @Deprecated(
      forRemoval = true
   )
   public EMFConfig.PhysicsModCompatChoice getPhysicsModModeFor(EMFEntity entity) {
      String typeString = getTypeString(entity);
      return typeString == null
         ? this.attemptPhysicsModPatch_2
         : Objects.requireNonNullElseGet(
            (EMFConfig.PhysicsModCompatChoice)this.entityPhysicsModPatchOverrides.get(typeString), () -> this.attemptPhysicsModPatch_2
         );
   }

   public EMFConfig.VanillaModelRenderMode getVanillaHologramModeFor(EMFEntity entity) {
      String typeString = getTypeString(entity);
      return typeString == null
         ? this.vanillaModelHologramRenderMode_2
         : Objects.requireNonNullElseGet(
            (EMFConfig.VanillaModelRenderMode)this.entityVanillaHologramOverrides.get(typeString), () -> this.vanillaModelHologramRenderMode_2
         );
   }

   @Nullable
   private static String getTypeString(EMFEntity entity) {
      if (entity instanceof BlockEntity block) {
         return ETFApi.getBlockEntityTypeToTranslationKey(block.getType());
      } else {
         return entity instanceof Entity realBoy ? realBoy.getType().getDescriptionId() : null;
      }
   }

   public boolean isModelDisabled(String modelName) {
      return this.modelsNamesDisabled.contains(modelName);
   }

   public TConfigEntryCategory getGUIOptions() {
      return new Empty()
         .add(
            new TConfigEntry[]{
               new TConfigEntryCategory("config.entity_features.models_main")
                  .add(
                     new TConfigEntry[]{
                        new TConfigEntryCategory("entity_model_features.config.options", "entity_model_features.config.options.tooltip")
                           .add(
                              new TConfigEntry[]{
                                 new TConfigEntryEnumButton(
                                    "entity_model_features.config.allowed_cem",
                                    "entity_model_features.config.allowed_cem.tooltip",
                                    () -> this.allowedCEM,
                                    it -> this.allowedCEM = it,
                                    EMFConfig.CustomEntityModelSupportMode.ALL
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.ebe_config_modify",
                                    "entity_model_features.config.ebe_config_modify.tooltip",
                                    () -> this.allowEBEModConfigModify,
                                    value -> this.allowEBEModConfigModify = value,
                                    true
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.double_chest_fix",
                                    "entity_model_features.config.double_chest_fix.tooltip",
                                    () -> this.doubleChestAnimFix,
                                    value -> this.doubleChestAnimFix = value,
                                    true
                                 )
                              }
                           ),
                        new TConfigEntryCategory("entity_model_features.config.player_settings")
                           .add(
                              new TConfigEntry[]{
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.prevent_hand",
                                    "entity_model_features.config.prevent_hand.tooltip",
                                    () -> this.preventFirstPersonHandAnimating,
                                    value -> this.preventFirstPersonHandAnimating = value,
                                    false
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.only_client",
                                    "entity_model_features.config.only_client.tooltip",
                                    () -> this.onlyClientPlayerModel,
                                    value -> this.onlyClientPlayerModel = value,
                                    false
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.reset_player",
                                    "entity_model_features.config.reset_player.tooltip",
                                    () -> this.resetPlayerModelEachRender_v2,
                                    value -> this.resetPlayerModelEachRender_v2 = value,
                                    true
                                 )
                              }
                           ),
                        new TConfigEntryCategory("entity_model_features.config.performance")
                           .add(
                              new TConfigEntry[]{
                                 new TConfigEntryEnumSlider(
                                    "entity_model_features.config.update",
                                    "entity_model_features.config.update.tooltip",
                                    () -> this.modelUpdateFrequency,
                                    value -> this.modelUpdateFrequency = value,
                                    UpdateFrequency.Average
                                 ),
                                 new TConfigEntryInt(
                                    "entity_model_features.config.lod",
                                    "entity_model_features.config.lod.tooltip",
                                    () -> this.animationLODDistance,
                                    value -> this.animationLODDistance = value,
                                    20,
                                    0,
                                    65,
                                    true,
                                    true
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.low_fps_lod",
                                    "entity_model_features.config.low_fps_lod.tooltip",
                                    () -> this.retainDetailOnLowFps,
                                    value -> this.retainDetailOnLowFps = value,
                                    true
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.large_mob_lod",
                                    "entity_model_features.config.large_mob_lod.tooltip",
                                    () -> this.retainDetailOnLargerMobs,
                                    value -> this.retainDetailOnLargerMobs = value,
                                    true
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.iris_shadow_skip",
                                    "entity_model_features.config.iris_shadow_skip.tooltip",
                                    () -> this.animationFrameSkipDuringIrisShadowPass,
                                    value -> this.animationFrameSkipDuringIrisShadowPass = value,
                                    true
                                 )
                              }
                           ),
                        new TConfigEntryCategory("entity_model_features.config.tools", "entity_model_features.config.tools.tooltip")
                           .add(
                              new TConfigEntry[]{
                                 new TConfigEntryEnumSlider(
                                    "entity_model_features.config.vanilla_render",
                                    "entity_model_features.config.vanilla_render.tooltip",
                                    () -> this.vanillaModelHologramRenderMode_2,
                                    value -> this.vanillaModelHologramRenderMode_2 = value,
                                    EMFConfig.VanillaModelRenderMode.OFF
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.print_mode",
                                    "entity_model_features.config.print_mode.tooltip",
                                    () -> this.automaticModelExporting,
                                    value -> this.automaticModelExporting = value,
                                    false
                                 )
                              }
                           ),
                        new TConfigEntryCategory("entity_model_features.config.debug", "entity_model_features.config.debug.tooltip")
                           .add(
                              new TConfigEntry[]{
                                 new TConfigEntryEnumSlider(
                                    "entity_model_features.config.render",
                                    "entity_model_features.config.render.tooltip",
                                    () -> this.renderModeChoice,
                                    value -> this.renderModeChoice = value,
                                    EMFConfig.RenderModeChoice.NORMAL
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.debug_hover",
                                    "entity_model_features.config.debug_hover.tooltip",
                                    () -> this.onlyDebugRenderOnHover,
                                    value -> this.onlyDebugRenderOnHover = value,
                                    false
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.log_models",
                                    "entity_model_features.config.log_models.tooltip",
                                    () -> this.logModelCreationData,
                                    value -> this.logModelCreationData = value,
                                    false
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.debug_right_click",
                                    "entity_model_features.config.debug_right_click.tooltip",
                                    () -> this.debugOnRightClick,
                                    value -> this.debugOnRightClick = value,
                                    false
                                 ),
                                 new TConfigEntryCustomButton(
                                    "entity_model_features.config.load_warn.title",
                                    "entity_model_features.config.load_warn.tooltip",
                                    button -> reprintLoadingErrors(false)
                                 ),
                                 new TConfigEntryCustomButton(
                                    "entity_model_features.config.load_warn.title2",
                                    "entity_model_features.config.load_warn.tooltip",
                                    button -> reprintLoadingErrors(true)
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.show_reload_error_toast",
                                    "entity_model_features.config.show_reload_error_toast.tooltip",
                                    () -> this.showReloadErrorToast,
                                    value -> this.showReloadErrorToast = value,
                                    true
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.export_rotations",
                                    "entity_model_features.config.export_rotations.tooltip",
                                    () -> this.exportRotations,
                                    value -> this.exportRotations = value,
                                    false
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.asmmaths",
                                    "entity_model_features.config.asmmaths.tooltip",
                                    () -> this.asmMaths,
                                    value -> this.asmMaths = value,
                                    true
                                 ),
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.logASM",
                                    "entity_model_features.config.asmmaths.logASM",
                                    () -> this.logASM,
                                    value -> this.logASM = value,
                                    false
                                 )
                              }
                           ),
                        this.getModelSettings(),
                        this.getMathInfo()
                     }
                  ),
               this.getEntitySettings(),
               new TConfigEntryCategory("config.entity_features.optifine_settings", "config.entity_texture_features.optifine.desc")
                  .add(
                     new TConfigEntry[]{
                        new TConfigEntryBoolean(
                           "entity_model_features.config.variation_base",
                           "entity_model_features.config.variation_base.tooltip",
                           () -> this.enforceOptifineVariationRequiresDefaultModel_v2,
                           value -> this.enforceOptifineVariationRequiresDefaultModel_v2 = value,
                           false
                        ),
                        new TConfigEntryBoolean(
                           "entity_model_features.config.optifine_subfolders",
                           "entity_model_features.config.optifine_subfolders.tooltip",
                           () -> this.enforceOptifineSubFoldersVariantOnly,
                           value -> this.enforceOptifineSubFoldersVariantOnly = value,
                           true
                        ),
                        new TConfigEntryBoolean(
                           "entity_model_features.config.optifine_syntax",
                           "entity_model_features.config.optifine_syntax.tooltip",
                           () -> this.enforceOptiFineAnimSyntaxLimits,
                           value -> this.enforceOptiFineAnimSyntaxLimits = value,
                           true
                        ),
                        new TConfigEntryBoolean(
                           "entity_model_features.config.optifine_fallback_properties",
                           "entity_model_features.config.optifine_fallback_properties.tooltip",
                           () -> this.allowOptifineFallbackProperties,
                           value -> this.allowOptifineFallbackProperties = value,
                           true
                        )
                     }
                  )
            }
         );
   }

   private static void reprintLoadingErrors(boolean stacktrace) {
      EMFUtils.logError(
         "~~//BEGIN: EMF MASS PRINT OF LOADING ERRORS\\\\~~\nNot all errors here will be helpful as they may be missing surrounding log context.\nThis is best used to know what you might need to be searching for in your log\nor to give the dev debugging info."
      );
      int i = 1;

      for (Exception loadingException : EMFManager.getInstance().loadingExceptions) {
         System.out.println("----------//Exception #" + i + "\\\\---------- \n" + loadingException.getMessage());
         if (stacktrace) {
            loadingException.printStackTrace();
         }

         i++;
      }

      EMFUtils.logError("~~\\\\END: EMF MASS PRINT OF LOADING ERRORS//~~");
   }

   private TConfigEntryCategory getMathInfo() {
      TConfigEntryCategory category = new TConfigEntryCategory("entity_model_features.config.math");
      category.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.math.explain", 200, TextAlignment.LEFT));
      TConfigEntryCategory variables = new TConfigEntryCategory("entity_model_features.config.variables");
      category.add(variables);
      variables.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.variables.explain", 200, TextAlignment.LEFT));

      for (UniqueVariableFactory uniqueVariableFactory : VariableRegistry.getInstance().getUniqueVariableFactories()) {
         TConfigEntryCategory unique = new TConfigEntryCategory(uniqueVariableFactory.getTitleTranslationKey())
            .addAll(TConfigEntryText.fromLongOrMultilineTranslation(uniqueVariableFactory.getExplanationTranslationKey(), 200, TextAlignment.LEFT));
         variables.add(unique);
      }

      VariableRegistry.getInstance().getSingletonVariableExplanationTranslationKeys().keySet().stream().sorted().forEach(key -> {
         String value = VariableRegistry.getInstance().getSingletonVariableExplanationTranslationKeys().get(key);
         TConfigEntryCategory uniquex = new TConfigEntryCategory(key).addAll(TConfigEntryText.fromLongOrMultilineTranslation(value, 200, TextAlignment.LEFT));
         variables.add(uniquex);
      });
      TConfigEntryCategory methods = new TConfigEntryCategory("entity_model_features.config.functions");
      category.add(methods);
      methods.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.functions.explain", 200, TextAlignment.LEFT));
      MethodRegistry.getInstance()
         .getMethodExplanationTranslationKeys()
         .keySet()
         .stream()
         .sorted()
         .forEach(
            key -> {
               String value = MethodRegistry.getInstance().getMethodExplanationTranslationKeys().get(key);
               TConfigEntryCategory method = new TConfigEntryCategory(key + "()")
                  .addAll(TConfigEntryText.fromLongOrMultilineTranslation(value, 200, TextAlignment.LEFT));
               methods.add(method);
            }
         );
      return category;
   }

   private TConfigEntryCategory getModelSettings() {
      TConfigEntryCategory category = new TConfigEntryCategory("entity_model_features.config.models");
      category.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.models_text", 200, TextAlignment.LEFT));
      HashMap<EMFModel_ID, ModelLayerLocation> map = new HashMap<>(EMFManager.getInstance().cache_LayersByModelName);
      map.put(new EMFModel_ID("wolf_collar"), ModelLayers.WOLF);
      map.entrySet()
         .stream()
         .sorted(Entry.comparingByKey())
         .forEach(
            entry -> {
               EMFModel_ID mapData = entry.getKey();
               if (!mapData.toString().startsWith("_")) {
                  ModelLayerLocation layer = entry.getValue();
                  if (layer != null) {
                     LayerDefinition vanilla = (LayerDefinition)Minecraft.getInstance().getEntityModels().roots.get(layer);
                     if (vanilla != null) {
                        String namespace = "minecraft".equals(mapData.getNamespace()) ? "" : mapData.getNamespace() + ":";
                        String fileName = namespace + mapData.getfileName();
                        TConfigEntryCategory model = new TConfigEntryCategory(fileName + ".jem");
                        model.setAlign(Align.RIGHT);
                        model.setWidgetBackgroundToFullWidth();
                        model.setRenderFeature(new EMFConfig.ModelRootRenderer(layer));
                        category.add(model);
                        StringBuilder fallbacks = new StringBuilder();
                        mapData.forEachFallback(fallBackData -> fallbacks.append(fallBackData.getfileName()).append(".jem\n"));
                        model.add(
                              new TConfigEntry[]{
                                 new TConfigEntryBoolean(
                                    "entity_model_features.config.models.enabled",
                                    "entity_model_features.config.models.enabled.tooltip",
                                    () -> !this.modelsNamesDisabled.contains(fileName),
                                    value -> {
                                       if (value) {
                                          this.modelsNamesDisabled.remove(fileName);
                                       } else {
                                          this.modelsNamesDisabled.add(fileName);
                                       }
                                    },
                                    true
                                 ),
                                 new TConfigEntryCategory("entity_model_features.config.models.part_names").addAll(this.getmappings(mapData.getMapId())),
                                 this.getExport(mapData, layer),
                                 new TConfigEntryCategory("entity_model_features.config.models.file_names")
                                    .addAll(
                                       TConfigEntryText.fromLongOrMultilineTranslation(
                                          "<Folders>\nassets/"
                                             + mapData.getNamespace()
                                             + "/emf/cem/\nassets/"
                                             + mapData.getNamespace()
                                             + "/optifine/cem/\n\n<possible model names>\n<checked from top down>\n"
                                             + mapData.getfileName()
                                             + ".jem\n"
                                             + fallbacks,
                                          600,
                                          TextAlignment.CENTER
                                       )
                                    )
                              }
                           )
                           .addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.models.explain", 100, TextAlignment.LEFT));
                     }
                  }
               }
            }
         );
      return category;
   }

   @NotNull
   private TConfigEntry getExport(EMFModel_ID key, ModelLayerLocation layer) {
      TConfigEntry export;
      try {
         Objects.requireNonNull(key.getMapId());
         export = new TConfigEntryCustomButton(
            "entity_model_features.config.models.export",
            "entity_model_features.config.models.export.tooltip",
            button -> {
               try {
                  EMFModelMappings.getMapOf(
                     key,
                     Objects.requireNonNullElseGet(
                           ((IEMFUnmodifiedLayerRootGetter)Minecraft.getInstance().getEntityModels()).emf$getUnmodifiedRoots().get(layer),
                           () -> (LayerDefinition)Minecraft.getInstance().getEntityModels().roots.get(layer)
                        )
                        .bakeRoot(),
                     false,
                     false,
                     true
                  );
               } catch (Exception var4) {
                  var4.printStackTrace();
               }

               button.active = false;
               button.setMessage(Component.translatable("entity_model_features.config.models.export.success"));
            }
         );
      } catch (Exception var5) {
         export = new TwoLines("entity_model_features.config.models.export.fail", var5.getMessage());
         var5.printStackTrace();
      }

      return export;
   }

   private Collection<TConfigEntry> getmappings(String mapKey) {
      ArrayList<TConfigEntry> list = new ArrayList<>();
      Map<String, String> map;
      if (EMFModelMappings.OPTIFINE_MODEL_MAP_CACHE.containsKey(mapKey)) {
         list.add(new TConfigEntryText("entity_model_features.config.variable_explanation.optifine_parts"));
         list.add(new TConfigEntryText("\\/"));
         map = EMFModelMappings.OPTIFINE_MODEL_MAP_CACHE.get(mapKey);
      } else {
         list.add(new TConfigEntryText("entity_model_features.config.variable_explanation.unknown_parts"));
         list.add(new TConfigEntryText("\\/"));
         map = EMFModelMappings.UNKNOWN_MODEL_MAP_CACHE.get(mapKey);
      }

      if (map == null) {
         return List.of();
      } else {
         for (String entry : map.keySet()) {
            list.add(new TConfigEntryText(entry));
         }

         return list;
      }
   }

   private TConfigEntryCategory getEntitySettings() {
      TConfigEntryCategory category = new TConfigEntryCategory("config.entity_features.per_entity_settings");

      try {
         BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            String translationKey = entityType.getDescriptionId();
            TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey);
            this.addEntityConfigs(entityCategory, translationKey);
            category.add(entityCategory);
         });
         BlockEntityRenderers.PROVIDERS.keySet().forEach(entityType -> {
            String translationKey = ETFApi.getBlockEntityTypeToTranslationKey(entityType);
            TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey);
            this.addEntityConfigs(entityCategory, translationKey);
            category.add(entityCategory);
         });
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return category;
   }

   private void addEntityConfigs(TConfigEntryCategory entityCategory, String translationKey) {
      TConfigEntryCategory category = new TConfigEntryCategory("config.entity_features.models_main");
      entityCategory.add(category);
      category.add(
         new TConfigEntry[]{
            new TConfigEntryEnumSlider(
               "entity_model_features.config.render",
               "entity_model_features.config.render.tooltip",
               () -> (EMFConfig.RenderModeChoice)this.entityRenderModeOverrides.get(translationKey),
               layer -> this.entityRenderModeOverrides.putNullable(translationKey, layer),
               null,
               EMFConfig.RenderModeChoice.class
            ),
            new TConfigEntryEnumButton(
               "entity_model_features.config.vanilla_render",
               "entity_model_features.config.vanilla_render.tooltip",
               () -> (EMFConfig.VanillaModelRenderMode)this.entityVanillaHologramOverrides.get(translationKey),
               layer -> this.entityVanillaHologramOverrides.putNullable(translationKey, layer),
               null,
               EMFConfig.VanillaModelRenderMode.class
            )
         }
      );
   }

   public ResourceLocation getModIcon() {
      return EMFUtils.res("entity_model_features", "textures/gui/icon.png");
   }

   public static enum CustomEntityModelSupportMode {
      OFF("options.off"),
      ALL("entity_model_features.config.allowed_cem.all"),
      KNOWN_ONLY("entity_model_features.config.allowed_cem.known_only"),
      UNKNOWN_ONLY("entity_model_features.config.allowed_cem.unknown_only");

      private final String text;

      private CustomEntityModelSupportMode(String text) {
         this.text = text;
      }

      @Override
      public String toString() {
         return Component.translatable(this.text).getString();
      }

      public boolean allowsKnown() {
         return this == ALL || this == KNOWN_ONLY;
      }

      public boolean allowsUnknowns() {
         return this == ALL || this == UNKNOWN_ONLY;
      }

      public boolean isOn() {
         return this != OFF;
      }

      public boolean allowsAll() {
         return this == ALL;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static enum ModelPrintMode {
      NONE("options.off"),
      LOG_ONLY("entity_model_features.config.print_mode.log"),
      LOG_AND_JEM("entity_model_features.config.print_mode.log_jem"),
      ALL_LOG_ONLY("entity_model_features.config.print_mode.all_log"),
      ALL_LOG_AND_JEM("entity_model_features.config.print_mode.all_log_jem");

      private final String text;

      private ModelPrintMode(String text) {
         this.text = text;
      }

      public boolean doesJems() {
         return this == LOG_AND_JEM || this == ALL_LOG_AND_JEM;
      }

      public boolean doesAll() {
         return this == ALL_LOG_ONLY || this == ALL_LOG_AND_JEM;
      }

      public boolean doesLog() {
         return this != NONE;
      }

      @Override
      public String toString() {
         return Component.translatable(this.text).getString();
      }
   }

   private static class ModelRootRenderer implements Renderable {
      private final ModelLayerLocation layer;
      private ModelPart root = null;
      private boolean asserted = false;

      ModelRootRenderer(ModelLayerLocation layer) {
         this.layer = layer;
      }

      private boolean canRender() {
         if (!this.asserted && this.root == null) {
            this.asserted = true;

            try {
               this.root = ((LayerDefinition)Minecraft.getInstance().getEntityModels().roots.get(this.layer)).bakeRoot();
            } catch (Exception var2) {
               var2.printStackTrace();
            }
         }

         return this.root != null;
      }

      public void render(GuiGraphics context, int mouseX, int mouseY) {
         if (this.canRender()) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null) {
               return;
            }

            int y = (int)(screen.height * 0.75);
            int x = (int)(screen.width * 0.33);
            float g = (float)(-Math.atan((-mouseY + screen.height / 2.0F) / 40.0F));
            float g2 = (float)(-Math.atan((-mouseX + screen.width / 3.0F) / 400.0F));
            Quaternionf quaternionf = new Quaternionf().rotateZ(3.1415927F).rotateY(g2 * 8.0F);
            Quaternionf quaternionf2 = new Quaternionf().rotateX(-(g * 20.0F * 0.017453292F) * 2.0F);
            quaternionf.mul(quaternionf2);
            PoseStack matrixStack = context.pose();
            Lighting.setupForEntityInInventory();
            matrixStack.pushPose();
            matrixStack.translate(x, y, 150.0);
            float scaling = (float)(screen.height * 0.3);
            matrixStack.mulPose(new Matrix4f().scaling(scaling, scaling, -scaling));
            matrixStack.mulPose(quaternionf);
            matrixStack.pushPose();
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            matrixStack.translate(0.0F, -1.501F, 0.0F);
            VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
            if (buffer != null) {
               this.renderBoxes(matrixStack, buffer, this.root);
            }

            matrixStack.popPose();
         }
      }

      private void renderBoxes(PoseStack matrices, VertexConsumer vertices, ModelPart modelPart) {
         if (modelPart.visible && (!modelPart.cubes.isEmpty() || !modelPart.children.isEmpty())) {
            matrices.pushPose();
            modelPart.translateAndRotate(matrices);
            if (!modelPart.skipDraw) {
               for (Cube cuboid : modelPart.cubes) {
                  AABB box = new AABB(
                     cuboid.minX / 16.0F, cuboid.minY / 16.0F, cuboid.minZ / 16.0F, cuboid.maxX / 16.0F, cuboid.maxY / 16.0F, cuboid.maxZ / 16.0F
                  );
                  LevelRenderer.renderLineBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
               }
            }

            for (ModelPart modelPartChildren : modelPart.children.values()) {
               this.renderBoxes(matrices, vertices, modelPartChildren);
            }

            matrices.popPose();
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static enum PhysicsModCompatChoice {
      OFF("options.off"),
      VANILLA("entity_model_features.config.physics.1"),
      CUSTOM("entity_model_features.config.physics.2");

      private final String text;

      private PhysicsModCompatChoice(String text) {
         this.text = text;
      }

      @Override
      public String toString() {
         return Component.translatable(this.text).getString();
      }
   }

   public static enum RenderModeChoice {
      NORMAL("entity_model_features.config.render.normal"),
      GREEN("entity_model_features.config.render.green"),
      LINES_AND_TEXTURE("entity_model_features.config.render.lines_texture"),
      LINES_AND_TEXTURE_FLASH("entity_model_features.config.render.lines_texture_flash"),
      LINES("entity_model_features.config.render.lines"),
      NONE("entity_model_features.config.render.none");

      private final String text;

      private RenderModeChoice(String text) {
         this.text = text;
      }

      @Override
      public String toString() {
         return Component.translatable(this.text).getString();
      }
   }

   public static enum VanillaModelRenderMode {
      OFF("options.off"),
      NORMAL("entity_model_features.config.vanilla_render.normal"),
      OFFSET("entity_model_features.config.vanilla_render.offset");

      private final String text;

      private VanillaModelRenderMode(String text) {
         this.text = text;
      }

      @Override
      public String toString() {
         return Component.translatable(this.text).getString();
      }
   }
}
