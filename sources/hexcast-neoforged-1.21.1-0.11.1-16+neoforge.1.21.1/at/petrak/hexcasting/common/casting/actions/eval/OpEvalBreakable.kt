package at.petrak.hexcasting.common.casting.actions.eval

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.ContinuationIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs

public object OpEvalBreakable : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      val var10000: Iota = CollectionsKt.removeLastOrNull(stack) as Iota;
      if (var10000 == null) {
         throw new MishapNotEnoughArgs(1, 0);
      } else {
         stack.add(new ContinuationIota(continuation));
         return OpEval.INSTANCE.exec(env, image, continuation, stack, var10000);
      }
   }
}
