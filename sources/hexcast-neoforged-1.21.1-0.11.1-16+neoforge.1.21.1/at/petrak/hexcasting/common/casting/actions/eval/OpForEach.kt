package at.petrak.hexcasting.common.casting.actions.eval

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import java.util.ArrayList

public object OpForEach : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.size() < 2) {
         throw new MishapNotEnoughArgs(2, stack.size());
      } else {
         val instrs: SpellList = OperatorUtils.getList$default(stack, CollectionsKt.getLastIndex(stack) - 1, 0, 2, null);
         val datums: SpellList = OperatorUtils.getList$default(stack, CollectionsKt.getLastIndex(stack), 0, 2, null);
         CollectionsKt.removeLastOrNull(stack);
         CollectionsKt.removeLastOrNull(stack);
         val frame: FrameForEach = new FrameForEach(datums, instrs, null, new ArrayList<>());
         val image2: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
         val var10003: java.util.List = CollectionsKt.emptyList();
         val var10004: SpellContinuation = continuation.pushFrame(frame);
         val var10005: EvalSound = HexEvalSounds.THOTH;
         return new OperationResult(image2, var10003, var10004, var10005);
      }
   }
}
