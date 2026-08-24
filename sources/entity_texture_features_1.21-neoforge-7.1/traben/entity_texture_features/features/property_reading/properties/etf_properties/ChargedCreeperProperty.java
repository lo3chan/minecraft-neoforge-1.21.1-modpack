package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.monster.Creeper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ChargedCreeperProperty extends BooleanProperty {
   protected ChargedCreeperProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"creeperCharged", "creeper_charged"}));
   }

   public static ChargedCreeperProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ChargedCreeperProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity != null && etfEntity.entity() instanceof Creeper creeper ? creeper.isPowered() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"creeperCharged", "creeper_charged"};
   }
}
