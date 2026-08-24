package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class HardcoreProperty extends BooleanProperty {
   protected HardcoreProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"hardcore"}));
   }

   public static HardcoreProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new HardcoreProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState entity) {
      return entity != null ? entity.world().getLevelData().isHardcore() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"hardcore"};
   }
}
