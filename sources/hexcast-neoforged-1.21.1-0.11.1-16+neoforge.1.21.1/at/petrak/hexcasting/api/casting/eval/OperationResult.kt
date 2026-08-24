package at.petrak.hexcasting.api.casting.eval

import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation

public data class OperationResult(newImage: CastingImage, sideEffects: List<OperatorSideEffect>, newContinuation: SpellContinuation, sound: EvalSound) {
   public final val newImage: CastingImage
   public final val sideEffects: List<OperatorSideEffect>
   public final val newContinuation: SpellContinuation
   public final val sound: EvalSound

   init {
      this.newImage = newImage;
      this.sideEffects = sideEffects;
      this.newContinuation = newContinuation;
      this.sound = sound;
   }

   public operator fun component1(): CastingImage {
      return this.newImage;
   }

   public operator fun component2(): List<OperatorSideEffect> {
      return this.sideEffects;
   }

   public operator fun component3(): SpellContinuation {
      return this.newContinuation;
   }

   public operator fun component4(): EvalSound {
      return this.sound;
   }

   public fun copy(
      newImage: CastingImage = this.newImage,
      sideEffects: List<OperatorSideEffect> = this.sideEffects,
      newContinuation: SpellContinuation = this.newContinuation,
      sound: EvalSound = this.sound
   ): OperationResult {
      return new OperationResult(newImage, sideEffects, newContinuation, sound);
   }

   public override fun toString(): String {
      return "OperationResult(newImage=${this.newImage}, sideEffects=${this.sideEffects}, newContinuation=${this.newContinuation}, sound=${this.sound})";
   }

   public override fun hashCode(): Int {
      return ((this.newImage.hashCode() * 31 + this.sideEffects.hashCode()) * 31 + this.newContinuation.hashCode()) * 31 + this.sound.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is OperationResult) {
         return false;
      } else {
         val var2: OperationResult = other as OperationResult;
         if (!(this.newImage == (other as OperationResult).newImage)) {
            return false;
         } else if (!(this.sideEffects == var2.sideEffects)) {
            return false;
         } else if (!(this.newContinuation == var2.newContinuation)) {
            return false;
         } else {
            return this.sound == var2.sound;
         }
      }
   }
}
