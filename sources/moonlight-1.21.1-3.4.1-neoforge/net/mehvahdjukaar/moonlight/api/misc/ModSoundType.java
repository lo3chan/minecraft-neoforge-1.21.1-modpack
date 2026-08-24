package net.mehvahdjukaar.moonlight.api.misc;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

public class ModSoundType extends SoundType {
   private final Supplier<SoundEvent> breakSound;
   private final Supplier<SoundEvent> stepSound;
   private final Supplier<SoundEvent> placeSound;
   private final Supplier<SoundEvent> hitSound;
   private final Supplier<SoundEvent> fallSound;

   public ModSoundType(
      float volumeIn,
      float pitchIn,
      Supplier<SoundEvent> breakSoundIn,
      Supplier<SoundEvent> stepSoundIn,
      Supplier<SoundEvent> placeSoundIn,
      Supplier<SoundEvent> hitSoundIn,
      Supplier<SoundEvent> fallSoundIn
   ) {
      super(volumeIn, pitchIn, null, null, null, null, null);
      this.breakSound = breakSoundIn;
      this.stepSound = stepSoundIn;
      this.placeSound = placeSoundIn;
      this.hitSound = hitSoundIn;
      this.fallSound = fallSoundIn;
   }

   @NotNull
   public SoundEvent getBreakSound() {
      return this.breakSound.get();
   }

   @NotNull
   public SoundEvent getStepSound() {
      return this.stepSound.get();
   }

   @NotNull
   public SoundEvent getPlaceSound() {
      return this.placeSound.get();
   }

   @NotNull
   public SoundEvent getHitSound() {
      return this.hitSound.get();
   }

   @NotNull
   public SoundEvent getFallSound() {
      return this.fallSound.get();
   }
}
