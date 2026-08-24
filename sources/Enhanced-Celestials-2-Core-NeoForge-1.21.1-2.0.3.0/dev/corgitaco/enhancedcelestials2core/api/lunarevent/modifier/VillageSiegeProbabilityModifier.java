package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record VillageSiegeProbabilityModifier(double probability) implements LunarEventModifier {
   public static final MapCodec<VillageSiegeProbabilityModifier> CODEC = Codec.DOUBLE
      .fieldOf("probability")
      .xmap(VillageSiegeProbabilityModifier::new, VillageSiegeProbabilityModifier::probability);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.VILLAGE_SIEGE_PROBABILITY;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.village_siege_probability", new Object[]{this.probability});
   }
}
