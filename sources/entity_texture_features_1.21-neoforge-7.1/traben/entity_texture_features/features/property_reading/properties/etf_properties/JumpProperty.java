package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class JumpProperty extends FloatRangeFromStringArrayProperty {
   protected JumpProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"jump", "jumpStrength", "jumpHeight"}));
   }

   public static JumpProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new JumpProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
      return entity != null && entity.entity() instanceof AbstractHorse horse ? horse.playerJumpPendingScale : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"jump", "jumpStrength", "jumpHeight"};
   }
}
