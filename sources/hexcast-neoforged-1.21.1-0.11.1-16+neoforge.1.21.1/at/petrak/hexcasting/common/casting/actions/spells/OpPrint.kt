package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

public object OpPrint : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.isEmpty()) {
         throw new MishapNotEnoughArgs(1, 0);
      } else {
         val datum: Iota = stack.get(CollectionsKt.getLastIndex(stack)) as Iota;
         val image2: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
         val var10003: java.util.List = CollectionsKt.listOf(new OperatorSideEffect.AttemptSpell(new OpPrint.Spell(datum), false, false));
         val var10005: EvalSound = HexEvalSounds.SPELL;
         return new OperationResult(image2, var10003, continuation, var10005);
      }
   }

   private data class Spell(datum: Iota) : RenderedSpell {
      public final val datum: Iota

      init {
         this.datum = datum;
      }

      public override fun cast(env: CastingEnvironment) {
         env.printMessage(this.datum.display());
      }

      public operator fun component1(): Iota {
         return this.datum;
      }

      public fun copy(datum: Iota = this.datum): at.petrak.hexcasting.common.casting.actions.spells.OpPrint.Spell {
         return new OpPrint.Spell(datum);
      }

      public override fun toString(): String {
         return "Spell(datum=${this.datum})";
      }

      public override fun hashCode(): Int {
         return this.datum.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpPrint.Spell) {
            return false;
         } else {
            return this.datum == (other as OpPrint.Spell).datum;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
