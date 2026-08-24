package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.horse.Llama;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class LlamaInventoryProperty extends SimpleIntegerArrayProperty {
   protected LlamaInventoryProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericIntegerSplitWithRanges(properties, propertyNum, new String[]{"llamaInventory"}));
   }

   public static LlamaInventoryProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new LlamaInventoryProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"llamaInventory"};
   }

   @Override
   protected int getValueFromEntity(ETFEntityRenderState entity) {
      return entity != null && entity.entity() instanceof Llama llama ? llama.getInventoryColumns() : -2147483648;
   }
}
