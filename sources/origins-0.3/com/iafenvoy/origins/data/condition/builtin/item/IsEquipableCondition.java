package com.iafenvoy.origins.data.condition.builtin.item;

import com.iafenvoy.origins.data.condition.ItemCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record IsEquipableCondition(Optional<EquipmentSlot> slot) implements ItemCondition {
   public static final MapCodec<IsEquipableCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(EquipmentSlot.CODEC.optionalFieldOf("slot").forGetter(IsEquipableCondition::slot)).apply(i, IsEquipableCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends ItemCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Level level, @NotNull ItemStack stack) {
      Equipable equipable = Equipable.get(stack);
      return equipable == null ? false : this.slot.<Boolean>map(x -> Objects.equals(x, equipable.getEquipmentSlot())).orElse(true);
   }
}
