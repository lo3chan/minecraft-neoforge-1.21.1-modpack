package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.animal.Panda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class PandaGeneProperty extends StringArrayOrRegexProperty {
   protected PandaGeneProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"hiddenGene", "gene"}));
   }

   public static PandaGeneProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new PandaGeneProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return true;
   }

   @Nullable
   @Override
   protected String getValueFromEntity(ETFEntityRenderState entityETF) {
      return entityETF != null && entityETF.entity() instanceof Panda panda ? panda.getHiddenGene().getSerializedName() : null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"hiddenGene", "gene"};
   }
}
