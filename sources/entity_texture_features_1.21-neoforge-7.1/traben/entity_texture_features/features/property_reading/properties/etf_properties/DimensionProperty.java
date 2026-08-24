package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Optional;
import java.util.Properties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public class DimensionProperty extends StringArrayOrRegexProperty {
   private final boolean doPrint;

   protected DimensionProperty(String string) throws RandomProperty.RandomPropertyException {
      super(string.replace("print:", ""));
      this.doPrint = string.startsWith("print:");
   }

   public static DimensionProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new DimensionProperty(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "dimension"));
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      if (etfEntity == null) {
         return null;
      } else {
         Level world = etfEntity.world();
         if (world == null) {
            return null;
         } else {
            Optional<ResourceKey<DimensionType>> dimKey = etfEntity.world().dimensionTypeRegistration().unwrapKey();
            if (dimKey.isEmpty()) {
               return null;
            } else {
               ResourceLocation key = dimKey.get().location();
               if (key == null) {
                  return null;
               } else {
                  String output;
                  if (key.equals(BuiltinDimensionTypes.OVERWORLD_EFFECTS) || key.getPath().equals("overworld_caves")) {
                     output = "overworld";
                  } else if (key.equals(BuiltinDimensionTypes.NETHER_EFFECTS)) {
                     output = "the_nether";
                  } else if (key.equals(BuiltinDimensionTypes.END_EFFECTS)) {
                     output = "the_end";
                  } else {
                     output = key.toString();
                  }

                  if (this.doPrint) {
                     ETFUtils2.logMessage("[Dimension property print]: " + output);
                  }

                  return output;
               }
            }
         }
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"dimension"};
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return false;
   }
}
