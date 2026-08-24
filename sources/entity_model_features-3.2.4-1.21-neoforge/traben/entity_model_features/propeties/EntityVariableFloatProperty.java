package traben.entity_model_features.propeties;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty.RandomPropertyException;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class EntityVariableFloatProperty extends RandomProperty {
   private final List<FloatRangeFromStringArrayProperty> VARIABLES;

   protected EntityVariableFloatProperty(Properties properties, int propertyNum) throws RandomPropertyException {
      String keyPrefix = "var." + propertyNum + ".";
      this.VARIABLES = new ArrayList<>();

      for (Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
         String key = objectObjectEntry.getKey().toString();
         Object value = objectObjectEntry.getValue();
         if (key != null && key.startsWith(keyPrefix)) {
            String instruction = ((String)value).trim();
            String variableKey = "var." + key.replaceAll(keyPrefix, "");
            if (!variableKey.isBlank() && !instruction.isBlank()) {
               EntityVariableFloatProperty.InnerTester tester = new EntityVariableFloatProperty.InnerTester(
                  instruction, emfEntity -> emfEntity.emf$getVariableMap().getOrDefault(variableKey, 0.0F), variableKey
               );
               this.VARIABLES.add(tester);
            }
         }
      }

      if (this.VARIABLES.isEmpty()) {
         throw new RandomPropertyException("Variable float failed");
      }
   }

   public static EntityVariableFloatProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new EntityVariableFloatProperty(properties, propertyNum);
      } catch (RandomPropertyException var3) {
         return null;
      }
   }

   protected boolean testEntityInternal(ETFEntityRenderState etfEntity) {
      for (FloatRangeFromStringArrayProperty variable : this.VARIABLES) {
         if (!variable.testEntityInternal(etfEntity)) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public String[] getPropertyIds() {
      return new String[]{"var"};
   }

   protected String getPrintableRuleInfo() {
      return null;
   }

   private static class InnerTester extends FloatRangeFromStringArrayProperty {
      private final Function<EMFEntity, Float> getter;
      private final String id;

      protected InnerTester(String string, Function<EMFEntity, Float> getter, String id) throws RandomPropertyException {
         super(string);
         this.getter = getter;
         this.id = id;
      }

      @Nullable
      protected Float getRangeValueFromEntity(ETFEntityRenderState etfEntity) {
         return etfEntity != null && etfEntity.entity() instanceof EMFEntity IEMFEntity ? this.getter.apply(IEMFEntity) : null;
      }

      @NotNull
      public String[] getPropertyIds() {
         return new String[]{this.id};
      }
   }
}
