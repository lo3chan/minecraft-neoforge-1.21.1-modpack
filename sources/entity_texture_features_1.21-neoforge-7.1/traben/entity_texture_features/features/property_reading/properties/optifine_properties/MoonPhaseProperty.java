package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class MoonPhaseProperty extends SimpleIntegerArrayProperty {
   protected MoonPhaseProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"moonPhase"}));
   }

   public static MoonPhaseProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new MoonPhaseProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"moonPhase"};
   }

   @Override
   protected int getValueFromEntity(ETFEntityRenderState entity) {
      return entity.world() == null ? -2147483648 : entity.world().getMoonPhase();
   }
}
