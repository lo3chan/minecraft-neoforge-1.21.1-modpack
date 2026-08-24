package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class LightProperty extends SimpleIntegerArrayProperty {
   protected LightProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"light"}));
   }

   public static LightProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new LightProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"light"};
   }

   @Override
   protected int getValueFromEntity(ETFEntityRenderState entity) {
      return entity != null && entity.world() != null && entity.blockPos() != null ? entity.world().getMaxLocalRawBrightness(entity.blockPos()) : -1;
   }
}
