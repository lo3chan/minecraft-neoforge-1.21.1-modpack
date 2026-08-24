package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class HealthProperty extends FloatRangeFromStringArrayProperty {
   private final boolean isPercentage = this.originalInput.contains("%");

   protected HealthProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"health"}));
   }

   public static HealthProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new HealthProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
      if (entity != null && entity.entity() instanceof LivingEntity alive) {
         float health = alive.getHealth();
         return this.isPercentage ? Mth.ceil(health / alive.getMaxHealth() * 100.0F) : health;
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"health"};
   }
}
