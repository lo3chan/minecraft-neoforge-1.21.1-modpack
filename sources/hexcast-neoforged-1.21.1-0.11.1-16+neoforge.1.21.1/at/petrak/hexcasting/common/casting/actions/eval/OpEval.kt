package at.petrak.hexcasting.common.casting.actions.eval

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.FrameFinishEval
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.mojang.datafixers.util.Either
import kotlin.jvm.functions.Function1

public object OpEval : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      val var10000: Iota = CollectionsKt.removeLastOrNull(stack) as Iota;
      if (var10000 == null) {
         throw new MishapNotEnoughArgs(1, 0);
      } else {
         return this.exec(env, image, continuation, stack, var10000);
      }
   }

   public fun exec(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation, newStack: MutableList<Iota>, iota: Iota): OperationResult {
      val instrs: Either = OperatorUtils.evaluatable(iota, 0);
      val newCont: SpellContinuation = if (!instrs.left().isPresent()
            && (continuation !is SpellContinuation.NotDone || (continuation as SpellContinuation.NotDone).getFrame() !is FrameFinishEval))
         continuation.pushFrame(FrameFinishEval.INSTANCE)
         else
         continuation;
      val instrsList: SpellList = instrs.map(OpEval::exec$lambda$1, OpEval::exec$lambda$3) as SpellList;
      val frame: FrameEvaluate = new FrameEvaluate(instrsList, true);
      val image2: CastingImage = CastingImage.copy$default(image.withUsedOp(), newStack, 0, null, false, 0L, null, 62, null);
      val var10003: java.util.List = CollectionsKt.emptyList();
      val var10004: SpellContinuation = newCont.pushFrame(frame);
      val var10005: EvalSound = HexEvalSounds.HERMES;
      return new OperationResult(image2, var10003, var10004, var10005);
   }

   @JvmStatic
   fun `exec$lambda$0`(it: Iota): SpellList {
      return new SpellList.LList(0, CollectionsKt.listOf(it));
   }

   @JvmStatic
   fun `exec$lambda$1`(`$tmp0`: Function1, p0: Any): SpellList {
      return `$tmp0`.invoke(p0) as SpellList;
   }

   @JvmStatic
   fun `exec$lambda$2`(it: SpellList): SpellList {
      return it;
   }

   @JvmStatic
   fun `exec$lambda$3`(`$tmp0`: Function1, p0: Any): SpellList {
      return `$tmp0`.invoke(p0) as SpellList;
   }
}
