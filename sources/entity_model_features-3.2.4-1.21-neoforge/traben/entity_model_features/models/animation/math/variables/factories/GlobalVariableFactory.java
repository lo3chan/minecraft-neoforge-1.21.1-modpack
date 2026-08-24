package traben.entity_model_features.models.animation.math.variables.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public class GlobalVariableFactory extends UniqueVariableFactory {
   private static final Map<String, Float> globalVariables = new HashMap<>();

   public static void clear() {
      globalVariables.clear();
   }

   public static void setGlobalVariable(String key, float value) {
      globalVariables.put(key, value);
   }

   public static float getGlobalVariable(String key) {
      return globalVariables.getOrDefault(key, 0.0F);
   }

   @Override
   public MathValue.ResultSupplier getSupplierOrNull(String variableKey, AnimSetupContext context) {
      return () -> globalVariables.getOrDefault(variableKey, 0.0F);
   }

   @Nullable
   @Override
   public BooleanSupplier getASMBoolSupplierOrNull(String variableKey, AnimSetupContext context) {
      return () -> globalVariables.getOrDefault(variableKey, -1.0F / 0.0F) > 0.0F;
   }

   @Nullable
   @Override
   public MathValue.ResultSupplier getASMFloatSupplierOrNull(String variableKey, AnimSetupContext context) {
      return () -> globalVariables.getOrDefault(variableKey, 0.0F);
   }

   @Override
   public boolean createsThisVariable(String variableKey) {
      return variableKey == null ? false : variableKey.matches("global_(var|varb)\\.\\w+");
   }

   @Nullable
   @Override
   public String getExplanationTranslationKey() {
      return "entity_model_features.config.variable_explanation.global_variable";
   }

   @Nullable
   @Override
   public String getTitleTranslationKey() {
      return "entity_model_features.config.variable_explanation.global_variable.title";
   }
}
