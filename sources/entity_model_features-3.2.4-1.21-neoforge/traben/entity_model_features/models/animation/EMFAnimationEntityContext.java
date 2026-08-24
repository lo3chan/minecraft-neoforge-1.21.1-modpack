package traben.entity_model_features.models.animation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.mixins.accessor.MinecraftClientAccessor;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.ETFConfig.RenderLayerOverride;

@Deprecated
public final class EMFAnimationEntityContext {
   private static final Map<UUID, Integer> knownHighestAngerTimeByUUID = new HashMap<UUID, Integer>() {
      public Integer get(Object key) {
         return super.getOrDefault(key, 0);
      }
   };
   private static final Map<String, Integer> lodEntityTimers = new HashMap<>();
   public static boolean setInHand = false;
   public static boolean isFirstPersonHand = false;
   public static boolean setInItemFrame = false;
   public static boolean setIsOnHead = false;
   public static boolean setIsInGui = false;
   @Deprecated
   public static double lastFOV = 70.0;
   public static boolean is_in_ground_override = false;
   public static Set<UUID> entitiesToForceVanillaModel = new HashSet<>();
   private static float shadowSize = 0.0F / 0.0F;
   private static float shadowOpacity = 0.0F / 0.0F;
   private static float leashX = 0.0F / 0.0F;
   private static float leashY = 0.0F / 0.0F;
   private static float leashZ = 0.0F / 0.0F;
   private static float shadowX = 0.0F / 0.0F;
   private static float shadowZ = 0.0F / 0.0F;
   private static float limbAngle = 0.0F / 0.0F;
   private static float limbDistance = 0.0F / 0.0F;
   private static float headYaw = 0.0F / 0.0F;
   private static float headPitch = 0.0F / 0.0F;
   private static boolean onShoulder = false;
   private static Function<ResourceLocation, RenderType> layerFactory = null;
   private static Boolean lodFrameSkipping = null;
   private static boolean announceModels = false;
   private static float frameCounter = 0.0F;
   private static boolean isLayerPhase = false;
   private static boolean checkedIfIEmotePlayerExists = false;
   private static Class<?> iEmotePlayerEntityType = null;
   private static Method isPlayingEmoteMethod = null;
   public static HashMap<UUID, ModelPart[]> entitiesPausedParts = new HashMap<>();
   public static Set<UUID> entitiesPaused = new HashSet<>();
   public static List<Function<EMFEntity, Boolean>> pauseListeners = new ArrayList<>();
   public static List<Function<EMFEntity, Boolean>> forceVanillaModelListeners = new ArrayList<>();
   private static EMFEntityRenderState emfState = null;

   public static boolean isEntityAnimPaused() {
      if (emfState == null) {
         return false;
      } else {
         EMFEntity entity = emfState.emfEntity();
         if (entity != null) {
            if (isPlayerEmoting(entity)) {
               return true;
            }

            for (Function<EMFEntity, Boolean> pauseListener : pauseListeners) {
               try {
                  if (pauseListener.apply(entity)) {
                     return true;
                  }
               } catch (Exception var4) {
               }
            }
         }

         return entitiesPaused.contains(emfState.uuid());
      }
   }

   public static boolean isEntityAnimPausedWrapped() {
      if (emfState == null) {
         return false;
      } else {
         try {
            return isEntityAnimPaused();
         } catch (Exception var3) {
            try {
               return isEntityAnimPausedBackupDontMixinToThisOneUseTheNewAPI();
            } catch (Exception var2) {
               return false;
            }
         }
      }
   }

   private static boolean isEntityAnimPausedBackupDontMixinToThisOneUseTheNewAPI() {
      if (emfState == null) {
         return false;
      } else {
         EMFEntity entity = emfState.emfEntity();
         if (entity != null) {
            if (isPlayerEmoting(entity)) {
               return true;
            }

            for (Function<EMFEntity, Boolean> pauseListener : pauseListeners) {
               try {
                  if (pauseListener.apply(entity)) {
                     return true;
                  }
               } catch (Exception var4) {
               }
            }
         }

         return entitiesPaused.contains(emfState.uuid());
      }
   }

   private static boolean isPlayerEmoting(EMFEntity entity) {
      if (entity instanceof Player player) {
         Method emoteMethod = getIsPlayingEmoteMethod();
         if (emoteMethod == null) {
            return false;
         } else {
            try {
               return (Boolean)emoteMethod.invoke(player);
            } catch (Exception var4) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   private static Class<?> getIEmotePlayerEntityType() {
      if (checkedIfIEmotePlayerExists) {
         return iEmotePlayerEntityType;
      } else {
         checkedIfIEmotePlayerExists = true;

         try {
            iEmotePlayerEntityType = Class.forName("io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity");
         } catch (ClassNotFoundException var1) {
            iEmotePlayerEntityType = null;
         }

         return iEmotePlayerEntityType;
      }
   }

   @Nullable
   private static Method getIsPlayingEmoteMethod() {
      if (isPlayingEmoteMethod != null) {
         return isPlayingEmoteMethod;
      } else {
         Class<?> emotePlayerType = getIEmotePlayerEntityType();
         if (emotePlayerType == null) {
            return null;
         } else {
            try {
               isPlayingEmoteMethod = emotePlayerType.getMethod("isPlayingEmote");
            } catch (NoSuchMethodException var2) {
               isPlayingEmoteMethod = null;
            }

            return isPlayingEmoteMethod;
         }
      }
   }

   @Nullable
   public static ModelPart[] getEntityPartsAnimPaused() {
      if (emfState == null) {
         return null;
      } else {
         ModelPart[] parts = entitiesPausedParts.get(emfState.uuid());
         return parts != null && parts.length != 0 ? parts : null;
      }
   }

   private EMFAnimationEntityContext() {
   }

   public static void incFrameCount() {
      if (!IrisShadowPassDetection.getInstance().inShadowPass()) {
         float inc = frameCounter + 1.0F;
         frameCounter = inc > 27719.0F ? 0.0F : inc;
      }
   }

   public static float getFrameCounter() {
      return frameCounter;
   }

   public static boolean isJumping() {
      return emfEntity() instanceof LivingEntity alive && alive.jumping;
   }

   public static void setEntityVariable(String variable, float value) {
      if (emfState != null) {
         emfState.variableMap().put(variable, value);
      }
   }

   public static float getEntityVariable(String variable, float defaultValue) {
      return emfState == null ? defaultValue : emfState.variableMap().getOrDefault(variable, defaultValue);
   }

   public static void setLayerFactory(Function<ResourceLocation, RenderType> layerFactory) {
      if (!(emfEntity() instanceof Arrow)) {
         EMFEntityRenderState state = getEmfState();
         if (state != null && state.layerFactory() == null) {
            state.setLayerFactory(layerFactory);
         }

         EMFAnimationEntityContext.layerFactory = layerFactory;
      }
   }

   private static int distanceOfEntityFrom(BlockPos pos) {
      if (emfState == null) {
         return 0;
      } else {
         BlockPos blockPos = emfState.blockPos();
         float f = blockPos.getX() - pos.getX();
         float g = blockPos.getY() - pos.getY();
         float h = blockPos.getZ() - pos.getZ();
         return (int)Mth.sqrt(f * f + g * g + h * h);
      }
   }

   private static int getLODFactorOfEntity() {
      if (((EMFConfig)EMF.config().getConfig()).animationLODDistance == 0) {
         return 0;
      } else if (Minecraft.getInstance().player != null && !Minecraft.getInstance().player.isScoping()) {
         int distance = distanceOfEntityFrom(Minecraft.getInstance().player.blockPosition());
         if (distance < 1) {
            return 0;
         } else {
            int factor = distance / ((EMFConfig)EMF.config().getConfig()).animationLODDistance;
            int factorByFOV = (int)(factor * lastFOV / 70.0);
            int lodFactor;
            if (((EMFConfig)EMF.config().getConfig()).retainDetailOnLowFps && Minecraft.getInstance().getFps() < 59) {
               float fpsPercentageOf60 = Minecraft.getInstance().getFps() / 60.0F;
               lodFactor = (int)(factorByFOV * fpsPercentageOf60);
            } else {
               lodFactor = factorByFOV;
            }

            if (((EMFConfig)EMF.config().getConfig()).retainDetailOnLargerMobs && emfEntity() instanceof Entity entity) {
               float entitySize = Math.max(entity.getBbWidth(), entity.getBbHeight());
               if (entitySize > 2.0F) {
                  lodFactor = (int)(lodFactor / (entitySize / 2.0F));
               }
            }

            return lodFactor;
         }
      } else {
         return 0;
      }
   }

   public static boolean isLODSkippingThisFrame(String modelId) {
      if (isInGui() || isOnShoulder()) {
         return false;
      } else if (lodFrameSkipping != null) {
         return lodFrameSkipping;
      } else if (!ETF.IRIS_DETECTED
         || !IrisShadowPassDetection.getInstance().inShadowPass()
         || !((EMFConfig)EMF.config().getConfig()).animationFrameSkipDuringIrisShadowPass
         || getEMFEntity() instanceof Player player && player.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
         if (((EMFConfig)EMF.config().getConfig()).animationLODDistance != 0 && emfState != null) {
            EntityType<?> type = emfState.entityType();
            if (type == EntityType.VILLAGER || type == EntityType.HORSE) {
               return false;
            } else if (type == EntityType.BLAZE) {
               return false;
            } else {
               String lodKey = emfState.uuid() + modelId;
               int lodTimer = lodEntityTimers.getOrDefault(lodKey, 0);
               int lodResult;
               if (lodTimer < 1) {
                  lodResult = getLODFactorOfEntity();
               } else {
                  lodResult = lodTimer - 1;
               }

               lodEntityTimers.put(lodKey, lodResult);
               lodFrameSkipping = lodResult > 0;
               return lodFrameSkipping;
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static EMFEntityRenderState getEmfState() {
      return emfState;
   }

   @Deprecated
   private static EMFEntity emfEntity() {
      return emfState == null ? null : emfState.emfEntity();
   }

   public static void setCurrentEntityIteration(@Nullable EMFEntityRenderState state) {
      setCurrentEntityIteration(state, false);
   }

   public static void setCurrentEntityIteration(@Nullable EMFEntityRenderState state, boolean skipModelVariation) {
      isFirstPersonHand = false;
      EMFManager.getInstance().entityRenderCount++;
      layerFactory = null;
      shadowSize = 0.0F / 0.0F;
      shadowOpacity = 0.0F / 0.0F;
      leashX = 0.0F;
      leashY = 0.0F;
      leashZ = 0.0F;
      shadowX = 0.0F;
      shadowZ = 0.0F;
      newEntity(state);
      if (state != null) {
         if (!skipModelVariation) {
            Set<EMFModelPartRoot> roots = EMFManager.getInstance().rootPartsPerEntityTypeForVariation.get(state.typeString());
            if (roots != null) {
               if (isEntityForcedToVanillaModel()) {
                  roots.forEach(root -> root.setVariantStateTo(0));
               } else {
                  roots.forEach(EMFModelPartRoot::doVariantCheck);
               }

               if (state.emfEntity() instanceof Player player && ((EMFConfig)EMF.config().getConfig()).resetPlayerModelEachRender_v2) {
                  roots.forEach(EMFModelPartRoot::resetVanillaPartsToDefaults);
               }
            }
         }

         if (((EMFConfig)EMF.config().getConfig()).debugOnRightClick && state.uuid().equals(EMFManager.getInstance().entityForDebugPrint)) {
            announceModels = true;
            EMFManager.getInstance().entityForDebugPrint = null;
         }
      }

      lodFrameSkipping = null;
   }

   public static void anounceModels(EMFEntityRenderState assertEntity) {
      String type = assertEntity.typeString();
      Set<EMFModelPartRoot> debugRoots = EMFManager.getInstance().rootPartsPerEntityTypeForDebug.get(type);
      EMFUtils.chat("§e-----------EMF Debug Printout-------------§r");
      if (debugRoots == null) {
         EMFUtils.chat("\n§c§oThe EMF debug printout did not find any custom models registered to the following entity:\n §3§l§u" + type);
      } else {
         String message = "\n§2§oThe EMF debug printout found the following custom models for the entity:\n §3§l§u"
            + type
            + "§r\n§2§oThis first model is usually the primary model for the entity.";
         EMFUtils.chat(message);
         int count = 1;

         for (EMFModelPartRoot debugRoot : debugRoots) {
            StringBuilder model = new StringBuilder();
            model.append("§eModel #").append(count).append("§r").append(entryAndValue("name", debugRoot.modelName.getfileName() + ".jem"));
            if (debugRoot.modelName.hasFallbackModels()) {
               model.append("\n§eFallback Models:§r");
               debugRoot.modelName.forEachFallback(modelId -> model.append("\n§6 - §r").append(modelId.getfileName()));
            }

            if (debugRoot.directoryContext != null) {
               model.append(entryAndValue("directory", debugRoot.directoryContext.getRelativeDirectoryLocationNoValidation(debugRoot.modelName.getfileName())));
            }

            if (debugRoot.textureOverride != null) {
               model.append(entryAndValue("texture_override", debugRoot.textureOverride.toString()));
            }

            if (debugRoot.variantTester != null) {
               Set<Integer> set = new HashSet<>(debugRoot.allKnownStateVariants.keySet());
               set.remove(0);
               model.append(entryAndValue("model_variants", set.toString()))
                  .append(entryAndValue("current_variant", String.valueOf(debugRoot.currentModelVariant)));
            }

            EMFUtils.chat(model + "\n§6 - parts:§r printed in game log only.");
            EMFUtils.log("\n - parts: " + debugRoot.simplePrintChildren(0));
            count++;
         }
      }

      EMFUtils.chat("\n§e----------------------------------------§r");
      if (!EMFManager.getInstance().modelsAnnounced.isEmpty()) {
         String vanillaMessage = "\n§2§oThe EMF debug printout found the following non-custom models for the entity:\n §3§l§u"
            + type
            + "§r\n§2§oThis first model is usually the primary model for the entity.";
         EMFUtils.chat(vanillaMessage);
         int count = 1;

         for (EMFModel_ID data : EMFManager.getInstance().modelsAnnounced) {
            StringBuilder modelx = new StringBuilder();
            modelx.append("\n§eNon-Custom Model #").append(count).append("§r").append(entryAndValue("possible .jem name", data.getDisplayFileName()));
            if (data.hasFallbackModels()) {
               modelx.append("§eFallback Models:§r");
               data.forEachFallback(modelId -> model.append("\n§6 - §r").append(modelId.getfileName()));
            }

            Map<String, String> map = EMFModelMappings.getMapOf(data, null);
            if (!map.isEmpty()) {
               EMFUtils.chat(modelx + "\n§6 - part names:§r printed in game log only.");
               StringBuilder parts = new StringBuilder();
               parts.append("\n - part names: ");
               map.forEach((k, v) -> parts.append("\n   | - [").append(k).append(']'));
               EMFUtils.log(parts.toString());
            } else {
               EMFUtils.chat(modelx.toString());
               EMFUtils.log(" - part names: could not be found. use the 'printout unknown models' setting instead.");
            }
         }

         EMFUtils.chat("\n§e----------------------------------------§r");
         EMFManager.getInstance().modelsAnnounced.clear();
      }

      announceModels = false;
   }

   public static boolean doAnnounceModels() {
      return announceModels;
   }

   private static String entryAndValue(String entry, String value) {
      return "\n§6 - " + entry + ":§r " + value;
   }

   public static void setCurrentEntityNoIteration(@Nullable EMFEntityRenderState state) {
      newEntity(state);
   }

   private static void newEntity(@Nullable EMFEntityRenderState state) {
      emfState = state;
      limbAngle = 0.0F / 0.0F;
      limbDistance = 0.0F / 0.0F;
      headYaw = 0.0F / 0.0F;
      headPitch = 0.0F / 0.0F;
      if (state != null) {
         if (state.entity() instanceof Arrow) {
            layerFactory = RenderType::entityCutout;
         } else if (state.isBlockEntity()) {
            layerFactory = RenderType::entitySolid;
         }

         if (state.layerFactory() == null) {
            state.setLayerFactory(layerFactory);
         }
      }

      onShoulder = false;
   }

   public static void globalReset() {
      reset();
      frameCounter = 0.0F;
   }

   public static boolean isLayerPhase() {
      return isLayerPhase;
   }

   public static void setLayerPhase() {
      isLayerPhase = true;
   }

   public static void unsetLayerPhase() {
      isLayerPhase = false;
   }

   public static void reset() {
      isFirstPersonHand = false;
      layerFactory = null;
      emfState = null;
      limbAngle = 0.0F / 0.0F;
      limbDistance = 0.0F / 0.0F;
      headYaw = 0.0F / 0.0F;
      headPitch = 0.0F / 0.0F;
      onShoulder = false;
      shadowSize = 0.0F / 0.0F;
      shadowOpacity = 0.0F / 0.0F;
      leashX = 0.0F;
      leashY = 0.0F;
      leashZ = 0.0F;
      shadowX = 0.0F;
      shadowZ = 0.0F;
      lodFrameSkipping = null;
      isLayerPhase = false;
   }

   public static RenderType getLayerFromRecentFactoryOrETFOverrideOrTranslucent(ResourceLocation identifier) {
      if (layerFactory == null) {
         RenderLayerOverride layer = ((ETFConfig)ETF.config().getConfig()).getRenderLayerOverride();
         if (layer == null) {
            return RenderType.entityTranslucent(identifier);
         } else {
            return switch (layer) {
               case TRANSLUCENT -> RenderType.entityTranslucent(identifier);
               case TRANSLUCENT_CULL -> RenderType.entityTranslucentCull(identifier);
               case END -> RenderType.endGateway();
               case OUTLINE -> RenderType.outline(identifier);
               default -> throw new MatchException(null, null);
            };
         }
      } else {
         return layerFactory.apply(identifier);
      }
   }

   public static float getRuleIndex() {
      return emfState == null ? 0.0F : ((Integer)EMFManager.getInstance().lastModelRuleOfEntity.get(emfState.uuid())).intValue();
   }

   public static boolean isEntityForcedToVanillaModel() {
      if (emfState == null) {
         return false;
      } else if (entitiesToForceVanillaModel.contains(emfState.uuid())) {
         return true;
      } else {
         try {
            EntityType<?> type = emfState.entity().etf$getType();
            if (type != null && type.toString().contains("customnpc")) {
               CompoundTag nbtTags = emfState.entity().etf$getNbt();
               if (((LivingEntity)emfState.entity()).getMaxHealth() == 777.0F
                  || nbtTags.contains("PuppetStanding")
                  || nbtTags.contains("PuppetMoving")
                  || nbtTags.contains("PuppetAttacking")
                  || nbtTags.contains("PuppetAnimate")) {
                  return true;
               }
            }

            Iterator var5 = forceVanillaModelListeners.iterator();

            Function<EMFEntity, Boolean> check;
            do {
               if (!var5.hasNext()) {
                  return ((EMFConfig)EMF.config().getConfig()).onlyClientPlayerModel && getEMFEntity() instanceof Player player && !player.isLocalPlayer();
               }

               check = (Function<EMFEntity, Boolean>)var5.next();
            } while (!check.apply((EMFEntity)emfState.entity()));

            return true;
         } catch (Exception var3) {
            return ((EMFConfig)EMF.config().getConfig()).onlyClientPlayerModel && getEMFEntity() instanceof Player player && !player.isLocalPlayer();
         }
      }
   }

   @Deprecated
   @Nullable
   public static EMFEntity getEMFEntity() {
      return emfEntity();
   }

   public static float getDimension() {
      if (emfState != null && emfState.world() != null) {
         Optional<ResourceKey<DimensionType>> optional = emfState.world().dimensionTypeRegistration().unwrapKey();
         if (optional.isEmpty()) {
            return 0.0F;
         } else {
            ResourceLocation id = optional.get().location();
            if (id.equals(BuiltinDimensionTypes.NETHER_EFFECTS)) {
               return -1.0F;
            } else {
               return id.equals(BuiltinDimensionTypes.END_EFFECTS) ? 1.0F : 0.0F;
            }
         }
      } else {
         return 0.0F;
      }
   }

   public static float getPlayerX() {
      return Minecraft.getInstance().player == null
         ? 0.0F
         : (float)Mth.lerp(getTickDelta(), Minecraft.getInstance().player.xo, Minecraft.getInstance().player.getX());
   }

   public static float getPlayerY() {
      return Minecraft.getInstance().player == null
         ? 0.0F
         : (float)Mth.lerp(getTickDelta(), Minecraft.getInstance().player.yo, Minecraft.getInstance().player.getY());
   }

   public static float getPlayerZ() {
      return Minecraft.getInstance().player == null
         ? 0.0F
         : (float)Mth.lerp(getTickDelta(), Minecraft.getInstance().player.zo, Minecraft.getInstance().player.getZ());
   }

   public static float getPlayerRX() {
      return Minecraft.getInstance().player == null
         ? 0.0F
         : (float)Math.toRadians(Mth.rotLerp(getTickDelta(), Minecraft.getInstance().player.xRotO, Minecraft.getInstance().player.getXRot()));
   }

   public static float getPlayerRY() {
      return Minecraft.getInstance().player == null
         ? 0.0F
         : (float)Math.toRadians(Mth.rotLerp(getTickDelta(), Minecraft.getInstance().player.yRotO, Minecraft.getInstance().player.getYRot()));
   }

   public static float getEntityX() {
      return emfState == null ? 0.0F : (float)Mth.lerp(getTickDelta(), emfState.prevX(), emfState.x());
   }

   public static float getEntityY() {
      return emfState == null ? 0.0F : (float)Mth.lerp(getTickDelta(), emfState.prevY(), emfState.y());
   }

   public static float getEntityZ() {
      return emfState == null ? 0.0F : (float)Mth.lerp(getTickDelta(), emfState.prevZ(), emfState.z());
   }

   public static float getEntityRX() {
      return emfState == null ? 0.0F : (float)Math.toRadians(Mth.rotLerp(getTickDelta(), emfState.prevPitch(), emfState.pitch()));
   }

   public static float getEntityRY() {
      if (emfState == null) {
         return 0.0F;
      } else if (isInGui()) {
         return emfEntity() instanceof LivingEntity alive
            ? (float)Math.toRadians(alive.yBodyRot)
            : (emfEntity() instanceof Entity entity ? (float)Math.toRadians(entity.getYRot()) : 0.0F);
      } else {
         return emfEntity() instanceof LivingEntity alive
            ? (float)Math.toRadians(Mth.rotLerp(getTickDelta(), alive.yBodyRotO, alive.yBodyRot))
            : (emfEntity() instanceof Entity entity ? (float)Math.toRadians(Mth.rotLerp(getTickDelta(), entity.yRotO, entity.yRot)) : 0.0F);
      }
   }

   public static float getTime() {
      if (emfState != null && emfState.world() != null) {
         long upTimeInTicks = emfState.world().getGameTime();
         return constrainedFloat(upTimeInTicks, 27720) + getTickDelta();
      } else {
         return 0.0F + getTickDelta();
      }
   }

   public static float getDayTime() {
      return emfState != null && emfState.world() != null ? constrainedFloat(emfState.world().getDayTime(), 31415) + getTickDelta() : 0.0F + getTickDelta();
   }

   public static float getDayCount() {
      return emfState != null && emfState.world() != null ? (float)(emfState.world().getDayTime() / 27720L) : 0.0F + getTickDelta();
   }

   public static float getHealth() {
      if (emfState == null) {
         return 0.0F;
      } else {
         return emfEntity() instanceof LivingEntity alive ? alive.getHealth() : 1.0F;
      }
   }

   public static float getDeathTime() {
      return emfEntity() instanceof LivingEntity alive ? (alive.deathTime > 0 ? alive.deathTime + getTickDelta() : 0.0F) : 0.0F;
   }

   public static float getAngerTime() {
      if (!(emfEntity() instanceof NeutralMob)) {
         return 0.0F;
      } else {
         float currentKnownHighest = knownHighestAngerTimeByUUID.getOrDefault(emfState.uuid(), 0).intValue();
         int angerTime = ((NeutralMob)emfEntity()).getRemainingPersistentAngerTime();
         if (angerTime <= 0) {
            knownHighestAngerTimeByUUID.put(emfState.uuid(), 0);
            return 0.0F;
         } else {
            if (angerTime > currentKnownHighest) {
               knownHighestAngerTimeByUUID.put(emfState.uuid(), angerTime);
            }

            return angerTime - getTickDelta();
         }
      }
   }

   public static float getAngerTimeStart() {
      return emfEntity() instanceof NeutralMob ? knownHighestAngerTimeByUUID.getOrDefault(emfState.uuid(), 0).intValue() : 0.0F;
   }

   public static float getMaxHealth() {
      return emfEntity() instanceof LivingEntity alive ? alive.getMaxHealth() : 1.0F;
   }

   public static float getId() {
      return emfState != null && !isOnShoulder() ? Math.abs(emfState.optifineId()) % 27720 : 0.0F;
   }

   public static float getHurtTime() {
      return emfEntity() instanceof LivingEntity alive ? (alive.hurtTime > 0 ? alive.hurtTime - getTickDelta() : 0.0F) : 0.0F;
   }

   public static float getHeightAboveGround() {
      if (!(emfEntity() instanceof Entity)) {
         return 0.0F;
      } else {
         float y = getEntityY();
         MutableBlockPos pos = emfState.blockPos().mutable();
         int worldBottom = emfState.world().getMinBuildHeight();
         if (emfState.isBlockEntity()) {
            pos.move(Direction.DOWN);
         }

         while (!emfState.world().getBlockState(pos).entityCanStandOn(emfState.world(), pos, (Entity)emfEntity()) && pos.getY() > worldBottom) {
            pos.move(Direction.DOWN);
         }

         return y - pos.getY();
      }
   }

   public static float getFluidDepthDown() {
      if (emfState != null && !emfState.world().getFluidState(emfState.blockPos()).isEmpty()) {
         BlockPos pos = emfState.blockPos();
         int worldBottom = emfState.world().getMinBuildHeight();

         while (!emfState.world().getFluidState(pos).isEmpty() && pos.getY() > worldBottom) {
            pos = pos.below();
         }

         return emfState.blockPos().getY() - pos.getY();
      } else {
         return 0.0F;
      }
   }

   public static float getFluidDepthUp() {
      if (emfState != null && !emfState.world().getFluidState(emfState.blockPos()).isEmpty()) {
         BlockPos pos = emfState.blockPos();
         int worldTop = emfState.world().getMaxBuildHeight();

         while (!emfState.world().getFluidState(pos).isEmpty() && pos.getY() < worldTop) {
            pos = pos.above();
         }

         return pos.getY() - emfState.blockPos().getY();
      } else {
         return 0.0F;
      }
   }

   public static float getFluidDepth() {
      return emfState != null && !emfState.world().getFluidState(emfState.blockPos()).isEmpty() ? getFluidDepthDown() + getFluidDepthUp() - 1.0F : 0.0F;
   }

   public static boolean isInWater() {
      return emfState != null && emfState.isTouchingWater();
   }

   public static boolean isBurning() {
      return emfState != null && emfState.isOnFire();
   }

   public static boolean isRiding() {
      return emfState != null && emfState.hasVehicle();
   }

   public static boolean isChild() {
      return emfEntity() instanceof LivingEntity alive && alive.isBaby();
   }

   public static boolean isOnGround() {
      return emfState != null && emfState.isOnGround();
   }

   public static boolean isClimbing() {
      return emfEntity() instanceof LivingEntity alive && alive.onClimbable();
   }

   public static boolean isAlive() {
      return emfState == null ? false : emfState.isAlive();
   }

   public static boolean isUsingItem() {
      if (emfState == null) {
         return false;
      } else {
         return emfEntity() instanceof LivingEntity entity ? entity.isUsingItem() : false;
      }
   }

   public static boolean isSwingingArm(boolean right) {
      if (emfState == null) {
         return false;
      } else if (getSwingProgress() == 0.0F && !isUsingItem()) {
         return false;
      } else if (emfEntity() instanceof LivingEntity entity) {
         boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
         boolean usingMainHand = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;
         return right ? isRightHanded == usingMainHand : isRightHanded != usingMainHand;
      } else {
         return false;
      }
   }

   public static boolean isHoldingItem(boolean right) {
      if (emfState == null) {
         return false;
      } else if (emfEntity() instanceof LivingEntity entity) {
         boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
         InteractionHand arm;
         if (right) {
            arm = isRightHanded ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
         } else {
            arm = isRightHanded ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         }

         return !entity.getItemInHand(arm).isEmpty();
      } else {
         return false;
      }
   }

   public static boolean isAggressive() {
      if (emfState == null) {
         return false;
      } else if (emfEntity() instanceof EnderMan enderman) {
         return enderman.isCreepy();
      } else if (emfEntity() instanceof Blaze blaze) {
         return blaze.isOnFire();
      } else if (emfEntity() instanceof Guardian guardian) {
         return guardian.getActiveAttackTarget() != null;
      } else if (emfEntity() instanceof Vindicator vindicator) {
         return vindicator.isAggressive();
      } else if (emfEntity() instanceof SpellcasterIllager caster) {
         return caster.isCastingSpell();
      } else if (emfEntity() instanceof Vex vex) {
         return vex.isCharging();
      } else if (emfEntity() instanceof NeutralMob angry && angry.isAngry()) {
         return true;
      } else {
         return emfEntity() instanceof Targeting targets && targets.getTarget() != null ? true : emfEntity() instanceof Mob mob && mob.isAggressive();
      }
   }

   public static boolean isGlowing() {
      return emfState != null && emfState.isGlowing();
   }

   public static boolean isHurt() {
      return emfEntity() instanceof LivingEntity alive && alive.hurtTime > 0;
   }

   public static boolean isInHand() {
      return setInHand;
   }

   public static boolean isInItemFrame() {
      return setInItemFrame;
   }

   public static boolean isInGround() {
      return is_in_ground_override || emfEntity() instanceof Projectile proj && proj.isInWall();
   }

   public static boolean isInGui() {
      return setIsInGui;
   }

   public static boolean isClientHovered() {
      if (emfState == null) {
         return false;
      } else {
         Minecraft mc = Minecraft.getInstance();
         if (emfState.isBlockEntity()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && emfState.distanceTo(player) <= player.blockInteractionRange() + 1.0) {
               Entity entity = mc.getCameraEntity();
               if (entity != null) {
                  HitResult block = entity.pick(20.0, 0.0F, false);
                  if (block.getType() == Type.BLOCK) {
                     return ((BlockHitResult)block).getBlockPos().equals(emfState.blockPos());
                  }
               }
            }

            return false;
         } else {
            return mc.crosshairPickEntity != null && mc.crosshairPickEntity.equals(emfEntity());
         }
      }
   }

   public static boolean isInLava() {
      return emfState != null && emfState.isInLava();
   }

   public static boolean isInvisible() {
      return emfState != null && emfState.isInvisible();
   }

   public static boolean isOnHead() {
      return setIsOnHead;
   }

   public static boolean isOnShoulder() {
      return onShoulder;
   }

   public static void setCurrentEntityOnShoulder(boolean onShoulder) {
      EMFAnimationEntityContext.onShoulder = onShoulder;
   }

   public static boolean isRidden() {
      return emfState != null && emfState.hasPassengers();
   }

   public static boolean isSitting() {
      return emfState == null
         ? false
         : emfEntity() instanceof TamableAnimal tame && tame.isInSittingPose()
            || emfEntity() instanceof Fox fox && fox.isSitting()
            || emfEntity() instanceof Parrot parrot && parrot.isInSittingPose()
            || emfEntity() instanceof Cat cat && cat.isInSittingPose()
            || emfEntity() instanceof Wolf wolf && wolf.isInSittingPose()
            || emfEntity() instanceof Camel camel && camel.isCamelSitting();
   }

   public static boolean isSneaking() {
      return emfState != null && emfState.isSneaking();
   }

   public static boolean isSprinting() {
      return emfState != null && emfState.isSprinting();
   }

   public static boolean isTamed() {
      return emfEntity() instanceof TamableAnimal tame && tame.isTame();
   }

   public static boolean isWet() {
      return emfState != null && emfState.isWet();
   }

   public static float getSwingProgress() {
      if (isInGui()) {
         return 0.0F;
      } else {
         return emfEntity() instanceof LivingEntity alive ? alive.getAttackAnim(getTickDelta()) : 0.0F;
      }
   }

   public static float getAge() {
      return emfState == null ? 0.0F + getTickDelta() : constrainedFloat(emfState.age(), 27720) + getTickDelta();
   }

   private static float constrainedFloat(float value, int constraint) {
      return value >= constraint ? value % constraint : value;
   }

   private static float constrainedFloat(float value) {
      return constrainedFloat(value, 27720);
   }

   private static float constrainedFloat(long value, int constraint) {
      return (float)(value >= constraint ? value % constraint : value);
   }

   private static float constrainedFloat(long value) {
      return constrainedFloat(value, 27720);
   }

   private static float constrainedFloat(int value, int constraint) {
      return value >= constraint ? value % constraint : value;
   }

   private static float constrainedFloat(int value) {
      return constrainedFloat(value, 27720);
   }

   public static float getFrameTime() {
      if (Minecraft.getInstance().isPaused()) {
         return 0.0F;
      } else {
         return Minecraft.getInstance().level != null && Minecraft.getInstance().level.tickRateManager().isFrozen()
            ? 0.0F
            : ((MinecraftClientAccessor)Minecraft.getInstance()).getTimer().getGameTimeDeltaTicks() / 20.0F;
      }
   }

   public static float getLimbAngle() {
      if (emfState == null) {
         return 0.0F;
      } else {
         if (Float.isNaN(limbAngle)) {
            doLimbValues();
         }

         return limbAngle;
      }
   }

   public static void setLimbAngle(float limbAngle) {
      EMFAnimationEntityContext.limbAngle = limbAngle;
   }

   public static float getLimbDistance() {
      if (emfState == null) {
         return 0.0F;
      } else {
         if (Float.isNaN(limbDistance)) {
            doLimbValues();
         }

         return limbDistance == 1.0E-45F ? 0.0F : limbDistance;
      }
   }

   public static void setLimbDistance(float limbDistance) {
      EMFAnimationEntityContext.limbDistance = limbDistance;
   }

   private static void doLimbValues() {
      float o = 0.0F;
      float n = 0.0F;

      assert emfState != null;

      if (!emfState.hasVehicle() && emfEntity() instanceof LivingEntity alive) {
         o = alive.walkAnimation.position(getTickDelta());
         n = alive.walkAnimation.speed(getTickDelta());
         if (alive.isBaby()) {
            o *= 3.0F;
         }

         if (n > 1.0F) {
            n = 1.0F;
         }
      } else if (emfEntity() instanceof AbstractMinecart) {
         n = 1.0F;
         o = -(getEntityX() + getEntityZ());
      } else if (emfEntity() instanceof Boat boat) {
         n = 1.0F;
         o = Math.max(boat.getRowingTime(1, getTickDelta()), boat.getRowingTime(0, getTickDelta()));
      }

      limbDistance = n;
      limbAngle = o;
   }

   public static float getHeadYaw() {
      if (emfState == null) {
         return 0.0F;
      } else {
         if (Float.isNaN(headYaw)) {
            if (isInGui()) {
               return 0.0F;
            }

            doHeadValues();
         }

         return headYaw;
      }
   }

   public static void setHeadYaw(float headYaw) {
      EMFAnimationEntityContext.headYaw = headYaw;
   }

   public static float getHeadPitch() {
      if (emfState == null) {
         return 0.0F;
      } else {
         if (Float.isNaN(headPitch)) {
            if (isInGui()) {
               return 0.0F;
            }

            doHeadValues();
         }

         return headPitch;
      }
   }

   public static void setHeadPitch(float headPitch) {
      EMFAnimationEntityContext.headPitch = headPitch;
   }

   private static void doHeadValues() {
      if (emfEntity() instanceof LivingEntity livingEntity) {
         float h = Mth.rotLerp(getTickDelta(), livingEntity.yBodyRotO, livingEntity.yBodyRot);
         float j = Mth.rotLerp(getTickDelta(), livingEntity.yHeadRotO, livingEntity.yHeadRot);
         float k = j - h;
         if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
            h = Mth.rotLerp(getTickDelta(), livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
            k = j - h;
            float l = Mth.wrapDegrees(k);
            if (l < -85.0F) {
               l = -85.0F;
            }

            if (l >= 85.0F) {
               l = 85.0F;
            }

            h = j - l;
            if (l * l > 2500.0F) {
               h += l * 0.2F;
            }

            k = j - h;
         }

         float m = Mth.lerp(getTickDelta(), livingEntity.xRotO, livingEntity.getXRot());
         if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
            m *= -1.0F;
            k *= -1.0F;
         }

         headPitch = m;
         if (!(k >= 180.0F) && !(k < -180.0F)) {
            headYaw = k;
         } else {
            headYaw = Mth.wrapDegrees(k);
         }
      } else {
         headPitch = 0.0F;
         headYaw = 0.0F;
      }
   }

   public static float getTickDelta() {
      return ((MinecraftClientAccessor)Minecraft.getInstance()).getTimer().getGameTimeDeltaPartialTick(true);
   }

   public static float getMoveForward() {
      if (emfState != null && !isInGui()) {
         double lookDir = Math.toRadians(90.0F - emfState.yaw());
         Vec3 velocity = emfState.emfVelocity();
         double x = velocity.x;
         double y = velocity.z;
         double newX = x * Math.cos(lookDir) - y * Math.sin(lookDir);
         return processMove(newX, x, y);
      } else {
         return 0.0F;
      }
   }

   public static float getMoveStrafe() {
      if (emfState != null && !isInGui()) {
         double lookDir = Math.toRadians(90.0F - emfState.yaw());
         Vec3 velocity = emfState.emfVelocity();
         double x = velocity.x;
         double y = velocity.z;
         double newY = x * Math.sin(lookDir) + y * Math.cos(lookDir);
         return processMove(newY, x, y);
      } else {
         return 0.0F;
      }
   }

   private static float processMove(double value, double x, double y) {
      double totalMovementVector = Math.sqrt(x * x + y * y);
      return totalMovementVector == 0.0 ? 0.0F : (float)(-(value / totalMovementVector));
   }

   public static float getShadowSize() {
      return shadowSize;
   }

   public static void setShadowSize(float shadowSize) {
      EMFAnimationEntityContext.shadowSize = shadowSize;
   }

   public static float getShadowOpacity() {
      return shadowOpacity;
   }

   public static void setShadowOpacity(float shadowOpacity) {
      EMFAnimationEntityContext.shadowOpacity = shadowOpacity;
   }

   public static float getLeashX() {
      return leashX;
   }

   public static void setLeashX(float leashX) {
      EMFAnimationEntityContext.leashX = leashX;
   }

   public static float getLeashY() {
      return leashY;
   }

   public static void setLeashY(float leashY) {
      EMFAnimationEntityContext.leashY = leashY;
   }

   public static float getLeashZ() {
      return leashZ;
   }

   public static void setLeashZ(float leashZ) {
      EMFAnimationEntityContext.leashZ = leashZ;
   }

   public static float getShadowX() {
      return shadowX;
   }

   public static void setShadowX(float shadowX) {
      EMFAnimationEntityContext.shadowX = shadowX;
   }

   public static float getShadowZ() {
      return shadowZ;
   }

   public static void setShadowZ(float shadowZ) {
      EMFAnimationEntityContext.shadowZ = shadowZ;
   }

   public static EMFAnimationEntityContext.IterationContext getIterationContext() {
      return new EMFAnimationEntityContext.IterationContext(
         EMFManager.getInstance().entityRenderCount,
         emfState,
         layerFactory,
         lodFrameSkipping,
         shadowSize,
         shadowOpacity,
         leashX,
         leashY,
         leashZ,
         shadowX,
         shadowZ
      );
   }

   public static void setIterationContext(EMFAnimationEntityContext.IterationContext context) {
      EMFManager.getInstance().entityRenderCount = context.entityRenderCount;
      emfState = context.emfState;
      layerFactory = context.layerFactory;
      lodFrameSkipping = context.lodFrameSkipping;
      shadowSize = context.shadowSize;
      shadowOpacity = context.shadowOpacity;
      leashX = context.leashX;
      leashY = context.leashY;
      leashZ = context.leashZ;
      shadowX = context.shadowX;
      shadowZ = context.shadowZ;
   }

   public record IterationContext(
      long entityRenderCount,
      @Nullable EMFEntityRenderState emfState,
      Function<ResourceLocation, RenderType> layerFactory,
      Boolean lodFrameSkipping,
      float shadowSize,
      float shadowOpacity,
      float leashX,
      float leashY,
      float leashZ,
      float shadowX,
      float shadowZ
   ) {
   }
}
