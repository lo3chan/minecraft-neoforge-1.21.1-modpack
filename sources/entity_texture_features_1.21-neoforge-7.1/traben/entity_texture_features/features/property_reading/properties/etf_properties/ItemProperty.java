package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ItemProperty extends StringArrayOrRegexProperty {
   protected ItemProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"items", "item"}).replaceAll("(?<=(^| ))minecraft:", ""));
   }

   public static ItemProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ItemProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return true;
   }

   @Override
   public boolean testEntityInternal(ETFEntityRenderState entity) {
      if (this.ARRAY.size() == 1
         && this.ARRAY.stream().anyMatch(string -> "none".equals(string) || "any".equals(string) || "holding".equals(string) || "wearing".equals(string))) {
         if (this.ARRAY.contains("none")) {
            for (ItemStack item : entity.itemsEquipped()) {
               if (item != null && !item.isEmpty()) {
                  return false;
               }
            }

            return true;
         } else {
            Iterable<ItemStack> items;
            if (this.ARRAY.contains("any")) {
               items = entity.itemsEquipped();
            } else if (this.ARRAY.contains("holding")) {
               items = entity.handItems();
            } else {
               items = entity.armorItems();
            }

            boolean found = false;

            for (ItemStack itemx : items) {
               if (itemx != null && !itemx.isEmpty()) {
                  found = true;
                  break;
               }
            }

            return found;
         }
      } else {
         Iterable<ItemStack> equipped = entity.itemsEquipped();
         boolean found = false;

         for (ItemStack itemxx : equipped) {
            String itemString = itemxx.getItem().toString().replaceFirst("^minecraft:", "");
            found = this.MATCHER.testString(itemString);
            if (found) {
               break;
            }
         }

         return found;
      }
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      return null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"items", "item"};
   }
}
