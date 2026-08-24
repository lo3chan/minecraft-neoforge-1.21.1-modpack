package traben.entity_model_features.models.animation.math.variables.factories;

import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathConstant;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.utils.EMFUtils;

public class ModelPartVariableFactory extends UniqueVariableFactory {
   @Override
   public MathValue.ResultSupplier getSupplierOrNull(String variableKey, AnimSetupContext context) {
      String[] split = variableKey.split("\\.");
      String partName = split[0];
      EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
      EMFModelPart part = EMFManager.getModelFromHierarchicalId(partName, context.allPartsBySingleAndFullHeirachicalId);
      if (partVariable != null) {
         if (part != null) {
            return () -> partVariable.getValue(part);
         } else {
            if (this.printing()) {
               EMFUtils.logWarn(
                  "no part found for: ["
                     + variableKey
                     + "] in ["
                     + context.modelName
                     + "]. Available parts were: "
                     + context.allPartsBySingleAndFullHeirachicalId.keySet()
               );
            }

            return partVariable.isBoolean() ? MathConstant.FALSE_CONST::getResult : MathConstant.ZERO_CONST::getResult;
         }
      } else if (!context.modelName.endsWith("chest_large.jem") || !partName.endsWith("_left") && !partName.endsWith("_right")) {
         if (this.printing()) {
            EMFUtils.logWarn(
               "no part found for: ["
                  + variableKey
                  + "] in ["
                  + context.modelName
                  + "]. Available parts were: "
                  + context.allPartsBySingleAndFullHeirachicalId.keySet()
            );
         }

         return null;
      } else {
         return MathConstant.ZERO_CONST::getResult;
      }
   }

   @Nullable
   @Override
   public BooleanSupplier getASMBoolSupplierOrNull(String variableKey, AnimSetupContext context) {
      String[] split = variableKey.split("\\.");
      String partName = split[0];
      EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
      EMFModelPart part = EMFManager.getModelFromHierarchicalId(partName, context.allPartsBySingleAndFullHeirachicalId);
      if (partVariable == null || !partVariable.isBoolean()) {
         return null;
      } else if (part != null) {
         return switch (partVariable) {
            case VISIBLE -> () -> part.visible;
            case VISIBLE_BOXES -> () -> !part.skipDraw;
            default -> null;
         };
      } else {
         if (this.printing() && (!context.modelName.endsWith("chest_large.jem") || !partName.endsWith("_left") && !partName.endsWith("_right"))) {
            EMFUtils.logWarn(
               "no part found for: ["
                  + variableKey
                  + "] in ["
                  + context.modelName
                  + "]. Available parts were: "
                  + context.allPartsBySingleAndFullHeirachicalId.keySet()
            );
         }

         return () -> false;
      }
   }

   @Nullable
   @Override
   public MathValue.ResultSupplier getASMFloatSupplierOrNull(String variableKey, AnimSetupContext context) {
      String[] split = variableKey.split("\\.");
      String partName = split[0];
      EMFModelOrRenderVariable partVariable = EMFModelOrRenderVariable.get(split[1]);
      EMFModelPart part = EMFManager.getModelFromHierarchicalId(partName, context.allPartsBySingleAndFullHeirachicalId);
      if (partVariable == null || partVariable.isBoolean()) {
         return null;
      } else if (part != null) {
         return switch (partVariable) {
            case TX -> () -> part.x;
            case TY -> () -> part.y;
            case TZ -> () -> part.z;
            case RX -> () -> part.xRot;
            case RY -> () -> part.yRot;
            case RZ -> () -> part.zRot;
            case SX -> () -> part.xScale;
            case SY -> () -> part.yScale;
            case SZ -> () -> part.zScale;
            default -> null;
         };
      } else {
         if (this.printing() && (!context.modelName.endsWith("chest_large.jem") || !partName.endsWith("_left") && !partName.endsWith("_right"))) {
            EMFUtils.logWarn(
               "no part found for: ["
                  + variableKey
                  + "] in ["
                  + context.modelName
                  + "]. Available parts were: "
                  + context.allPartsBySingleAndFullHeirachicalId.keySet()
            );
         }

         return () -> 0.0F;
      }
   }

   @Override
   public boolean createsThisVariable(String variableKey) {
      if (variableKey == null) {
         return false;
      } else {
         String[] split = variableKey.split("\\.");
         String partName = split[0];
         if ("render".equals(partName) && ((EMFConfig)EMF.config().getConfig()).enforceOptiFineAnimSyntaxLimits) {
            if (EMFModelOrRenderVariable.get(split[1]) != null && this.printing()) {
               EMFUtils.logError("Model part variable [" + variableKey + "] is not allowed, 'render' is a protected animation key name.");
            }

            return false;
         } else {
            return variableKey.matches("[a-zA-Z0-9_]+\\.([trs][xyz]$|visible$|visible_boxes$)");
         }
      }
   }

   @Nullable
   @Override
   public String getExplanationTranslationKey() {
      return "entity_model_features.config.variable_explanation.model_part";
   }

   @Nullable
   @Override
   public String getTitleTranslationKey() {
      return "entity_model_features.config.variable_explanation.model_part.title";
   }
}
