package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.WeightedSoundEntry;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class ModifyHurtSoundPower extends Power {
   public static final MapCodec<ModifyHurtSoundPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("muted", false).forGetter(ModifyHurtSoundPower::isMuted),
            WeightedSoundEntry.LIST_CODEC.fieldOf("sound").forGetter(ModifyHurtSoundPower::getSound),
            Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(ModifyHurtSoundPower::getVolume),
            Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(ModifyHurtSoundPower::getPitch)
         )
         .apply(i, ModifyHurtSoundPower::new)
   );
   private final boolean muted;
   private final List<WeightedSoundEntry> sound;
   private final float volume;
   private final float pitch;

   protected ModifyHurtSoundPower(Power.BaseSettings settings, boolean muted, List<WeightedSoundEntry> sound, float volume, float pitch) {
      super(settings);
      this.muted = muted;
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
   }

   public boolean isMuted() {
      return this.muted;
   }

   public List<WeightedSoundEntry> getSound() {
      return this.sound;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getVolume() {
      return this.volume;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public Stream<WeightedSoundEntry.SoundHolder> streamSoundHolder() {
      return this.sound.stream().map(s -> new WeightedSoundEntry.SoundHolder(s, this.volume, this.pitch));
   }
}
