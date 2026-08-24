package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ModLoadedProperty extends StringArrayOrRegexProperty {
   private final boolean matched;

   protected ModLoadedProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "modLoaded", "modsLoaded"));
      boolean matches = false;

      assert ETF.modsLoaded() != null;

      for (String modId : ETF.modsLoaded()) {
         if (this.MATCHER.testString(modId)) {
            matches = true;
            break;
         }
      }

      this.matched = matches;
   }

   public static ModLoadedProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ModLoadedProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      return null;
   }

   @Override
   public boolean testEntity(ETFEntityRenderState entity, boolean isUpdate) {
      return this.matched;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"modLoaded", "modsLoaded"};
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return false;
   }
}
