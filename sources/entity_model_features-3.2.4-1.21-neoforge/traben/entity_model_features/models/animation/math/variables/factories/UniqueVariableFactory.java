package traben.entity_model_features.models.animation.math.variables.factories;

import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public abstract class UniqueVariableFactory {
   @Override
   public boolean equals(Object obj) {
      return obj != null && obj.getClass().equals(this.getClass());
   }

   @Nullable
   public abstract MathValue.ResultSupplier getSupplierOrNull(String var1, AnimSetupContext var2);

   @Nullable
   public BooleanSupplier getASMBoolSupplierOrNull(String variableKey, AnimSetupContext context) {
      MathValue.ResultSupplier x = this.getSupplierOrNull(variableKey, context);
      return x == null ? null : () -> x.get() > 0.0F;
   }

   @Nullable
   public MathValue.ResultSupplier getASMFloatSupplierOrNull(String variableKey, AnimSetupContext context) {
      return this.getSupplierOrNull(variableKey, context);
   }

   public abstract boolean createsThisVariable(String var1);

   @Nullable
   public abstract String getExplanationTranslationKey();

   @Nullable
   public abstract String getTitleTranslationKey();

   protected boolean printing() {
      return ((EMFConfig)EMF.config().getConfig()).logModelCreationData;
   }
}
