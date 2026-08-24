package com.iafenvoy.origins.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.util.HolderHelper;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;

@EventBusSubscriber
public record ItemPowersComponent(Multimap<EquipmentSlotGroup, ItemPowersComponent.Entry> powers) {
   public static final Codec<ItemPowersComponent> CODEC = RecordCodecBuilder.create(
      i -> i.group(
            CollectionCodecs.multiMapCodec(EquipmentSlotGroup.CODEC, ItemPowersComponent.Entry.CODEC).fieldOf("powers").forGetter(ItemPowersComponent::powers)
         )
         .apply(i, ItemPowersComponent::new)
   );
   public static final ItemPowersComponent EMPTY = new ItemPowersComponent(HashMultimap.create());

   public boolean isEmpty() {
      return this.powers.isEmpty();
   }

   public boolean contains(EquipmentSlot slot, Holder<Power> power) {
      return this.powers
         .entries()
         .stream()
         .filter(e -> ((EquipmentSlotGroup)e.getKey()).test(slot))
         .anyMatch(e -> Objects.equals(((ItemPowersComponent.Entry)e.getValue()).power(), power));
   }

   public List<ItemPowersComponent.Entry> get(EquipmentSlot slot) {
      return this.powers.entries().stream().filter(e -> ((EquipmentSlotGroup)e.getKey()).test(slot)).map(Map.Entry::getValue).toList();
   }

   @SubscribeEvent
   public static void appendTooltip(AddAttributeTooltipsEvent event) {
      ItemPowersComponent component = (ItemPowersComponent)event.getStack().getOrDefault(OriginsDataComponents.ITEM_POWERS, EMPTY);
      boolean advanced = event.getContext().flag().isAdvanced();

      for (ItemPowersComponent.Entry entry : component.powers.values()) {
         Power power = (Power)entry.power.value();
         ResourceLocation id = HolderHelper.id(entry.power);
         if (!entry.hidden()) {
            event.addTooltipLines(
               new Component[]{
                  Component.translatable("tooltip.origins.stack_power.name", new Object[]{power.getName(id)})
                     .withStyle(entry.negative() ? ChatFormatting.RED : ChatFormatting.YELLOW)
               }
            );
            if (advanced) {
               event.addTooltipLines(
                  new Component[]{
                     Component.translatable("tooltip.origins.stack_power.description", new Object[]{power.getDescription(id)}).withStyle(ChatFormatting.GRAY)
                  }
               );
            }
         }
      }
   }

   public static ItemPowersComponent.Builder builder() {
      return new ItemPowersComponent.Builder();
   }

   public static class Builder {
      private final Multimap<EquipmentSlotGroup, ItemPowersComponent.Entry> powers = HashMultimap.create();

      public ItemPowersComponent.Builder add(ItemStack stack) {
         return this.add((ItemPowersComponent)stack.getOrDefault(OriginsDataComponents.ITEM_POWERS, ItemPowersComponent.EMPTY));
      }

      public ItemPowersComponent.Builder add(ItemPowersComponent component) {
         this.powers.putAll(component.powers);
         return this;
      }

      public ItemPowersComponent.Builder add(EquipmentSlotGroup slot, Holder<Power> power, boolean hidden, boolean negative) {
         this.powers.put(slot, new ItemPowersComponent.Entry(power, hidden, negative));
         return this;
      }

      public ItemPowersComponent.Builder add(Iterable<EquipmentSlotGroup> slots, Holder<Power> power, boolean hidden, boolean negative) {
         slots.forEach(slot -> this.add(slot, power, hidden, negative));
         return this;
      }

      public ItemPowersComponent.Builder remove(EquipmentSlotGroup slot, Holder<Power> power) {
         for (ItemPowersComponent.Entry entry : this.powers.get(slot).stream().filter(e1 -> e1.power().equals(power)).toList()) {
            this.powers.remove(slot, entry);
         }

         return this;
      }

      public ItemPowersComponent.Builder remove(Iterable<EquipmentSlotGroup> slots, Holder<Power> power) {
         slots.forEach(slot -> this.remove(slot, power));
         return this;
      }

      public ItemPowersComponent build() {
         return new ItemPowersComponent(this.powers);
      }
   }

   public record Entry(Holder<Power> power, boolean hidden, boolean negative) {
      public static final Codec<ItemPowersComponent.Entry> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               Power.CODEC.fieldOf("power").forGetter(ItemPowersComponent.Entry::power),
               Codec.BOOL.optionalFieldOf("hidden", false).forGetter(ItemPowersComponent.Entry::hidden),
               Codec.BOOL.optionalFieldOf("negative", false).forGetter(ItemPowersComponent.Entry::negative)
            )
            .apply(i, ItemPowersComponent.Entry::new)
      );
   }
}
