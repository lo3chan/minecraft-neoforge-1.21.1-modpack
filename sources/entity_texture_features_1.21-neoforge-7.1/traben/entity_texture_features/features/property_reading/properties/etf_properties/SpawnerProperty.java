package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class SpawnerProperty extends BooleanProperty {
   protected SpawnerProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"isSpawner", "spawner"}));
   }

   public static SpawnerProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new SpawnerProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity != null ? etfEntity.uuid().getLeastSignificantBits() == 53021371281465L : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"isSpawner", "spawner"};
   }
}
