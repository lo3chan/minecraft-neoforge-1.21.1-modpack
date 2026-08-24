package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Properties;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Slime;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class SizeProperty extends SimpleIntegerArrayProperty {
   protected SizeProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"sizes", "size"}));
   }

   public static SizeProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new SizeProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"sizes", "size"};
   }

   @Override
   protected int getValueFromEntity(ETFEntityRenderState entity) {
      if (entity != null && entity.entity() instanceof Slime slime) {
         return slime.getSize() - 1;
      } else {
         return entity != null && entity.entity() instanceof Phantom phantom ? phantom.getPhantomSize() : 0;
      }
   }
}
