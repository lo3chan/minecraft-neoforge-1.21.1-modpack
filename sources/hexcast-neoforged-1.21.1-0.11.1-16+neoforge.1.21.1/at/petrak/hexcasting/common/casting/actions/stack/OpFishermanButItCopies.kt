package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

public object OpFishermanButItCopies : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.size() < 2) {
         throw new MishapNotEnoughArgs(2, stack.size());
      } else {
         val depth: Int = OperatorUtils.getIntBetween$default(stack, CollectionsKt.getLastIndex(stack), -(stack.size() - 2), stack.size() - 2, 0, 8, null);
         stack.removeLast();
         if (depth >= 0) {
            stack.add(stack.get(stack.size() - 1 - depth) as Iota);
         } else {
            stack.add(stack.size() - 1 + depth, CollectionsKt.last(stack) as Iota);
         }

         val var8: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
         val var10003: java.util.List = CollectionsKt.emptyList();
         val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
         return new OperationResult(var8, var10003, continuation, var10005);
      }
   }
}
