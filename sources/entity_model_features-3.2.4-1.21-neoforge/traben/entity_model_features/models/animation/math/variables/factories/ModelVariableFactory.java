package traben.entity_model_features.models.animation.math.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public class ModelVariableFactory extends UniqueVariableFactory {
   @Override
   public MathValue.ResultSupplier getSupplierOrNull(String variableKey, AnimSetupContext context) {
      return variableKey.startsWith("varb")
         ? () -> EMFAnimationEntityContext.getEntityVariable(variableKey, -1.0F / 0.0F)
         : () -> EMFAnimationEntityContext.getEntityVariable(variableKey, 0.0F);
   }

   @Override
   public boolean createsThisVariable(String variableKey) {
      return variableKey == null ? false : variableKey.matches("(var|varb)\\.\\w+");
   }

   @Nullable
   @Override
   public String getExplanationTranslationKey() {
      return "entity_model_features.config.variable_explanation.entity_variable";
   }

   @Nullable
   @Override
   public String getTitleTranslationKey() {
      return "entity_model_features.config.variable_explanation.entity_variable.title";
   }
}
