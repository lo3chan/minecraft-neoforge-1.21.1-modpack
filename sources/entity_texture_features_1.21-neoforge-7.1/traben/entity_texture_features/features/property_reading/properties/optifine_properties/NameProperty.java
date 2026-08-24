package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class NameProperty extends StringArrayOrRegexProperty {
   protected NameProperty(String data) throws RandomProperty.RandomPropertyException {
      super(data);
   }

   public static NameProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         String dataFromProperty = readPropertiesOrThrow(properties, propertyNum, new String[]{"name", "names"});
         ArrayList<String> names = new ArrayList<>();
         if (dataFromProperty.isBlank()) {
            throw new RandomProperty.RandomPropertyException("Name failed");
         } else {
            if (!dataFromProperty.startsWith("regex:") && !dataFromProperty.startsWith("pattern:")) {
               Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(dataFromProperty);

               while (m.find()) {
                  names.add(m.group(1).replace("\"", "").trim());
               }
            } else {
               names.add(dataFromProperty);
            }

            StringBuilder builder = new StringBuilder();

            for (String str : names) {
               builder.append(str).append(" ");
            }

            return new NameProperty(builder.toString().trim());
         }
      } catch (RandomProperty.RandomPropertyException var7) {
         return null;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return false;
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      if (etfEntity != null && etfEntity.entity() instanceof Player player) {
         return player.getName().getString();
      } else {
         if (etfEntity != null && etfEntity.hasCustomName()) {
            Component entityNameText = etfEntity.customName();
            if (entityNameText != null) {
               if (entityNameText.getContents() instanceof LiteralContents literal) {
                  return literal.text();
               }

               return entityNameText.getString();
            }
         }

         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"name", "names"};
   }
}
