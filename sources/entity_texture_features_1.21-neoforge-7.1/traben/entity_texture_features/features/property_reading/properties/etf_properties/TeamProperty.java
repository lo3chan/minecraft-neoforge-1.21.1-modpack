package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TeamProperty extends StringArrayOrRegexProperty {
   protected TeamProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "teams", "team"));
   }

   public static TeamProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new TeamProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity.scoreboardTeam() != null ? etfEntity.scoreboardTeam().getName() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"teams", "team"};
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return false;
   }
}
