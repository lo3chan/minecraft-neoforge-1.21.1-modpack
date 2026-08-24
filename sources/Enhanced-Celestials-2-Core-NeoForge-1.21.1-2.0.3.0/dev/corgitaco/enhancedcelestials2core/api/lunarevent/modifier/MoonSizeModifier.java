package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record MoonSizeModifier(float moonSize) implements LunarEventModifier {
   public static final MapCodec<MoonSizeModifier> CODEC = Codec.FLOAT.fieldOf("moon_size").xmap(MoonSizeModifier::new, MoonSizeModifier::moonSize);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.MOON_SIZE;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.moon_size", new Object[]{this.moonSize});
   }
}
