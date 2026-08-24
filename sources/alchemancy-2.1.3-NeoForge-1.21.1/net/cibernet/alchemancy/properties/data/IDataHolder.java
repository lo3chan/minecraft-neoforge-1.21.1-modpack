package net.cibernet.alchemancy.properties.data;

import java.util.Optional;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IDataHolder<T> {
   T readData(CompoundTag var1);

   CompoundTag writeData(T var1);

   T getDefaultData();

   default T combineData(@Nullable T currentData, T newData) {
      return currentData == null ? newData : currentData;
   }

   default void combineDataAndSet(ItemStack stack, ItemStack from) {
      this.setData(stack, this.hasData(stack) ? this.combineData(this.getData(stack), this.getData(from)) : this.getData(from));
   }

   default boolean hasData(ItemStack stack) {
      return stack.has(AlchemancyItems.Components.PROPERTY_DATA)
         && this instanceof Property property
         && ((PropertyDataComponent)stack.get(AlchemancyItems.Components.PROPERTY_DATA)).getDataNbt(property.asHolder()).isPresent();
   }

   default T getData(ItemStack stack) {
      if (this instanceof Property property) {
         if (stack.has(AlchemancyItems.Components.PROPERTY_DATA)) {
            Optional<CompoundTag> nbt = ((PropertyDataComponent)stack.get(AlchemancyItems.Components.PROPERTY_DATA))
               .getDataNbt(AlchemancyProperties.getHolder(property));
            if (nbt.isPresent()) {
               return this.readData(nbt.get());
            }
         }

         return this.getDefaultData();
      } else {
         return this.getDefaultData();
      }
   }

   default void setData(ItemStack stack, T value) {
      stack.set(
         AlchemancyItems.Components.PROPERTY_DATA,
         this.setData((PropertyDataComponent)stack.getOrDefault(AlchemancyItems.Components.PROPERTY_DATA, PropertyDataComponent.EMPTY), value)
      );
   }

   default void removeData(ItemStack stack) {
      PropertyDataComponent comp = (PropertyDataComponent)stack.getOrDefault(AlchemancyItems.Components.PROPERTY_DATA, PropertyDataComponent.EMPTY);
      comp = this.removeData(comp);
      stack.set(AlchemancyItems.Components.PROPERTY_DATA, comp);
   }

   default PropertyDataComponent setData(PropertyDataComponent component, T value) {
      if (this instanceof Property property) {
         PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(component);
         data.setDataNbt(AlchemancyProperties.getHolder(property), this.writeData(value));
         return data.toImmutable();
      } else {
         return component;
      }
   }

   default PropertyDataComponent removeData(PropertyDataComponent component) {
      if (this instanceof Property property) {
         PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(component);
         data.removeData(AlchemancyProperties.getHolder(property));
         return data.toImmutable();
      } else {
         return component;
      }
   }

   default boolean cluelessCanReset() {
      return true;
   }

   default void copyData(ItemStack from, ItemStack to) {
      this.setData(to, this.getData(from));
   }
}
