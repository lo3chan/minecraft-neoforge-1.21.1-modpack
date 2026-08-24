package traben.entity_model_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mod_compat.EBEConfigModifier;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.EMFPartialArmor;
import traben.entity_model_features.models.IEMFModelNameContainer;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.asm.ASMAnimationHandler;
import traben.entity_model_features.models.animation.math.asm.ASMParser;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathExpressionParser;
import traben.entity_model_features.models.animation.math.expression_tree.OldEMFAnimationHandler;
import traben.entity_model_features.models.animation.math.methods.emf.NBTMethod;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;
import traben.entity_model_features.utils.EMFDirectoryHandler;
import traben.entity_model_features.utils.EMFResourceCaching;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFLruCache.UUIDInteger;

public class EMFManager {
   private static final Map<BlockEntityType<?>, String> EBETypes = Map.of(
      BlockEntityType.BED,
      "bed",
      BlockEntityType.CHEST,
      "chest",
      BlockEntityType.TRAPPED_CHEST,
      "chest",
      BlockEntityType.ENDER_CHEST,
      "chest",
      BlockEntityType.SHULKER_BOX,
      "shulker_box",
      BlockEntityType.BELL,
      "bell",
      BlockEntityType.SIGN,
      "sign",
      BlockEntityType.DECORATED_POT,
      "decorated_pot"
   );
   public static EMFModelPartRoot lastCreatedRootModelPart = null;
   private static EMFManager self = null;
   public final boolean IS_PHYSICS_MOD_INSTALLED;
   public final boolean IS_EBE_INSTALLED;
   public final UUIDInteger lastModelRuleOfEntity;
   public final UUIDInteger lastModelSuffixOfEntity;
   public final Map<String, Set<EMFModelPartRoot>> rootPartsPerEntityTypeForDebug = new ConcurrentHashMap<>();
   public final Set<EMFModel_ID> modelsAnnounced = new HashSet<>();
   public final Map<String, Set<EMFModelPartRoot>> rootPartsPerEntityTypeForVariation = new ConcurrentHashMap<>();
   public final Map<String, EMFJemData> cache_JemDataByFileName = new HashMap<>();
   public final Map<EMFModel_ID, ModelLayerLocation> cache_LayersByModelName = new ConcurrentHashMap<>();
   public final Set<String> EBE_JEMS_FOUND_LAST = new HashSet<>();
   private final Map<ModelLayerLocation, Integer> amountOfLayerAttempts = new ConcurrentHashMap<>();
   private final Set<String> EBE_JEMS_FOUND = new HashSet<>();
   private final ArrayList<String> KNOWN_RESOURCEPACK_ORDER;
   public UUID entityForDebugPrint = null;
   public long entityRenderCount = 0L;
   public boolean isAnimationValidationPhase = false;
   public String currentSpecifiedModelLoading = "";
   public BlockEntityType<?> currentBlockEntityTypeLoading = null;
   private boolean traderLlamaHappened = false;
   @Nullable
   private String lastUndeadHorse = null;
   public final List<Exception> loadingExceptions = new ArrayList<>();
   private static int managerInstanceHash = 0;
   private EMFPartialArmor armorParts = null;

   public void receiveException(Exception exception) {
      if (exception != null && exception.getMessage() != null && !exception.getMessage().trim().equals("null")) {
         this.loadingExceptions.add(exception);
      }
   }

   private EMFManager() {
      EMFAnimationEntityContext.globalReset();
      this.IS_PHYSICS_MOD_INSTALLED = ETF.isThisModLoaded("physicsmod");
      this.IS_EBE_INSTALLED = ETF.isThisModLoaded("enhancedblockentities");
      this.lastModelRuleOfEntity = new UUIDInteger();
      this.lastModelRuleOfEntity.defaultReturnValue(0);
      this.lastModelSuffixOfEntity = new UUIDInteger();
      this.lastModelSuffixOfEntity.defaultReturnValue(0);
      this.KNOWN_RESOURCEPACK_ORDER = new ArrayList<>();
      managerInstanceHash++;
   }

   public static int getManagerInstanceHash() {
      return managerInstanceHash;
   }

   public static EMFManager getInstance() {
      if (self == null) {
         self = new EMFManager();
      }

      return self;
   }

   public static void resetInstance() {
      EMFUtils.log("Clearing data for reload.", false);
      EMF.config().loadFromFile();
      EMFModelMappings.UNKNOWN_MODEL_MAP_CACHE.clear();
      EMFResourceCaching.clearCache();
      NBTMethod.CACHE.clear();
      GlobalVariableFactory.clear();
      self = new EMFManager();
   }

   @Nullable
   public static EMFJemData getJemDataWithDirectory(EMFDirectoryHandler jemDirectory, EMFModel_ID mobModelIDInfo) {
      String pathOfJem = jemDirectory.getFinalFileLocation();
      if (getInstance().cache_JemDataByFileName.containsKey(pathOfJem)) {
         return getInstance().cache_JemDataByFileName.get(pathOfJem);
      } else {
         boolean print = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;

         try {
            ResourceLocation jemLoc = EMFUtils.res(pathOfJem);
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if (!EMFResourceCaching.resourceExists(resourceManager, jemLoc)) {
               if (print) {
                  EMFUtils.log(pathOfJem + ", .jem read failed " + pathOfJem + " does not exist", false);
               }

               return null;
            }

            if (print) {
               EMFUtils.log(pathOfJem + ", .jem read success " + pathOfJem + " exists", false);
            }

            Optional<Resource> res = resourceManager.getResource(jemLoc);
            if (res.isEmpty()) {
               return null;
            }

            Resource jemResource = res.get();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jemResource.open()));
            EMFJemData jem = (EMFJemData)gson.fromJson(reader, EMFJemData.class);
            reader.close();
            jem.prepare(jemDirectory, mobModelIDInfo);
            if (mobModelIDInfo.areBothSame()) {
               getInstance().cache_JemDataByFileName.put(pathOfJem, jem);
            }

            return jem;
         } catch (FileNotFoundException | ResourceLocationException var11) {
            if (print) {
               EMFUtils.log(pathOfJem + ", .jem failed to load: " + var11, false);
            }
         } catch (Exception var12) {
            EMFUtils.log(pathOfJem + ", .jem failed to load: " + var12, false);
            var12.printStackTrace();
            EMFException.recordException(var12);
         }

         return null;
      }
   }

   public static EMFModelPart getModelFromHierarchicalId(String hierarchId, Map<String, EMFModelPart> map) {
      if (hierarchId != null && !hierarchId.isBlank()) {
         EMFModelPart part = map.get(hierarchId);
         if (part == null) {
            part = map.get("EMF_" + hierarchId);
         }

         if (part != null) {
            return part;
         } else {
            for (Entry<String, EMFModelPart> entry : map.entrySet()) {
               String key = entry.getKey();
               if (!key.endsWith(":" + hierarchId) && !key.endsWith(":EMF_" + hierarchId)) {
                  String[] parts = hierarchId.split(":");
                  boolean allPartsMatch = Arrays.stream(parts).allMatch(partStr -> key.contains(partStr) || key.contains("EMF_" + partStr));
                  String last = parts[parts.length - 1];
                  if (!allPartsMatch || !key.equals(last) && !key.endsWith(":" + last) && !key.endsWith(":EMF_" + last)) {
                     continue;
                  }

                  return entry.getValue();
               }

               return entry.getValue();
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static void handleBoats(String originalLayerName, EMFModel_ID mobNameForFileAndMap) throws EMFException {
      String jem;
      if (originalLayerName.startsWith("chest_boat/")) {
         jem = originalLayerName.startsWith("chest_boat/bamboo") ? "chest_raft" : "chest_boat";
      } else {
         if (!originalLayerName.startsWith("boat/")) {
            return;
         }

         jem = originalLayerName.startsWith("boat/bamboo") ? "raft" : "boat";
      }

      mobNameForFileAndMap.setMapIdAndAddFallbackModel(jem);
      String type = mobNameForFileAndMap.getfileName().split("/")[1];
      mobNameForFileAndMap.setFileName(type + "_" + jem);
   }

   public boolean wasEBEModified() {
      return !this.EBE_JEMS_FOUND_LAST.isEmpty();
   }

   public ArrayList<String> getResourcePackList() {
      if (this.KNOWN_RESOURCEPACK_ORDER.isEmpty()) {
         for (PackResources pack : Minecraft.getInstance().getResourceManager().listPacks().toList()) {
            this.KNOWN_RESOURCEPACK_ORDER.add(pack.packId());
         }
      }

      return this.KNOWN_RESOURCEPACK_ORDER;
   }

   public void modifyEBEIfRequired() {
      if (this.IS_EBE_INSTALLED && !this.EBE_JEMS_FOUND.isEmpty() && ((EMFConfig)EMF.config().getConfig()).allowEBEModConfigModify) {
         try {
            EBEConfigModifier.modifyEBEConfig(this.EBE_JEMS_FOUND);
         } catch (Error | Exception var3) {
            EMFUtils.logWarn("EBE config modification issue: " + var3);
            if (var3 instanceof Exception f) {
               EMFException.recordException(f);
            }
         }
      }

      this.EBE_JEMS_FOUND_LAST.clear();
      this.EBE_JEMS_FOUND_LAST.addAll(this.EBE_JEMS_FOUND);
      this.EBE_JEMS_FOUND.clear();
   }

   public ModelPart injectIntoModelRootGetter(ModelLayerLocation layer, ModelPart root) {
      EMFConfig.CustomEntityModelSupportMode allowedCEM = ((EMFConfig)EMF.config().getConfig()).allowedCEM;
      if (!allowedCEM.isOn()) {
         return root;
      } else {
         if (!EMF.isLoadingPhase) {
            Integer creationsOfLayer = this.amountOfLayerAttempts.put(layer, this.amountOfLayerAttempts.getOrDefault(layer, 0) + 1);
            if (creationsOfLayer != null && creationsOfLayer > 64) {
               if (creationsOfLayer == 65) {
                  EMFUtils.logError(
                     "model attempted creation more than 64 times {"
                        + layer.toString()
                        + "]. EMF is now ignoring this model. Please inform the mod maker that this is not how entity models are meant to be utilised. They should ALWAYS be stored and reused."
                  );
               }

               return root;
            }
         }

         String originalLayerName = layer.getModel().getPath();
         String originalLayerBase = originalLayerName.replaceFirst("_baby$", "");
         EMFModel_ID mobNameForFileAndMap = new EMFModel_ID(
            !this.currentSpecifiedModelLoading.isBlank() && !this.currentSpecifiedModelLoading.startsWith("emf$")
               ? this.currentSpecifiedModelLoading
               : originalLayerName
         );

         try {
            lastCreatedRootModelPart = null;
            boolean printing = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;
            if (!"main".equals(layer.getLayer())) {
               mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName() + "_" + layer.getLayer());
               originalLayerName = originalLayerName + "_" + layer.getLayer();
            }

            boolean isBaby = false;
            boolean modded;
            if (!"minecraft".equals(layer.getModel().getNamespace())) {
               modded = true;
               if (!allowedCEM.allowsUnknowns()) {
                  return root;
               }

               mobNameForFileAndMap.setBoth(originalLayerName.toLowerCase().replaceAll("[^a-z0-9/._-]", "_"));
               mobNameForFileAndMap.namespace = layer.getModel().getNamespace();
            } else {
               modded = false;
               if (!allowedCEM.allowsKnown()) {
                  return root;
               }

               boolean skipSwitch = false;
               if (mobNameForFileAndMap.getfileName().matches(".*_collar($|_\\w*)")) {
                  String baseName = mobNameForFileAndMap.getfileName().replaceFirst("_collar", "");
                  mobNameForFileAndMap.addFallbackModel(baseName);
                  if (isBaby) {
                     mobNameForFileAndMap.addFallbackModel(baseName.replaceFirst("_baby", ""));
                  }
               }

               String filename = mobNameForFileAndMap.getfileName();
               if (filename.startsWith("zombie_horse") || filename.startsWith("skeleton_horse")) {
                  this.lastUndeadHorse = filename;
               }

               if (!skipSwitch) {
                  switch (originalLayerName) {
                     case "parrot_shoulder":
                        mobNameForFileAndMap.setMapIdAndAddFallbackModel("parrot");
                        break;
                     case "evoker":
                        mobNameForFileAndMap.addFallbackModel("evocation_illager");
                        break;
                     case "evoker_fangs":
                        mobNameForFileAndMap.addFallbackModel("evocation_fangs");
                        break;
                     case "vindicator":
                        mobNameForFileAndMap.addFallbackModel("vindication_illager");
                        break;
                     case "bed_foot":
                        mobNameForFileAndMap.setBoth("bed_foot").addFallbackModel("bed");
                        break;
                     case "bed_head":
                        mobNameForFileAndMap.setBoth("bed_head").addFallbackModel("bed");
                        break;
                     case "book":
                        if (this.currentSpecifiedModelLoading.equals("enchanting_book")) {
                           mobNameForFileAndMap.setBoth("enchanting_book", "book").addFallbackModel("book");
                        } else {
                           mobNameForFileAndMap.setBoth("lectern_book", "book").addFallbackModel("book");
                        }
                        break;
                     case "salmon_small":
                     case "salmon_large":
                        mobNameForFileAndMap.addFallbackModel("salmon");
                        break;
                     case "breeze_wind_charge":
                        mobNameForFileAndMap.setMapIdAndAddFallbackModel("wind_charge");
                        break;
                     case "creaking_transient":
                        mobNameForFileAndMap.setMapIdAndAddFallbackModel("creaking");
                        break;
                     case "chest":
                        mobNameForFileAndMap.setBoth(
                           this.currentSpecifiedModelLoading != null && !this.currentSpecifiedModelLoading.isBlank()
                              ? this.currentSpecifiedModelLoading
                              : "chest",
                           "chest"
                        );
                        break;
                     case "conduit_cage":
                        mobNameForFileAndMap.setBoth("conduit_cage").addFallbackModel("conduit");
                        break;
                     case "conduit_eye":
                        mobNameForFileAndMap.setBoth("conduit_eye").addFallbackModel("conduit");
                        break;
                     case "conduit_shell":
                        mobNameForFileAndMap.setBoth("conduit_shell").addFallbackModel("conduit");
                        break;
                     case "conduit_wind":
                        mobNameForFileAndMap.setBoth("conduit_wind").addFallbackModel("conduit");
                        break;
                     case "creeper_armor":
                        mobNameForFileAndMap.setBoth("creeper_charge");
                        break;
                     case "creeper_head":
                        mobNameForFileAndMap.setBoth("head_creeper");
                        break;
                     case "decorated_pot_base":
                        mobNameForFileAndMap.setBoth("decorated_pot_base").addFallbackModel("decorated_pot");
                        break;
                     case "decorated_pot_sides":
                        mobNameForFileAndMap.setBoth("decorated_pot_sides").addFallbackModel("decorated_pot");
                        break;
                     case "double_chest_left":
                        this.getDoubleChest(root, mobNameForFileAndMap, false, printing);
                        break;
                     case "double_chest_right":
                        this.getDoubleChest(root, mobNameForFileAndMap, true, printing);
                        break;
                     case "dragon_skull":
                        mobNameForFileAndMap.setBoth("head_dragon");
                        break;
                     case "ender_dragon":
                        mobNameForFileAndMap.setBoth("dragon");
                        break;
                     case "leash_knot":
                        mobNameForFileAndMap.setBoth("lead_knot");
                        break;
                     case "llama":
                     case "llama_baby":
                        this.traderLlamaHappened = false;
                        break;
                     case "llama_decor":
                        mobNameForFileAndMap.setBoth(this.traderLlamaHappened ? "trader_llama_decor" : "llama_decor");
                        break;
                     case "llama_baby_decor":
                        mobNameForFileAndMap.setBoth(this.traderLlamaHappened ? "trader_llama_baby_decor" : "llama_baby_decor");
                        break;
                     case "chest_minecart":
                     case "command_block_minecart":
                     case "spawner_minecart":
                     case "tnt_minecart":
                     case "furnace_minecart":
                     case "hopper_minecart":
                        mobNameForFileAndMap.setMapIdAndAddFallbackModel("minecart");
                        break;
                     case "piglin_head":
                        mobNameForFileAndMap.setBoth("head_piglin");
                        break;
                     case "player_head":
                        mobNameForFileAndMap.setBoth("head_player");
                        break;
                     case "player_slim":
                        mobNameForFileAndMap.addFallbackModel("player");
                        break;
                     case "arrow":
                        if (this.currentSpecifiedModelLoading.equals("spectral_arrow")) {
                           mobNameForFileAndMap.setBoth("spectral_arrow");
                           mobNameForFileAndMap.addFallbackModel("arrow");
                        }
                        break;
                     case "boat_water_patch":
                        if (this.currentSpecifiedModelLoading.startsWith("emf$boat$")) {
                           String type = this.currentSpecifiedModelLoading.substring(9);
                           mobNameForFileAndMap.setBoth(type + "_boat_patch", "boat_patch").addFallbackModel("boat_patch");
                           this.currentSpecifiedModelLoading = "";
                        } else {
                           mobNameForFileAndMap.setBoth("boat_patch");
                        }
                        break;
                     case "pufferfish_big":
                        mobNameForFileAndMap.setBoth("puffer_fish_big");
                        break;
                     case "pufferfish_medium":
                        mobNameForFileAndMap.setBoth("puffer_fish_medium");
                        break;
                     case "pufferfish_small":
                        mobNameForFileAndMap.setBoth("puffer_fish_small");
                        break;
                     case "shulker":
                        if (this.currentSpecifiedModelLoading.equals("shulker_box")) {
                           mobNameForFileAndMap.setBoth("shulker_box");
                        }
                        break;
                     case "skeleton_skull":
                        mobNameForFileAndMap.setBoth("head_skeleton");
                        break;
                     case "sheep_fur":
                        mobNameForFileAndMap.setBoth("sheep_wool");
                        break;
                     case "sheep_wool_undercoat":
                     case "sheep_baby_wool_undercoat":
                        mobNameForFileAndMap.addFallbackModel(originalLayerName.replace("_wool_undercoat", ""));
                        break;
                     case "trader_llama":
                     case "trader_llama_baby":
                        this.traderLlamaHappened = true;
                        break;
                     case "tropical_fish_large":
                        mobNameForFileAndMap.setBoth("tropical_fish_b");
                        break;
                     case "tropical_fish_large_pattern":
                        mobNameForFileAndMap.setBoth("tropical_fish_pattern_b");
                        break;
                     case "tropical_fish_small":
                        mobNameForFileAndMap.setBoth("tropical_fish_a");
                        break;
                     case "tropical_fish_small_pattern":
                        mobNameForFileAndMap.setBoth("tropical_fish_pattern_a");
                        break;
                     case "wither_skeleton_skull":
                        mobNameForFileAndMap.setBoth("head_wither_skeleton");
                        break;
                     case "zombie_head":
                        mobNameForFileAndMap.setBoth("head_zombie");
                        break;
                     case "undead_horse_armor":
                        if (this.lastUndeadHorse != null) {
                           mobNameForFileAndMap.pushNewMainModelAndMapIdAddingOldAsFallback(this.lastUndeadHorse.replaceAll("_baby", "") + "_armor");
                        }
                        break;
                     case "undead_horse_baby_armor":
                        if (this.lastUndeadHorse != null) {
                           mobNameForFileAndMap.pushNewMainModelAndMapIdAddingOldAsFallback(this.lastUndeadHorse.replaceAll("_baby", "") + "_baby_armor");
                        }
                        break;
                     default:
                        if (!this.currentSpecifiedModelLoading.isBlank() && !this.currentSpecifiedModelLoading.startsWith("emf$")) {
                           String finalMapData = this.currentSpecifiedModelLoading;
                           switch (finalMapData) {
                              case "sign":
                              case "hanging_sign":
                                 String sign = originalLayerName.replace("sign/standing/", "").replace("sign/wall/", "").replace("hanging_sign/", "");
                                 if (originalLayerName.startsWith("sign/standing/")) {
                                    sign = sign + "_sign";
                                    mobNameForFileAndMap.setFileName(sign).setMapIdAndAddFallbackModel("sign");
                                 } else if (originalLayerName.startsWith("sign/wall/")) {
                                    sign = sign + "_wall_sign";
                                    mobNameForFileAndMap.setFileName(sign).setMapIdAndAddFallbackModel("wall_sign").setMapIdAndAddFallbackModel("sign");
                                 } else {
                                    sign = sign + "_hanging_sign";
                                    mobNameForFileAndMap.setFileName(sign).setMapIdAndAddFallbackModel("hanging_sign");
                                 }
                                 break;
                              default:
                                 if (((EMFConfig)EMF.config().getConfig()).automaticModelExporting) {
                                    EMFUtils.log(
                                       "EMF unknown modifiable block entity model identified during loading: " + this.currentSpecifiedModelLoading + ".jem"
                                    );
                                 }

                                 mobNameForFileAndMap.setFileName(this.currentSpecifiedModelLoading)
                                    .setMapIdAndAddFallbackModel(this.currentSpecifiedModelLoading, originalLayerName);
                           }
                        } else if (originalLayerName.contains("/") && layer.getLayer().equals("main")) {
                           handleBoats(originalLayerName, mobNameForFileAndMap);
                        }
                  }
               }
            }

            if (((EMFConfig)EMF.config().getConfig()).automaticModelExporting
               && !this.currentSpecifiedModelLoading.isBlank()
               && this.currentSpecifiedModelLoading.contains(":")) {
               EMFUtils.log("EMF modifiable modded block entity model identified during loading: " + mobNameForFileAndMap.getfileName() + ".jem");
            }

            if (!ResourceLocation.isValidPath(mobNameForFileAndMap.getfileName() + ".jem")) {
               String newValidPath = mobNameForFileAndMap.getfileName().replaceAll("[^a-z0-9/_.-]", "_");
               mobNameForFileAndMap.setBoth(newValidPath, mobNameForFileAndMap.getMapId());
            }

            mobNameForFileAndMap.finishAndPrepAutomatedFallbacks();
            if (mobNameForFileAndMap.getfileName().isBlank()) {
               if (mobNameForFileAndMap.hasFallbackModels()) {
                  mobNameForFileAndMap = mobNameForFileAndMap.getNextFallbackModel();
               } else {
                  if (originalLayerName.isBlank()) {
                     throw new EMFException("Model name is blank, for input layer: " + layer);
                  }

                  mobNameForFileAndMap.setFileName(originalLayerName);
               }
            }

            if (!allowedCEM.allowsAll()) {
               boolean known = EMFModelMappings.isKnownMapping(mobNameForFileAndMap);
               boolean allowed = known ? allowedCEM.allowsKnown() : allowedCEM.allowsUnknowns();
               if (!allowed) {
                  return root;
               }
            }

            this.cache_LayersByModelName.put(mobNameForFileAndMap, layer);
            mobNameForFileAndMap.forEachFallback(fallBack -> this.cache_LayersByModelName.put(fallBack, layer));
            if (printing) {
               EMFUtils.log(" > checking if: [" + mobNameForFileAndMap + "], is allowed as a model name.");
            }

            if (((EMFConfig)EMF.config().getConfig()).isModelDisabled(mobNameForFileAndMap.getMapId())) {
               if (printing) {
                  EMFUtils.logWarn(" > Vanilla model used for: [" + mobNameForFileAndMap + "], because it is disabled in EMF's settings or via the API.");
               }

               ((IEMFModelNameContainer)root).emf$insertKnownMappings(mobNameForFileAndMap);
               return root;
            } else {
               Map<String, String> optifinePartNameMap = EMFModelMappings.getMapOf(mobNameForFileAndMap, root);
               if (printing) {
                  EMFUtils.log(" >> EMF trying to find model: " + mobNameForFileAndMap.getNamespace() + ":optifine/cem/" + mobNameForFileAndMap + ".jem");
               }

               MutableTriple<EMFJemData, ImmutablePair<EMFDirectoryHandler, EMFDirectoryHandler>, EMFModel_ID> modelDataAndContext = this.getJemAndContext(
                  printing, mobNameForFileAndMap, originalLayerBase
               );
               mobNameForFileAndMap.forEachFallback(
                  fallbackModelId -> {
                     if (printing) {
                        EMFUtils.log(" >> EMF trying to find fallback model: " + fallbackModelId.getNamespace() + ":optifine/cem/" + fallbackModelId + ".jem");
                     }

                     MutableTriple<EMFJemData, ImmutablePair<EMFDirectoryHandler, EMFDirectoryHandler>, EMFModel_ID> fallbackModelDataAndContext = this.getJemAndContext(
                        printing, fallbackModelId, originalLayerBase
                     );
                     EMFJemData currentBestOrFirstModel = (EMFJemData)modelDataAndContext.getLeft();
                     EMFJemData fallBackModel = (EMFJemData)fallbackModelDataAndContext.getLeft();
                     boolean isFirstModelInValid = currentBestOrFirstModel == null && ((ImmutablePair)modelDataAndContext.getMiddle()).getRight() == null;
                     if (isFirstModelInValid
                        || currentBestOrFirstModel != null
                           && fallBackModel != null
                           && fallBackModel.directoryContext.packIndex() > currentBestOrFirstModel.directoryContext.packIndex()) {
                        modelDataAndContext.setLeft(fallBackModel);
                        modelDataAndContext.setMiddle((ImmutablePair)fallbackModelDataAndContext.getMiddle());
                        modelDataAndContext.setRight(fallbackModelId);
                     }
                  }
               );
               EMFJemData jemData = (EMFJemData)modelDataAndContext.getLeft();
               ImmutablePair<EMFDirectoryHandler, EMFDirectoryHandler> directoryContextBaseAndVariant = (ImmutablePair<EMFDirectoryHandler, EMFDirectoryHandler>)modelDataAndContext.getMiddle();
               EMFModel_ID finalMapData = (EMFModel_ID)modelDataAndContext.getRight();
               boolean hasVariants = directoryContextBaseAndVariant.getRight() != null;
               if (jemData != null || hasVariants) {
                  if (jemData == null && ((EMFConfig)EMF.config().getConfig()).enforceOptifineVariationRequiresDefaultModel_v2) {
                     EMFUtils.logWarn(
                        "The model ["
                           + finalMapData.getfileName()
                           + "] has variation but does not have a default 'base' model, this is not allowed in the OptiFine format.\nYou may disable this requirement in EMF in the 'model > options' settings. Though it is usually best to preserve OptiFine compatibility.\nYou can get a default model by exporting it in the EMF settings via 'models > allmodels > *model* > export'"
                     );
                  } else {
                     Set<String> optifinePartNames = new HashSet<>();
                     optifinePartNameMap.forEach((optifine, vanilla) -> {
                        if (!optifine.equals("EMPTY")) {
                           optifinePartNames.add(vanilla);
                        }
                     });
                     EMFModelPartRoot emfRoot = new EMFModelPartRoot(
                        finalMapData,
                        Objects.requireNonNullElseGet((EMFDirectoryHandler)directoryContextBaseAndVariant.getLeft(), directoryContextBaseAndVariant::getRight),
                        root,
                        optifinePartNames,
                        new HashMap<>()
                     );
                     if (jemData != null) {
                        emfRoot.addVariantOfJem(jemData, 1);
                        emfRoot.setVariantStateTo(1);
                        this.setupAnimationsFromJemToModel(jemData, emfRoot, 1);
                        emfRoot.containsCustomModel = true;
                        if (hasVariants) {
                           emfRoot.discoverAndInitVariants(originalLayerBase);
                        } else if (!modded
                           && jemData.directoryContext.isSubFolder
                           && ((EMFConfig)EMF.config().getConfig()).enforceOptifineSubFoldersVariantOnly) {
                           EMFUtils.logError(
                              "Error loading ["
                                 + jemData.directoryContext.getFinalFileLocation()
                                 + "] as it is in a subfolder but does not have any variants. This is not allowed in the OptiFine format. You may disable this requirement in EMF's settings at 'model > OptiFine settings'. Though it is usually best to preserve OptiFine compatibility."
                           );
                           throw new Exception("Subfolder without variants, OptiFine compat enabled");
                        }
                     } else {
                        emfRoot.setVariant1ToVanilla0();
                        emfRoot.discoverAndInitVariants(originalLayerBase);
                     }

                     emfRoot.setVariantStateTo(1);
                     if (emfRoot.containsCustomModel) {
                        lastCreatedRootModelPart = emfRoot;
                        if (this.IS_EBE_INSTALLED && this.currentBlockEntityTypeLoading != null && EBETypes.containsKey(this.currentBlockEntityTypeLoading)) {
                           this.EBE_JEMS_FOUND.add(EBETypes.get(this.currentBlockEntityTypeLoading));
                        }

                        try {
                           if (mobNameForFileAndMap.getMapId().equals("armor_stand")) {
                              EMFModelPartVanilla toHide = (EMFModelPartVanilla)emfRoot.getChild("hat");
                              toHide.allKnownStateVariants.entrySet().forEach(entry -> entry.setValue(entry.getValue().copy(false)));
                              toHide.visible = false;
                           }
                        } catch (Exception var19) {
                        }

                        if (printing) {
                           EMFUtils.logWarn(" > EMF model used for: " + mobNameForFileAndMap);
                        }

                        return emfRoot;
                     }
                  }
               }

               if (printing) {
                  EMFUtils.logWarn(" > Vanilla model used for: " + mobNameForFileAndMap);
               }

               ((IEMFModelNameContainer)root).emf$insertKnownMappings(mobNameForFileAndMap);
               return root;
            }
         } catch (Exception var20) {
            EMFUtils.logWarn("default model returned for " + layer + " due to exception: " + var20);
            var20.printStackTrace();
            ((IEMFModelNameContainer)root).emf$insertKnownMappings(mobNameForFileAndMap);
            EMFException.recordException(var20);
            return root;
         }
      }
   }

   public void reloadEnd() {
      if (((EMFConfig)EMF.config().getConfig()).showReloadErrorToast && !this.loadingExceptions.isEmpty()) {
         try {
            ToastComponent toastManager = Minecraft.getInstance().getToasts();
            SystemToast.add(
               toastManager,
               SystemToastId.PERIODIC_NOTIFICATION,
               Component.translatable("entity_model_features.config.load_warn.1"),
               Component.translatable("entity_model_features.config.load_warn.3")
            );
         } catch (Exception var2) {
         }
      }
   }

   @Nullable
   public EMFPartialArmor getArmorParts() {
      if (this.armorParts == null) {
         this.armorParts = new EMFPartialArmor();
      }

      return this.armorParts;
   }

   private MutableTriple<EMFJemData, ImmutablePair<EMFDirectoryHandler, EMFDirectoryHandler>, EMFModel_ID> getJemAndContext(
      boolean printing, EMFModel_ID mobNameForFileAndMap, String possibleBasePropertiesName
   ) {
      EMFDirectoryHandler baseModelDir = EMFDirectoryHandler.getDirectoryManagerOrNull(
         printing, mobNameForFileAndMap.getNamespace(), mobNameForFileAndMap.getfileName(), ".jem"
      );
      EMFDirectoryHandler propertiesOrSecondDir = EMFDirectoryHandler.getDirectoryManagerOrNull(
         printing, mobNameForFileAndMap.getNamespace(), mobNameForFileAndMap.getfileName(), ".properties"
      );
      if (propertiesOrSecondDir == null) {
         propertiesOrSecondDir = EMFDirectoryHandler.getDirectoryManagerOrNull(
            printing, mobNameForFileAndMap.getNamespace(), mobNameForFileAndMap.getfileName(), "2.jem"
         );
      }

      if (baseModelDir != null && !baseModelDir.validForThisBase(propertiesOrSecondDir)) {
         propertiesOrSecondDir = null;
      }

      EMFJemData jemDataFirst = baseModelDir == null ? null : getJemDataWithDirectory(baseModelDir, mobNameForFileAndMap);
      return MutableTriple.of(jemDataFirst, ImmutablePair.of(baseModelDir, propertiesOrSecondDir), mobNameForFileAndMap);
   }

   private void getDoubleChest(ModelPart root, EMFModel_ID mobNameForFileAndMap, boolean isRight, boolean printing) throws EMFException {
      String thisSide = isRight ? "right" : "left";
      String otherSide = isRight ? "left" : "right";
      mobNameForFileAndMap.setBoth(this.currentSpecifiedModelLoading + "_large", "double_chest_" + thisSide);
      if (((EMFConfig)EMF.config().getConfig()).doubleChestAnimFix) {
         if (printing) {
            EMFUtils.log("injecting empty " + otherSide + " side parts into 'double chest' for animation purposes");
         }

         Map<String, ModelPart> newChildren = new HashMap<>(root.children);
         newChildren.putIfAbsent("lid_" + otherSide, new ModelPart(List.of(), Map.of()));
         newChildren.putIfAbsent("base_" + otherSide, new ModelPart(List.of(), Map.of()));
         newChildren.putIfAbsent("knob_" + otherSide, new ModelPart(List.of(), Map.of()));
         root.children = newChildren;
      }

      mobNameForFileAndMap.addFallbackModel(mobNameForFileAndMap.namespace, mobNameForFileAndMap.getfileName());
      mobNameForFileAndMap.setFileName(this.currentSpecifiedModelLoading + "_" + thisSide);
   }

   public void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPartRoot emfRootPart, int variantNum) {
      boolean printing = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;
      HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = new HashMap<>();
      allPartsBySingleAndFullHeirachicalId.put("root", emfRootPart);
      allPartsBySingleAndFullHeirachicalId.putAll(
         emfRootPart.getAllChildPartsAsAnimationMap("", variantNum, EMFModelMappings.getMapOf(emfRootPart.modelName, null))
      );
      OldEMFAnimationHandler oldAnimationHandler = new OldEMFAnimationHandler(jemData.directoryContext.getFileNameWithType());
      if (printing) {
         EMFUtils.log(" > finalAnimationsForModel =");

         for (List<LinkedHashMap<String, String>> animList : jemData.getAllTopLevelAnimationsByVanillaPartName().values()) {
            for (LinkedHashMap<String, String> animMap : animList) {
               for (Entry<String, String> entry : animMap.entrySet()) {
                  EMFUtils.log(" >> " + entry.getKey() + " = " + entry.getValue());
               }
            }
         }
      }

      for (List<LinkedHashMap<String, String>> animList : jemData.getAllTopLevelAnimationsByVanillaPartName().values()) {
         for (LinkedHashMap<String, String> animMap : animList) {
            for (Entry<String, String> animationLine : animMap.entrySet()) {
               String animKey = animationLine.getKey();
               if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
                  EMFUtils.log("parsing animation value: [" + animKey + "]");
               }

               String[] animKeyParts = animKey.split("\\.");
               String modelId = animKeyParts[0];
               String modelVariable = animKeyParts[1];
               EMFModelOrRenderVariable thisVariable = EMFModelOrRenderVariable.get(modelVariable);
               if (thisVariable == null) {
                  thisVariable = EMFModelOrRenderVariable.getRenderVariable(animKey);
               }

               EMFModelPart thisPart = "render".equals(modelId) ? null : getModelFromHierarchicalId(modelId, allPartsBySingleAndFullHeirachicalId);
               oldAnimationHandler.addAnimLineData(new EMFAnimationHandler.AnimLineData(animKey, animationLine.getValue(), thisPart, thisVariable));
            }
         }
      }

      if (!oldAnimationHandler.lines().isEmpty()) {
         ASMAnimationHandler asmHandler = null;
         this.isAnimationValidationPhase = true;

         try {
            Iterator<EMFAnimationHandler.AnimLineData> animMapIterate = oldAnimationHandler.lines().iterator();
            AnimSetupContext context = new AnimSetupContext(
               jemData.directoryContext.getFileNameWithType(), oldAnimationHandler, allPartsBySingleAndFullHeirachicalId
            );

            while (animMapIterate.hasNext()) {
               EMFAnimationHandler.AnimLineData line = animMapIterate.next();
               context.animKey = line.animKey;
               MathComponent emfCalculator = MathExpressionParser.getOptimizedExpression(line.expression, false, context);
               context.animKey = null;
               if (emfCalculator == MathExpressionParser.NULL_EXPRESSION) {
                  EMFUtils.logError(
                     "animation was invalid: [" + line.animKey + "] = [" + line.expression + "] in model [" + oldAnimationHandler.modelName + "]"
                  );
                  this.isAnimationValidationPhase = false;
                  return;
               }

               oldAnimationHandler.addParsedLine(line, emfCalculator);
            }

            if (((EMFConfig)EMF.config().getConfig()).asmMaths) {
               ASMVariableHandler varHandler = new ASMVariableHandler();
               ASMParser.ASMExecutor executor = ASMParser.compileOrNull(oldAnimationHandler, varHandler);
               if (executor == null) {
                  EMFUtils.logError("ASM animation was invalid: for model [" + oldAnimationHandler.modelName + "]");
                  this.isAnimationValidationPhase = false;
                  return;
               }

               asmHandler = new ASMAnimationHandler(executor, varHandler, context);
            }
         } finally {
            this.isAnimationValidationPhase = false;
         }

         EMFAnimationHandler handler = (EMFAnimationHandler)(asmHandler == null ? oldAnimationHandler : asmHandler);
         if (handler.finishAndValidate()) {
            emfRootPart.receiveAnimationHandler(variantNum, handler);
         }
      }
   }
}
