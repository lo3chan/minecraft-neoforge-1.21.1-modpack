package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import kotlin.math.MathKt

public object OpFisherman : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.size() < 2) {
         throw new MishapNotEnoughArgs(2, stack.size());
      } else {
         val it: OpFisherman = this;
         val x: Iota = CollectionsKt.last(stack) as Iota;
         stack.removeLast();
         val maxIdx: Int = stack.size() - 1;
         if (x is DoubleIota) {
            val var11: Double = (x as DoubleIota).getDouble();
            val rounded: Int = MathKt.roundToInt(var11);
            if (Math.abs(var11 - (double)rounded) <= 1.0E-4 && -maxIdx <= rounded && rounded <= maxIdx) {
               if (rounded >= 0) {
                  stack.add(stack.remove(stack.size() - 1 - rounded) as Iota);
               } else {
                  val var15: Iota = stack.removeLast() as Iota;
                  val var10001: Int = stack.size() + rounded;
                  stack.add(var10001, var15);
               }

               val var16: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
               val var10003: java.util.List = CollectionsKt.emptyList();
               val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
               return new OperationResult(var16, var10003, continuation, var10005);
            }
         }

         throw MishapInvalidIota.Companion.of(x, 0, "int.between", -maxIdx, maxIdx);
      }
   }
}
