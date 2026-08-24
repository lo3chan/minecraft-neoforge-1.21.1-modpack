package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class DistanceToPlayerProperty extends FloatRangeFromStringArrayProperty {
   protected DistanceToPlayerProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"distance", "distanceFromPlayer"}));
   }

   public static DistanceToPlayerProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new DistanceToPlayerProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
      return Minecraft.getInstance().player == null ? null : entity.distanceTo(Minecraft.getInstance().player);
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"distance", "distanceFromPlayer"};
   }
}
