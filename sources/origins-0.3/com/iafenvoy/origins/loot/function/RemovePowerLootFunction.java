package com.iafenvoy.origins.loot.function;

import com.iafenvoy.origins.data.ItemPowersComponent;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.registry.OriginsLootItemFunctions;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

public class RemovePowerLootFunction extends LootItemConditionalFunction {
   public static final MapCodec<RemovePowerLootFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance)
         .and(
            instance.group(
               CombinedCodecs.EQUIPMENT_SLOT_GROUP.optionalFieldOf("slot", List.of(EquipmentSlotGroup.ANY)).forGetter(RemovePowerLootFunction::slots),
               Power.CODEC.fieldOf("power").forGetter(RemovePowerLootFunction::powerId)
            )
         )
         .apply(instance, RemovePowerLootFunction::new)
   );
   private final List<EquipmentSlotGroup> slots;
   private final Holder<Power> power;

   private RemovePowerLootFunction(List<LootItemCondition> conditions, List<EquipmentSlotGroup> slots, Holder<Power> power) {
      super(conditions);
      this.slots = slots;
      this.power = power;
   }

   public List<EquipmentSlotGroup> slots() {
      return this.slots;
   }

   public Holder<Power> powerId() {
      return this.power;
   }

   @NotNull
   public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
      return (LootItemFunctionType<? extends LootItemConditionalFunction>)OriginsLootItemFunctions.REMOVE_POWER.get();
   }

   @NotNull
   public ItemStack run(ItemStack stack, @NotNull LootContext context) {
      ItemPowersComponent itemPowers = (ItemPowersComponent)stack.get(OriginsDataComponents.ITEM_POWERS);
      if (itemPowers == null) {
         return stack;
      } else {
         ItemPowersComponent newItemPowers = ItemPowersComponent.builder().add(itemPowers).remove(this.slots, this.power).build();
         if (newItemPowers.isEmpty()) {
            stack.remove((DataComponentType)OriginsDataComponents.ITEM_POWERS.get());
         } else {
            stack.set((DataComponentType)OriginsDataComponents.ITEM_POWERS.get(), newItemPowers);
         }

         return stack;
      }
   }
}
