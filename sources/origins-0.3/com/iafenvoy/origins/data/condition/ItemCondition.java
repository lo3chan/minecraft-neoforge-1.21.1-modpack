package com.iafenvoy.origins.data.condition;

import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ItemCondition {
   Codec<ItemCondition> CODEC = DefaultedCodec.registryDispatch(
      ConditionRegistries.ITEM_CONDITION, ItemCondition::codec, Function.identity(), () -> AlwaysTrueCondition.INSTANCE
   );

   static MapCodec<ItemCondition> optionalCodec(String name) {
      return CODEC.optionalFieldOf(name, AlwaysTrueCondition.INSTANCE);
   }

   @NotNull
   MapCodec<? extends ItemCondition> codec();

   boolean test(@NotNull Level var1, @NotNull ItemStack var2);
}
