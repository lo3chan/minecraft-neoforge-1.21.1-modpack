package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class TeammateProperty extends BooleanProperty {
   protected TeammateProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"isTeammate", "teammate"}));
   }

   public static TeammateProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new TeammateProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity != null && etfEntity.entity() instanceof Entity entity && Minecraft.getInstance().player != null
         ? entity.isAlliedTo(Minecraft.getInstance().player)
         : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"isTeammate", "teammate"};
   }
}
