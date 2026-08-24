package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record ExperienceModifier(double amplifier) implements LunarEventModifier {
   public static final MapCodec<ExperienceModifier> CODEC = Codec.DOUBLE.fieldOf("amplifier").xmap(ExperienceModifier::new, ExperienceModifier::amplifier);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.EXPERIENCE;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.experience", new Object[]{this.amplifier});
   }
}
