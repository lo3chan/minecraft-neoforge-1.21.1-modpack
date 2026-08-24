package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record AnvilCostModifier(double amplifier) implements LunarEventModifier {
   public static final MapCodec<AnvilCostModifier> CODEC = Codec.DOUBLE.fieldOf("amplifier").xmap(AnvilCostModifier::new, AnvilCostModifier::amplifier);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.ANVIL_COST;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.anvil_cost", new Object[]{this.amplifier});
   }
}
