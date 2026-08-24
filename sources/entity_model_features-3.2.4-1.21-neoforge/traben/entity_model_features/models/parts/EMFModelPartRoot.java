package traben.entity_model_features.models.parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFDirectoryHandler;
import traben.entity_model_features.utils.EMFResourceCaching;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.ETFApi.ETFVariantSuffixProvider;
import traben.entity_texture_features.features.property_reading.PropertiesRandomProvider;
import traben.entity_texture_features.utils.ETFLruCache.UUIDInteger;

public class EMFModelPartRoot extends EMFModelPartVanilla {
   public final EMFModel_ID modelName;
   public final ModelPart vanillaRoot;
   @NotNull
   public final UUIDInteger entitySuffixMap = new UUIDInteger();
   private final Map<String, EMFModelPartVanilla> allVanillaParts;
   private final Map<Integer, ModelPart> vanillaFormatModelPartOfEachState = new HashMap<>();
   public EMFDirectoryHandler directoryContext;
   public ETFVariantSuffixProvider variantTester = null;
   public boolean containsCustomModel = false;
   public boolean containsCustomAnims = false;
   private long lastMobCountAnimatedOn = 0L;
   private boolean hasRemovedTopLevelJemTextureFromChildren = false;
   private boolean hasArmItemOverrides = false;
   public boolean isMainModel = false;
   private Set<String> registeredSet = new HashSet<>();
   private final HashMap<Integer, Runnable> animations = new HashMap<>();
   @Nullable
   private Runnable animation = null;
   private ResourceLocation jemLevelOverride = null;

   @Override
   public EMFModelPartRoot getRoot() {
      return this;
   }

   public EMFModelPartRoot(
      EMFModel_ID mobNameForFileAndMap,
      EMFDirectoryHandler directoryContext,
      ModelPart vanillaRoot,
      Collection<String> optifinePartNames,
      Map<String, EMFModelPartVanilla> mapForCreatedParts
   ) {
      super("root", vanillaRoot, optifinePartNames, mapForCreatedParts, null);
      this.allVanillaParts = mapForCreatedParts;
      this.allVanillaParts.putIfAbsent(this.name, this);
      this.modelName = mobNameForFileAndMap;
      this.directoryContext = directoryContext;
      this.vanillaRoot = vanillaRoot;
   }

   @Override
   protected float[] debugBoxColor() {
      return new float[]{1.0F, 0.0F, 0.0F};
   }

   public Collection<EMFModelPartVanilla> getAllVanillaPartsEMF() {
      return this.allVanillaParts.values();
   }

   public void oneTimeRunnable() {
      this.registerModelRunnableWithEntityTypeContext();
   }

   private void registerModelRunnableWithEntityTypeContext() {
      EMFEntityRenderState entity = EMFAnimationEntityContext.getEmfState();
      if (entity != null) {
         String type = entity.typeString();
         if (this.registeredSet.contains(type)) {
            return;
         }

         this.registeredSet.add(type);
         EMFConfig config = (EMFConfig)EMF.config().getConfig();
         if (config.debugOnRightClick) {
            EMFManager.getInstance().rootPartsPerEntityTypeForDebug.computeIfAbsent(type, k -> new HashSet<>()).add(this);
         }

         EMFManager.getInstance().rootPartsPerEntityTypeForVariation.computeIfAbsent(type, k -> new HashSet<>()).add(this);
         if (this.variantTester != null && config.logModelCreationData) {
            EMFUtils.log("Registered new variating model for: " + type);
         }
      }
   }

   public void doVariantCheck() {
      EMFEntityRenderState emfState = EMFAnimationEntityContext.getEmfState();
      if (this.variantTester != null && emfState != null) {
         UUID id = emfState.uuid();
         int finalSuffix = (Integer)this.entitySuffixMap.get(id);
         if (finalSuffix != -1) {
            this.checkIfShouldExpireEntity(id);
         } else {
            finalSuffix = Math.max(1, this.variantTester.getSuffixForETFEntity(emfState));
            this.entitySuffixMap.put(id, finalSuffix);
         }

         EMFManager.getInstance().lastModelSuffixOfEntity.put(id, finalSuffix);
         this.setVariantStateTo(finalSuffix);
      } else {
         this.setVariantStateTo(1);
      }
   }

   public void checkIfShouldExpireEntity(UUID id) {
      if (this.variantTester.entityCanUpdate(id)) {
         switch (((EMFConfig)EMF.config().getConfig()).modelUpdateFrequency) {
            case Never:
               break;
            case Instant:
               this.entitySuffixMap.remove(id);
               break;
            default:
               int delay = ((EMFConfig)EMF.config().getConfig()).modelUpdateFrequency.getDelay();
               int time = (int)(EMFAnimationEntityContext.getTime() % delay);
               if (time == Math.abs(id.hashCode()) % delay) {
                  this.entitySuffixMap.remove(id);
               }
         }
      }
   }

   public void addVariantOfJem(EMFJemData jemData, int variant) {
      if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
         EMFUtils.log(" > " + jemData.getMobModelIDInfo().getfileName() + ", constructing variant #" + variant);
      }

      if (jemData.hasAttachments) {
         this.hasArmItemOverrides = true;
      }

      Map<String, EMFModelPartCustom> newEmfParts = new HashMap<>();

      for (EMFPartData part : jemData.models) {
         if (part.part != null) {
            String idUnique = EMFUtils.getIdUnique(newEmfParts.keySet(), part.id);
            newEmfParts.put(idUnique, new EMFModelPartCustom(part, variant, part.part, idUnique, this));
         }
      }

      ResourceLocation rootTextureOverride = jemData.getCustomTexture();

      for (Entry<String, EMFModelPartVanilla> vanillaEntry : this.allVanillaParts.entrySet()) {
         EMFModelPartVanilla thisPart = vanillaEntry.getValue();
         EMFModelPartWithState.EMFModelState vanillaState = thisPart.allKnownStateVariants.get(0).copy();
         thisPart.setFromState(vanillaState);
         thisPart.textureOverride = rootTextureOverride;
         Map<String, ModelPart> children = new HashMap<>(thisPart.vanillaChildren);

         for (Entry<String, EMFModelPartCustom> newPartEntry : newEmfParts.entrySet()) {
            EMFModelPartCustom newPart = newPartEntry.getValue();
            if (vanillaEntry.getKey().equals(newPart.partToBeAttached)) {
               if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
                  EMFUtils.log(" > > > EMF custom part attached: " + newPartEntry.getKey());
               }

               if (!newPart.attach) {
                  thisPart.cubes = List.of();
                  thisPart.children.values().forEach(partx -> {
                     if (partx instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified) {
                        vanilla.setHideInTheseStates(variant);
                     }
                  });
               }

               children.put(EMFUtils.getIdUnique(thisPart.children.keySet(), newPartEntry.getKey()), newPart);
            }
         }

         thisPart.children = children;
         thisPart.allKnownStateVariants.put(variant, thisPart.getCurrentState());
      }

      this.allKnownStateVariants.putIfAbsent(variant, this.allKnownStateVariants.get(0).copy());
   }

   public void discoverAndInitVariants(String fallbackPropertiesName) {
      boolean printing = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;
      String thisDirectoryFileName = this.directoryContext.getRelativeDirectoryLocationNoValidation(this.directoryContext.rawFileName);
      ResourceLocation propertyID = this.directoryContext.getRelativeFilePossiblyEMFOverridden(this.directoryContext.rawFileName + ".properties");
      if (printing) {
         EMFUtils.log(" > checking properties file: " + propertyID + " for: " + thisDirectoryFileName + ".jem");
      }

      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
      boolean exists = EMFResourceCaching.resourceExists(resourceManager, propertyID);
      if (!exists && ((EMFConfig)EMF.config().getConfig()).allowOptifineFallbackProperties) {
         ResourceLocation fallbackPropertiesID = this.directoryContext.getRelativeFilePossiblyEMFOverridden(fallbackPropertiesName + ".properties");
         if (printing) {
            EMFUtils.log(" > checking fallback .properties file: " + fallbackPropertiesID + " for: " + thisDirectoryFileName + ".jem");
         }

         exists = EMFResourceCaching.resourceExists(resourceManager, fallbackPropertiesID);
         if (exists) {
            propertyID = fallbackPropertiesID;
         }
      }

      if (exists) {
         if (printing) {
            EMFUtils.log(" > found properties file: " + propertyID + " for: " + thisDirectoryFileName + ".jem");
         }

         this.variantTester = ETFApi.getVariantSupplierOrNull(propertyID, EMFUtils.res(thisDirectoryFileName + ".jem"), new String[]{"models"});
         if (this.variantTester instanceof PropertiesRandomProvider propertiesRandomProvider) {
            propertiesRandomProvider.setOnMeetsRuleHook((entity, rule) -> {
               if (rule == null) {
                  EMFManager.getInstance().lastModelRuleOfEntity.remove(entity.uuid());
               } else {
                  EMFManager.getInstance().lastModelRuleOfEntity.put(entity.uuid(), rule.ruleNumber);
               }
            });
         }

         if (this.variantTester != null) {
            Set<Integer> allModelVariants = this.variantTester.getAllSuffixes();
            allModelVariants.remove(1);
            allModelVariants.remove(0);
            if (!allModelVariants.isEmpty()) {
               for (int variant : allModelVariants) {
                  this.setVariantStateTo(1);
                  EMFDirectoryHandler variantDirectoryContext = EMFDirectoryHandler.getDirectoryManagerOrNull(
                     ((EMFConfig)EMF.config().getConfig()).logModelCreationData,
                     this.directoryContext.namespace,
                     this.directoryContext.rawFileName,
                     variant + ".jem"
                  );
                  boolean canUseVariant = this.directoryContext.validForThisBase(variantDirectoryContext);
                  if (printing) {
                     EMFUtils.log(
                        " > incorporating variant jem file: " + this.directoryContext.namespace + ":" + this.directoryContext.rawFileName + variant + ".jem"
                     );
                  }

                  EMFJemData jemDataVariant = canUseVariant ? EMFManager.getJemDataWithDirectory(variantDirectoryContext, this.modelName) : null;
                  if (jemDataVariant != null) {
                     this.addVariantOfJem(jemDataVariant, variant);
                     this.setVariantStateTo(variant);
                     EMFManager.getInstance().setupAnimationsFromJemToModel(jemDataVariant, this, variant);
                     this.containsCustomModel = true;
                  } else {
                     this.allKnownStateVariants.put(variant, this.allKnownStateVariants.get(1));
                     if (printing) {
                        EMFUtils.log(
                           " > invalid jem variant file: " + this.directoryContext.namespace + ":" + this.directoryContext.rawFileName + variant + ".jem"
                        );
                     }
                  }
               }
            } else if (printing) {
               EMFUtils.logWarn("properties with only 1 variant found: " + propertyID + ".");
            }
         } else {
            if (printing) {
               EMFUtils.logWarn("null properties found for: " + propertyID);
            }

            this.directoryContext = null;
         }
      } else {
         if (printing) {
            EMFUtils.logWarn("no properties or variants found for found for: [" + thisDirectoryFileName + ".jem]");
         }

         this.directoryContext = null;
      }
   }

   public void setVariant1ToVanilla0() {
      this.allKnownStateVariants.put(1, this.allKnownStateVariants.get(0));
      this.allVanillaParts.forEach((k, child) -> child.allKnownStateVariants.put(1, child.allKnownStateVariants.get(0)));
   }

   public void tryRenderVanillaRootNormally(PoseStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
      if (this.vanillaRoot != null) {
         matrixStack.pushPose();
         if (((EMFConfig)EMF.config().getConfig()).getVanillaHologramModeFor(EMFAnimationEntityContext.getEMFEntity())
            == EMFConfig.VanillaModelRenderMode.OFFSET) {
            matrixStack.translate(1.0F, 0.0F, 0.0F);
         }

         this.vanillaRoot.render(matrixStack, vertexConsumer, light, overlay);
         matrixStack.popPose();
      }
   }

   public boolean hasAnimation() {
      return this.animation != null;
   }

   public void triggerManualAnimation(PoseStack pose) {
      if (this.animation != null) {
         this.animation.run();
      }

      this.checkArmOverrides(pose);
   }

   public void checkArmOverrides(PoseStack pose) {
      if (this.hasArmItemOverrides) {
         this.processArmItemOverrides(pose);
      }
   }

   public ModelPart getVanillaFormatRoot() {
      if (!this.vanillaFormatModelPartOfEachState.containsKey(this.currentModelVariant)) {
         this.vanillaFormatModelPartOfEachState.put(this.currentModelVariant, this.getVanillaModelPartsOfCurrentState());
      }

      return this.vanillaFormatModelPartOfEachState.get(this.currentModelVariant);
   }

   @Override
   public void setVariantStateTo(int newVariant) {
      super.setVariantStateTo(newVariant);
      this.animation = this.animations.get(newVariant);
   }

   public void animate() {
      if (this.animation != null && !EMFAnimationEntityContext.isEntityAnimPausedWrapped()) {
         this.animation.run();
      }
   }

   public void receiveAnimationHandler(int variant, EMFAnimationHandler animationHandler) {
      this.containsCustomAnims = true;
      Runnable run = () -> {
         if (this.lastMobCountAnimatedOn != EMFManager.getInstance().entityRenderCount) {
            this.lastMobCountAnimatedOn = EMFManager.getInstance().entityRenderCount;
            if (EMFAnimationEntityContext.isFirstPersonHand && ((EMFConfig)EMF.config().getConfig()).preventFirstPersonHandAnimating) {
               return;
            }

            try {
               animationHandler.animate(EMFAnimationEntityContext.getEntityPartsAnimPaused());
            } catch (Throwable var4) {
               EMFUtils.logError("Error in animation for model [" + this.modelName.getfileName() + "].");
               var4.printStackTrace();
               EMFUtils.logError("Disabling all animations for model: [" + this.modelName + "]");
               this.animations.remove(variant);
               this.animation = null;
               this.containsCustomAnims = false;
            }
         }
      };
      this.animations.put(variant, run);
   }

   public ResourceLocation getTopLevelJemTexture() {
      if (this.hasRemovedTopLevelJemTextureFromChildren) {
         return this.jemLevelOverride;
      } else {
         this.hasRemovedTopLevelJemTextureFromChildren = true;
         this.jemLevelOverride = this.textureOverride;
         if (this.textureOverride != null) {
            this.allVanillaParts.values().forEach(emf -> {
               if (emf.textureOverride.equals(this.textureOverride)) {
                  emf.textureOverride = null;
               }
            });
         }

         return this.jemLevelOverride;
      }
   }

   public void resetVanillaPartsToDefaults() {
      this.resetState();
      this.allVanillaParts.values().forEach(EMFModelPartWithState::resetState);
   }

   @Override
   public String toString() {
      return "[EMF root part of " + this.modelName.getfileName() + "]";
   }

   @Override
   public String toStringShort() {
      return "[EMF root part of " + this.modelName.getfileName() + "]";
   }
}
