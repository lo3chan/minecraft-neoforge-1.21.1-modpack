package fuzs.puzzleslib.api.block.v1;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class HolderBackedSoundType extends SoundType {
   final Holder<SoundEvent> breakSound;
   final Holder<SoundEvent> stepSound;
   final Holder<SoundEvent> placeSound;
   final Holder<SoundEvent> hitSound;
   final Holder<SoundEvent> fallSound;

   public HolderBackedSoundType(
      float volume,
      float pitch,
      Holder<SoundEvent> breakSound,
      Holder<SoundEvent> stepSound,
      Holder<SoundEvent> placeSound,
      Holder<SoundEvent> hitSound,
      Holder<SoundEvent> fallSound
   ) {
      super(volume, pitch, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY);
      this.breakSound = breakSound;
      this.stepSound = stepSound;
      this.placeSound = placeSound;
      this.hitSound = hitSound;
      this.fallSound = fallSound;
   }

   public SoundEvent getBreakSound() {
      return (SoundEvent)this.breakSound.value();
   }

   public SoundEvent getStepSound() {
      return (SoundEvent)this.stepSound.value();
   }

   public SoundEvent getPlaceSound() {
      return (SoundEvent)this.placeSound.value();
   }

   public SoundEvent getHitSound() {
      return (SoundEvent)this.hitSound.value();
   }

   public SoundEvent getFallSound() {
      return (SoundEvent)this.fallSound.value();
   }
}
