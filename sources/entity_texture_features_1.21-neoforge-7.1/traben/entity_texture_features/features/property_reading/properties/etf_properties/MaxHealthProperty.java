package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class MaxHealthProperty extends FloatRangeFromStringArrayProperty {
   protected MaxHealthProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"maxHealth", "max_health"}));
   }

   public static MaxHealthProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new MaxHealthProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
      return entity != null && entity.entity() instanceof LivingEntity alive ? alive.getMaxHealth() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"maxHealth", "max_health"};
   }
}
