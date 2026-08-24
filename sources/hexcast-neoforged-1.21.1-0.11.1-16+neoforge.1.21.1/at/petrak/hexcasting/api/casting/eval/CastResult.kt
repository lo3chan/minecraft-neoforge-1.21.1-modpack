package at.petrak.hexcasting.api.casting.eval

import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota

public data class CastResult(cast: Iota,
   continuation: SpellContinuation,
   newData: CastingImage?,
   sideEffects: List<OperatorSideEffect>,
   resolutionType: ResolvedPatternType,
   sound: EvalSound
) {
   public final val cast: Iota
   public final val continuation: SpellContinuation
   public final val newData: CastingImage?
   public final val sideEffects: List<OperatorSideEffect>
   public final val resolutionType: ResolvedPatternType
   public final val sound: EvalSound

   init {
      this.cast = cast;
      this.continuation = continuation;
      this.newData = newData;
      this.sideEffects = sideEffects;
      this.resolutionType = resolutionType;
      this.sound = sound;
   }

   public operator fun component1(): Iota {
      return this.cast;
   }

   public operator fun component2(): SpellContinuation {
      return this.continuation;
   }

   public operator fun component3(): CastingImage? {
      return this.newData;
   }

   public operator fun component4(): List<OperatorSideEffect> {
      return this.sideEffects;
   }

   public operator fun component5(): ResolvedPatternType {
      return this.resolutionType;
   }

   public operator fun component6(): EvalSound {
      return this.sound;
   }

   public fun copy(
      cast: Iota = this.cast,
      continuation: SpellContinuation = this.continuation,
      newData: CastingImage? = this.newData,
      sideEffects: List<OperatorSideEffect> = this.sideEffects,
      resolutionType: ResolvedPatternType = this.resolutionType,
      sound: EvalSound = this.sound
   ): CastResult {
      return new CastResult(cast, continuation, newData, sideEffects, resolutionType, sound);
   }

   public override fun toString(): String {
      return "CastResult(cast=${this.cast}, continuation=${this.continuation}, newData=${this.newData}, sideEffects=${this.sideEffects}, resolutionType=${this.resolutionType}, sound=${this.sound})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        ((this.cast.hashCode() * 31 + this.continuation.hashCode()) * 31 + (if (this.newData == null) 0 else this.newData.hashCode())) * 31
                           + this.sideEffects.hashCode()
                     )
                     * 31
                  + this.resolutionType.hashCode()
            )
            * 31
         + this.sound.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CastResult) {
         return false;
      } else {
         val var2: CastResult = other as CastResult;
         if (!(this.cast == (other as CastResult).cast)) {
            return false;
         } else if (!(this.continuation == var2.continuation)) {
            return false;
         } else if (!(this.newData == var2.newData)) {
            return false;
         } else if (!(this.sideEffects == var2.sideEffects)) {
            return false;
         } else if (this.resolutionType != var2.resolutionType) {
            return false;
         } else {
            return this.sound == var2.sound;
         }
      }
   }
}
