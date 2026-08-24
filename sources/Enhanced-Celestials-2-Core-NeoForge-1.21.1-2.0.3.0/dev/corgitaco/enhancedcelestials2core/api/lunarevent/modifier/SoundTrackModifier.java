package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public record SoundTrackModifier(SoundEvent soundTrack) implements LunarEventModifier {
   public static final MapCodec<SoundTrackModifier> CODEC = SoundEvent.DIRECT_CODEC
      .fieldOf("sound_track")
      .xmap(SoundTrackModifier::new, SoundTrackModifier::soundTrack);

   @Override
   public LunarEventModifierType<?> type() {
      return LunarEventModifierTypes.SOUND_TRACK;
   }

   @Override
   public Component description() {
      return Component.translatable("enhancedcelestials2core.lunar_event_modifier.sound_track", new Object[]{this.soundTrack.getLocation().toString()});
   }
}
