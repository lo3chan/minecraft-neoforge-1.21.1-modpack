package at.petrak.hexcasting.api.casting.castables

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import kotlin.jvm.internal.SourceDebugExtension

public interface ConstMediaAction : Action {
   public val argc: Int

   public open val mediaCost: Long
      public open get() {
      }


   public abstract fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
   }

   public open fun executeWithOpCount(args: List<Iota>, env: CastingEnvironment): at.petrak.hexcasting.api.casting.castables.ConstMediaAction.CostMediaActionResult {
   }

   public override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
   }

   public data class CostMediaActionResult(resultStack: List<Iota>, opCount: Long = 1L) {
      public final val resultStack: List<Iota>
      public final val opCount: Long

      init {
         this.resultStack = resultStack;
         this.opCount = opCount;
      }

      public operator fun component1(): List<Iota> {
         return this.resultStack;
      }

      public operator fun component2(): Long {
         return this.opCount;
      }

      public fun copy(resultStack: List<Iota> = this.resultStack, opCount: Long = this.opCount): at.petrak.hexcasting.api.casting.castables.ConstMediaAction.CostMediaActionResult {
         return new ConstMediaAction.CostMediaActionResult(resultStack, opCount);
      }

      public override fun toString(): String {
         return "CostMediaActionResult(resultStack=${this.resultStack}, opCount=${this.opCount})";
      }

      public override fun hashCode(): Int {
         return this.resultStack.hashCode() * 31 + java.lang.Long.hashCode(this.opCount);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ConstMediaAction.CostMediaActionResult) {
            return false;
         } else {
            val var2: ConstMediaAction.CostMediaActionResult = other as ConstMediaAction.CostMediaActionResult;
            if (!(this.resultStack == (other as ConstMediaAction.CostMediaActionResult).resultStack)) {
               return false;
            } else {
               return this.opCount == var2.opCount;
            }
         }
      }
   }

   // $VF: Class flags could not be determined
   @SourceDebugExtension(["SMAP\nConstMediaAction.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConstMediaAction.kt\nat/petrak/hexcasting/api/casting/castables/ConstMediaAction$DefaultImpls\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,45:1\n1#2:46\n*E\n"])
   internal class DefaultImpls {
      @JvmStatic
      fun getMediaCost(`$this`: ConstMediaAction): Long {
         return 0L;
      }

      @JvmStatic
      fun executeWithOpCount(`$this`: ConstMediaAction, args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
         return new ConstMediaAction.CostMediaActionResult(`$this`.execute(args, env), 0L, 2, null);
      }

      @JvmStatic
      fun operate(`$this`: ConstMediaAction, env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
         val stack: java.util.List = CollectionsKt.toMutableList(image.getStack());
         if (`$this`.getArgc() > stack.size()) {
            throw new MishapNotEnoughArgs(`$this`.getArgc(), stack.size());
         } else {
            val args: java.util.List = CollectionsKt.takeLast(stack, `$this`.getArgc());
            val result: Int = `$this`.getArgc();

            for (int sideEffects = 0; sideEffects < result; sideEffects++) {
               stack.removeLast();
            }

            val var10: ConstMediaAction.CostMediaActionResult = `$this`.executeWithOpCount(args, env);
            stack.addAll(var10.getResultStack());
            val var11: java.util.List = CollectionsKt.mutableListOf(new OperatorSideEffect[]{new OperatorSideEffect.ConsumeMedia(`$this`.getMediaCost())});
            val var12: CastingImage = CastingImage.copy$default(image, stack, 0, null, false, image.getOpsConsumed() + var10.getOpCount(), null, 46, null);
            val var10005: EvalSound = HexEvalSounds.NORMAL_EXECUTE;
            return new OperationResult(var12, var11, continuation, var10005);
         }
      }
   }
}
