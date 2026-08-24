package net.cibernet.alchemancy.item.components;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record PropertyDataComponent(Map<Holder<Property>, CompoundTag> data) {
   public static final PropertyDataComponent EMPTY = new PropertyDataComponent(Map.of());
   public static final Codec<PropertyDataComponent> CODEC = Codec.unboundedMap(Property.CODEC, CompoundTag.CODEC)
      .xmap(PropertyDataComponent::new, PropertyDataComponent::data);
   public static final StreamCodec<RegistryFriendlyByteBuf, PropertyDataComponent> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.map(HashMap::new, Property.STREAM_CODEC, ByteBufCodecs.COMPOUND_TAG), PropertyDataComponent::data, PropertyDataComponent::new
   );

   public Optional<CompoundTag> getDataNbt(Holder<Property> property) {
      return this.data.containsKey(property) ? Optional.of(this.data.get(property)) : Optional.empty();
   }

   public static void mergeData(ItemStack to, ItemStack from) {
      PropertyDataComponent toData = (PropertyDataComponent)to.get(AlchemancyItems.Components.PROPERTY_DATA);
      PropertyDataComponent fromData = (PropertyDataComponent)from.get(AlchemancyItems.Components.PROPERTY_DATA);
      if (fromData != null) {
         if (toData == null) {
            to.set(AlchemancyItems.Components.PROPERTY_DATA, fromData);
         } else {
            to.set(AlchemancyItems.Components.PROPERTY_DATA, new PropertyDataComponent.Mutable(toData).mergeData(fromData).toImmutable());
         }
      }
   }

   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   public static class Mutable {
      private final HashMap<Holder<Property>, CompoundTag> data;

      public Mutable(PropertyDataComponent data) {
         this.data = new HashMap<>(data.data);
      }

      public Optional<CompoundTag> getDataNbt(Holder<Property> property) {
         return this.data.containsKey(property) ? Optional.of(this.data.get(property)) : Optional.empty();
      }

      public void setDataNbt(Holder<Property> property, CompoundTag value) {
         this.data.put(property, value);
      }

      public void removeData(Holder<Property> propertyHolder) {
         this.data.remove(propertyHolder);
      }

      public PropertyDataComponent toImmutable() {
         return new PropertyDataComponent(this.data);
      }

      public PropertyDataComponent.Mutable mergeData(PropertyDataComponent other) {
         for (Entry<Holder<Property>, CompoundTag> entry : other.data().entrySet()) {
            if (this.data.containsKey(entry.getKey())) {
               this.data.get(entry.getKey()).merge(entry.getValue());
            } else {
               this.data.put(entry.getKey(), entry.getValue());
            }
         }

         return this;
      }
   }
}
