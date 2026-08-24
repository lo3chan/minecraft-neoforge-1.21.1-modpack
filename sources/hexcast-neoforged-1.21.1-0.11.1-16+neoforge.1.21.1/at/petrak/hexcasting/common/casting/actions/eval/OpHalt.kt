package at.petrak.hexcasting.common.casting.actions.eval

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

public object OpHalt : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      var newStack: java.util.List = CollectionsKt.toList(image.getStack());
      var done: Boolean = false;

      var newCont: SpellContinuation;
      for (newCont = continuation; !done && newCont instanceof SpellContinuation.NotDone; newCont = ((SpellContinuation.NotDone)newCont).getNext()) {
         val image2: Pair = (newCont as SpellContinuation.NotDone).getFrame().breakDownwards(newStack);
         done = image2.getFirst() as java.lang.Boolean;
         newStack = image2.getSecond() as java.util.List;
      }

      if (!done) {
         newStack = CollectionsKt.emptyList();
      }

      val var8: CastingImage = CastingImage.copy$default(image.withUsedOp(), newStack, 0, null, false, 0L, null, 62, null);
      val var10003: java.util.List = CollectionsKt.emptyList();
      val var10005: EvalSound = HexEvalSounds.SPELL;
      return new OperationResult(var8, var10003, newCont, var10005);
   }
}
