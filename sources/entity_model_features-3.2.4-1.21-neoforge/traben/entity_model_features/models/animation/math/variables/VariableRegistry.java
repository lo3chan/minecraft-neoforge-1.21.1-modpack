package traben.entity_model_features.models.animation.math.variables;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathConstant;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.expression_tree.MathVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.ModelPartVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.ModelVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.RenderVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.utils.EMFUtils;

public final class VariableRegistry {
   private static final VariableRegistry INSTANCE = new VariableRegistry();
   private final Map<String, MathComponent> singletonVariables = new HashMap<>();
   private final Map<String, String> singletonVariableExplanationTranslationKeys = new HashMap<>();
   private final List<UniqueVariableFactory> uniqueVariableFactories = new ArrayList<>();
   public final Map<String, BooleanSupplier> singletonASMVariablesBool = new HashMap<>();
   public final Map<String, MathValue.ResultSupplier> singletonASMVariablesFloat = new HashMap<>();

   public BooleanSupplier getASMVarBoolOrDefault(String varKey, AnimSetupContext context) {
      BooleanSupplier simple = this.singletonASMVariablesBool.get(varKey);
      if (simple == null) {
         for (UniqueVariableFactory uniqueVariableFactory : this.uniqueVariableFactories) {
            if (uniqueVariableFactory.createsThisVariable(varKey)) {
               BooleanSupplier supplier = uniqueVariableFactory.getASMBoolSupplierOrNull(varKey, context);
               if (supplier != null) {
                  return supplier;
               }
            }
         }

         return () -> false;
      } else {
         return simple;
      }
   }

   public MathValue.ResultSupplier getASMVarFloatOrDefault(String varKey, AnimSetupContext context) {
      MathValue.ResultSupplier simple = this.singletonASMVariablesFloat.get(varKey);
      if (simple == null) {
         for (UniqueVariableFactory uniqueVariableFactory : this.uniqueVariableFactories) {
            if (uniqueVariableFactory.createsThisVariable(varKey)) {
               MathValue.ResultSupplier supplier = uniqueVariableFactory.getASMFloatSupplierOrNull(varKey, context);
               if (supplier != null) {
                  return supplier;
               }
            }
         }

         return () -> 0.0F;
      } else {
         return simple;
      }
   }

   private VariableRegistry() {
      this.singletonVariables.put("pi", new MathConstant(3.1415927F));
      this.singletonVariables.put("-pi", new MathConstant(-3.1415927F));
      this.singletonVariableExplanationTranslationKeys.put("pi", emfTranslationKey("pi"));
      this.singletonVariables.put("e", new MathConstant(2.7182817F));
      this.singletonVariables.put("-e", new MathConstant(-2.7182817F));
      this.singletonVariableExplanationTranslationKeys.put("e", emfTranslationKey("e"));
      this.singletonVariables.put("true", new MathConstant(1.0F / 0.0F));
      this.singletonVariables.put("!true", new MathConstant(-1.0F / 0.0F));
      this.singletonVariableExplanationTranslationKeys.put("true", emfTranslationKey("true"));
      this.singletonVariables.put("false", new MathConstant(-1.0F / 0.0F));
      this.singletonVariables.put("!false", new MathConstant(1.0F / 0.0F));
      this.singletonVariableExplanationTranslationKeys.put("false", emfTranslationKey("false"));
      this.registerSimpleFloatVariable("limb_swing", EMFAnimationEntityContext::getLimbAngle);
      this.registerSimpleFloatVariable("frame_time", EMFAnimationEntityContext::getFrameTime);
      this.registerSimpleFloatVariable("limb_speed", EMFAnimationEntityContext::getLimbDistance);
      this.registerSimpleFloatVariable("age", EMFAnimationEntityContext::getAge);
      this.registerSimpleFloatVariable("head_pitch", EMFAnimationEntityContext::getHeadPitch);
      this.registerSimpleFloatVariable("head_yaw", EMFAnimationEntityContext::getHeadYaw);
      this.registerSimpleFloatVariable("swing_progress", EMFAnimationEntityContext::getSwingProgress);
      this.registerSimpleFloatVariable("hurt_time", EMFAnimationEntityContext::getHurtTime);
      this.registerSimpleFloatVariable("dimension", EMFAnimationEntityContext::getDimension);
      this.registerSimpleFloatVariable("time", EMFAnimationEntityContext::getTime);
      this.registerSimpleFloatVariable("player_pos_x", emfTranslationKey("player_pos"), EMFAnimationEntityContext::getPlayerX);
      this.registerSimpleFloatVariable("player_pos_y", emfTranslationKey("player_pos"), EMFAnimationEntityContext::getPlayerY);
      this.registerSimpleFloatVariable("player_pos_z", emfTranslationKey("player_pos"), EMFAnimationEntityContext::getPlayerZ);
      this.registerSimpleFloatVariable("pos_x", emfTranslationKey("pos"), EMFAnimationEntityContext::getEntityX);
      this.registerSimpleFloatVariable("pos_y", emfTranslationKey("pos"), EMFAnimationEntityContext::getEntityY);
      this.registerSimpleFloatVariable("pos_z", emfTranslationKey("pos"), EMFAnimationEntityContext::getEntityZ);
      this.registerSimpleFloatVariable("player_rot_x", emfTranslationKey("player_rot"), EMFAnimationEntityContext::getPlayerRX);
      this.registerSimpleFloatVariable("player_rot_y", emfTranslationKey("player_rot"), EMFAnimationEntityContext::getPlayerRY);
      this.registerSimpleFloatVariable("rot_x", emfTranslationKey("rot"), EMFAnimationEntityContext::getEntityRX);
      this.registerSimpleFloatVariable("rot_y", emfTranslationKey("rot"), EMFAnimationEntityContext::getEntityRY);
      this.registerSimpleFloatVariable("health", EMFAnimationEntityContext::getHealth);
      this.registerSimpleFloatVariable("death_time", EMFAnimationEntityContext::getDeathTime);
      this.registerSimpleFloatVariable("anger_time", EMFAnimationEntityContext::getAngerTime);
      this.registerSimpleFloatVariable("max_health", EMFAnimationEntityContext::getMaxHealth);
      this.registerSimpleFloatVariable("id", EMFAnimationEntityContext::getId);
      this.registerSimpleFloatVariable("day_time", EMFAnimationEntityContext::getDayTime);
      this.registerSimpleFloatVariable("day_count", EMFAnimationEntityContext::getDayCount);
      this.registerSimpleFloatVariable("rule_index", EMFAnimationEntityContext::getRuleIndex);
      this.registerSimpleFloatVariable("anger_time_start", EMFAnimationEntityContext::getAngerTimeStart);
      this.registerSimpleFloatVariable("move_forward", EMFAnimationEntityContext::getMoveForward);
      this.registerSimpleFloatVariable("move_strafing", EMFAnimationEntityContext::getMoveStrafe);
      this.registerSimpleFloatVariable("height_above_ground", EMFAnimationEntityContext::getHeightAboveGround);
      this.registerSimpleFloatVariable("fluid_depth", EMFAnimationEntityContext::getFluidDepth);
      this.registerSimpleFloatVariable("fluid_depth_down", EMFAnimationEntityContext::getFluidDepthDown);
      this.registerSimpleFloatVariable("fluid_depth_up", EMFAnimationEntityContext::getFluidDepthUp);
      this.registerSimpleFloatVariable("nan", () -> EMFManager.getInstance().isAnimationValidationPhase ? 0.0F : 0.0F / 0.0F);
      this.registerSimpleFloatVariable(
         "distance",
         () -> EMFAnimationEntityContext.getEMFEntity() == null
            ? 0.0F
            : EMFAnimationEntityContext.getEMFEntity().etf$distanceTo(Minecraft.getInstance().player)
      );
      this.registerSimpleFloatVariable("frame_counter", EMFAnimationEntityContext::getFrameCounter);
      this.registerSimpleBoolVariable("is_hovered", EMFAnimationEntityContext::isClientHovered);
      this.registerSimpleBoolVariable("is_paused", () -> Minecraft.getInstance().isPaused());
      this.registerSimpleBoolVariable("is_first_person_hand", () -> EMFAnimationEntityContext.isFirstPersonHand);
      this.registerSimpleBoolVariable(
         "is_right_handed",
         () -> EMFAnimationEntityContext.getEMFEntity() == null
            ? false
            : EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity entity && entity.getMainArm() == HumanoidArm.RIGHT
      );
      this.registerSimpleBoolVariable("is_swinging_right_arm", () -> EMFAnimationEntityContext.isSwingingArm(true));
      this.registerSimpleBoolVariable("is_swinging_left_arm", () -> EMFAnimationEntityContext.isSwingingArm(false));
      this.registerSimpleBoolVariable("is_holding_item_right", () -> EMFAnimationEntityContext.isHoldingItem(true));
      this.registerSimpleBoolVariable("is_holding_item_left", () -> EMFAnimationEntityContext.isHoldingItem(false));
      this.registerSimpleBoolVariable("is_using_item", EMFAnimationEntityContext::isUsingItem);
      this.registerSimpleBoolVariable(
         "is_swimming",
         () -> EMFAnimationEntityContext.getEMFEntity() == null
            ? false
            : EMFAnimationEntityContext.getEMFEntity() instanceof Entity entity && entity.isSwimming()
      );
      this.registerSimpleBoolVariable(
         "is_gliding",
         () -> EMFAnimationEntityContext.getEMFEntity() == null
            ? false
            : EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity entity && entity.isFallFlying()
      );
      this.registerSimpleBoolVariable(
         "is_blocking",
         () -> EMFAnimationEntityContext.getEMFEntity() == null
            ? false
            : EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity livingEntity && livingEntity.isBlocking()
      );
      this.registerSimpleBoolVariable(
         "is_crawling",
         () -> EMFAnimationEntityContext.getEMFEntity() == null
            ? false
            : EMFAnimationEntityContext.getEMFEntity() instanceof Entity entity && entity.isVisuallyCrawling()
      );
      this.registerSimpleBoolVariable("is_climbing", EMFAnimationEntityContext::isClimbing);
      this.registerSimpleBoolVariable("is_child", EMFAnimationEntityContext::isChild);
      this.registerSimpleBoolVariable("is_in_water", EMFAnimationEntityContext::isInWater);
      this.registerSimpleBoolVariable("is_riding", EMFAnimationEntityContext::isRiding);
      this.registerSimpleBoolVariable("is_on_ground", EMFAnimationEntityContext::isOnGround);
      this.registerSimpleBoolVariable("is_burning", EMFAnimationEntityContext::isBurning);
      this.registerSimpleBoolVariable("is_alive", EMFAnimationEntityContext::isAlive);
      this.registerSimpleBoolVariable("is_glowing", EMFAnimationEntityContext::isGlowing);
      this.registerSimpleBoolVariable("is_aggressive", EMFAnimationEntityContext::isAggressive);
      this.registerSimpleBoolVariable("is_hurt", EMFAnimationEntityContext::isHurt);
      this.registerSimpleBoolVariable("is_in_hand", EMFAnimationEntityContext::isInHand);
      this.registerSimpleBoolVariable("is_in_item_frame", EMFAnimationEntityContext::isInItemFrame);
      this.registerSimpleBoolVariable("is_in_ground", EMFAnimationEntityContext::isInGround);
      this.registerSimpleBoolVariable("is_in_gui", EMFAnimationEntityContext::isInGui);
      this.registerSimpleBoolVariable("is_in_lava", EMFAnimationEntityContext::isInLava);
      this.registerSimpleBoolVariable("is_invisible", EMFAnimationEntityContext::isInvisible);
      this.registerSimpleBoolVariable("is_on_head", EMFAnimationEntityContext::isOnHead);
      this.registerSimpleBoolVariable("is_on_shoulder", EMFAnimationEntityContext::isOnShoulder);
      this.registerSimpleBoolVariable("is_ridden", EMFAnimationEntityContext::isRidden);
      this.registerSimpleBoolVariable("is_sitting", EMFAnimationEntityContext::isSitting);
      this.registerSimpleBoolVariable("is_sneaking", EMFAnimationEntityContext::isSneaking);
      this.registerSimpleBoolVariable("is_sprinting", EMFAnimationEntityContext::isSprinting);
      this.registerSimpleBoolVariable("is_tamed", EMFAnimationEntityContext::isTamed);
      this.registerSimpleBoolVariable("is_wet", EMFAnimationEntityContext::isWet);
      this.registerSimpleBoolVariable("is_jumping", EMFAnimationEntityContext::isJumping);
      this.registerContextVariable(new ModelPartVariableFactory());
      this.registerContextVariable(new ModelVariableFactory());
      this.registerContextVariable(new RenderVariableFactory());
      this.registerContextVariable(new GlobalVariableFactory());
      this.registerSimpleSpellingErrorWarning(
         new String[]{
            "is_agressive",
            "is_aggressive",
            "is_aggresive",
            "is_aggressive",
            "is_agresive",
            "is_aggressive",
            "is_riden",
            "is_ridden",
            "frame_count",
            "frame_counter"
         }
      );
   }

   private static String emfTranslationKey(String key) {
      return "entity_model_features.config.variable_explanation." + key;
   }

   public static VariableRegistry getInstance() {
      return INSTANCE;
   }

   public Map<String, String> getSingletonVariableExplanationTranslationKeys() {
      return this.singletonVariableExplanationTranslationKeys;
   }

   public List<UniqueVariableFactory> getUniqueVariableFactories() {
      return this.uniqueVariableFactories;
   }

   public void registerContextVariable(UniqueVariableFactory factory) {
      if (factory == null) {
         EMFUtils.logWarn("Tried to register a null context variable factory");
      } else if (this.uniqueVariableFactories.contains(factory)) {
         EMFUtils.logWarn("Tried to register a duplicate context variable factory: " + factory.getClass().getName());
      } else {
         this.uniqueVariableFactories.add(factory);
      }
   }

   private void registerSimpleFloatVariable(String variableName, MathValue.ResultSupplier supplier) {
      this.registerSimpleFloatVariable(variableName, emfTranslationKey(variableName), supplier);
   }

   public void registerSimpleFloatVariable(String variableName, @Translatable String explanationTranslationKey, MathValue.ResultSupplier supplier) {
      if (this.singletonVariables.containsKey(variableName)) {
         EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
      } else {
         this.singletonVariables.put(variableName, new MathVariable(variableName, false, supplier));
         this.singletonVariables.put("-" + variableName, new MathVariable("-" + variableName, true, supplier));
         this.singletonVariableExplanationTranslationKeys.put(variableName, explanationTranslationKey);
         this.singletonASMVariablesFloat.put(variableName, supplier);
      }
   }

   private void registerSimpleSpellingErrorWarning(String[] warns) {
      if (warns.length % 2 != 0) {
         throw new IllegalArgumentException("registerSimpleSpellingErrorWarning must have an even number of elements");
      } else {
         for (int i = 0; i < warns.length; i += 2) {
            String variableName = warns[i];
            String correction = warns[i + 1];
            this.singletonVariables.put(variableName, new MathVariable(variableName, false, () -> {
               if (this.printing()) {
                  EMFUtils.logError("Math spelling error: [" + variableName + "]. You probably meant: [" + correction + "].");
               }

               return 0.0F / 0.0F;
            }));
         }
      }
   }

   private void registerSimpleBoolVariable(String variableName, BooleanSupplier boolGetter) {
      this.registerSimpleBoolVariable(variableName, emfTranslationKey(variableName), boolGetter);
   }

   public void registerSimpleBoolVariable(String variableName, @Translatable String explanationTranslationKey, BooleanSupplier boolGetter) {
      if (this.singletonVariables.containsKey(variableName)) {
         if (this.printing()) {
            EMFUtils.log("Duplicate variable: " + variableName + ". ignoring duplicate");
         }
      } else {
         this.singletonVariables.put(variableName, new MathVariable(variableName, () -> MathValue.fromBoolean(boolGetter)));
         this.singletonVariables.put("!" + variableName, new MathVariable("!" + variableName, () -> MathValue.invertBoolean(boolGetter)));
         this.singletonVariableExplanationTranslationKeys.put(variableName, explanationTranslationKey);
         this.singletonASMVariablesBool.put(variableName, boolGetter);
      }
   }

   public MathComponent getVariable(String variableName, boolean isNegative, AnimSetupContext context) {
      try {
         String variableWithNegative = isNegative ? "-" + variableName : variableName;
         if (this.singletonVariables.containsKey(variableWithNegative)) {
            return this.singletonVariables.get(variableWithNegative);
         }

         boolean invertBooleans = variableName.startsWith("!");
         String variableNameWithoutBooleanInvert = invertBooleans ? variableName.substring(1) : variableName;

         for (UniqueVariableFactory uniqueVariableFactory : this.uniqueVariableFactories) {
            if (uniqueVariableFactory.createsThisVariable(variableNameWithoutBooleanInvert)) {
               MathValue.ResultSupplier supplier = uniqueVariableFactory.getSupplierOrNull(variableNameWithoutBooleanInvert, context);
               if (supplier != null) {
                  return new MathVariable(variableName, isNegative, invertBooleans ? () -> MathValue.invertBoolean(supplier) : supplier);
               }
            }
         }

         if (this.printing()) {
            EMFUtils.logError(
               "Variable ["
                  + variableName
                  + "] not found in animation ["
                  + context.animKey
                  + "] of model ["
                  + context.modelName
                  + "]. EMF will treat the variable as zero."
            );
         }
      } catch (Exception var10) {
         if (this.printing()) {
            EMFUtils.logError(
               "Error finding variable: ["
                  + variableName
                  + "] in animation ["
                  + context.animKey
                  + "] of model ["
                  + context.modelName
                  + "]. EMF will treat the variable as zero."
            );
         }
      }

      return variableName.startsWith("is_") ? MathConstant.FALSE_CONST : MathConstant.ZERO_CONST;
   }

   private boolean printing() {
      return ((EMFConfig)EMF.config().getConfig()).logModelCreationData;
   }
}
