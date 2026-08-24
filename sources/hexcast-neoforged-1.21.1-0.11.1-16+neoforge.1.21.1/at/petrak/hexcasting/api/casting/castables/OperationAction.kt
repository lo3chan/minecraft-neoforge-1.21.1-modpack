package at.petrak.hexcasting.api.casting.castables

import at.petrak.hexcasting.api.casting.arithmetic.engine.NoOperatorCandidatesException
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidOperatorArgs
import at.petrak.hexcasting.common.lib.hex.HexArithmetics

public data class OperationAction(pattern: HexPattern) : Action {
   public final val pattern: HexPattern

   init {
      this.pattern = pattern;
   }

   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      try {
         return HexArithmetics.getEngine().run(this.pattern, env, image, continuation);
      } catch (var6: NoOperatorCandidatesException) {
         val var10002: java.util.List = var6.getArgs();
         val var10003: HexPattern = var6.getPattern();
         throw new MishapInvalidOperatorArgs(var10002, var10003);
      }
   }

   public operator fun component1(): HexPattern {
      return this.pattern;
   }

   public fun copy(pattern: HexPattern = this.pattern): OperationAction {
      return new OperationAction(pattern);
   }

   public override fun toString(): String {
      return "OperationAction(pattern=${this.pattern})";
   }

   public override fun hashCode(): Int {
      return this.pattern.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is OperationAction) {
         return false;
      } else {
         return this.pattern == (other as OperationAction).pattern;
      }
   }
}
