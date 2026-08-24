package traben.entity_model_features.utils;

import traben.entity_model_features.models.parts.EMFModelPartRoot;

public interface IEMFCustomModelHolder {
   default boolean emf$hasModel() {
      return this.emf$getModel() != null;
   }

   EMFModelPartRoot emf$getModel();

   void emf$setModel(EMFModelPartRoot var1);
}
