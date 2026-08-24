package traben.entity_model_features.propeties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty.RandomPropertyException;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class EntityVariableBooleanProperty extends RandomProperty {
   private final Map<String, Boolean> VARIABLE_MAP;

   protected EntityVariableBooleanProperty(Properties properties, int propertyNum) throws RandomPropertyException {
      String keyPrefix = "varb." + propertyNum + ".";
      this.VARIABLE_MAP = new HashMap<>();
      properties.forEach((key, value) -> {
         if (key != null && ((String)key).startsWith(keyPrefix)) {
            String instruction = ((String)value).trim();
            String variableKey = "varb." + ((String)key).replaceAll(keyPrefix, "");
            if (!variableKey.isBlank() && !instruction.isBlank()) {
               boolean matchTrue = instruction.contains("true");
               this.VARIABLE_MAP.put(variableKey, matchTrue);
            }
         }
      });
      if (this.VARIABLE_MAP.isEmpty()) {
         throw new RandomPropertyException("Variable booleans failed");
      }
   }

   public static EntityVariableBooleanProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new EntityVariableBooleanProperty(properties, propertyNum);
      } catch (RandomPropertyException var3) {
         return null;
      }
   }

   protected boolean testEntityInternal(ETFEntityRenderState etfEntity) {
      if (etfEntity != null && etfEntity.entity() instanceof EMFEntity IEMFEntity) {
         for (Entry<String, Boolean> stringFunctionEntry : this.VARIABLE_MAP.entrySet()) {
            boolean value = MathValue.toBoolean(IEMFEntity.emf$getVariableMap().getOrDefault(stringFunctionEntry.getKey(), -1.0F / 0.0F));
            if (stringFunctionEntry.getValue() != value) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @NotNull
   public String[] getPropertyIds() {
      return new String[]{"varb"};
   }

   protected String getPrintableRuleInfo() {
      return null;
   }
}
