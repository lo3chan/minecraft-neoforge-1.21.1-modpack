package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;

public record BeaconRadiusModifier(double amplifier) implements LunarEventModifier {
   public static final MapCodec<BeaconRadiusModifier> CODEC = Codec.DOUBLE
      .fieldOf("amplifier")
      .xmap(BeaconRadiusModifier::new, BeaconRadiusModifier::amplifier);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.BEACON_RADIUS;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.beacon_radius", new Object[]{this.amplifier});
   }
}
