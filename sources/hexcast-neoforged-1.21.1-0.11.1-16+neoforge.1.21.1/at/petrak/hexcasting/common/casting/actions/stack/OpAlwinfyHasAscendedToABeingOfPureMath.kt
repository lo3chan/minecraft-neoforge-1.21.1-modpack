package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import it.unimi.dsi.fastutil.ints.IntArrayList
import kotlin.jvm.internal.markers.KMappedMarker

public object OpAlwinfyHasAscendedToABeingOfPureMath : Action {
   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
      if (stack.isEmpty()) {
         throw new MishapNotEnoughArgs(1, 0);
      } else {
         val code: Int = OperatorUtils.getPositiveInt$default(stack, CollectionsKt.getLastIndex(stack), 0, 2, null);
         stack.removeLast();
         val strides: IntArrayList = new IntArrayList();
         val editTarget: OpAlwinfyHasAscendedToABeingOfPureMath.FactorialIter = new OpAlwinfyHasAscendedToABeingOfPureMath.FactorialIter();

         while (editTarget.hasNext()) {
            val swap: Int = editTarget.next();
            if (swap > code) {
               break;
            }

            strides.add(swap);
         }

         if (strides.size() > stack.size()) {
            throw new MishapNotEnoughArgs(strides.size() + 1, stack.size() + 1);
         } else {
            var var13: java.util.List = stack.subList(stack.size() - strides.size(), stack.size());
            val var14: java.util.List = CollectionsKt.toMutableList(var13);
            var radix: Int = code;

            for (Integer divisor : CollectionsKt.asReversedMutable((java.util.List)strides)) {
               val index: Int = radix / divisor;
               radix %= divisor;
               var13.set(0, var14.remove(index));
               var13 = var13.subList(1, var13.size());
            }

            val var15: CastingImage = CastingImage.copy$default(image.withUsedOp(), stack, 0, null, false, 0L, null, 62, null);
            val var10003: java.util.List = CollectionsKt.emptyList();
            val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
            return new OperationResult(var15, var10003, continuation, var10005);
         }
      }
   }

   private class FactorialIter : java.util.Iterator<Integer>, KMappedMarker {
      public final var acc: Int = 1
         internal set

      public final var n: Int = 1
         internal set

      public override operator fun hasNext(): Boolean {
         return true;
      }

      public open operator fun next(): Int {
         val out: Int = this.acc;
         this.acc = this.acc * this.n;
         val var2: Int = this.n++;
         return out;
      }

      override fun remove() {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }
   }
}
