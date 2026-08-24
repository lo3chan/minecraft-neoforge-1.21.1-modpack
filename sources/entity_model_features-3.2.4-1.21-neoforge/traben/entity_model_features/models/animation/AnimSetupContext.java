package traben.entity_model_features.models.animation;

import java.util.HashMap;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.math.expression_tree.OldEMFAnimationHandler;
import traben.entity_model_features.models.parts.EMFModelPart;

public class AnimSetupContext implements AutoCloseable {
   public OldEMFAnimationHandler oldAnimationHandler;
   public HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId;
   @Nullable
   public String animKey = null;
   public final String modelName;

   public AnimSetupContext(String modelName, OldEMFAnimationHandler oldAnimationHandler, HashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId) {
      this.modelName = modelName;
      this.oldAnimationHandler = oldAnimationHandler;
      this.allPartsBySingleAndFullHeirachicalId = allPartsBySingleAndFullHeirachicalId;
   }

   @Override
   public void close() {
      this.oldAnimationHandler = null;
      this.allPartsBySingleAndFullHeirachicalId = null;
   }
}
