package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record EnchantmentCostModifier(double amplifier) implements LunarEventModifier {
   public static final MapCodec<EnchantmentCostModifier> CODEC = Codec.DOUBLE
      .fieldOf("amplifier")
      .xmap(EnchantmentCostModifier::new, EnchantmentCostModifier::amplifier);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.ENCHANTMENT_COST;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.enchantment_cost", new Object[]{this.amplifier});
   }
}
