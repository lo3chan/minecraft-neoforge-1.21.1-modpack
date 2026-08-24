package traben.entity_model_features.models.animation.math.variables.factories;

import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.utils.EMFUtils;

public class RenderVariableFactory extends UniqueVariableFactory {
   @Override
   public MathValue.ResultSupplier getSupplierOrNull(String variableKey, AnimSetupContext context) {
      MathValue.ResultSupplier renderVariableCalculator = context.oldAnimationHandler.getLastResultGetter(variableKey);
      if (renderVariableCalculator != null) {
         return renderVariableCalculator;
      } else {
         EMFModelOrRenderVariable variable = EMFModelOrRenderVariable.getRenderVariable(variableKey);
         if (variable != null && variable.isRenderVariable()) {
            return variable::getValue;
         } else {
            if (this.printing()) {
               EMFUtils.logWarn("no render variable found for: [" + variableKey + "]");
            }

            return null;
         }
      }
   }

   @Override
   public boolean createsThisVariable(String variableKey) {
      return variableKey == null ? false : variableKey.matches("(render)\\.\\w+");
   }

   @Nullable
   @Override
   public String getExplanationTranslationKey() {
      return "entity_model_features.config.variable_explanation.render_variable";
   }

   @Nullable
   @Override
   public String getTitleTranslationKey() {
      return "entity_model_features.config.variable_explanation.render_variable.title";
   }
}
