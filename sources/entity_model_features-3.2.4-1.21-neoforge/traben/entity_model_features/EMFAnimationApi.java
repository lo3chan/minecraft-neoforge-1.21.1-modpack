package traben.entity_model_features;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.valueproviders.SampledFloat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.asm.ASMVisitable;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartCustom;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;

public interface EMFAnimationApi {
   static int getApiVersion() {
      return 9;
   }

   @Nullable
   static EMFEntity getCurrentEntity() {
      return EMFAnimationEntityContext.getEMFEntity();
   }

   static void registerSingletonAnimationVariable(
      String sourceModId, String variableName, String variableExplanationTranslationKeyOrText, BooleanSupplier variableValueSupplier
   ) throws Exception {
      if (sourceModId != null && variableName != null && variableValueSupplier != null && variableExplanationTranslationKeyOrText != null) {
         VariableRegistry.getInstance().registerSimpleBoolVariable(variableName, variableExplanationTranslationKeyOrText, variableValueSupplier);
         EMFUtils.log("Successful registration of singleton variable:" + variableName + " from mod " + sourceModId);
      } else {
         throw paramFail("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
      }
   }

   @Deprecated(
      since = "api v8"
   )
   static void registerSingletonAnimationVariable(
      String sourceModId, String variableName, String variableExplanationTranslationKeyOrText, SampledFloat variableValueSupplier
   ) {
      EMFUtils.logWarn("INVALID registration of singleton variable:" + variableName + " from mod " + sourceModId);
   }

   static void registerSingletonAnimationVariable(
      String sourceModId, String variableName, String variableExplanationTranslationKeyOrText, Supplier<Float> variableValueSupplier
   ) throws Exception {
      if (sourceModId != null && variableName != null && variableValueSupplier != null && variableExplanationTranslationKeyOrText != null) {
         VariableRegistry.getInstance().registerSimpleFloatVariable(variableName, variableExplanationTranslationKeyOrText, variableValueSupplier::get);
         EMFUtils.log("Successful registration of singleton variable:" + variableName + " from mod " + sourceModId);
      } else {
         throw paramFail("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
      }
   }

   static void registerUniqueAnimationVariableFactory(String sourceModId, String variableName, UniqueVariableFactory uniqueVariableFactory) throws Exception {
      if (sourceModId != null && variableName != null && uniqueVariableFactory != null) {
         VariableRegistry.getInstance().registerContextVariable(uniqueVariableFactory);
         EMFUtils.log("Successful registration of unique variable:" + variableName + " from mod " + sourceModId);
      } else {
         throw paramFail("Invalid registration of unique variable:" + variableName + " from mod " + sourceModId);
      }
   }

   static void registerCustomFunctionFromStaticMethod(
      String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, Method staticMethod, @Nullable ASMVisitable asmCompiler
   ) throws Exception {
      if (sourceModId != null && methodName != null && staticMethod != null && methodExplanationTranslationKeyOrText != null) {
         MethodRegistry.getInstance().registerSimpleMethodFactory(methodName, methodExplanationTranslationKeyOrText, staticMethod, asmCompiler);
         EMFUtils.log("Successful registration of custom function:" + methodName + " from mod " + sourceModId);
      } else {
         throw paramFail("Invalid registration of custom function:" + methodName + " from mod " + sourceModId);
      }
   }

   static void registerCustomFunctionFactory(
      String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, MethodRegistry.MethodFactory factory
   ) throws Exception {
      if (sourceModId != null && methodName != null && factory != null && methodExplanationTranslationKeyOrText != null) {
         MethodRegistry.getInstance().registerAndWrapMethodFactory(methodName, methodExplanationTranslationKeyOrText, factory);
         EMFUtils.log("Successful registration of custom function:" + methodName + " from mod " + sourceModId);
      } else {
         throw paramFail("Invalid registration of custom function:" + methodName + " from mod " + sourceModId);
      }
   }

   static EMFEntity emfEntityOf(Entity entity) {
      return (EMFEntity)entity;
   }

   static EMFEntity emfEntityOf(BlockEntity blockEntity) {
      return (EMFEntity)blockEntity;
   }

   static boolean registerPauseCondition(Function<EMFEntity, Boolean> shouldPause) throws Exception {
      if (shouldPause == null) {
         throw paramFail("null pause condition");
      } else {
         EMFAnimationEntityContext.pauseListeners.add(shouldPause);
         return true;
      }
   }

   static boolean pauseAllCustomAnimationsForEntity(EMFEntity entityOrBlockEntity) {
      if (entityOrBlockEntity != null && entityOrBlockEntity.etf$getUuid() != null) {
         EMFAnimationEntityContext.entitiesPaused.add(entityOrBlockEntity.etf$getUuid());
         return true;
      } else {
         return false;
      }
   }

   static boolean resumeAllCustomAnimationsForEntity(EMFEntity entityOrBlockEntity) {
      if (entityOrBlockEntity != null && entityOrBlockEntity.etf$getUuid() != null) {
         EMFAnimationEntityContext.entitiesPaused.remove(entityOrBlockEntity.etf$getUuid());
         EMFAnimationEntityContext.entitiesPausedParts.remove(entityOrBlockEntity.etf$getUuid());
         return true;
      } else {
         return false;
      }
   }

   static boolean pauseCustomAnimationsForThesePartsOfEntity(EMFEntity entityOrBlockEntity, ModelPart... parts) {
      if (entityOrBlockEntity != null && entityOrBlockEntity.etf$getUuid() != null && parts != null && parts.length != 0) {
         EMFAnimationEntityContext.entitiesPausedParts.put(entityOrBlockEntity.etf$getUuid(), parts);
         return true;
      } else {
         return false;
      }
   }

   static boolean registerVanillaModelCondition(Function<EMFEntity, Boolean> shouldUseVanillaModel) throws Exception {
      if (shouldUseVanillaModel == null) {
         throw paramFail("null vanilla model condition");
      } else {
         EMFAnimationEntityContext.forceVanillaModelListeners.add(shouldUseVanillaModel);
         return true;
      }
   }

   static boolean lockEntityToVanillaModel(EMFEntity entityOrBlockEntity) {
      if (entityOrBlockEntity != null && entityOrBlockEntity.etf$getUuid() != null) {
         EMFAnimationEntityContext.entitiesToForceVanillaModel.add(entityOrBlockEntity.etf$getUuid());
         return true;
      } else {
         return false;
      }
   }

   static boolean unlockEntityToVanillaModel(EMFEntity entityOrBlockEntity) {
      if (entityOrBlockEntity != null && entityOrBlockEntity.etf$getUuid() != null) {
         EMFAnimationEntityContext.entitiesToForceVanillaModel.remove(entityOrBlockEntity.etf$getUuid());
         return true;
      } else {
         return false;
      }
   }

   static int getCurrentEMFVariantOfModel(EntityModel<?> model) {
      return !isModelCustomizedByEMF(model) ? -1 : ((IEMFModel)model).emf$getEMFRootModel().currentModelVariant;
   }

   static boolean isModelAnimatedByEMF(EntityModel<?> model) {
      return !isModelCustomizedByEMF(model) ? false : ((IEMFModel)model).emf$getEMFRootModel().hasAnimation();
   }

   static boolean isModelCustomizedByEMF(EntityModel<?> model) {
      return model == null ? false : ((IEMFModel)model).emf$isEMFModel();
   }

   static boolean isModelPartCustomToEMF(ModelPart modelPart) {
      return modelPart == null ? false : modelPart instanceof EMFModelPartCustom;
   }

   static boolean isModelPartAnimatedByEMF(ModelPart modelPart) {
      return modelPart == null ? false : modelPart instanceof EMFModelPart emf && emf.isSetByAnimation;
   }

   @Deprecated(
      since = "api v9"
   )
   static void registerAnimationFunction(String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, Function<Float, Float> function) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v9"
   )
   static void registerAnimationBiFunction(
      String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, BiFunction<Float, Float, Float> biFunction
   ) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v9"
   )
   static void registerAnimationTriFunction(
      String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, TriFunction<Float, Float, Float, Float> triFunction
   ) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v9"
   )
   static void registerAnimationMultiFunction(
      String sourceModId, String methodName, String methodExplanationTranslationKeyOrText, Function<List<Float>, Float> multiFunction
   ) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerSingletonAnimationVariable(String sourceModId, String variableName, BooleanSupplier variableValueSupplier) throws Exception {
      EMFUtils.logWarn("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
      registerSingletonAnimationVariable(sourceModId, variableName, variableName, variableValueSupplier);
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerSingletonAnimationVariable(String sourceModId, String variableName, SampledFloat variableValueSupplier) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerSingletonAnimationVariable(String sourceModId, String variableName, Supplier<Float> variableValueSupplier) throws Exception {
      EMFUtils.logWarn("Invalid registration of singleton variable:" + variableName + " from mod " + sourceModId);
      registerSingletonAnimationVariable(sourceModId, variableName, variableName, variableValueSupplier);
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerAnimationFunction(String sourceModId, String methodName, Function<Float, Float> function) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerAnimationBiFunction(String sourceModId, String methodName, BiFunction<Float, Float, Float> biFunction) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerAnimationTriFunction(String sourceModId, String methodName, TriFunction<Float, Float, Float, Float> triFunction) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerAnimationMultiFunction(String sourceModId, String methodName, Function<List<Float>, Float> multiFunction) throws Exception {
      throw dependencyException();
   }

   @Deprecated(
      since = "api v2"
   )
   static void registerCustomFunctionFactory(String sourceModId, String methodName, MethodRegistry.MethodFactory factory) throws Exception {
      throw dependencyException();
   }

   private static Exception dependencyException() {
      return new UnsupportedOperationException("EMF Animation API: method is deprecated.");
   }

   private static Exception paramFail(String message) {
      EMFUtils.logError("EMF Animation API: " + message);
      return new InvalidParameterException("EMF Animation API: " + message);
   }
}
