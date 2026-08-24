package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class RegionalDifficultyProperty extends FloatRangeFromStringArrayProperty {
   protected RegionalDifficultyProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"regionalDifficulty", "regional_difficulty"}));
   }

   public static RegionalDifficultyProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new RegionalDifficultyProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"regionalDifficulty", "regional_difficulty"};
   }

   protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
      return entity != null && entity.world() != null ? entity.world().getCurrentDifficultyAt(entity.blockPos()).getEffectiveDifficulty() : 0.0F;
   }
}
