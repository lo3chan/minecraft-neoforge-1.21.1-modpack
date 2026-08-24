package net.cibernet.alchemancy.item.components;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;

public record InfusedPropertiesComponent(List<Holder<Property>> properties) {
   public static final Codec<InfusedPropertiesComponent> CODEC = Codec.list(Property.CODEC).xmap(InfusedPropertiesComponent::new, a -> a.properties);
   public static final StreamCodec<RegistryFriendlyByteBuf, InfusedPropertiesComponent> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.holderRegistry(AlchemancyProperties.REGISTRY.getRegistryKey()).apply(ByteBufCodecs.list()),
      comp -> comp.properties,
      InfusedPropertiesComponent::new
   );
   public static final InfusedPropertiesComponent EMPTY = new InfusedPropertiesComponent(new ArrayList<>());

   public void forEachProperty(Consumer<Holder<Property>> consumer, boolean ignoreDisabled) {
      this.properties
         .stream()
         .sorted(Comparator.comparingInt(p -> ((Property)p.value()).getPriority()))
         .filter(property -> !ignoreDisabled || !property.is(AlchemancyTags.Properties.DISABLED))
         .forEach(consumer);
   }

   public void forEachProperty(Consumer<Holder<Property>> consumer) {
      this.forEachProperty(consumer, true);
   }

   public boolean hasProperty(Holder<Property> property) {
      return !property.is(AlchemancyTags.Properties.DISABLED) && this.properties.contains(property);
   }

   public boolean hasProperty(TagKey<Property> propertyTag) {
      return this.properties.stream().anyMatch(property -> property.is(propertyTag) && !property.is(AlchemancyTags.Properties.DISABLED));
   }

   @Override
   public boolean equals(Object other) {
      return other == this || other instanceof InfusedPropertiesComponent itemenchantments && this.properties.equals(itemenchantments.properties);
   }

   @Override
   public String toString() {
      StringBuilder str = new StringBuilder("[");

      for (Holder<Property> holder : this.properties) {
         str.append(((Property)holder.value()).getKey()).append(" ");
      }

      str.append("]");
      return str.toString();
   }

   public static class Mutable {
      private final List<Holder<Property>> properties;

      public Mutable(InfusedPropertiesComponent component) {
         this.properties = new ArrayList<>(component.properties);
      }

      public boolean hasProperty(Holder<Property> property) {
         return this.properties.contains(property);
      }

      public boolean addProperty(Holder<Property> property) {
         return !this.hasProperty(property) ? this.properties.add(property) : false;
      }

      public boolean removeProperty(Holder<Property> property) {
         return this.properties.remove(property);
      }

      public boolean truncateProperties(int limit) {
         List<Holder<Property>> slotless = List.copyOf(this.properties)
            .stream()
            .filter(propertyHolder -> propertyHolder.is(AlchemancyTags.Properties.SLOTLESS))
            .toList();
         if (this.properties.size() - slotless.size() <= limit) {
            return false;
         } else {
            this.properties.removeIf(slotless::contains);
            this.properties.subList(limit, this.properties.size()).clear();
            this.properties.addAll(slotless);
            return true;
         }
      }

      public InfusedPropertiesComponent toImutable() {
         return new InfusedPropertiesComponent(this.properties);
      }
   }
}
