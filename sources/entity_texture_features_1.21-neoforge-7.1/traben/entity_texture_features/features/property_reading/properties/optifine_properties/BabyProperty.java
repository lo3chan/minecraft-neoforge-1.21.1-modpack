package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class BabyProperty extends BooleanProperty {
   protected BabyProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"baby"}));
   }

   public static BabyProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new BabyProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState entity) {
      return entity != null && entity.entity() instanceof LivingEntity alive ? alive.isBaby() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"baby"};
   }
}
