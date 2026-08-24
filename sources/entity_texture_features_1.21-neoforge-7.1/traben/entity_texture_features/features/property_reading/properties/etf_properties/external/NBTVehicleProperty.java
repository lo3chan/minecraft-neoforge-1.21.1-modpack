package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class NBTVehicleProperty extends NBTProperty {
   protected NBTVehicleProperty(Properties properties, int propertyNum, String nbtPrefix) throws RandomProperty.RandomPropertyException {
      super(properties, propertyNum, nbtPrefix);
   }

   public static NBTProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new NBTVehicleProperty(properties, propertyNum, "nbtVehicle");
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   protected CompoundTag getEntityNBT(ETFEntityRenderState entity) {
      if (entity != null && entity.entity() instanceof Entity e) {
         ETFEntity vehicle = (ETFEntity)e.getVehicle();
         return vehicle != null ? vehicle.etf$getNbt() : INTENTIONAL_FAILURE;
      } else {
         return INTENTIONAL_FAILURE;
      }
   }
}
