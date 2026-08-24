package traben.entity_model_features.propeties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.EMFManager;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty.RandomPropertyException;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ModelRuleIndexProperty extends SimpleIntegerArrayProperty {
   protected ModelRuleIndexProperty(Properties properties, int propertyNum) throws RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"modelRule", "model_rule"}));
   }

   public static ModelRuleIndexProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ModelRuleIndexProperty(properties, propertyNum);
      } catch (RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   public String[] getPropertyIds() {
      return new String[]{"modelRule", "model_rule"};
   }

   protected int getValueFromEntity(ETFEntityRenderState entity) {
      return (Integer)EMFManager.getInstance().lastModelRuleOfEntity.get(entity.uuid());
   }
}
