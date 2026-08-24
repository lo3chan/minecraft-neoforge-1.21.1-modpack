package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ClientPlayerProperty extends BooleanProperty {
   protected ClientPlayerProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"isClientPlayer", "clientPlayer"}));
   }

   public static ClientPlayerProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ClientPlayerProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState etfEntity) {
      return etfEntity != null
         && etfEntity.entity() instanceof Player entity
         && Minecraft.getInstance().player != null
         && entity.getUUID().equals(Minecraft.getInstance().player.getUUID());
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"isClientPlayer", "clientPlayer"};
   }
}
