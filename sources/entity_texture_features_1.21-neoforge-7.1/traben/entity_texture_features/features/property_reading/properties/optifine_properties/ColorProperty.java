package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.Optional;
import java.util.Properties;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ColorProperty extends StringArrayOrRegexProperty {
   protected ColorProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "colors", "collarColors"));
   }

   public static ColorProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ColorProperty(properties, propertyNum);
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
   protected String getValueFromEntity(ETFEntityRenderState state) {
      if (state != null) {
         ETFEntity entity = state.entity();
         if (entity instanceof Llama llama) {
            DyeColor str = llama.getSwag();
            if (str != null) {
               return str.getName();
            }
         }

         if (entity instanceof Cat cat) {
            return cat.getCollarColor().getName();
         }

         if (entity instanceof Shulker shulker) {
            DyeColor str = shulker.getColor();
            if (str != null) {
               return str.getName();
            }
         }

         if (entity instanceof Wolf wolf) {
            return wolf.getCollarColor().getName();
         }

         if (entity instanceof Sheep sheep) {
            return sheep.getColor().getName();
         }

         if (entity instanceof TropicalFish fishy) {
            DyeColor str = TropicalFish.getBaseColor(fishy.getVariant().getPackedId());
            return str.getName();
         }

         if (entity instanceof VariantHolder<?> variantHolder) {
            try {
               if (variantHolder.getVariant() instanceof DyeColor dye) {
                  return dye.getName();
               }

               if (variantHolder.getVariant() instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof DyeColor dye) {
                  return dye.getName();
               }
            } catch (Exception var8) {
            }
         }
      }

      return null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"colors", "collarColors"};
   }
}
