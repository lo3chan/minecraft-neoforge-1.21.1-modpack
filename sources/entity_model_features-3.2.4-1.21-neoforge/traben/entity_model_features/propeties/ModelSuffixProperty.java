package traben.entity_model_features.propeties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.EMFManager;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty.RandomPropertyException;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ModelSuffixProperty extends SimpleIntegerArrayProperty {
   protected ModelSuffixProperty(Properties properties, int propertyNum) throws RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"modelSuffix", "model_suffix"}));
   }

   public static ModelSuffixProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ModelSuffixProperty(properties, propertyNum);
      } catch (RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   public String[] getPropertyIds() {
      return new String[]{"modelSuffix", "model_suffix"};
   }

   protected int getValueFromEntity(ETFEntityRenderState entity) {
      int val = (Integer)EMFManager.getInstance().lastModelSuffixOfEntity.get(entity.uuid());
      return Math.max(val, 0);
   }
}
