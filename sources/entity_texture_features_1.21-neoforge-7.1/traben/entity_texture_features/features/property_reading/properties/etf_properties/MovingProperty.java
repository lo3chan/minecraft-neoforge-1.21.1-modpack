package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class MovingProperty extends BooleanProperty {
   protected MovingProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"moving", "is_moving"}));
   }

   public static MovingProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new MovingProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity.velocity().horizontalDistance() != 0.0;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"moving", "is_moving"};
   }
}
