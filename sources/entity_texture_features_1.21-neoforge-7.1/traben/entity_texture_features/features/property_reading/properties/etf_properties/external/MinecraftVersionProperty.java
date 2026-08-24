package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SemVerRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class MinecraftVersionProperty extends SemVerRangeFromStringArrayProperty {
   private final SemVerRangeFromStringArrayProperty.SemVerNumber version = new SemVerRangeFromStringArrayProperty.SemVerNumber(
      Minecraft.getInstance().getLaunchedVersion()
   );

   protected MinecraftVersionProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"minecraftVersion"}));
   }

   public static MinecraftVersionProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new MinecraftVersionProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   protected SemVerRangeFromStringArrayProperty.SemVerNumber getRangeValueFromEntity(ETFEntityRenderState entity) {
      return this.version;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"minecraftVersion"};
   }
}
