package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.IronGolem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class PlayerCreatedProperty extends BooleanProperty {
   protected PlayerCreatedProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"playerCreated", "player_created"}));
   }

   public static PlayerCreatedProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new PlayerCreatedProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity != null && etfEntity.entity() instanceof IronGolem golem ? golem.isPlayerCreated() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"playerCreated", "player_created"};
   }
}
