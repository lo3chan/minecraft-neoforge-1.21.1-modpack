package net.cibernet.alchemancy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MixinUtils {
   public static ItemStack sodiumCompat$processedStack = null;
   private static final List<ResourceLocation> COMPONENTS_TO_MODIFY = new ArrayList<>();
   private static final List<ResourceLocation> COMPONENTS_TO_IGNORE = List.of(
      Alchemancy.resourceLocation("infused_properties"), Alchemancy.resourceLocation("innate_properties"), Alchemancy.resourceLocation("property_data")
   );

   public static void registerModifiableDataComponent(ResourceLocation... keys) {
      COMPONENTS_TO_MODIFY.addAll(Arrays.asList(keys));
   }

   public static <T> T getDataComponent(Object holder, DataComponentType<? extends T> dataComponentType, T original) {
      ResourceLocation key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(dataComponentType);
      if (key == null
         || !AlchemancyItems.Components.INFUSED_PROPERTIES.isBound()
         || !(AlchemancyProperties.SHOCKING.isBound() && holder instanceof ItemStack stack)
         || COMPONENTS_TO_IGNORE.contains(key)) {
         return original;
      } else if (BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(dataComponentType).is(AlchemancyTags.DataComponents.DISABLED_BY_DEAD)
         && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DEAD)) {
         return null;
      } else if (!COMPONENTS_TO_MODIFY.contains(key)) {
         return original;
      } else {
         AtomicReference<T> result = new AtomicReference<>(original);
         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
            T val = (T)((Property)propertyHolder.value()).modifyDataComponent(stack, dataComponentType, result.get());
            result.set(val);
         }, !BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(dataComponentType).is(AlchemancyTags.DataComponents.UNTOGGLEABLE));
         return result.get();
      }
   }
}
