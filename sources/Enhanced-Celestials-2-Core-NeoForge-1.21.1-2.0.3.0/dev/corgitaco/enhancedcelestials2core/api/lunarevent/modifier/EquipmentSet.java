package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record EquipmentSet(EnumMap<EquipmentSlot, MobEquipment.EquipmentEntry> equipment) {
   public static final Codec<EquipmentSet> DIRECT_CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, MobEquipment.EquipmentEntry.CODEC)
      .xmap(EquipmentSet::fromMap, EquipmentSet::equipment)
      .validate(EquipmentSet::validate);

   private static EquipmentSet fromMap(Map<EquipmentSlot, MobEquipment.EquipmentEntry> map) {
      EnumMap<EquipmentSlot, MobEquipment.EquipmentEntry> enumMap = new EnumMap<>(EquipmentSlot.class);
      enumMap.putAll(map);
      return new EquipmentSet(enumMap);
   }

   private static DataResult<EquipmentSet> validate(EquipmentSet set) {
      for (Entry<EquipmentSlot, MobEquipment.EquipmentEntry> entry : set.equipment.entrySet()) {
         EquipmentSlot slot = entry.getKey();
         ItemStack stack = entry.getValue().item();
         if (!MobEquipment.canEquip(slot, stack)) {
            return DataResult.error(() -> "Item " + BuiltInRegistries.ITEM.getKey(stack.getItem()) + " cannot be equipped in the " + slot.getName() + " slot");
         }
      }

      return DataResult.success(set);
   }

   public boolean isEmpty() {
      return this.equipment.isEmpty();
   }
}
